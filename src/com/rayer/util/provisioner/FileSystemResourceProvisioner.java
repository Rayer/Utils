package com.rayer.util.provisioner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.rayer.util.filesystem.FileUtil;

/**
 * A Resource Provisioner DAO for ResourceProxy. It serve as a resourece source from FileSystem.
 * For usage, see ResourceProxy.java
 * @author rayer
 * @param <T>
 * @param <IndexType>
 * @see ResourceProxy
 * @see ResourceProvisioner
 */
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
			fout.flush();
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return true;
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
	
	/**
	 * Describe how to form Resource from Inputstream
	 * @param in inputstream
	 * @return target resource
	 */
	public abstract T formFromStream(InputStream in);
	
	/**
	 * Describe how to output from Resource(Serialize)
	 * @param target target
	 * @param fo outputstream
	 */
	public abstract void writeToOutputStream(T target, FileOutputStream fo);
	
	@Override
	public boolean clearAllCachedResource() {
		return FileUtil.deleteTree(mCacheDir);
	}
}
