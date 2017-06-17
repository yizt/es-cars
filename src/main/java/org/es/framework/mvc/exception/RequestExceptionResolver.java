package org.es.framework.mvc.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.es.framework.util.json.ResponseJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;


public class RequestExceptionResolver extends AbstractHandlerExceptionResolver {
	private static final Logger log = LoggerFactory.getLogger(RequestExceptionResolver.class);

	public ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		ModelAndView modelAndView = new ModelAndView();
		String json = ResponseJSON.instance().setStatus(false).setExceptionMessage(ex.getMessage()).toJSON();
		modelAndView.addObject("errorMessage", json);
		modelAndView.setView(new StringView("errorMessage"));
		log.error(ex.getMessage(), ex);
		return modelAndView;
	}

}
