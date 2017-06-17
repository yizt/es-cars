package org.es.framework.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.es.framework.domain.Menus;
import org.es.framework.domain.Roles;
import org.es.framework.service.RolesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuDTOServiceImpl implements MenuDTOService {

	@Autowired
	private RolesService rolesService;

	public List<MenuDTO> getUserListMenu(CurrentUser user) {
		List<MenuDTO> menuDTOs = new ArrayList<MenuDTO>();
		Map<Long, MenuDTO> pMenuDTOMap = new HashMap<Long, MenuDTO>();
		String[] roles = user.getRole();
		for (String code : roles) {
			Roles role = rolesService.getRoleByCode(code);
			List<Menus> menus = rolesService.findMenusByRoleId(role.getId());
			for (Menus subMenu : menus) {
				Menus pMenu = subMenu.getpMenu();
				MenuDTO pMenuDTO = pMenuDTOMap.get(pMenu.getId());
				if (pMenuDTO == null) {
					pMenuDTO = new MenuDTO();
					BeanUtils.copyProperties(pMenu, pMenuDTO, "subMenus");
					pMenuDTOMap.put(pMenuDTO.getId(), pMenuDTO);
					menuDTOs.add(pMenuDTO);
				}
				
				MenuDTO menuDTO = new MenuDTO();
				BeanUtils.copyProperties(subMenu, menuDTO, "subMenus");
				pMenuDTO.getSubMenus().add(menuDTO);
				Collections.sort(pMenuDTO.getSubMenus());
			}
		}
		Collections.sort(menuDTOs);
		return menuDTOs;
	}
	
	public String[] getMenuUrl(CurrentUser user){
		Set<String> menuUrls = new HashSet<String>();
		String[] roles = user.getRole();
		for (String code : roles) {
			Roles role = rolesService.getRoleByCode(code);
			List<Menus> menus = rolesService.findMenusByRoleId(role.getId());
			for (Menus subMenu : menus) {
				menuUrls.add(subMenu.getResourceUrl());
			}
		}
		return menuUrls.toArray(new String[]{});
	}

}
