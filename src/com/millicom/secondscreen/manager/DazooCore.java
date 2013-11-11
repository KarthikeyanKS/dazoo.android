package com.millicom.secondscreen.manager;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSBroadcastPage;
import com.millicom.secondscreen.content.SSChannelPage;
import com.millicom.secondscreen.content.SSGuidePage;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSTagsPage;
import com.millicom.secondscreen.content.SSTvDatePage;
import com.millicom.secondscreen.content.model.Broadcast;
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
	private static boolean				mIsDefaultChannels	= false;
	private static boolean				mIsAllChannels		= false;
	private static boolean				mIsMyChannels		= false;
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
		GetTvDates tvDatesTask = new GetTvDates();
		tvDatesTask.execute();

		GetTags tagsTask = new GetTags();
		tagsTask.execute();

		if (token != null && TextUtils.isEmpty(token) != true) {

			// get all channels
			GetAllChannels allChannelsTask = new GetAllChannels();
			allChannelsTask.execute();

			// get info only about user channels
			if (MyChannelsService.getMyChannels(token)) {
				mMyChannelsIds = DazooStore.getInstance().getMyChannelIds();
				mIsMyChannels = true;
			} else {
				mDefaultChannelsIds = DazooStore.getInstance().getDefaultChannelIds();
			}
		} else {
			// get the default package of channels
			GetDefaultChannels defaultChannelsTask = new GetDefaultChannels();
			defaultChannelsTask.execute();
		}
	}

	// prepare tagged broadcasts for the specific date
	private static boolean prepareTaggedContent(TvDate date) {
		if (mTags != null && mTags.isEmpty() != true) {
			if (mGuides != null && mGuides.isEmpty() != true) {
				for (int i = 1; i < mTags.size(); i++) {
					ArrayList<Broadcast> taggedBroadcasts = DazooStoreOperations.getTaggedBroadcasts(date.getDate(), mTags.get(i));
					DazooStoreOperations.saveTaggedBroadcast(date, mTags.get(i), taggedBroadcasts);
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
						DazooStore.getInstance().setTvDates(mTvDates);
						mIsTvDate = true;

						// attempt the common callback interface
						getGuide(mDateIndex, false);
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
			String guidePageUrl = null;
			if (token != null && TextUtils.isEmpty(token) != true) {
				mMyChannelsIds = DazooStore.getInstance().getMyChannelIds();
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
						if (token != null && TextUtils.isEmpty(token) != true) {
							if (DazooStoreOperations.saveMyGuides(mGuides, mDate.getDate())) {

								if (prepareTaggedContent(mDate)) {
									if (mIsChannel) {
										// notify the ChannelPageActivity that the guide is available and UI may be updated
										LocalBroadcastManager.getInstance(mContext).sendBroadcast(
												new Intent(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE_VALUE, true));

									} else {
										// notify the HomeActivity that the guide is available and UI may be updated
										LocalBroadcastManager.getInstance(mContext).sendBroadcast(
												new Intent(Consts.INTENT_EXTRA_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, true));
									}
								}
							}
						} else {

							if (DazooStoreOperations.saveGuides(mGuides, mDate.getDate())) {

								if (prepareTaggedContent(mDate)) {

									if (mIsChannel) {
										// notify the ChannelPageActivity that the guide is available and UI may be updated
										LocalBroadcastManager.getInstance(mContext).sendBroadcast(
												new Intent(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_CHANNEL_GUIDE_AVAILABLE_VALUE, true));

									} else {
										// notify the HomeActivity that the guide is available and UI may be updated
										LocalBroadcastManager.getInstance(mContext).sendBroadcast(
												new Intent(Consts.INTENT_EXTRA_GUIDE_AVAILABLE).putExtra(Consts.INTENT_EXTRA_GUIDE_AVAILABLE_VALUE, true));
									}
								}
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
		Log.d(TAG, "mIsTvDate:" + mIsTvDate + "  mIsTags: " + mIsTags + "   mIsDefaultChannels: " + mIsDefaultChannels + "  mIsAllChannels: " + mIsAllChannels);
		if (mIsTvDate == true && mIsTags == true && ((mIsDefaultChannels) || (mIsAllChannels))) {
			TvDate date = mTvDates.get(dateIndex);
			GetGuide getGuideTask = new GetGuide(date, isChannel);
			getGuideTask.execute(mContext);
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
}
