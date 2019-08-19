package com.wxmp.config;

import com.wxmp.core.interceptor.AuthInterceptor;
import com.wxmp.wxapi.interceptor.WxOAuth2Interceptor;
import com.wxmp.wxmoreway.interceptor.UserAuthorize;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfg implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加登陆拦截器
        List<String> patterns = new ArrayList<>();
        patterns.add("/**/*.js");
        patterns.add("/**/*.css");
        patterns.add("/**/*.svg");
        patterns.add("/**/*.ttf");
        patterns.add("/**/*.woff");
        patterns.add("/**/*.eot");
        patterns.add("/**/*.png");
        patterns.add("/**/*.jpg");
        patterns.add("/message/**");
        patterns.add("/front/**");
        patterns.add("/**/*.txt");
        patterns.add("/**/*.html");
        patterns.add("/moreway/**");
        patterns.add("//**");
        String[] allowUrls = {"/", "/user/logout", "/user/login", "/user/register", "/wxweb/sendmsg", "/wxapi/oauthOpenid", "/views/login.html", "/common/getverifycode"};
        AuthInterceptor authInterceptor = new AuthInterceptor();
        authInterceptor.setAllowUrls(allowUrls);
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/")
                .excludePathPatterns(patterns);
        //添加oauth拦截器
        List<String> patterns1 = new ArrayList<>();
        patterns1.add("/**/*.html");
        WxOAuth2Interceptor wxOAuth2Interceptor = new WxOAuth2Interceptor();
        wxOAuth2Interceptor.setIncludes(new String[]{"/wxweb/sendmsg.html", "/wxapi/oauthOpenid.html"});
        registry.addInterceptor(wxOAuth2Interceptor).addPathPatterns("/**/*.html").excludePathPatterns(patterns1);
        //公众号用户授权拦截
        UserAuthorize userAuthorize = new UserAuthorize();
        registry.addInterceptor(userAuthorize)
                .addPathPatterns("/moreway/**")
                .excludePathPatterns("/moreway/v1","/moreway/v1/getUserInfo","/moreway/v1/ticket","/moreway/v1/userAdd",
                        "/moreway/v1/sendMsg");
    }

    //配置首页启动
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/views/login.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}