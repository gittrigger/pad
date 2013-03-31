package com.pad;

import java.io.IOException;

//import com.android.email;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.WebView;
import android.app.Activity;

import android.view.View;

public class Uplink {

	private static final String TAG = "Pad Uplink";
	public String UPLINK = "http://tool.weboutech.com/uplink";
	private WebView mHttpConsole;
	private Context mContext;

	
	public Uplink(Context ctx){
    	this.mContext = ctx;
    }


	public void SocketTest() throws IOException {
		
		Log.i(TAG,"socketTest()");
		
        ConnectivityManager mgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        int sleepMS = 100;
        try {Thread.sleep(sleepMS);} catch (InterruptedException e) {Log.w(TAG,"Thread Sleep interrupted by exception");}
        for(int serviceAttempt = 1; serviceAttempt < 5; serviceAttempt++){

        	sleepMS = 10000;
        		 
        	NetworkInfo netInfo = mgr.getActiveNetworkInfo(); 
        	if (netInfo != null) {
        	        Log.i(TAG, "netInfo " + netInfo.isAvailable() );
        	        if(netInfo.isAvailable()){
        	        	Log.i(TAG,"Network is Available");
        	        }else{
        	        	Log.e(TAG,"Network is not available");
        	        }
        	        if(netInfo.isConnectedOrConnecting() && !netInfo.isConnected()){
        	        	Log.i(TAG,"Network is in the process of connecting.");
        	        	break;
        	        }else if(netInfo.isConnectedOrConnecting()){
        	        	Log.i(TAG,"Network is Connected");
        	        	break;
        	        }else{
        	        	Log.e(TAG,"Network is not connected.");
        	        }
        	        if(netInfo.isAvailable() || netInfo.isConnected() || netInfo.isConnectedOrConnecting() ){
        	        	int loopcnt = 0;
        	        	for(loopcnt=0;loopcnt < 5;loopcnt++){
        	        		if(netInfo.isConnectedOrConnecting() ){
        	        			Log.w(TAG,"Network is in the process of connecting.");
        	        			try {Thread.sleep(100);} catch (InterruptedException e) {Log.w(TAG,"Thread Sleep interrupted by exception");}
        	        		}
        	        	}
        	        	Log.i(TAG, "Passed Network Test");
        	        	
        	            //mHttpConsole = (WebView) findViewById(R.id.httpconsole);
        	            //mHttpConsole.loadUrl("file:///android_asset/uplink.html");
        	        }
        	}else{
        		Log.e(TAG,"network not available");
        	}
  
        }
        
        	
        
	}
}


