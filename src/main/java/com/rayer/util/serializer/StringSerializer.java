package com.rayer.util.serializer;

import java.io.IOException;


public class StringSerializer implements ISerializer<String> {

	static final String ENCODING = "UTF-8";
	@Override
	public byte[] serialize(String object) throws IOException {
		if(object instanceof String == false)
			throw new IOException();
		
		return ((String)object).getBytes(ENCODING);
		
	}
	

	@Override
	public String deserialize(byte[] input) throws IOException {
		String ret = new String(input, ENCODING);
		return ret;
	}


}
