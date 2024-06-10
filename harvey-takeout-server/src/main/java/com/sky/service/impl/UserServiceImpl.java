package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.HttpClientConstant;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.WxConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    UserMapper userMapper;
    
    public String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put(WxConstant.APPID, weChatProperties.getAppid());
        map.put(WxConstant.SECRET, weChatProperties.getSecret());
        map.put(WxConstant.CODE, code);
        map.put(WxConstant.GRANT_TYPE, weChatProperties.getGrantType());

        String json = HttpClientUtil.doGet(HttpClientConstant.WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);

        return jsonObject.getString("openid");
    }

    // Feat: Login with WX
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        // get openid
        String openid = getOpenid(userLoginDTO.getCode());

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // get user by openid
        User user = userMapper.selectByOpenid(openid);
        
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        }

        // get token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setId(user.getId());
        userLoginVO.setOpenid(user.getOpenid());
        userLoginVO.setToken(token);

        return userLoginVO;
    }
}
