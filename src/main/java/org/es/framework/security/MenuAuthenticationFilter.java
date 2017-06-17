package org.es.framework.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.es.framework.utils.EsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

public class MenuAuthenticationFilter extends GenericFilterBean {

	@Autowired
	private MenuDTOService menuDTOService;
	
	private String excludeURL;
	
	private static String[]  resourcesUrl={"/images/","/css/","/js/","/img/","/resources/"};
	
	public String getExcludeURL() {
		return excludeURL;
	}

	public void setExcludeURL(String excludeURL) {
		this.excludeURL = excludeURL;
	}


	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = ((HttpServletRequest) request).getSession();

		String requestString = request.getServletPath().toString();
		
		if (excludeURL != null && !"".equals(excludeURL)) {
			String[] ecludeURLArray = excludeURL.split(",");
			for (String ecludeURLString : ecludeURLArray) {
				if (requestString.equals(ecludeURLString)) {
					chain.doFilter(request, response);
					return;
				}
			}
		}
		//判断是否是静态资源
		for (String url : resourcesUrl) {
			if (requestString.contains(url)) {
				chain.doFilter(request, response);
				return;
			}
		}
		// 判断请求权限验证
		if (requestString != null && !"".equals(requestString)) {
			String[] urls = (String[]) session.getAttribute(EsConstants.SessionName.currentMenuUrls);
			if(urls == null){
				CurrentUser user = (CurrentUser) session.getAttribute(EsConstants.SessionName.currentUser);
				if (user!=null) {
					urls = menuDTOService.getMenuUrl(user);
					session.setAttribute(EsConstants.SessionName.currentMenuUrls, urls);
					for (String url : urls) {
						if(requestString.contains(url)){
							chain.doFilter(request, response);
							return;
						}
					}
				}
			}
			response.sendRedirect(request.getContextPath() + "/accessDenied");
			return;
		}else{
			chain.doFilter(request, response);
			return;
		}
	}

}
