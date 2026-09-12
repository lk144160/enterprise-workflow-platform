package com.ruoyi.business.message.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.message.domain.SysMessage;

/**
 * 站内消息 Mapper 接口
 *
 * @author renovationops
 */
public interface SysMessageMapper
{
    public SysMessage selectSysMessageById(Long messageId);

    public List<SysMessage> selectSysMessageList(SysMessage sysMessage);

    public int insertSysMessage(SysMessage sysMessage);

    public int markRead(@Param("messageId") Long messageId, @Param("receiverId") Long receiverId);

    public int markAllRead(@Param("receiverId") Long receiverId);

    public int countUnread(@Param("receiverId") Long receiverId);

    public int deleteSysMessageByIds(Long[] messageIds);
}
