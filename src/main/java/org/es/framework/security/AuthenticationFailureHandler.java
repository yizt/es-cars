package org.es.framework.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.es.framework.util.json.ResponseJSON;
import org.es.framework.utils.ConstantFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Value("${essecurity.failureRedirectUrl}")
	private String failureRedirectUrl;

	public AuthenticationFailureHandler() {
		super();
	}

	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if (ConstantFunction.isAjax(request)) {
			OutputStream output = response.getOutputStream();
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			String msg = exception.getMessage();
			Map<String, String> msgMap = new HashMap<String, String>();
			msgMap.put("errorMessage", msg);
			output.write(ResponseJSON.instance().setData(msgMap).setStatus(false).toJSON().getBytes("utf-8"));
			output.flush();
		} else {
			HttpSession session = request.getSession();
			session.setAttribute("errorMessge", exception.getMessage());
			super.setDefaultFailureUrl(failureRedirectUrl);
			super.onAuthenticationFailure(request, response, exception);
		}
	}

}
