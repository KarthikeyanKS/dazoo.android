package com.mitv.activities;



import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.fragments.TVHolderFragment;
import com.mitv.fragments.TVHolderFragment.OnViewPagerIndexChangedListener;
import com.mitv.managers.RateAppManager;
import com.mitv.managers.ContentManager;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;



public class HomeActivity 
	extends TVDateSelectionActivity
{
	private static final String TAG = HomeActivity.class.getName();

	
	private LinearLayout fragmentContainer;
	
	private TVHolderFragment activeFragment;
	private int selectedTagIndex;
	private boolean hasShowWelcomeToast;


	
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
		
		initLayout();
		
		boolean isConnected = NetworkUtils.isConnected();
		
		if (isConnected) {
			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

			selectedTagIndex = 0;
			hasShowWelcomeToast = false;			
			
			registerAsListenerForRequest(RequestIdentifierEnum.TV_GUIDE_STANDALONE);
			
			RateAppManager.appLaunched(this);
			
		} else {
			updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
		}
	}
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
				
		showWelcomeToast();
		
		if(ContentManager.sharedInstance().isUpdatingGuide()) 
		{
			setGUIToLoading();
		}
	}	
	
	
	
	protected void onSaveInstanceState(Bundle bundle) 
	{
		  super.onSaveInstanceState(bundle);
	}
	
		
	
	private void showWelcomeToast() 
	{
		if (hasShowWelcomeToast == false) 
		{
			String message = ContentManager.sharedInstance().getFromCacheWelcomeMessage();

			if (!TextUtils.isEmpty(message)) 
			{
				ToastHelper.createAndShowShortToast(message);
			}

			hasShowWelcomeToast = true;
		}
	}
	
	
	
	@Override
	protected void attachFragment() 
	{
		if (GenericUtils.isActivityNotNullAndNotFinishingAndNotDestroyed(this)) 
		{
			FragmentManager fm = getSupportFragmentManager();
			
			if (activeFragment == null) 
			{
				activeFragment = TVHolderFragment.newInstance(selectedTagIndex, getOnViewPagerIndexChangedListener());

				FragmentTransaction fragmentTransaction = fm.beginTransaction().replace(R.id.fragment_container, activeFragment, null);
				
				try
				{
					fragmentTransaction.commitAllowingStateLoss();
				}
				catch(IllegalStateException ilstex)
				{
					Log.e(TAG, ilstex.getMessage());
				}
			} 
			else 
			{
				FragmentTransaction fragmentTransaction = fm.beginTransaction().attach(activeFragment);
						
				try
				{
					fragmentTransaction.commitAllowingStateLoss();
				}
				catch(IllegalStateException ilstex)
				{
					Log.e(TAG, ilstex.getMessage());
				}
			}
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
		
		fragmentContainer = (LinearLayout) findViewById(R.id.fragment_container);
	}
	
	

	@Override
	protected void loadData() 
	{
		setGUIToLoading();
		
		ContentManager.sharedInstance().getElseFetchFromServiceTVGuideUsingSelectedTVDate(this, false);
	}
	
	
	private void setGUIToLoading() 
	{
		updateUI(UIStatusEnum.LOADING);
		String loadingMessage = getString(R.string.loading_message_guide_new_day);
		setLoadingLayoutDetailsMessage(loadingMessage);
	}
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasEnoughDataToShowContent = ContentManager.sharedInstance().getFromCacheHasTVTagsAndGuideForSelectedTVDate();
		if(!hasEnoughDataToShowContent) {
			Log.w(TAG, "Not enough data to show content");
		}
		return hasEnoughDataToShowContent;
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
		super.updateUI(status);
			
		switch (status) 
		{
			case FAILED:
			case LOADING:
			{
				fragmentContainer.setVisibility(View.GONE);
				break;
			}
		
			case SUCCESS_WITH_CONTENT:
			{
				fragmentContainer.setVisibility(View.VISIBLE);
				attachFragment();
				break;
			}
			
			default:{/*Do nothing*/break;}
		}
	}
	
	
	
	private OnViewPagerIndexChangedListener getOnViewPagerIndexChangedListener()
	{
		OnViewPagerIndexChangedListener listener = new OnViewPagerIndexChangedListener() 
		{
			@Override
			public void onIndexSelected(int position) 
			{
				selectedTagIndex = position;
				
				boolean isConnected = NetworkUtils.isConnected();

				if (hasEnoughDataToShowContent() && isConnected == false) 
				{
					ToastHelper.createAndShowNoInternetConnectionToast();
				}
			}	
		};
		
		return listener;
	}
}
