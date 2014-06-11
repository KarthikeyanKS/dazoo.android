
package com.mitv.fragments;



import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.competition.CompetitionPageActivity;
import com.mitv.adapters.list.TVGuideCompetitionTagListAdapter;
import com.mitv.adapters.list.TVGuideListAdapter;
import com.mitv.asynctasks.other.RemoveAlreadyEndedBroadcastsTask;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.TVGuideTabTypeEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.ui.elements.EventCountDownTimer;
import com.mitv.utilities.DateUtils;



public class TVGuideTabFragmentCompetition
	extends TVGuideTabFragment
	implements OnPageChangeListener
{
	private static final String TAG = TVGuideTabFragmentCompetition.class.getName();
	
	
	private Competition competition;
	private long competitionID;
	private LinearLayout listView;
	private TVGuideListAdapter listAdapter;
	private ArrayList<TVBroadcastWithChannelInfo> taggedBroadcasts;
	private TVGuideCompetitionTagListAdapter tvTagListAdapter;
	private EventCountDownTimer eventCountDownTimer;
	private boolean isOngoing;
	private boolean hasEnded;
	
	private RelativeLayout countDownLayout;
	private RelativeLayout countDownAreaContainer;
	private TextView remainingTimeInDays;
	private TextView remainingTimeInHours;
	private TextView remainingTimeInMinutes;
	private TextView remainingTimeInDaysTitle;
	private TextView remainingTimeInHoursTitle;
	private TextView remainingTimeInMinutesTitle;
	private TextView title;
	private RelativeLayout competitionGoToScheduleLayout;
	
	private RelativeLayout competitionBannerLayout;
	
	
	
	/* An empty constructor is required by the Fragment Manager */
	public TVGuideTabFragmentCompetition()
	{
		super();
	}
	
	
	
	public TVGuideTabFragmentCompetition(long competitionID, String competitionDisplayName)
	{
		super(new Long(competitionID).toString(), competitionDisplayName, TVGuideTabTypeEnum.COMPETITION);

		this.competitionID = competitionID;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.layout_competition_tab, null);

		super.initRequestCallbackLayouts(rootView);
		
		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);
		
		registerAsListenerForRequest(RequestIdentifierEnum.TV_GUIDE_STANDALONE);
		
		RelativeLayout learnMoreButton = (RelativeLayout) rootView.findViewById(R.id.competition_go_to_schedule_layout);
		
		initView();
		
		isOngoing = false;
		
        learnMoreButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
            	TrackingGAManager.sharedInstance().sendUserCompetitionTabCalendarPressed(getCompetition().getDisplayName());
            	
                Intent intent = new Intent(activity, CompetitionPageActivity.class);
                
                intent.putExtra(Constants.INTENT_COMPETITION_ID, getCompetition().getCompetitionId());
                
                activity.startActivity(intent);
            }
        });
        
        RelativeLayout competitionCountDownAreaLayout = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_layout);
        
        competitionCountDownAreaLayout.setOnClickListener(new View.OnClickListener() 
        {	
            public void onClick(View v)
            {
            	TrackingGAManager.sharedInstance().sendUserCompetitionTabCountdownPressed(getCompetition().getDisplayName());
            }
        });
        
        competitionBannerLayout = (RelativeLayout) rootView.findViewById(R.id.competition_banner_layout);
        
        competitionBannerLayout.setOnClickListener(new View.OnClickListener() 
        {	
            public void onClick(View v)
            {
            	TrackingGAManager.sharedInstance().sendUserCompetitionTabCountdownPressed(getCompetition().getDisplayName());
            }
        });
        
        if (savedInstanceState != null) 
        {
            // Restore last state for checked position.
        	competitionID = savedInstanceState.getLong(Constants.INTENT_COMPETITION_ID, 0);
        }
		
		return rootView;
	}
	
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
        
        outState.putLong(Constants.INTENT_COMPETITION_ID, competitionID);
    }
	
	
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		if(eventCountDownTimer != null)
		{
			eventCountDownTimer.cancel();
		}
	}
	
	
	
	@Override
	protected void loadData()
	{
		updateUI(UIStatusEnum.LOADING);
		
		String loadingString = getString(R.string.competition_tab_loading_text);
		
		setLoadingLayoutDetailsMessage(loadingString);
		
		taggedBroadcasts = null;
		
		int reloadInterval = ContentManager.sharedInstance().getFromCacheAppConfiguration().getCompetitionEventPageReloadInterval();

		boolean forceRefresh = wasActivityDataUpdatedMoreThan(reloadInterval);
		
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, forceRefresh, getCompetition().getCompetitionId());
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasEnoughData = ContentManager.sharedInstance().getFromCacheHasCompetitionData(competitionID);
		
		return hasEnoughData;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			Competition currentCompetition = getCompetition();
			
			boolean hasBegun = currentCompetition.hasBegun();
			hasEnded = currentCompetition.hasEnded();
			boolean isVisible = currentCompetition.isVisible();
			
			isOngoing = hasBegun && !hasEnded && isVisible;
			
			boolean noContent = true;
			
			HashMap<String, ArrayList<TVBroadcastWithChannelInfo>> taggedBroadcastForDay = ContentManager.sharedInstance().getFromCacheTaggedBroadcastsForSelectedTVDate();
				
			if(taggedBroadcastForDay != null) 
			{
				/* Just filtering by tag: FIFA
				 * Not: Mundial FIFA, does not work */
				taggedBroadcasts = taggedBroadcastForDay.get(Constants.FIFA_TAG_ID);
	
				if(taggedBroadcasts != null && !taggedBroadcasts.isEmpty()) 
				{
					noContent = false;
				}
			}
			
			if(noContent) 
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
			} 
			else 
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
			}
		}
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{
			case SUCCESS_WITH_CONTENT:
			{
				setData();
				
				clearContentOnTagAndReload();
				
				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	
	
	/* Tab elements */
	
	public interface OnViewPagerIndexChangedListener 
	{
		public void onIndexSelected(int position);
	}
	

	
	@Override
	public void onPageSelected(int pos) {}

	
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	
	
	@Override
	public void onPageScrollStateChanged(int arg0) {}
	
	
	
	private void initView()
	{
		countDownLayout = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_layout);
				
		countDownAreaContainer = (RelativeLayout) rootView.findViewById(R.id.time_left_section);
		
		remainingTimeInDays = (TextView) rootView.findViewById(R.id.competition_days_number);
		remainingTimeInHours = (TextView) rootView.findViewById(R.id.competition_hours_number);
		remainingTimeInMinutes = (TextView) rootView.findViewById(R.id.competition_minutes_number);
		
		remainingTimeInDaysTitle = (TextView) rootView.findViewById(R.id.competition_days_title);
		remainingTimeInHoursTitle = (TextView) rootView.findViewById(R.id.competition_hours_title);
		remainingTimeInMinutesTitle = (TextView) rootView.findViewById(R.id.competition_minutes_title);
		
		title = (TextView) rootView.findViewById(R.id.competition_title);
		
		competitionGoToScheduleLayout = (RelativeLayout) rootView.findViewById(R.id.competition_go_to_schedule_layout);
		
		listView =  (LinearLayout) rootView.findViewById(R.id.competition_tag_list_events);
	}
	
	
	
	private Competition getCompetition()
	{
		if(this.competition == null)
		{
			Log.d(TAG, "Competition ID is: " + competitionID);
			
			this.competition = ContentManager.sharedInstance().getFromCacheCompetitionByID(competitionID);
		}
		
		return competition;
	}
	
	
	
	private void setData()
	{		
		if (isOngoing)
		{
			title.setText("");
		
			LayoutParams parameters = countDownLayout.getLayoutParams();
			
			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
			
			parameters.height = height;
			
			countDownLayout.setLayoutParams(parameters);
			
			countDownLayout.setVisibility(View.VISIBLE);
			
			competitionGoToScheduleLayout.setVisibility(View.VISIBLE);
			
			countDownAreaContainer.setVisibility(View.GONE);
		}
		
		else if (hasEnded) 
		{
			// TODO ?
		}
		else
		{
			title.setText(activity.getString(R.string.competition_page_time_left_title));
		
			LayoutParams parameters = countDownLayout.getLayoutParams();
			
			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, getResources().getDisplayMetrics());
			
			parameters.height = height;
			
			countDownLayout.setLayoutParams(parameters);
			
			countDownLayout.setVisibility(View.VISIBLE);
			
			competitionGoToScheduleLayout.setVisibility(View.VISIBLE);
			
			countDownAreaContainer.setVisibility(View.VISIBLE);
			
			competitionBannerLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
		
			String competitionName = getCompetition().getDisplayName();
			
			long eventStartTimeInMiliseconds = getCompetition().getBeginTimeCalendarGMT().getTimeInMillis();
				
			long millisecondsUntilEventStart = (eventStartTimeInMiliseconds - DateUtils.getNowWithGMTTimeZone().getTimeInMillis());

			eventCountDownTimer = new EventCountDownTimer(
					competitionName, 
					millisecondsUntilEventStart, 
					remainingTimeInDays, 
					remainingTimeInHours, 
					remainingTimeInMinutes,
					remainingTimeInDaysTitle,
					remainingTimeInHoursTitle,
					remainingTimeInMinutesTitle,
					title,
					countDownLayout);
				
			eventCountDownTimer.start();
		}
	}
	
	
	
	@Override
	public void onTimeChange(int hour)
	{
		if (listAdapter != null) 
		{
			listAdapter.refreshList(hour);
		}
	}
	
	
	
	protected void clearContentOnTagAndReload() 
	{
		int startIndex = TVBroadcastWithChannelInfo.getClosestBroadcastIndex(taggedBroadcasts, 0);

		RemoveAlreadyEndedBroadcastsTask removeAlreadyEndedBroadcastsTask = new RemoveAlreadyEndedBroadcastsTask(taggedBroadcasts, startIndex);
		removeAlreadyEndedBroadcastsTask.run();
		
		listView.removeAllViews();
		
		tvTagListAdapter = new TVGuideCompetitionTagListAdapter(activity, Constants.FIFA_TAG_ID, taggedBroadcasts, startIndex);
			
		for (int i = 0; i < tvTagListAdapter.getCount(); i++) 
		{
            View listItem = tvTagListAdapter.getView(i, null, listView);
           
            if (listItem != null) 
            {
            	listView.addView(listItem);
            }
        }
	}
	
}
