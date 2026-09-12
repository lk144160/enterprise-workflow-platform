package com.ruoyi.business.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.crm.domain.CrmCustomer;

/**
 * 客户线索 Mapper 接口
 *
 * @author renovationops
 */
public interface CrmCustomerMapper
{
    public CrmCustomer selectCrmCustomerById(Long customerId);

    public CrmCustomer selectCrmCustomerByPhone(String phone);

    public List<CrmCustomer> selectCrmCustomerList(CrmCustomer crmCustomer);

    /** 超期未跟进线索（待跟进/跟进中且最近跟进或创建超过7天，提醒扫描用） */
    public List<CrmCustomer> selectOverdueFollowList(@Param("days") int days);

    public int insertCrmCustomer(CrmCustomer crmCustomer);

    public int updateCrmCustomer(CrmCustomer crmCustomer);

    /** 仅更新状态（业务联动用） */
    public int updateCustomerStatus(@Param("customerId") Long customerId, @Param("status") String status);

    /** 更新负责设计师/家装顾问绑定（允许置空） */
    public int updateCustomerBind(CrmCustomer crmCustomer);

    public int deleteCrmCustomerById(Long customerId);

    public int deleteCrmCustomerByIds(Long[] customerIds);
}
