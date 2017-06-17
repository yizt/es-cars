package org.es.cars.es.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mick.yi on 2017/4/27.
 * es指标接口参数
 */
public class ESParam {
    /**
     * 过滤列表
     */
    private List<ESQueryFilter> filter;
    /**
     * 分组类别
     */
    private List<ESQueryGroup> group;

    public ESParam setFilter(ESQueryFilter ... filter) {
        this.filter = Arrays.asList(filter);
        return this;
    }
    public ESParam addFilter(ESQueryFilter esQueryFilter){
        if(this.filter==null)
            this.filter=new ArrayList<ESQueryFilter>();
        filter.add(esQueryFilter);
        return this;
    }

    public ESParam setGroup(ESQueryGroup ... groups) {
        this.group = Arrays.asList(groups);
        return this;
    }

    public List<ESQueryFilter> getFilter() {
        return filter;
    }

    public List<ESQueryGroup> getGroup() {
        return group;
    }
}
