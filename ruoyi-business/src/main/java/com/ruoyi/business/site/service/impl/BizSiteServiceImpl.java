package com.ruoyi.business.site.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.attachment.domain.BizAttachment;
import com.ruoyi.business.common.BusinessNoService;
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.site.domain.BizProgressStage;
import com.ruoyi.business.site.domain.BizSite;
import com.ruoyi.business.site.mapper.BizProgressStageMapper;
import com.ruoyi.business.site.mapper.BizSiteMapper;
import com.ruoyi.business.site.service.IBizSiteService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 施工工地服务实现
 *
 * @author renovationops
 */
@Service
public class BizSiteServiceImpl implements IBizSiteService
{
    /** 工地状态 */
    public static final String SITE_STATUS_PENDING = "0";
    public static final String SITE_STATUS_ONGOING = "1";
    public static final String SITE_STATUS_DONE = "2";

    /** 阶段状态 */
    public static final String STAGE_STATUS_PENDING = "0";
    public static final String STAGE_STATUS_ONGOING = "1";
    public static final String STAGE_STATUS_DONE = "2";

    /** 标准工序（字典 biz_stage_type） */
    private static final String[] STAGE_TEMPLATE = {
        "prepare", "demolition", "hydropower", "waterproof", "masonry",
        "carpentry", "paint", "installation", "soft_furnish", "acceptance"
    };

    @Autowired
    private BizSiteMapper siteMapper;

    @Autowired
    private BizProgressStageMapper stageMapper;

    @Autowired
    private BusinessNoService businessNoService;

    @Override
    public BizSite selectBizSiteById(Long siteId)
    {
        BizSite site = siteMapper.selectBizSiteById(siteId);
        if (site != null)
        {
            fillDetail(site);
        }
        return maskKeyPassword(site);
    }

    @Override
    public List<BizSite> selectBizSiteList(BizSite site)
    {
        List<BizSite> list = siteMapper.selectBizSiteList(site);
        for (BizSite item : list)
        {
            maskKeyPassword(item);
        }
        return list;
    }

    @Override
    public List<BizContract> selectAvailableContracts()
    {
        return siteMapper.selectAvailableContracts();
    }

    @Override
    @Transactional
    public int insertBizSite(BizSite site)
    {
        if (site.getContractId() == null)
        {
            throw new ServiceException("请选择关联合同");
        }
        if (site.getStartDate() == null)
        {
            throw new ServiceException("请选择开工日期");
        }
        if (StringUtils.isEmpty(site.getKeyPassword()))
        {
            throw new ServiceException("请填写门锁密码（钥匙）");
        }
        if (siteMapper.selectBizSiteByContractId(site.getContractId()) != null)
        {
            throw new ServiceException("该合同已开工地，一合同仅允许一个工地");
        }
        // 从合同带出客户信息，未填写时回填
        BizContract contract = mustGetContract(site.getContractId());
        site.setCustomerId(contract.getCustomerId());
        if (StringUtils.isEmpty(site.getEstate()))
        {
            site.setEstate(contract.getEstate());
        }
        if (site.getArea() == null)
        {
            site.setArea(contract.getArea());
        }
        if (site.getDesignerId() == null)
        {
            site.setDesignerId(contract.getOwnerUserId());
            site.setDesignerName(contract.getOwnerUserName());
        }
        site.setSiteNo(businessNoService.nextNo("GD"));
        site.setStatus(SITE_STATUS_PENDING);
        site.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        site.setCreateBy(SecurityUtils.getUsername());
        int rows = siteMapper.insertBizSite(site);
        // 自动铺排标准工序
        initStages(site, contract.getEndDate());
        return rows;
    }

    @Override
    @Transactional
    public int updateBizSite(BizSite site)
    {
        BizSite exist = mustGetSite(site.getSiteId());
        if (SITE_STATUS_DONE.equals(exist.getStatus()))
        {
            throw new ServiceException("工地已完工，不允许再修改");
        }
        site.setUpdateBy(SecurityUtils.getUsername());
        return siteMapper.updateBizSite(site);
    }

    @Override
    @Transactional
    public int deleteBizSiteByIds(Long[] siteIds)
    {
        for (Long siteId : siteIds)
        {
            BizSite exist = mustGetSite(siteId);
            if (SITE_STATUS_DONE.equals(exist.getStatus()))
            {
                throw new ServiceException("工地【" + exist.getSiteNo() + "】已完工，不允许删除");
            }
        }
        stageMapper.deleteStageAttachments(siteIds);
        stageMapper.deleteStagesBySiteIds(siteIds);
        return siteMapper.deleteBizSiteByIds(siteIds);
    }

    @Override
    @Transactional
    public int startStage(Long stageId)
    {
        BizProgressStage stage = mustGetStage(stageId);
        if (STAGE_STATUS_DONE.equals(stage.getStatus()))
        {
            throw new ServiceException("该阶段已完成，无需开始");
        }
        if (STAGE_STATUS_ONGOING.equals(stage.getStatus()))
        {
            throw new ServiceException("该阶段已在进行中");
        }
        BizProgressStage update = new BizProgressStage();
        update.setStageId(stageId);
        update.setStatus(STAGE_STATUS_ONGOING);
        update.setActualStartDate(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        int rows = stageMapper.updateBizProgressStage(update);
        // 工地转施工中
        if (rows > 0)
        {
            BizSite site = mustGetSite(stage.getSiteId());
            if (SITE_STATUS_PENDING.equals(site.getStatus()))
            {
                BizSite siteUpdate = new BizSite();
                siteUpdate.setSiteId(site.getSiteId());
                siteUpdate.setStatus(SITE_STATUS_ONGOING);
                siteUpdate.setUpdateBy(SecurityUtils.getUsername());
                siteMapper.updateBizSite(siteUpdate);
            }
        }
        return rows;
    }

    @Override
    @Transactional
    public int completeStage(Long stageId)
    {
        BizProgressStage stage = mustGetStage(stageId);
        if (STAGE_STATUS_DONE.equals(stage.getStatus()))
        {
            throw new ServiceException("该阶段已完成");
        }
        BizProgressStage update = new BizProgressStage();
        update.setStageId(stageId);
        update.setStatus(STAGE_STATUS_DONE);
        update.setActualEndDate(new Date());
        if (stage.getActualStartDate() == null)
        {
            update.setActualStartDate(new Date());
        }
        update.setUpdateBy(SecurityUtils.getUsername());
        int rows = stageMapper.updateBizProgressStage(update);
        // 全部阶段完成 → 工地转已完工
        if (rows > 0)
        {
            List<BizProgressStage> stages = stageMapper.selectStagesBySiteId(stage.getSiteId());
            boolean allDone = stages.stream().allMatch(s -> STAGE_STATUS_DONE.equals(s.getStatus()));
            if (allDone)
            {
                BizSite siteUpdate = new BizSite();
                siteUpdate.setSiteId(stage.getSiteId());
                siteUpdate.setStatus(SITE_STATUS_DONE);
                siteUpdate.setFinishDate(new Date());
                siteUpdate.setUpdateBy(SecurityUtils.getUsername());
                siteMapper.updateBizSite(siteUpdate);
            }
        }
        return rows;
    }

    @Override
    @Transactional
    public int updateStage(BizProgressStage stage)
    {
        mustGetStage(stage.getStageId());
        if (stage.getPlanStartDate() != null && stage.getPlanEndDate() != null
                && stage.getPlanEndDate().before(stage.getPlanStartDate()))
        {
            throw new ServiceException("计划结束日期不能早于计划开始日期");
        }
        if (stage.getActualStartDate() != null && stage.getActualEndDate() != null
                && stage.getActualEndDate().before(stage.getActualStartDate()))
        {
            throw new ServiceException("实际完成日期不能早于实际开始日期");
        }
        // 手工补录实际日期时同步阶段状态：填了实际完成即已完成，仅填实际开始即进行中
        if (stage.getActualEndDate() != null)
        {
            stage.setStatus(STAGE_STATUS_DONE);
        }
        else if (stage.getActualStartDate() != null)
        {
            stage.setStatus(STAGE_STATUS_ONGOING);
        }
        stage.setUpdateBy(SecurityUtils.getUsername());
        int rows = stageMapper.updateBizProgressStage(stage);
        // 全部阶段完成时工地转已完工
        if (rows > 0 && STAGE_STATUS_DONE.equals(stage.getStatus()))
        {
            BizProgressStage current = stageMapper.selectBizProgressStageById(stage.getStageId());
            List<BizProgressStage> stages = stageMapper.selectStagesBySiteId(current.getSiteId());
            boolean allDone = stages.stream().allMatch(s -> STAGE_STATUS_DONE.equals(s.getStatus()));
            if (allDone)
            {
                BizSite site = mustGetSite(current.getSiteId());
                if (!SITE_STATUS_DONE.equals(site.getStatus()))
                {
                    BizSite siteUpdate = new BizSite();
                    siteUpdate.setSiteId(current.getSiteId());
                    siteUpdate.setStatus(SITE_STATUS_DONE);
                    siteUpdate.setFinishDate(new Date());
                    siteUpdate.setUpdateBy(SecurityUtils.getUsername());
                    siteMapper.updateBizSite(siteUpdate);
                }
            }
        }
        return rows;
    }

    @Override
    @Transactional
    public int addStage(BizProgressStage stage)
    {
        if (stage.getSiteId() == null)
        {
            throw new ServiceException("缺少工地信息");
        }
        if (StringUtils.isEmpty(stage.getStageName()))
        {
            throw new ServiceException("请填写施工步骤名称");
        }
        BizSite site = mustGetSite(stage.getSiteId());
        if (SITE_STATUS_DONE.equals(site.getStatus()))
        {
            throw new ServiceException("工地已完工，不允许新增施工步骤");
        }
        if (stage.getPlanStartDate() != null && stage.getPlanEndDate() != null
                && stage.getPlanEndDate().before(stage.getPlanStartDate()))
        {
            throw new ServiceException("计划结束日期不能早于计划开始日期");
        }
        List<BizProgressStage> stages = stageMapper.selectStagesBySiteId(stage.getSiteId());
        // 计算插入位置顺序号：指定了参照步骤则插到它之前（后续步骤顺移），否则追加到末尾
        if (stage.getInsertBeforeStageId() != null)
        {
            BizProgressStage ref = stages.stream()
                    .filter(s -> stage.getInsertBeforeStageId().equals(s.getStageId()))
                    .findFirst()
                    .orElseThrow(() -> new ServiceException("插入位置的参照步骤不存在"));
            stageMapper.bumpSortOrderFrom(stage.getSiteId(), ref.getSortOrder());
            stage.setSortOrder(ref.getSortOrder());
        }
        else
        {
            int maxOrder = stages.stream().mapToInt(s -> s.getSortOrder() == null ? 0 : s.getSortOrder()).max().orElse(0);
            stage.setSortOrder(maxOrder + 1);
        }
        stage.setStageType("other");
        stage.setStatus(STAGE_STATUS_PENDING);
        stage.setCreateBy(SecurityUtils.getUsername());
        return stageMapper.insertBizProgressStage(stage);
    }

    @Override
    @Transactional
    public int deleteStage(Long stageId)
    {
        BizProgressStage stage = mustGetStage(stageId);
        if (!STAGE_STATUS_PENDING.equals(stage.getStatus()))
        {
            throw new ServiceException("仅【未开始】的施工步骤可删除");
        }
        stageMapper.deleteStageAttachmentByStageId(stageId);
        return stageMapper.deleteBizProgressStageById(stageId);
    }

    /**
     * 新工地自动铺排标准工序：按合同工期均分
     */
    private void initStages(BizSite site, Date contractEndDate)
    {
        Date start = site.getStartDate();
        Date end = contractEndDate;
        if (end == null || !end.after(start))
        {
            end = addDays(start, 100);
        }
        long totalDays = (end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000L) + 1;
        long perStage = Math.max(totalDays / STAGE_TEMPLATE.length, 1);
        String createBy = SecurityUtils.getUsername();
        for (int i = 0; i < STAGE_TEMPLATE.length; i++)
        {
            Date planStart = addDays(start, (int) (perStage * i));
            Date planEnd = (i == STAGE_TEMPLATE.length - 1) ? end : addDays(start, (int) (perStage * (i + 1) - 1));
            BizProgressStage stage = new BizProgressStage();
            stage.setSiteId(site.getSiteId());
            stage.setStageType(STAGE_TEMPLATE[i]);
            stage.setSortOrder(i + 1);
            stage.setPlanStartDate(planStart);
            stage.setPlanEndDate(planEnd.after(end) ? end : planEnd);
            stage.setStatus(STAGE_STATUS_PENDING);
            stage.setCreateBy(createBy);
            stageMapper.insertBizProgressStage(stage);
        }
    }

    /** 日期加天数 */
    private Date addDays(Date date, int days)
    {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        c.add(java.util.Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    /**
     * 详情组装：阶段列表 + 现场照片
     */
    private void fillDetail(BizSite site)
    {
        List<BizProgressStage> stages = stageMapper.selectStagesBySiteId(site.getSiteId());
        if (!stages.isEmpty())
        {
            List<Long> stageIds = stages.stream().map(BizProgressStage::getStageId).collect(java.util.stream.Collectors.toList());
            List<BizAttachment> attachments = stageMapper.selectStageAttachments(stageIds);
            java.util.Map<Long, List<BizAttachment>> group = new java.util.HashMap<>();
            for (BizAttachment att : attachments)
            {
                group.computeIfAbsent(att.getBizId(), k -> new java.util.ArrayList<>()).add(att);
            }
            for (BizProgressStage stage : stages)
            {
                stage.setAttachments(group.get(stage.getStageId()));
            }
        }
        site.setStages(stages);
    }

    /**
     * 无密码查看权限时抹除门锁密码
     */
    private BizSite maskKeyPassword(BizSite site)
    {
        if (site != null && !SecurityUtils.hasPermi("biz:site:viewkey"))
        {
            site.setKeyPassword(null);
        }
        return site;
    }

    private BizSite mustGetSite(Long siteId)
    {
        BizSite site = siteMapper.selectBizSiteById(siteId);
        if (site == null)
        {
            throw new ServiceException("工地不存在");
        }
        return site;
    }

    private BizProgressStage mustGetStage(Long stageId)
    {
        BizProgressStage stage = stageMapper.selectBizProgressStageById(stageId);
        if (stage == null)
        {
            throw new ServiceException("施工阶段不存在");
        }
        return stage;
    }

    private BizContract mustGetContract(Long contractId)
    {
        BizContract contract = siteMapper.selectAvailableContracts().stream()
                .filter(c -> contractId.equals(c.getContractId())).findFirst().orElse(null);
        if (contract == null)
        {
            throw new ServiceException("合同不存在或不可开工（仅已生效且未开工地的合同可选）");
        }
        return contract;
    }
}
