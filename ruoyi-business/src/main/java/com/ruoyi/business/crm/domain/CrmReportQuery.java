package com.ruoyi.business.crm.domain;

/**
 * 客情统计查询条件
 *
 * @author renovationops
 */
public class CrmReportQuery
{
    /** 统计开始日期（yyyy-MM-dd） */
    private String beginTime;

    /** 统计结束日期（yyyy-MM-dd） */
    private String endTime;

    /** 业务员姓名（可选，模糊匹配，空为全部） */
    private String ownerName;

    public String getBeginTime() { return beginTime; }
    public void setBeginTime(String beginTime) { this.beginTime = beginTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
