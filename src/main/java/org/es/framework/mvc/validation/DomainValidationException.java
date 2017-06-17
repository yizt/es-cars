package org.es.framework.mvc.validation;

import java.util.Map;

public class DomainValidationException extends RuntimeException {

	private static final long serialVersionUID = 4345504160068164233L;

	private Map<String, String> validateMap = null;

	public DomainValidationException(Map<String, String> validateMap) {
		super();
		this.validateMap = validateMap;
	}

	public Map<String, String> getValidateMap() {
		return validateMap;
	}
}
