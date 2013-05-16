package com.rayer.util.databridge;

import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONConverter {
	
	/**
	 * This is a generic JSON Converter, it will get all 'public' field and fetch data in JSONObject according to field name.
	 * SomeJAVABean bean = new SomeJAVABean(); </br>
	 * JSONConverter.extractFromJSON(bean.getClass(), bean, someJSONObject);
	 * @param targetClass bean .class
	 * @param targetObject bean instance
	 * @param userObject A json object for this bean
	 */
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
