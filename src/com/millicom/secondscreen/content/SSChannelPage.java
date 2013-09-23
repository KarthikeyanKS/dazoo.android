package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Link;

public class SSChannelPage extends SSPage{

	private static final String TAG = "SSChannelPage";
	
	private static SSChannelPage	sInstance;
	public String 				mChannelsPageUrl;

	public static SSChannelPage getInstance() {
		if (sInstance == null) sInstance = new SSChannelPage();
		return sInstance;
	}

	public boolean getPage(SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");

		// Remember the callback
		super.mPageCallback = pageCallback;
		mChannelsPageUrl = Consts.MILLICOM_SECONDSCREEN_CHANNELS_PAGE_API;
		Link programTypesPageLink = new Link();
		programTypesPageLink.setUrl(mChannelsPageUrl);
		
		super.getPage(programTypesPageLink, pageCallback);
		return true;
	}
	
	public ArrayList<Channel> getChannels(){
		Log.d(TAG,"get Channels");
		return super.getChannels();
	}
	
	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult){	
	Log.d(TAG, "parseGetPageResult");
		try {
			super.parseChannels(jsonArray);

			// The resulting page is this
			pageGetResult.setPage(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void handleGetStartPageUriResult() {
		Log.d(TAG, "handleGetStartPageUriResult");

		// If get start page uri failed or get start page fails
		if (!getPage(mPageCallback)) {
			Log.d(TAG, "Get dates page uri or get dates page failed");

			// If we have a callback
			if (mPageCallback != null) {
				// Tell our callback about it
				SSPageGetResult pageGetResult = new SSPageGetResult(this);
				mPageCallback.onGetPageResult(pageGetResult);
			}
		}
	}

}
