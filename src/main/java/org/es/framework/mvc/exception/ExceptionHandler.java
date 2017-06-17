package org.es.framework.mvc.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;

@ControllerAdvice
class ExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger("ErrorLog");

  @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
  public ModelAndView exception(Exception exception, WebRequest request) {
    ModelAndView modelAndView = new ModelAndView("/views/admin/error");
    Throwable rootCause = Throwables.getRootCause(exception);
    modelAndView.addObject("errorMessage", rootCause);
    LOGGER.error(rootCause.toString(), exception);
    return modelAndView;
  }
}