package com.wuli.badminton.service.impl;

import com.wuli.badminton.dao.VenueMapper;
import com.wuli.badminton.dto.VenueDto;
import com.wuli.badminton.enums.ResponseEnum;
import com.wuli.badminton.enums.VenueStatusEnum;
import com.wuli.badminton.pojo.Venue;
import com.wuli.badminton.service.VenueService;
import com.wuli.badminton.vo.ResponseVo;
import com.wuli.badminton.vo.VenueVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 场地服务实现类
 */
@Service
@Slf4j
public class VenueServiceImpl implements VenueService {
    
    @Autowired
    private VenueMapper venueMapper;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public ResponseVo<List<VenueVo>> getAllVenues() {
        log.info("【获取所有场地】开始查询");
        
        List<Venue> venues = venueMapper.selectAll();
        List<VenueVo> venueVos = new ArrayList<>();
        
        for (Venue venue : venues) {
            VenueVo venueVo = convertToVenueVo(venue);
            venueVos.add(venueVo);
        }
        
        log.info("【获取所有场地】查询成功，共{}个场地", venueVos.size());
        return ResponseVo.success(venueVos);
    }
    
    @Override
    public ResponseVo<VenueVo> getVenueById(Integer id) {
        log.info("【获取场地详情】场地ID: {}", id);
        
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            log.warn("【获取场地详情】场地不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
        }
        
        VenueVo venueVo = convertToVenueVo(venue);
        
        log.info("【获取场地详情】查询成功，场地名称: {}", venue.getName());
        return ResponseVo.success(venueVo);
    }
    
    @Override
    public ResponseVo<List<VenueVo>> getVenuesByStatus(Integer status) {
        log.info("【根据状态获取场地】状态: {}", status);
        
        List<Venue> venues = venueMapper.selectByStatus(status);
        List<VenueVo> venueVos = new ArrayList<>();
        
        for (Venue venue : venues) {
            VenueVo venueVo = convertToVenueVo(venue);
            venueVos.add(venueVo);
        }
        
        log.info("【根据状态获取场地】查询成功，共{}个场地", venueVos.size());
        return ResponseVo.success(venueVos);
    }
    
    @Override
    @Transactional
    public ResponseVo<String> addVenue(VenueDto venueDto) {
        log.info("【新增场地】场地名称: {}", venueDto.getName());
        
        // 转换为实体对象
        Venue venue = new Venue();
        BeanUtils.copyProperties(venueDto, venue);
        
        Date now = new Date();
        venue.setCreateTime(now);
        venue.setUpdateTime(now);
        
        // 设置默认状态为空闲中
        if (venue.getStatus() == null) {
            venue.setStatus(VenueStatusEnum.AVAILABLE.getCode());
        }
        
        int result = venueMapper.insert(venue);
        
        if (result > 0) {
            log.info("【新增场地】新增成功，场地ID: {}", venue.getId());
            return ResponseVo.success("场地新增成功");
        } else {
            log.error("【新增场地】新增失败");
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "场地新增失败");
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> updateVenue(Integer id, VenueDto venueDto) {
        log.info("【更新场地信息】场地ID: {}, 场地名称: {}", id, venueDto.getName());
        
        Venue existingVenue = venueMapper.selectById(id);
        if (existingVenue == null) {
            log.warn("【更新场地信息】场地不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
        }
        
        // 更新场地信息
        existingVenue.setName(venueDto.getName());
        existingVenue.setDescription(venueDto.getDescription());
        existingVenue.setLocation(venueDto.getLocation());
        existingVenue.setPricePerHour(venueDto.getPricePerHour());
        existingVenue.setType(venueDto.getType());
        if (venueDto.getStatus() != null) {
            existingVenue.setStatus(venueDto.getStatus());
        }
        existingVenue.setUpdateTime(new Date());
        
        int result = venueMapper.updateById(existingVenue);
        
        if (result > 0) {
            log.info("【更新场地信息】更新成功，场地: {}", existingVenue.getName());
            return ResponseVo.success("场地信息更新成功");
        } else {
            log.error("【更新场地信息】更新失败，场地ID: {}", id);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "场地信息更新失败");
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> updateVenueStatus(Integer id, Integer status) {
        log.info("【更新场地状态】场地ID: {}, 新状态: {}", id, status);
        
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            log.warn("【更新场地状态】场地不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
        }
        
        // 验证状态值是否有效
        VenueStatusEnum statusEnum = VenueStatusEnum.getByCode(status);
        if (statusEnum == null) {
            log.warn("【更新场地状态】无效的状态值: {}", status);
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, "无效的场地状态");
        }
        
        int result = venueMapper.updateStatus(id, status);
        
        if (result > 0) {
            log.info("【更新场地状态】更新成功，场地: {}, 状态: {}", venue.getName(), statusEnum.getDesc());
            return ResponseVo.success("场地状态更新成功");
        } else {
            log.error("【更新场地状态】更新失败，场地ID: {}", id);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "场地状态更新失败");
        }
    }
    
    @Override
    @Transactional
    public ResponseVo<String> deleteVenue(Integer id) {
        log.info("【删除场地】场地ID: {}", id);
        
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            log.warn("【删除场地】场地不存在，ID: {}", id);
            return ResponseVo.error(ResponseEnum.VENUE_NOT_EXIST);
        }
        
        // 检查场地是否可以删除（如果有未完成的预约，不允许删除）
        // TODO: 这里可以添加预约检查逻辑
        
        int result = venueMapper.deleteById(id);
        
        if (result > 0) {
            log.info("【删除场地】删除成功，场地: {}", venue.getName());
            return ResponseVo.success("场地删除成功");
        } else {
            log.error("【删除场地】删除失败，场地ID: {}", id);
            return ResponseVo.error(ResponseEnum.SERVER_ERROR, "场地删除失败");
        }
    }
    
    /**
     * 转换为VenueVo
     */
    private VenueVo convertToVenueVo(Venue venue) {
        VenueVo venueVo = new VenueVo();
        BeanUtils.copyProperties(venue, venueVo);
        
        // 设置类型描述
        venueVo.setTypeDesc(venue.getType() == 1 ? "羽毛球场" : "未知");
        
        // 设置状态描述
        VenueStatusEnum statusEnum = VenueStatusEnum.getByCode(venue.getStatus());
        venueVo.setStatusDesc(statusEnum != null ? statusEnum.getDesc() : "未知");
        
        // 格式化时间
        if (venue.getCreateTime() != null) {
            venueVo.setCreateTime(dateFormat.format(venue.getCreateTime()));
        }
        if (venue.getUpdateTime() != null) {
            venueVo.setUpdateTime(dateFormat.format(venue.getUpdateTime()));
        }
        
        return venueVo;
    }
} 