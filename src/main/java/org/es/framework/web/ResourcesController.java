package org.es.framework.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.es.framework.security.MenuDTO;
import org.es.framework.util.json.ResponseJSON;
import org.es.framework.utils.EsConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/pub")
public class ResourcesController {

	@RequestMapping("menus")
	public ResponseEntity<String> toView(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<MenuDTO> menuDTOs = (List<MenuDTO>)session.getAttribute(EsConstants.SessionName.currentMenus);
		return ResponseJSON.instance().setData(menuDTOs).responseEntity();
	}


}
