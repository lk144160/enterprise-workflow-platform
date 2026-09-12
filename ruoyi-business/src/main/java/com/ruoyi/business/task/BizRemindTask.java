package com.ruoyi.business.task;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.business.contract.domain.BizPaymentPlan;
import com.ruoyi.business.contract.mapper.BizPaymentPlanMapper;
import com.ruoyi.business.crm.domain.CrmCustomer;
import com.ruoyi.business.crm.mapper.CrmCustomerMapper;
import com.ruoyi.business.flow.mapper.FlowTaskMapper;
import com.ruoyi.business.hr.domain.HrEmployee;
import com.ruoyi.business.hr.mapper.HrEmployeeMapper;
import com.ruoyi.business.message.service.ISysMessageService;
import com.ruoyi.business.remind.service.IBizRemindService;
import com.ruoyi.business.stock.domain.BizInventory;
import com.ruoyi.business.stock.mapper.BizInventoryMapper;
import com.ruoyi.common.utils.DateUtils;

/**
 * 业务提醒定时任务（sys_job 调用：bizRemindTask.方法名()）
 *
 * @author renovationops
 */
@Component("bizRemindTask")
public class BizRemindTask
{
    private static final Logger log = LoggerFactory.getLogger(BizRemindTask.class);

    /** 消息类型 */
    private static final String MSG_WARN = "warn";

    /** 角色ID（与 business_seed.sql 初始化一致） */
    private static final Long ROLE_MARKET_MANAGER = 11L;
    private static final Long ROLE_ADMIN_FINANCE = 14L;

    /** 线索超期未跟进天数 */
    private static final int FOLLOW_OVERDUE_DAYS = 7;

    /** 劳动合同提前提醒天数 */
    private static final int CONTRACT_EXPIRE_DAYS = 30;

    @Autowired
    private CrmCustomerMapper customerMapper;

    @Autowired
    private BizPaymentPlanMapper paymentPlanMapper;

    @Autowired
    private HrEmployeeMapper employeeMapper;

    @Autowired
    private BizInventoryMapper inventoryMapper;

    @Autowired
    private FlowTaskMapper flowTaskMapper;

    @Autowired
    private ISysMessageService messageService;

    @Autowired
    private IBizRemindService remindService;

    /**
     * 线索超期跟进扫描（每日 09:00）
     * 待跟进/跟进中且最近跟进（无则取创建时间）超过7天的线索，
     * 提醒客户归属部门的市场部经理（业务员为文本录入，无账号绑定）
     */
    public void scanOverdueFollow()
    {
        List<CrmCustomer> list = customerMapper.selectOverdueFollowList(FOLLOW_OVERDUE_DAYS);
        for (CrmCustomer customer : list)
        {
            String lastTime = customer.getLatestFollowTime() == null
                    ? DateUtils.parseDateToStr("yyyy-MM-dd", customer.getCreateTime())
                    : DateUtils.parseDateToStr("yyyy-MM-dd", customer.getLatestFollowTime());
            String title = "线索超期未跟进提醒";
            String owner = customer.getOwnerName() == null ? "" : "业务员【" + customer.getOwnerName() + "】的";
            // 提醒客户归属部门的市场部经理
            List<Long> managers = flowTaskMapper.selectUserIdsByRoleAndDept(ROLE_MARKET_MANAGER, customer.getDeptId());
            for (Long managerId : managers)
            {
                messageService.send(managerId, MSG_WARN, title,
                        owner + "线索【" + customer.getCustomerName() + "】已超过" + FOLLOW_OVERDUE_DAYS
                                + "天未跟进（最近跟进：" + lastTime + "），请及时跟进或更新状态。",
                        "/market/customer/profile", null);
            }
        }
        log.info("线索超期跟进扫描完成，提醒线索数：{}", list.size());
    }

    /**
     * 收款到期与逾期扫描（每日 09:00）
     * 待收款且计划收款日已到的期次，提醒合同负责人与行政财务
     */
    public void scanPaymentDue()
    {
        List<BizPaymentPlan> plans = paymentPlanMapper.selectDueRemindList();
        List<Long> financeUsers = flowTaskMapper.selectUserIdsByRole(ROLE_ADMIN_FINANCE);
        for (BizPaymentPlan plan : plans)
        {
            String title = "收款到期提醒";
            String content = "合同【" + plan.getContractNo() + "】客户【" + plan.getCustomerName() + "】第"
                    + plan.getPeriodNo() + "期（" + plan.getPeriodName() + "）计划收款日 "
                    + DateUtils.parseDateToStr("yyyy-MM-dd", plan.getPlanDate())
                    + "，应收 " + plan.getPlanAmount() + " 元，尚未收齐，请跟进收款。";
            messageService.send(plan.getOwnerId(), MSG_WARN, title, content, "/payment", null);
            for (Long financeId : financeUsers)
            {
                if (!financeId.equals(plan.getOwnerId()))
                {
                    messageService.send(financeId, MSG_WARN, title, content, "/payment", null);
                }
            }
        }
        log.info("收款到期与逾期扫描完成，提醒期次数：{}", plans.size());
    }

    /**
     * 劳动合同到期提醒（每日 09:00）
     * 30天内到期的在职员工合同，提醒行政财务
     */
    public void scanContractExpire()
    {
        List<HrEmployee> employees = employeeMapper.selectContractExpireList(CONTRACT_EXPIRE_DAYS);
        if (employees.isEmpty())
        {
            return;
        }
        List<Long> financeUsers = flowTaskMapper.selectUserIdsByRole(ROLE_ADMIN_FINANCE);
        for (HrEmployee employee : employees)
        {
            String title = "劳动合同到期提醒";
            String content = "员工【" + employee.getName() + "】的劳动合同将于 "
                    + DateUtils.parseDateToStr("yyyy-MM-dd", employee.getContractExpireDate())
                    + " 到期，请及时办理续签或终止手续。";
            messageService.sendBatch(financeUsers, MSG_WARN, title, content, "/finance/employee", null);
        }
        log.info("劳动合同到期提醒完成，提醒人数：{}", employees.size());
    }

    /**
     * 库存预警扫描（每小时）
     * 库存低于安全库存的物资，提醒行政财务
     */
    public void scanLowStock()
    {
        List<BizInventory> list = inventoryMapper.selectBelowSafetyList();
        if (list.isEmpty())
        {
            return;
        }
        List<Long> financeUsers = flowTaskMapper.selectUserIdsByRole(ROLE_ADMIN_FINANCE);
        StringBuilder names = new StringBuilder();
        for (BizInventory inventory : list)
        {
            if (names.length() > 0)
            {
                names.append("、");
            }
            names.append(inventory.getMaterialName()).append("（库存 ").append(inventory.getQuantity())
                    .append(inventory.getUnit()).append(" / 安全库存 ").append(inventory.getSafetyStock())
                    .append(inventory.getUnit()).append("）");
        }
        messageService.sendBatch(financeUsers, MSG_WARN, "库存预警",
                "以下物资库存已低于安全库存，请及时采购补库：" + names, "/finance/material", null);
        log.info("库存预警扫描完成，预警物资数：{}", list.size());
    }

    /**
     * 自定义提醒扫描（每5分钟）
     * 扫描到点的自定义提醒，经通知渠道分发（站内信，后期可扩展短信等渠道）
     */
    public void scanCustomRemind()
    {
        remindService.scanAndNotifyDueReminds();
    }
}
