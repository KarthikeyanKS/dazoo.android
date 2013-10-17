package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Link;
import com.millicom.secondscreen.http.SSHttpClient;
import com.millicom.secondscreen.http.SSHttpClientCallback;
import com.millicom.secondscreen.http.SSHttpClientGetResult;
import com.millicom.secondscreen.manager.ContentParser;

public class SSBroadcastPage {

	public static final String				TAG				= "SSBroadcastPage";
	private static SSBroadcastPage			sInstance;
	public String							mBroadcastPageUrl;
	private SSHttpClient<SSPageGetResult>	mHttpClient		= new SSHttpClient<SSPageGetResult>();
	private Broadcast						mBroadcast;
	private ContentParser					mContentParser	= new ContentParser();

	public SSBroadcastPage(String broadcastPageUrl) {
		this.mBroadcastPageUrl = broadcastPageUrl;
	}

	public boolean getPage(SSPageCallback aSSPageCallback) {

		return mHttpClient.doHttpGet(mBroadcastPageUrl, new SSHttpClientCallback<SSPageGetResult>() {

			@Override
			public SSPageGetResult onHandleHttpGetResultInBackground(SSHttpClientGetResult aHttpClientGetResult) {
				// Handle the http get result
				return handleHttpGetResult(aHttpClientGetResult);
			}

			@Override
			public void onHttpGetResultFinal(SSPageGetResult aPageGetResult) {
				Log.d(TAG, "SSPageGetResult : " + aPageGetResult);
				// If no result is given
				if (aPageGetResult == null) {
					Log.d(TAG, "Result is not null!");
					// Create a default result, will indicate failure
					aPageGetResult = new SSPageGetResult();

				}
			}
		});
	}

	protected SSPageGetResult handleHttpGetResult(SSHttpClientGetResult aHttpClientGetResult) {
		Log.d(TAG, "In onHandleHttpGetResult");
		JSONObject jsonObject = aHttpClientGetResult.getJson();
		Log.d(TAG, "JSONObject is not null: " + (jsonObject != null));

		SSPageGetResult pageGetResult = new SSPageGetResult(aHttpClientGetResult.getUri(), null);

		try {
			mBroadcast = mContentParser.parseBroadcast(jsonObject);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageGetResult;
	}

	public Broadcast getBroadcast() {
		return mBroadcast;
	}
}