package hk.samwong.roomservice.android.library.apicalls;


import hk.samwong.roomservice.android.library.constants.HttpVerb;
import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.commons.dataFormat.Response;
import hk.samwong.roomservice.commons.dataFormat.RoomStatistic;
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

/**
 * For my testing purpose only, assume this is not supported in the long run.
 * @author wongsam
 *
 */
public abstract class PutStatistics extends APICaller<List<RoomStatistic>, Void, Response> {

	private Context context; 
	
	public PutStatistics(Context context) {
		this.context = context;
	}

	abstract protected void onPostExecute(Response result);

	@Override
	protected Response doInBackground(List<RoomStatistic>... stats) {
		if(stats.length != 1){
			throw new IllegalArgumentException("Expects a single list of RoomStatistic"); 
		}

		// prepare parameters
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs
				.add(new BasicNameValuePair(ParameterKey.OPERATION.toString(),
						Operation.UPLOAD_STATISTICS.toString()));
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.VALIDATION_STATISTICS.toString(),
				new Gson().toJson(stats[0], new TypeToken<List<RoomStatistic>>() {
				}.getType())));
		
		Log.d(LogTag.APICALL.toString(), nameValuePairs.toString());

		try {
			String result = getJsonResponseFromAPICall(HttpVerb.PUT, nameValuePairs, context);
			return new Gson().fromJson(result, new TypeToken<Response>() {
			}.getType());
		} catch (Exception e) {
			addException(e);
		}
		return new Response().setExplanation("Failed to complete API call").setReturnCode(ReturnCode.UNRECOVERABLE_EXCEPTION);
		
	}


}
