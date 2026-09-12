package com.ruoyi.business.design.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.common.BusinessNoService;
import com.ruoyi.business.contract.service.IBizPaymentService;
import com.ruoyi.business.contract.service.PaymentConstants;
import com.ruoyi.business.design.domain.BizDisclosure;
import com.ruoyi.business.design.mapper.BizDisclosureMapper;
import com.ruoyi.business.design.service.IBizDisclosureService;
import com.ruoyi.business.flow.service.FlowCallback;
import com.ruoyi.business.flow.service.FlowConstants;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 技术交底服务实现（含审批流回调）
 *
 * @author renovationops
 */
@Service
public class BizDisclosureServiceImpl implements IBizDisclosureService, FlowCallback
{
    /** 交底状态 */
    public static final String STATUS_DRAFT = "0";
    public static final String STATUS_AUDITING = "1";
    public static final String STATUS_CONFIRMED = "2";
    public static final String STATUS_REJECTED = "3";

    @Autowired
    private BizDisclosureMapper disclosureMapper;

    @Autowired
    private BusinessNoService businessNoService;

    @Autowired
    private IFlowEngineService flowEngineService;

    @Autowired
    private IBizPaymentService paymentService;

    @Override
    public BizDisclosure selectBizDisclosureById(Long disclosureId)
    {
        return disclosureMapper.selectBizDisclosureById(disclosureId);
    }

    @Override
    public List<BizDisclosure> selectBizDisclosureList(BizDisclosure disclosure)
    {
        return disclosureMapper.selectBizDisclosureList(disclosure);
    }

    @Override
    @Transactional
    public int insertBizDisclosure(BizDisclosure disclosure)
    {
        if (disclosure.getContractId() == null)
        {
            throw new ServiceException("请选择关联合同");
        }
        if (StringUtils.isEmpty(disclosure.getContent()))
        {
            throw new ServiceException("交底内容不能为空");
        }
        disclosure.setDisclosureNo(businessNoService.nextNo("JB"));
        disclosure.setStatus(STATUS_DRAFT);
        disclosure.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        disclosure.setCreateBy(SecurityUtils.getUsername());
        return disclosureMapper.insertBizDisclosure(disclosure);
    }

    @Override
    @Transactional
    public int updateBizDisclosure(BizDisclosure disclosure)
    {
        BizDisclosure exist = mustGet(disclosure.getDisclosureId());
        if (!STATUS_DRAFT.equals(exist.getStatus()) && !STATUS_REJECTED.equals(exist.getStatus()))
        {
            throw new ServiceException("仅草稿/已驳回状态的交底单可修改");
        }
        disclosure.setUpdateBy(SecurityUtils.getUsername());
        return disclosureMapper.updateBizDisclosure(disclosure);
    }

    @Override
    @Transactional
    public int deleteBizDisclosureByIds(Long[] disclosureIds)
    {
        for (Long disclosureId : disclosureIds)
        {
            BizDisclosure exist = mustGet(disclosureId);
            if (!STATUS_DRAFT.equals(exist.getStatus()) && !STATUS_REJECTED.equals(exist.getStatus()))
            {
                throw new ServiceException("交底单【" + exist.getDisclosureNo() + "】已进入流程，不允许删除");
            }
        }
        return disclosureMapper.deleteBizDisclosureByIds(disclosureIds);
    }

    @Override
    @Transactional
    public void submitDisclosure(Long disclosureId)
    {
        BizDisclosure disclosure = mustGet(disclosureId);
        if (!STATUS_DRAFT.equals(disclosure.getStatus()) && !STATUS_REJECTED.equals(disclosure.getStatus()))
        {
            throw new ServiceException("仅草稿/已驳回状态的交底单可提交审批");
        }
        BizDisclosure update = new BizDisclosure();
        update.setDisclosureId(disclosureId);
        update.setStatus(STATUS_AUDITING);
        update.setUpdateBy(SecurityUtils.getUsername());
        disclosureMapper.updateBizDisclosure(update);
        flowEngineService.startFlow(FlowConstants.DISCLOSURE_APPROVAL, FlowConstants.BIZ_TYPE_DISCLOSURE, disclosureId,
                "技术交底 " + disclosure.getDisclosureNo() + " " + (disclosure.getCustomerName() == null ? "" : disclosure.getCustomerName()), null);
    }

    // ==================== 审批流回调 ====================

    @Override
    public String bizType()
    {
        return FlowConstants.BIZ_TYPE_DISCLOSURE;
    }

    @Override
    @Transactional
    public void onApproved(Long bizId)
    {
        BizDisclosure update = new BizDisclosure();
        update.setDisclosureId(bizId);
        update.setStatus(STATUS_CONFIRMED);
        update.setConfirmTime(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        disclosureMapper.updateBizDisclosure(update);
        // 交底确认里程碑：触发施工款期次到期
        BizDisclosure disclosure = disclosureMapper.selectBizDisclosureById(bizId);
        if (disclosure != null)
        {
            paymentService.triggerPlans(disclosure.getContractId(), PaymentConstants.TRIGGER_DISCLOSURE_CONFIRM, bizId);
        }
    }

    @Override
    @Transactional
    public void onRejected(Long bizId)
    {
        BizDisclosure update = new BizDisclosure();
        update.setDisclosureId(bizId);
        update.setStatus(STATUS_REJECTED);
        update.setUpdateBy(SecurityUtils.getUsername());
        disclosureMapper.updateBizDisclosure(update);
    }

    @Override
    @Transactional
    public void onCanceled(Long bizId)
    {
        BizDisclosure update = new BizDisclosure();
        update.setDisclosureId(bizId);
        update.setStatus(STATUS_DRAFT);
        update.setUpdateBy(SecurityUtils.getUsername());
        disclosureMapper.updateBizDisclosure(update);
    }

    private BizDisclosure mustGet(Long disclosureId)
    {
        BizDisclosure disclosure = disclosureMapper.selectBizDisclosureById(disclosureId);
        if (disclosure == null)
        {
            throw new ServiceException("技术交底单不存在");
        }
        return disclosure;
    }
}
