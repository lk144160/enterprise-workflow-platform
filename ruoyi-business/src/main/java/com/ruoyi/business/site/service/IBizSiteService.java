package com.ruoyi.business.site.service;

import java.util.List;
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.site.domain.BizProgressStage;
import com.ruoyi.business.site.domain.BizSite;

/**
 * 施工工地服务接口
 *
 * @author renovationops
 */
public interface IBizSiteService
{
    public BizSite selectBizSiteById(Long siteId);

    public List<BizSite> selectBizSiteList(BizSite site);

    /** 可开工的合同（已生效且未建工地） */
    public List<BizContract> selectAvailableContracts();

    public int insertBizSite(BizSite site);

    public int updateBizSite(BizSite site);

    public int deleteBizSiteByIds(Long[] siteIds);

    /** 阶段开始施工（记录实际开始日期，工地转施工中） */
    public int startStage(Long stageId);

    /** 阶段完工（记录实际完成日期，全部完成时工地转已完工） */
    public int completeStage(Long stageId);

    /** 调整阶段计划（计划起止/备注/步骤名称） */
    public int updateStage(BizProgressStage stage);

    /** 新增自定义施工步骤（追加到末尾） */
    public int addStage(BizProgressStage stage);

    /** 删除施工步骤（仅未开始的步骤可删，附件一并清理） */
    public int deleteStage(Long stageId);
}
