package com.mitv.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.mitv.http.SSHttpClient;
import com.mitv.http.SSHttpClientCallback;
import com.mitv.http.SSHttpClientGetResult;
import com.mitv.manager.AppConfigurationManager;
import com.mitv.manager.ContentParser;
import com.mitv.model.Broadcast;
import com.mitv.model.TVChannel;
import com.mitv.model.ChannelGuide;
import com.mitv.model.Link;
import com.mitv.model.ProgramType;
import com.mitv.model.SearchResult;
import com.mitv.model.TVTag;
import com.mitv.model.TVDate;

public abstract class SSPage {

	private static final String				TAG				= "SSPage";
	protected String 						mPageUrl;
	protected SSPageCallback				mPageCallback	= null;
	private SSHttpClient<SSPageGetResult>	mHttpClient		= new SSHttpClient<SSPageGetResult>();
	private ContentParser					mContentParser	= new ContentParser();

	// array with data
	private ArrayList<ChannelGuide>				mGuide;
	private ArrayList<ProgramType>			mProgramTypes;
	private ArrayList<TVDate>				mTvDates;
	private ArrayList<TVChannel>				mChannels;
	private ArrayList<TVTag>					mTags;
	private Broadcast						mBroadcast;
	private ArrayList<Broadcast>			mSeriesUpcomingBroadcasts;
	private SearchResult					mSearchResult;
	private ArrayList<Broadcast> 			mProgramBroadcasts;	
	private String							mApiVersion;

	public SSPage(String url) {
		this.mPageUrl = url;
	}
	
	public SSPage(){}
	
	public void cancelGetPage() {
		mHttpClient.cancelRequest();
	}
	
	public boolean getPage(SSPageCallback aSSPageCallback) {
		return getPage(this.mPageUrl, aSSPageCallback);
	}

	public boolean getPage(String url, SSPageCallback aSSPageCallback) {
		Log.d(TAG, "get Page");
				
		Link aLink = new Link();
		aLink.setUrl(url);
		
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
				
				if(mPageCallback != null) {
					mPageCallback.onGetPageResult(aPageGetResult);
				}
			}
		});
	}

	// Implemented by siblings to parse the json result for the page, called on the background thread
	protected abstract void parseGetPageResult(JSONArray jsonArray, SSPageGetResult pageGetResult);

	protected abstract void parseGetPageResult(JSONObject jsonObject, SSPageGetResult pageGetResult);

	protected void handleGetStartPageUriResult() {
		Log.d(TAG, "handleGetStartPageUriResult");

		// If get start page uri failed or get start page fails
		if (!getPage(mPageUrl, mPageCallback)) {
			Log.d(TAG, "Get page uri or get page failed");

			// If we have a callback
			if (mPageCallback != null) {
				// Tell our callback about it
				SSPageGetResult pageGetResult = new SSPageGetResult(this);
				mPageCallback.onGetPageResult(pageGetResult);
			}
		}
	}
	
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

	public ArrayList<TVDate> getTvDates() {
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
	
	public void parseSearchResult(JSONObject jsonObject) throws Exception {
		this.mSearchResult = mContentParser.parseSearchResult(jsonObject);
	}

	public SearchResult getSearchResult() {
		return mSearchResult;
	}
	
	public ArrayList<ChannelGuide> getGuide() {
		return mGuide;
	}

	public void parseChannels(JSONArray jsonArray) throws Exception {
		this.mChannels = mContentParser.parseChannels(jsonArray);
	}

	public ArrayList<TVChannel> getChannels() {
		return mChannels;
	}

	public void parseTags(JSONArray jsonArray) throws Exception {
		this.mTags = mContentParser.parseTags(jsonArray);
	}

	public ArrayList<TVTag> getTags() {
		return mTags;
	}

	public Broadcast getBroadcast() {
		return mBroadcast;
	}
	
	public void parseAndUpdateAppConfiguration(JSONObject configurationJSONObject) throws Exception {
		AppConfigurationManager.getInstance().updateConfiguration(configurationJSONObject);
	}

	public void parseBroadcast(JSONObject jsonObject) throws Exception {
		this.mBroadcast = mContentParser.parseBroadcast(jsonObject);
	}

	public ArrayList<Broadcast> getSeriesUpcomingBroadcasts() {
		return mSeriesUpcomingBroadcasts;
	}
	
	public void parseSeriesUpcomingBroadcasts(JSONArray jsonArray) throws Exception {
		this.mSeriesUpcomingBroadcasts = mContentParser.parseBroadcasts(jsonArray);
	}
	
	public ArrayList<Broadcast> getProgramBroadcasts(){
		return mProgramBroadcasts;
	}
	
	public void parseProgramBroadcasts(JSONArray jsonArray) throws Exception{
		this.mProgramBroadcasts = mContentParser.parseBroadcasts(jsonArray);
	}
	
	public void parseApiVersion(JSONArray versionJSONArray) {
		this.mApiVersion = mContentParser.parseApiVersion(versionJSONArray);
	}
	
	public String getApiVersionString() {
		return mApiVersion;
	}
	

}
