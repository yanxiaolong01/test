package com.wxmp.wxmoreway.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wxmp.core.spring.SpringContextHolder;
import com.wxmp.wxapi.process.MpAccount;
import com.wxmp.wxapi.process.WxApi;
import com.wxmp.wxapi.process.WxMemoryCacheClient;
import com.wxmp.wxmoreway.service.MorewayUserService;
import com.wxmp.wxmoreway.util.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserAuthorize extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String cookieValue = CookieUtils.getCookieValue(request, "userOpenid");
        System.out.println(cookieValue);
        if (StringUtils.isBlank(cookieValue)) {
            String code = request.getParameter("code");
            //1、如果不是微信中打开则返回
            MpAccount mpAccount = WxMemoryCacheClient.getMpAccount();
            String appId = mpAccount.getAppid();
            String appSecret = mpAccount.getAppsecret();
            if (StringUtils.isNotBlank(code)) {
                MorewayUserService morewayUserService = SpringContextHolder.getBean(MorewayUserService.class);
                String result = morewayUserService.getUserInfo(code, appId, appSecret);
                JSONObject jsonObject = JSON.parseObject(result);
                System.out.println("获取到授权用户信息:" + jsonObject);
                //保存一小时cookie
                String opId = jsonObject.getString("openid");
                Cookie cookie = new Cookie("userOpenid", opId);
                cookie.setMaxAge(600);
                response.addCookie(cookie);
            } else {
                String redirectUrl = "https://www.cnovotech.com"+request.getRequestURI();
                System.out.println(redirectUrl);
                String scope = "snsapi_base";
                String state = "state";
                String url = WxApi.getOAuthCodeUrl(appId, redirectUrl, scope, state);
                response.sendRedirect(url);
            }
        }
        return super.preHandle(request, response, handler);
    }
}
