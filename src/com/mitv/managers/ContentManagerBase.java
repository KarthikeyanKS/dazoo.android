
package com.mitv.managers;



import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.mitv.APIClient;
import com.mitv.enums.FeedItemTypeEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.Cache;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.models.objects.mitvapi.TVFeedItem;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;



public abstract class ContentManagerBase
	implements ContentCallbackListener
{
	private static final String TAG = ContentManagerBase.class.getName();
	
	
	
	private CacheManager contentManagerCache;
	private APIClient apiClient;
	
	private FetchDataProgressCallbackListener fetchDataProgressCallbackListener;
	
	protected boolean completedTVDatesRequest;
	protected boolean completedTVChannelIdsDefaultRequest;
	protected boolean completedTVChannelIdsUserRequest;
	protected boolean completedTVGuideRequest;
	protected boolean isFetchingTVGuide;
	protected boolean isAPIVersionTooOld;
	protected boolean isUpdatingGuide;
	protected boolean isBuildingTaggedBroadcasts;
	protected boolean isFetchingFeedItems;
	protected boolean isGoingToMyChannelsFromSearch;
	protected Boolean isLocalDeviceCalendarOffSyncForInitialCall;

	protected int tvGuideInitialRetryCount = 0;
		
	
	
	public ContentManagerBase()
	{
		this.contentManagerCache = new CacheManager();
		
		this.apiClient = new APIClient(this);
		
		this.isLocalDeviceCalendarOffSyncForInitialCall = null;
	}
	

	
	protected APIClient getAPIClient() 
	{
		if(apiClient == null) 
		{
			Log.w(TAG, "!!! WARNING !!! APIClient in ContentManager is null, this should not be happening => reinitializing it");
			
			apiClient = new APIClient(this);
		}
		
		return apiClient;
	}
	
	
	/**
	 * This method is for internal use only
	 */
	protected Cache getCache()
	{
		if(contentManagerCache == null) 
		{
			Log.w(TAG, "ContentManagerCache Object is null => Reinitializing instance");
			
			contentManagerCache = new CacheManager();
		}
		
		return contentManagerCache.getCache();
	}
	
	
	
	public CacheManager getCacheManager()
	{
		if(contentManagerCache == null)
		{
			Log.w(TAG, "ContentManagerCache Object is null => Reinitializing instance");
			
			contentManagerCache = new CacheManager();
		}
		
		return contentManagerCache;
	}
	
	
	
	protected FetchDataProgressCallbackListener getFetchDataProgressCallbackListener()
	{
		return fetchDataProgressCallbackListener;
	}
	
	
	
	public void setFetchDataProgressCallbackListener(FetchDataProgressCallbackListener listener)
	{
		this.fetchDataProgressCallbackListener = listener;
	}
	
	
	
	public void setGoingToMyChannelsFromSearch(boolean isGoingToMyChannelsFromSearch) 
	{
		this.isGoingToMyChannelsFromSearch = isGoingToMyChannelsFromSearch;
	}
	
	
	
	public boolean isGoingToMyChannelsFromSearch() 
	{
		return isGoingToMyChannelsFromSearch;
	}
	
	
	
	public boolean isUpdatingGuide() 
	{
		return isUpdatingGuide;
	}
	
	
	
	public void resetTvGuideInitialRetryCount() 
	{
		tvGuideInitialRetryCount = 0;
	}


	
	public void setNewTVChannelIdsAndFetchGuide(ViewCallbackListener activityCallbackListener, ArrayList<TVChannelId> tvChannelIdsOnlyNewOnes, ArrayList<TVChannelId> tvChannelIdsAll)
	{
		/* The change of guide will only affect the selected day, getElseFetchFromServiceTVGuideUsingTVDate will check the cache, and the cache verifies that we have all
		 * the TVChannelGuides for the selected channels. */
		TVDate tvDate = getCacheManager().getTVDateSelected();
		
		/* Set the flag "updatingGuide" to true */
		isUpdatingGuide = true;
		
		/* Perform setting of TVChannelIds against backend, and fetch only the TVGuide for the new channels */
		apiClient.setNewTVChannelIdsAndFetchGuide(activityCallbackListener, tvDate, tvChannelIdsOnlyNewOnes, tvChannelIdsAll);
		
		/* Directly set the TVChannelIds in the Cache to directly update the UserProfileActivity GUI */
		getCache().setTvChannelIdsUser(tvChannelIdsAll);
	}
	
	
	
	/* This method does not require any ActivityCallbackListener, "fire and forget". */
	public void performInternalTracking(final TVBroadcastWithChannelInfo broadcast) 
	{
		if(broadcast != null && 
		   broadcast.getProgram() != null && 
		   broadcast.getProgram().getProgramId() != null && 
		   !TextUtils.isEmpty(broadcast.getProgram().getProgramId())) 
		{
			String tvProgramId = broadcast.getProgram().getProgramId();
			
			String deviceId = GenericUtils.getDeviceID();
			
			apiClient.performInternalTracking(null, tvProgramId, deviceId);
		}
	}
	
	
	
	
	public boolean isSelectedTVDateToday() 
	{
		boolean isToday = false;
		
		TVDate tvDateSelected = getCacheManager().getTVDateSelected();
		
		if(tvDateSelected != null)
		{
			isToday = tvDateSelected.isToday();
		}
		
		return isToday;
	
	}


	
	public void setReturnActivity(final Class<?> returnActivity) 
	{
		getCache().setReturnActivity(returnActivity);
	}
	
	
	
	/**
	 * This method tries to start the return activity stored in the cache, if null it does nothing and returns false
	 * else it starts the activity and sets it to null and returns true
	 * @param caller
	 * @return
	 */
	public boolean tryStartReturnActivity(final Activity caller) 
	{
		boolean returnActivityWasSet = getReturnActivity() != null;
		
		if(returnActivityWasSet) 
		{
			Intent intent = new Intent(caller, getReturnActivity());
			
			getCache().clearReturnActivity();
			
			caller.startActivity(intent);
		}
		
		return returnActivityWasSet;
	}

	
	
	public Class<?> getReturnActivity() 
	{
		Class<?> returnActivity = getCache().getReturnActivity();
		
		return returnActivity;
	}
	
	
	
	public void setLikeToAddAfterLogin(final UserLike userLikeToAdd) 
	{
		getCache().setLikeToAddAfterLogin(userLikeToAdd);
	}
	
	
	
	public boolean isLocalDeviceCalendarOffSync()
	{
		return isLocalDeviceCalendarOffSyncForInitialCall;
	}
	
	
	
	public void setLocalDeviceCalendarOffSync(boolean isLocalDeviceCalendarOffSyncForInitialCall)
	{
		this.isLocalDeviceCalendarOffSyncForInitialCall = isLocalDeviceCalendarOffSyncForInitialCall;
	}
	
	
	
	/* No handling when new year, just returns true, which means that the tutorial will not show. */
	public boolean checkIfUserOpenedAppLastTwoWeeks()
	{
		Calendar now = DateUtils.getNowWithGMTTimeZone();
		
		String date = getCache().getUserTutorialStatus().getDateUserLastOpendApp();
		
		Calendar lastTime = DateUtils.convertISO8601StringToCalendar(date);
		
		if (lastTime.before(now))
		{
			int a = now.get(Calendar.DAY_OF_YEAR);
			int b = lastTime.get(Calendar.DAY_OF_YEAR);
			
			int difference = a-b;
			
			if (difference > 13) 
			{
				return false;
			}
		}

		return true;
	}
	
	
	
	/**
	 * Filter out broadcast with a beginTime that has passed.
	 * 
	 * @param activityFeed
	 */
	protected static ArrayList<TVFeedItem> filterOldBroadcasts(ArrayList<TVFeedItem> activityFeed, ArrayList<TVFeedItem> contentAsArrayList)
	{
		if (activityFeed != null && !activityFeed.isEmpty())
		{	
			ArrayList<TVFeedItem> oldFeedItemsToDelete = new ArrayList<TVFeedItem>();
			
			if (contentAsArrayList == null) 
			{
				contentAsArrayList = activityFeed;
			}

			for (int i = 0; i < activityFeed.size(); i++) 
			{
				TVFeedItem item = activityFeed.get(i);

				if (item.getItemType() != FeedItemTypeEnum.POPULAR_BROADCASTS) 
				{
					TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo = item.getBroadcast();

					boolean isItemTooOld = checkIfTVFeedItemIsOld(tvBroadcastWithChannelInfo);

					/* If item is too old or if it has shown before the item will be removed */
					if (isItemTooOld) 
					{	
						Log.d(TAG, "MMM, Removing broadcast: " + tvBroadcastWithChannelInfo.getTitle() + " Starttime: "
								+ tvBroadcastWithChannelInfo.getBeginTime());
						
						oldFeedItemsToDelete.add(item);
					}
				}
			}
			
			if (oldFeedItemsToDelete != null && !oldFeedItemsToDelete.isEmpty())
			{
				contentAsArrayList.removeAll(oldFeedItemsToDelete);
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
	protected static boolean checkIfTVFeedItemIsOld(TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo)
	{
		boolean removeItem = false;
		
		Calendar now = DateUtils.getNowWithLocalTimezone();
		
		Long nowLong = now.getTimeInMillis();
		
		Long beginTime = tvBroadcastWithChannelInfo.getBeginTimeMillis();
		
		int difference = beginTime.compareTo(nowLong);
		
		/* beginTime is less than nowLong: Item should NOT be included in the feed activity. Items is old */
		if (difference < 0)
		{
			removeItem = true;
		}		
		
		return removeItem;
	}
}
