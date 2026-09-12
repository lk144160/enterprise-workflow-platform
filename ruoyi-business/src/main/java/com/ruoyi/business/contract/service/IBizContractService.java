package com.ruoyi.business.contract.service;

import java.util.List;
import com.ruoyi.business.contract.domain.BizContract;

/**
 * 合同服务接口
 *
 * @author renovationops
 */
public interface IBizContractService
{
    public BizContract selectBizContractById(Long contractId);

    public List<BizContract> selectBizContractList(BizContract bizContract);

    /** 新增合同（自动生成四期收款计划） */
    public int insertBizContract(BizContract bizContract);

    /** 修改合同（仅待审批可改） */
    public int updateBizContract(BizContract bizContract);

    public int deleteBizContractByIds(Long[] contractIds);

    /** 提交审批 */
    public void submitContract(Long contractId);

    /** 完工登记 */
    public void finishContract(Long contractId);

    /** 归档（需全部期次结清/减免/作废） */
    public void archiveContract(Long contractId);

    /** 终止（未结清期次作废） */
    public void terminateContract(Long contractId, String reason);
}
