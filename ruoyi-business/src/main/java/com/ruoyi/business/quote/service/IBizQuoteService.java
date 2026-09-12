package com.ruoyi.business.quote.service;

import java.util.List;
import com.ruoyi.business.quote.domain.BizQuote;

/**
 * 报价单服务接口
 *
 * @author renovationops
 */
public interface IBizQuoteService
{
    public BizQuote selectBizQuoteById(Long quoteId);

    public List<BizQuote> selectBizQuoteList(BizQuote bizQuote);

    /** 新增报价单（主子表，金额服务端计算） */
    public int insertBizQuote(BizQuote bizQuote);

    /** 修改报价单（仅草稿/已驳回可改，版本号+1） */
    public int updateBizQuote(BizQuote bizQuote);

    public int deleteBizQuoteByIds(Long[] quoteIds);

    /** 提交审批 */
    public void submitQuote(Long quoteId);

    /** 撤销审批（回到草稿） */
    public void cancelQuote(Long quoteId);

    /** 作废 */
    public void invalidateQuote(Long quoteId);
}
