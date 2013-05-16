package com.rayer.util.provisioner;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.rayer.util.stream.PatchInputStream;


/**
 * Resource Provider as Internet connection. For usage, see ResourceProxy.java
 * @author rayer
 * @param <T, IndexType>
 * @see ResourceProxy
 * @see ResourceProvisioner
 */
public abstract class InternetResourceProvisioner<T, IndexType> implements ResourceProvisioner<T, IndexType> {

	IndexType mIdentificator;
	//private Map<String, List<String>> mHeaderFields;
	int mFileLength = 0;

	@Override
	public T getResource(IndexType identificator) throws IOException {
		
		mIdentificator = identificator;
		//完全不用管cache的問題 來一次連一次就對了
		InputStream is = createStream();
		if(is == null)
			return null;
		
		//修正某些jpeg的問題 不過相對的，不是jpeg的場合應該是不用patch
		is = new PatchInputStream(is);
		//if(is == null)
			//return null;
		
		T ret = formFromStream(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Some combinations (such as Bitmap + FileSystemResourceProvider) meets some difficulty to transform.<br>
	 * such it is a bit difficult to translate from Bitmap to FileSystem.<br>
	 * <p>
	 * To avoid such difficulty, use this method to get raw stream.<br>
	 * WARNING : YOU MUST ASSURE THIS STREAM WILL BE CLOSE MANUALLY!
	 * @return
	 * @throws IOException 
	 */
	public InputStream getResourceStream() throws IOException {
		return createStream();
	}

	public IndexType getIdentificator() {
		return mIdentificator;
	}

	@Override
	public boolean setResource(IndexType identificator, T targetResource) {
		
		return false;
	}
	
	/*
	 * Define how to form target from InputStream(from internet connection);
	 */
	public abstract T formFromStream(InputStream is);
	
	/**
	 * Specify the way how identificator turn into URL.
	 * If this process is no need, simply return identificator itself.
	 * @param indentificator
	 * @return
	 */
	public abstract String getUrlAddress(IndexType identificator);
	
	InputStream createStream() throws IOException {
		URL url = null;
		InputStream is = null;
		
		try {
		
			url = new URL(getUrlAddress(mIdentificator));
			URLConnection conn = url.openConnection();
						
			is = conn.getInputStream();
			mFileLength = conn.getHeaderFieldInt("content-length", -1);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//throw new RuntimeException();
			return null;
		}
		
		return is;
	}
	
	public int getTotalLength() {
		return mFileLength;
	}
	
	@Override
	public boolean dereferenceResource(IndexType identificator) {
		//do nothing
		return true;
	}
	
	@Override
	public boolean clearAllCachedResource() {
		return false;
	}
}
