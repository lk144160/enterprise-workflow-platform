package com.ruoyi.business.expense.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.attachment.mapper.BizAttachmentMapper;
import com.ruoyi.business.common.BusinessNoService;
import com.ruoyi.business.expense.domain.BizExpense;
import com.ruoyi.business.expense.mapper.BizExpenseMapper;
import com.ruoyi.business.expense.service.IBizExpenseService;
import com.ruoyi.business.flow.service.FlowCallback;
import com.ruoyi.business.flow.service.FlowConstants;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 报销单服务实现（含审批流回调）
 *
 * @author renovationops
 */
@Service
public class BizExpenseServiceImpl implements IBizExpenseService, FlowCallback
{
    /** 报销状态 */
    public static final String STATUS_DRAFT = "0";
    public static final String STATUS_AUDITING = "1";
    public static final String STATUS_APPROVED = "2";
    public static final String STATUS_REJECTED = "3";
    public static final String STATUS_PAID = "4";
    public static final String STATUS_INVALID = "5";

    /** 发票附件业务类型 */
    private static final String ATTACH_BIZ_TYPE = "expense";

    @Autowired
    private BizExpenseMapper expenseMapper;

    @Autowired
    private BizAttachmentMapper attachmentMapper;

    @Autowired
    private BusinessNoService businessNoService;

    @Autowired
    private IFlowEngineService flowEngineService;

    @Override
    public BizExpense selectBizExpenseById(Long expenseId)
    {
        return expenseMapper.selectBizExpenseById(expenseId);
    }

    @Override
    public List<BizExpense> selectBizExpenseList(BizExpense expense)
    {
        return expenseMapper.selectBizExpenseList(expense);
    }

    @Override
    @Transactional
    public int insertBizExpense(BizExpense expense)
    {
        checkBase(expense);
        expense.setExpenseNo(businessNoService.nextNo("BX"));
        expense.setApplicantId(SecurityUtils.getUserId());
        expense.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        expense.setStatus(STATUS_DRAFT);
        expense.setCreateBy(SecurityUtils.getUsername());
        return expenseMapper.insertBizExpense(expense);
    }

    @Override
    @Transactional
    public int updateBizExpense(BizExpense expense)
    {
        BizExpense exist = mustGet(expense.getExpenseId());
        checkOwner(exist);
        if (!STATUS_DRAFT.equals(exist.getStatus()) && !STATUS_REJECTED.equals(exist.getStatus()))
        {
            throw new ServiceException("仅草稿/已驳回状态的报销单可修改");
        }
        checkBase(expense);
        expense.setUpdateBy(SecurityUtils.getUsername());
        return expenseMapper.updateBizExpense(expense);
    }

    @Override
    @Transactional
    public int deleteBizExpenseByIds(Long[] expenseIds)
    {
        for (Long expenseId : expenseIds)
        {
            BizExpense exist = mustGet(expenseId);
            checkOwner(exist);
            if (!STATUS_DRAFT.equals(exist.getStatus()) && !STATUS_REJECTED.equals(exist.getStatus()))
            {
                throw new ServiceException("报销单【" + exist.getExpenseNo() + "】已进入流程，不允许删除");
            }
        }
        return expenseMapper.deleteBizExpenseByIds(expenseIds);
    }

    @Override
    @Transactional
    public void submitExpense(Long expenseId)
    {
        BizExpense expense = mustGet(expenseId);
        checkOwner(expense);
        if (!STATUS_DRAFT.equals(expense.getStatus()) && !STATUS_REJECTED.equals(expense.getStatus()))
        {
            throw new ServiceException("仅草稿/已驳回状态的报销单可提交");
        }
        // 发票张数与附件数一致性校验
        int attachCount = attachmentMapper.countByBiz(ATTACH_BIZ_TYPE, expenseId);
        int invoiceCount = expense.getInvoiceCount() == null ? 0 : expense.getInvoiceCount();
        if (invoiceCount <= 0)
        {
            throw new ServiceException("发票张数必须大于0");
        }
        if (attachCount != invoiceCount)
        {
            throw new ServiceException("发票张数（" + invoiceCount + "）与已上传发票附件数（" + attachCount + "）不一致");
        }
        BizExpense update = new BizExpense();
        update.setExpenseId(expenseId);
        update.setStatus(STATUS_AUDITING);
        update.setSubmitTime(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        expenseMapper.updateBizExpense(update);
        flowEngineService.startFlow(FlowConstants.EXPENSE_APPROVAL, FlowConstants.BIZ_TYPE_EXPENSE, expenseId,
                "报销单 " + expense.getExpenseNo() + " " + expense.getApplicantName() + " "
                        + (expense.getAmount() == null ? "" : expense.getAmount() + "元"),
                expense.getAmount());
    }

    @Override
    @Transactional
    public void payExpense(Long expenseId)
    {
        BizExpense expense = mustGet(expenseId);
        if (!STATUS_APPROVED.equals(expense.getStatus()))
        {
            throw new ServiceException("仅审批通过的报销单可登记打款");
        }
        BizExpense update = new BizExpense();
        update.setExpenseId(expenseId);
        update.setStatus(STATUS_PAID);
        update.setPayTime(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        expenseMapper.updateBizExpense(update);
    }

    @Override
    @Transactional
    public void invalidateExpense(Long expenseId)
    {
        BizExpense expense = mustGet(expenseId);
        if (STATUS_AUDITING.equals(expense.getStatus()))
        {
            throw new ServiceException("审批中的报销单不可作废");
        }
        if (STATUS_PAID.equals(expense.getStatus()))
        {
            throw new ServiceException("已打款的报销单不可作废");
        }
        BizExpense update = new BizExpense();
        update.setExpenseId(expenseId);
        update.setStatus(STATUS_INVALID);
        update.setUpdateBy(SecurityUtils.getUsername());
        expenseMapper.updateBizExpense(update);
    }

    // ==================== 审批流回调 ====================

    @Override
    public String bizType()
    {
        return FlowConstants.BIZ_TYPE_EXPENSE;
    }

    @Override
    @Transactional
    public void onApproved(Long bizId)
    {
        BizExpense update = new BizExpense();
        update.setExpenseId(bizId);
        update.setStatus(STATUS_APPROVED);
        update.setUpdateBy(SecurityUtils.getUsername());
        expenseMapper.updateBizExpense(update);
    }

    @Override
    @Transactional
    public void onRejected(Long bizId)
    {
        BizExpense update = new BizExpense();
        update.setExpenseId(bizId);
        update.setStatus(STATUS_REJECTED);
        update.setUpdateBy(SecurityUtils.getUsername());
        expenseMapper.updateBizExpense(update);
    }

    @Override
    @Transactional
    public void onCanceled(Long bizId)
    {
        BizExpense update = new BizExpense();
        update.setExpenseId(bizId);
        update.setStatus(STATUS_DRAFT);
        update.setUpdateBy(SecurityUtils.getUsername());
        expenseMapper.updateBizExpense(update);
    }

    // ==================== 私有方法 ====================

    private BizExpense mustGet(Long expenseId)
    {
        BizExpense expense = expenseMapper.selectBizExpenseById(expenseId);
        if (expense == null)
        {
            throw new ServiceException("报销单不存在");
        }
        return expense;
    }

    private void checkOwner(BizExpense expense)
    {
        if (!expense.getApplicantId().equals(SecurityUtils.getUserId()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId()))
        {
            throw new ServiceException("仅申请人本人可操作该报销单");
        }
    }

    private void checkBase(BizExpense expense)
    {
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("报销金额必须大于0");
        }
        if (expense.getExpenseDate() == null)
        {
            throw new ServiceException("费用发生日期不能为空");
        }
    }
}
