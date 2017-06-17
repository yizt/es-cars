package org.es.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.es.framework.domain.Menus;
import org.es.framework.domain.Roles;
import org.es.framework.service.RolesService;
import org.es.framework.util.json.JsonUtils;
import org.es.framework.util.json.ResponseJSON;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 角色管理
 * @author zhouqi
 */
@Controller
@RequestMapping(value = "/roles")
public class RolesController {
	
	@Resource
	private RolesService rolesService;
	
	@RequestMapping("list")
	@ResponseBody
	public String list() {
		List<Roles> roles = rolesService.listRoles();
		return ResponseJSON.instance().setData(roles).toJSON();
	}
	
	@RequestMapping("del")
	@ResponseBody
	public String del(@RequestParam("roleId") Long roleId) {
		rolesService.del(roleId);
		return ResponseJSON.instance().addAlertMessage("删除角色成功").toJSON();
	}
	
	@RequestMapping("menus")
	@ResponseBody
	public String getMenus(@RequestParam("roleId") Long roleId) {
		List<Menus> menus = rolesService.findMenusByRoleId(roleId);
		return ResponseJSON.instance().setData(menus).addFilterName("pMenu").toJSON();
	}
	
	@RequestMapping("selectMenu/{userId}")
	@ResponseBody
	public String selectMenu(@RequestBody String menusJson, @PathVariable("userId") Long roleId) {
		List<Menus> menus = JsonUtils.toObject(menusJson, new TypeReference<List<Menus>>() {
		});
		rolesService.selectMenu(roleId, menus);
		return ResponseJSON.instance().addAlertMessage("角色菜单更新成功").toJSON();
	}
	
	/**
	 * 角色管理--查询
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
		Page<Roles> pages = rolesService.find(name, available, pageIndex, pageSize, sort,
				sortFields.toArray(new String[] {}));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("content", pages.getContent());
		result.put("numberOfPages", pages.getTotalPages());
		result.put("pageNums", pages.getNumber());
		result.put("total", pages.getTotalElements());
		return ResponseJSON.instance().setData(result).toJSON();
	}
	
	/**
	 * 角色管理--新增
	 * @param rolesJson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("add")
	@ResponseBody
	public String add(@RequestBody String rolesJson) {
		Map<String, Object> paramMap = JsonUtils.toObject(rolesJson, HashMap.class);
		rolesService.add(paramMap);
		
		return ResponseJSON.instance().addAlertMessage("保存角色成功").toJSON();
	}
	
	/**
	 * 角色管理--修改
	 * @param rolesJson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("update")
	@ResponseBody
	public String update(@RequestBody String rolesJson) {
		Map<String, Object> paramMap = JsonUtils.toObject(rolesJson, HashMap.class);
		rolesService.update(paramMap);
		
		return ResponseJSON.instance().addAlertMessage("保存角色成功").toJSON();
	}
	
	/**
	 * 角色管理--删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("delBatch")
	@ResponseBody
	public String delBatch(@RequestParam("ids") List<Long> ids) {
		rolesService.delBatch(ids);
		return ResponseJSON.instance().addAlertMessage("删除角色成功").toJSON();
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	@RequestMapping("detail")
	@ResponseBody
	public String detail(@RequestParam("id") Long id) {
		return ResponseJSON.instance().setData(rolesService.detail(id)).toJSON();
	}
	
	/**
	 * 获得所有角色
	 * @return
	 */
	@RequestMapping("allRoles")
	@ResponseBody
	public String getAllRoles() {
		return ResponseJSON.instance().setData(rolesService.getAllRoles()).toJSON();
	}
	
	/**
	 * 获得用户所属角色
	 * @param userId
	 * @return
	 */
	@RequestMapping("getRolesByUser")
	@ResponseBody
	public String getRolesByUser(@RequestParam("userId") Long userId) {
		return ResponseJSON.instance().setData(rolesService.getRolesByUser(userId)).toJSON();
	}
}
