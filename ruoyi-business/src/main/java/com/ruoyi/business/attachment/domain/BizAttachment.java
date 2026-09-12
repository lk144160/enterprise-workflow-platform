package com.ruoyi.business.attachment.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 业务附件 biz_attachment
 *
 * @author renovationops
 */
public class BizAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 附件ID */
    private Long attachmentId;

    /** 业务类型（quote/contract/drawing/disclosure/expense/payment_voucher） */
    private String bizType;

    /** 业务单据ID */
    private Long bizId;

    /** 文件地址 */
    private String fileUrl;

    /** 原始文件名 */
    private String fileName;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 上传人 */
    private Long uploadUserId;

    public Long getAttachmentId() { return attachmentId; }
    public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public Long getUploadUserId() { return uploadUserId; }
    public void setUploadUserId(Long uploadUserId) { this.uploadUserId = uploadUserId; }
}
