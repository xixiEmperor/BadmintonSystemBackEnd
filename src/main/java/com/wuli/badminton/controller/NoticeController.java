package com.wuli.badminton.controller;

import com.github.pagehelper.PageInfo;
import com.wuli.badminton.dto.NoticeDto;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.service.NoticeService;
import com.wuli.badminton.vo.NoticeVo;
import com.wuli.badminton.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 公告通知控制器
 */
@RestController
@RequestMapping("/api/reservation/notice")
@Slf4j
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    /**
     * 获取已发布的通知列表（前台和后台共用）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param type 通知类型：1-普通通知，2-重要通知，null-全部类型
     * @return 通知列表
     */
    @GetMapping
    public ResponseVo<PageInfo<NoticeVo>> getPublishedNotices(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "type", required = false) Integer type) {
        
        log.info("【获取已发布通知】请求参数: pageNum={}, pageSize={}, type={}", pageNum, pageSize, type);
        
        PageInfo<NoticeVo> noticeList = noticeService.getPublishedNotices(pageNum, pageSize, type);
        return ResponseVo.success(noticeList);
    }
    
    /**
     * 根据ID获取通知详情
     * @param id 通知ID
     * @return 通知详情
     */
    @GetMapping("/{id}")
    public ResponseVo<NoticeVo> getNoticeById(@PathVariable Integer id) {
        log.info("【获取通知详情】通知ID: {}", id);
        
        NoticeVo noticeVo = noticeService.getNoticeById(id);
        if (noticeVo == null) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "通知不存在");
        }
        return ResponseVo.success(noticeVo);
    }
    
    // ==================== 管理员接口 ====================
    
    /**
     * 管理员获取所有通知列表（包含草稿）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 通知列表
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<PageInfo<NoticeVo>> getAllNotices(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        
        log.info("【管理员获取所有通知】请求参数: pageNum={}, pageSize={}", pageNum, pageSize);
        
        PageInfo<NoticeVo> noticeList = noticeService.getAllNotices(pageNum, pageSize);
        return ResponseVo.success(noticeList);
    }
    
    /**
     * 创建通知（草稿状态）
     * @param noticeDto 通知数据
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<Integer> createNotice(@RequestBody @Valid NoticeDto noticeDto) {
        log.info("【创建通知】请求参数: title={}, type={}", noticeDto.getTitle(), noticeDto.getType());
        
        // 验证通知类型
        if (noticeDto.getType() != 1 && noticeDto.getType() != 2) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "通知类型只能是1(普通通知)或2(重要通知)");
        }
        
        Integer noticeId = noticeService.createNotice(noticeDto);
        if (noticeId == null) {
            return ResponseVo.error(ResponseEnum.ERROR, "创建通知失败");
        }
        
        return ResponseVo.success("通知创建成功", noticeId);
    }
    
    /**
     * 更新通知内容
     * @param id 通知ID
     * @param noticeDto 通知数据
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> updateNotice(
            @PathVariable Integer id,
            @RequestBody @Valid NoticeDto noticeDto) {
        
        log.info("【更新通知】请求参数: id={}, title={}, type={}", id, noticeDto.getTitle(), noticeDto.getType());
        
        // 验证通知类型
        if (noticeDto.getType() != 1 && noticeDto.getType() != 2) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "通知类型只能是1(普通通知)或2(重要通知)");
        }
        
        boolean result = noticeService.updateNotice(id, noticeDto);
        if (!result) {
            return ResponseVo.error(ResponseEnum.ERROR, "更新通知失败，通知不存在");
        }
        
        return ResponseVo.success("通知更新成功");
    }
    
    /**
     * 发布通知
     * @param id 通知ID
     * @return 发布结果
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> publishNotice(@PathVariable Integer id) {
        log.info("【发布通知】通知ID: {}", id);
        
        boolean result = noticeService.publishNotice(id);
        if (!result) {
            return ResponseVo.error(ResponseEnum.ERROR, "发布通知失败，通知不存在或已发布");
        }
        
        return ResponseVo.success("通知发布成功");
    }
    
    /**
     * 删除通知
     * @param id 通知ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVo<String> deleteNotice(@PathVariable Integer id) {
        log.info("【删除通知】通知ID: {}", id);
        
        boolean result = noticeService.deleteNotice(id);
        if (!result) {
            return ResponseVo.error(ResponseEnum.ERROR, "删除通知失败，通知不存在");
        }
        
        return ResponseVo.success("通知删除成功");
    }
} 