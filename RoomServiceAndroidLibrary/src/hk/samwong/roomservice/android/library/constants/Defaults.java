package hk.samwong.roomservice.android.library.constants;

import hk.samwong.roomservice.commons.parameterEnums.Classifier;

public interface Defaults {
	public static final String ROOMSERVICE_SERVLET_URL = "http://129.31.201.115:8080/RoomLevelLocator/api";
	
	public static final Classifier classifier = Classifier.ALL;
	public static final int MAX_RETRIES = 5;
	
	// on my Galaxy Nexus, the rate of wifi scan update is correlated with my polling rate.
	// The fastest update rate I can get is 0.4 scans/sec (by polling every 10ms)
	// Yet if I poll every 2 seconds, the wifi scan rate drops to 0.2 scans / sec. Very annoying.
	// So to save training time, I just went for 100ms.
	public static final long SAMPLING_INTERVAL_IN_MILLISEC = 100; 
	public static final int POLLING_FREQUENCY_IN_MILLISEC = 2000;
	public static final String appKey = "some very long shared secret that you get once I've implemented the registration bit";
}
