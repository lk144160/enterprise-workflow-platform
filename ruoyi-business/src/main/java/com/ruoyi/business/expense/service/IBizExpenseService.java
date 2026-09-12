package com.ruoyi.business.expense.service;

import java.util.List;
import com.ruoyi.business.expense.domain.BizExpense;

/**
 * 报销单服务接口
 *
 * @author renovationops
 */
public interface IBizExpenseService
{
    public BizExpense selectBizExpenseById(Long expenseId);

    public List<BizExpense> selectBizExpenseList(BizExpense expense);

    public int insertBizExpense(BizExpense expense);

    public int updateBizExpense(BizExpense expense);

    public int deleteBizExpenseByIds(Long[] expenseIds);

    /** 提交审批（校验发票附件张数一致） */
    public void submitExpense(Long expenseId);

    /** 打款登记（财务） */
    public void payExpense(Long expenseId);

    /** 作废 */
    public void invalidateExpense(Long expenseId);
}
