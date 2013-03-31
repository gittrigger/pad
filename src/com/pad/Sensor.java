package com.pad;

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class Sensor {

	float[] sensorMax, sensorMin, sensorSum;
	int[] sensorCount;
	float sensorV;
	
	private final static String TAG = "Pad Sensor";

	private SensorManager mgr;
	private SensorListener mSensorListener;
	//private DbAdapter mSensorDb;
	

	public void onCreate(Bundle savedInstanceState) {
		//mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		//mgr.registerListener(mSensorListener, mgr.SENSOR_ACCELEROMETER);

    	
/*		mSensorDb = new DbAdapter(//requires context remake this class Storage);
		mSensorDb.open();
		mSensorDb.DATABASE_TABLE = "sensor";
		mSensorDb.DATABASE_CREATE = "create table sensor (_id integer primary key autoincrement, sensorid, entrytime, )";
		*/
	}
	
	public void onSensorChanged(int sensor, float[] values){
		for(int i = 0; i < values.length; i++){
			sensorV = values[i];
			Log.i(TAG,"sensor " + sensor + " :" + i + ":"+sensorV);
			
			/*sensorCount[sensor]++;
			if( sensorCount[sensor] == 1){
				sensorMax[sensor] = sensorV;
				sensorMin[sensor] = sensorV;
				sensorSum[sensor] = 0;
			}
			if(sensorV > sensorMax[sensor]){
				sensorMax[sensor] = sensorV;
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
				 wl.acquire();
				 //pm.userActivity(System.currentTimeMillis(), false);
				   //..screen will stay on during this section..
	    			 wl.release();
	    		}
	    		if(sensorV < sensorMin[sensor]){
	    			sensorMin[sensor] = sensorV;
	    		}
	    		sensorSum[sensor] += sensorV;
	    	}*/
	 
	    	//sensorMax[sensor] = sensorMax[sensor] - (sensorMax[sensor] / 10);
	    	//sensorMin[sensor] = sensorMin[sensor] + (sensorMax[sensor] / 10);
	    	
	    }
	}
	    
	    
}
