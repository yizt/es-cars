package org.es.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.es.framework.domain.Groups;
import org.es.framework.mvc.validation.ValidatorUtil;
import org.es.framework.service.GroupsService;
import org.es.framework.util.json.JsonUtils;
import org.es.framework.util.json.ResponseJSON;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 组织机构管理
 * @author zhouqi
 *
 */
@Controller
@RequestMapping(value = "/groups")
public class GroupsController {

	@Resource
	private GroupsService groupsService;
	
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
		Integer available = (Integer) paramMap.get("available"); // 查询条件
		if (name != null) {
			name = name.trim();
		}
		if (sort != null) {
			sort = sort.trim();
		}
		Page<Groups> pages = groupsService.find(name, available, pageIndex, pageSize, sort,
				sortFields.toArray(new String[] {}));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("content", pages.getContent());
		result.put("numberOfPages", pages.getTotalPages());
		result.put("pageNums", pages.getNumber());
		result.put("total", pages.getTotalElements());
		return ResponseJSON.instance().setData(result).toJSON();
	}

	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("delBatch")
	@ResponseBody
	public String delBatch(@RequestParam("ids") List<Long> ids) {
		groupsService.delBatch(ids);

		return ResponseJSON.instance().addAlertMessage("删除组织成功").toJSON();
	}

	/**
	 * 新增/修改
	 * @param groupJson
	 * @return
	 */
	@RequestMapping("save")
	@ResponseBody
	public String save(@RequestBody String groupJson) {
		Groups groups = JsonUtils.toObject(groupJson, Groups.class);
		ValidatorUtil.checkDomain(groups);
		String msg = "保存组织成功";
		if (!groups.isNew()) {
			msg = "编辑组织成功";
		}
		groupsService.save(groups);

		return ResponseJSON.instance().addAlertMessage(msg).toJSON();
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	@RequestMapping("detail")
	@ResponseBody
	public String detail(@RequestParam("id") Long id) {
		return ResponseJSON.instance().setData(groupsService.detail(id)).toJSON();
	}

	/**
	 * 获得所有组织结构
	 * @return
	 */
	@RequestMapping("allGroups")
	@ResponseBody
	public String getAllGroups() {
		return ResponseJSON.instance().setData(groupsService.getAllGroups()).toJSON();
	}
}
