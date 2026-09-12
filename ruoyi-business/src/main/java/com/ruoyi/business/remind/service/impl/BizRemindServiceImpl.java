package com.ruoyi.business.remind.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.remind.channel.RemindNotifyDispatcher;
import com.ruoyi.business.remind.channel.RemindNotifyMessage;
import com.ruoyi.business.remind.domain.BizRemind;
import com.ruoyi.business.remind.mapper.BizRemindMapper;
import com.ruoyi.business.remind.service.IBizRemindService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 自定义提醒Service业务层处理
 *
 * @author renovationops
 */
@Service
public class BizRemindServiceImpl implements IBizRemindService
{
    private static final Logger log = LoggerFactory.getLogger(BizRemindServiceImpl.class);

    /** 状态：待触发/已触发/已取消 */
    private static final String STATUS_PENDING = "0";
    private static final String STATUS_TRIGGERED = "1";
    private static final String STATUS_CANCELLED = "2";

    /** 重复规则：一次性/每天/每周/每月 */
    private static final String REPEAT_ONCE = "0";
    private static final String REPEAT_DAILY = "1";
    private static final String REPEAT_WEEKLY = "2";
    private static final String REPEAT_MONTHLY = "3";

    @Autowired
    private BizRemindMapper remindMapper;

    @Autowired
    private RemindNotifyDispatcher notifyDispatcher;

    @Override
    public List<BizRemind> selectBizRemindList(BizRemind bizRemind)
    {
        return remindMapper.selectBizRemindList(bizRemind);
    }

    @Override
    public BizRemind selectBizRemindById(Long remindId)
    {
        return remindMapper.selectBizRemindById(remindId);
    }

    @Override
    public int insertBizRemind(BizRemind bizRemind)
    {
        validateRemindTime(bizRemind.getRemindTime());
        if (bizRemind.getReceiverId() == null)
        {
            // 未指定接收人时默认提醒自己
            bizRemind.setReceiverId(SecurityUtils.getUserId());
        }
        if (bizRemind.getRepeatType() == null || bizRemind.getRepeatType().isEmpty())
        {
            bizRemind.setRepeatType(REPEAT_ONCE);
        }
        bizRemind.setStatus(STATUS_PENDING);
        bizRemind.setCreateBy(SecurityUtils.getUsername());
        return remindMapper.insertBizRemind(bizRemind);
    }

    @Override
    public int updateBizRemind(BizRemind bizRemind)
    {
        BizRemind old = requireOwnedPending(bizRemind.getRemindId());
        validateRemindTime(bizRemind.getRemindTime());
        if (bizRemind.getReceiverId() == null)
        {
            bizRemind.setReceiverId(old.getReceiverId());
        }
        if (bizRemind.getRepeatType() == null || bizRemind.getRepeatType().isEmpty())
        {
            bizRemind.setRepeatType(old.getRepeatType());
        }
        bizRemind.setStatus(STATUS_PENDING);
        bizRemind.setUpdateBy(SecurityUtils.getUsername());
        return remindMapper.updateBizRemind(bizRemind);
    }

    @Override
    public int cancelBizRemindById(Long remindId)
    {
        requireOwnedPending(remindId);
        BizRemind update = new BizRemind();
        update.setRemindId(remindId);
        update.setStatus(STATUS_CANCELLED);
        update.setUpdateBy(SecurityUtils.getUsername());
        return remindMapper.updateBizRemind(update);
    }

    @Override
    public int deleteBizRemindByIds(Long[] remindIds)
    {
        for (Long remindId : remindIds)
        {
            requireOwned(remindId);
        }
        return remindMapper.deleteBizRemindByIds(remindIds);
    }

    @Override
    public int scanAndNotifyDueReminds()
    {
        List<BizRemind> dueList = remindMapper.selectDueRemindList();
        int triggered = 0;
        for (BizRemind remind : dueList)
        {
            try
            {
                notifyDispatcher.dispatch(buildNotifyMessage(remind));
            }
            catch (Exception e)
            {
                log.error("提醒分发失败，remindId={}", remind.getRemindId(), e);
            }
            BizRemind update = new BizRemind();
            update.setRemindId(remind.getRemindId());
            if (REPEAT_ONCE.equals(remind.getRepeatType()))
            {
                // 一次性提醒：置为已触发
                update.setStatus(STATUS_TRIGGERED);
            }
            else
            {
                // 重复提醒：滚动到下一次未来触发时间
                update.setRemindTime(nextRemindTime(remind.getRemindTime(), remind.getRepeatType()));
            }
            remindMapper.updateBizRemind(update);
            triggered++;
        }
        if (triggered > 0)
        {
            log.info("自定义提醒扫描完成，本次触发提醒数：{}", triggered);
        }
        return triggered;
    }

    /** 校验提醒时间必须是未来时间 */
    private void validateRemindTime(Date remindTime)
    {
        if (remindTime == null || !remindTime.after(new Date()))
        {
            throw new ServiceException("提醒时间必须晚于当前时间");
        }
    }

    /** 校验提醒存在、归属当前用户（或管理员）且处于待触发状态 */
    private BizRemind requireOwnedPending(Long remindId)
    {
        BizRemind remind = requireOwned(remindId);
        if (!STATUS_PENDING.equals(remind.getStatus()))
        {
            throw new ServiceException("仅待触发状态的提醒可操作");
        }
        return remind;
    }

    /** 校验提醒存在且归属当前用户（或管理员） */
    private BizRemind requireOwned(Long remindId)
    {
        BizRemind remind = remindMapper.selectBizRemindById(remindId);
        if (remind == null)
        {
            throw new ServiceException("提醒不存在");
        }
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())
                && !SecurityUtils.getUsername().equals(remind.getCreateBy()))
        {
            throw new ServiceException("无权操作他人创建的提醒");
        }
        return remind;
    }

    /** 构建渠道无关的通知消息（含关联业务跳转路由） */
    private RemindNotifyMessage buildNotifyMessage(BizRemind remind)
    {
        RemindNotifyMessage message = new RemindNotifyMessage();
        message.setRemindId(remind.getRemindId());
        message.setReceiverId(remind.getReceiverId());
        message.setTitle(remind.getTitle());
        message.setContent(remind.getContent());
        message.setRemindTime(remind.getRemindTime());
        message.setRepeatType(remind.getRepeatType());
        message.setRoutePath(resolveRoutePath(remind.getRelateType()));
        if (remind.getRelateId() != null)
        {
            message.setRouteParams("{\"relateId\":" + remind.getRelateId()
                    + ",\"relateType\":\"" + (remind.getRelateType() == null ? "" : remind.getRelateType()) + "\"}");
        }
        return message;
    }

    /** 关联业务类型 → 前端路由 */
    private String resolveRoutePath(String relateType)
    {
        if (relateType == null)
        {
            return "/remind";
        }
        switch (relateType)
        {
            case "customer":  return "/market/customer/profile";
            case "quote":     return "/market/customer/quote";
            case "contract":  return "/contract";
            case "payment":   return "/payment";
            case "drawing":   return "/design/drawing";
            case "disclosure": return "/design/disclosure";
            default:          return "/remind";
        }
    }

    /** 计算下一次触发时间（若仍落后于当前时间则继续滚动，避免漏扫积压） */
    private Date nextRemindTime(Date from, String repeatType)
    {
        Date next = plusRepeat(from, repeatType);
        Date now = new Date();
        int guard = 0;
        while (!next.after(now) && guard < 100000)
        {
            next = plusRepeat(next, repeatType);
            guard++;
        }
        return next;
    }

    private Date plusRepeat(Date from, String repeatType)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        switch (repeatType)
        {
            case REPEAT_DAILY:   calendar.add(Calendar.DAY_OF_MONTH, 1); break;
            case REPEAT_WEEKLY:  calendar.add(Calendar.DAY_OF_MONTH, 7); break;
            case REPEAT_MONTHLY: calendar.add(Calendar.MONTH, 1); break;
            default:             calendar.add(Calendar.DAY_OF_MONTH, 1); break;
        }
        return calendar.getTime();
    }
}
