package com.ruoyi.business.site.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.attachment.domain.BizAttachment;
import com.ruoyi.business.site.domain.BizProgressStage;

/**
 * 施工阶段Mapper接口
 *
 * @author renovationops
 */
public interface BizProgressStageMapper
{
    public List<BizProgressStage> selectStagesBySiteId(Long siteId);

    public BizProgressStage selectBizProgressStageById(Long stageId);

    public int insertBizProgressStage(BizProgressStage stage);

    public int updateBizProgressStage(BizProgressStage stage);

    public int deleteStagesBySiteIds(@Param("siteIds") Long[] siteIds);

    /** 删除单个施工步骤 */
    public int deleteBizProgressStageById(Long stageId);

    /** 插入步骤前，将指定顺序号及之后的步骤整体后移一位 */
    public int bumpSortOrderFrom(@Param("siteId") Long siteId, @Param("fromOrder") Integer fromOrder);

    /** 删除单个步骤的现场照片附件 */
    public int deleteStageAttachmentByStageId(Long stageId);

    /** 查询阶段现场照片（biz_type=site_stage） */
    public List<BizAttachment> selectStageAttachments(@Param("stageIds") List<Long> stageIds);

    /** 删除阶段关联附件 */
    public int deleteStageAttachments(@Param("siteIds") Long[] siteIds);
}
