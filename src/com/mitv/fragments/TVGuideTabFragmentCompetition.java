
package com.mitv.fragments;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.competition.CompetitionPageActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.TVGuideTabTypeEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingManager;
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
	
	private EventCountDownTimer eventCountDownTimer;
	
	private RelativeLayout countDownAreaContainer;
	private TextView remainingTimeInDays;
	private TextView remainingTimeInHours;
	private TextView remainingTimeInMinutes;
	private TextView remainingTimeInDaysTitle;
	private TextView remainingTimeInHoursTitle;
	private TextView remainingTimeInMinutesTitle;
	private TextView title;
	private RelativeLayout competitionGoToScheduleLayout;
	
	
	
	
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
		rootView = inflater.inflate(R.layout.layout_competition_page_main, null);

		super.initRequestCallbackLayouts(rootView);
		
		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		RelativeLayout learnMoreButton = (RelativeLayout) rootView.findViewById(R.id.competition_go_to_schedule_layout);
		
        learnMoreButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
            	TrackingManager.sharedInstance().sendUserCompetitionTabCalendarPressed(getCompetition().getDisplayName());
            	
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
                TrackingManager.sharedInstance().sendUserCompetitionTabCountdownPressed(getCompetition().getDisplayName());
            }
        });
        
        RelativeLayout competitionBannerLayout = (RelativeLayout) rootView.findViewById(R.id.competition_banner_layout);
        
        competitionBannerLayout.setOnClickListener(new View.OnClickListener() 
        {	
            public void onClick(View v)
            {
                TrackingManager.sharedInstance().sendUserCompetitionTabCountdownPressed(getCompetition().getDisplayName());
            }
        });
        
        if (savedInstanceState != null) 
        {
            // Restore last state for checked position.
        	competitionID = savedInstanceState.getLong(Constants.INTENT_COMPETITION_ID, 0);
        }
        
        initView();
		
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
		
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionsData(this, false);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasCompetitionData(competitionID);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
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
				break;
			}
			
			default:
			{
				// Do nothing
				break;
			}
		}
	}
	
	
	
	@Override
	public void onTimeChange(int hour){}
	
	
	
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
		countDownAreaContainer = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_area);
		
		remainingTimeInDays = (TextView) rootView.findViewById(R.id.competition_days_number);
		remainingTimeInHours = (TextView) rootView.findViewById(R.id.competition_hours_number);
		remainingTimeInMinutes = (TextView) rootView.findViewById(R.id.competition_minutes_number);
		
		remainingTimeInDaysTitle = (TextView) rootView.findViewById(R.id.competition_days_title);
		remainingTimeInHoursTitle = (TextView) rootView.findViewById(R.id.competition_hours_title);
		remainingTimeInMinutesTitle = (TextView) rootView.findViewById(R.id.competition_minutes_title);
		
		title = (TextView) rootView.findViewById(R.id.competition_title);
		
		competitionGoToScheduleLayout = (RelativeLayout) rootView.findViewById(R.id.competition_go_to_schedule_layout);	
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
		if (getCompetition().hasBegun())
		{
			title.setText(activity.getString(R.string.competition_page_time_left_title));
		
			competitionGoToScheduleLayout.setVisibility(View.VISIBLE);
			
			countDownAreaContainer.setVisibility(View.GONE);
		}
		else
		{
			title.setText(activity.getString(R.string.competition_page_time_left_title));
			
			competitionGoToScheduleLayout.setVisibility(View.VISIBLE);
		
			String competitionName = getCompetition().getDisplayName();
			
			long eventStartTimeInMiliseconds = getCompetition().getBeginTimeCalendarGMT().getTimeInMillis();
				
			long millisecondsUntilEventStart = (eventStartTimeInMiliseconds - DateUtils.getNow().getTimeInMillis());

			eventCountDownTimer = new EventCountDownTimer(
					competitionName, 
					millisecondsUntilEventStart, 
					remainingTimeInDays, 
					remainingTimeInHours, 
					remainingTimeInMinutes,
					remainingTimeInDaysTitle,
					remainingTimeInHoursTitle,
					remainingTimeInMinutesTitle);
				
			eventCountDownTimer.start();
		}
	}
}
