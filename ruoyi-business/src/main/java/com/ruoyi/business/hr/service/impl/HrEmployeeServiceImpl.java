package com.ruoyi.business.hr.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.common.BusinessNoService;
import com.ruoyi.business.hr.domain.HrEmployee;
import com.ruoyi.business.hr.mapper.HrEmployeeMapper;
import com.ruoyi.business.hr.service.IHrEmployeeService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;

/**
 * 员工档案服务实现（离职联动停号）
 *
 * @author renovationops
 */
@Service
public class HrEmployeeServiceImpl implements IHrEmployeeService
{
    /** 员工状态 */
    public static final String STATUS_ON_JOB = "1";
    public static final String STATUS_PROBATION = "2";
    public static final String STATUS_LEFT = "3";

    @Autowired
    private HrEmployeeMapper employeeMapper;

    @Autowired
    private BusinessNoService businessNoService;

    @Autowired
    private ISysUserService userService;

    @Override
    public HrEmployee selectHrEmployeeById(Long employeeId)
    {
        return employeeMapper.selectHrEmployeeById(employeeId);
    }

    @Override
    public List<HrEmployee> selectHrEmployeeList(HrEmployee employee)
    {
        List<HrEmployee> list = employeeMapper.selectHrEmployeeList(employee);
        // 敏感字段脱敏：身份证号保留前6后4
        for (HrEmployee item : list)
        {
            item.setIdCard(maskIdCard(item.getIdCard()));
        }
        return list;
    }

    @Override
    @Transactional
    public int insertHrEmployee(HrEmployee employee)
    {
        if (StringUtils.isEmpty(employee.getName()))
        {
            throw new ServiceException("员工姓名不能为空");
        }
        employee.setEmployeeNo(businessNoService.nextNo("YG"));
        // 有试用到期日默认试用期，否则直接在职
        if (StringUtils.isEmpty(employee.getStatus()))
        {
            employee.setStatus(employee.getProbationEndDate() != null ? STATUS_PROBATION : STATUS_ON_JOB);
        }
        if (employee.getEntryDate() == null)
        {
            employee.setEntryDate(new Date());
        }
        employee.setCreateBy(SecurityUtils.getUsername());
        return employeeMapper.insertHrEmployee(employee);
    }

    @Override
    @Transactional
    public int updateHrEmployee(HrEmployee employee)
    {
        HrEmployee exist = mustGet(employee.getEmployeeId());
        if (STATUS_LEFT.equals(exist.getStatus()))
        {
            throw new ServiceException("已离职员工档案不可修改");
        }
        employee.setUpdateBy(SecurityUtils.getUsername());
        return employeeMapper.updateHrEmployee(employee);
    }

    @Override
    @Transactional
    public int deleteHrEmployeeByIds(Long[] employeeIds)
    {
        for (Long employeeId : employeeIds)
        {
            HrEmployee exist = mustGet(employeeId);
            if (!STATUS_LEFT.equals(exist.getStatus()))
            {
                throw new ServiceException("员工【" + exist.getName() + "】未办理离职，档案不可删除");
            }
        }
        return employeeMapper.deleteHrEmployeeByIds(employeeIds);
    }

    @Override
    @Transactional
    public void leaveEmployee(Long employeeId, Date leaveDate, String leaveReason)
    {
        HrEmployee exist = mustGet(employeeId);
        if (STATUS_LEFT.equals(exist.getStatus()))
        {
            throw new ServiceException("该员工已离职");
        }
        if (StringUtils.isEmpty(leaveReason))
        {
            throw new ServiceException("离职原因必填");
        }
        HrEmployee update = new HrEmployee();
        update.setEmployeeId(employeeId);
        update.setStatus(STATUS_LEFT);
        update.setLeaveDate(leaveDate == null ? new Date() : leaveDate);
        update.setLeaveReason(leaveReason);
        update.setUpdateBy(SecurityUtils.getUsername());
        employeeMapper.updateHrEmployee(update);

        // 联动停用系统账号
        if (exist.getUserId() != null)
        {
            SysUser user = userService.selectUserById(exist.getUserId());
            if (user != null && "0".equals(user.getStatus()))
            {
                SysUser statusUpdate = new SysUser();
                statusUpdate.setUserId(user.getUserId());
                statusUpdate.setStatus("1");
                userService.updateUserStatus(statusUpdate);
            }
        }
    }

    @Override
    public List<HrEmployee> selectContractExpireList(int days)
    {
        return employeeMapper.selectContractExpireList(days);
    }

    private HrEmployee mustGet(Long employeeId)
    {
        HrEmployee employee = employeeMapper.selectHrEmployeeById(employeeId);
        if (employee == null)
        {
            throw new ServiceException("员工档案不存在");
        }
        return employee;
    }

    /** 身份证号脱敏：保留前6后4 */
    private String maskIdCard(String idCard)
    {
        if (StringUtils.isEmpty(idCard) || idCard.length() < 11)
        {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }
}
