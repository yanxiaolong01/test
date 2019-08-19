package com.wxmp.wxmoreway.model;

import lombok.Data;

import java.util.List;

@Data
public class CurrencyResult {
	private int code;

	private String message;

	private Integer count;

	private int total;

	private List list;

	private boolean result;

	private String url;

	public CurrencyResult(int code, String message, int count) {
		this.code = code;
		this.message = message;
		this.count = count;
	}

	public CurrencyResult(int code, String message, int total, List list) {
		this.code = code;
		this.message = message;
		this.total = total;
		this.list = list;
	}

	public CurrencyResult(int code, String message, int total, boolean result, String url) {
		this.code = code;
		this.message = message;
		this.total = total;
		this.result = result;
		this.url = url;
	}
}
