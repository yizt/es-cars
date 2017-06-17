package org.es.framework.mvc.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.es.framework.mvc.ExtMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class ValidatorUtil implements ApplicationContextAware {
	private static final Logger log = LoggerFactory.getLogger(ValidatorUtil.class);

	private static final String DOT = ".";

	private static Validator validator;

	@SuppressWarnings({ "rawtypes" })
	public static void checkDomain(Object object, Class group){
		if (validator == null || object == null) {
			throw new IllegalArgumentException("checkDomain arguments is null");
		}

		Set<ConstraintViolation<Object>> set = validator.validate(object, group);

		Map<String, String> map = handerConstraintViolation(set);

		if (map.size() > 0) {
			throw new DomainValidationException(map);
		}
	}

	public static void checkDomain(Object object) {
		checkDomain(object, Default.class);
	}

	public static <T> Map<String, String> handerConstraintViolation2(Set<ConstraintViolation<?>> set) {
		Map<String, String> map = new HashMap<String, String>();

		for (ConstraintViolation<?> failure : set) {
			String name = failure.getRootBeanClass().getSimpleName().toLowerCase() + DOT
					+ failure.getPropertyPath().toString();
			String realName = ExtMessageSource.getMessage(name, name);
			int spaceNum = 1;
			if (map.keySet().contains(realName)) {
				spaceNum++;
				for (int i = 0; i < spaceNum; i++) {
					realName = realName.concat(" ");
				}
			}
			if (log.isDebugEnabled()) {
				log.debug(realName + failure.getMessage());
			}
			map.put(realName, failure.getMessage());
		}
		return map;
	}

	public static <T> Map<String, String> handerConstraintViolation(Set<ConstraintViolation<Object>> set) {
		Map<String, String> map = new HashMap<String, String>();

		for (ConstraintViolation<?> failure : set) {
			String name = failure.getLeafBean().getClass().getSimpleName().toLowerCase() + DOT
					+ failure.getPropertyPath().toString();
			String realName = ExtMessageSource.getMessage(name, name);
			int spaceNum = 1;
			if (map.keySet().contains(realName)) {
				spaceNum++;
				for (int i = 0; i < spaceNum; i++) {
					realName = realName.concat(" ");
				}
			}
			if (log.isDebugEnabled()) {
				log.debug(realName + failure.getMessage());
			}
			map.put(realName, failure.getMessage());
		}
		return map;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		LocalValidatorFactoryBean _validator = applicationContext.getBean(LocalValidatorFactoryBean.class);
		validator = _validator.getValidator();
	}

}
