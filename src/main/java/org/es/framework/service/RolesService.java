package org.es.framework.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.domain.Menus;
import org.es.framework.domain.Roles;
import org.es.framework.domain.RolesMenus;
import org.es.framework.mvc.exception.EsRuntimeException;
import org.es.framework.repository.MenusRepository;
import org.es.framework.repository.RolesMenusRepository;
import org.es.framework.repository.RolesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RolesService {

	@Resource
	private RolesRepository rolesRepository;

	@Resource
	private RolesMenusRepository rolesMenusRepository;

	@Resource
	private MenusRepository menusRepository;

	public List<Roles> listRoles() {
		List<Roles> roles = rolesRepository.findAll(new Sort(Direction.ASC, "code"));
		return roles;
	}

	public void del(Long roleId) {
		rolesRepository.delete(roleId);
	}

	public List<Menus> findMenusByRoleId(Long roleId) {
		return rolesMenusRepository.findMenusByRoleId(roleId);
	}

	public Roles getRoleByCode(String code) {
		return rolesRepository.findOneByCode(code);
	}

	public void selectMenu(Long roleId, List<Menus> menus) {
		Roles role = rolesRepository.getOne(roleId);
		if (role == null) {
			throw new EsRuntimeException("角色已经不存在");
		}
		rolesMenusRepository.delByRoleId(roleId);
		for (Menus menu : menus) {
			if (menu.getLevel() == 2) {
				menu = menusRepository.getOne(menu.getId());

				RolesMenus rolesMenu = new RolesMenus();
				rolesMenu.setRole(role);
				rolesMenu.setMenu(menu);
				rolesMenusRepository.save(rolesMenu);
			}
		}
	}

	/**
	 * 查询
	 * @param name
	 * @param available
	 * @param pageIndex
	 * @param size
	 * @param sort
	 * @param sortFieldName
	 * @return
	 */
	public Page<Roles> find(final String name, final Integer available, int pageIndex, int size, String sort,
			String... sortFieldName) {
		Specification<Roles> specification = new Specification<Roles>() {
			@Override
			public Predicate toPredicate(Root<Roles> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(name)) {
					predicates.add(cb.like(root.<String> get("name"), name + "%"));
				}
				if (null != available) {
					predicates.add(cb.equal(root.<String> get("available"), available));
				}
				query.where(predicates.toArray(new Predicate[] {}));
				return null;
			}
		};
		if (sortFieldName != null && sortFieldName.length > 0) {
			return rolesRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf(sort), sortFieldName)));
		} else {
			return rolesRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf("ASC"), "createdTime")));
		}
	}

	/**
	 * 新增
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void add(Map<String, Object> paramMap) {
		// 处理参数
		String name = String.valueOf(paramMap.get("name"));
		String code = String.valueOf(paramMap.get("code"));
		Integer available = (Integer) paramMap.get("available");

		// 判断角色是否存在
		if (rolesRepository.countByName(name) > 0) {
			throw new EsRuntimeException(String.format("名称是%s的角色已经存在", name));
		}
		if (rolesRepository.countByCode(code) > 0) {
			throw new EsRuntimeException(String.format("编码是%s的角色已经存在", code));
		}

		// 保存roles
		Roles role = new Roles();
		role.setName(name);
		role.setCode(code);
		if (null != available) {
			role.setAvailable(available);
		}
		role.sethVersion(0);
		role.setCreatedTime(new Date());
		rolesRepository.save(role);
		role = rolesRepository.findOneByCode(code); // 持久化

		// 判断菜单是否存在
		List<String> menusIds = (List<String>) paramMap.get("menuIds");
		List<String> menusNames = (List<String>) paramMap.get("menuNames");
		int size = menusIds.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Menus menu = menusRepository.findOne(Long.parseLong(menusIds.get(i)));
				if (menu == null) {
					throw new EsRuntimeException(String.format("菜单名是%s菜单不存在", menusNames.get(i)));
				}

				// 保存rolesmenus
				RolesMenus rolesMenus = new RolesMenus();
				rolesMenus.setRole(role); // 角色
				rolesMenus.setMenu(menu); // 菜单
				rolesMenus.setAvailable(1);
				rolesMenus.setCreatedTime(new Date());
				rolesMenus.sethVersion(0);
				rolesMenusRepository.save(rolesMenus);
			}
		}
	}

	/**
	 * 修改
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void update(Map<String, Object> paramMap) {
		// 处理参数
		Long id = Long.parseLong(String.valueOf(paramMap.get("id")));
		if (id == null) {
			throw new EsRuntimeException("系统错误");
		}
		String name = String.valueOf(paramMap.get("name"));
		String code = String.valueOf(paramMap.get("code"));
		Integer available = (Integer) paramMap.get("available");

		// 判断角色是否存在
		Roles role = rolesRepository.findOne(id);
		if (null == role) {
			throw new EsRuntimeException("用户不存在");
		}

		// 处理roles
		role.setName(name);
		role.setCode(code);
		if (null != available) {
			role.setAvailable(available);
		}
		rolesRepository.save(role);

		// 删除原角色菜单映射
		rolesMenusRepository.delByRoleId(id);
		// 判断菜单是否存在
		List<String> menusIds = (List<String>) paramMap.get("menuIds");
		List<String> menusNames = (List<String>) paramMap.get("menuNames");
		int size = menusIds.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Menus menu = menusRepository.findOne(Long.parseLong(menusIds.get(i)));
				if (menu == null) {
					throw new EsRuntimeException(String.format("菜单名是%s菜单不存在", menusNames.get(i)));
				}

				// 保存rolesmenus
				RolesMenus rolesMenus = new RolesMenus();
				rolesMenus.setRole(role); // 角色
				rolesMenus.setMenu(menu); // 菜单
				rolesMenus.setAvailable(1);
				rolesMenus.setCreatedTime(new Date());
				rolesMenus.sethVersion(0);
				rolesMenusRepository.save(rolesMenus);
			}
		}
	}

	/**
	 * 批量删除
	 * @param ids
	 */
	public void delBatch(List<Long> ids) {
		// 删除中间表记录
		rolesMenusRepository.delByRoleIds(ids);
		// 删除角色
		rolesRepository.delByIds(ids);
	}

	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public Roles detail(Long id) {
		return rolesRepository.findOne(id);
	}

	/**
	 * 获得所有权限
	 * @return
	 */
	public List<Roles> getAllRoles() {
		return rolesRepository.findAll();
	}

	/**
	 * 获得用户所属角色
	 * @param userId
	 * @return
	 */
	public List<Roles> getRolesByUser(Long userId) {
		return rolesRepository.getRolesByUser(userId);
	}
}
