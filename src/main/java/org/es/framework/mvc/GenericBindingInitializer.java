package org.es.framework.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

public class GenericBindingInitializer implements WebBindingInitializer {

  @Override
  public void initBinder(WebDataBinder binder, WebRequest request) {
    String usualFormat = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat dateFormat = new SimpleDateFormat(usualFormat);
    dateFormat.setLenient(false);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,
        true));
    binder.registerCustomEditor(Integer.class, new CustomNumberEditor(
        Integer.class, true));
    binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class,
        true));
    binder.registerCustomEditor(Double.class, new CustomNumberEditor(
        Double.class, true));

  }

}
