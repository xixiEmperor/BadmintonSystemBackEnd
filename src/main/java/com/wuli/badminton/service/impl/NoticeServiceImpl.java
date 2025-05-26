package com.wuli.badminton.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wuli.badminton.dao.NoticeMapper;
import com.wuli.badminton.dto.NoticeDto;
import com.wuli.badminton.pojo.Notice;
import com.wuli.badminton.service.NoticeService;
import com.wuli.badminton.vo.NoticeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告通知服务实现类
 */
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    
    @Autowired
    private NoticeMapper noticeMapper;
    
    /**
     * 获取已发布的通知列表（前台和后台共用）
     */
    @Override
    public PageInfo<NoticeVo> getPublishedNotices(Integer pageNum, Integer pageSize, Integer type) {
        log.info("【获取已发布通知】开始查询: pageNum={}, pageSize={}, type={}", pageNum, pageSize, type);
        
        PageHelper.startPage(pageNum, pageSize);
        
        List<Notice> notices;
        if (type != null) {
            notices = noticeMapper.selectPublishedNoticesByType(type);
        } else {
            notices = noticeMapper.selectPublishedNotices();
        }
        
        List<NoticeVo> noticeVoList = notices.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        PageInfo<Notice> pageInfo = new PageInfo<>(notices);
        PageInfo<NoticeVo> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result, "list");
        result.setList(noticeVoList);
        
        log.info("【获取已发布通知】查询完成: 共{}条记录", result.getTotal());
        return result;
    }
    
    /**
     * 获取所有通知列表（管理员用，包含草稿）
     */
    @Override
    public PageInfo<NoticeVo> getAllNotices(Integer pageNum, Integer pageSize) {
        log.info("【管理员获取所有通知】开始查询: pageNum={}, pageSize={}", pageNum, pageSize);
        
        PageHelper.startPage(pageNum, pageSize);
        
        List<Notice> notices = noticeMapper.selectAllNotices();
        
        List<NoticeVo> noticeVoList = notices.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        PageInfo<Notice> pageInfo = new PageInfo<>(notices);
        PageInfo<NoticeVo> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result, "list");
        result.setList(noticeVoList);
        
        log.info("【管理员获取所有通知】查询完成: 共{}条记录", result.getTotal());
        return result;
    }
    
    /**
     * 根据ID获取通知详情
     */
    @Override
    public NoticeVo getNoticeById(Integer id) {
        log.info("【获取通知详情】通知ID: {}", id);
        
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            log.warn("【获取通知详情】通知不存在: id={}", id);
            return null;
        }
        
        return convertToVo(notice);
    }
    
    /**
     * 创建通知（草稿状态）
     */
    @Override
    @Transactional
    public Integer createNotice(NoticeDto noticeDto) {
        log.info("【创建通知】开始创建: title={}, type={}", noticeDto.getTitle(), noticeDto.getType());
        
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDto, notice);
        
        Date now = new Date();
        notice.setStatus(Notice.STATUS_DRAFT);
        notice.setCreateTime(now);
        notice.setUpdateTime(now);
        
        int result = noticeMapper.insert(notice);
        if (result > 0) {
            log.info("【创建通知】创建成功: id={}, title={}", notice.getId(), notice.getTitle());
            return notice.getId();
        } else {
            log.error("【创建通知】创建失败: title={}", noticeDto.getTitle());
            return null;
        }
    }
    
    /**
     * 更新通知内容
     */
    @Override
    @Transactional
    public boolean updateNotice(Integer id, NoticeDto noticeDto) {
        log.info("【更新通知】开始更新: id={}, title={}", id, noticeDto.getTitle());
        
        Notice existingNotice = noticeMapper.selectById(id);
        if (existingNotice == null) {
            log.warn("【更新通知】通知不存在: id={}", id);
            return false;
        }
        
        Notice notice = new Notice();
        notice.setId(id);
        BeanUtils.copyProperties(noticeDto, notice);
        notice.setUpdateTime(new Date());
        
        int result = noticeMapper.updateById(notice);
        if (result > 0) {
            log.info("【更新通知】更新成功: id={}, title={}", id, noticeDto.getTitle());
            return true;
        } else {
            log.error("【更新通知】更新失败: id={}", id);
            return false;
        }
    }
    
    /**
     * 发布通知
     */
    @Override
    @Transactional
    public boolean publishNotice(Integer id) {
        log.info("【发布通知】开始发布: id={}", id);
        
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            log.warn("【发布通知】通知不存在: id={}", id);
            return false;
        }
        
        if (Notice.STATUS_PUBLISHED.equals(notice.getStatus())) {
            log.warn("【发布通知】通知已发布: id={}", id);
            return false;
        }
        
        Date now = new Date();
        int result = noticeMapper.publishNotice(id, now);
        
        if (result > 0) {
            log.info("【发布通知】发布成功: id={}", id);
            return true;
        } else {
            log.error("【发布通知】发布失败: id={}", id);
            return false;
        }
    }
    
    /**
     * 删除通知
     */
    @Override
    @Transactional
    public boolean deleteNotice(Integer id) {
        log.info("【删除通知】开始删除: id={}", id);
        
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            log.warn("【删除通知】通知不存在: id={}", id);
            return false;
        }
        
        int result = noticeMapper.deleteById(id);
        if (result > 0) {
            log.info("【删除通知】删除成功: id={}, title={}", id, notice.getTitle());
            return true;
        } else {
            log.error("【删除通知】删除失败: id={}", id);
            return false;
        }
    }
    
    /**
     * 转换Notice为NoticeVo
     */
    private NoticeVo convertToVo(Notice notice) {
        NoticeVo noticeVo = new NoticeVo();
        BeanUtils.copyProperties(notice, noticeVo);
        
        // 设置描述信息
        noticeVo.setTypeDesc(notice.getTypeDesc());
        noticeVo.setStatusDesc(notice.getStatusDesc());
        
        return noticeVo;
    }
} 