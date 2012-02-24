package com.rayer.util.provisioner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class FileSystemResourceProvisioner<T, IndexType> implements ResourceProvisioner<T, IndexType> {

	@Override
	public boolean setResource(IndexType identificator, T targetResource) {
		File file = new File(mCacheDir + identificator);
		/*
		if(file.exists())
			file.delete();
		*/
		try {
			FileOutputStream fout = new FileOutputStream(file);
			writeToOutputStream(targetResource, fout);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		
		return false;
	}

	String mCacheDir;
	
	public FileSystemResourceProvisioner(String cacheDir) {
		
		mCacheDir = cacheDir;
		if(mCacheDir.endsWith("/") == false)
			mCacheDir = mCacheDir + "/";

	}
	@Override
	public T getResource(IndexType identificator) {
		File parent = new File(mCacheDir);
		if(parent.exists() == false) {
			//Log.d("hamibook2", "Attemp to create subdirectory :" + mCacheDir + " result : " + parent.mkdirs());
			parent.mkdirs();
		}
		InputStream targetInputStream = null;
		
		File f = new File(mCacheDir + identificator);
		if(f.exists()) {
			T ret;
			try {
				targetInputStream = new FileInputStream(f);
				ret = formFromStream(targetInputStream);
				targetInputStream.close();
			} catch (FileNotFoundException e) {
				return null;
			} catch (IOException e) {				
				e.printStackTrace();
				return null;
			}
			return ret;
		}
		
		return null;
	}
	
	@Override
	public boolean dereferenceResource(IndexType identificator) {
		//do nothing
		return true;
	}
	
	public String getRootDir() {
		return mCacheDir;
	}
	
	public abstract T formFromStream(InputStream in);
	public abstract void writeToOutputStream(T target, FileOutputStream fo);
	
	@Override
	public boolean clearAllCachedResource() {
		return false;
	}
}
