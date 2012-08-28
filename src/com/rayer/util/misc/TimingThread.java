package com.rayer.util.misc;

/**
 * A not completed thing.... do not use it
 * @deprecated
 * @author rayer
 */
@SuppressWarnings("unused")
public class TimingThread extends Thread {
	private long mStartTime;
	private long mDelayTime;
	private int mRepeat = -1;
	
	private boolean mIsRunning = false;
	
	public TimingThread(TimingThreadListener listener) {
		
	}
	
	private boolean isInfinity() {
		return mRepeat <= -1;
	}
	
	private void terminate() {
		mIsRunning = true;
	}
	
	
	
	public static interface TimingThreadListener {
		void onThreadStart(long time);
		void onRepeat(long time, int repeatRemaining);
		void onEnd(long time);
	}

}
