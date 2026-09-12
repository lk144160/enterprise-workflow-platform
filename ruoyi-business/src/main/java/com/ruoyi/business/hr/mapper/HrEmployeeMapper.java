package com.ruoyi.business.hr.mapper;

import java.util.List;
import com.ruoyi.business.hr.domain.HrEmployee;

/**
 * 员工档案 Mapper 接口
 *
 * @author renovationops
 */
public interface HrEmployeeMapper
{
    public HrEmployee selectHrEmployeeById(Long employeeId);

    public HrEmployee selectHrEmployeeByNo(String employeeNo);

    public List<HrEmployee> selectHrEmployeeList(HrEmployee employee);

    /** 劳动合同即将到期员工（30天内，提醒用） */
    public List<HrEmployee> selectContractExpireList(int days);

    public int insertHrEmployee(HrEmployee employee);

    public int updateHrEmployee(HrEmployee employee);

    public int deleteHrEmployeeByIds(Long[] employeeIds);
}
