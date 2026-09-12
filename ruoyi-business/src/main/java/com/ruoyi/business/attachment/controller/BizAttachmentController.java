package com.ruoyi.business.attachment.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.attachment.domain.BizAttachment;
import com.ruoyi.business.attachment.service.IBizAttachmentService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 业务附件
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/business/attachment")
public class BizAttachmentController extends BaseController
{
    @Autowired
    private IBizAttachmentService attachmentService;

    /**
     * 按业务单据查询附件列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BizAttachment attachment)
    {
        List<BizAttachment> list = attachmentService.selectAttachmentList(attachment);
        return getDataTable(list);
    }

    /**
     * 保存附件关联记录
     */
    @PostMapping
    @Log(title = "业务附件", businessType = BusinessType.INSERT)
    public AjaxResult add(@RequestBody BizAttachment attachment)
    {
        return toAjax(attachmentService.saveAttachment(attachment));
    }

    /**
     * 删除附件
     */
    @DeleteMapping("/{attachmentId}")
    @Log(title = "业务附件", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable Long attachmentId)
    {
        return toAjax(attachmentService.deleteAttachmentById(attachmentId));
    }
}
