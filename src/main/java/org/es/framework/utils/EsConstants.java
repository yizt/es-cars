package org.es.framework.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EsConstants implements ApplicationContextAware {

	public final static String split = "\t\t";

	public static class SessionName {

		public static String currentUser = "currentUser";

		public static String currentMenus = "currentMenus";

		public static String currentMenuUrls = "currentMenuUrls";
	}

	public static class MenuLevel {

		public static int root = 1;

		public static int second = 2;
	}

	public static class Status {
		public static int init = 0;
		public static int running = 1;
		public static int success = 2;
		public static int failure = 3;
		public static int pause = 4;
		public static int stop = 5;
	}
	
	public static class YesNO{
		public static int yes = 1;
		public static int no = 0;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	}
}
