package com.ruoyi.business.remind.mapper;

import java.util.List;
import com.ruoyi.business.remind.domain.BizRemind;

/**
 * 自定义提醒Mapper接口
 *
 * @author renovationops
 */
public interface BizRemindMapper
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
     * 查询到期待触发的提醒（status=0 且 remind_time <= now）
     */
    public List<BizRemind> selectDueRemindList();

    /**
     * 新增自定义提醒
     */
    public int insertBizRemind(BizRemind bizRemind);

    /**
     * 修改自定义提醒
     */
    public int updateBizRemind(BizRemind bizRemind);

    /**
     * 删除自定义提醒
     */
    public int deleteBizRemindByIds(Long[] remindIds);
}
