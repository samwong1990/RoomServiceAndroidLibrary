package hk.samwong.roomservice.android.library.apicalls;


import hk.samwong.roomservice.android.library.constants.HttpVerb;
import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.commons.dataFormat.Response;
import hk.samwong.roomservice.commons.dataFormat.TrainingData;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;
import hk.samwong.roomservice.commons.parameterEnums.Operation;
import hk.samwong.roomservice.commons.parameterEnums.ParameterKey;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class SubmitBatchTrainingData extends
		APICaller<List<WifiInformation>, Void, Response> {

	private Context context;
	private String room;

	public SubmitBatchTrainingData(String room, Context context) {
		this.context = context;
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

		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.OPERATION
				.toString(), Operation.UPLOAD_TRAINING_DATA.toString()));
		nameValuePairs.add(new BasicNameValuePair(
				ParameterKey.BATCH_TRAINING_DATA.toString(), new Gson().toJson(
						trainingData, new TypeToken<TrainingData>() {
						}.getType())));
		
		Log.d(LogTag.APICALL.toString(), nameValuePairs.toString());

		try {
			String result = getJsonResponseFromAPICall(HttpVerb.POST,
					nameValuePairs, context);
			return new Gson().fromJson(result, new TypeToken<Response>() {
			}.getType());
		} catch (Exception e) {
			return new Response().setReturnCode(
					ReturnCode.UNRECOVERABLE_EXCEPTION).setExplanation(
					"Caught Exception: " + e);
		}
	}
}
