package com.ruoyi.business.remind.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.remind.domain.BizRemind;
import com.ruoyi.business.remind.service.IBizRemindService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 自定义提醒Controller
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/remind")
public class BizRemindController extends BaseController
{
    @Autowired
    private IBizRemindService remindService;

    /**
     * 查询自定义提醒列表（普通用户仅见自己收到或创建的，管理员可见全部；
     * 按关联业务查询时返回该业务的全部提醒）
     */
    @PreAuthorize("@ss.hasPermi('biz:remind:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizRemind bizRemind)
    {
        if (bizRemind.getRelateId() == null
                && !SecurityUtils.isAdmin(SecurityUtils.getUserId()))
        {
            bizRemind.setMineUserId(SecurityUtils.getUserId());
            bizRemind.setMineUserName(SecurityUtils.getUsername());
        }
        startPage();
        List<BizRemind> list = remindService.selectBizRemindList(bizRemind);
        return getDataTable(list);
    }

    /**
     * 获取自定义提醒详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:remind:query')")
    @GetMapping(value = "/{remindId}")
    public AjaxResult getInfo(@PathVariable("remindId") Long remindId)
    {
        return success(remindService.selectBizRemindById(remindId));
    }

    /**
     * 新增自定义提醒
     */
    @PreAuthorize("@ss.hasPermi('biz:remind:add')")
    @Log(title = "自定义提醒", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BizRemind bizRemind)
    {
        return toAjax(remindService.insertBizRemind(bizRemind));
    }

    /**
     * 修改自定义提醒
     */
    @PreAuthorize("@ss.hasPermi('biz:remind:edit')")
    @Log(title = "自定义提醒", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BizRemind bizRemind)
    {
        return toAjax(remindService.updateBizRemind(bizRemind));
    }

    /**
     * 取消自定义提醒
     */
    @PreAuthorize("@ss.hasPermi('biz:remind:cancel')")
    @Log(title = "自定义提醒", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{remindId}")
    public AjaxResult cancel(@PathVariable("remindId") Long remindId)
    {
        return toAjax(remindService.cancelBizRemindById(remindId));
    }

    /**
     * 删除自定义提醒
     */
    @PreAuthorize("@ss.hasPermi('biz:remind:remove')")
    @Log(title = "自定义提醒", businessType = BusinessType.DELETE)
    @DeleteMapping("/{remindIds}")
    public AjaxResult remove(@PathVariable Long[] remindIds)
    {
        return toAjax(remindService.deleteBizRemindByIds(remindIds));
    }
}
