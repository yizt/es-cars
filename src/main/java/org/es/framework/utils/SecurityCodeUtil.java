package org.es.framework.utils;

public class SecurityCodeUtil {

	
	public static String generateCode(){
		String code = "" ;
		
		for(int i =0 ; i< 6;i++)
		{
			int a = (int) (Math.random() * 9);
			code += String.valueOf(a);  
		}
		return code;
	}
	
}
