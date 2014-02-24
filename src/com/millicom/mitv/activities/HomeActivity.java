package com.millicom.mitv.activities;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.fragments.TVHolderFragment;
import com.millicom.mitv.fragments.TVHolderFragment.OnViewPagerIndexChangedListener;
import com.mitv.R;
import com.mitv.utilities.OldDateUtilities;



public class HomeActivity 
	extends TVDateSelectionActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = HomeActivity.class.getName();

	
	
	private Fragment activeFragment;
	private int selectedTagIndex = 0;
	private String welcomeMessage = "";
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

		ContentManager.sharedInstance().setSelectedHour(Integer.valueOf(OldDateUtilities.getCurrentHourString()));

		initViews();

		tryShowWelcomeToast();
		
		/* HOCKEY-APP */
		// checkForUpdates();
	}

	
	
	private void tryShowWelcomeToast() 
	{
		if (!hasShowWelcomeToast) 
		{
			welcomeMessage = ContentManager.sharedInstance().getFromStorageWelcomeMessage();

			if (welcomeMessage != null && !TextUtils.isEmpty(welcomeMessage)) 
			{
				Toast.makeText(getApplicationContext(), welcomeMessage, Toast.LENGTH_LONG).show();
			}

			hasShowWelcomeToast = true;
		}
	}
	
	
	@Override
	protected void attachFragment() 
	{
		activeFragment = TVHolderFragment.newInstance(selectedTagIndex, new OnViewPagerIndexChangedListener() 
		{
			@Override
			public void onIndexSelected(int position) 
			{
				selectedTagIndex = position;
			}
		});
		
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, activeFragment, null).commitAllowingStateLoss();
	}
	

	
	@Override
	protected void removeActiveFragment() 
	{
		try 
		{	
			if (activeFragment != null) {
				getSupportFragmentManager().beginTransaction().remove(activeFragment).commitAllowingStateLoss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void initViews() 
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
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult) 
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
				attachFragment();
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
