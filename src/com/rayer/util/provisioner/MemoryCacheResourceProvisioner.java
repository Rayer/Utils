package com.rayer.util.provisioner;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
 
public abstract class MemoryCacheResourceProvisioner<T, IndexType> implements
		ResourceProvisioner<T, IndexType> {
	HashMap<IndexType, ResourceElement > mResourceMap = new HashMap<IndexType, ResourceElement >();
	
	public MemoryCacheResourceProvisioner() {
	}

	@Override
	public T getResource(IndexType identificator) {
		ResourceElement target = mResourceMap.get(identificator);
		if(target == null)
			return null;
		
		++target.referenceCount;
		return target.object;
	}

	@Override
	public boolean setResource(IndexType identificator, T targetResource) {
		ResourceElement target = mResourceMap.get(identificator);
		if(target == null) {
			target = new ResourceElement(targetResource);
			mResourceMap.put(identificator, target);
		}
		
		return false;
	}
	
	@Override
	public boolean dereferenceResource(IndexType identificator) {
		ResourceElement target = mResourceMap.get(identificator);
		if(target == null)
			return false;
		
		--target.referenceCount;
		return true;
	}
	
	/**
	 * Clean up all rc = 0 object.
	 */
	public void refreshElement() {
		Set<Entry<IndexType, ResourceElement> > set = mResourceMap.entrySet();
		
		for(Entry<IndexType, ResourceElement> e : set) {
			if(e.getValue().referenceCount <= 0)
				destroyElement(e.getValue().object);
			
			mResourceMap.remove(e.getKey());
		}
	}

	
	/**
	 * define how to destroy an object.
	 * @param source
	 * @return
	 */
	public abstract boolean destroyElement(T source);

	
	class ResourceElement {
		ResourceElement(T inResource) {
			object = inResource;
		}
		
		T object;
		int referenceCount;
	}

}
