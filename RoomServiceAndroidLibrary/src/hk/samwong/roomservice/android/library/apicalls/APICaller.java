package hk.samwong.roomservice.android.library.apicalls;

import hk.samwong.roomservice.android.library.constants.Defaults;
import hk.samwong.roomservice.android.library.constants.HttpVerb;
import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.android.library.helpers.AsyncTaskWithExceptionsAndContext;
import hk.samwong.roomservice.android.library.helpers.URLBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;


public abstract class APICaller<Params, Progress, Result> extends
		AsyncTaskWithExceptionsAndContext<Params, Progress, Result> {

	protected static String SERVLET_URL;

	public APICaller(Context context) {
		super(context);
		SERVLET_URL = Defaults.ROOMSERVICE_SERVLET_URL;
	}

	/**
	 * This method also wait for connection using the blocking ensureConnected method
	 * @param verb
	 * @param nameValuePairs
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	protected String getJsonResponseFromAPICall(HttpVerb verb,
			List<NameValuePair> nameValuePairs) throws MalformedURLException, IOException {
		Log.i(LogTag.APICALL.toString(), verb.toString() + " parameters: " + nameValuePairs);
		for(int retry=0; retry<Defaults.MAX_RETRIES; retry++){
			// Wait for internet connection
			ensureConnected();

			// Make the call
			UrlEncodedFormEntity postData;
			HttpURLConnection urlConnection = null;
			try{
				// specify connection details
				postData = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
				if(verb.equals(HttpVerb.POST)){
					urlConnection = (HttpURLConnection)  new URL(SERVLET_URL).openConnection();
					urlConnection.setRequestMethod(verb.toString());
					urlConnection.setDoOutput(true);
					urlConnection.setFixedLengthStreamingMode((int) postData
							.getContentLength());
					postData.writeTo(urlConnection.getOutputStream());
				}else{
					urlConnection = (HttpURLConnection)  URLBuilder.build(nameValuePairs).openConnection();
					urlConnection.setRequestMethod(verb.toString());
				}
				urlConnection.setConnectTimeout(1000);

				// Got connection, begin
				urlConnection.connect();
				Scanner scanner = new Scanner(urlConnection.getInputStream(),
						"UTF-8").useDelimiter("\\A");
				if (scanner.hasNext()) {
					String result = scanner.next();
					Log.i(LogTag.APICALL.toString(), "RESPONSE:" + result);
					return result;
				}
				Log.w(LogTag.APICALL.toString(), "No response from the server.");
			} catch (UnsupportedEncodingException e) {
				addException(e);
				Log.e(LogTag.APICALL.toString(), "Caught UnsupportedEncodingException:" + e);
			} catch (IOException e){
				addException(e);
				Log.e(LogTag.APICALL.toString(), "Caught IOException:" + e);
			} finally {
				if (urlConnection != null)
					urlConnection.disconnect();
			}
		}
		throw new IOException("Failed to complete the API call after " + Defaults.MAX_RETRIES + " retries.");
	}
	
	/**
	 * Blocks until device is connected, and give up if retried too many times.
	 * @throws ConnectException 
	 */
	private void ensureConnected() throws ConnectException{
		ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		int timeout = 50;
		for(int i=1; i <= Defaults.MAX_RETRIES; i++){
			if(networkInfo == null || !networkInfo.isConnected()) {
				// exponential wait
				SystemClock.sleep(timeout);
				timeout *= 2;
			}else{
				return;
			}
		}
		ConnectException exception = new ConnectException(String.format("Can't get internet connection after %d exponential backoff", Defaults.MAX_RETRIES));
		addException(exception);
		throw exception;
	}
}
