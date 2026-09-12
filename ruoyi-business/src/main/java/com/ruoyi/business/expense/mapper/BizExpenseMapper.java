package com.ruoyi.business.expense.mapper;

import java.util.List;
import com.ruoyi.business.expense.domain.BizExpense;

/**
 * 报销单 Mapper 接口
 *
 * @author renovationops
 */
public interface BizExpenseMapper
{
    public BizExpense selectBizExpenseById(Long expenseId);

    public List<BizExpense> selectBizExpenseList(BizExpense expense);

    public int insertBizExpense(BizExpense expense);

    public int updateBizExpense(BizExpense expense);

    public int deleteBizExpenseByIds(Long[] expenseIds);
}
