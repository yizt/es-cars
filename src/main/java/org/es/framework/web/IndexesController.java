package org.es.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.es.framework.domain.Indexes;
import org.es.framework.mvc.validation.ValidatorUtil;
import org.es.framework.service.IndexesService;
import org.es.framework.util.json.JsonUtils;
import org.es.framework.util.json.ResponseJSON;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 指标管理/指标分类管理
 * @author zhouqi
 *
 */
@Controller
@RequestMapping(value = "/indexes")
public class IndexesController {
	
	@Resource
	private IndexesService indexesService;
	
	/**
	 * 查询
	 * @param queryParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("query")
	@ResponseBody
	public String query(@RequestBody String queryParam) {
		Map<String, Object> paramMap = JsonUtils.toObject(queryParam, HashMap.class);
		Integer pageIndex = (Integer) paramMap.get("pageIndex"); // 分页 当前页数
		Integer pageSize = (Integer) paramMap.get("pageSize"); // 分页 每页显示记录数
		String sort = (String) paramMap.get("sort"); // 排序：顺序/倒序
		List<String> sortFields = (List<String>) paramMap.get("sortFields"); // 排序字段
		String name = (String) paramMap.get("name"); // 查询条件
		Integer isLeaf = (Integer) paramMap.get("isLeaf"); // 查询条件
		if (name != null) {
			name = name.trim();
		}
		if (sort != null) {
			sort = sort.trim();
		}
		Page<Indexes> pages = indexesService.find(name, isLeaf, pageIndex, pageSize, sort,
				sortFields.toArray(new String[] {}));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("content", pages.getContent());
		result.put("numberOfPages", pages.getTotalPages());
		result.put("pageNums", pages.getNumber());
		result.put("total", pages.getTotalElements());
		return ResponseJSON.instance().setData(result).toJSON();
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	@RequestMapping("detail")
	@ResponseBody
	public String detail(@RequestParam("id") Long id) {
		return ResponseJSON.instance().setData(indexesService.detail(id)).toJSON();
	}
	
	/**
	 * 新增/修改
	 * @param indexJson
	 * @return
	 */
	@RequestMapping("save")
	@ResponseBody
	public String save(@RequestBody String indexJson) {
		Indexes indexes = JsonUtils.toObject(indexJson, Indexes.class);
		ValidatorUtil.checkDomain(indexes);
		String msg = "保存指标成功";
		if (!indexes.isNew()) {
			msg = "编辑指标成功";
		}
		indexesService.save(indexes);
		
		return ResponseJSON.instance().addAlertMessage(msg).toJSON();
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("delBatch")
	@ResponseBody
	public String delBatch(@RequestParam("ids") List<Long> ids) {
		indexesService.delBatch(ids);
		
		return ResponseJSON.instance().addAlertMessage("删除指标成功").toJSON();
	}
}
