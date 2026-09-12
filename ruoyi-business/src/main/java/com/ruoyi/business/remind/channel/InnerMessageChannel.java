package com.ruoyi.business.remind.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.business.message.service.ISysMessageService;

/**
 * 站内信通知渠道（默认渠道）
 *
 * @author renovationops
 */
@Component
public class InnerMessageChannel implements RemindNotifyChannel
{
    /** 站内消息类型：提醒 */
    private static final String MSG_TYPE_REMIND = "remind";

    @Autowired
    private ISysMessageService messageService;

    @Override
    public String getChannel()
    {
        return "inner";
    }

    @Override
    public void send(RemindNotifyMessage message)
    {
        messageService.send(message.getReceiverId(), MSG_TYPE_REMIND,
                message.getTitle(), message.getContent(),
                message.getRoutePath(), message.getRouteParams());
    }
}
