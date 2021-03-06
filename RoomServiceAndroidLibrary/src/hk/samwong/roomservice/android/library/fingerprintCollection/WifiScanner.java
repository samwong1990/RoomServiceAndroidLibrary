package hk.samwong.roomservice.android.library.fingerprintCollection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import hk.samwong.roomservice.commons.dataFormat.BssidStrength;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;

public class WifiScanner {

	public synchronized static WifiInformation getWifiInformation(Activity activity) {
		WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		if(!wifi.isWifiEnabled()){
			wifi.setWifiEnabled(true);
		}
		wifi.startScan();
		List<ScanResult> results = wifi.getScanResults();
		Set<BssidStrength> signalStrengths = new HashSet<BssidStrength>(30);
		for (int i = 0; i < results.size(); i++) {
			ScanResult scanResult = results.get(i);
			signalStrengths.add(new BssidStrength(scanResult.BSSID,
					scanResult.level));
		}
		return new WifiInformation().withSignalStrengths(signalStrengths);

	}

}
