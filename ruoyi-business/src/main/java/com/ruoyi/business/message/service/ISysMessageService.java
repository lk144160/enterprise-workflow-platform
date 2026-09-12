package com.ruoyi.business.message.service;

import java.util.List;
import com.ruoyi.business.message.domain.SysMessage;

/**
 * 站内消息服务接口
 *
 * @author renovationops
 */
public interface ISysMessageService
{
    /**
     * 发送站内消息
     *
     * @param receiverId 接收人
     * @param msgType 类型（todo/result/notice/warn）
     * @param title 标题
     * @param content 内容
     * @param routePath 前端跳转路由
     * @param routeParams 跳转参数（JSON 字符串，可空）
     */
    void send(Long receiverId, String msgType, String title, String content, String routePath, String routeParams);

    /**
     * 发送给多个接收人
     */
    void sendBatch(List<Long> receiverIds, String msgType, String title, String content, String routePath, String routeParams);

    public List<SysMessage> selectMyMessageList(SysMessage sysMessage);

    public int countUnread(Long receiverId);

    public int markRead(Long messageId, Long receiverId);

    public int markAllRead(Long receiverId);
}
