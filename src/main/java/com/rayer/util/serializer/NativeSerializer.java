package com.rayer.util.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class NativeSerializer<TARGET extends Serializable> implements ISerializer<TARGET> {

	@Override
	public byte[] serialize(TARGET object) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] ret;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(object);
		  ret = bos.toByteArray();
		  
		} finally {
		  out.close();
		  bos.close();
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TARGET deserialize(byte[] input) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(input);
		ObjectInput in = null;
		TARGET ret;
		try {
		  in = new ObjectInputStream(bis);
		  ret = (TARGET)in.readObject(); 
		} finally {
		  bis.close();
		  //in.close();
		}
		return ret;
	}

}
