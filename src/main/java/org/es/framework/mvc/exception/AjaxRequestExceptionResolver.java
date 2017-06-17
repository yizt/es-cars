package org.es.framework.mvc.exception;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.es.framework.mvc.validation.DomainValidationException;
import org.es.framework.mvc.validation.ValidatorUtil;
import org.es.framework.util.json.ResponseJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.google.common.base.Throwables;

public class AjaxRequestExceptionResolver extends SimpleMappingExceptionResolver {
	private static final Logger LOG = LoggerFactory.getLogger(AjaxRequestExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			if (handler != null && handlerMethod.getMethodAnnotation(ResponseBody.class) != null) {
				ResponseJSON responseJSON = ResponseJSON.instance();
				ModelAndView modelAndView = new ModelAndView();
				if (ex instanceof DomainValidationException) {
					Map<String, String> validateMap = ((DomainValidationException) ex).getValidateMap();
					hander(responseJSON, validateMap);
				} else if (ex instanceof ConstraintViolationException) {
					Map<String, String> validateMap = ValidatorUtil
							.handerConstraintViolation2(((ConstraintViolationException) ex).getConstraintViolations());
					hander(responseJSON, validateMap);
				} else if (ex instanceof EsRuntimeException) {
					responseJSON.addAlertMessage(ex.getMessage());
					responseJSON.setStatus(false);
				} else {
					Throwable rootCause = Throwables.getRootCause(ex);
					LOG.error(rootCause.toString(), ex);
					responseJSON.addAlertMessage(ex.getMessage() == null ? "系统异常" : ex.getMessage());
					responseJSON.setStatus(false);
				}
				modelAndView.addObject("errorMessage", responseJSON.toJSON());
				modelAndView.setView(new StringView("errorMessage"));
				return modelAndView;
			}
		}
		ResponseJSON responseJSON = ResponseJSON.instance();
		ModelAndView modelAndView = new ModelAndView();
		Throwable rootCause = Throwables.getRootCause(ex);
		responseJSON.addAlertMessage(ex.getMessage() == null ? "系统异常" : ex.getMessage());
		modelAndView.addObject("errorMessage", responseJSON.toJSON());
		modelAndView.setView(new StringView("errorMessage"));
		LOG.error(rootCause.toString(), ex);
		return modelAndView;
	}

	public String getStackTraceInfo(Throwable ex) {
		if (ex == null) {
			return "";
		}

		StringBuffer stackTraceInfo = new StringBuffer(ex.toString());
		StackTraceElement[] astacktraceelement = ex.getStackTrace();

		for (int i = 0; i < astacktraceelement.length; i++) {
			stackTraceInfo.append("\r\n\tat ").append(astacktraceelement[i]);
		}
		return stackTraceInfo.toString();
	}

	private void hander(ResponseJSON responseJSON, Map<String, String> validateMap) {
		for (Map.Entry<String, String> validMessage : validateMap.entrySet()) {
			responseJSON.addValidationMessages(validMessage.getKey(), validMessage.getValue());
		}
		responseJSON.setStatus(false);
	}

}
