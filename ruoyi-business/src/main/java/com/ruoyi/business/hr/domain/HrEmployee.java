package com.ruoyi.business.hr.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 员工档案对象 hr_employee
 *
 * @author renovationops
 */
public class HrEmployee extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 档案ID */
    private Long employeeId;

    /** 关联系统账号user_id（可空） */
    private Long userId;

    /** 员工编号 */
    private String employeeNo;

    /** 姓名 */
    private String name;

    /** 性别（0男 1女 2未知） */
    private String gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /** 身份证号（敏感，脱敏展示） */
    private String idCard;

    /** 联系电话 */
    private String phone;

    /** 部门 */
    private Long deptId;

    /** 岗位 */
    private Long postId;

    /** 学历（字典 hr_education） */
    private String education;

    /** 入职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date entryDate;

    /** 试用到期日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date probationEndDate;

    /** 转正日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date formalDate;

    /** 劳动合同到期日（到期提醒） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractExpireDate;

    /** 基本工资（敏感，脱敏展示） */
    private BigDecimal baseSalary;

    /** 状态（1在职 2试用期 3已离职） */
    private String status;

    /** 离职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaveDate;

    /** 离职原因 */
    private String leaveReason;

    /** 部门名称（展示） */
    private String deptName;

    /** 账号用户名（展示） */
    private String userName;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public Date getEntryDate() { return entryDate; }
    public void setEntryDate(Date entryDate) { this.entryDate = entryDate; }
    public Date getProbationEndDate() { return probationEndDate; }
    public void setProbationEndDate(Date probationEndDate) { this.probationEndDate = probationEndDate; }
    public Date getFormalDate() { return formalDate; }
    public void setFormalDate(Date formalDate) { this.formalDate = formalDate; }
    public Date getContractExpireDate() { return contractExpireDate; }
    public void setContractExpireDate(Date contractExpireDate) { this.contractExpireDate = contractExpireDate; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getLeaveDate() { return leaveDate; }
    public void setLeaveDate(Date leaveDate) { this.leaveDate = leaveDate; }
    public String getLeaveReason() { return leaveReason; }
    public void setLeaveReason(String leaveReason) { this.leaveReason = leaveReason; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
