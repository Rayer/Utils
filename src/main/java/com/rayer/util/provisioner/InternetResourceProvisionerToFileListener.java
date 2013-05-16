package com.rayer.util.provisioner;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Only use with InternetResourceProvisionerToFile
 * @author rayer
 * @see InternetResourceProvisionerToFile
 */
public interface InternetResourceProvisionerToFileListener {
	/**
	 * While at checkpoint, provisioner will inform this method.<br>
	 * @param percentage current downloaded percentage
	 * @param downloaded current downloaded bytes
	 * @param totalSize total bytes needed to be downloaded.
	 */
	void onPrePercentDownloaded(int percentage, int downloaded, int totalSize);
	
	
	/**
	 * Occurs while download started
	 */
	void onStartDownload();
	
	/**
	 * Occurs while download ended
	 */
	void onEndDownload();

	/**
	 * Occurs while you want to interrupt the downloading progress.
	 */
	boolean onCheckInterrupt();
	
	/**
	 * Occurs while network error occurs.
	 * @param e
	 */
	void onNetworkError(IOException e);
	
	/**
	 * Occurs while File System error occurs. i.e. Permission problem, missing storage target(for ex. SD Card)...etc
	 * @param e
	 */
	void onFilesystemError(FileNotFoundException e);
	
	/**
	 * If this supports download monitor, it will inform download monitor
	 * @return the monitor that supports.
	 */	
	
}
