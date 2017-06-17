package org.es.cars.es.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by mick.yi on 2017/4/24.
 * ES查询过滤对象
 */
public class ESQueryFilter {
    /**
     * 过滤条件类型
     */
    public interface FilterType {
        /**
         * 不包含
         */
        String REGEXP_MATCH="regexp_match";
        /**
         * 模糊匹配
         */
        String MATCH="match";
        /**
         * 精确匹配
         */
        String EQUAL="equal";
        /**
         * 多值匹配
         */
        String LIST="list";
        /**
         * 范围匹配
         */
        String RANGE="range";
    }

    /**
     * 无上界或下界表示方法
     */
    public static final String NaN="NaN";
    private String field;
    @JSONField(name="filter_type")
    private String filterType;
    @JSONField(name="match_value")
    private String matchValue;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(String matchValue) {
        this.matchValue = matchValue;
    }

    public ESQueryFilter(String field, String filterType, String matchValue) {
        this.field = field;
        this.filterType = filterType;
        this.matchValue = matchValue;
    }
}
