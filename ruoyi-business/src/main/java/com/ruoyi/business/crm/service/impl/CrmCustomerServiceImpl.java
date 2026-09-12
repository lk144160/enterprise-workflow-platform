package com.ruoyi.business.crm.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.crm.domain.CrmCustomer;
import com.ruoyi.business.crm.domain.CrmFollowRecord;
import com.ruoyi.business.crm.mapper.CrmCustomerMapper;
import com.ruoyi.business.crm.mapper.CrmFollowRecordMapper;
import com.ruoyi.business.crm.service.ICrmCustomerService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 客户线索服务实现
 *
 * @author renovationops
 */
@Service
public class CrmCustomerServiceImpl implements ICrmCustomerService
{
    /** 线索状态 */
    public static final String STATUS_PENDING = "1";
    public static final String STATUS_FOLLOWING = "2";
    public static final String STATUS_CONVERTED = "3";
    public static final String STATUS_SIGNED = "4";
    public static final String STATUS_LOST = "5";

    /** 跟进方式-到访 */
    public static final String FOLLOW_TYPE_VISIT = "3";

    @Autowired
    private CrmCustomerMapper customerMapper;

    @Autowired
    private CrmFollowRecordMapper followRecordMapper;

    @Override
    public CrmCustomer selectCrmCustomerById(Long customerId)
    {
        CrmCustomer customer = customerMapper.selectCrmCustomerById(customerId);
        if (customer != null)
        {
            customer.setFollowRecords(followRecordMapper.selectRecordListByCustomerId(customerId));
        }
        return customer;
    }

    @Override
    public List<CrmCustomer> selectCrmCustomerList(CrmCustomer crmCustomer)
    {
        return customerMapper.selectCrmCustomerList(crmCustomer);
    }

    @Override
    public int insertCrmCustomer(CrmCustomer crmCustomer)
    {
        checkPhoneUnique(crmCustomer);
        if (StringUtils.isEmpty(crmCustomer.getStatus()))
        {
            crmCustomer.setStatus(STATUS_PENDING);
        }
        if (crmCustomer.getDeptId() == null)
        {
            crmCustomer.setDeptId(SecurityUtils.getLoginUser().getDeptId());
        }
        crmCustomer.setCreateBy(SecurityUtils.getUsername());
        return customerMapper.insertCrmCustomer(crmCustomer);
    }

    @Override
    public int updateCrmCustomer(CrmCustomer crmCustomer)
    {
        checkPhoneUnique(crmCustomer);
        crmCustomer.setUpdateBy(SecurityUtils.getUsername());
        int rows = customerMapper.updateCrmCustomer(crmCustomer);
        // 负责设计师/家装顾问绑定单独同步（允许置空，支持后期绑定/解绑）
        customerMapper.updateCustomerBind(crmCustomer);
        return rows;
    }

    @Override
    @Transactional
    public int deleteCrmCustomerByIds(Long[] customerIds)
    {
        for (Long customerId : customerIds)
        {
            CrmCustomer customer = customerMapper.selectCrmCustomerById(customerId);
            if (customer == null)
            {
                continue;
            }
            // 已转化/已签约线索不允许删除
            if (STATUS_CONVERTED.equals(customer.getStatus()) || STATUS_SIGNED.equals(customer.getStatus()))
            {
                throw new ServiceException("线索【" + customer.getCustomerName() + "】已转化/已签约，不允许删除");
            }
        }
        return customerMapper.deleteCrmCustomerByIds(customerIds);
    }

    @Override
    public List<CrmFollowRecord> selectFollowRecordList(CrmFollowRecord record)
    {
        return followRecordMapper.selectFollowRecordList(record);
    }

    @Override
    @Transactional
    public int addFollowRecord(CrmFollowRecord record)
    {
        CrmCustomer customer = customerMapper.selectCrmCustomerById(record.getCustomerId());
        if (customer == null)
        {
            throw new ServiceException("线索不存在");
        }
        if (STATUS_LOST.equals(customer.getStatus()))
        {
            throw new ServiceException("线索已流失，无法继续跟进");
        }
        record.setCreateBy(SecurityUtils.getUsername());
        if (record.getFollowTime() == null)
        {
            record.setFollowTime(new Date());
        }
        // 到访记录默认值：接待人默认当前用户，到访人数默认1
        if (FOLLOW_TYPE_VISIT.equals(record.getFollowType()))
        {
            if (record.getReceptionUserId() == null)
            {
                record.setReceptionUserId(SecurityUtils.getUserId());
            }
            if (record.getVisitCount() == null)
            {
                record.setVisitCount(1);
            }
        }
        else
        {
            // 非到访记录清空到访专属字段
            record.setVisitCount(null);
            record.setCompanion(null);
            record.setReceptionUserId(null);
            record.setVisitPurpose(null);
            record.setFeedback(null);
        }
        int rows = followRecordMapper.insertCrmFollowRecord(record);

        // 联动更新线索：最近跟进时间、下次跟进时间、状态 待跟进->跟进中
        CrmCustomer update = new CrmCustomer();
        update.setCustomerId(record.getCustomerId());
        update.setLatestFollowTime(record.getFollowTime() != null ? record.getFollowTime() : DateUtils.getNowDate());
        update.setNextFollowTime(record.getNextFollowTime());
        if (STATUS_PENDING.equals(customer.getStatus()))
        {
            update.setStatus(STATUS_FOLLOWING);
        }
        update.setUpdateBy(SecurityUtils.getUsername());
        customerMapper.updateCrmCustomer(update);
        return rows;
    }

    @Override
    @Transactional
    public int markLoss(Long customerId, String lossReason)
    {
        CrmCustomer customer = customerMapper.selectCrmCustomerById(customerId);
        if (customer == null)
        {
            throw new ServiceException("线索不存在");
        }
        if (StringUtils.isEmpty(lossReason))
        {
            throw new ServiceException("流失原因必填");
        }
        CrmCustomer update = new CrmCustomer();
        update.setCustomerId(customerId);
        update.setStatus(STATUS_LOST);
        update.setLossReason(lossReason);
        update.setUpdateBy(SecurityUtils.getUsername());
        return customerMapper.updateCrmCustomer(update);
    }

    @Override
    public void updateStatus(Long customerId, String status)
    {
        customerMapper.updateCustomerStatus(customerId, status);
    }

    /**
     * 手机号查重
     */
    private void checkPhoneUnique(CrmCustomer crmCustomer)
    {
        if (StringUtils.isEmpty(crmCustomer.getPhone()))
        {
            throw new ServiceException("手机号不能为空");
        }
        CrmCustomer exist = customerMapper.selectCrmCustomerByPhone(crmCustomer.getPhone());
        if (exist != null && !exist.getCustomerId().equals(crmCustomer.getCustomerId()))
        {
            throw new ServiceException("手机号【" + crmCustomer.getPhone() + "】已存在线索【" + exist.getCustomerName() + "】");
        }
    }
}
