package com.rayer.util.misc;


/**
 * Another unfinished works, do not use it.
 * @deprecated
 * @author rayer
 */
public class TimingThreadRaw extends Thread {
	
	long mEndTime;
	TimingThreadRawListener mListener;
	
	public interface TimingThreadRawListener {
		void OnTimeUp(long TimeStamp);
	}
	
	public TimingThreadRaw(long time, TimingThreadRawListener listener) {
		mEndTime = System.currentTimeMillis() + time;
		mListener = listener;
		start();
	}
	
	public void addTime(long time) {
		mEndTime += time;
		
	}
	
	@Override
	public void run() {
		try {
			while(System.currentTimeMillis() < mEndTime) {
				Thread.sleep(100);
			}
			
			if(mListener != null)
				mListener.OnTimeUp(System.currentTimeMillis());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}