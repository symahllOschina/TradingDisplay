package com.trading.display.utils;


/**
 * 服务地址管理类
 *
 * 
 */
public class NitConfig {
	
	/**  打包前必看：
	 * 1，替换正式域名前缀，
	 * 4，升级版本号
	 */
	public static final String isTest = "true";//测试为 isTest = "test"正式为isTest = "true"
	
	/** 测试服务前缀 */
	public static final String basePath =  			 "https://dev.weupay.com/pay/api/qmp/100";
	public static final String queryBasePath =  	 "https://dev.weupay.com/pay/api/qmp/200";
	public static final String queryDataShowPath1 =  	 "https://dev.weupay.com/admin/api/monitor";
	public static final String queryPayTypeNumPath1 =  	 "http://test.weupay.com:8080/download/api/monitor";
	public static final String querySumHistoryPath = "http://test.weupay.com:8080/download/api/qmp/200";//test:dev
	
	/** 正式服务器 */
	public static final String queryDataShowPath =  	 "https://pay.wandingkeji.cn";
	public static final String queryPayTypeNumPath =  	 "https://download.wandingkeji.cn";

	/**
	 * 当天汇总查询
	 * 入参：startTime,endTime
	 */
	public static final String queryDataShowUrl = queryDataShowPath +   "/admin/api/monitor/1/queryDataShow";

	/**
	 * 当天汇总查询
	 * 入参：startTime,endTime
	 */
	public static final String queryPayTypeNumUrl = queryPayTypeNumPath +   "/download/api/monitor/1/dataShowByType";


	

	
	
	
	
	
	
	
	
	
	
	
	
	
}
