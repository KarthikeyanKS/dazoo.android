package com.millicom.secondscreen.manager;

import android.util.Log;

import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.http.SSHttpClient;
import com.millicom.secondscreen.http.SSHttpClientCallback;
import com.millicom.secondscreen.http.SSHttpClientGetResult;

public class GenericTrackingManager {
	private static final String TAG = "GenericTrackingManager";

	private static GenericTrackingManager selfInstance;
	private SSHttpClient<SSPageGetResult> mHttpClient;

	public static GenericTrackingManager getInstance() {
		if (selfInstance == null) {
			selfInstance = new GenericTrackingManager();
		}
		return selfInstance;
	}

	public GenericTrackingManager() {
		mHttpClient = new SSHttpClient<SSPageGetResult>();
	}

	public static void trackUrlStatic(String trackingUrl) {
		getInstance().trackUrl(trackingUrl);
	}

	public void trackUrl(String trackingUrl) {
		if (trackingUrl != null) {
			mHttpClient.doHttpGet(trackingUrl, new SSHttpClientCallback<SSPageGetResult>() {

				@Override
				public SSPageGetResult onHandleHttpGetResultInBackground(SSHttpClientGetResult aHttpClientGetResult) {
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
