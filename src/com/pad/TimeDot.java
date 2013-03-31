package com.pad;
// millisecond

import android.content.Context;
import android.util.Log;

public class TimeDot {
	private Context mContext;
	public int mTempralLength;
	final private static String TAG = "TimeDot";

	public void TimeDot(Context cmx){
		mContext = cmx;
		mTempralLength = 1000; // default to 1000 milliseconds
	}
	
	public void run(){
		try { Thread.sleep(mTempralLength); }
		catch(InterruptedException e){Log.w(TAG,"InterruptedException " + e.getLocalizedMessage());}
	}
	
}
