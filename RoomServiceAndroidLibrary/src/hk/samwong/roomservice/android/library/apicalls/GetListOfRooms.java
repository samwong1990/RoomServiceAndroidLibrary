package hk.samwong.roomservice.android.library.apicalls;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.android.library.helpers.AsyncTaskWithExceptionsAndContext;
import hk.samwong.roomservice.android.library.helpers.AuthenticationDetailsPreperator;
import hk.samwong.roomservice.android.library.helpers.URLBuilder;
import hk.samwong.roomservice.commons.dataFormat.AuthenticationDetails;
import hk.samwong.roomservice.commons.dataFormat.ResponseWithListOfRooms;
import hk.samwong.roomservice.commons.parameterEnums.Operation;
import hk.samwong.roomservice.commons.parameterEnums.ParameterKey;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;

public abstract class GetListOfRooms extends
		AsyncTaskWithExceptionsAndContext<Void, Void, List<String>> {

	public GetListOfRooms(Context context) {
		super(context);
	}

	@Override
	protected List<String> doInBackground(Void... params) {
		HttpURLConnection urlConnection = null;
		try {
			AuthenticationDetails authenticationDetails = new AuthenticationDetailsPreperator().getAuthenticationDetails(getContext());
			
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair(ParameterKey.OPERATION.toString(), Operation.GET_LIST_OF_ROOMS.toString()));
			nvps.add(new BasicNameValuePair(ParameterKey.AUENTICATION_DETAILS.toString(), AuthenticationDetailsPreperator.getAuthenticationDetailsAsJson(authenticationDetails)));
			
			urlConnection = (HttpURLConnection) URLBuilder.build(nvps).openConnection();
			urlConnection.setConnectTimeout(1000);
			urlConnection.setChunkedStreamingMode(0);
			urlConnection.connect();
			Scanner scanner = new Scanner(urlConnection.getInputStream(),
					"UTF-8").useDelimiter("\\A");
			if (scanner.hasNext()) {
				String result = scanner.next();
				Log.i(LogTag.APICALL.toString(), result);
				ResponseWithListOfRooms response = new Gson().fromJson(result,
						new TypeToken<ResponseWithListOfRooms>() {
						}.getType());
				if(response.getReturnCode().equals(ReturnCode.OK)){
					return response.getListOfRooms();
				}else{
					throw new IOException(response.getExplanation());
				}
			}
			Log.w(LogTag.APICALL.toString(),
					"No response for the list of room query");
		} catch (IOException e) {
			addException(e);
			Log.e(LogTag.APICALL.toString(), "Caught IOException: " + e);
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return null;
	}

	abstract protected void onPostExecute(List<String> result);

}
