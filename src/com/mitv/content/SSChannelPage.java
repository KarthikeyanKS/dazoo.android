package com.mitv.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.model.Channel;

public class SSChannelPage extends SSPage {

	private static final String	TAG	= "SSChannelPage";

	private static SSChannelPage	sInstance;
	public static SSChannelPage getInstance() {
		if (sInstance == null) { 
			sInstance = new SSChannelPage();
		}
		return sInstance;
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

	@Override
	protected void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult) {
		// not necessary here
	}

}
