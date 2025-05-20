package com.wuli.badminton.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wuli.badminton.dao.MallCategoryMapper;
import com.wuli.badminton.dao.MallProductMapper;
import com.wuli.badminton.dao.ProductSpecificationMapper;
import com.wuli.badminton.dao.SpecificationOptionMapper;
import com.wuli.badminton.dto.PageResult;
import com.wuli.badminton.dto.ProductAddDto;
import com.wuli.badminton.dto.ProductDetailDto;
import com.wuli.badminton.dto.ProductListDto;
import com.wuli.badminton.pojo.MallCategory;
import com.wuli.badminton.pojo.MallProduct;
import com.wuli.badminton.pojo.ProductSpecification;
import com.wuli.badminton.pojo.SpecificationOption;
import com.wuli.badminton.service.MallProductService;
import com.wuli.badminton.util.UploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MallProductServiceImpl implements MallProductService {

    private static final Logger logger = LoggerFactory.getLogger(MallProductServiceImpl.class);

    @Autowired
    private MallProductMapper productMapper;

    @Autowired
    private MallCategoryMapper categoryMapper;
    
    @Autowired
    private UploadUtil uploadUtil;
    
    @Autowired
    private ProductSpecificationMapper specificationMapper;
    
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult<ProductListDto> getProductList(String categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        logger.info("获取商品列表: categoryId={}, keyword={}, pageNum={}, pageSize={}, orderBy={}", 
                categoryId, keyword, pageNum, pageSize, orderBy);
        
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (categoryId != null && !"all".equals(categoryId)) {
            try {
                Integer cateId = Integer.parseInt(categoryId);
                params.put("categoryId", cateId);
            } catch (NumberFormatException e) {
                logger.warn("无效的分类ID: {}", categoryId);
                params.put("categoryId", -1); // 使用一个不存在的ID确保查不到数据
            }
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        if (orderBy != null) {
            params.put("orderBy", orderBy);
        }
        
        // 设置只查询在售商品 (状态1为在售)
        params.put("status", 1);
        
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<MallProduct> products = productMapper.findList(params);
        PageInfo<MallProduct> pageInfo = new PageInfo<>(products);
        
        // 转换为DTO
        List<ProductListDto> productDtos = products.stream().map(product -> {
            ProductListDto dto = new ProductListDto();
            BeanUtils.copyProperties(product, dto);
            return dto;
        }).collect(Collectors.toList());
        
        return PageResult.build(pageNum, pageSize, pageInfo.getTotal(), productDtos);
    }

    @Override
    public PageResult<ProductListDto> getAdminProductList(String categoryId, String keyword, Integer status, Integer pageNum, Integer pageSize, String orderBy) {
        logger.info("管理员获取商品列表: categoryId={}, keyword={}, status={}, pageNum={}, pageSize={}, orderBy={}", 
                categoryId, keyword, status, pageNum, pageSize, orderBy);
        
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (categoryId != null && !"all".equals(categoryId)) {
            try {
                Integer cateId = Integer.parseInt(categoryId);
                params.put("categoryId", cateId);
            } catch (NumberFormatException e) {
                logger.warn("无效的分类ID: {}", categoryId);
                params.put("categoryId", -1); // 使用一个不存在的ID确保查不到数据
            }
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        if (orderBy != null) {
            params.put("orderBy", orderBy);
        }
        
        // 只有在传入状态参数时才加入状态过滤条件
        if (status != null) {
            params.put("status", status);
        }
        
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<MallProduct> products = productMapper.findList(params);
        PageInfo<MallProduct> pageInfo = new PageInfo<>(products);
        
        // 转换为DTO
        List<ProductListDto> productDtos = products.stream().map(product -> {
            ProductListDto dto = new ProductListDto();
            BeanUtils.copyProperties(product, dto);
            return dto;
        }).collect(Collectors.toList());
        
        return PageResult.build(pageNum, pageSize, pageInfo.getTotal(), productDtos);
    }

    @Override
    public ProductDetailDto getProductDetail(Integer productId) {
        logger.info("获取商品详情: productId={}", productId);
        
        MallProduct product = productMapper.findById(productId);
        if (product == null) {
            logger.warn("商品不存在: productId={}", productId);
            return null;
        }
        
        // 检查商品是否已下架或删除 (状态1为在售)
        if (product.getStatus() != 1) {
            logger.warn("商品已下架或删除: productId={}, status={}", productId, product.getStatus());
            return null;
        }
        
        ProductDetailDto detailDto = new ProductDetailDto();
        BeanUtils.copyProperties(product, detailDto);
        
        // 如果商品有规格，获取规格信息
        if (product.getHasSpecification() != null && product.getHasSpecification() == 1) {
            // 获取规格列表
            List<ProductSpecification> specs = specificationMapper.findByProductId(productId);
            List<Map<String, Object>> specList = new ArrayList<>();
            
            for (ProductSpecification spec : specs) {
                Map<String, Object> specMap = new HashMap<>();
                specMap.put("id", spec.getId());
                specMap.put("specifications", spec.getSpecifications());
                specMap.put("priceAdjustment", spec.getPriceAdjustment());
                specMap.put("stock", spec.getStock());
                specList.add(specMap);
            }
            detailDto.setSpecifications(specList);
            
            // 获取规格选项
            List<SpecificationOption> options = specificationOptionMapper.findByProductId(productId);
            Map<String, List<String>> optionsMap = new HashMap<>();
            
            for (SpecificationOption option : options) {
                optionsMap.put(option.getSpecKey(), option.getSpecValues());
            }
            detailDto.setSpecOptions(optionsMap);
        }
        
        return detailDto;
    }

    @Override
    @Transactional
    public Integer addProduct(ProductAddDto productDto) {
        logger.info("添加商品: name={}", productDto.getName());
        
        // 检查分类是否存在
        MallCategory category = categoryMapper.findById(productDto.getCategoryId());
        if (category == null) {
            logger.warn("分类不存在: categoryId={}", productDto.getCategoryId());
            return null;
        }
        
        MallProduct product = new MallProduct();
        BeanUtils.copyProperties(productDto, product);
        
        // 设置分类名称
        product.setCategoryName(category.getName());
        
        // 设置初始销量
        product.setSales(0);
        
        // 如果没有指定状态，默认设置为下架状态(2)
        if (product.getStatus() == null) {
            product.setStatus(2);
        }
        
        // 插入商品
        int rows = productMapper.insert(product);
        if (rows > 0) {
            logger.info("商品添加成功: id={}", product.getId());
            
            // 如果有规格，添加规格信息
            if (productDto.getHasSpecification() != null && productDto.getHasSpecification() == 1) {
                try {
                    // 添加规格选项
                    if (productDto.getSpecOptions() != null && !productDto.getSpecOptions().isEmpty()) {
                        for (Map.Entry<String, List<String>> entry : productDto.getSpecOptions().entrySet()) {
                            SpecificationOption option = new SpecificationOption();
                            option.setProductId(product.getId());
                            option.setSpecKey(entry.getKey());
                            option.setSpecValues(entry.getValue());
                            specificationOptionMapper.insert(option);
                        }
                    }
                    
                    // 添加规格组合
                    if (productDto.getSpecifications() != null && !productDto.getSpecifications().isEmpty()) {
                        for (Map<String, Object> specMap : productDto.getSpecifications()) {
                            ProductSpecification spec = new ProductSpecification();
                            spec.setProductId(product.getId());
                            
                            @SuppressWarnings("unchecked")
                            Map<String, String> specs = (Map<String, String>) specMap.get("specs");
                            spec.setSpecifications(specs);
                            
                            BigDecimal priceAdjustment = new BigDecimal(specMap.get("priceAdjustment").toString());
                            spec.setPriceAdjustment(priceAdjustment);
                            
                            Integer stock = Integer.parseInt(specMap.get("stock").toString());
                            spec.setStock(stock);
                            
                            spec.setSales(0);
                            // 默认规格状态为在售(1)
                            spec.setStatus(1);
                            
                            specificationMapper.insert(spec);
                        }
                    }
                } catch (Exception e) {
                    logger.error("添加商品规格失败: {}", e.getMessage(), e);
                    throw new RuntimeException("添加商品规格失败", e);
                }
            }
            
            return product.getId();
        } else {
            logger.error("商品添加失败");
            return null;
        }
    }

    @Override
    @Transactional
    public boolean updateProduct(Integer productId, ProductAddDto productDto) {
        logger.info("更新商品: productId={}", productId);
        
        // 检查商品是否存在
        MallProduct product = productMapper.findById(productId);
        if (product == null) {
            logger.warn("商品不存在: productId={}", productId);
            return false;
        }
        
        // 检查分类是否存在
        if (productDto.getCategoryId() != null && !productDto.getCategoryId().equals(product.getCategoryId())) {
            MallCategory category = categoryMapper.findById(productDto.getCategoryId());
            if (category == null) {
                logger.warn("分类不存在: categoryId={}", productDto.getCategoryId());
                return false;
            }
            product.setCategoryName(category.getName());
        }
        
        // 更新商品信息
        BeanUtils.copyProperties(productDto, product);
        product.setId(productId);
        
        int rows = productMapper.update(product);
        if (rows > 0) {
            // 如果修改了规格信息
            if (productDto.getHasSpecification() != null) {
                try {
                    // 如果新增了规格，或者修改了规格
                    if (productDto.getHasSpecification() == 1) {
                        // 先删除原有规格和选项
                        specificationMapper.deleteByProductId(productId);
                        specificationOptionMapper.deleteByProductId(productId);
                        
                        // 添加规格选项
                        if (productDto.getSpecOptions() != null && !productDto.getSpecOptions().isEmpty()) {
                            for (Map.Entry<String, List<String>> entry : productDto.getSpecOptions().entrySet()) {
                                SpecificationOption option = new SpecificationOption();
                                option.setProductId(productId);
                                option.setSpecKey(entry.getKey());
                                option.setSpecValues(entry.getValue());
                                specificationOptionMapper.insert(option);
                            }
                        }
                        
                        // 添加规格组合
                        if (productDto.getSpecifications() != null && !productDto.getSpecifications().isEmpty()) {
                            for (Map<String, Object> specMap : productDto.getSpecifications()) {
                                ProductSpecification spec = new ProductSpecification();
                                spec.setProductId(productId);
                                
                                @SuppressWarnings("unchecked")
                                Map<String, String> specs = (Map<String, String>) specMap.get("specs");
                                spec.setSpecifications(specs);
                                
                                BigDecimal priceAdjustment = new BigDecimal(specMap.get("priceAdjustment").toString());
                                spec.setPriceAdjustment(priceAdjustment);
                                
                                Integer stock = Integer.parseInt(specMap.get("stock").toString());
                                spec.setStock(stock);
                                
                                spec.setSales(0);
                                spec.setStatus(1);
                                
                                specificationMapper.insert(spec);
                            }
                        }
                    } else if (productDto.getHasSpecification() == 0) {
                        // 如果取消了规格，删除所有规格和选项
                        specificationMapper.deleteByProductId(productId);
                        specificationOptionMapper.deleteByProductId(productId);
                    }
                } catch (Exception e) {
                    logger.error("更新商品规格失败: {}", e.getMessage(), e);
                    throw new RuntimeException("更新商品规格失败", e);
                }
            }
            
            return true;
        }
        
        return false;
    }

    @Override
    public boolean onSale(Integer productId) {
        logger.info("商品上架: productId={}", productId);
        
        // 检查商品是否存在
        MallProduct product = productMapper.findById(productId);
        if (product == null) {
            logger.warn("商品不存在: productId={}", productId);
            return false;
        }
        
        // 已经是上架状态(1)
        if (product.getStatus() == 1) {
            logger.info("商品已经是上架状态: productId={}", productId);
            return true;
        }
        
        // 如果是删除状态(3)，不允许直接上架
        if (product.getStatus() == 3) {
            logger.warn("被删除的商品不能上架: productId={}", productId);
            return false;
        }
        
        // 更新为上架状态(1)
        int rows = productMapper.updateStatus(productId, 1);
        return rows > 0;
    }

    @Override
    public boolean offSale(Integer productId) {
        logger.info("商品下架: productId={}", productId);
        
        // 检查商品是否存在
        MallProduct product = productMapper.findById(productId);
        if (product == null) {
            logger.warn("商品不存在: productId={}", productId);
            return false;
        }
        
        // 已经是下架状态(2)
        if (product.getStatus() == 2) {
            logger.info("商品已经是下架状态: productId={}", productId);
            return true;
        }
        
        // 如果是删除状态(3)，不允许下架
        if (product.getStatus() == 3) {
            logger.warn("被删除的商品不能下架: productId={}", productId);
            return false;
        }
        
        // 更新为下架状态(2)
        int rows = productMapper.updateStatus(productId, 2);
        return rows > 0;
    }

    @Override
    public boolean deleteProduct(Integer productId) {
        logger.info("删除商品: productId={}", productId);
        
        // 检查商品是否存在
        MallProduct product = productMapper.findById(productId);
        if (product == null) {
            logger.warn("商品不存在: productId={}", productId);
            return false;
        }
        
        // 已经是删除状态(3)
        if (product.getStatus() == 3) {
            logger.info("商品已经是删除状态: productId={}", productId);
            return true;
        }
        
        // 更新为删除状态(3)
        int rows = productMapper.updateStatus(productId, 3);
        return rows > 0;
    }

    @Override
    public boolean updateStock(Integer productId, Integer stock) {
        logger.info("更新商品库存: productId={}, stock={}", productId, stock);
        
        // 检查商品是否存在
        MallProduct product = productMapper.findById(productId);
        if (product == null) {
            logger.warn("商品不存在: productId={}", productId);
            return false;
        }
        
        // 库存不能小于0
        if (stock < 0) {
            logger.warn("库存不能小于0: stock={}", stock);
            return false;
        }
        
        int rows = productMapper.updateStock(productId, stock);
        return rows > 0;
    }

    @Override
    public String uploadProductImages(MultipartFile mainImage, MultipartFile[] subImages) throws Exception {
        logger.info("上传商品图片");
        return uploadUtil.uploadProductImages(mainImage, subImages);
    }
    
    @Override
    public List<ProductSpecification> getProductSpecifications(Integer productId) {
        logger.info("获取商品规格列表: productId={}", productId);
        return specificationMapper.findByProductId(productId);
    }
    
    @Override
    public ProductSpecification getProductSpecification(Integer productId, Map<String, String> specifications) {
        logger.info("获取商品规格: productId={}, specifications={}", productId, specifications);
        return specificationMapper.findByProductIdAndSpecifications(productId, specifications);
    }
    
    @Override
    @Transactional
    public Integer addProductSpecification(Integer productId, ProductSpecification specification) {
        logger.info("添加商品规格: productId={}", productId);
        
        // 检查商品是否存在
        MallProduct product = productMapper.findById(productId);
        if (product == null) {
            logger.warn("商品不存在: productId={}", productId);
            return null;
        }
        
        // 设置商品ID
        specification.setProductId(productId);
        
        // 如果没有设置销量，设置初始销量为0
        if (specification.getSales() == null) {
            specification.setSales(0);
        }
        
        // 如果没有设置状态，设置为正常
        if (specification.getStatus() == null) {
            specification.setStatus(1);
        }
        
        // 插入规格
        int rows = specificationMapper.insert(specification);
        if (rows > 0) {
            // 更新商品设置为有规格
            if (product.getHasSpecification() == null || product.getHasSpecification() != 1) {
                product.setHasSpecification(1);
                productMapper.update(product);
            }
            
            logger.info("商品规格添加成功: id={}", specification.getId());
            return specification.getId();
        } else {
            logger.error("商品规格添加失败");
            return null;
        }
    }
    
    @Override
    @Transactional
    public boolean updateProductSpecification(Integer specificationId, ProductSpecification specification) {
        logger.info("更新商品规格: specificationId={}", specificationId);
        
        // 检查规格是否存在
        ProductSpecification existingSpec = specificationMapper.findById(specificationId);
        if (existingSpec == null) {
            logger.warn("商品规格不存在: specificationId={}", specificationId);
            return false;
        }
        
        // 设置规格ID
        specification.setId(specificationId);
        
        // 更新规格
        int rows = specificationMapper.update(specification);
        return rows > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteProductSpecification(Integer specificationId) {
        logger.info("删除商品规格: specificationId={}", specificationId);
        
        // 检查规格是否存在
        ProductSpecification specification = specificationMapper.findById(specificationId);
        if (specification == null) {
            logger.warn("商品规格不存在: specificationId={}", specificationId);
            return false;
        }
        
        // 删除规格
        int rows = specificationMapper.deleteById(specificationId);
        if (rows > 0) {
            // 检查商品是否还有其他规格
            List<ProductSpecification> specifications = specificationMapper.findByProductId(specification.getProductId());
            if (specifications == null || specifications.isEmpty()) {
                // 如果没有其他规格，更新商品设置为无规格
                MallProduct product = productMapper.findById(specification.getProductId());
                if (product != null && product.getHasSpecification() != null && product.getHasSpecification() == 1) {
                    product.setHasSpecification(0);
                    productMapper.update(product);
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public List<SpecificationOption> getSpecificationOptions(Integer productId) {
        logger.info("获取商品规格选项: productId={}", productId);
        return specificationOptionMapper.findByProductId(productId);
    }
    
    @Override
    public boolean updateSpecificationStock(Integer specificationId, Integer stock) {
        logger.info("更新商品规格库存: specificationId={}, stock={}", specificationId, stock);
        
        // 检查规格是否存在
        ProductSpecification specification = specificationMapper.findById(specificationId);
        if (specification == null) {
            logger.warn("商品规格不存在: specificationId={}", specificationId);
            return false;
        }
        
        // 库存不能小于0
        if (stock < 0) {
            logger.warn("库存不能小于0: stock={}", stock);
            return false;
        }
        
        // 更新库存
        int rows = specificationMapper.updateStock(specificationId, stock);
        return rows > 0;
    }
} 