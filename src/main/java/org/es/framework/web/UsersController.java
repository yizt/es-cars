package org.es.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.es.framework.domain.Roles;
import org.es.framework.domain.Users;
import org.es.framework.mvc.validation.ValidatorUtil;
import org.es.framework.service.GroupsService;
import org.es.framework.service.MenusService;
import org.es.framework.service.UsersService;
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
 * 用户管理
 * @author zhouqi
 *
 */
@Controller
@RequestMapping(value = "/users")
public class UsersController {

	@Resource
	private UsersService usersService;

	@Resource
	private GroupsService groupsService;

	@Resource
	private MenusService menusService;

	@RequestMapping("save")
	@ResponseBody
	public String save(@RequestBody String usersJson) {
		Users user = JsonUtils.toObject(usersJson, Users.class);
		ValidatorUtil.checkDomain(user);
		String msg = "保存用户成功";
		if (!user.isNew()) {
			msg = "用户编辑成功";
		}
		usersService.save(user);
		return ResponseJSON.instance().addAlertMessage(msg).toJSON();
	}

	@RequestMapping("selectRole/{userId}")
	@ResponseBody
	public String selectRole(@RequestBody String rolesJson, @PathVariable("userId") Long userId) {
		List<Roles> roles = JsonUtils.toObject(rolesJson, new TypeReference<List<Roles>>() {
		});
		usersService.selectRole(userId, roles);
		return ResponseJSON.instance().addAlertMessage("用户角色更新成功").toJSON();
	}

	@RequestMapping("roles")
	@ResponseBody
	public String getRoles(Long userId) {
		List<Roles> roles = usersService.findRolesByUserId(userId);
		return ResponseJSON.instance().setData(roles).toJSON();
	}

	@RequestMapping("del")
	@ResponseBody
	public String del(@RequestParam("userId") Long userId) {
		usersService.del(userId);
		return ResponseJSON.instance().addAlertMessage("删除用户成功").toJSON();
	}

	/**
	 * 用户管理--查询
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
		String userName = (String) paramMap.get("userName"); // 查询条件
		Integer available = (Integer) paramMap.get("available"); // 查询条件
		if (userName != null) {
			userName = userName.trim();
		}
		if (sort != null) {
			sort = sort.trim();
		}
		Page<Users> pages = usersService.find(userName, available, pageIndex, pageSize, sort,
				sortFields.toArray(new String[] {}));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("content", pages.getContent());
		result.put("numberOfPages", pages.getTotalPages());
		result.put("pageNums", pages.getNumber());
		result.put("total", pages.getTotalElements());

		return ResponseJSON.instance().setData(result).addFilterName("password").toJSON();
	}

	/**
	 * 用户管理--新增
	 * @param usersJson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("add")
	@ResponseBody
	public String add(@RequestBody String usersJson) {
		Map<String, Object> paramMap = JsonUtils.toObject(usersJson, HashMap.class);
		usersService.add(paramMap);

		return ResponseJSON.instance().addAlertMessage("保存用户成功").toJSON();
	}

	/**
	 * 用户管理--修改
	 * @param usersJson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("update")
	@ResponseBody
	public String update(@RequestBody String usersJson) {
		Map<String, Object> paramMap = JsonUtils.toObject(usersJson, HashMap.class);
		usersService.update(paramMap);

		return ResponseJSON.instance().addAlertMessage("保存用户成功").toJSON();
	}

	/**
	 * 用户管理--删除
	 * @param userIds
	 * @return
	 */
	@RequestMapping("delBatch")
	@ResponseBody
	public String delBatch(@RequestParam("ids") List<Long> ids) {
		usersService.delBatch(ids);

		return ResponseJSON.instance().addAlertMessage("删除用户成功").toJSON();
	}

	/**
	 * 详情
	 * @param id
	 * @return
	 */
	@RequestMapping("detail")
	@ResponseBody
	public String detail(@RequestParam("id") Long id) {
		return ResponseJSON.instance().setData(usersService.detail(id)).addFilterName("password").toJSON();
	}
}
