package com.trading.display.query.util;

import android.util.Log;


import com.trading.display.utils.DateTimeUtil;
import com.trading.display.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 查询时日期时间转换帮助类 */
public class QueryDateTime {




    /**
     * 起始日期时间
     */
    public static String getStartTimeStamp(){
        String ssStr = "";
        //获取系统日期
        String sysDateStr = DateTimeUtil.getFormatSystemTime("yyyyMMdd");
        Log.e("获取的日期：",sysDateStr);
        ssStr = "000000";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = null;
            date = simpleDateFormat.parse(sysDateStr+ssStr);
            long ts = date.getTime();
            String stampStr = String.valueOf(ts);
            Log.e("生成的时间戳",stampStr);
            return stampStr;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 结束日期时间
     */
    public static String getEndTimeStamp(){
        //获取系统日期
        String sysDateStr = DateTimeUtil.getFormatSystemTime("yyyyMMddHHmmss");
        Log.e("获取的日期：",sysDateStr);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = null;
            date = simpleDateFormat.parse(sysDateStr);
            long ts = date.getTime();
            String stampStr = String.valueOf(ts);
            Log.e("生成的时间戳",stampStr);
            return stampStr;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
