package org.es.framework.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.es.framework.utils.EsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	private Logger logger = LoggerFactory.getLogger(LoginAuthenticationFilter.class);

	protected static final String DEFAULT_FILTER_PROCESSES_URL = "/";
	private static final String DEFAULT_USERNAME_PROPERTY = "username";
	private static final String DEFAULT_PASSWORD_PROPERTY = "password";
	private String usernameProperty;
	private String passwordProperty;
	
	private UserDetailsService userDetailsService;
	
	private MyPasswordEncoder myPasswordEncoder;
	
	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public LoginAuthenticationFilter() {
		super(DEFAULT_FILTER_PROCESSES_URL);
	}

	public LoginAuthenticationFilter(String processurl) {
		super(processurl);
	}

	public MyPasswordEncoder getMyPasswordEncoder() {
		return myPasswordEncoder;
	}

	public void setMyPasswordEncoder(MyPasswordEncoder myPasswordEncoder) {
		this.myPasswordEncoder = myPasswordEncoder;
	}

	/**
	 * 登陆，尝试授权的方法
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		HttpSession session = request.getSession();
		session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
		session.removeAttribute("errorMessge");
		// 将POST请求踢出
		if (!request.getMethod().equals("POST")) {
			logger.warn("Authentication method not supported:{}", request.getMethod());
			throw new AuthenticationServiceException("登录不支持: " + request.getMethod() + "方法");
		}

		String username = obtainUsername(request);
		String password = obtainPassword(request);
		if (username == null || username.equals("")) {
			throw new AuthenticationServiceException("用户名为空");
		}
		if (password == null || password.equals("")) {
			throw new AuthenticationServiceException("密码为空");
		}
		CurrentUser currentUser = (CurrentUser)userDetailsService.loadUserByUsername(username);
		if(currentUser == null){
			throw new AuthenticationServiceException("用户名不正确"); 
		}
		if(!currentUser.getPassword().equals(myPasswordEncoder.encodePassword(password,username))){
			throw new AuthenticationServiceException("密码不正确"); 
		}
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, currentUser.getPassword(),currentUser.getAuthorities());
		authRequest.setDetails(currentUser);
		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
		session.setAttribute(EsConstants.SessionName.currentUser, currentUser);
		logger.info("current login user:{}", username);
		return authentication;
	}

	public String getUsernameProperty() {
		if (usernameProperty == null) {
			usernameProperty = DEFAULT_USERNAME_PROPERTY;
		}
		return usernameProperty;
	}

	public void setUsernameProperty(String usernameProperty) {

		this.usernameProperty = usernameProperty;
	}

	public String getPasswordProperty() {
		if (passwordProperty == null) {
			passwordProperty = DEFAULT_PASSWORD_PROPERTY;
		}
		return passwordProperty;
	}

	public void setPasswordProperty(String passwordProperty) {
		this.passwordProperty = passwordProperty;
	}

	protected String obtainUsername(HttpServletRequest request) {
		Object obj = request.getParameter(getUsernameProperty());
		return null == obj ? "" : obj.toString().trim();
	}

	protected String obtainPassword(HttpServletRequest request) {

		Object obj = request.getParameter(getPasswordProperty());
		return null == obj ? "" : obj.toString().trim();
	}
}
