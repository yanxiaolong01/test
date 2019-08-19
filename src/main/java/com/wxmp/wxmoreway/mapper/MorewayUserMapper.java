package com.wxmp.wxmoreway.mapper;

import com.wxmp.wxmoreway.domain.MorewayUser;

import java.util.List;

public interface MorewayUserMapper {

    Integer add(MorewayUser morewayUser);

    List<MorewayUser> getMorewayUserInfo(MorewayUser morewayUser);

}
