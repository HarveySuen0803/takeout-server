package com.sky.service.impl;

import com.sky.constant.RedisConstant;
import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Integer selectStatus() {
        return (Integer) redisTemplate.opsForValue().get(RedisConstant.SHOP_STATUS);
    }

    @Override
    public void updateStatus(Integer status) {
        redisTemplate.opsForValue().set(RedisConstant.SHOP_STATUS, status);
    }
}
