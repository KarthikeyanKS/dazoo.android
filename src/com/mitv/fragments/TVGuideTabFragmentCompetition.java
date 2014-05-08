
package com.mitv.fragments;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.ui.elements.EventCountDownTimer;
import com.mitv.utilities.GenericUtils;



public class TVGuideTabFragmentCompetition
	extends TVGuideTabFragment
	implements OnPageChangeListener
{
	private static final String TAG = TVGuideTabFragmentCompetition.class.getName();
	
	
	private Competition competition;
	private Event event;
	
	private EventCountDownTimer eventCountDownTimer;
	
	private RelativeLayout countDownAreaContainer;
	private RelativeLayout countDownAreaDays;
	private RelativeLayout countDownAreaHours;
	private RelativeLayout countDownAreaMinutes;
	private TextView timeLeft;
	private ImageView backgroundImg;
	
	
	
	
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
		
		registerAsListenerForRequest(RequestIdentifierEnum.COMPETITION_INITIAL_DATA);

		// Important: Reset the activity whenever the view is recreated
		activity = getActivity();
		
		Button button = (Button) rootView.findViewById(R.id.competition_button);
		
        button.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
                Intent intent = new Intent(activity, CompetitionPageActivity.class);
                
                intent.putExtra(Constants.INTENT_COMPETITION_ID, competition.getCompetitionId());
                
                startActivity(intent);
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
		updateUI(UIStatusEnum.LOADING);

		String loadingMessage = String.format(GenericUtils.getCurrentLocale(activity), "%s %s", getString(R.string.loading_message_tag), getTabTitle());
		
		setLoadingLayoutDetailsMessage(loadingMessage);
		
		long competitionID = competition.getCompetitionId();
		
		ContentManager.sharedInstance().getElseFetchFromServiceCompetitionInitialData(this, false, competitionID);
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
		if(fetchRequestResult.wasSuccessful())
		{
			event = ContentManager.sharedInstance().getFromCacheNextUpcomingEventForSelectedCompetition();

			if(event == null)
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
	public void onTimeChange(int hour)
	{}
	
	
	
	/* Tab elements */
	
	public interface OnViewPagerIndexChangedListener 
	{
		public void onIndexSelected(int position);
	}
	

	
	@Override
	public void onPageSelected(int pos) 
	{
		
	}

	
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2){}

	
	
	@Override
	public void onPageScrollStateChanged(int arg0){}
	
	
	
	private void initView()
	{
		countDownAreaContainer = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_area);
		countDownAreaDays = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_area);
		countDownAreaHours = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_area);
		countDownAreaMinutes = (RelativeLayout) rootView.findViewById(R.id.competition_count_down_area);
		
		if (event != null) 
		{
			
		}
	}
	
}
