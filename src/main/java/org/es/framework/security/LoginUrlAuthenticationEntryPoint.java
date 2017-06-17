package org.es.framework.security;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.es.framework.util.json.ResponseJSON;
import org.es.framework.utils.ConstantFunction;
import org.springframework.security.core.AuthenticationException;

public class LoginUrlAuthenticationEntryPoint extends
		org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint {

	private String loginFormUrl;

	public LoginUrlAuthenticationEntryPoint(String loginFormUrl) {
		super(loginFormUrl);
		this.loginFormUrl = loginFormUrl;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		// 判断AJAX请求，则登出
		if (ConstantFunction.isAjax(request)) {
			OutputStream output = response.getOutputStream();
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			output.write(ResponseJSON.instance().setRedirectUrl(request.getContextPath() + loginFormUrl)
					.addAlertMessage("会话失效，需要重新登录系统").setStatus(false).toJSON().getBytes("utf-8"));
			output.flush();
		} else {
			HttpSession session = request.getSession();
			session.setAttribute("errorMessge", "会话失效，需要重新登录系统");
			super.commence(request, response, authException);
		}
	}
}
