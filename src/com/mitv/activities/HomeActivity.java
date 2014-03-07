package com.mitv.activities;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
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
	private static final String TAG = HomeActivity.class.getName();

	
	
	private Fragment activeFragment;
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
		
		int currentHour = DateUtils.getCurrentHourOn24HourFormat();
		
		ContentManager.sharedInstance().setSelectedHour(currentHour);
		
		showWelcomeToast();
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
			if (activeFragment != null) 
			{
				getSupportFragmentManager().beginTransaction().remove(activeFragment).commitAllowingStateLoss();
			}
		} 
		catch (Exception e) 
		{
			Log.e(TAG , e.getMessage(), e);
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