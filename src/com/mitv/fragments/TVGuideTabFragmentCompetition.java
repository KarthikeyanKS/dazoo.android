
package com.mitv.fragments;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
	@SuppressWarnings("unused")
	private static final String TAG = TVGuideTabFragmentCompetition.class.getName();
	
	
	private Competition competition;
	
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
	
	
	
	public TVGuideTabFragmentCompetition(Competition competition)
	{
		super(new Long(competition.getCompetitionId()).toString(), competition.getDisplayName(), TVGuideTabTypeEnum.COMPETITION);

		this.competition = competition;
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
            	TrackingManager.sharedInstance().sendUserCompetitionTabCalendarPressed(competition.getDisplayName());
            	
                Intent intent = new Intent(activity, CompetitionPageActivity.class);
                
                intent.putExtra(Constants.INTENT_COMPETITION_ID, competition.getCompetitionId());
                
                activity.startActivity(intent);
            }
        });
        
        RelativeLayout competitionCountDownAreaLayout = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_layout);
        
        competitionCountDownAreaLayout.setOnClickListener(new View.OnClickListener() 
        {	
            public void onClick(View v)
            {
                TrackingManager.sharedInstance().sendUserCompetitionTabCountdownPressed(competition.getDisplayName());
            }
        });
        
        RelativeLayout competitionBannerLayout = (RelativeLayout) rootView.findViewById(R.id.competition_banner_layout);
        
        competitionBannerLayout.setOnClickListener(new View.OnClickListener() 
        {	
            public void onClick(View v)
            {
                TrackingManager.sharedInstance().sendUserCompetitionTabCountdownPressed(competition.getDisplayName());
            }
        });
        
        initView();
		
		return rootView;
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
		updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		long competitionID = competition.getCompetitionId();
		
		return ContentManager.sharedInstance().getFromCacheHasCompetitionData(competitionID);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		// Do nothing
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
	
	
	
	private void setData()
	{
		if (competition.hasBegun())
		{
			title.setText(activity.getString(R.string.competition_page_time_left_title));
		
			competitionGoToScheduleLayout.setVisibility(View.VISIBLE);
			
			countDownAreaContainer.setVisibility(View.GONE);
		}
		else
		{
			title.setText(activity.getString(R.string.competition_page_time_left_title));
			
			competitionGoToScheduleLayout.setVisibility(View.VISIBLE);
		
			String competitionName = competition.getDisplayName();
			
			long eventStartTimeInMiliseconds = competition.getBeginTimeCalendarLocal().getTimeInMillis();
				
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
