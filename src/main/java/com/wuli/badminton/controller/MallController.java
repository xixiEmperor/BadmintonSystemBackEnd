package com.wuli.badminton.controller;

import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.ProductAddDto;
import com.wuli.badminton.dto.ProductDetailDto;
import com.wuli.badminton.dto.ProductListDto;
import com.wuli.badminton.pojo.MallCategory;
import com.wuli.badminton.pojo.ProductSpecification;
import com.wuli.badminton.pojo.SpecificationOption;
import com.wuli.badminton.pojo.User;
import com.wuli.badminton.service.MallCategoryService;
import com.wuli.badminton.service.MallProductService;
import com.wuli.badminton.service.UserService;
import com.wuli.badminton.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城控制器
 */
@RestController
@RequestMapping("/api/mall")
public class MallController {
    
    private static final Logger logger = LoggerFactory.getLogger(MallController.class);
    
    @Autowired
    private MallCategoryService categoryService;
    
    @Autowired
    private MallProductService productService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取商品分类列表
     * @return 分类列表
     */
    @GetMapping("/categories")
    public ResponseVo<List<MallCategory>> getCategories() {
        logger.info("获取商品分类列表");
        
        List<MallCategory> categories = categoryService.getAllValidCategories();
        
        return ResponseVo.success(categories);
    }
    
    /**
     * 获取商品列表
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param orderBy 排序方式
     * @return 商品列表
     */
    @GetMapping("/products")
    public ResponseVo<PageResult<ProductListDto>> getProductList(
            @RequestParam(required = false, defaultValue = "all") String categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderBy) {
        
        logger.info("获取商品列表: categoryId={}, keyword={}, pageNum={}, pageSize={}, orderBy={}", 
                categoryId, keyword, pageNum, pageSize, orderBy);
        
        // 验证分类是否存在
        if (categoryId != null && !"all".equals(categoryId)) {
            try {
                Integer cateId = Integer.parseInt(categoryId);
                MallCategory category = categoryService.getCategoryById(cateId);
                if (category == null) {
                    logger.warn("分类不存在: categoryId={}", categoryId);
                    return ResponseVo.error(4001, "参数错误，分类不存在");
                }
            } catch (NumberFormatException e) {
                logger.warn("无效的分类ID: {}", categoryId);
                return ResponseVo.error(4001, "参数错误，分类不存在");
            }
        }
        
        // 验证排序方式
        if (orderBy != null && !orderBy.isEmpty()) {
            if (!orderBy.equals("price_asc") && !orderBy.equals("price_desc") && !orderBy.equals("sales_desc")) {
                logger.warn("无效的排序方式: {}", orderBy);
                return ResponseVo.error(4002, "参数错误，排序方式无效");
            }
        }
        
        try {
            PageResult<ProductListDto> result = productService.getProductList(categoryId, keyword, pageNum, pageSize, orderBy);
            return ResponseVo.success(result);
        } catch (Exception e) {
            logger.error("获取商品列表异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 管理员获取商品列表（包括全部状态）
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     * @param status 状态（null表示所有状态，1-在售，2-下架，3-删除）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param orderBy 排序方式
     * @return 商品列表
     */
    @GetMapping("/admin/products")
    public ResponseVo<PageResult<ProductListDto>> getAdminProductList(
            @RequestParam(required = false, defaultValue = "all") String categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderBy) {
        
        logger.info("管理员获取商品列表: categoryId={}, keyword={}, status={}, pageNum={}, pageSize={}, orderBy={}", 
                categoryId, keyword, status, pageNum, pageSize, orderBy);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试获取管理员商品列表");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能查看所有商品
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试获取管理员商品列表: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        // 验证状态参数
        if (status != null && (status < 1 || status > 3)) {
            logger.warn("无效的状态参数: {}", status);
            return ResponseVo.error(4002, "参数错误，状态值无效。有效值为：1-在售，2-下架，3-删除");
        }
        
        // 验证分类是否存在
        if (categoryId != null && !"all".equals(categoryId)) {
            try {
                Integer cateId = Integer.parseInt(categoryId);
                MallCategory category = categoryService.getCategoryById(cateId);
                if (category == null) {
                    logger.warn("分类不存在: categoryId={}", categoryId);
                    return ResponseVo.error(4001, "参数错误，分类不存在");
                }
            } catch (NumberFormatException e) {
                logger.warn("无效的分类ID: {}", categoryId);
                return ResponseVo.error(4001, "参数错误，分类不存在");
            }
        }
        
        // 验证排序方式
        if (orderBy != null && !orderBy.isEmpty()) {
            if (!orderBy.equals("price_asc") && !orderBy.equals("price_desc") && !orderBy.equals("sales_desc")) {
                logger.warn("无效的排序方式: {}", orderBy);
                return ResponseVo.error(4002, "参数错误，排序方式无效");
            }
        }
        
        try {
            PageResult<ProductListDto> result = productService.getAdminProductList(categoryId, keyword, status, pageNum, pageSize, orderBy);
            return ResponseVo.success(result);
        } catch (Exception e) {
            logger.error("获取管理员商品列表异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    @GetMapping("/products/{productId}")
    public ResponseVo<ProductDetailDto> getProductDetail(@PathVariable Integer productId) {
        logger.info("获取商品详情: productId={}", productId);
        
        try {
            ProductDetailDto productDetail = productService.getProductDetail(productId);
            if (productDetail == null) {
                logger.warn("商品不存在或已下架: productId={}", productId);
                return ResponseVo.error(4003, "商品不存在或已下架");
            }
            
            return ResponseVo.success(productDetail);
        } catch (Exception e) {
            logger.error("获取商品详情异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 上传商品图片
     * @param mainImage 主图
     * @param subImages 子图
     * @return 图片URL
     */
    @PostMapping("/products/upload")
    public ResponseVo<?> uploadProductImages(
            @RequestParam("mainImage") MultipartFile mainImage,
            @RequestParam(value = "subImages", required = false) MultipartFile[] subImages) {
        
        logger.info("上传商品图片: mainImage={}, subImages.length={}", 
                mainImage.getOriginalFilename(), 
                subImages != null ? subImages.length : 0);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试上传商品图片");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能上传商品图片
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试上传商品图片: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        // 检查主图
        if (mainImage == null || mainImage.isEmpty()) {
            logger.warn("主图为空");
            return ResponseVo.error(4004, "主图不能为空");
        }
        
        try {
            String imageJson = productService.uploadProductImages(mainImage, subImages);
            
            logger.info("商品图片上传成功: {}", imageJson);
            
            return ResponseVo.success("上传成功", imageJson);
        } catch (Exception e) {
            logger.error("商品图片上传失败: {}", e.getMessage(), e);
            return ResponseVo.error(5000, "上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加商品
     * @param productDto 商品信息
     * @return 添加结果
     */
    @PostMapping("/products")
    public ResponseVo<?> addProduct(@RequestBody ProductAddDto productDto) {
        logger.info("添加商品: name={}", productDto.getName());
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试添加商品");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能添加商品
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试添加商品: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        // 参数验证
        if (productDto.getName() == null || productDto.getName().trim().isEmpty()) {
            return ResponseVo.error(4004, "商品名称不能为空");
        }
        if (productDto.getCategoryId() == null) {
            return ResponseVo.error(4004, "分类不能为空");
        }
        if (productDto.getPrice() == null || productDto.getPrice().doubleValue() <= 0) {
            return ResponseVo.error(4004, "价格必须大于0");
        }
        if (productDto.getStock() == null || productDto.getStock() < 0) {
            return ResponseVo.error(4004, "库存不能小于0");
        }
        
        try {
            Integer productId = productService.addProduct(productDto);
            if (productId == null) {
                return ResponseVo.error(5000, "添加商品失败");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            
            return ResponseVo.success("添加商品成功", result);
        } catch (Exception e) {
            logger.error("添加商品异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 更新商品
     * @param productId 商品ID
     * @param productDto 商品信息
     * @return 更新结果
     */
    @PutMapping("/products/{productId}")
    public ResponseVo<?> updateProduct(
            @PathVariable Integer productId, 
            @RequestBody ProductAddDto productDto) {
        
        logger.info("更新商品: productId={}", productId);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试更新商品");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能更新商品
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试更新商品: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        try {
            boolean success = productService.updateProduct(productId, productDto);
            if (!success) {
                return ResponseVo.error(5000, "更新商品失败");
            }
            
            return ResponseVo.success("更新商品成功");
        } catch (Exception e) {
            logger.error("更新商品异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 商品上架
     * @param productId 商品ID
     * @return 上架结果
     */
    @PutMapping("/products/{productId}/on_sale")
    public ResponseVo<?> onSale(@PathVariable Integer productId) {
        logger.info("商品上架: productId={}", productId);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试上架商品");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能上架商品
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试上架商品: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        try {
            boolean success = productService.onSale(productId);
            if (!success) {
                return ResponseVo.error(5000, "上架商品失败，商品不存在或状态异常");
            }
            
            return ResponseVo.success("上架商品成功");
        } catch (Exception e) {
            logger.error("上架商品异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 商品下架
     * @param productId 商品ID
     * @return 下架结果
     */
    @PutMapping("/products/{productId}/off_sale")
    public ResponseVo<?> offSale(@PathVariable Integer productId) {
        logger.info("商品下架: productId={}", productId);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试下架商品");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能下架商品
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试下架商品: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        try {
            boolean success = productService.offSale(productId);
            if (!success) {
                return ResponseVo.error(5000, "下架商品失败，商品不存在或状态异常");
            }
            
            return ResponseVo.success("下架商品成功");
        } catch (Exception e) {
            logger.error("下架商品异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 删除商品
     * @param productId 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/products/{productId}")
    public ResponseVo<?> deleteProduct(@PathVariable Integer productId) {
        logger.info("删除商品: productId={}", productId);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试删除商品");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能删除商品
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试删除商品: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        try {
            boolean success = productService.deleteProduct(productId);
            if (!success) {
                return ResponseVo.error(5000, "删除商品失败，商品不存在或状态异常");
            }
            
            return ResponseVo.success("删除商品成功");
        } catch (Exception e) {
            logger.error("删除商品异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 更新商品库存
     * @param productId 商品ID
     * @param stockMap 库存信息 {"stock": 100}
     * @return 更新结果
     */
    @PutMapping("/products/{productId}/stock")
    public ResponseVo<?> updateProductStock(
            @PathVariable Integer productId, 
            @RequestBody Map<String, Integer> stockMap) {
        
        logger.info("更新商品库存: productId={}, stockMap={}", productId, stockMap);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试更新商品库存");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能更新商品库存
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试更新商品库存: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        // 获取库存参数
        Integer stock = stockMap.get("stock");
        if (stock == null) {
            logger.warn("缺少库存参数");
            return ResponseVo.error(4004, "缺少库存参数");
        }
        
        try {
            boolean success = productService.updateStock(productId, stock);
            if (!success) {
                return ResponseVo.error(5000, "更新库存失败，商品不存在或参数错误");
            }
            
            return ResponseVo.success("更新库存成功");
        } catch (Exception e) {
            logger.error("更新库存异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 获取商品规格列表
     * @param productId 商品ID
     * @return 规格列表
     */
    @GetMapping("/products/{productId}/specifications")
    public ResponseVo<List<ProductSpecification>> getProductSpecifications(@PathVariable Integer productId) {
        logger.info("获取商品规格列表: productId={}", productId);
        
        try {
            List<ProductSpecification> specifications = productService.getProductSpecifications(productId);
            return ResponseVo.success(specifications);
        } catch (Exception e) {
            logger.error("获取商品规格列表异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 根据规格条件获取特定商品规格
     * @param productId 商品ID
     * @param specifications 规格条件，如{"color":"红色","size":"S"}
     * @return 匹配的规格信息
     */
    @PostMapping("/products/{productId}/specification")
    public ResponseVo<ProductSpecification> getProductSpecification(
            @PathVariable Integer productId,
            @RequestBody Map<String, String> specifications) {
        
        logger.info("获取商品规格: productId={}, specifications={}", productId, specifications);
        
        if (specifications == null || specifications.isEmpty()) {
            return ResponseVo.error(4004, "规格参数不能为空");
        }
        
        try {
            ProductSpecification specification = productService.getProductSpecification(productId, specifications);
            if (specification == null) {
                return ResponseVo.error(4004, "未找到匹配的商品规格");
            }
            
            return ResponseVo.success(specification);
        } catch (Exception e) {
            logger.error("获取商品规格异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 获取商品规格选项
     * @param productId 商品ID
     * @return 规格选项列表
     */
    @GetMapping("/products/{productId}/spec_options")
    public ResponseVo<List<SpecificationOption>> getSpecificationOptions(@PathVariable Integer productId) {
        logger.info("获取商品规格选项: productId={}", productId);
        
        try {
            List<SpecificationOption> options = productService.getSpecificationOptions(productId);
            return ResponseVo.success(options);
        } catch (Exception e) {
            logger.error("获取商品规格选项异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 添加商品规格
     * @param productId 商品ID
     * @param specification 规格信息
     * @return 添加结果
     */
    @PostMapping("/products/{productId}/specifications")
    public ResponseVo<?> addProductSpecification(
            @PathVariable Integer productId,
            @RequestBody ProductSpecification specification) {
        
        logger.info("添加商品规格: productId={}", productId);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试添加商品规格");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能添加商品规格
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试添加商品规格: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        // 参数验证
        if (specification.getSpecifications() == null || specification.getSpecifications().isEmpty()) {
            return ResponseVo.error(4004, "规格信息不能为空");
        }
        if (specification.getStock() == null || specification.getStock() < 0) {
            return ResponseVo.error(4004, "库存不能小于0");
        }
        
        try {
            Integer specificationId = productService.addProductSpecification(productId, specification);
            if (specificationId == null) {
                return ResponseVo.error(5000, "添加商品规格失败");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("specificationId", specificationId);
            
            return ResponseVo.success("添加商品规格成功", result);
        } catch (Exception e) {
            logger.error("添加商品规格异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 更新商品规格
     * @param specificationId 规格ID
     * @param specification 规格信息
     * @return 更新结果
     */
    @PutMapping("/specifications/{specificationId}")
    public ResponseVo<?> updateProductSpecification(
            @PathVariable Integer specificationId,
            @RequestBody ProductSpecification specification) {
        
        logger.info("更新商品规格: specificationId={}", specificationId);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试更新商品规格");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能更新商品规格
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试更新商品规格: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        // 参数验证
        if (specification.getSpecifications() != null && specification.getSpecifications().isEmpty()) {
            return ResponseVo.error(4004, "规格信息不能为空");
        }
        if (specification.getStock() != null && specification.getStock() < 0) {
            return ResponseVo.error(4004, "库存不能小于0");
        }
        
        try {
            boolean success = productService.updateProductSpecification(specificationId, specification);
            if (!success) {
                return ResponseVo.error(5000, "更新商品规格失败，规格不存在或参数错误");
            }
            
            return ResponseVo.success("更新商品规格成功");
        } catch (Exception e) {
            logger.error("更新商品规格异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 删除商品规格
     * @param specificationId 规格ID
     * @return 删除结果
     */
    @DeleteMapping("/specifications/{specificationId}")
    public ResponseVo<?> deleteProductSpecification(@PathVariable Integer specificationId) {
        logger.info("删除商品规格: specificationId={}", specificationId);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试删除商品规格");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能删除商品规格
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试删除商品规格: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        try {
            boolean success = productService.deleteProductSpecification(specificationId);
            if (!success) {
                return ResponseVo.error(5000, "删除商品规格失败，规格不存在或状态异常");
            }
            
            return ResponseVo.success("删除商品规格成功");
        } catch (Exception e) {
            logger.error("删除商品规格异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
    
    /**
     * 更新商品规格库存
     * @param specificationId 规格ID
     * @param stockMap 库存信息 {"stock": 100}
     * @return 更新结果
     */
    @PutMapping("/specifications/{specificationId}/stock")
    public ResponseVo<?> updateSpecificationStock(
            @PathVariable Integer specificationId,
            @RequestBody Map<String, Integer> stockMap) {
        
        logger.info("更新商品规格库存: specificationId={}, stockMap={}", specificationId, stockMap);
        
        // 检查用户是否已登录
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warn("未登录用户尝试更新商品规格库存");
            return ResponseVo.error(401, "请先登录");
        }
        
        // 检查用户权限，只有管理员能更新商品规格库存
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            logger.warn("普通用户尝试更新商品规格库存: username={}", currentUser.getUsername());
            return ResponseVo.error(4003, "没有权限进行此操作");
        }
        
        // 获取库存参数
        Integer stock = stockMap.get("stock");
        if (stock == null) {
            logger.warn("缺少库存参数");
            return ResponseVo.error(4004, "缺少库存参数");
        }
        
        try {
            boolean success = productService.updateSpecificationStock(specificationId, stock);
            if (!success) {
                return ResponseVo.error(5000, "更新规格库存失败，规格不存在或参数错误");
            }
            
            return ResponseVo.success("更新规格库存成功");
        } catch (Exception e) {
            logger.error("更新规格库存异常: {}", e.getMessage(), e);
            return ResponseVo.error(999, "服务器错误，请稍后重试");
        }
    }
}