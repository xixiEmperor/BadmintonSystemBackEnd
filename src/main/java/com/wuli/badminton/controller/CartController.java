package com.wuli.badminton.controller;

import com.wuli.badminton.dto.CartDto;
import com.wuli.badminton.dto.CartItemForm;
import com.wuli.badminton.service.CartService;
import com.wuli.badminton.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    @Autowired
    private CartService cartService;
    
    /**
     * 获取购物车
     * @return 购物车数据
     */
    @GetMapping("")
    public ResponseVo<CartDto> getCart() {
        logger.info("获取购物车");
        
        try {
            CartDto cartDto = cartService.getUserCart();
            return ResponseVo.success(cartDto);
        } catch (Exception e) {
            logger.error("获取购物车异常: {}", e.getMessage(), e);
            if (e.getMessage().contains("用户未登录")) {
                return ResponseVo.error(401, "请先登录");
            }
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 添加商品到购物车
     * @param form 购物车商品表单
     * @return 添加结果
     */
    @PostMapping("")
    public ResponseVo<?> addToCart(@RequestBody CartItemForm form) {
        logger.info("添加商品到购物车: productId={}, quantity={}, specs={}", 
                form.getProductId(), form.getQuantity(), form.getSpecs());
        
        // 参数校验
        if (form.getProductId() == null) {
            return ResponseVo.error(4001, "商品ID不能为空");
        }
        if (form.getQuantity() == null || form.getQuantity() <= 0) {
            return ResponseVo.error(4001, "商品数量必须大于0");
        }
        
        try {
            boolean success = cartService.addItem(form);
            if (success) {
                return ResponseVo.success("添加成功",null);
            } else {
                return ResponseVo.error(4002, "添加失败，商品可能已下架或库存不足");
            }
        } catch (Exception e) {
            logger.error("添加商品到购物车异常: {}", e.getMessage(), e);
            if (e.getMessage().contains("用户未登录")) {
                return ResponseVo.error(401, "请先登录");
            }
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 更新购物车商品数量
     * @param productId 商品ID
     * @param body 请求体，包含quantity和specs
     * @return 更新结果
     */
    @PutMapping("/{productId}")
    public ResponseVo<?> updateCart(
            @PathVariable Integer productId,
            @RequestBody Map<String, Object> body) {
        
        Integer quantity = (Integer) body.get("quantity");
        @SuppressWarnings("unchecked")
        Map<String, String> specs = (Map<String, String>) body.get("specs");
        
        logger.info("更新购物车商品数量: productId={}, quantity={}, specs={}", 
                productId, quantity, specs);
        
        // 参数校验
        if (quantity == null || quantity <= 0) {
            return ResponseVo.error(4001, "商品数量必须大于0");
        }
        
        try {
            boolean success = cartService.updateQuantity(productId, quantity, specs);
            if (success) {
                return ResponseVo.success("更新成功",null);
            } else {
                return ResponseVo.error(4002, "更新失败，商品可能已下架或不在购物车中");
            }
        } catch (Exception e) {
            logger.error("更新购物车商品数量异常: {}", e.getMessage(), e);
            if (e.getMessage().contains("用户未登录")) {
                return ResponseVo.error(401, "请先登录");
            }
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 删除购物车商品
     * @param productId 商品ID
     * @param specs 规格信息，可通过查询参数或请求体传递
     * @param requestBody 请求体中的规格信息
     * @return 删除结果
     */
    @DeleteMapping("/{productId}")
    public ResponseVo<?> deleteCartItem(
            @PathVariable Integer productId,
            @RequestParam(required = false) Map<String, String> specs,
            @RequestBody(required = false) Map<String, Object> requestBody) {
        
        // 优先使用请求体中的规格信息
        Map<String, String> finalSpecs = specs;
        if (requestBody != null && requestBody.containsKey("specs")) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> bodySpecs = (Map<String, String>) requestBody.get("specs");
                if (bodySpecs != null) {
                    finalSpecs = bodySpecs;
                }
            } catch (ClassCastException e) {
                logger.warn("规格信息类型转换错误", e);
            }
        }
        
        logger.info("删除购物车商品: productId={}, specs={}", productId, finalSpecs);
        
        try {
            boolean success = cartService.deleteItem(productId, finalSpecs);
            if (success) {
                return ResponseVo.success("删除成功");
            } else {
                return ResponseVo.error(4002, "删除失败，商品可能已不在购物车中");
            }
        } catch (Exception e) {
            logger.error("删除购物车商品异常: {}", e.getMessage(), e);
            if (e.getMessage().contains("用户未登录")) {
                return ResponseVo.error(401, "请先登录");
            }
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 选择/取消选择购物车中的商品
     * @param productId 商品ID
     * @param body 请求体，包含selected和specs
     * @return 操作结果
     */
    @PutMapping("/select/{productId}")
    public ResponseVo<?> selectCartItem(
            @PathVariable Integer productId,
            @RequestBody Map<String, Object> body) {
        
        Boolean selected = (Boolean) body.get("selected");
        @SuppressWarnings("unchecked")
        Map<String, String> specs = (Map<String, String>) body.get("specs");
        
        logger.info("选择/取消选择购物车商品: productId={}, selected={}, specs={}", 
                productId, selected, specs);
        
        if (selected == null) {
            return ResponseVo.error(4001, "选中状态不能为空");
        }
        
        try {
            boolean success = cartService.selectItem(productId, specs, selected);
            if (success) {
                CartDto cartDto = cartService.getUserCart();
                return ResponseVo.success("操作成功", cartDto);
            } else {
                return ResponseVo.error(4002, "操作失败，商品可能已不在购物车中");
            }
        } catch (Exception e) {
            logger.error("选择/取消选择购物车商品异常: {}", e.getMessage(), e);
            if (e.getMessage().contains("用户未登录")) {
                return ResponseVo.error(401, "请先登录");
            }
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 全选/取消全选购物车
     * @param body 请求体，包含selected
     * @return 操作结果
     */
    @PutMapping("/select-all")
    public ResponseVo<?> selectAllCartItems(@RequestBody Map<String, Boolean> body) {
        Boolean selected = body.get("selected");
        
        logger.info("全选/取消全选购物车: selected={}", selected);
        
        if (selected == null) {
            return ResponseVo.error(4001, "选中状态不能为空");
        }
        
        try {
            boolean success = cartService.selectAll(selected);
            if (success) {
                CartDto cartDto = cartService.getUserCart();
                return ResponseVo.success("操作成功", cartDto);
            } else {
                return ResponseVo.error(4002, "操作失败，购物车可能为空");
            }
        } catch (Exception e) {
            logger.error("全选/取消全选购物车异常: {}", e.getMessage(), e);
            if (e.getMessage().contains("用户未登录")) {
                return ResponseVo.error(401, "请先登录");
            }
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 清空购物车
     * @return 操作结果
     */
    @DeleteMapping("")
    public ResponseVo<?> clearCart() {
        logger.info("清空购物车");
        
        try {
            boolean success = cartService.clearCart();
            if (success) {
                return ResponseVo.success("清空成功");
            } else {
                return ResponseVo.error(4002, "清空失败");
            }
        } catch (Exception e) {
            logger.error("清空购物车异常: {}", e.getMessage(), e);
            if (e.getMessage().contains("用户未登录")) {
                return ResponseVo.error(401, "请先登录");
            }
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
}