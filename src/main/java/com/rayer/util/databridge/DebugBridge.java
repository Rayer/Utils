package com.rayer.util.databridge;

import java.lang.reflect.Field;
/**
 * This is just a set of simple debug tool
 * @author Rayer
 */
public class DebugBridge {
	
	/**
	 * This is a debug tool just simply output all 'public' fields.
	 * For a more advanced version to print out private/protect members, maybe in next release.
	 * Usage : String str = DebugBridge.attachDebugInfo(foo.getClass(), foo);
	 * @param targetClass .class target
	 * @param targetObj target obj
	 * @return String with all field / value, in format of 'somefield = 3'
	 */
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
