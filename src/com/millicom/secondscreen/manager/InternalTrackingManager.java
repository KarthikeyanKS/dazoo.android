package com.millicom.secondscreen.manager;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.utilities.DeviceUtilities;

public class InternalTrackingManager extends GenericTrackingManager {

	private static final String TAG = "InternalTrackingManager";

	private static InternalTrackingManager selfInstance;
	private static String deviceId;

	public static InternalTrackingManager getInstance() {
		if (selfInstance == null) {
			selfInstance = new InternalTrackingManager();
		}
		return selfInstance;
	}

	public InternalTrackingManager() {
		deviceId = DeviceUtilities.getDeviceId();
	}

	public static void trackBroadcastStatic(Broadcast broadcast) {
		getInstance().trackBroadcast(broadcast);
	}

	public void trackBroadcast(Broadcast broadcast) {
		if (broadcast != null) {
			if (broadcast.getProgram() != null) {
				String trackingUrl = String.format(Consts.MILLICOM_SECONDSCREEN_TRACKING_URL, broadcast.getProgram().getProgramId(), deviceId);
				super.trackUrl(trackingUrl);
			}
		}
	}

}
