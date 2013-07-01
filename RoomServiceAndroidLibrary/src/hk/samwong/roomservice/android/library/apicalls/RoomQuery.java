package hk.samwong.roomservice.android.library.apicalls;


import hk.samwong.roomservice.android.library.constants.Defaults;
import hk.samwong.roomservice.android.library.constants.HttpVerb;
import hk.samwong.roomservice.android.library.fingerprintCollection.WifiScanner;
import hk.samwong.roomservice.commons.dataFormat.ResponseWithReports;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;
import hk.samwong.roomservice.commons.helper.InstanceFriendlyGson;
import hk.samwong.roomservice.commons.parameterEnums.Operation;
import hk.samwong.roomservice.commons.parameterEnums.ParameterKey;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * First way to use it, is to simply execute this. This would do a wifi scan and then do the api calls. You get the Reports in return.
 * Second way to use this, is to supply a wifi scan. This class will take care of the api calls. Useful for polling.
 * @author wongsam
 * 
 */
public abstract class RoomQuery extends
		APICaller<Void, Integer, ResponseWithReports> {

	private WifiInformation wifiscan;
	private Activity activity;
	
	public RoomQuery(Activity activity) {
		this.activity = activity;
	}

	public RoomQuery(Activity activity, WifiInformation wifiscan) {
		this.wifiscan = wifiscan;
		this.activity = activity;
	}

	abstract protected void onPostExecute(ResponseWithReports result);

	/*
	 * Should always return a proper object. ie not null.
	 */
	protected ResponseWithReports doInBackground(Void... param) {
		if (wifiscan == null) {
			wifiscan = WifiScanner
					.getWifiInformation(activity);
		}

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.OPERATION
				.toString(), Operation.CLASSIFY.toString()));
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.CLASSIFIER
				.toString(), Defaults.classifier.toString()));
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.OBSERVATION
				.toString(), new Gson().toJson(wifiscan)));
		
		try {
			String result = getJsonResponseFromAPICall(HttpVerb.POST,
					nameValuePairs, activity);
			return InstanceFriendlyGson.gson.fromJson(result,
					new TypeToken<ResponseWithReports>() {
					}.getType());
		} catch (Exception e) {
			addException(e);
		}
		return (ResponseWithReports) new ResponseWithReports().setReturnCode(
				ReturnCode.UNRECOVERABLE_EXCEPTION).setExplanation(
				"Failed to complete API call");

	}

}