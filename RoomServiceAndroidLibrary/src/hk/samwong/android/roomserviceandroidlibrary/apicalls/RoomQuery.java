package hk.samwong.android.roomserviceandroidlibrary.apicalls;

import hk.samwong.android.roomserviceandroidlibrary.constants.Defaults;
import hk.samwong.android.roomserviceandroidlibrary.constants.HttpVerb;
import hk.samwong.android.roomserviceandroidlibrary.fingerprintCollection.WifiScanner;
import hk.samwong.android.roomserviceandroidlibrary.helpers.AuthenticationDetailsPreperator;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hk.samwong.roomservice.commons.dataFormat.AuthenticationDetails;
import hk.samwong.roomservice.commons.dataFormat.ResponseWithReports;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;
import hk.samwong.roomservice.commons.helper.InstanceFriendlyGson;
import hk.samwong.roomservice.commons.parameterEnums.Operation;
import hk.samwong.roomservice.commons.parameterEnums.ParameterKey;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;

/**
 * First way to use it, is to simply execute this. This would do a wifi scan and then do the api calls. You get the Reports in return.
 * Second way to use this, is to supply a wifi scan. This class will take care of the api calls. Useful for polling.
 * @author wongsam
 * 
 */
public abstract class RoomQuery extends
		APICaller<Activity, Integer, ResponseWithReports> {

	private WifiInformation wifiscan;

	public RoomQuery(Context context) {
		super(context);
	}

	public RoomQuery(Context context, WifiInformation wifiscan) {
		super(context);
		this.wifiscan = wifiscan;
	}

	abstract protected void onPostExecute(ResponseWithReports result);

	/*
	 * Should always return a proper object. ie not null.
	 */
	protected ResponseWithReports doInBackground(Activity... param) {
		if (param.length == 0) {
			throw new IllegalArgumentException(
					"RoomQuery requires an activity to perform wifi scanning.");
		}
		Activity activity = param[0];

		if (wifiscan == null) {
			wifiscan = WifiScanner
					.getWifiInformation(activity);
		}

		AuthenticationDetails authenticationDetails = new AuthenticationDetailsPreperator()
				.getAuthenticationDetails(getContext());

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.OPERATION
				.toString(), Operation.CLASSIFY.toString()));
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.CLASSIFIER
				.toString(), Defaults.classifier.toString()));
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.OBSERVATION
				.toString(), new Gson().toJson(wifiscan)));
		nameValuePairs
				.add(new BasicNameValuePair(ParameterKey.AUENTICATION_DETAILS
						.toString(), AuthenticationDetailsPreperator
						.getAuthenticationDetailsAsJson(authenticationDetails)));

		try {
			String result = getJsonResponseFromAPICall(HttpVerb.POST,
					nameValuePairs);
			return InstanceFriendlyGson.gson.fromJson(result,
					new TypeToken<ResponseWithReports>() {
					}.getType());
		} catch (Exception e) {
			addException(e);
		}
		return (ResponseWithReports) new ResponseWithReports().withReturnCode(
				ReturnCode.UNRECOVERABLE_EXCEPTION).withExplanation(
				"Failed to complete API call");

	}

}