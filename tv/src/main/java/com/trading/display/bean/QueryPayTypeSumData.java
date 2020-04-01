package com.trading.display.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 交易类型笔数以及商户数
 */
public class QueryPayTypeSumData implements Serializable{

    private Integer empCount;//商户数  ": 67,
    private List<QueryPayTypeDetailData> dataList;//各交易方式汇总集合

    public QueryPayTypeSumData() {
    }

    public Integer getEmpCount() {
        return empCount;
    }

    public void setEmpCount(Integer empCount) {
        this.empCount = empCount;
    }

    public List<QueryPayTypeDetailData> getDataList() {
        return dataList;
    }

    public void setDataList(List<QueryPayTypeDetailData> dataList) {
        this.dataList = dataList;
    }
}
