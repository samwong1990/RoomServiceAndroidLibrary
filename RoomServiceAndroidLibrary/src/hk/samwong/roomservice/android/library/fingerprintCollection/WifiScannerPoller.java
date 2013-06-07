package hk.samwong.roomservice.android.library.fingerprintCollection;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;

import hk.samwong.roomservice.android.library.constants.Defaults;
import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.android.library.helpers.AsyncTaskWithExceptions;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;

/**
 * Android may report old wifi scans if the calls are in quick sucession. We use this poller to collect new wifi fingerprints.
 * Typical usage:
 * Define your logic with the new wifi fingerprints in the onProgressUpdate method.
 * 'execute()' this task
 * Call cancel when you're done with data collection.
 * Don't forget to use onCancelled instead of onPostExecute to clean up/whatever afterwards.
 * @author wongsam
 *
 */
public abstract class WifiScannerPoller extends
		AsyncTaskWithExceptions<Activity, WifiInformation, Void> {
	
	WifiInformation prevScan;
	
	@Override
	abstract protected void onProgressUpdate(WifiInformation... values);
	
	@Override
	protected void onPostExecute(Void result) {
		throw new NoSuchMethodError("You should use onCancelled() to work with your data");
	}
	
	@Override
	protected Void doInBackground(Activity... params) {
		if(params.length != 1){
			addException(new IllegalArgumentException("Expects a single activity as param."));
		}
		Activity activity = params[0];
		WifiInformation datapoint = null;
		long lastUpdate = System.currentTimeMillis();
		while(!isCancelled()){
			datapoint = WifiScanner.getWifiInformation(activity);
			
			if(!datapoint.equals(prevScan)){
				Log.i(LogTag.CLIENT.toString(), "Obtained new fingerprint after " + (lastUpdate - System.currentTimeMillis()) + "ms");
				prevScan = datapoint;
				lastUpdate = System.currentTimeMillis();
				publishProgress(datapoint);
			}else{
				Log.v(LogTag.CLIENT.toString(), "Same old fingerprint");
			}
			SystemClock.sleep(Defaults.SAMPLING_INTERVAL_IN_MILLISEC);
		}
		return null;
	}
}
