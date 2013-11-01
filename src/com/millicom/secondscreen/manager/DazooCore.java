package com.millicom.secondscreen.manager;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSChannelPage;
import com.millicom.secondscreen.content.SSGuidePage;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSTagsPage;
import com.millicom.secondscreen.content.SSTvDatePage;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.mychannels.MyChannelsService;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.storage.DazooStoreOperations;

public class DazooCore {
	private static final String			TAG					= "DazooCore";
	private static Context				mContext;
	private static String				token;
	private static int					mDateIndex			= 0;
	private static boolean				mIsTvDate			= false;
	private static boolean				mIsTags				= false;
	private static boolean				mIsChannels			= false;
	private static boolean				mIsDefaultChannels	= false;
	private static boolean				mIsListChannels		= false;
	private static boolean				mIsMyChannels		= false;
	private static boolean				mIsGuide			= false;

	private static ArrayList<TvDate>	mTvDates			= new ArrayList<TvDate>();
	private static ArrayList<Channel>	mDefaultChannels	= new ArrayList<Channel>();
	private static ArrayList<Channel>	mListChannels		= new ArrayList<Channel>();
	private static ArrayList<Tag>		mTags				= new ArrayList<Tag>();
	private static ArrayList<Guide>		mGuides				= new ArrayList<Guide>();
	private static ArrayList<String>	mMyChannelsIds		= new ArrayList<String>();
	private static ArrayList<String>	mChannelsIds		= new ArrayList<String>();

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

	private void getTagsDatesChannels() {
		GetTvDates tvDatesTask = new GetTvDates();
		tvDatesTask.execute();

		GetTags tagsTask = new GetTags();
		tagsTask.execute();

		// get the information about the available channels
		GetDefaultChannels channelsTask = new GetDefaultChannels();
		channelsTask.execute();

		if (token != null && TextUtils.isEmpty(token) != true) {
			// get info only about user channels
			if (MyChannelsService.getMyChannels(token)) {
				mMyChannelsIds = DazooStore.getInstance().getMyChannelIds();
			} else {
				mChannelsIds = DazooStore.getInstance().getDefaultChannelIds();
			}
		}
	}

	// task to get the tv-dates
	private class GetTvDates extends AsyncTask<String, String, Boolean> {

		protected void onPostExecute(Boolean result) {
			if (result) {
				// store the dates in the storage singleton
				DazooStore.getInstance().setTvDates(mTvDates);
				mIsTvDate = true;
			}

			// attempt the common callback interface
			getGuide(mDateIndex);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			SSTvDatePage.getInstance().getPage(new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mTvDates = SSTvDatePage.getInstance().getTvDates();
				}
			});

			if (mTvDates != null && mTvDates.isEmpty() != true) {
				return true;
			} else return false;
		}
	}

	// task to get the tags
	private class GetTags extends AsyncTask<String, String, Boolean> {

		protected void onPostExecute(Boolean result) {
			if (result) {
				DazooStore.getInstance().setTags(mTags);
				mIsTags = true;
			}

			// attempt the get the guide
			getGuide(mDateIndex);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			SSTagsPage.getInstance().getPage(new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mTags = SSTagsPage.getInstance().getTags();
				}
			});

			if (mTags != null && mTags.isEmpty() != true) return true;
			else return false;
		}
	}

	// task to get all channels to be listed in the channel selection list under MyProfile/MyChannels
	private class GetListChannels extends AsyncTask<String, String, Boolean> {

		protected void onPostExecute(Boolean result) {
			if (result) {
				// store the list channels (used in the my profile/my guide)
				DazooStoreOperations.saveListChannels(mListChannels);
				mIsListChannels = true;
				mIsChannels = true;
				
				// attempt the common callback interface
				getGuide(mDateIndex);
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			SSChannelPage.getInstance().getPage(Consts.MILLICOM_SECONDSCREEN_CHANNELS_PAGE_URL, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mListChannels = SSChannelPage.getInstance().getChannels();
				}
			});

			if (mListChannels != null && mListChannels.isEmpty() != true) {
				return true;
			} else return false;
		}

	}

	// task to get the channels in the default package
	private class GetDefaultChannels extends AsyncTask<String, String, Boolean> {

		protected void onPostExecute(Boolean result) {
			if (result) {
				// store the default channels (used in the tvguide)
				DazooStoreOperations.saveDefaultChannels(mDefaultChannels);
				mIsDefaultChannels = true;
				mIsChannels = true;

				// attempt the common callback interface
				getGuide(mDateIndex);
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			SSChannelPage.getInstance().getPage(Consts.MILLICOM_SECONDSCREEN_CHANNELS_DEFAULT_PAGE_URL, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {

					mDefaultChannels = SSChannelPage.getInstance().getChannels();
				}
			});

			if (mDefaultChannels != null && mDefaultChannels.isEmpty() != true) {
				return true;
			} else return false;
		}
	}

	// task to get the tvguide for all the channels
	private class GetGuide extends AsyncTask<String, String, Boolean> {

		protected void onPostExecute(Boolean result) {
			// save the guide
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String date = params[0];

			// get guide for the date
			String guidePageUrl = null;
			if (token != null && TextUtils.isEmpty(token) != true) {
				guidePageUrl = getPageUrl(date, mMyChannelsIds);
			} else {
				guidePageUrl = getPageUrl(date, mChannelsIds);
			}

			SSGuidePage.getInstance().getPage(guidePageUrl, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {
					mGuides = SSGuidePage.getInstance().getGuide();
				}
			});
			if (mGuides != null && mGuides.isEmpty() != true) {
				return true;
			}
			return false;
		}
	}

	public void getGuide(int dateIndex) {
		if (mIsTvDate == true && mIsTags == true && mIsChannels) {
			String date = mTvDates.get(dateIndex).getDate();
			GetGuide getGuideTask = new GetGuide();
			getGuideTask.execute(date);
		}
	}
	
	public boolean saveMyGuideTable(ArrayList<Guide> myGuideTable, TvDate tvDate){
		return DazooStoreOperations.saveMyGuides(myGuideTable, tvDate);
	}
	
	public boolean saveGuideTable(ArrayList<Guide> guideTable, TvDate tvDate){
		return DazooStoreOperations.saveGuides(guideTable, tvDate);
	}

	// construct the url for the guide
	private String getPageUrl(String date, ArrayList<String> channelIds) {
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

}
