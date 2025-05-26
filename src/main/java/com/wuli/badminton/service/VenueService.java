package com.wuli.badminton.service;

import com.wuli.badminton.dto.VenueDto;
import com.wuli.badminton.dto.VenueStatusMatrixDto;
import com.wuli.badminton.dto.VenueAvailabilityQueryDto;
import com.wuli.badminton.vo.ResponseVo;
import com.wuli.badminton.vo.VenueVo;
import com.wuli.badminton.vo.VenueStatusMatrixVo;
import com.wuli.badminton.vo.VenueAvailabilityVo;
import java.util.List;

/**
 * 场地服务接口
 */
public interface VenueService {
    
    /**
     * 获取所有场地列表
     */
    ResponseVo<List<VenueVo>> getAllVenues();
    
    /**
     * 根据ID获取场地详情
     */
    ResponseVo<VenueVo> getVenueById(Integer id);
    
    /**
     * 新增场地
     */
    ResponseVo<String> addVenue(VenueDto venueDto);
    
    /**
     * 更新场地信息
     */
    ResponseVo<String> updateVenue(Integer id, VenueDto venueDto);
    
    /**
     * 更新场地状态（启用/禁用）
     */
    ResponseVo<String> updateVenueStatus(Integer id, Integer status);
    
    /**
     * 删除场地
     */
    ResponseVo<String> deleteVenue(Integer id);
    
    /**
     * 根据状态获取场地列表
     */
    ResponseVo<List<VenueVo>> getVenuesByStatus(Integer status);
    
    /**
     * 获取场地状态矩阵（用于可视化展示）
     */
    ResponseVo<VenueStatusMatrixVo> getVenueStatusMatrix(VenueStatusMatrixDto dto);
    
    /**
     * 按时间段查询场地可用性（用于查找可用场地）
     */
    ResponseVo<VenueAvailabilityVo> queryVenueAvailability(VenueAvailabilityQueryDto dto);
} 