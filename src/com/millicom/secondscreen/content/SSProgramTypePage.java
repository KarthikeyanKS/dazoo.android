package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;

import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.model.Link;
import com.millicom.secondscreen.content.model.ProgramType;
import com.millicom.secondscreen.content.model.TvDate;

public class SSProgramTypePage extends SSPage{

public static final String TAG = "SSProgramTypePage";
	
	private static SSProgramTypePage	sInstance;
	public String 				mProgramTypesPageUrl;

	public static SSProgramTypePage getInstance() {
		if (sInstance == null) sInstance = new SSProgramTypePage();
		return sInstance;
	}

	public boolean getPage(SSPageCallback pageCallback) {
		Log.d(TAG, "getPage");

		// Remember the callback
		super.mPageCallback = pageCallback;
		mProgramTypesPageUrl = Consts.MILLICOM_SECONDSCREEN_PROGRAM_TYPES_PAGE_API;
		Link programTypesPageLink = new Link();
		programTypesPageLink.setUrl(mProgramTypesPageUrl);
		
		super.getPage(programTypesPageLink, pageCallback);
		return true;
	}

	public ArrayList<ProgramType> getProgramTypes(){
		Log.d(TAG,"get Program types");
		return super.getProgramTypes();
	}
	
	@Override
	//protected void parseGetPageResult(JSONObject aJsonObject, SSPageGetResult aVPPageGetResult) {
	protected void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult){	
	Log.d(TAG, "parseGetPageResult");
		try {
			super.parseProgramTypes(jsonArray);

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
