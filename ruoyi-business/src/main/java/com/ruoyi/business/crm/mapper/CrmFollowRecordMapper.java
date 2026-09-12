package com.ruoyi.business.crm.mapper;

import java.util.List;
import com.ruoyi.business.crm.domain.CrmFollowRecord;

/**
 * 线索跟进记录 Mapper 接口
 *
 * @author renovationops
 */
public interface CrmFollowRecordMapper
{
    public CrmFollowRecord selectCrmFollowRecordById(Long recordId);

    public List<CrmFollowRecord> selectRecordListByCustomerId(Long customerId);

    /** 跟进记录列表（客情-跟进动态，关联客户信息，支持客户名/方式筛选） */
    public List<CrmFollowRecord> selectFollowRecordList(CrmFollowRecord record);

    public int insertCrmFollowRecord(CrmFollowRecord record);

    public int deleteRecordById(Long recordId);
}
