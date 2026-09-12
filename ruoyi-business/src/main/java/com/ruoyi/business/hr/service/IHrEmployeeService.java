package com.ruoyi.business.hr.service;

import java.util.List;
import com.ruoyi.business.hr.domain.HrEmployee;

/**
 * 员工档案服务接口
 *
 * @author renovationops
 */
public interface IHrEmployeeService
{
    public HrEmployee selectHrEmployeeById(Long employeeId);

    /** 列表查询（身份证号脱敏） */
    public List<HrEmployee> selectHrEmployeeList(HrEmployee employee);

    /** 入职办理（新建档案） */
    public int insertHrEmployee(HrEmployee employee);

    public int updateHrEmployee(HrEmployee employee);

    public int deleteHrEmployeeByIds(Long[] employeeIds);

    /** 离职登记（联动停用系统账号） */
    public void leaveEmployee(Long employeeId, java.util.Date leaveDate, String leaveReason);

    /** 劳动合同到期提醒列表 */
    public List<HrEmployee> selectContractExpireList(int days);
}
