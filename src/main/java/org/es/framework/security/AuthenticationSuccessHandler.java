package org.es.framework.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.es.framework.util.json.ResponseJSON;
import org.es.framework.utils.ConstantFunction;
import org.es.framework.utils.EsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationSuccessHandler implements
		org.springframework.security.web.authentication.AuthenticationSuccessHandler {
	@Autowired
	private MenuDTOService menuDTOService;

	@Value("${essecurity.successRedirectUrl}")
	private String successRedirectUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		HttpSession session = request.getSession();
		CurrentUser user = (CurrentUser) session.getAttribute(EsConstants.SessionName.currentUser);
		session.setAttribute(EsConstants.SessionName.currentMenus, userLimit(user));
		if (ConstantFunction.isAjax(request)) {
			OutputStream output = response.getOutputStream();
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			output.write(ResponseJSON.instance().setRedirectUrl(request.getContextPath() + successRedirectUrl)
					.setStatus(true).toJSON().getBytes("utf-8"));
			output.flush();
		} else {
			response.sendRedirect(request.getContextPath() + successRedirectUrl);
		}

	}

	private List<MenuDTO> userLimit(CurrentUser user) {
		return menuDTOService.getUserListMenu(user);
	}
}
