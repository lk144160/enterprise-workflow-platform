package com.ruoyi.business.deposit.service;

import java.util.List;
import com.ruoyi.business.deposit.domain.BizDeposit;

/**
 * 定金服务接口
 *
 * @author renovationops
 */
public interface IBizDepositService
{
    public BizDeposit selectBizDepositById(Long depositId);

    public List<BizDeposit> selectBizDepositList(BizDeposit bizDeposit);

    /** 客户已收未抵扣定金合计 */
    public java.math.BigDecimal selectReceivedSumByCustomer(Long customerId);

    public int insertBizDeposit(BizDeposit bizDeposit);

    public int updateBizDeposit(BizDeposit bizDeposit);

    public int deleteBizDepositByIds(Long[] depositIds);

    /** 签约抵扣：定金转合同第1期收款 */
    public int deductDeposit(Long depositId, Long contractId);

    /** 发起退还审批 */
    public void applyRefund(Long depositId, String reason);
}
