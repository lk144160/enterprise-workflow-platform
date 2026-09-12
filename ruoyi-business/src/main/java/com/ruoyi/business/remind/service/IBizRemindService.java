package com.ruoyi.business.remind.service;

import java.util.List;
import com.ruoyi.business.remind.domain.BizRemind;

/**
 * 自定义提醒Service接口
 *
 * @author renovationops
 */
public interface IBizRemindService
{
    /**
     * 查询自定义提醒列表
     */
    public List<BizRemind> selectBizRemindList(BizRemind bizRemind);

    /**
     * 查询自定义提醒
     */
    public BizRemind selectBizRemindById(Long remindId);

    /**
     * 新增自定义提醒
     */
    public int insertBizRemind(BizRemind bizRemind);

    /**
     * 修改自定义提醒（仅待触发状态可改）
     */
    public int updateBizRemind(BizRemind bizRemind);

    /**
     * 取消提醒（仅待触发状态可取消）
     */
    public int cancelBizRemindById(Long remindId);

    /**
     * 批量删除自定义提醒
     */
    public int deleteBizRemindByIds(Long[] remindIds);

    /**
     * 扫描到点提醒并分发通知（定时任务调用，每5分钟）
     *
     * @return 本次触发的提醒数
     */
    public int scanAndNotifyDueReminds();
}
