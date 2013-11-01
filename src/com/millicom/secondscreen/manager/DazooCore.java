package com.millicom.secondscreen.manager;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.SSChannelPage;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.SSTagsPage;
import com.millicom.secondscreen.content.SSTvDatePage;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.storage.DazooStoreOperations;

import com.millicom.secondscreen.SecondScreenApplication;

public class DazooCore {
	private static final String	TAG				= "DazooCore";
	private static Context mContext;
	private static String				token;
	private static boolean				mIsTvDate		= false;
	private static boolean				mIsTags			= false;
	private static boolean				mIsChannels		= false;
	private static boolean				mIsMyChannels	= false;
	private static boolean				mIsGuide		= false;

	private ArrayList<TvDate>	mTvDates		= new ArrayList<TvDate>();
	private ArrayList<Channel>	mChannels		= new ArrayList<Channel>();
	private ArrayList<Channel>	mMyChannels		= new ArrayList<Channel>();
	private ArrayList<Tag>		mTags			= new ArrayList<Tag>();
	private ArrayList<Guide>	mGuides			= new ArrayList<Guide>();

	// private constructor prevents instantiation from other classes
	private DazooCore() {};
	
	/**
	 * DazooCoreHolder is loaded on the first execution of DazooCore.getInstance()
	 * or the first access to the DazooCoreHolder.INSTANCE, not before
	 */
	private static class DazooCoreHolder{
		public static final DazooCore INSTANCE = new DazooCore();
	}

	public static DazooCore getInstance(Context context) {
		mContext = context;
		token = ((SecondScreenApplication)context.getApplicationContext()).getAccessToken();
		return DazooCoreHolder.INSTANCE;
	}

	private void getTagsDatesChannels() {
		GetTvDates tvDatesTask = new GetTvDates();
		tvDatesTask.execute();

		GetTags tagsTask = new GetTags();
		tagsTask.execute();

		GetChannels channelsTask = new GetChannels();
		channelsTask.execute();
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
			getGuide();
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
			getGuide();
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

	// task to get the all the available channels
	private class GetChannels extends AsyncTask<String, String, Boolean> {

		protected void onPostExecute(Boolean result) {
			if (result) {
				// store the channels
				DazooStoreOperations.saveChannels(mChannels);
				mIsChannels = true;

				// attempt the common callback interface
				getGuide();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			SSChannelPage.getInstance().getPage(new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {

					mChannels = SSChannelPage.getInstance().getChannels();
				}
			});

			if (mChannels != null && mChannels.isEmpty() != true) {
				return true;
			} else return false;
		}
	}

	// task to get the tvguide for all the channels
	private class GetGuide extends AsyncTask<String, String, Boolean> {

		protected void onPostExecute(Boolean result) {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return null;
		}
	}

	public void getGuide() {
		if (mIsTvDate == true && mIsTags == true && mIsChannels) {

		}
	}

}
