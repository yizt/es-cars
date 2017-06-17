package org.es.cars.es.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by mick.yi on 2017/4/22.
 * es查询分组对象
 */
public class ESQueryGroup {
    /**
     * 分组类型
     */
    public interface GroupType {
        String LIST="list";
        String RAGNE="range";
        String DATEHISTOGRAM="date_histogram";//时间分组
        String CHILD="child";
        String CHILDFILTER="child_filter";//子type的过滤分组，加入到聚合中，结果才准确
    }
    /**
     * 分组字段
     */
    private String field;
    /**
     * 分组类型
     */
    @JSONField(name="group_type")
    private String groupType;

    public String getField() {
        return field;
    }

    public String getGroupType() {
        return groupType;
    }


    public void setField(String field) {
        this.field = field;
    }

    public ESQueryGroup(String field, String groupType) {
        this.field = field;
        this.groupType = groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

}
