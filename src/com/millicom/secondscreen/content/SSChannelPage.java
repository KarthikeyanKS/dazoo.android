package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Link;

public class SSChannelPage extends SSPage {

	private static final String	TAG	= "SSChannelPage";
	public String				mPageUrl;

	private SSChannelPage() {
	};

	private static class SSChannelPageHolder {
		public static final SSChannelPage	INSTANCE	= new SSChannelPage();
	}

	public static SSChannelPage getInstance() {
		return SSChannelPageHolder.INSTANCE;
	}

	public boolean getPage(String url, SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");

		// Remember the callback
		super.mPageCallback = pageCallback;
		Link programTypesPageLink = new Link();
		programTypesPageLink.setUrl(url);

		super.getPage(programTypesPageLink, pageCallback);
		return true;
	}

	public ArrayList<Channel> getChannels() {
		Log.d(TAG, "get Channels");
		return super.getChannels();
	}

	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult) {
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
		if (!getPage(mPageUrl, mPageCallback)) {
			Log.d(TAG, "Get dates page uri or get dates page failed");

			// If we have a callback
			if (mPageCallback != null) {
				// Tell our callback about it
				SSPageGetResult pageGetResult = new SSPageGetResult(this);
				mPageCallback.onGetPageResult(pageGetResult);
			}
		}
	}

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		// not necessary here
	}

}
