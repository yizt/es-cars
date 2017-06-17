package org.es.framework.security;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("myPasswordEncoder")
public class MyPasswordEncoder extends Md5PasswordEncoder {

	public MyPasswordEncoder() {
		super();
	}

}
