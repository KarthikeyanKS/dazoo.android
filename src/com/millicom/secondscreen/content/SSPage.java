package com.millicom.secondscreen.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Link;
import com.millicom.secondscreen.content.model.ProgramType;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.http.SSHttpClient;
import com.millicom.secondscreen.http.SSHttpClientCallback;
import com.millicom.secondscreen.http.SSHttpClientGetResult;
import com.millicom.secondscreen.manager.ContentParser;

public abstract class SSPage {

	private static final String				TAG				= "SSPage";
	protected SSPageCallback				mPageCallback	= null;
	private SSHttpClient<SSPageGetResult>	mHttpClient		= new SSHttpClient<SSPageGetResult>();
	private ContentParser					mContentParser	= new ContentParser();

	// array with data
	private ArrayList<Guide>				mGuide;
	private ArrayList<ProgramType>			mProgramTypes;
	private ArrayList<TvDate>				mTvDates;
	private ArrayList<Channel>				mChannels;
	private ArrayList<Tag>					mTags;
	private Broadcast						mBroadcast;

	public void cancelGetPage() {
		mHttpClient.cancelRequest();
	}

	public boolean getPage(Link aLink, SSPageCallback aSSPageCallback) {
		Log.d(TAG, "get Page");
		mPageCallback = aSSPageCallback;
		return mHttpClient.doHttpGet(aLink.getUrl(), new SSHttpClientCallback<SSPageGetResult>() {

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
				mPageCallback.onGetPageResult(aPageGetResult);
			}
		});
	}

	// Implemented by siblings to parse the json result for the page, called on the background thread
	protected abstract void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult);

	protected abstract void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult);

	protected SSPageGetResult handleHttpGetResult(SSHttpClientGetResult aHttpClientGetResult) {
		Log.d(TAG, "In onHandleHttpGetResult");
		JSONArray jsonArray = aHttpClientGetResult.getJsonArray();

		if (jsonArray != null) {
			SSPageGetResult pageGetResult = new SSPageGetResult(aHttpClientGetResult.getUri(), null);

			try {
				parseGetPageResult(jsonArray, pageGetResult);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return pageGetResult;
		} else {
			JSONObject jsonObject = aHttpClientGetResult.getJson();
			SSPageGetResult pageGetResult = new SSPageGetResult(aHttpClientGetResult.getUri(), null);

			try {
				parseGetPageResult(jsonObject, pageGetResult);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return pageGetResult;
		}
	}

	public void parseTvDates(JSONArray jsonArray) throws Exception {
		this.mTvDates = mContentParser.parseDates(jsonArray);
	}

	public ArrayList<TvDate> getTvDates() {
		Log.d(TAG,"mTvDates:" + mTvDates);
		return mTvDates;
	}

	public void parseProgramTypes(JSONArray jsonArray) throws Exception {
		this.mProgramTypes = mContentParser.parseProgramTypes(jsonArray);
	}

	public ArrayList<ProgramType> getProgramTypes() {
		return mProgramTypes;
	}

	public void parseGuide(JSONArray jsonArray) throws Exception {
		this.mGuide = mContentParser.parseGuide(jsonArray);
	}

	public ArrayList<Guide> getGuide() {
		return mGuide;
	}

	public void parseChannels(JSONArray jsonArray) throws Exception {
		this.mChannels = mContentParser.parseChannels(jsonArray);
	}

	public ArrayList<Channel> getChannels() {
		Log.d(TAG,"mChannels: " + mChannels.size());
		return mChannels;
	}

	public void parseTags(JSONArray jsonArray) throws Exception {
		this.mTags = mContentParser.parseTags(jsonArray);
	}

	public ArrayList<Tag> getTags() {
		Log.d(TAG,"mTAGS: " + mTags.size());
		return mTags;
	}

	public Broadcast getBroadcast() {
		return mBroadcast;
	}

	public void parseBroadcast(JSONObject jsonObject) throws Exception {
		this.mBroadcast = mContentParser.parseBroadcast(jsonObject);
	}
}
