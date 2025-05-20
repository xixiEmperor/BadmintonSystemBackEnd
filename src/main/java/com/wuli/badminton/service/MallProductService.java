package com.wuli.badminton.service;

import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.ProductAddDto;
import com.wuli.badminton.dto.ProductDetailDto;
import com.wuli.badminton.dto.ProductListDto;
import com.wuli.badminton.pojo.ProductSpecification;
import com.wuli.badminton.pojo.SpecificationOption;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MallProductService {
    
    /**
     * 获取商品列表
     * @param categoryId 分类ID，为"all"时表示所有分类
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param orderBy 排序方式：price_asc, price_desc, sales_desc
     * @return 分页的商品列表
     */
    PageResult<ProductListDto> getProductList(String categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy);
    
    /**
     * 管理员获取商品列表（包括全部状态）
     * @param categoryId 分类ID，为"all"时表示所有分类
     * @param keyword 搜索关键词
     * @param status 状态（null表示所有状态）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param orderBy 排序方式：price_asc, price_desc, sales_desc
     * @return 分页的商品列表
     */
    PageResult<ProductListDto> getAdminProductList(String categoryId, String keyword, Integer status, Integer pageNum, Integer pageSize, String orderBy);
    
    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductDetailDto getProductDetail(Integer productId);
    
    /**
     * 添加商品
     * @param productDto 商品信息
     * @return 添加成功返回商品ID，失败返回null
     */
    Integer addProduct(ProductAddDto productDto);
    
    /**
     * 更新商品
     * @param productId 商品ID
     * @param productDto 商品信息
     * @return 更新成功返回true，否则返回false
     */
    boolean updateProduct(Integer productId, ProductAddDto productDto);
    
    /**
     * 商品上架
     * @param productId 商品ID
     * @return 上架成功返回true，否则返回false
     */
    boolean onSale(Integer productId);
    
    /**
     * 商品下架
     * @param productId 商品ID
     * @return 下架成功返回true，否则返回false
     */
    boolean offSale(Integer productId);
    
    /**
     * 删除商品
     * @param productId 商品ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteProduct(Integer productId);
    
    /**
     * 更新商品库存
     * @param productId 商品ID
     * @param stock 库存
     * @return 更新成功返回true，否则返回false
     */
    boolean updateStock(Integer productId, Integer stock);
    
    /**
     * 上传商品图片
     * @param mainImage 主图
     * @param subImages 子图
     * @return 上传成功返回JSON字符串，包含图片URL
     */
    String uploadProductImages(MultipartFile mainImage, MultipartFile[] subImages) throws Exception;
    
    /**
     * 根据商品ID获取所有商品规格
     * @param productId 商品ID
     * @return 规格列表
     */
    List<ProductSpecification> getProductSpecifications(Integer productId);
    
    /**
     * 根据商品ID和规格条件查询具体规格
     * @param productId 商品ID
     * @param specifications 规格条件，如{color=红色, size=S}
     * @return 规格信息
     */
    ProductSpecification getProductSpecification(Integer productId, Map<String, String> specifications);
    
    /**
     * 添加商品规格
     * @param productId 商品ID
     * @param specification 规格信息
     * @return 添加成功返回规格ID，失败返回null
     */
    Integer addProductSpecification(Integer productId, ProductSpecification specification);
    
    /**
     * 更新商品规格
     * @param specificationId 规格ID
     * @param specification 规格信息
     * @return 更新成功返回true，否则返回false
     */
    boolean updateProductSpecification(Integer specificationId, ProductSpecification specification);
    
    /**
     * 删除商品规格
     * @param specificationId 规格ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteProductSpecification(Integer specificationId);
    
    /**
     * 获取商品规格选项
     * @param productId 商品ID
     * @return 规格选项列表
     */
    List<SpecificationOption> getSpecificationOptions(Integer productId);
    
    /**
     * 更新商品规格库存
     * @param specificationId 规格ID
     * @param stock 库存
     * @return 更新成功返回true，否则返回false
     */
    boolean updateSpecificationStock(Integer specificationId, Integer stock);
} 