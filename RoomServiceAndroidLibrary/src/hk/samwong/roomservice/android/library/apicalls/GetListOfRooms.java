package hk.samwong.roomservice.android.library.apicalls;

import hk.samwong.roomservice.android.library.constants.HttpVerb;
import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.commons.dataFormat.ResponseWithListOfRooms;
import hk.samwong.roomservice.commons.parameterEnums.Operation;
import hk.samwong.roomservice.commons.parameterEnums.ParameterKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class GetListOfRooms extends
		APICaller<Context, Void, List<String>> {
	
	private Context context;
	
	public GetListOfRooms(Context context){
		this.context = context;
	}
	
	@Override
	protected List<String> doInBackground(Context... params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.OPERATION.toString(),
				Operation.GET_LIST_OF_ROOMS.toString()));
		Log.d(LogTag.APICALL.toString(), nameValuePairs.toString());

		try {
			String result = getJsonResponseFromAPICall(HttpVerb.GET,
					nameValuePairs, context);
			ResponseWithListOfRooms response = new Gson().fromJson(result, new TypeToken<ResponseWithListOfRooms>() {
			}.getType());
			return response.getListOfRooms();
		} catch (Exception e) {
			addException(e);
		}
		return Collections.emptyList();
	}

	abstract protected void onPostExecute(List<String> result);

}
