package com.ruoyi.business.site.mapper;

import java.util.List;
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.site.domain.BizSite;

/**
 * 施工工地Mapper接口
 *
 * @author renovationops
 */
public interface BizSiteMapper
{
    public BizSite selectBizSiteById(Long siteId);

    public List<BizSite> selectBizSiteList(BizSite site);

    public BizSite selectBizSiteByContractId(Long contractId);

    public int insertBizSite(BizSite site);

    public int updateBizSite(BizSite site);

    public int deleteBizSiteByIds(Long[] siteIds);

    /** 可开工的合同（已生效且未建工地） */
    public List<BizContract> selectAvailableContracts();
}
