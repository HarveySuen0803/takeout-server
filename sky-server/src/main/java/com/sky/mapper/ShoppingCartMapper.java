package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShoppingCartMapper {
    public Long selectIdByCondition(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where id = #{id}")
    public ShoppingCart selectById(Long id);

    void update(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);
}
