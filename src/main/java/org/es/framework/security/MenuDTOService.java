package org.es.framework.security;

import java.util.List;

public interface MenuDTOService {
	
	public List<MenuDTO> getUserListMenu(CurrentUser user);
	
	public String[] getMenuUrl(CurrentUser user);
	
}
