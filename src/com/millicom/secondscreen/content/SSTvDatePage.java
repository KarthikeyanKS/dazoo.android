package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Link;
import com.millicom.secondscreen.content.model.TvDate;

public class SSTvDatePage extends SSPage{

	public static final String TAG = "SSTvDatePage";
	
	private static SSTvDatePage	sInstance;
	public String 				mTvDatesPageUrl;

	public static SSTvDatePage getInstance() {
		if (sInstance == null) sInstance = new SSTvDatePage();
		return sInstance;
	}

	public boolean getPage(SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");

		// Remember the callback
		super.mPageCallback = pageCallback;
		mTvDatesPageUrl = Consts.MILLICOM_SECONDSCREEN_DATES_PAGE_API;
		Link tvDatesPageLink = new Link();
		tvDatesPageLink.setUrl(mTvDatesPageUrl);
		
		super.getPage(tvDatesPageLink, pageCallback);
		return true;
	}

	public ArrayList<TvDate> getTvDates() {
		Log.d(TAG, "get Tv Dates");
		return super.getTvDates();
	}
	
	@Override
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult){	
	Log.d(TAG, "parseGetPageResult");
		try {
			super.parseTvDates(jsonArray);

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
