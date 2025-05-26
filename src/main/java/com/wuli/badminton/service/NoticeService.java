package com.wuli.badminton.service;

import com.github.pagehelper.PageInfo;
import com.wuli.badminton.dto.NoticeDto;
import com.wuli.badminton.vo.NoticeVo;

/**
 * 公告通知服务接口
 */
public interface NoticeService {
    
    /**
     * 获取已发布的通知列表（前台和后台共用）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param type 通知类型，null表示查询所有类型
     * @return 通知列表
     */
    PageInfo<NoticeVo> getPublishedNotices(Integer pageNum, Integer pageSize, Integer type);
    
    /**
     * 获取所有通知列表（管理员用，包含草稿）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 通知列表
     */
    PageInfo<NoticeVo> getAllNotices(Integer pageNum, Integer pageSize);
    
    /**
     * 根据ID获取通知详情
     * @param id 通知ID
     * @return 通知详情
     */
    NoticeVo getNoticeById(Integer id);
    
    /**
     * 创建通知（草稿状态）
     * @param noticeDto 通知数据
     * @return 通知ID
     */
    Integer createNotice(NoticeDto noticeDto);
    
    /**
     * 更新通知内容
     * @param id 通知ID
     * @param noticeDto 通知数据
     * @return 是否成功
     */
    boolean updateNotice(Integer id, NoticeDto noticeDto);
    
    /**
     * 发布通知
     * @param id 通知ID
     * @return 是否成功
     */
    boolean publishNotice(Integer id);
    
    /**
     * 删除通知
     * @param id 通知ID
     * @return 是否成功
     */
    boolean deleteNotice(Integer id);
} 