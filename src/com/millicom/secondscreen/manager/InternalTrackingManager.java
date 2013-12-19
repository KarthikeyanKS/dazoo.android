package com.millicom.secondscreen.manager;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.http.SSHttpClient;
import com.millicom.secondscreen.http.SSHttpClientCallback;
import com.millicom.secondscreen.http.SSHttpClientGetResult;

public class InternalTrackingManager {

	private static final String TAG = "InternalTrackingManager";

	private static InternalTrackingManager selfInstance;
	private static String deviceId;
	private SSHttpClient<SSPageGetResult> mHttpClient;

	public static InternalTrackingManager getInstance() {
		if (selfInstance == null) {
			selfInstance = new InternalTrackingManager();
		}
		return selfInstance;
	}

	public InternalTrackingManager() {
		mHttpClient = new SSHttpClient<SSPageGetResult>();
		Context context = SecondScreenApplication.getInstance().getApplicationContext();
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
	}

	public static void trackBroadcastStatic(Broadcast broadcast) {
		getInstance().trackBroadcast(broadcast);
	}

	public void trackBroadcast(Broadcast broadcast) {
		if (broadcast != null) {
			if (broadcast.getProgram() != null) {
				String trackingUrl = String.format(Consts.MILLICOM_SECONDSCREEN_TRACKING_URL, broadcast.getProgram().getProgramId(), deviceId);
				mHttpClient.doHttpGet(trackingUrl, new SSHttpClientCallback<SSPageGetResult>() {

					@Override
					public SSPageGetResult onHandleHttpGetResultInBackground(SSHttpClientGetResult aHttpClientGetResult) {
						// Handle the http get result
						// aHttpClientGetResult.getRawData()

						SSPageGetResult pageGetResult = new SSPageGetResult(aHttpClientGetResult.getUri(), null);

						return pageGetResult;
					}

					@Override
					public void onHttpGetResultFinal(SSPageGetResult aPageGetResult) {
						Log.d(TAG, "SSPageGetResult : " + aPageGetResult);
					}
				});
			}
		}
	}

}
