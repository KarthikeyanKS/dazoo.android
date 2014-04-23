
package com.mitv.asynctasks.mitvapi.usertoken;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import android.util.Log;

import com.mitv.Constants;
import com.mitv.enums.FeedItemTypeEnum;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.models.comparators.TVFeedItemComparatorByTime;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVFeedItem;
import com.mitv.utilities.DateUtils;



public class GetUserTVFeedItems
	extends AsyncTaskWithUserToken<TVFeedItem[]> 
{
	private static final String TAG = GetUserTVFeedItems.class.getName();
	
	private static final String URL_SUFFIX = Constants.URL_ACTIVITY_FEED;
	
	private ArrayList<TVFeedItem> oldFeedItemsToDelete = new ArrayList<TVFeedItem>();
	private ArrayList<TVFeedItem> similarFeedItemsToDelete = new ArrayList<TVFeedItem>();
	ArrayList<TVFeedItem> activityFeed = new ArrayList<TVFeedItem>();
	
	private static RequestIdentifierEnum getRequestIdentifier(int itemStartIndex)
	{
		if(itemStartIndex == 0)
		{
			return RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM;
		}
		else
		{
			return RequestIdentifierEnum.USER_ACTIVITY_FEED_ITEM_MORE;
		}
	}
	
	
	
	public GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener)
	{
		this(contentCallbackListener, activityCallbackListener, false, 0, false, 0);
	}
	
	
	
	public GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			int itemStartIndex,
			int itemLimit)
	{
		this(contentCallbackListener, activityCallbackListener, true, itemStartIndex, true, itemLimit);
	}
	
	
	
	private GetUserTVFeedItems(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			boolean useItemStartIndex,
			int itemStartIndex,
			boolean useItemLimit,
			int itemLimit)
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(itemStartIndex), TVFeedItem[].class, HTTPRequestTypeEnum.HTTP_GET, URL_SUFFIX);
		
		if(useItemStartIndex)
		{
			this.urlParameters.add(Constants.API_SKIP, String.valueOf(itemStartIndex));
		}
		
		if(useItemLimit)
		{
			this.urlParameters.add(Constants.API_LIMIT, String.valueOf(itemLimit));
		}
	}
	
	
	
	@Override
	protected Void doInBackground(String... params) 
	{
		super.doInBackground(params);
		
//		Log.d(TAG, "Removing FETCHING DATA WE SHOULD UPDATE FEEDACTIVITY!!!!!!!!!!!!!! ");
		 
		/* IMPORTANT, PLEASE OBSERVE, CHANGING CLASS OF CONTENT TO NOT REFLECT TYPE SPECIFIED IN CONSTRUCTOR CALL TO SUPER */
		if(requestResultStatus.wasSuccessful() && requestResultObjectContent != null)
		{
//			Log.d(TAG, "Removing FETCHING DATA WAS SUCCESSFUL!!!!!!!!!!!!!! ");
			
			TVFeedItem[] contentAsArray = (TVFeedItem[]) requestResultObjectContent;
		
			ArrayList<TVFeedItem> contentAsArrayList = new ArrayList<TVFeedItem>(Arrays.asList(contentAsArray));
			
			Collections.sort(contentAsArrayList, new TVFeedItemComparatorByTime());
			
			if (Constants.ENABLE_FILTER_IN_FEEDACTIVITY) {
				
				if (ContentManager.sharedInstance().getFromCacheActivityFeedData() != null) {
//					activityFeed = ContentManager.sharedInstance().getFromCacheActivityFeedData();
//					activityFeed.addAll(contentAsArrayList);
//					contentAsArrayList = filterOldBroadcasts(activityFeed, contentAsArrayList);
//					contentAsArrayList = filterSimilarBroadcasts(activityFeed, contentAsArrayList);
					
				} else {
					contentAsArrayList = filterOldBroadcasts(contentAsArrayList, null);
//					contentAsArrayList = filterSimilarBroadcasts(contentAsArrayList, null);
				}
			}
		
			requestResultObjectContent = contentAsArrayList;
		}
		else
		{
			Log.w(TAG, "The requestResultObjectContent is null.");
		}
		 
		return null;
	}
	
	
	
	
	/**
	 * Filter out broadcast with a beginTime that has passed.
	 * 
	 * @param activityFeed
	 */
	public ArrayList<TVFeedItem> filterOldBroadcasts(ArrayList<TVFeedItem> activityFeed, ArrayList<TVFeedItem> contentAsArrayList) {
		if (!activityFeed.isEmpty() && activityFeed != null) {

			for (int i = 0; i < activityFeed.size(); i++) {

				TVFeedItem item = activityFeed.get(i);

				if (item.getItemType() != FeedItemTypeEnum.POPULAR_BROADCASTS) {

					TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo = item.getBroadcast();

					boolean isItemTooOld = checkIfTVFeedItemIsOld(tvBroadcastWithChannelInfo);

					/* If item is too old or if it has shown before the item will be removed */
					if (isItemTooOld) {
						Log.d(TAG, "Removing old broadcasts from list!! Broadcast removed: " + tvBroadcastWithChannelInfo.getTitle() + " Starttime: "
								+ tvBroadcastWithChannelInfo.getBeginTime());
						
						oldFeedItemsToDelete.add(item);
					}
				}
			}
			
			if (oldFeedItemsToDelete.size() > 0 && !oldFeedItemsToDelete.isEmpty() && oldFeedItemsToDelete != null) {
				
				if (contentAsArrayList != null) {
					contentAsArrayList.removeAll(oldFeedItemsToDelete);
					
				} else {
					activityFeed.removeAll(oldFeedItemsToDelete);
					contentAsArrayList = activityFeed;
				}
			}
		}
		
		return contentAsArrayList;
	}
	
	
	
	/**
	 * Check if TVFeedItem is too old and needs to be removed from the activityFeed.
	 * 
	 * @param item
	 * @return true: Item will be removed
	 * @return false: Item will NOT be removed
	 */
	private boolean checkIfTVFeedItemIsOld(TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo) {
		boolean removeItem = false;
		
		Long timeZoneOffsetInMillis = DateUtils.getTimeZoneOffsetInMillis();
		
		Calendar now = DateUtils.getNow();
		
		Long nowLong = now.getTimeInMillis() + timeZoneOffsetInMillis;
		
		Long beginTime = tvBroadcastWithChannelInfo.getBeginTimeMillis();
		
		int difference = beginTime.compareTo(nowLong);
		
		/* beginTime is less than nowLong: Item should NOT be included in the feed activity. Items is old */
		if (difference < 0) {
			removeItem = true;
		}		
		
		return removeItem;
	}
	
	
	
	/**
	 * Filter out broadcasts that has been shown for more than two times.
	 * 
	 * @param activityFeed
	 */
	private ArrayList<TVFeedItem> filterSimilarBroadcasts(ArrayList<TVFeedItem> activityFeed, ArrayList<TVFeedItem> contentAsArrayList) {
		if (!activityFeed.isEmpty() && activityFeed != null) {

			for (int i = activityFeed.size() - 1; i > 2; i--) {

				TVFeedItem item = activityFeed.get(i);

				if (item.getItemType() != FeedItemTypeEnum.POPULAR_BROADCASTS) {

					TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo = item.getBroadcast();

					boolean hasItemBeenShownBefore = checkIfTVFeedItemHasShownBefore(activityFeed, tvBroadcastWithChannelInfo);

					/* If item is too old or if it has shown before the item will be removed */
					if (hasItemBeenShownBefore) {
						Log.d(TAG, "Removing broadcasts from list!! More than 2 times, Broadcast removed: " + tvBroadcastWithChannelInfo.getTitle());

						similarFeedItemsToDelete.add(item);
					}
				}
			}

			if (similarFeedItemsToDelete.size() > 0 && !similarFeedItemsToDelete.isEmpty() && similarFeedItemsToDelete != null) {

				if (contentAsArrayList != null) {
					contentAsArrayList.removeAll(similarFeedItemsToDelete);

				} else {
					activityFeed.removeAll(similarFeedItemsToDelete);
					contentAsArrayList = activityFeed;
				}
			}
		}
		
		return contentAsArrayList;
	}
	
	
	
	/**
	 * Start checking from item number 3 in the list if it is a repetition or not.
	 * 
	 * @param activityFeed
	 * @param tvBroadcastWithChannelInfo
	 * @return TRUE: If repetition
	 * @return FALSE: If NOT repetition
	 */
	private boolean checkIfTVFeedItemHasShownBefore(ArrayList<TVFeedItem> activityFeed, TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo) {
		int counterHowManyTimesItemAppearsInList = 0;
		String nameOfItem = tvBroadcastWithChannelInfo.getTitle();
		
		for (int i = 0; i < activityFeed.size(); i++) {
			
			TVFeedItem item = activityFeed.get(i);
			TVBroadcastWithChannelInfo tvBroadcastWithChannelInfoFromList = item.getBroadcast();
			
			if (tvBroadcastWithChannelInfoFromList != null) {
				String nameOfItemInList = tvBroadcastWithChannelInfoFromList.getTitle();
				
				if (nameOfItemInList.equals(nameOfItem)) {
					counterHowManyTimesItemAppearsInList++;
				}
				
				if (counterHowManyTimesItemAppearsInList > 2) {
					return true;
				}
			}
			
		}
		
		return false;
	}
}