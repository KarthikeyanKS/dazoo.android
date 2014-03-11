package com.mitv.activities;



import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.fragments.TVHolderFragment;
import com.mitv.fragments.TVHolderFragment.OnViewPagerIndexChangedListener;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.DateUtils;



public class HomeActivity 
	extends TVDateSelectionActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = HomeActivity.class.getName();

	
	private TVHolderFragment activeFragment;
	private int selectedTagIndex = 0;
	private boolean hasShowWelcomeToast = false;


	
	@Override
	protected void setActivityCallbackListener()
	{
		activityCallbackListener = this;
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_home_activity);

		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		initLayout();
	}
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if(Constants.USE_HOCKEY_APP_CRASH_REPORTS)
		{
			hockeyAppCheckForCrashes();
		}
		
		if(Constants.USE_HOCKEY_APP_UPDATE_NOTIFICATIONS)
		{
			hockeyAppCheckForUpdates();
		}
		
		int currentHour = DateUtils.getCurrentHourOn24HourFormat();
		
		ContentManager.sharedInstance().setSelectedHour(currentHour);
		
		showWelcomeToast();
		
		if(ContentManager.sharedInstance().isUpdatingGuide()) {
			updateUI(UIStatusEnum.LOADING);
		}
	}
	
	
	
	/* Do not use this in Google Play builds */
	private void hockeyAppCheckForCrashes() 
	{
		CrashManager.register(this, Constants.HOCKEY_APP_TOKEN);
	}

	
	
	/* Do not use this in Google Play builds */
	private void hockeyAppCheckForUpdates() 
	{
		UpdateManager.register(this, Constants.HOCKEY_APP_TOKEN);
	}
	
	
	
	private void showWelcomeToast() 
	{
		if (!hasShowWelcomeToast) 
		{
			String welcomeMessage = ContentManager.sharedInstance().getFromCacheWelcomeMessage();

			if (!TextUtils.isEmpty(welcomeMessage)) 
			{
				ToastHelper.createAndShowToast(this, welcomeMessage);
			}

			hasShowWelcomeToast = true;
		}
	}
	
	
	
	@Override
	protected void attachFragment() 
	{
		FragmentManager fm = getSupportFragmentManager();
		
		if(activeFragment == null)
		{
			activeFragment = TVHolderFragment.newInstance(selectedTagIndex, new OnViewPagerIndexChangedListener() 
			{
				@Override
				public void onIndexSelected(int position) 
				{
					selectedTagIndex = position;
				}
			});
			
			fm.beginTransaction().replace(R.id.fragment_container, activeFragment, null).commit();
		}
		else
		{
			fm.beginTransaction().attach(activeFragment).commit();
		}
	}
	

	
	@Override
	protected void removeActiveFragment() 
	{
		if (activeFragment != null) 
		{
			getSupportFragmentManager().beginTransaction().remove(activeFragment).commitAllowingStateLoss();

			activeFragment = null;
		}
	}

	
	
	private void initLayout() 
	{
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
	}
	
	

	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceTVGuideUsingSelectedTVDate(this, false);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return ContentManager.sharedInstance().getFromCacheHasTVGuideForSelectedTVDate();
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if(fetchRequestResult.wasSuccessful())
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
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
			case SUCCEEDED_WITH_DATA:
			{
				showDaySelection();
				attachFragment();
				break;
			}
			
			case NO_CONNECTION_AVAILABLE:
			{
				hideDaySelection();
				break;
			}
	
			default:
			{
				// Do nothing
				break;
			}
		}
	}
}
