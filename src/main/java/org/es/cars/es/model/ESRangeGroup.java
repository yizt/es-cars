package org.es.cars.es.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by mick.yi on 2017/4/24.
 * 区间分组对象
 */
public class ESRangeGroup extends ESQueryGroup{
    /**
     * 分组区间定义
     */
    @JSONField(name="range_define")
    private String rangeDefine;

    public String getRangeDefine() {
        return rangeDefine;
    }

    public ESRangeGroup(String field, String groupType, String rangeDefine) {
        super(field, groupType);
        this.rangeDefine = rangeDefine;
    }
}
