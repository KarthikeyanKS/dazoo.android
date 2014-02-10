package com.mitv.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.content.SSApiVersionPage;
import com.mitv.content.SSAppConfigurationPage;
import com.mitv.content.SSChannelPage;
import com.mitv.content.SSGuidePage;
import com.mitv.content.SSPageCallback;
import com.mitv.content.SSPageGetResult;
import com.mitv.content.SSTagsPage;
import com.mitv.content.SSTvDatePage;
import com.mitv.http.NetworkUtils;
import com.mitv.like.LikeService;
import com.mitv.model.AdzerkAd;
import com.mitv.model.Broadcast;
import com.mitv.model.Channel;
import com.mitv.model.ChannelGuide;
import com.mitv.model.Tag;
import com.mitv.model.TvDate;
import com.mitv.mychannels.MyChannelsService;
import com.mitv.storage.MiTVStore;
import com.mitv.storage.MiTVStoreOperations;

public class MiTVCore {
	private static final String			TAG					= "MiTVCore";
	private static Context				mContext;
	private static int					mDateIndex			= 0;
	private static boolean				mHasFetchedTvDate			= false;
	private static boolean				mHasFetchedTags				= false;
	private static boolean				mHasFetchedChannels			= false;

	private static ArrayList<TvDate>		mTvDates		= new ArrayList<TvDate>();
	private static ArrayList<Channel>		mChannels		= new ArrayList<Channel>();
	private static ArrayList<Tag>			mTags			= new ArrayList<Tag>();
	private static ArrayList<ChannelGuide>	mChannelGuides	= new ArrayList<ChannelGuide>();
	private static ArrayList<String>		mChannelsIds	= new ArrayList<String>();
	

	// private constructor prevents instantiation from other classes
	private MiTVCore() {
	};

	/**
	 * MiTVCoreHolder is loaded on the first execution of MiTVCore.getInstance() or the first access to the MiTVCoreHolder.INSTANCE, not before
	 */
	private static class MiTVCoreHolder {
		public static final MiTVCore	INSTANCE	= new MiTVCore();
	}

	public static MiTVCore getInstance(Context context, int dateIndex) {
		mContext = context;
		mDateIndex = dateIndex;
		return MiTVCoreHolder.INSTANCE;
	}

	public void fetchContent() {
		getDates();
		getTags();
		getChannels();
	}
	

	private void getTags() {
		if (MiTVStore.getInstance().getTags() == null || MiTVStore.getInstance().getTags().isEmpty()) {
			GetTags tagsTask = new GetTags();
			tagsTask.execute();
		}
	}
	
	private void getDates() {
		if (MiTVStore.getInstance().getTvDates() == null || MiTVStore.getInstance().getTvDates().isEmpty()) {
			GetTvDates tvDatesTask = new GetTvDates();
			tvDatesTask.execute();
		}
	}
	
	private void getChannels() {
		HashMap<String, Channel> allChannels = MiTVStore.getInstance().getChannels();
		
		/* Channels not fetched, go fetch!!! */
		if (allChannels == null || allChannels.isEmpty()) {
			
			/* If logged in, fetch ALL channels, and fetch your selected channels ids using the 'MyChannelService' */
			if (SecondScreenApplication.isLoggedIn()) {
				MyChannelsService.getMyChannels();
				GetAllChannelsTask allChannelsTask = new GetAllChannelsTask();
				allChannelsTask.execute();
			} else {
				GetDefaultChannelsTask defaultChannelsTask = new GetDefaultChannelsTask();
				defaultChannelsTask.execute();
			}
		} else {
			/* Channels already fetched, try fetch guide */
			mHasFetchedChannels = true;
			getGuide(mDateIndex, false);
		}
	}

	// prepare tagged broadcasts for the specific date
	private static boolean prepareTaggedContent(TvDate date) {
		if (mTags != null && mTags.isEmpty() != true) {
			if (mChannelGuides != null && mChannelGuides.isEmpty() != true) {
				for (int i = 1; i < mTags.size(); i++) {
					ArrayList<Broadcast> taggedBroadcasts = null;
					taggedBroadcasts = MiTVStoreOperations.getTaggedBroadcasts(date.getDate(), mTags.get(i));
					MiTVStoreOperations.saveTaggedBroadcast(date, mTags.get(i), taggedBroadcasts);
				}
				return true;
			}
		}
		return false;
	}
	
	// task to get the tv-dates
	private static class GetTvDates extends AsyncTask<String, String, Void> {

		@Override
		protected Void doInBackground(String... params) {
			SSTvDatePage.getInstance().getPage(new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mTvDates = SSTvDatePage.getInstance().getTvDates();

					if (mTvDates != null && mTvDates.isEmpty() != true) {
						Log.d(TAG, "Dates: " + mTvDates.size());
						MiTVStore.getInstance().setTvDates(mTvDates);
						mHasFetchedTvDate = true;

						// attempt the common callback interface
						getGuide(mDateIndex, false);
					} else {
						LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_BAD_REQUEST));
					}
				}
			});

			return null;
		}
	}

	// task to get the tags
	private static class GetTags extends AsyncTask<String, String, Void> {

		@Override
		protected Void doInBackground(String... params) {
			SSTagsPage.getInstance().getPage(new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mTags = SSTagsPage.getInstance().getTags();

					if (mTags != null && mTags.isEmpty() != true) {
						Log.d(TAG, "Tags: " + mTags.size());

						// insert general Tag category in the list
						Tag tagAll = new Tag();
						tagAll.setId(mContext.getResources().getString(R.string.all_categories_id));
						tagAll.setName(mContext.getResources().getString(R.string.all_categories_name));
						mTags.add(0, tagAll);

						MiTVStore.getInstance().setTags(mTags);
						mHasFetchedTags = true;
						// attempt the get the guide
						getGuide(mDateIndex, false);
					} else {
						LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_BAD_REQUEST));
					}
				}
			});
			return null;
		}
	}

	// task to get all channels to be listed in the channel selection list under MyProfile/MyChannels
	private static class GetChannelsTask extends AsyncTask<String, String, Void> {

		@Override
		protected Void doInBackground(String... params) {
			
			String getChannelsBaseUrl = params[0];
			
			SSChannelPage.getInstance().getPage(getChannelsBaseUrl, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					ArrayList<Channel>	channels = SSChannelPage.getInstance().getChannels();
					mChannels = channels;

					if (mChannels != null && mChannels.isEmpty() != true) {
						Log.d(TAG, "ALL Channels: " + mChannels.size());
						// store the list channels (used in the my profile/my guide)
						MiTVStoreOperations.saveChannels(mChannels);

						mHasFetchedChannels = true;
						
						// attempt the common callback interface
						getGuide(mDateIndex, false);
					} else {
						LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_BAD_REQUEST));
					}
				}
			});
			return null;
		}
	}
	
	private static class GetAllChannelsTask extends GetChannelsTask {
		@Override
		protected Void doInBackground(String... params) {
			return super.doInBackground(Consts.URL_CHANNELS_ALL);
		}
		
	}
	
	private static class GetDefaultChannelsTask extends GetChannelsTask {
		@Override
		protected Void doInBackground(String... params) {
			return super.doInBackground(Consts.URL_CHANNELS_DEFAULT);
		}
		
	}
		
	public static interface AppConfigurationCallback {
		public void onAppConfigurationResult();
	}
	
	public static void getAppConfiguration(AppConfigurationCallback appConfigCallBack) {
		GetAppConfigurationTask getAppConfigurationTask = new GetAppConfigurationTask(appConfigCallBack);
		getAppConfigurationTask.execute();
	}
	
	private static class GetAppConfigurationTask extends AsyncTask<String, Void, Void> {

		private AppConfigurationCallback appConfigCallBack = null;

		public GetAppConfigurationTask(AppConfigurationCallback appConfigCallBack) {
			this.appConfigCallBack = appConfigCallBack;
		}

		@Override
		protected Void doInBackground(String... params) {
			SSAppConfigurationPage.getInstance().getPage(new SSPageCallback() {

				@Override
				public void onGetPageResult(SSPageGetResult pageGetResult) {
					if (appConfigCallBack != null) {
						appConfigCallBack.onAppConfigurationResult();
					}
				}
			});

			return null;
		}
	}
	
	public static void getAdzerkAd(String divId, List<Integer> adFormats, AdCallBack callback) {
		GetAdzerkAdTask getAdzerkAdTask = new GetAdzerkAdTask(divId, adFormats, callback);
		getAdzerkAdTask.execute();
	}
	
	public static interface AdCallBack {
		public void onAdResult(AdzerkAd ad);
	}
		
	private static class GetAdzerkAdTask extends AsyncTask<String, Void, Void> {

		private final String TAG = "GetAdzerkAdTask";
		
		private String divId;
		private AdCallBack adCallBack = null;
		private List<Integer> adFormats;
		
		public GetAdzerkAdTask(String divId, List<Integer> adFormats, AdCallBack adCallBack) {
			this.divId = divId;
			this.adFormats = adFormats;
			this.adCallBack = adCallBack;
		}
		
		//TODO test using the 'page' pattern, but how should we enter the params to the HTTP POST (not get!)
//		@Override
//		protected Void doInBackground(String... params) {
//			SSAdzerkAdPage.getInstance().getPage(divId, new SSPageCallback() {
//				@Override
//				public void onGetPageResult(SSPageGetResult aPageGetResult) {
//					AdzerkAd ad = SSAdzerkAdPage.getInstance().getAd();
//					
//					if(ad != null) {
//						GetAdzerkAdTask.this.adCallBack.onAdResult(ad);
//					}
//				}
//			});
//
//			return null;
//		}
		
		protected Void doInBackground(String... params) {
			AdzerkAd ad = null;
			
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.ADS_POST_URL);
				
				int networkId = AppConfigurationManager.getInstance().getAdzerkNetworkId();
				int siteId = AppConfigurationManager.getInstance().getAdzerkSiteId();
				
				AdzerkJSONObjectPlacement placement = new AdzerkJSONObjectPlacement(divId, networkId, siteId, adFormats);
				
				List<AdzerkJSONObjectPlacement> placements = Arrays.asList(placement);
				
				AdzerkJSONObjectRequest adRequestJSONObject = new AdzerkJSONObjectRequest(placements, true);
				
				String jsonString = new Gson().toJson(adRequestJSONObject);
				
				StringEntity stringEntity = new StringEntity(jsonString);

				httpPost.setEntity(stringEntity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				HttpResponse response = client.execute(httpPost);

				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					HttpEntity entity = response.getEntity();
					String result = null;
					 if (entity != null) {
				            InputStream instream = entity.getContent();
				            result = NetworkUtils.convertStreamToString(instream);
				            instream.close();
				        }
					
					JSONObject jsonObj = new JSONObject(result);
					
					ad = new AdzerkAd(divId, jsonObj);
					
				} else if (response.getStatusLine().getStatusCode() == Consts.BAD_RESPONSE) {
					Log.d(TAG, "Invalid Token!");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(ad != null) {
				this.adCallBack.onAdResult(ad);
			}
			
			return null;
		}
		
		private class AdzerkJSONObjectRequest {
			private AdzerkJSONObjectUser user;
			private boolean isMobile;
			private List<AdzerkJSONObjectPlacement> placements;
			

			public AdzerkJSONObjectRequest(List<AdzerkJSONObjectPlacement> placements, boolean isMobile, AdzerkJSONObjectUser user) {
				this.user = user;
				this.placements = placements;
				this.isMobile = isMobile;
			}
			
			public AdzerkJSONObjectRequest(List<AdzerkJSONObjectPlacement> placements, boolean isMobile) {
				this(placements, isMobile, null);
			}
		}
		
		private class AdzerkJSONObjectPlacement {
			private String divName;
			private Integer networkId;
			private Integer siteId;
			private List<Integer> adTypes;
			
			public AdzerkJSONObjectPlacement(String divName, Integer networkId, Integer siteId, List<Integer> adTypes) {
				this.divName = divName;
				this.networkId = networkId;
				this.siteId = siteId;
				this.adTypes = adTypes;
			}
		}
		
		private class AdzerkJSONObjectUser {
			private String userKey;
			
			public AdzerkJSONObjectUser(String userKey) {
				this.userKey = userKey;
			}
		}
	}
	
	public static interface ApiVersionCallback {
		public void onApiVersionResult();
	}
	
	public static void getApiVersion(ApiVersionCallback apiVersionCallback) {
		GetApiVersionTask getApiVersionTask = new GetApiVersionTask(apiVersionCallback);
		getApiVersionTask.execute();
	}
	
	private static class GetApiVersionTask extends AsyncTask<String, Void, Void> {

		private ApiVersionCallback apiVersionCallback = null;

		public GetApiVersionTask(ApiVersionCallback apiVersionCallback) {
			this.apiVersionCallback = apiVersionCallback;
		}

		@Override
		protected Void doInBackground(String... params) {
			SSApiVersionPage.getInstance().getPage(new SSPageCallback() {

				@Override
				public void onGetPageResult(SSPageGetResult pageGetResult) {
					if (apiVersionCallback != null) {
						apiVersionCallback.onApiVersionResult();
					}
				}
			});

			return null;
		}
	}
	

		
	// task to get the tvguide for all the channels
	private static class GetGuide extends AsyncTask<Context, String, Void> {

		private TvDate	mDate;
		private boolean	mIsChannel;

		public GetGuide(TvDate date, boolean isChannel) {
			this.mDate = date;
			this.mIsChannel = isChannel;
		}

		@Override
		protected Void doInBackground(Context... params) {
			// get guide for the date
			ArrayList<String> channelIds = MiTVStore.getInstance().getChannelIds();
			
			mChannelsIds = channelIds;

			String guidePageUrl = getPageUrl(mDate.getDate(), mChannelsIds);
			
			SSGuidePage.getInstance().getPage(guidePageUrl, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					ArrayList<ChannelGuide> channelGuides = SSGuidePage.getInstance().getGuide();
					mChannelGuides = channelGuides;

					if (mChannelGuides != null && mChannelGuides.isEmpty() != true) {
						boolean guideSaveOperationSuccessfull;

						guideSaveOperationSuccessfull = MiTVStoreOperations.saveGuides(mChannelGuides, mDate.getDate());
						if(guideSaveOperationSuccessfull) {
							if (prepareTaggedContent(mDate)) {
	
								Intent intent = null;
								if (mIsChannel) {
									// notify the ChannelPageActivity that the guide is available and UI may be updated
									intent = new Intent(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE_VALUE, true);
								} else {
									// notify the HomeActivity that the guide is available and UI may be updated
									intent = new Intent(Consts.INTENT_EXTRA_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, true);
								}
								LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
							}
							
							if(SecondScreenApplication.isLoggedIn()) {
								// get the list of likes and save in MiTVStore to avoid excessive backend requests
								ArrayList<String> likeIds = LikeService.getLikeIdsList();
								MiTVStore.getInstance().setLikeIds(likeIds);
							}
						}
					}
				}
			});
			mChannelGuides = null;
			return null;
		}

	}

	public static void getGuide(int dateIndex, boolean isChannel) {
		Log.d(TAG, "mHasFetchedTvDate:" + mHasFetchedTvDate + "  mHasFetchedTags: " + mHasFetchedTags +  " hasFetchedChannels " + mHasFetchedChannels);
		if (mHasFetchedTvDate && mHasFetchedTags && mHasFetchedChannels) {
			TvDate date = mTvDates.get(dateIndex);
			if (MiTVStore.getInstance().isGuideForDate(date.getDate())) {
				// notify the HomeActivity that the guide is available and UI may be updated

				Log.d(TAG, "There is a ready one!");
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, true));
			} else {

				GetGuide getGuideTask = new GetGuide(date, isChannel);
				getGuideTask.execute(mContext);
			}
		}
	}

	public static boolean filterGuideByTag(TvDate tvDate, Tag tag) {
		ArrayList<Broadcast> taggedBroadcasts = MiTVStoreOperations.getTaggedBroadcasts(tvDate.getDate(), tag);
		if (taggedBroadcasts != null && taggedBroadcasts.isEmpty() != true) {
			MiTVStoreOperations.saveTaggedBroadcast(tvDate, tag, taggedBroadcasts);
			return true;
		} else return false;
	}

	public static boolean filterGuides(TvDate tvDate, int count) {
		ArrayList<Tag> tags = MiTVStore.getInstance().getTags();
		boolean result = false;
		for (int i = 1; i < count; i++) {
			filterGuideByTag(tvDate, tags.get(i));
			result = true;
		}
		return result;
	}

	// construct the url for the guide
	private static String getPageUrl(String date, ArrayList<String> channelIds) {
		StringBuilder sB = new StringBuilder();
		sB.append(Consts.URL_GUIDE);

		sB.append(Consts.REQUEST_QUERY_SEPARATOR);
		sB.append(date);

		sB.append(Consts.REQUEST_PARAMETER_SEPARATOR);
		for (int i = 0; i < channelIds.size(); i++) {
			if (i == 0) {
				sB.append(Consts.API_CHANNEL_ID_WITH_EQUALS_SIGN);
				sB.append(channelIds.get(i));
			} else {
				sB.append(Consts.REQUEST_QUERY_AND);
				sB.append(Consts.API_CHANNEL_ID_WITH_EQUALS_SIGN);
				sB.append(channelIds.get(i));
			}
		}
		return sB.toString();
	}

	public static void resetAll() {
		mDateIndex = 0;
		mHasFetchedTvDate = false;
		mHasFetchedTags = false;

		if (mTvDates != null) {
			if (!mTvDates.isEmpty()) {
				mTvDates.clear();
				mTvDates = new ArrayList<TvDate>();
			}
		}

		if (mChannels != null) {
			if (!mChannels.isEmpty()) {
				mChannels.clear();
				mChannels = new ArrayList<Channel>();
			}
		}

		if (mTags != null) {
			if (!mTags.isEmpty()) {
				mTags.clear();
				mTags = new ArrayList<Tag>();
			}
		}

		if (mChannelGuides != null) {
			if (!mChannelGuides.isEmpty()) {
				mChannelGuides.clear();
				mChannelGuides = new ArrayList<ChannelGuide>();
			}
		}

		if (mChannelsIds != null) {
			if (!mChannelsIds.isEmpty()) {
				mChannelsIds.clear();
				mChannelsIds = new ArrayList<String>();
			}
		}

	}
}
