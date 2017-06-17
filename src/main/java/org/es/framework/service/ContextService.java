package org.es.framework.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service(value = "contextService")
public class ContextService implements ApplicationContextAware {

	private static ContextService contextService;

	private ThreadLocal<Map<String, Object>> contextMap = new ThreadLocal<Map<String, Object>>();

	public void _init() {
		contextMap.set(new HashMap<String, Object>());
	}

	public void _set(String key, Object value) {
		this.contextMap.get().put(key, value);
	}

	public Object _get(String key) {
		return this.contextMap.get().get(key);
	}

	public void _clear() {
		this.contextMap.remove();
	}

	public static void init() {
		ContextService.contextService._init();
	}

	public static Object get(String key) {
		return ContextService.contextService._get(key);
	}

	public static void set(String key, Object value) {
		ContextService.contextService._set(key, value);
	}

	public static void setLoginStates(Object value) {
		ContextService.contextService._set("loginStates", value);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ContextService.contextService = (ContextService) applicationContext.getBean("contextService");
	}

}
