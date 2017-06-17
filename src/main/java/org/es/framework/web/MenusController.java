package org.es.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.es.framework.domain.Menus;
import org.es.framework.mvc.validation.ValidatorUtil;
import org.es.framework.service.MenusService;
import org.es.framework.util.json.JsonUtils;
import org.es.framework.util.json.ResponseJSON;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 权限管理
 * @author zhouqi
 *
 */
@Controller
@RequestMapping(value = "/menus")
public class MenusController {

	private static final String _MENUS = "_menus";

	@Resource
	private MenusService menusService;

	@SuppressWarnings("unchecked")
	@RequestMapping("list")
	@ResponseBody
	public String list(HttpSession session) {
		List<Menus> menus = null;
		if (session.getAttribute(_MENUS) != null) {
			menus = (List<Menus>) session.getAttribute(_MENUS);
		} else {
			menus = menusService.listMenus();
			session.setAttribute(_MENUS, menus);
		}
		return ResponseJSON.instance().setData(menus).addFilterName("pMenu").toJSON();
	}

	@RequestMapping("all")
	@ResponseBody
	public String all() {
		List<Menus> menus = menusService.listMenus();
		return ResponseJSON.instance().setData(menus).addFilterName("pMenu").toJSON();
	}

	@RequestMapping("del")
	@ResponseBody
	public String del(@RequestBody String menuJson) {
		Menus menu = JsonUtils.toObject(menuJson, Menus.class);
		menusService.del(menu);
		return ResponseJSON.instance().addAlertMessage("删除菜单成功").toJSON();
	}
	
	/**
	 * 菜单管理--查询
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
		Page<Menus> pages = menusService.find(name, available, pageIndex, pageSize, sort,
				sortFields.toArray(new String[] {}));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("content", pages.getContent());
		result.put("numberOfPages", pages.getTotalPages());
		result.put("pageNums", pages.getNumber());
		result.put("total", pages.getTotalElements());
		return ResponseJSON.instance().setData(result).addFilterName("pMenu").toJSON();
	}
	
	/**
	 * 菜单管理--新增/修改
	 * @param menuJson
	 * @return
	 */
	@RequestMapping("save")
	@ResponseBody
	public String save(@RequestBody String menuJson) {
		Menus menu = JsonUtils.toObject(menuJson, Menus.class);
		ValidatorUtil.checkDomain(menu);
		String msg = "保存菜单成功";
		if (!menu.isNew()) {
			msg = "编辑菜单成功";
		}
		menusService.save(menu);

		return ResponseJSON.instance().addAlertMessage(msg).toJSON();
	}
	
	/**
	 * 菜单管理--删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("delBatch")
	@ResponseBody
	public String delBatch(@RequestParam("ids") List<Long> ids) {
		menusService.delBatch(ids);
		return ResponseJSON.instance().addAlertMessage("删除菜单成功").toJSON();
	}

	/**
	 * 菜单管理--详情
	 * @param menuId
	 * @return
	 */
	@RequestMapping("detail")
	@ResponseBody
	public String detail(@RequestParam("id") Long id) {
		return ResponseJSON.instance().setData(menusService.detail(id)).toJSON();
	}

	/**
	 * 获得用户的菜单
	 * @param userId
	 * @return
	 */
	@RequestMapping("getMenusByUser")
	@ResponseBody
	public String getMenusByUser(@RequestParam("userId") Long userId) {
		return ResponseJSON.instance().setData(menusService.getMenusByUser(userId)).toJSON();
	}
	
	/**
	 * 获得角色绑定的菜单
	 * @param roleId
	 * @return
	 */
	@RequestMapping("getMenusByRole")
	@ResponseBody
	public String getMenusByRole(@RequestParam("roleId") Long roleId) {
		return ResponseJSON.instance().setData(menusService.getMenusByRole(roleId)).toJSON();
	}

	/**
	 * 获得所有权限
	 * @return
	 */
	@RequestMapping("getAllMenus")
	@ResponseBody
	public String getAllMenus() {
		return ResponseJSON.instance().setData(menusService.getAllMenus()).toJSON();
	}
}
