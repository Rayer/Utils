package com.rayer.util.provisioner;
/*
This is ResourceProxy. It consumes several ResourceProvisioner and use it as cache orderly.

MemoryCacheResourceProvisioner<Bitmap, String> memCache;
FileSystemResourceProvisioner<Bitmap, String> fsCache;
InternetResourceProvisioner<Bitmap, String> inet;

ResourceProxy<Bitmap, String> proxy = new ResourceProxy<Bitmap, String>();
proxy.addProvisioner(memCahce);
proxy.addProvisioner(fsCache);
proxy.addProvisioner(inet);

Bitmap target = proxy.getResource();

It will see if memory have bitmap, if no, will see if it cached in FileSystem, if still no, it will download from Internet and fill FileSystem and Memory.
If FileSystem have cache, it will fill Memory 

of course, if Memory have cahce, it will only ask MemoryCache and skip others. And, there is a option named 'forced' to force it reload from last provisioner.
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class ResourceProxy<T, IndexType> {
	boolean mForceUpdate = false;
	
	ArrayList<ResourceProvisioner<T, IndexType> > mProvisionerList = new ArrayList<ResourceProvisioner<T, IndexType> >();
	ArrayList<ResourceProvisioner<T, IndexType> > mWaitToWriteList = new ArrayList<ResourceProvisioner<T, IndexType> >();
	/**
	 * Add a provisioner. The proxy will ORDERLLY try to obtain resource from these provisioners.<br>
	 * Be careful these provisioner will NOT check its existence before add into the proxy.
	 * @param provisioner root is ResourceProvisioner<T>. And there is some frequency used provisioner such like FileSystemResourceProvisioner and InternetResourceProvisioner 
	 * @return now provisioner count. 
	 */
	public int addProvisioner(ResourceProvisioner<T, IndexType> provisioner) {
		if(provisioner != null)
			mProvisionerList.add(provisioner);
		return mProvisionerList.size();
	}
	
	/**
	 * Toggles force update. Force update means use only LAST provisioner as resource and attempt to write it into previous provisioners.
	 * @param forceUpdate
	 */
	public void setForceUpdate(boolean forceUpdate) {
		mForceUpdate = forceUpdate;
	}
	
	/**
	 * Get resource via blocking method.
	 * @param listener as its name, usually not useful while in non-async mode, but may have some use in special case. Can be null.
	 * @return Resource
	 */
	public synchronized T getResource(ResourceProxyListener<T, IndexType> listener) {
		mWaitToWriteList.clear();
		
		ResourceProvisioner<T, IndexType> lastProvisioner = mProvisionerList.get(mProvisionerList.size() - 1);
		
		T target = null;
//		Iterator<ResourceProvisioner<T, IndexType> > itor = mProvisionerList.iterator();

			//Force Update機制調整 在Force Update下 只會取最後一個Provisioner並且把他寫入前面所有的Provisioner
			
//			if(mForceUpdate == true) {
//				targetProvisioner = lastProvisioner;
//				try {
//					target = lastProvisioner.getResource(getIndentificator());
//					if(target != null)
//						for(ResourceProvisioner<T, IndexType> r : mProvisionerList) {
//							if(r != targetProvisioner)
//								r.setResource(getIndentificator(), target);
//						}
//							
//				} catch (IOException e) {
//					listener.onNotifyErrorOccures(e);
//					e.printStackTrace();
//				}
//				
//				return target;
//			}
//			
//			if(targetProvisioner != lastProvisioner)
//				try {
//					target = mForceUpdate ? null : targetProvisioner.getResource(getIndentificator());
//					
//				} catch (IOException e) {
//					if(listener != null)
//						listener.onNotifyErrorOccures(e);
//					else
//						e.printStackTrace();
//				}
//			
//			if(target != null) {
//				for(ResourceProvisioner<T, IndexType> r : mWaitToWriteList)
//					r.setResource(getIndentificator(), target);
//				break;
//			}
//			
//			mWaitToWriteList.add(targetProvisioner);
		
		if(mForceUpdate) {
			try {
				target = lastProvisioner.getResource(getIndentificator());
			} catch (IOException e) {
				if(listener != null)
					listener.onNotifyErrorOccures(e);
				e.printStackTrace();
			}
			if(target != null)
				for(ResourceProvisioner<T, IndexType> r : mProvisionerList)
					if(r != lastProvisioner) {
						r.setResource(getIndentificator(), target);
					}
		}
		else
		{
			for(ResourceProvisioner<T, IndexType> targetProvisioner : mProvisionerList) {
				try {
					target = targetProvisioner.getResource(getIndentificator());

				} catch (IOException e) {
					if(listener != null)
						listener.onNotifyErrorOccures(e);
					e.printStackTrace();
				}
				if(target == null)
					mWaitToWriteList.add(targetProvisioner);
				else {
//					try {
//						throw new Exception("Get from : " + targetProvisioner.getClass().getName());
//					}catch(Exception e) {
//						e.printStackTrace();
//					}
					for(ResourceProvisioner<T, IndexType> r : mWaitToWriteList)
						if(r != targetProvisioner) {
							r.setResource(getIndentificator(), target);
						}
					break;
				}
			}
		}

		if(listener != null)
			listener.onFinishedLoading(target, getIndentificator());
		return target;
	}
	
	/**
	 * Get resource via non-blocking(asynchronized) method
	 * @param listener listener as its name mentioned.
	 */
	
	static Executor defaultExecutor = Executors.newFixedThreadPool(10);
	Executor exec;
	
	public void setExecutor(Executor inExec) {
		exec = inExec;
	}
	
	private Executor getExec() {
		if(exec == null)
			return defaultExecutor;
		
		return exec;
	}
	
	public void getResourceAsync(final ResourceProxyListener<T, IndexType> listener) {
		
		getExec().execute(new Runnable(){

			@Override
			public void run() {	
				getResource(listener);
				//T target = getResource(listener);
				//listener.onFinishedLoading(target);				
			}});
		
//		Thread t = new Thread(new Runnable(){
//
//			@Override
//			public void run() {
//				T target = getResource(listener);
//				listener.onFinishedLoading(target);
//			}});
//		t.start();
		
	}
	
	public abstract IndexType getIndentificator();
	
	public interface ResourceProxyListener<T, IndexType> {
		void onNotifyCacheAvailible(boolean isCacheAvailible);
		void onNotifyCacheDownloadCompleted();
		void onNotifyErrorOccures(Exception e);
		void onFinishedLoading(T t, IndexType index);
	}
	
	public abstract static class DefResourceProxyListener<T, IndexType> implements ResourceProxyListener<T, IndexType> {

		@Override
		public void onNotifyCacheAvailible(boolean isCacheAvailible) {
			
		}

		@Override
		public void onNotifyCacheDownloadCompleted() {
			
		}

		@Override
		public void onNotifyErrorOccures(Exception e) {
			e.printStackTrace();
		}

		@Override
		public abstract void onFinishedLoading(T t, IndexType index);
		
	}
}



