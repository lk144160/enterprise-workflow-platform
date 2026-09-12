package com.ruoyi.business.flow.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.attachment.domain.BizAttachment;
import com.ruoyi.business.attachment.service.IBizAttachmentService;
import com.ruoyi.business.contract.mapper.BizContractMapper;
import com.ruoyi.business.contract.mapper.BizPaymentPlanMapper;
import com.ruoyi.business.design.mapper.BizDisclosureMapper;
import com.ruoyi.business.deposit.mapper.BizDepositMapper;
import com.ruoyi.business.expense.mapper.BizExpenseMapper;
import com.ruoyi.business.flow.domain.FlowDefinition;
import com.ruoyi.business.flow.domain.FlowInstance;
import com.ruoyi.business.flow.domain.FlowNode;
import com.ruoyi.business.flow.domain.FlowTask;
import com.ruoyi.business.flow.mapper.FlowDefinitionMapper;
import com.ruoyi.business.flow.mapper.FlowInstanceMapper;
import com.ruoyi.business.flow.mapper.FlowNodeMapper;
import com.ruoyi.business.flow.mapper.FlowTaskMapper;
import com.ruoyi.business.flow.service.FlowCallback;
import com.ruoyi.business.flow.service.FlowConstants;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.business.message.service.ISysMessageService;
import com.ruoyi.business.quote.mapper.BizQuoteMapper;
import com.ruoyi.business.stock.domain.BizStockOrder;
import com.ruoyi.business.stock.mapper.BizStockOrderItemMapper;
import com.ruoyi.business.stock.mapper.BizStockOrderMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 审批流引擎实现（表驱动状态机）
 *
 * @author renovationops
 */
@Service
public class FlowEngineServiceImpl implements IFlowEngineService
{
    @Autowired
    private FlowDefinitionMapper definitionMapper;

    @Autowired
    private FlowNodeMapper nodeMapper;

    @Autowired
    private FlowInstanceMapper instanceMapper;

    @Autowired
    private FlowTaskMapper taskMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysMessageService messageService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BizQuoteMapper quoteMapper;

    @Autowired
    private BizContractMapper contractMapper;

    @Autowired
    private BizPaymentPlanMapper paymentPlanMapper;

    @Autowired
    private BizDisclosureMapper disclosureMapper;

    @Autowired
    private BizExpenseMapper expenseMapper;

    @Autowired
    private BizStockOrderMapper stockOrderMapper;

    @Autowired
    private BizDepositMapper depositMapper;

    @Autowired
    private BizStockOrderItemMapper stockItemMapper;

    @Autowired
    private IBizAttachmentService attachmentService;

    @Override
    @Transactional
    public FlowInstance startFlow(String flowCode, String bizType, Long bizId, String bizTitle, BigDecimal amount)
    {
        FlowDefinition definition = definitionMapper.selectFlowDefinitionByCode(flowCode);
        if (definition == null || !"0".equals(definition.getStatus()))
        {
            throw new ServiceException("审批流程[" + flowCode + "]未配置或已停用");
        }
        FlowInstance exist = instanceMapper.selectActiveInstanceByBiz(bizType, bizId);
        if (exist != null)
        {
            throw new ServiceException("该单据已有进行中的审批，请勿重复提交");
        }
        LoginUserHolder holder = current();
        FlowInstance instance = new FlowInstance();
        instance.setFlowId(definition.getFlowId());
        instance.setFlowCode(flowCode);
        instance.setBizType(bizType);
        instance.setBizId(bizId);
        instance.setBizTitle(bizTitle);
        instance.setBizAmount(amount);
        instance.setStatus(FlowConstants.INSTANCE_RUNNING);
        instance.setStartUserId(holder.userId);
        instance.setCreateBy(holder.username);
        instanceMapper.insertFlowInstance(instance);

        List<FlowNode> nodes = nodeMapper.selectNodeListByFlowId(definition.getFlowId());
        FlowNode first = findNextActiveNode(nodes, 0, amount);
        if (first == null)
        {
            // 无有效节点：直接通过
            instance.setStatus(FlowConstants.INSTANCE_APPROVED);
            instance.setEndTime(new Date());
            instanceMapper.updateFlowInstance(instance);
            dispatchCallback(bizType, bizId, "approve");
            return instance;
        }
        moveToNode(instance, first);
        return instance;
    }

    @Override
    @Transactional
    public void handleTask(Long taskId, String action, String opinion)
    {
        FlowTask task = taskMapper.selectFlowTaskById(taskId);
        if (task == null || !FlowConstants.TASK_PENDING.equals(task.getStatus()))
        {
            throw new ServiceException("任务不存在或已处理");
        }
        Long userId = SecurityUtils.getUserId();
        if (!task.getApproverId().equals(userId))
        {
            throw new ServiceException("无权处理该审批任务");
        }
        FlowInstance instance = instanceMapper.selectFlowInstanceById(task.getInstanceId());
        if (instance == null || !FlowConstants.INSTANCE_RUNNING.equals(instance.getStatus()))
        {
            throw new ServiceException("审批实例已结束");
        }
        FlowNode node = nodeMapper.selectFlowNodeById(task.getNodeId());
        if (node == null)
        {
            throw new ServiceException("审批流程配置已调整，该任务关联的节点已不存在，请撤回重新发起审批");
        }
        if (FlowConstants.ACTION_AGREE.equals(action))
        {
            if (StringUtils.isEmpty(opinion))
            {
                opinion = "同意";
            }
            task.setStatus(FlowConstants.TASK_AGREE);
        }
        else if (FlowConstants.ACTION_REJECT.equals(action))
        {
            if (StringUtils.isEmpty(opinion))
            {
                throw new ServiceException("驳回意见必填");
            }
            task.setStatus(FlowConstants.TASK_REJECT);
        }
        else
        {
            throw new ServiceException("不支持的审批动作");
        }
        task.setOpinion(opinion);
        taskMapper.updateFlowTask(task);

        if (FlowConstants.TASK_REJECT.equals(task.getStatus()))
        {
            // 驳回：同节点其余任务失效，实例结束
            taskMapper.invalidPendingByNode(instance.getInstanceId(), node.getNodeId(), taskId);
            instance.setStatus(FlowConstants.INSTANCE_REJECTED);
            instance.setEndTime(new Date());
            instanceMapper.updateFlowInstance(instance);
            dispatchCallback(instance.getBizType(), instance.getBizId(), "reject");
            messageService.send(instance.getStartUserId(), "result", "审批驳回通知",
                    "您发起的【" + instance.getBizTitle() + "】已被驳回：" + opinion, routeOfBiz(instance), null);
            return;
        }

        // 同意：或签直接推进；会签需全部同意
        if ("1".equals(node.getMultiSign()))
        {
            taskMapper.invalidPendingByNode(instance.getInstanceId(), node.getNodeId(), taskId);
            advance(instance, node);
        }
        else
        {
            int pending = taskMapper.countPendingByNode(instance.getInstanceId(), node.getNodeId());
            if (pending == 0)
            {
                advance(instance, node);
            }
        }
    }

    @Override
    @Transactional
    public void transferTask(Long taskId, Long targetUserId, String opinion)
    {
        FlowTask task = taskMapper.selectFlowTaskById(taskId);
        if (task == null || !FlowConstants.TASK_PENDING.equals(task.getStatus()))
        {
            throw new ServiceException("任务不存在或已处理");
        }
        if (!task.getApproverId().equals(SecurityUtils.getUserId()))
        {
            throw new ServiceException("无权转交该审批任务");
        }
        SysUser target = userService.selectUserById(targetUserId);
        if (target == null)
        {
            throw new ServiceException("目标审批人不存在");
        }
        // 原任务标记转交，生成新任务
        task.setStatus(FlowConstants.TASK_TRANSFER);
        task.setOpinion("转交给 " + target.getNickName() + (StringUtils.isEmpty(opinion) ? "" : "：" + opinion));
        taskMapper.updateFlowTask(task);

        FlowTask newTask = new FlowTask();
        newTask.setInstanceId(task.getInstanceId());
        newTask.setNodeId(task.getNodeId());
        newTask.setNodeName(task.getNodeName());
        newTask.setApproverId(targetUserId);
        newTask.setStatus(FlowConstants.TASK_PENDING);
        newTask.setCreateBy(SecurityUtils.getUsername());
        taskMapper.insertFlowTask(newTask);

        FlowInstance instance = instanceMapper.selectFlowInstanceById(task.getInstanceId());
        messageService.send(targetUserId, "todo", "待办审批转交",
                SecurityUtils.getLoginUser().getUser().getNickName() + " 转交给您一条审批：【" + (instance != null ? instance.getBizTitle() : "") + "】",
                routeOfBiz(instance), null);
    }

    @Override
    @Transactional
    public void cancelInstance(Long instanceId)
    {
        FlowInstance instance = instanceMapper.selectFlowInstanceById(instanceId);
        if (instance == null || !FlowConstants.INSTANCE_RUNNING.equals(instance.getStatus()))
        {
            throw new ServiceException("审批实例不存在或已结束");
        }
        if (!instance.getStartUserId().equals(SecurityUtils.getUserId()))
        {
            throw new ServiceException("仅发起人可撤销审批");
        }
        List<FlowTask> tasks = taskMapper.selectTaskListByInstanceId(instanceId);
        boolean anyHandled = tasks.stream().anyMatch(t -> !FlowConstants.TASK_PENDING.equals(t.getStatus())
                && !FlowConstants.TASK_INVALID.equals(t.getStatus()));
        if (anyHandled)
        {
            throw new ServiceException("审批已处理，不可撤销");
        }
        for (FlowTask task : tasks)
        {
            if (FlowConstants.TASK_PENDING.equals(task.getStatus()))
            {
                taskMapper.invalidPendingByNode(instanceId, task.getNodeId(), null);
            }
        }
        instance.setStatus(FlowConstants.INSTANCE_CANCELED);
        instance.setEndTime(new Date());
        instanceMapper.updateFlowInstance(instance);
        dispatchCallback(instance.getBizType(), instance.getBizId(), "cancel");
    }

    @Override
    public List<FlowTask> selectTodoTaskList(FlowTask flowTask)
    {
        if (flowTask == null)
        {
            flowTask = new FlowTask();
        }
        flowTask.setApproverId(SecurityUtils.getUserId());
        return taskMapper.selectTodoTaskList(flowTask);
    }

    @Override
    public List<FlowInstance> selectMyInstanceList(FlowInstance flowInstance)
    {
        if (flowInstance == null)
        {
            flowInstance = new FlowInstance();
        }
        flowInstance.setStartUserId(SecurityUtils.getUserId());
        return instanceMapper.selectMyInstanceList(flowInstance);
    }

    @Override
    public FlowInstance getInstanceDetail(Long instanceId)
    {
        FlowInstance instance = instanceMapper.selectFlowInstanceById(instanceId);
        if (instance == null)
        {
            throw new ServiceException("审批实例不存在");
        }
        instance.setTasks(taskMapper.selectTaskListByInstanceId(instanceId));
        loadBizDetail(instance);
        return instance;
    }

    /**
     * 组装业务单据完整详情与图片附件（审批详情展示用）
     */
    private void loadBizDetail(FlowInstance instance)
    {
        Long bizId = instance.getBizId();
        if (bizId == null || StringUtils.isEmpty(instance.getBizType()))
        {
            return;
        }
        switch (instance.getBizType())
        {
            case FlowConstants.BIZ_TYPE_QUOTE:
                instance.setBizDetail(quoteMapper.selectBizQuoteById(bizId));
                break;
            case FlowConstants.BIZ_TYPE_CONTRACT:
                instance.setBizDetail(contractMapper.selectBizContractById(bizId));
                break;
            case FlowConstants.BIZ_TYPE_PAYMENT_REDUCE:
                instance.setBizDetail(paymentPlanMapper.selectBizPaymentPlanById(bizId));
                break;
            case FlowConstants.BIZ_TYPE_DISCLOSURE:
                instance.setBizDetail(disclosureMapper.selectBizDisclosureById(bizId));
                break;
            case FlowConstants.BIZ_TYPE_EXPENSE:
                instance.setBizDetail(expenseMapper.selectBizExpenseById(bizId));
                break;
            case FlowConstants.BIZ_TYPE_STOCK_OUT:
                BizStockOrder order = stockOrderMapper.selectBizStockOrderById(bizId);
                if (order != null)
                {
                    order.setItems(stockItemMapper.selectItemsByOrderId(bizId));
                }
                instance.setBizDetail(order);
                break;
            case FlowConstants.BIZ_TYPE_DEPOSIT_REFUND:
                instance.setBizDetail(depositMapper.selectBizDepositById(bizId));
                break;
            default:
                break;
        }
        // 图片/附件（如报销发票）
        BizAttachment query = new BizAttachment();
        query.setBizType(instance.getBizType());
        query.setBizId(bizId);
        instance.setAttachments(attachmentService.selectAttachmentList(query));
    }

    @Override
    public FlowInstance selectActiveInstance(String bizType, Long bizId)
    {
        return instanceMapper.selectActiveInstanceByBiz(bizType, bizId);
    }

    @Override
    public int countTodoTask()
    {
        FlowTask query = new FlowTask();
        query.setApproverId(SecurityUtils.getUserId());
        return taskMapper.selectTodoTaskList(query).size();
    }

    /**
     * 推进到下一有效节点；无下一节点则实例通过
     */
    private void advance(FlowInstance instance, FlowNode currentNode)
    {
        List<FlowNode> nodes = nodeMapper.selectNodeListByFlowId(instance.getFlowId());
        FlowNode next = findNextActiveNode(nodes, currentNode.getNodeOrder(), instance.getBizAmount());
        if (next == null)
        {
            instance.setStatus(FlowConstants.INSTANCE_APPROVED);
            instance.setCurrentNodeId(0L);
            instance.setEndTime(new Date());
            instanceMapper.updateFlowInstance(instance);
            dispatchCallback(instance.getBizType(), instance.getBizId(), "approve");
            messageService.send(instance.getStartUserId(), "result", "审批通过通知",
                    "您发起的【" + instance.getBizTitle() + "】已全部审批通过", routeOfBiz(instance), null);
        }
        else
        {
            moveToNode(instance, next);
        }
    }

    /**
     * 实例推进到指定节点：解析审批人并生成待办任务
     */
    private void moveToNode(FlowInstance instance, FlowNode node)
    {
        List<Long> approvers = resolveApprovers(node, instance.getStartUserId());
        if (StringUtils.isEmpty(approvers))
        {
            throw new ServiceException("审批节点[" + node.getNodeName() + "]未解析到可用审批人，请检查流程配置");
        }
        instance.setCurrentNodeId(node.getNodeId());
        instanceMapper.updateFlowInstance(instance);
        String username = SecurityUtils.getUsername();
        for (Long approverId : approvers)
        {
            FlowTask task = new FlowTask();
            task.setInstanceId(instance.getInstanceId());
            task.setNodeId(node.getNodeId());
            task.setNodeName(node.getNodeName());
            task.setApproverId(approverId);
            task.setStatus(FlowConstants.TASK_PENDING);
            task.setCreateBy(username);
            taskMapper.insertFlowTask(task);
            messageService.send(approverId, "todo", "待办审批提醒",
                    "您有一条待审批任务：【" + instance.getBizTitle() + "】（当前节点：" + node.getNodeName() + "）",
                    routeOfBiz(instance), null);
        }
    }

    /**
     * 条件路由：取 order 大于 afterOrder 且金额条件满足的第一个节点
     */
    private FlowNode findNextActiveNode(List<FlowNode> nodes, int afterOrder, BigDecimal amount)
    {
        for (FlowNode node : nodes)
        {
            if (node.getNodeOrder() != null && node.getNodeOrder() > afterOrder)
            {
                if (node.getConditionAmount() != null
                        && (amount == null || amount.compareTo(node.getConditionAmount()) < 0))
                {
                    continue;
                }
                return node;
            }
        }
        return null;
    }

    /**
     * 解析节点审批人
     * 1=指定用户 2=部门主管（取发起人部门 leader，回退主管角色） 3=指定角色（优先发起人部门内，回退全公司）
     */
    private List<Long> resolveApprovers(FlowNode node, Long startUserId)
    {
        List<Long> approvers = new ArrayList<>();
        SysUser startUser = userService.selectUserById(startUserId);
        String type = node.getApproverType();
        if ("1".equals(type))
        {
            for (String id : StringUtils.split(node.getApproverValue(), ","))
            {
                if (StringUtils.isNotEmpty(id))
                {
                    approvers.add(Long.parseLong(id.trim()));
                }
            }
        }
        else if ("2".equals(type))
        {
            if (startUser == null || startUser.getDeptId() == null)
            {
                throw new ServiceException("发起人未配置部门，无法解析部门主管");
            }
            SysDept dept = deptService.selectDeptById(startUser.getDeptId());
            if (dept != null && StringUtils.isNotEmpty(dept.getLeader()))
            {
                SysUser leader = userService.selectUserByUserName(dept.getLeader());
                if (leader != null && "0".equals(leader.getStatus()))
                {
                    approvers.add(leader.getUserId());
                }
            }
            if (StringUtils.isEmpty(approvers))
            {
                throw new ServiceException("部门【" + (dept != null ? dept.getDeptName() : "") + "】未配置负责人，无法解析部门主管");
            }
        }
        else if ("3".equals(type))
        {
            // 支持逗号分隔多角色（或签）：如 "13,1" = 设计部经理 + 超级管理员
            Set<Long> userIds = new LinkedHashSet<>();
            for (String rid : StringUtils.split(node.getApproverValue(), ","))
            {
                if (StringUtils.isEmpty(rid))
                {
                    continue;
                }
                Long roleId = Long.parseLong(rid.trim());
                List<Long> roleUsers;
                // 优先取发起人部门内该角色用户
                if (startUser != null && startUser.getDeptId() != null)
                {
                    roleUsers = taskMapper.selectUserIdsByRoleAndDept(roleId, startUser.getDeptId());
                }
                else
                {
                    roleUsers = new ArrayList<>();
                }
                // 回退：全公司该角色用户
                if (StringUtils.isEmpty(roleUsers))
                {
                    roleUsers = taskMapper.selectUserIdsByRole(roleId);
                }
                userIds.addAll(roleUsers);
            }
            approvers = new ArrayList<>(userIds);
        }
        return approvers;
    }

    /**
     * 按业务类型分发回调
     */
    private void dispatchCallback(String bizType, Long bizId, String result)
    {
        Map<String, FlowCallback> callbacks = applicationContext.getBeansOfType(FlowCallback.class);
        for (FlowCallback callback : callbacks.values())
        {
            if (bizType.equals(callback.bizType()))
            {
                switch (result)
                {
                    case "approve":
                        callback.onApproved(bizId);
                        break;
                    case "reject":
                        callback.onRejected(bizId);
                        break;
                    case "cancel":
                        callback.onCanceled(bizId);
                        break;
                }
            }
        }
    }

    private String routeOfBiz(FlowInstance instance)
    {
        if (instance == null || instance.getBizType() == null)
        {
            return null;
        }
        switch (instance.getBizType())
        {
            case "quote": return "/market/quote";
            case "contract": return "/market/contract";
            case "payment_reduce": return "/market/payment";
            case "disclosure": return "/design/disclosure";
            case "expense": return "/finance/expense";
            case "stock_out": return "/finance/stock";
            case "deposit_refund": return "/market/customer/deposit";
            default: return null;
        }
    }

    /** 当前登录人信息 */
    private static class LoginUserHolder
    {
        public Long userId;
        public String username;
    }

    private LoginUserHolder current()
    {
        LoginUserHolder holder = new LoginUserHolder();
        holder.userId = SecurityUtils.getUserId();
        holder.username = SecurityUtils.getUsername();
        return holder;
    }
}
