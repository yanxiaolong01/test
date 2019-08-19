package com.wxmp.wxmoreway.domain;

import lombok.Data;

import java.util.Date;

@Data
public class MorewayUser {

    private Integer id;

    private String nickName;

    private String openId;

    private String phone;

    private String country;

    private String province;

    private String city;

    private Integer sex;

    private String headImgUrl;

    private Date createTime;

    private String shareOpenId;

    private String randomStr;

}
