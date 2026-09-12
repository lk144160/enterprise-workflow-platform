package com.ruoyi.business.contract.mapper;

import java.util.List;
import com.ruoyi.business.contract.domain.BizContract;

/**
 * 合同 Mapper 接口
 *
 * @author renovationops
 */
public interface BizContractMapper
{
    public BizContract selectBizContractById(Long contractId);

    public List<BizContract> selectBizContractList(BizContract bizContract);

    public int insertBizContract(BizContract bizContract);

    public int updateBizContract(BizContract bizContract);

    public int deleteBizContractById(Long contractId);

    public int deleteBizContractByIds(Long[] contractIds);
}
