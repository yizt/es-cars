package org.es.framework.mvc.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * 为使非中文环境不出错，没有完善国际化前，暂时将locale改成中文
 * 
 */
public class LocaleChangeInterceptor extends HandlerInterceptorAdapter {
  /**
   * Default name of the locale specification parameter: "locale".
   */
  public static final String DEFAULT_PARAM_NAME = "locale";

  private String paramName = DEFAULT_PARAM_NAME;

  private String globalLocal;

  public String getGlobalLocal() {
    return globalLocal;
  }

  public void setGlobalLocal(String globalLocal) {
    this.globalLocal = globalLocal;
  }

  /**
   * Set the name of the parameter that contains a locale specification in a
   * locale change request. Default is "locale".
   */
  public void setParamName(String paramName) {
    this.paramName = paramName;
  }

  /**
   * Return the name of the parameter that contains a locale specification in a
   * locale change request.
   */
  public String getParamName() {
    return this.paramName;
  }

  @Override
  public boolean preHandle(HttpServletRequest request,
      HttpServletResponse response, Object handler) throws ServletException {

    String newLocale = request.getParameter(this.paramName);
    if (StringUtils.isEmpty(globalLocal) && StringUtils.isEmpty(newLocale)) {
      newLocale = "zh_CN";
    }
    if (newLocale != null) {
      LocaleResolver localeResolver = RequestContextUtils
          .getLocaleResolver(request);
      if (localeResolver == null) {
        throw new IllegalStateException(
            "No LocaleResolver found: not in a DispatcherServlet request?");
      }
      localeResolver.setLocale(request, response,
          StringUtils.parseLocaleString(newLocale));
    }
    // Proceed in any case.
    return true;
  }

}
