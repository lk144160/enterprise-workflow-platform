package com.ruoyi.business.quote.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.quote.domain.BizQuote;

/**
 * 报价单 Mapper 接口
 *
 * @author renovationops
 */
public interface BizQuoteMapper
{
    public BizQuote selectBizQuoteById(Long quoteId);

    public List<BizQuote> selectBizQuoteList(BizQuote bizQuote);

    /** 查询客户可选的已通过报价（合同关联合同用） */
    public List<BizQuote> selectApprovedQuotesByCustomer(Long customerId);

    public int insertBizQuote(BizQuote bizQuote);

    public int updateBizQuote(BizQuote bizQuote);

    public int deleteBizQuoteById(Long quoteId);

    public int deleteBizQuoteByIds(Long[] quoteIds);
}
