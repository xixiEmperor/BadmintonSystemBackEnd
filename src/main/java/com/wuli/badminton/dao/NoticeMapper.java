package com.wuli.badminton.dao;

import com.wuli.badminton.pojo.Notice;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 公告通知Mapper接口
 */
public interface NoticeMapper {
    
    /**
     * 插入通知
     * @param notice 通知对象
     * @return 影响行数
     */
    int insert(Notice notice);
    
    /**
     * 根据ID查询通知
     * @param id 通知ID
     * @return 通知对象
     */
    Notice selectById(Integer id);
    
    /**
     * 查询所有已发布的通知（按发布时间倒序）
     * @return 通知列表
     */
    List<Notice> selectPublishedNotices();
    
    /**
     * 查询所有通知（管理员用，包含草稿）
     * @return 通知列表
     */
    List<Notice> selectAllNotices();
    
    /**
     * 根据类型查询已发布的通知
     * @param type 通知类型
     * @return 通知列表
     */
    List<Notice> selectPublishedNoticesByType(@Param("type") Integer type);
    
    /**
     * 更新通知
     * @param notice 通知对象
     * @return 影响行数
     */
    int updateById(Notice notice);
    
    /**
     * 发布通知（更新状态和发布时间）
     * @param id 通知ID
     * @param publishTime 发布时间
     * @return 影响行数
     */
    int publishNotice(@Param("id") Integer id, @Param("publishTime") Date publishTime);
    
    /**
     * 删除通知
     * @param id 通知ID
     * @return 影响行数
     */
    int deleteById(Integer id);
} 