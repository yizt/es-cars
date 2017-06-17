package org.es.framework.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.domain.Menus;
import org.es.framework.mvc.exception.EsRuntimeException;
import org.es.framework.repository.MenusRepository;
import org.es.framework.repository.RolesMenusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MenusService {
	
	@Resource
	private MenusRepository menusRepository;
	
	@Resource
	private RolesMenusRepository rolesMenusRepository;
	
	public List<Menus> listMenus() {
		List<Menus> menus = menusRepository.findAll(new Sort(Direction.ASC, "displayOrder"));
		List<Menus> rootMenus = new ArrayList<Menus>();
		for (Menus menu : menus) {
			if (menu.getLevel() == 1) {
				rootMenus.add(menu);
			}
		}
		return rootMenus;
	}
	
	public void del(Menus menu) {
		if (menusRepository.countSubMenus(menu.getId()) > 0) {
			throw new EsRuntimeException(String.format("[%s]还有子菜单，不能删除", menu.getDisplayName()));
		}
		menusRepository.delete(menu);
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
	public Page<Menus> find(final String name, final Integer available, int pageIndex, int size, String sort,
			String... sortFieldName) {
		Specification<Menus> specification = new Specification<Menus>() {
			@Override
			public Predicate toPredicate(Root<Menus> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
			return menusRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf(sort), sortFieldName)));
		} else {
			return menusRepository.findAll(specification,
					new PageRequest(pageIndex - 1, size, new Sort(Direction.valueOf("ASC"), "createdTime")));
		}
	}
	
	/**
	 * 新增/修改
	 * @param menu
	 */
	public void save(Menus menu) {
		if (!menu.isNew()) {
			// 修改
			Menus updateMenu = menu;
			Menus updatepMenu = updateMenu.getpMenu();
			
			menu = menusRepository.findOne(menu.getId()); // 转持久状态
			
			if (!StringUtils.isBlank(updateMenu.getUrl())) {
				menu.setUrl(updateMenu.getUrl());
			}
			menu.setAvailable(updateMenu.getAvailable());
			menu.setIcon(updateMenu.getIcon());
			menu.setName(updateMenu.getName());
			menu.setDisplayName(updateMenu.getDisplayName());
			menu.setId(updateMenu.getId());
			
			if (null != updatepMenu) {
				Menus pMenu = menusRepository.findOne(updatepMenu.getId()); // 转持久状态
				pMenu.setId(updatepMenu.getId());
				menu.setpMenu(pMenu);
				menu.setLevel(pMenu.getLevel() + 1);
			}
		} else {
			// 新增
			int level = 0;
			if (menu.getpMenu() != null) {
				Menus pMenu = menusRepository.getOne(menu.getpMenu().getId());
				level = pMenu.getLevel();
				menu.setpMenu(pMenu);
			}
			menu.setLevel(level + 1);
		}
		
		menusRepository.save(menu);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delBatch(List<Long> ids) {
		// 判断是否还有子菜单
		int size = ids.size();
		for (int i = 0; i < size; i++) {
			int cnt = menusRepository.countSubMenus(ids.get(i));
			if (cnt > 0) {
				throw new EsRuntimeException(String.format("第[%s]个菜单还有子菜单，不能删除", i + 1));
			}
		}

		// 删除中间表记录
		rolesMenusRepository.delByMenuIds(ids);
		
		// 删除菜单
		menusRepository.delByIds(ids);
	}
	
	/**
	 * 详情
	 * @param id
	 * @return
	 */
	public Menus detail(Long id) {
		return menusRepository.findOne(id);
	}
	
	/**
	 * 获得用户菜单
	 * @param userId
	 * @return
	 */
	public List<Menus> getMenusByUser(Long userId) {
		return menusRepository.getMenusByUser(userId);
	}
	
	/**
	 * 获得角色绑定的菜单
	 * @param roleId
	 * @return
	 */
	public List<Menus> getMenusByRole(Long roleId) {
		return menusRepository.getMenusByRole(roleId);
	}

	/**
	 * 获得所有菜单
	 * @return
	 */
	public List<Menus> getAllMenus() {
		return menusRepository.findAll();
	}
}
