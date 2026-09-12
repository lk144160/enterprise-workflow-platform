package com.ruoyi.business.design.service;

import java.util.List;
import com.ruoyi.business.design.domain.BizDisclosure;

/**
 * 技术交底服务接口
 *
 * @author renovationops
 */
public interface IBizDisclosureService
{
    public BizDisclosure selectBizDisclosureById(Long disclosureId);

    public List<BizDisclosure> selectBizDisclosureList(BizDisclosure disclosure);

    /** 新增交底单（基于已完成设计任务） */
    public int insertBizDisclosure(BizDisclosure disclosure);

    /** 修改（草稿/已驳回可改） */
    public int updateBizDisclosure(BizDisclosure disclosure);

    public int deleteBizDisclosureByIds(Long[] disclosureIds);

    /** 提交审批 */
    public void submitDisclosure(Long disclosureId);
}
