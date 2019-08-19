package com.wxmp.wxmoreway.service;

import com.wxmp.wxmoreway.domain.MorewayUser;
import com.wxmp.wxmoreway.model.CurrencyResult;

import java.util.List;

public interface MorewayUserService {

    Integer add(MorewayUser morewayUser);

    String getUserInfo(String code, String APP_ID, String APP_SECRET);

    CurrencyResult userAdd(MorewayUser morewayUser);

    List<MorewayUser> getMorewayUserInfo(MorewayUser morewayUser);
}
