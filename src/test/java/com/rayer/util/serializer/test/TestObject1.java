package com.rayer.util.serializer.test;

import java.io.Serializable;
import java.util.List;

public class TestObject1 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1057600611247492288L;
	List<String> stringList;
	int nativeInt;
	Integer boxedInt;
	long nativeLong;
	Long boxedLong;
	double nativeDouble;
	Double boxedDouble;
	
	static class InnerClass implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5498321524170550786L;
		byte innerPadding1;
		byte[] innerPadding2;
	}
	
	InnerClass inner1;
	transient InnerClass inner2;
	
}
