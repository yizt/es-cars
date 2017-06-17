package org.es.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.es.framework.domain.Apis;
import org.es.framework.mvc.validation.ValidatorUtil;
import org.es.framework.service.ApisService;
import org.es.framework.util.json.JsonUtils;
import org.es.framework.util.json.ResponseJSON;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 接口管理
 * @author zhouqi
 *
 */
@Controller
@RequestMapping(value = "/apis")
public class ApisController {
	
	@Resource
	private ApisService apisService;
	
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
		Integer type = (Integer) paramMap.get("type"); // 查询条件
		int available = (int) paramMap.get("available"); // 查询条件
		if (name != null) {
			name = name.trim();
		}
		if (sort != null) {
			sort = sort.trim();
		}
		Page<Apis> pages = apisService.find(name, type, available, pageIndex, pageSize, sort,
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
		return ResponseJSON.instance().setData(apisService.detail(id)).toJSON();
	}
	
	/**
	 * 新增/修改
	 * @param apisJson
	 * @return
	 */
	@RequestMapping("save")
	@ResponseBody
	public String save(@RequestBody String apisJson) {
		Apis apis = JsonUtils.toObject(apisJson, Apis.class);
		ValidatorUtil.checkDomain(apis);
		String msg = "保存接口成功";
		if (!apis.isNew()) {
			msg = "编辑接口成功";
		}
		apisService.save(apis);
		
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
		apisService.delBatch(ids);
		
		return ResponseJSON.instance().addAlertMessage("删除接口成功").toJSON();
	}
}
