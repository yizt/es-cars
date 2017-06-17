package org.es.cars.web.api;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.es.framework.utils.ESQueryUtils;
import org.es.cars.es.model.ESQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by mick.yi on 2017/6/6.
 */
public class BaseController {
    @Resource
    protected ElasticsearchTemplate elasticsearchTemplate;
    @Value("${index.name}")
    protected String index;

    /**
     * 通用聚合查询处理(含有子type)
     * @param body                  restful查询接口参数
     * @param esQuery               包装的es查询信息
     * @param aggBuilder            自定义聚合
     * @param preFilter             预处理过滤
     * @param postFilter            后处理过滤
     * @return
     */
    public String dealAllHasChildType(String body, ESQuery esQuery, AbstractAggregationBuilder aggBuilder, Consumer<List> preFilter, Function<AbstractQueryBuilder,AbstractQueryBuilder> postFilter) {
        return ESQueryUtils.dealAllHasChildType(index,elasticsearchTemplate,body,esQuery,aggBuilder,preFilter,postFilter);
    }

        /**
         * 通用聚合查询处理(含有子type)
         * @param body                  restful查询接口参数
         * @param esQuery               包装的es查询信息
         * @param aggBuilder            自定义聚合
         * @param preFilter             预处理过滤
         * @return
         */
    public String dealAllHasChildType(String body, ESQuery esQuery, AbstractAggregationBuilder aggBuilder, Consumer<List> preFilter) {
        return ESQueryUtils.dealAllHasChildType(index,elasticsearchTemplate,body,esQuery,aggBuilder,preFilter,null);
    }

    /**
     * 通用聚合查询处理(含有子type)
     * @param body                  restful查询接口参数
     * @param esQuery               包装的es查询信息
     * @param aggBuilder            自定义聚合
     * @param postFilter            后处理过滤
     * @return
     */
    public String dealAllHasChildType(String body, ESQuery esQuery, AbstractAggregationBuilder aggBuilder, Function<AbstractQueryBuilder,AbstractQueryBuilder> postFilter) {
        return ESQueryUtils.dealAllHasChildType(index,elasticsearchTemplate,body,esQuery,aggBuilder,null,postFilter);
    }
    /**
     * 通用聚合查询处理(含有子type)
     * @param body                  restful查询接口参数
     * @param esQuery               包装的es查询信息
     * @param aggBuilder            自定义聚合
     * @return
     */
    public String dealAllHasChildType(String body, ESQuery esQuery, AbstractAggregationBuilder aggBuilder) {
        return ESQueryUtils.dealAllHasChildType(index,elasticsearchTemplate,body,esQuery,aggBuilder,null,null);
    }

    /**
     * 通用聚合查询处理(单个type)
     * @param body                  restful查询接口参数
     * @param aggBuilder            自定义聚合
     * @return
     */
    public String dealAllSingleType(String typeName,String body, AbstractQueryBuilder filterBuilder,AbstractAggregationBuilder aggBuilder) {
        return ESQueryUtils.dealAllSingleType(index,elasticsearchTemplate,typeName,body,filterBuilder,aggBuilder);
    }
    /**
     * 通用聚合查询处理(单个type)
     * @param body                  restful查询接口参数
     * @param aggBuilder            自定义聚合
     * @return
     */
    public String dealAllSingleType(String typeName,String body, AbstractAggregationBuilder aggBuilder) {
        return ESQueryUtils.dealAllSingleType(index,elasticsearchTemplate,typeName,body,null,aggBuilder);
    }


}
