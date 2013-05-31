package com.rayer.util.serializer;

import java.io.IOException;
import java.io.Serializable;

public class NativeZippedSerializer<TARGET extends Serializable> extends NativeSerializer<TARGET> {
	@Override
	public byte[] serialize(TARGET object) throws IOException {		
		return super.serialize(object);
	}

	@Override
	public TARGET deserialize(byte[] input) throws IOException,
			ClassNotFoundException {
		return super.deserialize(input);
	}
	
}
