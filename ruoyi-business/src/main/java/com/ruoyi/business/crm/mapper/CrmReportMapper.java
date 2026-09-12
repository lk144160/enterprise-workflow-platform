package com.ruoyi.business.crm.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.crm.domain.CrmCustomer;
import com.ruoyi.business.crm.domain.CrmFollowRecord;
import com.ruoyi.business.crm.domain.CrmReportQuery;

/**
 * 客情统计 Mapper 接口
 *
 * @author renovationops
 */
public interface CrmReportMapper
{
    /** 时间段内新增客户数 */
    public int countNewCustomers(CrmReportQuery query);

    /** 时间段内跟进总次数 */
    public int countFollows(CrmReportQuery query);

    /** 时间段内到访次数 */
    public int countVisits(CrmReportQuery query);

    /** 时间段内创建报价单数 */
    public int countQuotes(CrmReportQuery query);

    /** 时间段内签约合同数 */
    public int countContracts(CrmReportQuery query);

    /** 时间段内签约总金额 */
    public BigDecimal sumContractAmount(CrmReportQuery query);

    /** 客户明细：时间段内新增客户 */
    public List<CrmCustomer> selectCustomerDetails(CrmReportQuery query);

    /** 跟进明细：时间段内跟进记录 */
    public List<CrmFollowRecord> selectFollowDetails(CrmReportQuery query);

    /** 签约明细：时间段内签约合同 */
    public List<BizContract> selectContractDetails(CrmReportQuery query);
}
