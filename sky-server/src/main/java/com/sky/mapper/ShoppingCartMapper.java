package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    public Long selectIdByCondition(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where id = #{id}")
    public ShoppingCart selectById(Long id);

    void update(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> selectByUserId(Long userId);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);
}
