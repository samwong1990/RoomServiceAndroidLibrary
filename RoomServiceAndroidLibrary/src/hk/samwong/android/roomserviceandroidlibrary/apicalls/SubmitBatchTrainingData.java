package hk.samwong.android.roomserviceandroidlibrary.apicalls;

import hk.samwong.android.roomserviceandroidlibrary.constants.HttpVerb;
import hk.samwong.android.roomserviceandroidlibrary.constants.LogTag;
import hk.samwong.android.roomserviceandroidlibrary.helpers.AuthenticationDetailsPreperator;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hk.samwong.roomservice.commons.dataFormat.AuthenticationDetails;
import hk.samwong.roomservice.commons.dataFormat.Response;
import hk.samwong.roomservice.commons.dataFormat.TrainingData;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;
import hk.samwong.roomservice.commons.parameterEnums.Operation;
import hk.samwong.roomservice.commons.parameterEnums.ParameterKey;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;

public abstract class SubmitBatchTrainingData extends
		APICaller<List<WifiInformation>, Void, Response> {

	private String room;

	public SubmitBatchTrainingData(String room, Context context) {
		super(context);
		this.room = room;
	}

	abstract protected void onPostExecute(Response result);

	protected Response doInBackground(List<WifiInformation>... param) {
		if (param.length != 1) {
			throw new IllegalArgumentException("param.length != 1");
		}

		TrainingData trainingData = new TrainingData().withRoom(room)
				.withDatapoints(param[0]);
		Log.d(LogTag.APICALL.toString(), trainingData.toString());

		AuthenticationDetails authenticationDetails = new AuthenticationDetailsPreperator()
				.getAuthenticationDetails(getContext());

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.OPERATION
				.toString(), Operation.UPLOAD_TRAINING_DATA.toString()));
		nameValuePairs.add(new BasicNameValuePair(
				ParameterKey.BATCH_TRAINING_DATA.toString(), new Gson().toJson(
						trainingData, new TypeToken<TrainingData>() {
						}.getType())));
		nameValuePairs
				.add(new BasicNameValuePair(ParameterKey.AUENTICATION_DETAILS
						.toString(), AuthenticationDetailsPreperator
						.getAuthenticationDetailsAsJson(authenticationDetails)));
		Log.d(LogTag.APICALL.toString(), nameValuePairs.toString());

		try {
			String result = getJsonResponseFromAPICall(HttpVerb.POST,
					nameValuePairs);
			return new Gson().fromJson(result, new TypeToken<Response>() {
			}.getType());
		} catch (Exception e) {
			return new Response().withReturnCode(
					ReturnCode.UNRECOVERABLE_EXCEPTION).withExplanation(
					"Caught Exception: " + e);
		}
	}
}
