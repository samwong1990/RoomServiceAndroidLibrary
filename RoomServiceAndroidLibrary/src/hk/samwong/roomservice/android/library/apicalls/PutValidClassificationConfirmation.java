package hk.samwong.roomservice.android.library.apicalls;


import java.util.ArrayList;

import net.sf.javaml.core.Instance;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import hk.samwong.roomservice.android.library.constants.HttpVerb;
import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.android.library.helpers.AuthenticationDetailsPreperator;
import hk.samwong.roomservice.commons.dataFormat.AuthenticationDetails;
import hk.samwong.roomservice.commons.dataFormat.Report;
import hk.samwong.roomservice.commons.dataFormat.Response;
import hk.samwong.roomservice.commons.dataFormat.ResponseWithReports;
import hk.samwong.roomservice.commons.parameterEnums.Operation;
import hk.samwong.roomservice.commons.parameterEnums.ParameterKey;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;

/**
 * Classifier returned the right room, so the data used in query can be saved as
 * a new datapoint
 * 
 * @author wongsam
 * 
 */
public abstract class PutValidClassificationConfirmation extends
		APICaller<Report, Void, Response> {

	public PutValidClassificationConfirmation(Context context) {
		super(context);
	}

	abstract protected void onPostExecute(Response result);

	protected Response doInBackground(Report... param) {
		// input validation
		if (param.length != 1) {
			throw new IllegalArgumentException("Expects a single Report");
		}

		// prepare parameters
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs
				.add(new BasicNameValuePair(ParameterKey.OPERATION.toString(),
						Operation.CONFIRM_VALID_CLASSIFICATION.toString()));
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.ROOM.toString(),
				param[0].getRoom()));
		String instanceAsJson = new Gson().toJson(param[0].getInstance(),
				new TypeToken<Instance>() {
				}.getType());
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.INSTANCE
				.toString(), instanceAsJson));
		AuthenticationDetails authenticationDetails = new AuthenticationDetailsPreperator().getAuthenticationDetails(getContext());
		nameValuePairs.add(new BasicNameValuePair(
				ParameterKey.AUENTICATION_DETAILS.toString(),
				AuthenticationDetailsPreperator.getAuthenticationDetailsAsJson(authenticationDetails)));
		Log.d(LogTag.APICALL.toString(), nameValuePairs.toString());

		try {
			String result = getJsonResponseFromAPICall(HttpVerb.PUT, nameValuePairs);
			return new Gson().fromJson(result, new TypeToken<Response>() {
			}.getType());
		} catch (Exception e) {
			addException(e);
		}
		return new ResponseWithReports().withExplanation("Failed to complete API call").withReturnCode(ReturnCode.UNRECOVERABLE_EXCEPTION);
		
		
	}
}
