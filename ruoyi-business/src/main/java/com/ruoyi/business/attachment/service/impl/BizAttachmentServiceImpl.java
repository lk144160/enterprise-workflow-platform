package com.ruoyi.business.attachment.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.attachment.domain.BizAttachment;
import com.ruoyi.business.attachment.mapper.BizAttachmentMapper;
import com.ruoyi.business.attachment.service.IBizAttachmentService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 业务附件服务实现
 *
 * @author renovationops
 */
@Service
public class BizAttachmentServiceImpl implements IBizAttachmentService
{
    @Autowired
    private BizAttachmentMapper attachmentMapper;

    @Override
    public List<BizAttachment> selectAttachmentList(BizAttachment attachment)
    {
        return attachmentMapper.selectAttachmentList(attachment);
    }

    @Override
    public int saveAttachment(BizAttachment attachment)
    {
        attachment.setUploadUserId(SecurityUtils.getUserId());
        attachment.setCreateBy(SecurityUtils.getUsername());
        return attachmentMapper.insertAttachment(attachment);
    }

    @Override
    @Transactional
    public int saveAttachments(String bizType, Long bizId, List<BizAttachment> attachments)
    {
        if (attachments == null || attachments.isEmpty())
        {
            return 0;
        }
        int rows = 0;
        for (BizAttachment att : attachments)
        {
            att.setBizType(bizType);
            att.setBizId(bizId);
            rows += saveAttachment(att);
        }
        return rows;
    }

    @Override
    public int deleteAttachmentById(Long attachmentId)
    {
        return attachmentMapper.deleteAttachmentById(attachmentId);
    }

    @Override
    public int countByBiz(String bizType, Long bizId)
    {
        return attachmentMapper.countByBiz(bizType, bizId);
    }

    @Override
    public int deleteByBiz(String bizType, Long bizId)
    {
        return attachmentMapper.deleteByBiz(bizType, bizId);
    }
}
