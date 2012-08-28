package com.rayer.util.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * A not trusted download manager, don't use it
 * It will be replaced soon.
 * @author rayer
 *
 */
public class DownloadManager {
	
	interface IDMListener {
		void OnStartDownloading(InputStream in, long startTimeStamp);
		void OnQueued(long Timestamp);
		void OnFinished(long Timestamp);
		void OnNetworkError(IOException e);
	}
	
	enum DownloadStatus {Stopped, In_Queued, Canceled, Downloading, Finished, Error};
	boolean mIsRunning = true;
	int mSoftLimit = 3;

	@SuppressWarnings("unused")
	private class DownloadHandle {
		int priority = 2; //not used yet
		long requestTimeStamp = 0; 
		long startTimeStamp = 0;
		long endTimeStamp = 0;
		DownloadStatus status;
		InputStream networkStream;
		URL targetUrl;
		IDMListener targetListener;
		Thread handlingThread;
		
		void createDownloadThread() {
			handlingThread = new Thread() {
				@Override
				public void run() {
					try {
						startTimeStamp = System.currentTimeMillis();
						networkStream = targetUrl.openConnection().getInputStream();
						status = DownloadStatus.Downloading;
						targetListener.OnStartDownloading(networkStream, startTimeStamp);
						networkStream.close();
						endTimeStamp = System.currentTimeMillis();
						targetListener.OnFinished(endTimeStamp);
						
					} catch (IOException e) {
						status = DownloadStatus.Error;
						targetListener.OnNetworkError(e);
						e.printStackTrace();
					}
				}
			};
			handlingThread.start();
		}
	}
	
	ArrayList<DownloadHandle> mPendingList = new ArrayList<DownloadHandle>();
	ArrayList<DownloadHandle> mProcessingList = new ArrayList<DownloadHandle>();
	
	/**
	 * Add a request to download manager.
	 * @param targetUrl
	 * @param priority
	 * @param listener
	 * @return
	 */
	public int addRequest(URL targetUrl, int priority, IDMListener listener) {
		
		DownloadHandle handle = new DownloadHandle();
		handle.targetUrl = targetUrl;
		handle.priority = priority;
		handle.targetListener = listener;
		handle.status = DownloadStatus.In_Queued;
		handle.requestTimeStamp = System.currentTimeMillis();
		handle.targetListener.OnQueued(handle.requestTimeStamp);
		
		mPendingList.add(handle);
		
		return mPendingList.size();
	}
	
	Thread mWorkingThread = new Thread() {
		@Override
		public void run() {
			while(mIsRunning) {
				processingWorkingthread();
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					mIsRunning = false;
					e.printStackTrace();
				}
			}
		}

		private void processingWorkingthread() {
			while(mPendingList.size() != 0 && mProcessingList.size() < mSoftLimit) {
				DownloadHandle dh = mPendingList.get(0);
				mPendingList.remove(0);
				dh.createDownloadThread();
				mPendingList.remove(dh);
				mProcessingList.add(dh);
			}
			
			for(DownloadHandle d : mProcessingList) {
				if(d.handlingThread.isAlive() == false) {
					mProcessingList.remove(d);
					
				}
			}
				
		}

		
	};
	
	DownloadStatus queryDownloadTicketStatus() {
		return DownloadStatus.In_Queued;
	}

}
