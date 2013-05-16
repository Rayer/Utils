package com.rayer.util.provisioner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A DAO with Download progress support, it can be use solely without ResourceProxy or combined.
 * @author rayer
 * @param <IndexType>
 * @see ResourceProxy
 */
public abstract class InternetResourceProvisionerToFile<IndexType> extends
		InternetResourceProvisioner<File, IndexType> {

	public InternetResourceProvisionerToFile(InternetResourceProvisionerToFileListener inListener) {
		mListener = inListener;
	}
	
	InternetResourceProvisionerToFileListener mListener;
	InternetResourceProvisionerToFileListener mMonitorListener;
	

	protected abstract String getCurrentTempPath();
	
	
	@Override
	public File getResource(IndexType identificator){
		File f = null;
		try {
			f = super.getResource(identificator);
		} catch (IOException e) {
			if(mListener != null) {
				mListener.onNetworkError(e);
				
			}
			e.printStackTrace();
		}
		return f;
	}


	@Override
	public File formFromStream(InputStream is) {
		
		File tempPath = new File(getCurrentTempPath());
		tempPath.mkdirs();
		
		//logI("getting input stream : " + is.toString());
		
		if(mListener != null) {

			if(mMonitorListener == null)
				mMonitorListener = createDefaultListener();

			mListener.onStartDownload();
			mMonitorListener.onStartDownload();
		}
		
		//logI("listener : " + mListener + " and monitor listener : " + mMonitorListener);
		
		//雖然機率很低 還是得check一下有沒有重複檔案的問題
		File temp = new File(getCurrentTempPath() + "/" + System.currentTimeMillis());
		while(temp.exists() != false)
			temp = new File(getCurrentTempPath() + "/" + System.currentTimeMillis());


		FileOutputStream fos;
		try {
			fos = new FileOutputStream(temp);
			byte[] buf = new byte[8192];
			int len;
			int currentDownloaded = 0;
			int currentPercentage = 0;
			
			boolean isInterrupted = false;
			while((len = is.read(buf)) > 0 && isInterrupted == false) {

				fos.write(buf, 0, len);
				currentDownloaded += len;
				int percent = (currentDownloaded * 100)/ getTotalLength();
				//if(currentPercentage < percent) {

					currentPercentage = percent;
					if(mListener != null) {
						isInterrupted = mListener.onCheckInterrupt();
						if(isInterrupted == false) {

							mListener.onPrePercentDownloaded(currentPercentage, currentDownloaded, getTotalLength());
							mMonitorListener.onPrePercentDownloaded(currentPercentage, currentDownloaded, getTotalLength());
						}
					}
				//}
			}
			
			is.close();
			fos.flush();
			fos.close();
			
			if(isInterrupted == true)
				return null;
			
		} catch (FileNotFoundException e) {
			handleFileError(temp, e);
			return null;
		} catch (IOException e) {
			if(mListener != null){
				mListener.onNetworkError(e);
				mMonitorListener.onNetworkError(e);
			}
			e.printStackTrace();
			return null;
		}
		
		
		File ret = null;
		
		try {
			FileInputStream tempIs = new FileInputStream(temp);
			File root = new File(getTargetFileDir());
			root.mkdirs();
			
			ret = new File(getTargetFileDir() + "/" + getIdentificator());
			if(ret.exists())
				ret.delete();
			ret.getParentFile().mkdirs();
			FileOutputStream targetFos = new FileOutputStream(ret);
			byte[] buf = new byte[8192];
			int len;
			while((len = tempIs.read(buf)) > 0) 
				targetFos.write(buf, 0, len);
			
			targetFos.flush();
			targetFos.close();
			tempIs.close();
		} catch (FileNotFoundException e) {
			handleFileError(ret, e);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		if(mListener != null) {
			mListener.onEndDownload();
			mMonitorListener.onEndDownload();
		}
		
		temp.delete();
		
		return ret;

	}



//作一個甚麼都不做的listener
	private InternetResourceProvisionerToFileListener createDefaultListener() {
		return new InternetResourceProvisionerToFileListener(){

			@Override
			public void onPrePercentDownloaded(int percentage, int downloaded,
					int totalSize) {
				
			}

			@Override
			public void onStartDownload() {
				
			}

			@Override
			public void onEndDownload() {
				
			}

			@Override
			public boolean onCheckInterrupt() {
				return false;
			}

			@Override
			public void onNetworkError(IOException e) {
				
			}

			@Override
			public void onFilesystemError(FileNotFoundException e) {
				
			}

			
		};
	}


	void handleFileError(File ret, FileNotFoundException e) {
		if(mListener != null) {
			mListener.onFilesystemError(e);
			mMonitorListener.onFilesystemError(e);
		}
		e.printStackTrace();
	}


	@Override
	public abstract String getUrlAddress(IndexType identificator);
	public abstract String getTargetFileDir();

	@Override
	public boolean clearAllCachedResource(){
		return false;
	}
	//public abstract boolean hasError();
}
