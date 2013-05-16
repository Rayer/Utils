package com.rayer.util.provisioner;

import java.io.IOException;

/**
 * This is core interface for any Resource Provisioner
 * @author rayer
 *
 * @param <Type> Resource Type, for example, String or Bitmap or anything
 * @param <IndexType> A unique identifier type to all resource. It can be any type, but identifier to all resource must be unique.
 */
public interface ResourceProvisioner<Type, IndexType> {
	/**
	 * Describe when ResourceProxy provides identifier, how to return a source
	 * @param identificator a unique identificator to each resource.
	 * @return the resource, if there is no such resource, simply return null.
	 * @throws IOException
	 */
	Type getResource(IndexType identificator) throws IOException;
	
	/**
	 * Describe when ResourceProvisioner provides ID and Resource, how to cache it.
	 * @param identificator
	 * @param targetResource
	 * @return
	 */
	boolean setResource(IndexType identificator, Type targetResource);
	
	/**
	 * For some RC Provisioner use, not use it now.
	 * @param identificator
	 * @return
	 */
	boolean dereferenceResource(IndexType identificator);
	
	/**
	 * Describe how to clean up all cache.
	 * @return
	 */
	boolean clearAllCachedResource();

}

