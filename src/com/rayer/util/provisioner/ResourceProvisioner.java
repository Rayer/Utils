package com.rayer.util.provisioner;

import java.io.IOException;

public interface ResourceProvisioner<Type, IndexType> {
	Type getResource(IndexType identificator) throws IOException;
	boolean setResource(IndexType identificator, Type targetResource);
	boolean dereferenceResource(IndexType identificator);
	boolean clearAllCachedResource();

}

