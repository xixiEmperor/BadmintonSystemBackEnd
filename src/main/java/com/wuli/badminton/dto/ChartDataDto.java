package com.wuli.badminton.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 图表数据DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDto {
    private List<String> labels;    // X轴标签
    private List<Object> data;      // 数据值
    private String title;           // 图表标题
    private String type;            // 图表类型
    
    // 简单构造方法
    public ChartDataDto(List<String> labels, List<Object> data) {
        this.labels = labels;
        this.data = data;
    }
    
    /**
     * 用于饼图的数据项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PieDataItem {
        private String name;
        private Object value;
    }
    
    /**
     * 用于柱状图/折线图的系列数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeriesData {
        private String name;
        private List<Object> data;
        private String type; // 图表类型：line, bar等
    }
} 