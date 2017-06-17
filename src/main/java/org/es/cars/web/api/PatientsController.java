package org.es.cars.web.api;

import javax.annotation.Resource;
import javax.ws.rs.POST;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.index.query.QueryBuilders;
import org.es.framework.util.json.ResultSet;
import org.es.cars.es.model.OrgPatientInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/rest/patients")
public class PatientsController {

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;
	@SuppressWarnings("unchecked")
	@RequestMapping("cardno")
	@ResponseBody
	@POST
	public String getById(@RequestBody String body) {

		Map<String,List<Map<Object,Object>>> map = JSONArray.parseObject(body, Map.class);
		List<Map<Object, Object>> filter = map.get("filter");

		String id = filter.get(0).get("match_value").toString();
		//System.out.println(group.toString());

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
				.withIndices("patient_bak_index").withTypes("org_patient_info").withFilter(QueryBuilders.boolQuery()
						.must(QueryBuilders.termQuery("patient_identity_no", id)))
				.build();
		Page<OrgPatientInfo> pg = elasticsearchTemplate.queryForPage(searchQuery, OrgPatientInfo.class);
		System.out.println(pg.getContent().toString());
		//成功
		if("[]"!=(pg.getContent().toString()))
		{
			return JSON.toJSON(ResultSet.ok(pg.getContent())).toString();
		}else
		{
			return JSON.toJSON(ResultSet.fail()).toString();
		}
		//return ResponseJSON.instance().setData(pg.getContent()).toJSON();
	}

}
