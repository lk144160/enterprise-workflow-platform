package com.ruoyi.business.message.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.message.domain.SysMessage;
import com.ruoyi.business.message.service.ISysMessageService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 站内消息
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/system/message")
public class SysMessageController extends BaseController
{
    @Autowired
    private ISysMessageService messageService;

    /**
     * 我的消息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysMessage message)
    {
        startPage();
        message.setReceiverId(SecurityUtils.getUserId());
        List<SysMessage> list = messageService.selectMyMessageList(message);
        return getDataTable(list);
    }

    /**
     * 未读数量
     */
    @GetMapping("/unreadCount")
    public AjaxResult unreadCount()
    {
        return success(messageService.countUnread(SecurityUtils.getUserId()));
    }

    /**
     * 标记已读
     */
    @PostMapping("/read/{messageId}")
    public AjaxResult markRead(@PathVariable Long messageId)
    {
        return toAjax(messageService.markRead(messageId, SecurityUtils.getUserId()));
    }

    /**
     * 全部已读
     */
    @PostMapping("/readAll")
    public AjaxResult markAllRead()
    {
        return toAjax(messageService.markAllRead(SecurityUtils.getUserId()));
    }
}
