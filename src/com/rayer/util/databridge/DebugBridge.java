package com.rayer.util.databridge;

import java.lang.reflect.Field;

public class DebugBridge {
	
	public static String attachDebugInfo(Class<?> targetClass, Object targetObj) {
		if(targetObj == null)
			return "This is a null object!";
		Field[] fields = targetClass.getFields();
		
		StringBuilder sb = new StringBuilder();
		for(Field f : fields) {
			try {
				sb.append(f.getName() + " = " + f.get(targetObj) + " ");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
			
	}

}
