package com.ruoyi.business.attachment.service;

import java.util.List;
import com.ruoyi.business.attachment.domain.BizAttachment;

/**
 * 业务附件服务接口
 *
 * @author renovationops
 */
public interface IBizAttachmentService
{
    public List<BizAttachment> selectAttachmentList(BizAttachment attachment);

    /** 保存附件记录（文件上传走通用 /common/upload，此处只存关联） */
    public int saveAttachment(BizAttachment attachment);

    /** 按业务单据保存多条附件 */
    public int saveAttachments(String bizType, Long bizId, List<BizAttachment> attachments);

    public int deleteAttachmentById(Long attachmentId);

    /** 按业务单据统计附件数 */
    public int countByBiz(String bizType, Long bizId);

    /** 按业务单据删除附件记录（单据删除时级联清理） */
    public int deleteByBiz(String bizType, Long bizId);
}
