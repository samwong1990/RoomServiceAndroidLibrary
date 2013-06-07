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
import hk.samwong.roomservice.commons.parameterEnums.Operation;
import hk.samwong.roomservice.commons.parameterEnums.ParameterKey;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;

public abstract class UndoConfirmValidClassification  extends APICaller<Report, Void, Response>{
	
	public UndoConfirmValidClassification(Context context) {
		super(context);
	}

	protected Response doInBackground(Report... param) {
		
		if(param.length != 1){
			throw new IllegalArgumentException("param.length != 1");
		}
		
		String json = new Gson().toJson(param[0].getInstance(), new TypeToken<Instance>(){}.getType());
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.OPERATION.toString(), Operation.DELETE.toString()));
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.ROOM.toString(), param[0].getRoom()));
		nameValuePairs.add(new BasicNameValuePair(ParameterKey.INSTANCE.toString(), json));
		AuthenticationDetails authenticationDetails = new AuthenticationDetailsPreperator().getAuthenticationDetails(getContext());
		nameValuePairs.add(new BasicNameValuePair(
				ParameterKey.AUENTICATION_DETAILS.toString(),
				AuthenticationDetailsPreperator.getAuthenticationDetailsAsJson(authenticationDetails)));
		
		Log.d(LogTag.APICALL.toString(), nameValuePairs.toString());
		try{
			String result = getJsonResponseFromAPICall(HttpVerb.DELETE, nameValuePairs);
			return new Gson().fromJson(result, new TypeToken<Response>(){}.getType());
		} catch(Exception e){
			addException(e);
			Log.e(LogTag.APICALL.toString(), "Caught exception when posting new instance" + e, e);
			return new Response().withReturnCode(ReturnCode.UNRECOVERABLE_EXCEPTION).withExplanation("Caught Exception: " + e);
		}
	}

	abstract protected void onPostExecute(Response result);

}
