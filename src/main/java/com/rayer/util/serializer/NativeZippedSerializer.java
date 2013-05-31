package com.rayer.util.serializer;

<<<<<<< HEAD
import java.io.IOException;
import java.io.Serializable;
=======
import java.io.Serializable;

public class NativeZippedSerializer<TARGET extends Serializable> extends NativeSerializer<TARGET> {
>>>>>>> 3ae9036794525511616abb5d5e795e99308ddb10

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
