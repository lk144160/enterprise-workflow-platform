package com.ruoyi.business.message.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.message.domain.SysMessage;
import com.ruoyi.business.message.mapper.SysMessageMapper;
import com.ruoyi.business.message.service.ISysMessageService;
import com.ruoyi.common.utils.StringUtils;

/**
 * 站内消息服务实现
 *
 * @author renovationops
 */
@Service
public class SysMessageServiceImpl implements ISysMessageService
{
    private static final Logger log = LoggerFactory.getLogger(SysMessageServiceImpl.class);

    @Autowired
    private SysMessageMapper messageMapper;

    @Override
    public void send(Long receiverId, String msgType, String title, String content, String routePath, String routeParams)
    {
        if (receiverId == null)
        {
            return;
        }
        try
        {
            SysMessage msg = new SysMessage();
            msg.setReceiverId(receiverId);
            msg.setMsgType(StringUtils.isEmpty(msgType) ? "notice" : msgType);
            msg.setTitle(title);
            msg.setContent(content);
            msg.setRoutePath(routePath);
            msg.setRouteParams(routeParams);
            messageMapper.insertSysMessage(msg);
        }
        catch (Exception e)
        {
            // 消息发送失败不影响主业务
            log.error("发送站内消息失败: receiverId={}, title={}", receiverId, title, e);
        }
    }

    @Override
    public void sendBatch(List<Long> receiverIds, String msgType, String title, String content, String routePath, String routeParams)
    {
        if (StringUtils.isEmpty(receiverIds))
        {
            return;
        }
        for (Long receiverId : receiverIds)
        {
            send(receiverId, msgType, title, content, routePath, routeParams);
        }
    }

    @Override
    public List<SysMessage> selectMyMessageList(SysMessage sysMessage)
    {
        return messageMapper.selectSysMessageList(sysMessage);
    }

    @Override
    public int countUnread(Long receiverId)
    {
        return messageMapper.countUnread(receiverId);
    }

    @Override
    public int markRead(Long messageId, Long receiverId)
    {
        return messageMapper.markRead(messageId, receiverId);
    }

    @Override
    public int markAllRead(Long receiverId)
    {
        return messageMapper.markAllRead(receiverId);
    }
}
