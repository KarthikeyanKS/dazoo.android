package com.millicom.secondscreen.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSAdzerkAdPage;
import com.millicom.secondscreen.content.SSChannelPage;
import com.millicom.secondscreen.content.SSGuidePage;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSTagsPage;
import com.millicom.secondscreen.content.SSTvDatePage;
import com.millicom.secondscreen.content.model.AdzerkAd;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.http.NetworkUtils;
import com.millicom.secondscreen.like.LikeService;
import com.millicom.secondscreen.mychannels.MyChannelsService;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.storage.DazooStoreOperations;
import com.millicom.secondscreen.utilities.JSONUtilities;

public class DazooCore {
	private static final String			TAG					= "DazooCore";
	private static Context				mContext;
	private static String				token;
	private static int					mDateIndex			= 0;
	private static boolean				mIsTvDate			= false;
	private static boolean				mIsTags				= false;
	private static boolean				mIsDefaultChannels	= false;
	public static boolean				mIsAllChannels		= false;
	public static boolean				mIsMyChannels		= false;
	private static boolean				mIsGuide			= false;

	private static ArrayList<TvDate>	mTvDates			= new ArrayList<TvDate>();
	private static ArrayList<Channel>	mDefaultChannels	= new ArrayList<Channel>();
	private static ArrayList<Channel>	mAllChannels		= new ArrayList<Channel>();
	private static ArrayList<Tag>		mTags				= new ArrayList<Tag>();
	private static ArrayList<Guide>		mGuides				= new ArrayList<Guide>();
	private static ArrayList<String>	mMyChannelsIds		= new ArrayList<String>();
	private static ArrayList<String>	mAllChannelsIds		= new ArrayList<String>();
	private static ArrayList<String>	mDefaultChannelsIds	= new ArrayList<String>();

	// private constructor prevents instantiation from other classes
	private DazooCore() {
	};

	/**
	 * DazooCoreHolder is loaded on the first execution of DazooCore.getInstance() or the first access to the DazooCoreHolder.INSTANCE, not before
	 */
	private static class DazooCoreHolder {
		public static final DazooCore	INSTANCE	= new DazooCore();
	}

	public static DazooCore getInstance(Context context, int dateIndex) {
		mContext = context;
		token = ((SecondScreenApplication) context.getApplicationContext()).getAccessToken();
		mDateIndex = dateIndex;
		return DazooCoreHolder.INSTANCE;
	}

	public void fetchContent() {
		getTagsDatesChannels();
	}

	private static void getTagsDatesChannels() {
		if (DazooStore.getInstance().getTvDates() == null || DazooStore.getInstance().getTvDates().isEmpty()) {
			GetTvDates tvDatesTask = new GetTvDates();
			tvDatesTask.execute();
		}

		if (DazooStore.getInstance().getTags() == null || DazooStore.getInstance().getTags().isEmpty()) {
			GetTags tagsTask = new GetTags();
			tagsTask.execute();
		}

		if (token != null && TextUtils.isEmpty(token) != true) {

			// get all channels
			if (DazooStore.getInstance().getAllChannels() == null || DazooStore.getInstance().getAllChannels().isEmpty()) {
				GetAllChannels allChannelsTask = new GetAllChannels();
				allChannelsTask.execute();

				// get info only about user channels
				if (MyChannelsService.getMyChannels(token)) {
					mMyChannelsIds = DazooStore.getInstance().getMyChannelIds();
					mIsMyChannels = true;
				} else {
					mDefaultChannelsIds = DazooStore.getInstance().getDefaultChannelIds();
				}
			}
		} else {
			if (DazooStore.getInstance().getDefaultChannels() == null || DazooStore.getInstance().getDefaultChannels().isEmpty()) {
				// get the default package of channels
				GetDefaultChannels defaultChannelsTask = new GetDefaultChannels();
				defaultChannelsTask.execute();
			}
		}
	}

	// prepare tagged broadcasts for the specific date
	private static boolean prepareTaggedContent(TvDate date, boolean useMy) {
		if (mTags != null && mTags.isEmpty() != true) {
			if (mGuides != null && mGuides.isEmpty() != true) {
				for (int i = 1; i < mTags.size(); i++) {
					ArrayList<Broadcast> taggedBroadcasts = null;
					
					if(useMy) {
						taggedBroadcasts = DazooStoreOperations.getMyTaggedBroadcasts(date.getDate(), mTags.get(i));
						DazooStoreOperations.saveMyTaggedBroadcast(date, mTags.get(i), taggedBroadcasts);
					} else {
						taggedBroadcasts = DazooStoreOperations.getTaggedBroadcasts(date.getDate(), mTags.get(i));
						DazooStoreOperations.saveTaggedBroadcast(date, mTags.get(i), taggedBroadcasts);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	private static boolean prepareTaggedContent(TvDate date) {
		return prepareTaggedContent(date, false);
	}

	// prepare my tagged broadcasts for the specific date
	private static boolean prepareMyTaggedContent(TvDate date) {
		return prepareTaggedContent(date, true);
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
						DazooStore.getInstance().setTvDates(mTvDates);
						mIsTvDate = true;

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

						DazooStore.getInstance().setTags(mTags);
						mIsTags = true;
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
	private static class GetAllChannels extends AsyncTask<String, String, Void> {

		@Override
		protected Void doInBackground(String... params) {
			SSChannelPage.getInstance().getPage(Consts.MILLICOM_SECONDSCREEN_CHANNELS_ALL_PAGE_URL, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mAllChannels = SSChannelPage.getInstance().getChannels();

					if (mAllChannels != null && mAllChannels.isEmpty() != true) {
						Log.d(TAG, "ALL Channels: " + mAllChannels.size());
						// store the list channels (used in the my profile/my guide)
						DazooStoreOperations.saveAllChannels(mAllChannels);
						mIsAllChannels = true;

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

	// task to get the channels in the default package
	private static class GetDefaultChannels extends AsyncTask<String, String, Void> {

		@Override
		protected Void doInBackground(String... params) {
			SSChannelPage.getInstance().getPage(Consts.MILLICOM_SECONDSCREEN_CHANNELS_DEFAULT_PAGE_URL, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mDefaultChannels = SSChannelPage.getInstance().getChannels();
					if (mDefaultChannels != null && mDefaultChannels.isEmpty() != true) {
						Log.d(TAG, "DefaultChannels: " + mDefaultChannels.size());

						DazooStoreOperations.saveDefaultChannels(mDefaultChannels);
						mIsDefaultChannels = true;

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
	
	public static void getAdzerkAd(String divId, AdCallBack callback) {
		GetAdzerkAdTask getAdzerkAdTask = new GetAdzerkAdTask(divId, callback);
		getAdzerkAdTask.execute();
	}
	
	public static interface AdCallBack {
		public void onAdResult(AdzerkAd ad);
	}
	
	private static class GetAdzerkAdTask extends AsyncTask<String, Void, Void> {

		private final String TAG = "GetAdzerkAdTask";
		
		private String divId;
		private AdCallBack adCallBack = null;
		
		public GetAdzerkAdTask(String divId, AdCallBack adCallBack) {
			this.divId = divId;
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
				List<Integer> adTypes = AppConfigurationManager.getInstance().getAdzerkAdFormats();
				
				AdzerkJSONObjectPlacement placement = new AdzerkJSONObjectPlacement(divId, networkId, siteId, adTypes);
				
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
			final boolean loggedIn = token != null && TextUtils.isEmpty(token) != true;
			// get guide for the date
			String guidePageUrl = null;
			if (loggedIn) {
				mMyChannelsIds = DazooStore.getInstance().getMyChannelIds();
				
				if(mMyChannelsIds.isEmpty()) {
					//TODO Show text saying "you have not chosen any channels...."
					Log.w(TAG, "No Channels selected, will try to send request using all channels to fetch guide.");
					mMyChannelsIds = DazooStore.getInstance().getAllChannelIds();
				}
				
				guidePageUrl = getPageUrl(mDate.getDate(), mMyChannelsIds);
			} else {
				mDefaultChannelsIds = DazooStore.getInstance().getDefaultChannelIds();
				guidePageUrl = getPageUrl(mDate.getDate(), mDefaultChannelsIds);
			}

			SSGuidePage.getInstance().getPage(guidePageUrl, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mGuides = SSGuidePage.getInstance().getGuide();

					if (mGuides != null && mGuides.isEmpty() != true) {
						boolean guideSaveOperationSuccessfull;
						if (loggedIn) {
							guideSaveOperationSuccessfull = DazooStoreOperations.saveMyGuides(mGuides, mDate.getDate());
						} else {
							guideSaveOperationSuccessfull = DazooStoreOperations.saveGuides(mGuides, mDate.getDate());
						}
							
						if(guideSaveOperationSuccessfull) {
							if (prepareTaggedContent(mDate, loggedIn)) {
	
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
							
							if(loggedIn) {
								// get the list of likes and save in DazooStore to avoid excessive backend requests
								ArrayList<String> likeIds = LikeService.getLikeIdsList(token);
								DazooStore.getInstance().setLikeIds(likeIds);
							}
						}
					}
				}
			});
			mGuides = null;
			return null;
		}

	}

	public static void getGuide(int dateIndex, boolean isChannel) {
		Log.d(TAG, "APPROACH GUIDE!!!: ");

		if (token != null && TextUtils.isEmpty(token) != true) {
			Log.d(TAG, "mIsTvDate:" + mIsTvDate + "  mIsTags: " + mIsTags + "  mIsAllChannels: " + mIsAllChannels);

			if (mIsTvDate == true && mIsTags == true && mIsAllChannels) {
				TvDate date = mTvDates.get(dateIndex);
				if (DazooStore.getInstance().isMyGuideForDate(date.getDate())) {
					// notify the HomeActivity that the guide is available and UI may be updated

					Log.d(TAG, "There is a ready My one!");
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, true));
				} else {

					GetGuide getGuideTask = new GetGuide(date, isChannel);
					getGuideTask.execute(mContext);
				}
			}
		} else {
			Log.d(TAG, "mIsTvDate:" + mIsTvDate + "  mIsTags: " + mIsTags + "   mIsDefaultChannels: " + mIsDefaultChannels);
			if (mIsTvDate == true && mIsTags == true && mIsDefaultChannels) {
				TvDate date = mTvDates.get(dateIndex);
				if (DazooStore.getInstance().isGuideForDate(date.getDate())) {
					// notify the HomeActivity that the guide is available and UI may be updated

					Log.d(TAG, "There is a ready one!");
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, true));
				} else {

					GetGuide getGuideTask = new GetGuide(date, isChannel);
					getGuideTask.execute(mContext);
				}
			}
		}
	}

	public static boolean filterGuideByTag(TvDate tvDate, Tag tag) {
		ArrayList<Broadcast> taggedBroadcasts = DazooStoreOperations.getTaggedBroadcasts(tvDate.getDate(), tag);
		if (taggedBroadcasts != null && taggedBroadcasts.isEmpty() != true) {
			DazooStoreOperations.saveTaggedBroadcast(tvDate, tag, taggedBroadcasts);
			return true;
		} else return false;
	}

	public static boolean filterGuides(TvDate tvDate, int count) {
		ArrayList<Tag> tags = DazooStore.getInstance().getTags();
		boolean result = false;
		for (int i = 1; i < count; i++) {
			filterGuideByTag(tvDate, tags.get(i));
			result = true;
		}
		return result;
	}

	public boolean filterMyGuidesByTag(TvDate tvDate, Tag tag, int count) {
		ArrayList<Tag> tags = DazooStore.getInstance().getTags();
		boolean result = false;
		for (int i = 1; i < count; i++) {
			filterMyGuideByTag(tvDate, tags.get(i));
			result = true;
		}
		return result;
	}

	public static boolean filterMyGuideByTag(TvDate tvDate, Tag tag) {
		ArrayList<Broadcast> myTaggedBroadcasts = DazooStoreOperations.getMyTaggedBroadcasts(tvDate.getDate(), tag);
		if (myTaggedBroadcasts != null && myTaggedBroadcasts.isEmpty() != true) {
			DazooStoreOperations.saveMyTaggedBroadcast(tvDate, tag, myTaggedBroadcasts);

			// notify responsible fragments that the data is there
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_TAG_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_TAG_GUIDE_AVAILABLE_VALUE, true));
			return true;
		} else return false;
	}

	// construct the url for the guide
	private static String getPageUrl(String date, ArrayList<String> channelIds) {
		StringBuilder sB = new StringBuilder();
		sB.append(Consts.MILLICOM_SECONDSCREEN_GUIDE_PAGE_URL);

		sB.append(Consts.REQUEST_QUERY_SEPARATOR);
		sB.append(date);

		sB.append(Consts.REQUEST_PARAMETER_SEPARATOR);
		for (int i = 0; i < channelIds.size(); i++) {
			if (i == 0) {
				sB.append(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID_WITH_EQUALS_SIGN);
				sB.append(channelIds.get(i));
			} else {
				sB.append(Consts.REQUEST_QUERY_AND);
				sB.append(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID_WITH_EQUALS_SIGN);
				sB.append(channelIds.get(i));
			}
		}
		return sB.toString();
	}

	public static void resetAll() {
		mDateIndex = 0;
		mIsTvDate = false;
		mIsTags = false;
		mIsDefaultChannels = false;
		mIsAllChannels = false;
		mIsMyChannels = false;
		mIsGuide = false;

		if (!mTvDates.isEmpty()) {
			mTvDates.clear();
			mTvDates = new ArrayList<TvDate>();
		}
		if (!mDefaultChannels.isEmpty()) {
			mDefaultChannels.clear();
			mDefaultChannels = new ArrayList<Channel>();
		}
		if (!mAllChannels.isEmpty()) {
			mAllChannels.clear();
			mAllChannels = new ArrayList<Channel>();
		}

		if (!mTags.isEmpty()) {
			mTags.clear();
			mTags = new ArrayList<Tag>();
		}

		if (!mGuides.isEmpty()) {
			mGuides.clear();
			mGuides = new ArrayList<Guide>();
		}

		if (!mMyChannelsIds.isEmpty()) {
			mMyChannelsIds.clear();
			mMyChannelsIds = new ArrayList<String>();
		}

		if (!mAllChannelsIds.isEmpty()) {
			mAllChannelsIds.clear();
			mAllChannelsIds = new ArrayList<String>();
		}

		if (!mDefaultChannelsIds.isEmpty()) {
			mDefaultChannelsIds.clear();
			mDefaultChannelsIds = new ArrayList<String>();
		}

	}
}
