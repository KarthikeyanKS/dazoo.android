
package com.mitv.manager;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.millicom.asynctasks.AdzerkAdTask;
import com.millicom.asynctasks.ApiVersionTask;
import com.millicom.asynctasks.GetMyChannelsTask;
import com.millicom.asynctasks.UpdateMyChannelsTask;
import com.millicom.interfaces.AdCallBackInterface;
import com.millicom.interfaces.ApiVersionCallbackInterface;
import com.millicom.mitv.activities.MyProfileActivity;
import com.mitv.Consts;
import com.mitv.LikeService;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.content.SSAppConfigurationPage;
import com.mitv.content.SSChannelPage;
import com.mitv.content.SSGuidePage;
import com.mitv.content.SSPageCallback;
import com.mitv.content.SSPageGetResult;
import com.mitv.content.SSTagsPage;
import com.mitv.content.SSTvDatePage;
import com.mitv.model.OldBroadcast;
import com.mitv.model.OldTVChannel;
import com.mitv.model.OldTVChannelGuide;
import com.mitv.model.OldTVTag;
import com.mitv.model.OldTVDate;
import com.mitv.storage.MiTVStore;
import com.mitv.storage.MiTVStoreOperations;



public class ApiClient 
{
	private static final String TAG	= "MiTVCore";
	
	private static SecondScreenApplication application = SecondScreenApplication.getInstance();
	
	private static Context				mContext;
	private static int					mDateIndex			= 0;
	
	private static boolean				mHasFetchedTvDate			= false;
	private static boolean				mHasFetchedTags				= false;
	private static boolean				mHasFetchedChannels			= false;
	
	private static boolean				mShouldRefreshGuide			= true;
	private static boolean				mIsRefreshingGuide			= false;



	private ApiClient(){}

	/**
	 * MiTVCoreHolder is loaded on the first execution of MiTVCore.getInstance() or the first access to the MiTVCoreHolder.INSTANCE, not before
	 */
	private static class MiTVCoreHolder 
	{
		public static final ApiClient	INSTANCE	= new ApiClient();
	}

	public static ApiClient getInstance(Context context, int dateIndex) {
		mContext = context;
		mDateIndex = dateIndex;
		return MiTVCoreHolder.INSTANCE;
	}
	
	
	
	public static boolean ismShouldRefreshGuide() 
	{
		return mShouldRefreshGuide;
	}

	public static void setmShouldRefreshGuide(boolean mShouldRefreshGuide) 
	{
		ApiClient.mShouldRefreshGuide = mShouldRefreshGuide;
	}
	
	public static boolean ismIsRefreshingGuide() 
	{
		return mIsRefreshingGuide;
	}
	
	public static void setmIsRefreshingGuide(boolean mIsRefreshingGuide) 
	{
		ApiClient.mIsRefreshingGuide = mIsRefreshingGuide;
	}
	
	
	

	public void fetchContent() 
	{
		mHasFetchedTags = false;
		mHasFetchedTvDate = false;
		mHasFetchedChannels = false;
		
		getDates();
		getTags();
		getChannels();
	}
	

	
	private void getTags() 
	{
		ArrayList<OldTVTag> tags = MiTVStore.getInstance().getTags();
		
		if (tags == null || tags.isEmpty()) 
		{
			GetTags tagsTask = new GetTags();
			tagsTask.execute();
		} 
		else
		{
			/* Tags already fetched, try fetch guide */
			mHasFetchedTags = true;
			getGuide(mDateIndex, false);
		}
	}
	
	
	
	private void getDates() 
	{
		ArrayList<OldTVDate> tvDates = MiTVStore.getInstance().getTvDates();
		
		if (tvDates == null || tvDates.isEmpty()) 
		{
			GetTvDates tvDatesTask = new GetTvDates();
			tvDatesTask.execute();
		} 
		else 
		{
			/* Dates already fetched, try fetch guide */
			mHasFetchedTvDate = true;
			getGuide(mDateIndex, false);
		}
	}
	
	
	
	private void getChannels() 
	{
		HashMap<String, OldTVChannel> channels = MiTVStore.getInstance().getChannels();
		
		if (channels == null || channels.isEmpty()) 
		{	
			/* If logged in, fetch ALL channels, and fetch your selected channels ids using the 'MyChannelService' */
			if (SecondScreenApplication.isLoggedIn()) 
			{
				getMyChannelIds();
				
				GetAllChannelsTask allChannelsTask = new GetAllChannelsTask();
				allChannelsTask.execute();
			} 
			else 
			{
				GetDefaultChannelsTask defaultChannelsTask = new GetDefaultChannelsTask();
				defaultChannelsTask.execute();
			}
		} 
		else 
		{
			/* Channels already fetched, try fetch guide */
			mHasFetchedChannels = true;
			getGuide(mDateIndex, false);
		}
	}

	
	
	// prepare tagged broadcasts for the specific date
	private static boolean prepareTaggedContent(OldTVDate date) 
	{
		ArrayList<OldTVTag> tags = MiTVStore.getInstance().getTags();
		
		if (tags != null && 
			tags.isEmpty() != true) 
		{
			ArrayList<OldTVChannelGuide> channelGuides = MiTVStore.getInstance().getChannelGuides();
			
			if (channelGuides != null && channelGuides.isEmpty() != true) 
			{
				for (OldTVTag tag : tags)
				{
					ArrayList<OldBroadcast> taggedBroadcasts = null;
					
					taggedBroadcasts = MiTVStoreOperations.getTaggedBroadcasts(date.getDate(), tag);
					
					MiTVStoreOperations.saveTaggedBroadcast(date, tag, taggedBroadcasts);
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
					ArrayList<OldTVDate> tvDates =  SSTvDatePage.getInstance().getTvDates();

					if (tvDates != null && tvDates.isEmpty() != true) {
						Log.d(TAG, "Dates: " + tvDates.size());
						MiTVStore.getInstance().setTvDates(tvDates);
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
	private static class GetTags extends AsyncTask<String, String, Void> 
	{
		@Override
		protected Void doInBackground(String... params) 
		{
			SSTagsPage.getInstance().getPage(new SSPageCallback() 
			{
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) 
				{
					ArrayList<OldTVTag> tags = SSTagsPage.getInstance().getTags();

					if (tags != null && tags.isEmpty() != true) 
					{
						Log.d(TAG, "Tags: " + tags.size());

						// insert general Tag category in the list
						OldTVTag tagAll = new OldTVTag();
						tagAll.setId(mContext.getResources().getString(R.string.all_categories_id));
						tagAll.setName(mContext.getResources().getString(R.string.all_categories_name));
						tags.add(0, tagAll);

						MiTVStore.getInstance().setTags(tags);
						mHasFetchedTags = true;

						// attempt the get the guide
						getGuide(mDateIndex, false);
					} 
					else 
					{
						LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_BAD_REQUEST));
					}
				}
			});

			return null;
		}
	}

	
	
	// task to get all channels to be listed in the channel selection list under MyProfile/MyChannels
	private static class GetChannelsTask extends AsyncTask<String, String,  Void> 
	{
		@Override
		protected Void doInBackground(String... params) 
		{	
			String getChannelsBaseUrl = params[0];

			SSChannelPage.getInstance().getPage(getChannelsBaseUrl, new SSPageCallback() 
			{
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) 
				{
					ArrayList<OldTVChannel> channels = SSChannelPage.getInstance().getChannels();

					if (channels != null && channels.isEmpty() != true)
					{
						Log.d(TAG, "ALL Channels: " + channels.size());

						// store the list channels (used in the my profile/my guide)
						MiTVStoreOperations.saveChannels(channels);

						mHasFetchedChannels = true;

						// attempt the common callback interface
						getGuide(mDateIndex, false);
					} 
					else 
					{
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
	
	
	
	public static void getAdzerkAd(String divId, List<Integer> adFormats, AdCallBackInterface callback)
	{
		AdzerkAdTask getAdzerkAdTask = new AdzerkAdTask(divId, adFormats, callback);
		getAdzerkAdTask.execute();
	}

	
	
	public static void getApiVersion(ApiVersionCallbackInterface callback) 
	{
		ApiVersionTask getApiVersionTask = new ApiVersionTask(callback);
		getApiVersionTask.execute();
	}
	
	
	
	
	// task to get the tvguide for all the channels
	private static class GetGuideTask extends AsyncTask<Context, String, Void> 
	{
		private OldTVDate	mDate;
		private boolean	mIsChannel;

		public GetGuideTask(OldTVDate date, boolean isChannel) 
		{
			this.mDate = date;
			this.mIsChannel = isChannel;
		}
		
		
		
		@Override
		protected void onPreExecute() 
		{
			mIsRefreshingGuide = true;

			super.onPreExecute();
		}
		
		

		@Override
		protected Void doInBackground(Context... params) 
		{
			// get guide for the date
			ArrayList<String> channelIds = MiTVStore.getInstance().getChannelIds();
			
			String guidePageUrl = getPageUrl(mDate.getDate(), channelIds);
			
			SSGuidePage.getInstance().getPage(guidePageUrl, new SSPageCallback() 
			{
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) 
				{
					ArrayList<OldTVChannelGuide> channelGuides = SSGuidePage.getInstance().getGuide();
					
					if (channelGuides != null && channelGuides.isEmpty() != true)
					{
						boolean guideSaveOperationSuccessfull;

						guideSaveOperationSuccessfull = MiTVStoreOperations.saveGuides(channelGuides, mDate.getDate());
						
						if(guideSaveOperationSuccessfull)
						{
							if (prepareTaggedContent(mDate)) 
							{	
								Intent intent = null;
								
								if (mIsChannel)
								{
									// notify the ChannelPageActivity that the guide is available and UI may be updated
									intent = new Intent(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE_VALUE, true);
								}
								else
								{
									// notify the HomeActivity that the guide is available and UI may be updated
									intent = new Intent(Consts.INTENT_EXTRA_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, true);
								}
								
								LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
							}
							
							if(SecondScreenApplication.isLoggedIn()) 
							{
								// get the list of likes and save in MiTVStore to avoid excessive backend requests
								ArrayList<String> likeIds = LikeService.getLikeIdsList();
								MiTVStore.getInstance().setLikeIds(likeIds);
							}
						}
					}
				}
			});

			return null;
		}
		
		
		@Override
		protected void onPostExecute(Void result) 
		{
			mIsRefreshingGuide = false;

			super.onPostExecute(result);
		}

	}

	public static void getGuide(int dateIndex, boolean isChannel) 
	{
		Log.d(TAG, "mHasFetchedTvDate:" + mHasFetchedTvDate + "  mHasFetchedTags: " + mHasFetchedTags +  " hasFetchedChannels " + mHasFetchedChannels);
		
		if (mHasFetchedTvDate && mHasFetchedTags && mHasFetchedChannels) 
		{
			ArrayList<OldTVDate> tvDates = MiTVStore.getInstance().getTvDates();
			
			OldTVDate date = tvDates.get(dateIndex);
			
			if(mShouldRefreshGuide)
			{
				MiTVStore.getInstance().clearChannelGuides();
				
				mShouldRefreshGuide = false;
			}
			// No need for else
			
			if (MiTVStore.getInstance().isGuideForDate(date.getDate()))
			{
				// notify the HomeActivity that the guide is available and UI may be updated
				Log.d(TAG, "There is a ready one!");
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Consts.INTENT_EXTRA_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, true));
			} 
			else 
			{
				GetGuideTask getGuideTask = new GetGuideTask(date, isChannel);
				
				getGuideTask.execute(mContext);
			}
		}
	}

	public static boolean filterGuideByTag(OldTVDate tvDate, OldTVTag tag) {
		ArrayList<OldBroadcast> taggedBroadcasts = MiTVStoreOperations.getTaggedBroadcasts(tvDate.getDate(), tag);
		if (taggedBroadcasts != null && taggedBroadcasts.isEmpty() != true) {
			MiTVStoreOperations.saveTaggedBroadcast(tvDate, tag, taggedBroadcasts);
			return true;
		} else return false;
	}

	public static boolean filterGuides(OldTVDate tvDate, int count) {
		ArrayList<OldTVTag> tags = MiTVStore.getInstance().getTags();
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
	
	
	
	
	
	public static boolean updateMyChannelsList(String channelsJSON) 
	{
		UpdateMyChannelsTask addChannelToMyChannelsTask = new UpdateMyChannelsTask();

		try 
		{
			boolean isAdded = addChannelToMyChannelsTask.execute(channelsJSON).get();
			
			if (isAdded == true)
			{
				Log.d(TAG, "Channels are updated!");
				return true;
			} 
			else 
			{
				Log.d(TAG, "Error! MY CHANNELS are not updated");
				return false;
			}
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}

	
	
	public static void getMyChannelIds() 
	{
		GetMyChannelsTask getMyChannelsTask = new GetMyChannelsTask();

		String responseStr;
		
		try 
		{
			responseStr = getMyChannelsTask.execute().get();
			
			Log.d(TAG, "List of My Channels: " + responseStr);

			if (responseStr != null && 
				TextUtils.isEmpty(responseStr) != true && 
				responseStr != Consts.ERROR_STRING) 
			{
				// the extra check for ERROR_STRING was added to distinguish between empty response (there are no stored channels to this user) and empty response in case of error
				ArrayList<String> channelIds = new ArrayList<String>();
				channelIds = ContentParser.parseChannelIds(new JSONArray(responseStr));
				
				MiTVStore.getInstance().storeMyChannelIds(channelIds);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public static boolean isLoggedIn() {
		boolean isLoggedIn = false;

		String loginToken = application.getAccessToken();

		if (loginToken != null && TextUtils.isEmpty(loginToken) != true) {
			isLoggedIn = true;
		}
		return isLoggedIn;
	}

	public static void forceLogin() {
		if(isLoggedIn()) {
			logout();
		}
		
		login();
	}

	public static void login() {
		if (!isLoggedIn()) {
			Context applicationContext = application.getApplicationContext();
			Intent intent = new Intent(applicationContext, MyProfileActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			applicationContext.startActivity(intent);
		}
	}

	public static void logout() 
	{
		application.setAccessToken(null);
		application.setUserFirstName(null);
		application.setUserLastName(null);
		application.setUserEmail(null);
		application.setUserId(null);
		application.setUserExistringFlag(false);
		
		//TODO do something more effective here??
		MiTVStore.getInstance().clearChannelsAndIdsAndGuide();
		
		ContentManager.updateContent();

//		MiTVStore.getInstance().clearAll();
//		MiTVStore.getInstance().reinitializeAll();
	}
}
