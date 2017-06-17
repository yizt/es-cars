package org.es.framework.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.InternalMultiBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.InternalSingleBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.scripted.InternalScriptedMetric;
import org.es.framework.util.json.ResultSet;
import org.es.cars.es.model.*;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mick.yi on 2017/4/22.
 * elasticsearch 指标查询接口工具类型
 */
public class ESQueryUtils {

    /**
     * 获取指标接口查询对象ESQueryFilter列表
     *
     * @param filters
     * @return
     */
    private static Stream<ESQueryFilter> getESQueryFilters(Stream<Map<Object, Object>> filters) {
        return filters.map(filterElem -> {
            String field = filterElem.get("field").toString();
            String filterType = filterElem.get("filter_type").toString();
            String matchValue = filterElem.get("match_value").toString();
            return new ESQueryFilter(field, filterType, matchValue);
        });
    }

    /**
     * 获取指标接口查询对象ESQueryFilter列表
     *
     * @param filters
     * @return
     */
    public static Stream<ESQueryFilter> getESQueryFilters(List<Map<Object, Object>> filters) {
        if (filters == null || filters.size() == 0)
            return Stream.empty();
        else
            return getESQueryFilters(filters.stream());
    }

    /**
     * 获取QueryBuilder列表
     *
     * @param filterStream
     * @return
     */
    public static Stream<AbstractQueryBuilder> getESQueryBuilders(Stream<ESQueryFilter> filterStream) {
        return filterStream.map(esQueryFilter -> {
            AbstractQueryBuilder queryBuilder = null;
            switch (esQueryFilter.getFilterType()) {
                case ESQueryFilter.FilterType.REGEXP_MATCH://正则表达式
                    queryBuilder = QueryBuilders.regexpQuery(esQueryFilter.getField(), esQueryFilter.getMatchValue());
                    break;
                case ESQueryFilter.FilterType.EQUAL://精确匹配
                    queryBuilder = QueryBuilders.termQuery(esQueryFilter.getField(), esQueryFilter.getMatchValue());
                    break;
                case ESQueryFilter.FilterType.MATCH://模糊匹配
                    queryBuilder = QueryBuilders.wildcardQuery(esQueryFilter.getField(), "*" + esQueryFilter.getMatchValue() + "*");
                    break;
                case ESQueryFilter.FilterType.LIST://多值匹配
                    String[] values = JSONArray.parseObject(esQueryFilter.getMatchValue(), String[].class);
                    queryBuilder = QueryBuilders.termsQuery(esQueryFilter.getField(), values);
                    break;
                case ESQueryFilter.FilterType.RANGE://范围区间匹配
                    queryBuilder = QueryBuilders.rangeQuery(esQueryFilter.getField());
                    String[] ranges = esQueryFilter.getMatchValue().split(";");
                    if (!ESQueryFilter.NaN.equals(ranges[0])) //有下界
                        ((RangeQueryBuilder) queryBuilder).gte(ranges[0]);
                    if (!ESQueryFilter.NaN.equals(ranges[1])) //有上界
                        ((RangeQueryBuilder) queryBuilder).lt(ranges[1]);
                    break;
                default:
                    break;
            }
            return queryBuilder;
        });
    }

    /**
     * 获取最终的查询QueryBuilder
     *
     * @param filters
     * @return
     */
    public static BoolQueryBuilder transformFilter(Stream<AbstractQueryBuilder> filters) {
        //查询条件
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        filters.forEach(filter -> bqb.must(filter));
        return bqb;
    }

    /**
     * 组装几个方法
     * 获取查询的QueryBuilder
     *
     * @param filters
     * @return
     */
    public static AbstractQueryBuilder getQueryBuilder(List<Map<Object, Object>> filters) {
        //过滤条件
        return transformFilter(getESQueryBuilders(getESQueryFilters(filters.stream())));
    }

    /**
     * 组装几个方法
     * 获取查询的QueryBuilder
     *
     * @param filters
     * @return
     */
    public static AbstractQueryBuilder getQueryBuilder(List<Map<Object, Object>> filters, ESQuery esQuery) {
        List<ESQueryFilter> orgin = getESQueryFilters(filters.stream()).collect(Collectors.toList());
        List<ESQueryFilter> filter = orgin.stream().filter(x -> esQuery.getDimensions().contains(x.getField())).collect(Collectors.toList());
        List<ESQueryFilter> childFilter = orgin.stream().filter(x -> esQuery.getChildDims().contains(x.getField())).collect(Collectors.toList());

        if (childFilter.size() > 0) {
            BoolQueryBuilder build = transformFilter(getESQueryBuilders(filter.stream()));
            BoolQueryBuilder childBuild = transformFilter(getESQueryBuilders(childFilter.stream()));
            build.must(QueryBuilders.hasChildQuery(esQuery.getChildType(), childBuild, ScoreMode.Total));
            return build;
        } else
            return transformFilter(getESQueryBuilders(filter.stream()));
    }

    /**
     * 将接口的esjson对象转为ESQueryGroup
     *
     * @param groups group传入参数
     * @return ESQueryGroup对象列表
     */
    public static Stream<ESQueryGroup> getESQueryGroups(List<Map<Object, Object>> groups) {
        if (groups != null && groups.size() > 0)
            return getESQueryGroups(groups.stream());
        else return Stream.empty();
    }

    /**
     * 将接口的esjson对象转为ESQueryGroup
     *
     * @param groups group传入参数
     * @return ESQueryGroup对象列表
     */
    public static Stream<ESQueryGroup> getESQueryGroups(Stream<Map<Object, Object>> groups) {
        return groups.map(groupElem -> {
            String groupType = groupElem.get("group_type").toString();
            String field = groupElem.get("field").toString();
            ESQueryGroup esQueryGroup = null;
            switch (groupType) {
                case ESQueryGroup.GroupType.LIST:
                    esQueryGroup = new ESQueryGroup(field, groupType);
                    break;
                case ESQueryGroup.GroupType.RAGNE:
                    String rangeDefine = groupElem.get("range_define").toString();
                    esQueryGroup = new ESRangeGroup(field, groupType, rangeDefine);
                    break;
                case ESQueryGroup.GroupType.DATEHISTOGRAM:
                    String interval = groupElem.get("interval").toString();
                    String format = groupElem.get("format").toString();
                    esQueryGroup = new ESDateGroup(field, interval, format);
                    break;
                default:
                    break;
            }
            return esQueryGroup;
        });
    }

    /**
     * 将接口的esjson对象转为ESQueryGroup
     *
     * @param groups  group传入参数
     * @param filters filter传入参数
     * @return ESQueryGroup对象列表
     */
    public static Stream<ESQueryGroup> getESQueryGroups(List<Map<Object, Object>> groups, List<Map<Object, Object>> filters, ESQuery esQuery) {
        List<ESQueryGroup> orgin = getESQueryGroups(groups).collect(Collectors.toList());
        //拆分合并父子分组
        Stream<ESQueryGroup> aggs = orgin.stream().filter(x -> esQuery.getDimensions().contains(x.getField()));
        Stream<ESQueryGroup> childAggs = orgin.stream().filter(x -> esQuery.getChildDims().contains(x.getField()));

        ESQueryGroup child = new ESQueryGroup(esQuery.getChildType(), ESQueryGroup.GroupType.CHILD);
        Stream<ESQueryGroup> result = Stream.concat(aggs, Stream.of(child));
        //处理过滤中有子过滤的情况，子过滤必须加入的分组中
        List<ESQueryFilter> orginFilter = getESQueryFilters(filters.stream()).collect(Collectors.toList());
        Stream<ESQueryFilter> childFilter = orginFilter.stream().filter(x -> esQuery.getChildDims().contains(x.getField()));
        List<AbstractQueryBuilder> childFilterBuilders = getESQueryBuilders(childFilter).collect(Collectors.toList());
        //有子type的过滤,加入到分组中
        if (childFilterBuilders.size() > 0) {
            ESQueryGroup childFilterAgg = new ESChildFilterGroup(transformFilter(childFilterBuilders.stream()));
            result = Stream.concat(result, Stream.of(childFilterAgg));
        }
        //最后增加子聚合分组
        result = Stream.concat(result, childAggs);
        return result;
    }

    /**
     * 将ESQueryGroup列表 转为AggregationBuilder列表
     *
     * @param groupStream
     * @return
     */
    public static Stream<AbstractAggregationBuilder> getAggregationBuilders(Stream<ESQueryGroup> groupStream) {
        return groupStream.map(esQueryGroup -> {
            AbstractAggregationBuilder aggBuilder = null;
            String field = esQueryGroup.getField();
            switch (esQueryGroup.getGroupType()) {
                case ESQueryGroup.GroupType.RAGNE:
                    String[] ranges = ((ESRangeGroup) esQueryGroup).getRangeDefine().split(";");
                    if (ranges.length >= 1) {
                        aggBuilder = AggregationBuilders.range(field).field(field);
                        ((RangeAggregationBuilder) aggBuilder).addUnboundedTo(Double.valueOf(ranges[0]));
                        ((RangeAggregationBuilder) aggBuilder).addUnboundedFrom(Double.valueOf(ranges[ranges.length - 1]));
                    }
                    for (int i = 0; i < ranges.length - 1; i++) {
                        ((RangeAggregationBuilder) aggBuilder).addRange(Double.valueOf(ranges[i]), Double.valueOf(ranges[i + 1]));
                    }
                    break;
                case ESQueryGroup.GroupType.DATEHISTOGRAM:
                    ESDateGroup dateGroup = (ESDateGroup) esQueryGroup;
                    aggBuilder = AggregationBuilders.dateHistogram(field).field(field).
                            dateHistogramInterval(new DateHistogramInterval(dateGroup.getInterval())).
                            format(dateGroup.getFormat());
                    break;
                case ESQueryGroup.GroupType.CHILD:
                    aggBuilder = AggregationBuilders.children(field, field);
                    break;
                case ESQueryGroup.GroupType.CHILDFILTER:
                    aggBuilder = AggregationBuilders.filter(field, ((ESChildFilterGroup) esQueryGroup).getBuilder());
                    break;
                default:
                    aggBuilder = AggregationBuilders.terms(field).field(field);
                    break;
            }
            return aggBuilder;
        });
    }

    /**
     * 将多个分组条件串到一起
     *
     * @param aggStream
     * @return
     */
    public static AbstractAggregationBuilder tansformGroup(Stream<AbstractAggregationBuilder> aggStream) {
        AbstractAggregationBuilder[] aggs = aggStream.toArray(AbstractAggregationBuilder[]::new);
        for (int i = aggs.length - 1; i >= 1; i--) {
            aggs[i - 1].subAggregation(aggs[i]);
        }
        return aggs[0];
    }

    /**
     * 从参数直接获取分组的列表
     *
     * @param groups
     * @return
     */
    public static Stream<AbstractAggregationBuilder> getAggregationBuilders(List<Map<Object, Object>> groups, List<Map<Object, Object>> filters, ESQuery esQuery) {
        return getAggregationBuilders(getESQueryGroups(groups, filters, esQuery));
    }

    /**
     * 从参数直接获取分组的列表
     *
     * @param groups
     * @return
     */
    public static Stream<AbstractAggregationBuilder> getAggregationBuilders(List<Map<Object, Object>> groups) {
        return getAggregationBuilders(getESQueryGroups(groups));
    }

    /**
     * 指标接口通用获取返回值方法,处理父子关系的分组聚合中，返回顺序保持与调用的分组一致
     *
     * @param aggs
     * @param orderList 分组字段排序
     * @return
     */
    public static List<LinkedHashMap<String, String>> parse(Aggregations aggs, List<String> orderList) {
        List<LinkedHashMap<String, String>> orgin = parse(aggs).collect(Collectors.toList());
        if (orderList != null && orderList.size() >= 2 && orgin.size() >= 1) {//分组字段个数大于两个
            Stream<LinkedHashMap<String, String>> dest = orgin.stream().map(elem -> {
                LinkedHashMap<String, String> destElem = new LinkedHashMap<String, String>();
                for (String field : orderList) {
                    destElem.put(field, elem.get(field));
                }
                destElem.put("value", elem.get("value"));
                return destElem;
            });
            return dest.collect(Collectors.toList());
        } else
            return orgin;
    }

    /**
     * 指标接口通用获取返回值方法,通过此方法包装下，解决json解析出现$ref的问题
     *
     * @param aggs
     * @return
     */
    public static Stream<LinkedHashMap<String, String>> parse(Aggregations aggs) {
        return ESQueryUtils.parse(new LinkedHashMap<String, String>(), aggs).
                map(x -> JSONArray.parseObject(JSONArray.toJSONString(x), LinkedHashMap.class));
    }

    /**
     * 指标接口通用获取返回值方法
     *
     * @param prefix 前缀
     * @param aggs
     * @return LinkedHashMap的列表
     * 一个LinkedHashMap解析后如：{"inp_dept_name":"老年病学科","sex_name":"男","area_name":"十四病室一病区","value":"75"}
     */
    private static Stream<LinkedHashMap<String, String>> parse(LinkedHashMap<String, String> prefix, Aggregations aggs) {
        if (aggs == null || aggs.asList() == null || aggs.asList().size() == 0)
            return Stream.of(prefix);
        else if (aggs.asList().get(0) instanceof InternalSingleBucketAggregation) {//单桶(Filter，Children etc)
            Aggregations interAggs = ((InternalSingleBucketAggregation) aggs.asList().get(0)).getAggregations();
            if (interAggs == null || interAggs.asList() == null || interAggs.asList().size() == 0) {//没有子聚合
                prefix.put("value", String.valueOf(((InternalSingleBucketAggregation) aggs.asList().get(0)).getDocCount()));
                return Stream.of(prefix);
            } else {//有子聚合
                return parse(prefix, interAggs);
            }
        } else if (aggs.asList().get(0) instanceof InternalMultiBucketAggregation) {//多桶分组聚合
            InternalMultiBucketAggregation multiAggs = ((InternalMultiBucketAggregation) aggs.asList().get(0));
            String field = multiAggs.getName();
            List<? extends InternalMultiBucketAggregation.Bucket> buckets = multiAggs.getBuckets();
            return buckets.stream().map(bucket -> {
                prefix.put(field, bucket.getKeyAsString());
                Aggregations interAggs = bucket.getAggregations();
                if (interAggs == null || interAggs.asList() == null || interAggs.asList().size() == 0) {//没有子聚合
                    prefix.put("value", String.valueOf(bucket.getDocCount()));
                    return Stream.of(prefix);
                } else {//有子聚合
                    return parse(prefix, bucket.getAggregations());
                }
            }).flatMap(x -> x);
        } else if (aggs.asList().get(0) instanceof InternalNumericMetricsAggregation.SingleValue) {//单值聚合，如：sum，avg等
            double value = ((InternalNumericMetricsAggregation.SingleValue) aggs.asList().get(0)).value();
            prefix.put("value", String.valueOf(value));
            return Stream.of(prefix);
        } else if (aggs.asList().get(0) instanceof InternalScriptedMetric) {//脚本聚合
            double value = (Double) ((InternalScriptedMetric) aggs.asList().get(0)).aggregation();
            prefix.put("value", String.valueOf(value));
            return Stream.of(prefix);
        } else
            return null;
    }

    /**
     * 通用聚合查询处理(含有子type)
     *
     * @param index                 索引名
     * @param elasticsearchTemplate elasticsearchTemplate
     * @param body                  restful查询接口参数
     * @param esQuery               包装的es查询信息
     * @param aggBuilder            自定义聚合
     * @param preFilter             预处理过滤
     * @param postFilter            后处理过滤
     * @return
     */
    public static String dealAllHasChildType(String index, ElasticsearchTemplate elasticsearchTemplate, String body, ESQuery esQuery, AbstractAggregationBuilder aggBuilder, Consumer<List> preFilter, Function<AbstractQueryBuilder,AbstractQueryBuilder> postFilter) {
        Map<String, List<Map<Object, Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object, Object>> group = map.get("group");

        ////过滤条件
        if (preFilter != null)
            preFilter.accept(filter);
        AbstractQueryBuilder queryBuilder = ESQueryUtils.getQueryBuilder(filter, esQuery);
        //后处理
        if (postFilter != null)
            queryBuilder=postFilter.apply(queryBuilder);

        //分组字段顺序列表
        List<String> groupOrder = ESQueryUtils.getESQueryGroups(group).map(x -> x.getField()).collect(Collectors.toList());
        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group, filter, esQuery);

        //增加聚合
        if (aggBuilder != null)
            aggStream = Stream.concat(aggStream, Stream.of(aggBuilder));
        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);

        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes(esQuery.getType())
                .addAggregation(agg)
                .build();

        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        List<LinkedHashMap<String, String>> stream = ESQueryUtils.parse(aggregations, groupOrder);
        String result = JSON.toJSONString(ResultSet.ok(stream.toArray()));
        return result;
    }

    /**
     * 通用聚合查询处理(单个type)
     *
     * @param index                 索引名
     * @param elasticsearchTemplate elasticsearchTemplate
     * @param body                  restful查询接口参数
     * @param aggBuilder            自定义聚合
     * @return
     */
    public static String dealAllSingleType(String index, ElasticsearchTemplate elasticsearchTemplate, String typeName,String body,AbstractQueryBuilder filterBuilder, AbstractAggregationBuilder aggBuilder) {
        Map<String, List<Map<Object, Object>>> map = JSONArray.parseObject(body, Map.class);
        List<Map<Object, Object>> filter = map.get("filter");
        List<Map<Object, Object>> group = map.get("group");

        //过滤条件
        Stream<ESQueryFilter> esQueryFilters=ESQueryUtils.getESQueryFilters(filter);
        Stream<AbstractQueryBuilder> queryStream = ESQueryUtils.getESQueryBuilders(esQueryFilters);

        //增加过滤
        if (filterBuilder != null)
            queryStream = Stream.concat(queryStream, Stream.of(filterBuilder));
        //过滤
        AbstractQueryBuilder queryBuilder = ESQueryUtils.transformFilter(queryStream);

        //分组条件
        Stream<AbstractAggregationBuilder> aggStream = ESQueryUtils.getAggregationBuilders(group);


        //增加聚合
        if (aggBuilder != null)
            aggStream = Stream.concat(aggStream, Stream.of(aggBuilder));
        //聚合查询
        AbstractAggregationBuilder agg = ESQueryUtils.tansformGroup(aggStream);

        //执行查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices(index)
                .withTypes(typeName)
                .addAggregation(agg)
                .build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        //获取解析后的结果
        Stream<LinkedHashMap<String, String>> stream = ESQueryUtils.parse(aggregations);
        String result = JSON.toJSONString(ResultSet.ok(stream.toArray()));
        return result;
    }

}
