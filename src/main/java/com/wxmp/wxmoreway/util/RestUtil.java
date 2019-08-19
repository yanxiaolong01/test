package com.wxmp.wxmoreway.util;

import lombok.experimental.UtilityClass;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class RestUtil {
	public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

	Logger log = LoggerFactory.getLogger(RestUtil.class);

	public static String httpGet(String url){
		String getData ;
		OkHttpClient httpClient = new OkHttpClient();
		Request request = new Request.Builder()
				.url(url)
				.build();
		try {
			Response response = httpClient.newCall(request).execute();
			getData = response.body().string();

		}catch (Exception e){
			log.info("【发送 GET 请求出现异常】！" + e.getMessage());
			return "-1";
		}
		return getData;
	}
	public static String httpPost(String url, String json){
		String postData ;
		OkHttpClient httpClient = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		try {
			Response response = httpClient.newCall(request).execute();
			postData = response.body().string();
		}catch (Exception e){
			log.info("【发送 POST 请求出现异常】！" + e.getMessage());
			return "-1";
		}
		return postData;
	}
	public static String httpPut(String url, String json){
		String postData ;
		OkHttpClient httpClient = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
				.url(url)
				.put(requestBody)
				.build();
		try {
			Response response = httpClient.newCall(request).execute();
			postData = response.body().string();
		}catch (Exception e){
			log.info("【发送 Put 请求出现异常】！" + e.getMessage());
			return "-1";
		}
		return postData;
	}
}
