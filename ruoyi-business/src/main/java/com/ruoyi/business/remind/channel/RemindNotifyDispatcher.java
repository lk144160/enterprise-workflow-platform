package com.ruoyi.business.remind.channel;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * 提醒通知分发器
 *
 * 将到点提醒分发给所有已注册的 {@link RemindNotifyChannel} 渠道实现，
 * 并补全接收人姓名/手机号等渠道所需信息。单渠道失败不影响其他渠道。
 *
 * @author renovationops
 */
@Component
public class RemindNotifyDispatcher
{
    private static final Logger log = LoggerFactory.getLogger(RemindNotifyDispatcher.class);

    @Autowired
    private SysUserMapper userMapper;

    /** Spring 注入所有渠道实现（新增 @Component 渠道即自动生效） */
    private final List<RemindNotifyChannel> channels;

    public RemindNotifyDispatcher(List<RemindNotifyChannel> channels)
    {
        this.channels = channels;
    }

    /**
     * 分发提醒通知到全部渠道
     */
    public void dispatch(RemindNotifyMessage message)
    {
        fillReceiverInfo(message);
        for (RemindNotifyChannel channel : channels)
        {
            try
            {
                channel.send(message);
            }
            catch (Exception e)
            {
                log.error("提醒通知渠道[{}]发送失败，remindId={}，receiverId={}",
                        channel.getChannel(), message.getRemindId(), message.getReceiverId(), e);
            }
        }
    }

    /** 补全接收人姓名与手机号（短信等渠道直接可用） */
    private void fillReceiverInfo(RemindNotifyMessage message)
    {
        if (message.getReceiverId() == null)
        {
            return;
        }
        SysUser user = userMapper.selectUserById(message.getReceiverId());
        if (user != null)
        {
            message.setReceiverName(user.getNickName());
            message.setReceiverPhone(user.getPhonenumber());
        }
    }
}
