package org.es.framework.mvc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.Assert;

public class ExtResourceBundleMessageSource extends ResourceBundleMessageSource {

	private String[] basenames = new String[0];

	public void setBasenames(String... basenames) {
		if (basenames != null) {
			this.basenames = new String[basenames.length];
			for (int i = 0; i < basenames.length; i++) {
				String basename = basenames[i];
				Assert.hasText(basename, "Basename must not be empty");
				this.basenames[i] = basename.trim();
			}
		} else {
			this.basenames = new String[0];
		}
		super.setBasenames(basenames);
	}

	public ExtResourceBundleMessageSource() {
		super();
	}

	public Map<String, String> getAllMessageSource(Locale locale) {
		Map<String, String> messageMap = new HashMap<String, String>();
		for (int i = 0; i < basenames.length; i++) {
			ResourceBundle resourceBundle = super.getResourceBundle(basenames[i], locale);
			if(resourceBundle == null){
				resourceBundle = super.getResourceBundle(basenames[i],Locale.getDefault());
				if(resourceBundle == null){
					continue;
				}
			}
			Enumeration<String> keys = resourceBundle.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				messageMap.put(key, resourceBundle.getString(key));
			}
		}
		return messageMap;
	}

}
