package com.ruoyi.business.contract.mapper;

import java.util.List;
import com.ruoyi.business.contract.domain.BizPaymentRecord;

/**
 * 收款记录 Mapper 接口
 *
 * @author renovationops
 */
public interface BizPaymentRecordMapper
{
    public BizPaymentRecord selectBizPaymentRecordById(Long paymentId);

    public List<BizPaymentRecord> selectBizPaymentRecordList(BizPaymentRecord record);

    public List<BizPaymentRecord> selectRecordsByContractId(Long contractId);

    public int insertBizPaymentRecord(BizPaymentRecord record);
}
