package com.wuli.badminton.service;

import com.wuli.badminton.dto.CartDto;
import com.wuli.badminton.dto.CartItemForm;
import com.wuli.badminton.pojo.CartItem;

import java.util.List;
import java.util.Map;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 获取当前用户购物车
     * @return 购物车数据
     */
    CartDto getUserCart();
    
    /**
     * 添加商品到购物车
     * @param form 购物车商品表单
     * @return 是否添加成功
     */
    boolean addItem(CartItemForm form);
    
    /**
     * 更新购物车商品数量
     * @param productId 商品ID
     * @param quantity 数量
     * @param specs 规格信息
     * @return 是否更新成功
     */
    boolean updateQuantity(Integer productId, Integer quantity, Map<String, String> specs);
    
    /**
     * 删除购物车商品
     * @param productId 商品ID
     * @param specs 规格信息
     * @return 是否删除成功
     */
    boolean deleteItem(Integer productId, Map<String, String> specs);
    
    /**
     * 选择/取消选择单个商品
     * @param productId 商品ID
     * @param specs 规格信息
     * @param selected 是否选中
     * @return 是否操作成功
     */
    boolean selectItem(Integer productId, Map<String, String> specs, boolean selected);
    
    /**
     * 全选/取消全选
     * @param selected 是否全选
     * @return 是否操作成功
     */
    boolean selectAll(boolean selected);
    
    /**
     * 清空购物车
     * @return 是否清空成功
     */
    boolean clearCart();
    
    /**
     * 获取用户选中的购物车商品列表
     * @param userId 用户ID
     * @return 选中的购物车商品列表
     */
    List<CartItem> listSelectedItems(Long userId);
    
    /**
     * 获取用户所有购物车商品列表（包括未选中的）
     * @param userId 用户ID
     * @return 所有购物车商品列表
     */
    List<CartItem> listAllItems(Long userId);
    
    /**
     * 删除用户选中的购物车商品
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteSelectedItems(Long userId);
}