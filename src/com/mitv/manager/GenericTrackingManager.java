package com.mitv.manager;

import android.util.Log;

import com.mitv.content.SSPageGetResult;
import com.mitv.http.SSHttpClient;
import com.mitv.http.SSHttpClientCallback;
import com.mitv.http.SSHttpClientGetResult;

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
