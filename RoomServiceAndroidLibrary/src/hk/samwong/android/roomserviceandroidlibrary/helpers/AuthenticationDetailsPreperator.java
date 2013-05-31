package hk.samwong.android.roomserviceandroidlibrary.helpers;

import hk.samwong.android.roomserviceandroidlibrary.constants.LogTag;

import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hk.samwong.roomservice.commons.dataFormat.AuthenticationDetails;

public class AuthenticationDetailsPreperator implements LocationReceiver{
	private Location location = null;
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public synchronized AuthenticationDetails getAuthenticationDetails(Context context) {
		// Start the localization thread, then deal with other things
		LocateWithCellTower locateWithCellTower = new LocateWithCellTower();
		locateWithCellTower.requestSingleUpdate(context, this);

		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String wifiMacAddress = wm.getConnectionInfo().getMacAddress();
		// Wait for location to return
		while(countDownLatch.getCount() > 0){
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				Log.e(LogTag.APICALL.toString(), "CountDownLatch await interrupted.");
			}
		}
		return new AuthenticationDetails()
				.withDeviceWifiMacAddress(wifiMacAddress)
				.withDeviceModel(Build.MODEL)
				.withDeviceInstallID(Installation.id(context))
				.withDeviceBrand(Build.BRAND)
				.withDeviceManaufacturer(Build.MANUFACTURER)
				.withDeviceProduct(Build.PRODUCT)
				.withDeviceLatitude(location.getLatitude())
				.withDeviceLongitude(location.getLongitude())
				.withLocationAccuracy(location.getAccuracy());
	}

	public static String getAuthenticationDetailsAsJson(AuthenticationDetails authenticationDetails) {
		return new Gson().toJson(authenticationDetails,
				new TypeToken<AuthenticationDetails>() {
				}.getType());
	}

	@Override
	public void setLocation(Location location) {
		countDownLatch.countDown();
		this.location = location;
	}
}
