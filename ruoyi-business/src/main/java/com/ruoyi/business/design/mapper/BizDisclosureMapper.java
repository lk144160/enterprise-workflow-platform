package com.ruoyi.business.design.mapper;

import java.util.List;
import com.ruoyi.business.design.domain.BizDisclosure;

/**
 * 技术交底 Mapper 接口
 *
 * @author renovationops
 */
public interface BizDisclosureMapper
{
    public BizDisclosure selectBizDisclosureById(Long disclosureId);

    public List<BizDisclosure> selectBizDisclosureList(BizDisclosure disclosure);

    public int insertBizDisclosure(BizDisclosure disclosure);

    public int updateBizDisclosure(BizDisclosure disclosure);

    public int deleteBizDisclosureByIds(Long[] disclosureIds);
}
