package com.ruoyi.business.remind.channel;

import java.util.Date;

/**
 * 提醒通知消息（渠道无关的统一消息体）
 *
 * @author renovationops
 */
public class RemindNotifyMessage
{
    /** 提醒ID */
    private Long remindId;

    /** 接收人用户ID */
    private Long receiverId;

    /** 接收人姓名 */
    private String receiverName;

    /** 接收人手机号（供短信等渠道使用） */
    private String receiverPhone;

    /** 提醒标题 */
    private String title;

    /** 提醒内容 */
    private String content;

    /** 触发时间 */
    private Date remindTime;

    /** 重复规则（0一次性 1每天 2每周 3每月） */
    private String repeatType;

    /** 前端跳转路由 */
    private String routePath;

    /** 跳转参数（JSON） */
    private String routeParams;

    public Long getRemindId() { return remindId; }
    public void setRemindId(Long remindId) { this.remindId = remindId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getRemindTime() { return remindTime; }
    public void setRemindTime(Date remindTime) { this.remindTime = remindTime; }
    public String getRepeatType() { return repeatType; }
    public void setRepeatType(String repeatType) { this.repeatType = repeatType; }
    public String getRoutePath() { return routePath; }
    public void setRoutePath(String routePath) { this.routePath = routePath; }
    public String getRouteParams() { return routeParams; }
    public void setRouteParams(String routeParams) { this.routeParams = routeParams; }
}
