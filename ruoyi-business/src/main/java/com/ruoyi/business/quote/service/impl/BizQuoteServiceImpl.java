package com.ruoyi.business.quote.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.common.BusinessNoService;
import com.ruoyi.business.crm.service.ICrmCustomerService;
import com.ruoyi.business.flow.service.FlowCallback;
import com.ruoyi.business.flow.service.FlowConstants;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.business.quote.domain.BizQuote;
import com.ruoyi.business.quote.mapper.BizQuoteMapper;
import com.ruoyi.business.quote.service.IBizQuoteService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 报价单服务实现（含审批流回调）
 *
 * @author renovationops
 */
@Service
public class BizQuoteServiceImpl implements IBizQuoteService, FlowCallback
{
    /** 报价状态 */
    public static final String STATUS_DRAFT = "0";
    public static final String STATUS_AUDITING = "1";
    public static final String STATUS_APPROVED = "2";
    public static final String STATUS_REJECTED = "3";
    public static final String STATUS_INVALID = "4";

    @Autowired
    private BizQuoteMapper quoteMapper;

    @Autowired
    private BusinessNoService businessNoService;

    @Autowired
    private IFlowEngineService flowEngineService;

    @Autowired
    private ICrmCustomerService customerService;

    @Override
    public BizQuote selectBizQuoteById(Long quoteId)
    {
        return quoteMapper.selectBizQuoteById(quoteId);
    }

    @Override
    public List<BizQuote> selectBizQuoteList(BizQuote bizQuote)
    {
        return quoteMapper.selectBizQuoteList(bizQuote);
    }

    @Override
    @Transactional
    public int insertBizQuote(BizQuote quote)
    {
        quote.setQuoteNo(businessNoService.nextNo("BJ"));
        calcAmount(quote);
        quote.setStatus(STATUS_DRAFT);
        quote.setVersion(1);
        quote.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        quote.setCreateBy(SecurityUtils.getUsername());
        return quoteMapper.insertBizQuote(quote);
    }

    @Override
    @Transactional
    public int updateBizQuote(BizQuote quote)
    {
        BizQuote exist = quoteMapper.selectBizQuoteById(quote.getQuoteId());
        if (exist == null)
        {
            throw new ServiceException("报价单不存在");
        }
        if (!STATUS_DRAFT.equals(exist.getStatus()) && !STATUS_REJECTED.equals(exist.getStatus()))
        {
            throw new ServiceException("仅草稿或已驳回状态的报价单可修改");
        }
        calcAmount(quote);
        quote.setVersion((exist.getVersion() == null ? 1 : exist.getVersion()) + 1);
        quote.setUpdateBy(SecurityUtils.getUsername());
        return quoteMapper.updateBizQuote(quote);
    }

    @Override
    @Transactional
    public int deleteBizQuoteByIds(Long[] quoteIds)
    {
        for (Long quoteId : quoteIds)
        {
            BizQuote exist = quoteMapper.selectBizQuoteById(quoteId);
            if (exist == null)
            {
                continue;
            }
            if (!STATUS_DRAFT.equals(exist.getStatus()) && !STATUS_REJECTED.equals(exist.getStatus()))
            {
                throw new ServiceException("报价单【" + exist.getQuoteNo() + "】非草稿/驳回状态，不允许删除");
            }
        }
        return quoteMapper.deleteBizQuoteByIds(quoteIds);
    }

    @Override
    @Transactional
    public void submitQuote(Long quoteId)
    {
        BizQuote quote = mustGet(quoteId);
        if (!STATUS_DRAFT.equals(quote.getStatus()) && !STATUS_REJECTED.equals(quote.getStatus()))
        {
            throw new ServiceException("仅草稿或已驳回状态的报价单可提交审批");
        }
        if (quote.getTotalAmount() == null || quote.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("报价金额必须大于0，无法提交");
        }
        BizQuote update = new BizQuote();
        update.setQuoteId(quoteId);
        update.setStatus(STATUS_AUDITING);
        update.setSubmitTime(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        quoteMapper.updateBizQuote(update);
        flowEngineService.startFlow(FlowConstants.QUOTE_APPROVAL, FlowConstants.BIZ_TYPE_QUOTE, quoteId,
                "报价单 " + quote.getQuoteNo() + " " + quote.getProjectName(), quote.getFinalAmount());
    }

    @Override
    @Transactional
    public void cancelQuote(Long quoteId)
    {
        BizQuote quote = mustGet(quoteId);
        if (!STATUS_AUDITING.equals(quote.getStatus()))
        {
            throw new ServiceException("仅审批中的报价单可撤销");
        }
        // 引擎撤销后会回调 onCanceled 将状态置回草稿
        com.ruoyi.business.flow.domain.FlowInstance instance =
                flowEngineService.selectActiveInstance(FlowConstants.BIZ_TYPE_QUOTE, quoteId);
        if (instance == null)
        {
            throw new ServiceException("未找到进行中的审批实例");
        }
        flowEngineService.cancelInstance(instance.getInstanceId());
    }

    @Override
    @Transactional
    public void invalidateQuote(Long quoteId)
    {
        BizQuote quote = mustGet(quoteId);
        if (STATUS_AUDITING.equals(quote.getStatus()))
        {
            throw new ServiceException("审批中的报价单请先撤销再作废");
        }
        BizQuote update = new BizQuote();
        update.setQuoteId(quoteId);
        update.setStatus(STATUS_INVALID);
        update.setUpdateBy(SecurityUtils.getUsername());
        quoteMapper.updateBizQuote(update);
    }

    // ==================== 审批流回调 ====================

    @Override
    public String bizType()
    {
        return FlowConstants.BIZ_TYPE_QUOTE;
    }

    @Override
    @Transactional
    public void onApproved(Long bizId)
    {
        BizQuote update = new BizQuote();
        update.setQuoteId(bizId);
        update.setStatus(STATUS_APPROVED);
        update.setApproveTime(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        quoteMapper.updateBizQuote(update);
        // 报价通过即视为线索转化
        BizQuote quote = quoteMapper.selectBizQuoteById(bizId);
        if (quote != null)
        {
            customerService.updateStatus(quote.getCustomerId(), "3");
        }
    }

    @Override
    @Transactional
    public void onRejected(Long bizId)
    {
        BizQuote update = new BizQuote();
        update.setQuoteId(bizId);
        update.setStatus(STATUS_REJECTED);
        update.setApproveTime(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        quoteMapper.updateBizQuote(update);
    }

    @Override
    @Transactional
    public void onCanceled(Long bizId)
    {
        BizQuote update = new BizQuote();
        update.setQuoteId(bizId);
        update.setStatus(STATUS_DRAFT);
        update.setSubmitTime(null);
        update.setUpdateBy(SecurityUtils.getUsername());
        quoteMapper.updateBizQuote(update);
    }

    // ==================== 私有方法 ====================

    private BizQuote mustGet(Long quoteId)
    {
        BizQuote quote = quoteMapper.selectBizQuoteById(quoteId);
        if (quote == null)
        {
            throw new ServiceException("报价单不存在");
        }
        return quote;
    }

    /**
     * 金额服务端计算：最终报价 = 报价金额 - 优惠金额
     */
    private void calcAmount(BizQuote quote)
    {
        BigDecimal total = quote.getTotalAmount() == null ? BigDecimal.ZERO : quote.getTotalAmount();
        if (total.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("报价金额不能为负数");
        }
        BigDecimal discount = quote.getDiscountAmount() == null ? BigDecimal.ZERO : quote.getDiscountAmount();
        if (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(total) > 0)
        {
            throw new ServiceException("优惠金额必须在 0 ~ 报价金额之间");
        }
        quote.setTotalAmount(total);
        quote.setDiscountAmount(discount);
        quote.setFinalAmount(total.subtract(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}
