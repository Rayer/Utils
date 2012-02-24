package com.rayer.util.databridge;



/**
 * This class is for some native class which be declared as 'final' so that can't change it's initial value.
 * @author rayer
 *
 * @param <T>
 */
public class ObjectWrapper<T> {
	@SuppressWarnings("unchecked")
	T mTargetObject = (T) new Object();
	
	public ObjectWrapper(T value) {
		mTargetObject = value;
	}
	
	public T getValue() {
		return mTargetObject;
	}
	
	public void setValue(T value) {
		mTargetObject = value;
	}
	
}
