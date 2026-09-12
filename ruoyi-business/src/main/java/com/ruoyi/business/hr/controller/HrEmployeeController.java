package com.ruoyi.business.hr.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.hr.domain.HrEmployee;
import com.ruoyi.business.hr.service.IHrEmployeeService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 员工档案管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/hr/employee")
public class HrEmployeeController extends BaseController
{
    @Autowired
    private IHrEmployeeService employeeService;

    /**
     * 员工档案列表（身份证脱敏）
     */
    @PreAuthorize("@ss.hasPermi('hr:employee:list')")
    @GetMapping("/list")
    public TableDataInfo list(HrEmployee employee)
    {
        startPage();
        List<HrEmployee> list = employeeService.selectHrEmployeeList(employee);
        return getDataTable(list);
    }

    /**
     * 导出员工档案
     */
    @PreAuthorize("@ss.hasPermi('hr:employee:export')")
    @Log(title = "员工档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HrEmployee employee)
    {
        List<HrEmployee> list = employeeService.selectHrEmployeeList(employee);
        ExcelUtil<HrEmployee> util = new ExcelUtil<HrEmployee>(HrEmployee.class);
        util.exportExcel(response, list, "员工档案数据");
    }

    /**
     * 档案详情
     */
    @PreAuthorize("@ss.hasPermi('hr:employee:query')")
    @GetMapping("/{employeeId}")
    public AjaxResult getInfo(@PathVariable Long employeeId)
    {
        return success(employeeService.selectHrEmployeeById(employeeId));
    }

    /**
     * 劳动合同到期提醒
     */
    @PreAuthorize("@ss.hasPermi('hr:employee:list')")
    @GetMapping("/contractExpire")
    public AjaxResult contractExpire()
    {
        return success(employeeService.selectContractExpireList(30));
    }

    /**
     * 入职办理（新建档案）
     */
    @PreAuthorize("@ss.hasPermi('hr:employee:add')")
    @Log(title = "员工入职", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HrEmployee employee)
    {
        return toAjax(employeeService.insertHrEmployee(employee));
    }

    /**
     * 修改档案
     */
    @PreAuthorize("@ss.hasPermi('hr:employee:edit')")
    @Log(title = "员工档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HrEmployee employee)
    {
        return toAjax(employeeService.updateHrEmployee(employee));
    }

    /**
     * 删除档案（仅已离职）
     */
    @PreAuthorize("@ss.hasPermi('hr:employee:remove')")
    @Log(title = "员工档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{employeeIds}")
    public AjaxResult remove(@PathVariable Long[] employeeIds)
    {
        return toAjax(employeeService.deleteHrEmployeeByIds(employeeIds));
    }

    /**
     * 离职登记（联动停号）
     */
    @PreAuthorize("@ss.hasPermi('hr:employee:leave')")
    @Log(title = "员工离职登记", businessType = BusinessType.UPDATE)
    @PostMapping("/leave")
    public AjaxResult leave(@RequestBody HrEmployee body)
    {
        employeeService.leaveEmployee(body.getEmployeeId(), body.getLeaveDate(), body.getLeaveReason());
        return success();
    }
}
