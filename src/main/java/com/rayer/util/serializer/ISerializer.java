package com.rayer.util.serializer;

import java.io.IOException;

public interface ISerializer<TARGET> {
	byte[] serialize(TARGET object) throws IOException;
	TARGET deserialize(byte[] input) throws IOException, ClassNotFoundException;
}
