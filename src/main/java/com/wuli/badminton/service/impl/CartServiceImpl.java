package com.wuli.badminton.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuli.badminton.dto.CartDto;
import com.wuli.badminton.dto.CartItemForm;
import com.wuli.badminton.pojo.CartItem;
import com.wuli.badminton.pojo.MallProduct;
import com.wuli.badminton.pojo.ProductSpecification;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.service.CartService;
import com.wuli.badminton.service.MallProductService;
import com.wuli.badminton.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    
    // Redis中购物车key的前缀
    private static final String CART_REDIS_KEY_PREFIX = "cart:";
    
    // 购物车过期时间（30天）
    private static final int CART_EXPIRE_DAYS = 30;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MallProductService mallProductService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.error("获取当前用户失败");
            throw new RuntimeException("用户未登录");
        }
        return currentUser.getId();
    }
    
    /**
     * 获取用户购物车在Redis中的key
     * @param userId 用户ID
     * @return Redis key
     */
    private String getCartKey(Long userId) {
        return CART_REDIS_KEY_PREFIX + userId;
    }
    
    /**
     * 生成购物车项在Hash中的field名
     * @param productId 商品ID
     * @param specs 规格信息
     * @return Hash field名称
     */
    private String generateCartItemField(Integer productId, Map<String, String> specs) {
        if (specs == null || specs.isEmpty()) {
            return productId.toString();
        }
        
        // 将规格按照key的字母顺序排序，确保相同规格生成相同的field名
        TreeMap<String, String> sortedSpecs = new TreeMap<>(specs);
        try {
            return productId + ":" + objectMapper.writeValueAsString(sortedSpecs);
        } catch (JsonProcessingException e) {
            logger.error("生成购物车项field失败: productId={}, specs={}", productId, specs, e);
            return productId + ":" + sortedSpecs.toString();
        }
    }
    
    /**
     * 从field名称中提取商品ID
     * @param field field名称
     * @return 商品ID
     */
    private Integer extractProductId(String field) {
        String[] parts = field.split(":", 2);
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            logger.error("从field中提取商品ID失败: field={}", field, e);
            return null;
        }
    }
    
    /**
     * 从field名称中提取规格信息
     * @param field field名称
     * @return 规格信息
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> extractSpecs(String field) {
        String[] parts = field.split(":", 2);
        if (parts.length < 2) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(parts[1], Map.class);
        } catch (Exception e) {
            logger.error("从field中提取规格信息失败: field={}", field, e);
            return new HashMap<>();
        }
    }
    
    /**
     * 将JSON字符串转为CartItem对象
     * @param json JSON字符串
     * @return CartItem对象
     */
    private CartItem deserializeCartItem(String json) {
        try {
            return objectMapper.readValue(json, CartItem.class);
        } catch (Exception e) {
            logger.error("反序列化购物车项失败: json={}", json, e);
            return null;
        }
    }
    
    /**
     * 将CartItem对象转为JSON字符串
     * @param cartItem CartItem对象
     * @return JSON字符串
     */
    private String serializeCartItem(CartItem cartItem) {
        try {
            return objectMapper.writeValueAsString(cartItem);
        } catch (Exception e) {
            logger.error("序列化购物车项失败: cartItem={}", cartItem, e);
            throw new RuntimeException("序列化购物车项失败", e);
        }
    }
    
    /**
     * 根据商品ID和规格获取有效的商品规格
     * @param productId 商品ID
     * @param specs 规格信息
     * @return 商品规格
     */
    private ProductSpecification getValidProductSpecification(Integer productId, Map<String, String> specs) {
        // 获取商品
        MallProduct product = mallProductService.getProductMapper().findById(productId);
        if (product == null || !product.getStatus().equals(1)) {
            logger.warn("商品不存在或已下架: productId={}", productId);
            return null;
        }
        
        // 检查商品是否有规格
        if (product.getHasSpecification() == null || product.getHasSpecification() != 1) {
            logger.warn("商品没有规格: productId={}", productId);
            return null;
        }
        
        // 获取商品规格
        ProductSpecification specification = mallProductService.getProductSpecification(productId, specs);
        if (specification == null || specification.getStatus() == null || specification.getStatus() != 1) {
            logger.warn("商品规格不存在或已禁用: productId={}, specs={}", productId, specs);
            return null;
        }
        
        return specification;
    }
    
    /**
     * 验证商品信息并创建购物车项
     * @param form 购物车商品表单
     * @return 购物车项
     */
    private CartItem createCartItem(CartItemForm form) {
        Integer productId = form.getProductId();
        Integer quantity = form.getQuantity();
        Map<String, String> specs = form.getSpecs();
        
        // 验证参数
        if (productId == null || quantity == null || quantity <= 0) {
            logger.warn("参数错误: productId={}, quantity={}", productId, quantity);
            return null;
        }
        
        // 获取商品
        MallProduct product = mallProductService.getProductMapper().findById(productId);
        if (product == null || !product.getStatus().equals(1)) {
            logger.warn("商品不存在或已下架: productId={}", productId);
            return null;
        }
        
        CartItem cartItem = new CartItem();
        cartItem.setProductId(productId);
        cartItem.setProductName(product.getName());
        cartItem.setProductImage(product.getMainImage());
        cartItem.setProductPrice(product.getPrice());
        cartItem.setPriceAdjustment(BigDecimal.ZERO); // 默认设置价格调整为0
        cartItem.setQuantity(quantity);
        cartItem.setSelected(true);
        
        // 如果商品有规格
        if (product.getHasSpecification() != null && product.getHasSpecification() == 1) {
            if (specs == null || specs.isEmpty()) {
                logger.warn("商品有规格但未提供规格信息: productId={}", productId);
                return null;
            }
            
            ProductSpecification specification = mallProductService.getProductSpecification(productId, specs);
            if (specification == null || specification.getStatus() == null || specification.getStatus() != 1) {
                logger.warn("商品规格不存在或已禁用: productId={}, specs={}", productId, specs);
                return null;
            }
            
            cartItem.setSpecs(specs);
            cartItem.setStock(specification.getStock());
            cartItem.setSpecificationId(specification.getId());
            
            // 设置规格价格调整值
            cartItem.setPriceAdjustment(specification.getPriceAdjustment());
            
            // 计算实际价格
            BigDecimal actualPrice = product.getPrice().add(specification.getPriceAdjustment());
            cartItem.setTotalPrice(actualPrice.multiply(new BigDecimal(quantity)));
        } else {
            // 没有规格
            cartItem.setSpecs(new HashMap<>());
            cartItem.setStock(product.getStock());
            cartItem.setSpecificationId(null);
            cartItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(quantity)));
        }
        
        return cartItem;
    }
    
    /**
     * 更新购物车项的最新商品信息（价格、库存等）
     * @param cartItem 购物车项
     */
    private void updateCartItemLatestInfo(CartItem cartItem) {
        // 获取商品
        MallProduct product = mallProductService.getProductMapper().findById(cartItem.getProductId());
        if (product == null || !product.getStatus().equals(1)) {
            logger.warn("更新购物车项信息时发现商品不存在或已下架: productId={}", cartItem.getProductId());
            // 设置库存为0，前端可以据此提示用户
            cartItem.setStock(0);
            return;
        }
        
        // 更新商品基本信息
        cartItem.setProductName(product.getName());
        cartItem.setProductImage(product.getMainImage());
        cartItem.setProductPrice(product.getPrice());
        
        // 如果是有规格的商品
        if (product.getHasSpecification() != null && product.getHasSpecification() == 1 && 
                cartItem.getSpecs() != null && !cartItem.getSpecs().isEmpty()) {
            
            // 获取最新的规格信息
            ProductSpecification specification = mallProductService.getProductSpecification(
                    cartItem.getProductId(), cartItem.getSpecs());
            
            if (specification == null || specification.getStatus() == null || specification.getStatus() != 1) {
                logger.warn("更新购物车项信息时发现商品规格不存在或已禁用: productId={}, specs={}", 
                        cartItem.getProductId(), cartItem.getSpecs());
                // 设置库存为0
                cartItem.setStock(0);
                return;
            }
            
            // 更新规格相关信息
            cartItem.setStock(specification.getStock());
            cartItem.setSpecificationId(specification.getId());
            cartItem.setPriceAdjustment(specification.getPriceAdjustment());
            
            // 计算实际价格
            BigDecimal actualPrice = product.getPrice().add(specification.getPriceAdjustment());
            cartItem.setTotalPrice(actualPrice.multiply(new BigDecimal(cartItem.getQuantity())));
        } else {
            // 没有规格的商品
            cartItem.setStock(product.getStock());
            cartItem.setSpecificationId(null);
            cartItem.setPriceAdjustment(BigDecimal.ZERO);
            cartItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }
    }
    
    @Override
    public CartDto getUserCart() {
        Long userId = getCurrentUserId();
        String cartKey = getCartKey(userId);
        
        logger.info("获取用户购物车: userId={}", userId);
        
        // 从Redis获取购物车数据
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        Map<String, String> cartMap = hashOps.entries(cartKey);
        
        List<CartItem> cartItems = new ArrayList<>();
        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        boolean allSelected = true;
        
        // 处理购物车数据
        for (Map.Entry<String, String> entry : cartMap.entrySet()) {
            CartItem cartItem = deserializeCartItem(entry.getValue());
            if (cartItem != null) {
                // 更新最新的价格和库存
                updateCartItemLatestInfo(cartItem);
                
                cartItems.add(cartItem);
                
                // 统计选中的商品数量和价格
                if (cartItem.getSelected()) {
                    totalQuantity += cartItem.getQuantity();
                    totalPrice = totalPrice.add(cartItem.getTotalPrice());
                } else {
                    allSelected = false;
                }
                
                // 更新Redis中的数据
                hashOps.put(cartKey, entry.getKey(), serializeCartItem(cartItem));
            }
        }
        
        // 刷新购物车过期时间
        redisTemplate.expire(cartKey, CART_EXPIRE_DAYS, TimeUnit.DAYS);
        
        // 创建返回结果
        CartDto cartDto = new CartDto();
        cartDto.setCartItems(cartItems);
        cartDto.setTotalQuantity(totalQuantity);
        cartDto.setTotalPrice(totalPrice);
        cartDto.setAllSelected(allSelected);
        
        return cartDto;
    }
    
    @Override
    public boolean addItem(CartItemForm form) {
        Long userId = getCurrentUserId();
        String cartKey = getCartKey(userId);
        
        logger.info("添加商品到购物车: userId={}, productId={}, quantity={}, specs={}", 
                userId, form.getProductId(), form.getQuantity(), form.getSpecs());
        
        // 创建购物车项
        CartItem cartItem = createCartItem(form);
        if (cartItem == null) {
            return false;
        }
        
        // 生成购物车项的field名
        String field = generateCartItemField(form.getProductId(), form.getSpecs());
        
        // 获取Redis操作对象
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        
        // 检查购物车中是否已存在相同商品
        String existingItemJson = hashOps.get(cartKey, field);
        if (existingItemJson != null) {
            CartItem existingItem = deserializeCartItem(existingItemJson);
            if (existingItem != null) {
                // 更新数量
                int newQuantity = existingItem.getQuantity() + form.getQuantity();
                
                // 检查库存
                if (newQuantity > existingItem.getStock()) {
                    logger.warn("商品库存不足: productId={}, specs={}, stock={}, requiredQuantity={}", 
                            form.getProductId(), form.getSpecs(), existingItem.getStock(), newQuantity);
                    newQuantity = existingItem.getStock();
                }
                
                existingItem.setQuantity(newQuantity);
                
                // 更新价格信息
                if (existingItem.getSpecificationId() != null) {
                    // 有规格的商品 - 需要重新获取规格信息来获取价格调整值
                    ProductSpecification specification = mallProductService.getProductSpecification(
                            existingItem.getProductId(), existingItem.getSpecs());
                    if (specification != null) {
                        existingItem.setPriceAdjustment(specification.getPriceAdjustment());
                        BigDecimal actualPrice = existingItem.getProductPrice().add(specification.getPriceAdjustment());
                        existingItem.setTotalPrice(actualPrice.multiply(new BigDecimal(newQuantity)));
                    } else {
                        // 如果规格不存在，使用现有的价格调整值
                        BigDecimal actualPrice = existingItem.getProductPrice().add(existingItem.getPriceAdjustment());
                        existingItem.setTotalPrice(actualPrice.multiply(new BigDecimal(newQuantity)));
                    }
                } else {
                    // 无规格的商品
                    existingItem.setPriceAdjustment(BigDecimal.ZERO);
                    existingItem.setTotalPrice(existingItem.getProductPrice().multiply(new BigDecimal(newQuantity)));
                }
                
                // 更新Redis
                hashOps.put(cartKey, field, serializeCartItem(existingItem));
            } else {
                // 原有数据解析失败，直接覆盖
                hashOps.put(cartKey, field, serializeCartItem(cartItem));
            }
        } else {
            // 添加新商品
            hashOps.put(cartKey, field, serializeCartItem(cartItem));
        }
        
        // 刷新购物车过期时间
        redisTemplate.expire(cartKey, CART_EXPIRE_DAYS, TimeUnit.DAYS);
        
        return true;
    }
    
    @Override
    public boolean updateQuantity(Integer productId, Integer quantity, Map<String, String> specs) {
        Long userId = getCurrentUserId();
        String cartKey = getCartKey(userId);
        String field = generateCartItemField(productId, specs);
        
        logger.info("更新购物车商品数量: userId={}, productId={}, quantity={}, specs={}", 
                userId, productId, quantity, specs);
        
        // 验证参数
        if (productId == null || quantity == null || quantity <= 0) {
            logger.warn("参数错误: productId={}, quantity={}", productId, quantity);
            return false;
        }
        
        // 获取Redis操作对象
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        
        // 检查购物车中是否存在该商品
        String existingItemJson = hashOps.get(cartKey, field);
        if (existingItemJson == null) {
            logger.warn("购物车中不存在该商品: productId={}, specs={}", productId, specs);
            return false;
        }
        
        CartItem cartItem = deserializeCartItem(existingItemJson);
        if (cartItem == null) {
            logger.error("解析购物车项失败: json={}", existingItemJson);
            return false;
        }
        
        // 更新最新的商品信息
        updateCartItemLatestInfo(cartItem);
        
        // 检查库存
        if (quantity > cartItem.getStock()) {
            logger.warn("商品库存不足: productId={}, specs={}, stock={}, requiredQuantity={}", 
                    productId, specs, cartItem.getStock(), quantity);
            quantity = cartItem.getStock();
        }
        
        // 更新数量
        cartItem.setQuantity(quantity);
        
        // 更新总价
        if (cartItem.getSpecificationId() != null) {
            // 有规格的商品
            BigDecimal actualPrice = cartItem.getProductPrice().add(cartItem.getPriceAdjustment());
            cartItem.setTotalPrice(actualPrice.multiply(new BigDecimal(quantity)));
        } else {
            // 无规格的商品
            cartItem.setTotalPrice(cartItem.getProductPrice().multiply(new BigDecimal(quantity)));
        }
        
        // 更新Redis
        hashOps.put(cartKey, field, serializeCartItem(cartItem));
        
        // 刷新购物车过期时间
        redisTemplate.expire(cartKey, CART_EXPIRE_DAYS, TimeUnit.DAYS);
        
        return true;
    }
    
    @Override
    public boolean deleteItem(Integer productId, Map<String, String> specs) {
        Long userId = getCurrentUserId();
        String cartKey = getCartKey(userId);
        String field = generateCartItemField(productId, specs);
        
        logger.info("删除购物车商品: userId={}, productId={}, specs={}", userId, productId, specs);
        
        // 获取Redis操作对象
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        
        // 删除商品
        hashOps.delete(cartKey, field);
        
        // 刷新购物车过期时间
        redisTemplate.expire(cartKey, CART_EXPIRE_DAYS, TimeUnit.DAYS);
        
        return true;
    }
    
    @Override
    public boolean selectItem(Integer productId, Map<String, String> specs, boolean selected) {
        Long userId = getCurrentUserId();
        String cartKey = getCartKey(userId);
        String field = generateCartItemField(productId, specs);
        
        logger.info("选择/取消选择购物车商品: userId={}, productId={}, specs={}, selected={}", 
                userId, productId, specs, selected);
        
        // 获取Redis操作对象
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        
        // 检查购物车中是否存在该商品
        String existingItemJson = hashOps.get(cartKey, field);
        if (existingItemJson == null) {
            logger.warn("购物车中不存在该商品: productId={}, specs={}", productId, specs);
            return false;
        }
        
        CartItem cartItem = deserializeCartItem(existingItemJson);
        if (cartItem == null) {
            logger.error("解析购物车项失败: json={}", existingItemJson);
            return false;
        }
        
        // 更新选中状态
        cartItem.setSelected(selected);
        
        // 更新Redis
        hashOps.put(cartKey, field, serializeCartItem(cartItem));
        
        // 刷新购物车过期时间
        redisTemplate.expire(cartKey, CART_EXPIRE_DAYS, TimeUnit.DAYS);
        
        return true;
    }
    
    @Override
    public boolean selectAll(boolean selected) {
        Long userId = getCurrentUserId();
        String cartKey = getCartKey(userId);
        
        logger.info("全选/取消全选购物车: userId={}, selected={}", userId, selected);
        
        // 获取Redis操作对象
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        
        // 获取购物车所有商品
        Map<String, String> cartMap = hashOps.entries(cartKey);
        if (cartMap.isEmpty()) {
            logger.info("购物车为空: userId={}", userId);
            return true;
        }
        
        // 更新所有商品的选中状态
        for (Map.Entry<String, String> entry : cartMap.entrySet()) {
            CartItem cartItem = deserializeCartItem(entry.getValue());
            if (cartItem != null) {
                cartItem.setSelected(selected);
                hashOps.put(cartKey, entry.getKey(), serializeCartItem(cartItem));
            }
        }
        
        // 刷新购物车过期时间
        redisTemplate.expire(cartKey, CART_EXPIRE_DAYS, TimeUnit.DAYS);
        
        return true;
    }
    
    @Override
    public boolean clearCart() {
        Long userId = getCurrentUserId();
        String cartKey = getCartKey(userId);
        
        logger.info("清空购物车: userId={}", userId);
        
        // 删除购物车
        redisTemplate.delete(cartKey);
        
        return true;
    }
}