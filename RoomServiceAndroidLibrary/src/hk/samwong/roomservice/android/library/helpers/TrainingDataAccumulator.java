package hk.samwong.roomservice.android.library.helpers;


import hk.samwong.roomservice.android.library.constants.Defaults;
import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.android.library.fingerprintCollection.WifiScanner;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;

/**
 * Quick a hacky way of doing things. Looking for a better way to do this.
 * Ideally the onPreExecute bit should be in doInBackground, yet AsyncTask has to be started from UI thread.
 * ie it must be in either onPreExecute / onProgressUpdate / onPostExecute
 * 
 * I do want to keep onProgressUpdate for real progress update, so I've decided to put it in onPreExecute.
 *  
 * The problem with onPreExecute is that the poller would run before the user calls execute on TrainingDataAccumulator.
 * So I use the Context to execute the poller in the UI thread.
 * 
 * @author wongsam
 *
 */
public abstract class TrainingDataAccumulator extends
		AsyncTaskWithExceptions<Activity, WifiInformation, List<WifiInformation>> {

	private final String roomName;
	private final List<WifiInformation> fingerprintList = new ArrayList<WifiInformation>();
	
	public TrainingDataAccumulator(String roomName){
		this.roomName = roomName;
	}
	
	@Override
	protected abstract void onCancelled(List<WifiInformation> results);
	
//	@Override
//	protected void onPreExecute() {
//		poller = new WifiScannerPoller() {
//			@Override
//			protected void onProgressUpdate(WifiInformation... scanResult) {
//				if(!trainingAsyncTask.isCancelled()){
//					fingerprintList.add(scanResult[0]);
//					trainingAsyncTask.publishProgress(scanResult[0]);
//				}else{
//					this.cancel(false);
//				}
//			}
//			protected void onCancelled() {
//				blocker.countDown();
//			};
//		};
//	}
	
	@Override
	protected List<WifiInformation> doInBackground(Activity... params) {
		if(params.length != 1){
			addException(new IllegalArgumentException("Expects a single activity as param."));
		}
		final Activity activity = params[0];
		// execute the poller
		WifiInformation datapoint = null;
		WifiInformation prevScan = null;
		
		long lastUpdate = System.currentTimeMillis();
		while(!isCancelled()){
			datapoint = WifiScanner.getWifiInformation(activity);
		
			if(!datapoint.equals(prevScan)){
				Log.i(LogTag.CLIENT.toString(), "Obtained new fingerprint after " + (lastUpdate - System.currentTimeMillis()) + "ms");
				prevScan = datapoint;
				lastUpdate = System.currentTimeMillis();
				publishProgress(datapoint);
				fingerprintList.add(datapoint);
			}else{
				Log.v(LogTag.CLIENT.toString(), "Same old fingerprint");
			}
			SystemClock.sleep(Defaults.SAMPLING_INTERVAL_IN_MILLISEC);
		}
		return fingerprintList;
	}
	
	public String getRoomName() {
		return roomName;
	}	

}
