package com.mitv.manager;

import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.Consts;
import com.mitv.model.OldBroadcast;



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
		deviceId = GenericUtils.getDeviceId();
	}

	public static void trackBroadcastStatic(OldBroadcast broadcast) {
		getInstance().trackBroadcast(broadcast);
	}

	public void trackBroadcast(OldBroadcast broadcast) {
		if (broadcast != null) {
			if (broadcast.getProgram() != null) {
				String trackingUrl = String.format(Consts.URL_INTERNAL_TRACKING_OLD, broadcast.getProgram().getProgramId(), deviceId);
				super.trackUrl(trackingUrl);
			}
		}
	}

}
