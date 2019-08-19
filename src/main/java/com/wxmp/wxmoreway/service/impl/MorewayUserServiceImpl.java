package com.wxmp.wxmoreway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wxmp.wxapi.process.WxApi;
import com.wxmp.wxmoreway.domain.MorewayUser;
import com.wxmp.wxmoreway.mapper.MorewayUserMapper;
import com.wxmp.wxmoreway.model.CurrencyResult;
import com.wxmp.wxmoreway.service.MorewayUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MorewayUserServiceImpl implements MorewayUserService {

    @Autowired
    private MorewayUserMapper morewayUserMapper;

    @Override
    public Integer add(MorewayUser morewayUser) {
        Integer result = morewayUserMapper.add(morewayUser);
        return result;
    }

    @Override
    public String getUserInfo(String code, String APP_ID, String APP_SECRET) {
        String url = WxApi.getOAuthTokenUrl(APP_ID, APP_SECRET, code);
        JSONObject jsonObj = WxApi.httpsRequest(url, "GET", null);
        String openId = (String)jsonObj.get("openid");
        String accessToken = (String)jsonObj.get("access_token");
        String url1 = WxApi.getOAuthUserinfoUrl(accessToken, openId);
        JSONObject jsonObj1 = WxApi.httpsRequest(url1, "GET", null);
        return jsonObj1.toJSONString();
    }

    @Override
    public CurrencyResult userAdd(MorewayUser morewayUser) {
        MorewayUser mu = new MorewayUser();
        mu.setPhone(morewayUser.getPhone());
        List<MorewayUser> morewayUsers = morewayUserMapper.getMorewayUserInfo(mu);
        if(morewayUsers.isEmpty()){
            Integer result = morewayUserMapper.add(morewayUser);
            return new CurrencyResult(200, "添加成功", result);
        }else{
            return new CurrencyResult(300, "该手机号已被绑定", 0);
        }
    }

    @Override
    public List<MorewayUser> getMorewayUserInfo(MorewayUser morewayUser) {
        List<MorewayUser> result = morewayUserMapper.getMorewayUserInfo(morewayUser);
        return result;
    }
}
