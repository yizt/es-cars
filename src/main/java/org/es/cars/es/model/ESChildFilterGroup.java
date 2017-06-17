package org.es.cars.es.model;

import org.elasticsearch.index.query.AbstractQueryBuilder;

/**
 * Created by mick.yi on 2017/5/2.
 * 父子查询中子type的过滤分组
 */
public class ESChildFilterGroup extends ESQueryGroup{
    private AbstractQueryBuilder builder;

    public AbstractQueryBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(AbstractQueryBuilder builder) {
        this.builder = builder;
    }

    public ESChildFilterGroup(AbstractQueryBuilder builder) {
        super("child_filter", GroupType.CHILDFILTER);
        this.builder = builder;
    }
}
