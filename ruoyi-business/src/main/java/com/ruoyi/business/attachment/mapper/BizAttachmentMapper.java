package com.ruoyi.business.attachment.mapper;

import java.util.List;
import com.ruoyi.business.attachment.domain.BizAttachment;

/**
 * 业务附件 Mapper 接口
 *
 * @author renovationops
 */
public interface BizAttachmentMapper
{
    public List<BizAttachment> selectAttachmentList(BizAttachment attachment);

    public BizAttachment selectAttachmentById(Long attachmentId);

    public int insertAttachment(BizAttachment attachment);

    public int deleteAttachmentById(Long attachmentId);

    /** 按业务单据统计附件数（发票张数校验等） */
    public int countByBiz(String bizType, Long bizId);

    /** 按业务单据删除附件记录（单据删除时级联清理） */
    public int deleteByBiz(String bizType, Long bizId);
}
