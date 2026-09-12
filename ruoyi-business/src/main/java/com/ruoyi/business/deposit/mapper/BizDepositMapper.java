package com.ruoyi.business.deposit.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.business.deposit.domain.BizDeposit;

/**
 * 定金记录Mapper接口
 *
 * @author renovationops
 */
public interface BizDepositMapper
{
    public BizDeposit selectBizDepositById(Long depositId);

    public List<BizDeposit> selectBizDepositList(BizDeposit bizDeposit);

    /** 客户名下各状态定金统计（已收未抵扣金额合计） */
    public BigDecimal selectReceivedSumByCustomer(Long customerId);

    public int insertBizDeposit(BizDeposit bizDeposit);

    public int updateBizDeposit(BizDeposit bizDeposit);

    public int deleteBizDepositById(Long depositId);

    public int deleteBizDepositByIds(Long[] depositIds);
}
