package com.ruoyi.business.message.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 站内消息 sys_message
 *
 * @author renovationops
 */
public class SysMessage
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long messageId;

    /** 接收人 */
    private Long receiverId;

    /** 接收人姓名（关联查询） */
    private String receiverName;

    /** 类型（todo待办/result审批结果/notice公告/warn预警） */
    private String msgType;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 前端跳转路由 */
    private String routePath;

    /** 跳转参数（JSON） */
    private String routeParams;

    /** 已读（0未读 1已读） */
    private String isRead;

    /** 阅读时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }
    public String getRouteParams() { return routeParams; }
    public void setRouteParams(String routeParams) { this.routeParams = routeParams; }
    public String getIsRead() { return isRead; }
    public void setIsRead(String isRead) { this.isRead = isRead; }
    public Date getReadTime() { return readTime; }
    public void setReadTime(Date readTime) { this.readTime = readTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
