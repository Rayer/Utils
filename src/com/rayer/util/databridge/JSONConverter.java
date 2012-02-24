package com.rayer.util.databridge;

import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONConverter {
	
	public static void extractFromJSON(Class<?> targetClass, Object targetObject, JSONObject userObject) {
		Field[] fields = targetClass.getFields();
		for(Field f : fields) {
			try {
				f.set(targetObject, userObject.get(f.getName()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}
