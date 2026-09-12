package com.ruoyi.business.crm.service;

import java.util.List;
import com.ruoyi.business.crm.domain.CrmCustomer;
import com.ruoyi.business.crm.domain.CrmFollowRecord;

/**
 * 客户线索服务接口
 *
 * @author renovationops
 */
public interface ICrmCustomerService
{
    public CrmCustomer selectCrmCustomerById(Long customerId);

    public List<CrmCustomer> selectCrmCustomerList(CrmCustomer crmCustomer);

    public int insertCrmCustomer(CrmCustomer crmCustomer);

    public int updateCrmCustomer(CrmCustomer crmCustomer);

    public int deleteCrmCustomerByIds(Long[] customerIds);

    /** 新增跟进记录并联动更新线索 */
    public int addFollowRecord(CrmFollowRecord record);

    /** 跟进记录列表（客情-跟进动态） */
    public List<CrmFollowRecord> selectFollowRecordList(CrmFollowRecord record);

    /** 流失登记 */
    public int markLoss(Long customerId, String lossReason);

    /** 业务联动更新状态（3已转化 4已签约） */
    public void updateStatus(Long customerId, String status);
}
