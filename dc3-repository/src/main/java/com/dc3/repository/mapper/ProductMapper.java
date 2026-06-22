package com.dc3.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc3.domain.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("<script> " +
            "SELECT id, sku_code, product_name, tenant_id, is_deleted " +
            "FROM product " +
            "WHERE tenant_id = #{tenantId} " +
            "AND sku_code IN " +
            "<foreach item='sku' collection='skuCodes' open='(' separator=',' close=')'>" +
            "#{sku}" +
            "</foreach> " +
            "AND is_deleted = 0" +
            "</script>")
    List<Product> selectBySkuCodes(@Param("tenantId") String tenantId,
                                   @Param("skuCodes") Set<String> skuCodes);
}
