package com.ruoyi.business.remind.domain;

import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 自定义提醒对象 biz_remind
 *
 * @author renovationops
 */
public class BizRemind extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 提醒ID */
    private Long remindId;

    /** 提醒标题 */
    private String title;

    /** 提醒内容 */
    private String content;

    /** 触发时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date remindTime;

    /** 重复规则（0一次性 1每天 2每周 3每月） */
    private String repeatType;

    /** 关联业务类型（customer/quote/contract） */
    private String relateType;

    /** 关联业务ID */
    private Long relateId;

    /** 关联业务名称（冗余展示） */
    private String relateName;

    /** 接收人 */
    private Long receiverId;

    /** 接收人姓名（关联查询） */
    private String receiverName;

    /** 状态（0待触发 1已触发 2已取消） */
    private String status;

    /** 查询辅助：仅看我收到或我创建的（用户ID） */
    private transient Long mineUserId;

    /** 查询辅助：仅看我收到或我创建的（用户名） */
    private transient String mineUserName;

    public Long getRemindId() { return remindId; }
    public void setRemindId(Long remindId) { this.remindId = remindId; }

    @NotBlank(message = "提醒标题不能为空")
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @NotNull(message = "提醒时间不能为空")
    public Date getRemindTime() { return remindTime; }
    public void setRemindTime(Date remindTime) { this.remindTime = remindTime; }

    public String getRepeatType() { return repeatType; }
    public void setRepeatType(String repeatType) { this.repeatType = repeatType; }

    public String getRelateType() { return relateType; }
    public void setRelateType(String relateType) { this.relateType = relateType; }

    public Long getRelateId() { return relateId; }
    public void setRelateId(Long relateId) { this.relateId = relateId; }

    public String getRelateName() { return relateName; }
    public void setRelateName(String relateName) { this.relateName = relateName; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getMineUserId() { return mineUserId; }
    public void setMineUserId(Long mineUserId) { this.mineUserId = mineUserId; }

    public String getMineUserName() { return mineUserName; }
    public void setMineUserName(String mineUserName) { this.mineUserName = mineUserName; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("remindId", getRemindId())
                .append("title", getTitle())
                .append("remindTime", getRemindTime())
                .append("repeatType", getRepeatType())
                .append("receiverId", getReceiverId())
                .append("status", getStatus())
                .toString();
    }
}
