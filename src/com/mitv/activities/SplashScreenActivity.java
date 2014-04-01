
package com.mitv.activities;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.analytics.tracking.android.EasyTracker;
import com.mitv.ContentManager;
import com.mitv.GATrackingManager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.fragments.UserTutorialFragment;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.NetworkUtils;



public class SplashScreenActivity 
	extends FragmentActivity 
	implements ViewCallbackListener, FetchDataProgressCallbackListener, OnClickListener
{	
	@SuppressWarnings("unused")
	private static final String TAG = SplashScreenActivity.class.getName();
	
	
	private FontTextView progressTextView;
	private int fetchedDataCount = 0;
	
	boolean hasUserSeenTutorial;
	boolean isViewingTutorial = false;
	boolean isDataFetched = false;
	
	private static final int NUM_PAGES = 5;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		hasUserSeenTutorial = SecondScreenApplication.sharedInstance().hasUserSeenTutorial();
		
		if (hasUserSeenTutorial) {
			showSplashScreen();
			
		} else {
			showUserTutorial();
		}
		
		/* Google Analytics Tracking */
		EasyTracker.getInstance(this).activityStart(this);
		
		String className = this.getClass().getName();
		
		GATrackingManager.sendView(className);
	}

	
	
	@Override
	protected void onResume() 
	{
		super.onResume();		
				
		boolean isConnected = NetworkUtils.isConnected();
		
		if(isConnected)
		{
			GATrackingManager.sharedInstance().sendUserNetworkTypeEvent();
			
			loadData();
		}
		else 
		{
			updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
		}
	}
		

	
	@Override
	public void onFetchDataProgress(int totalSteps, String message) 
	{
		if (!isViewingTutorial) {
			fetchedDataCount++;
			
			StringBuilder sb = new StringBuilder();
			sb.append(fetchedDataCount);
			sb.append("/");
			sb.append(totalSteps);
			sb.append(" - ");
			sb.append(message);
			
			progressTextView.setText(sb.toString());
		}
	}



	protected void loadData()
	{
		ContentManager.sharedInstance().fetchFromServiceInitialCall(this, this);
	}



	@Override
	public final void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		ContentManager.sharedInstance().unregisterListenerFromAllRequests(this);
		
		switch (fetchRequestResult) 
		{
			case API_VERSION_TOO_OLD: 
			{
				updateUI(UIStatusEnum.API_VERSION_TOO_OLD);
				break;
			}
			
			default:
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
				break;
			}
		}
	}
	
	
	
	protected void updateUI(UIStatusEnum status)
	{
		switch (status)
		{
			case API_VERSION_TOO_OLD:
			{
				DialogHelper.showMandatoryAppUpdateDialog(this);
				break;
			}
			
			default:
			{
				boolean isLocalDeviceCalendarOffSync = ContentManager.sharedInstance().isLocalDeviceCalendarOffSync();
				
				if(isLocalDeviceCalendarOffSync)
				{
					String message = getString(R.string.review_date_time_settings);

					ToastHelper.createAndShowLongToast(message);
				}
				
				isDataFetched = true;
				
				if (!isViewingTutorial) {
					startPrimaryActivity();
				}
				break;
			}
		}
	}
	
	
	
	private void startPrimaryActivity() 
	{
		if(SecondScreenApplication.isAppRestarting()) {
			Log.d(TAG, "isAppRestarting is true => setting to false");
			SecondScreenApplication.setAppIsRestarting(false);
		}
		
		
		Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
		
		startActivity(intent);
		
		finish();
	}
	
	
	
	private void showSplashScreen() {
		isViewingTutorial = false;
		
		setContentView(R.layout.layout_splash_screen_activity);
		
		progressTextView = (FontTextView) findViewById(R.id.splash_screen_activity_progress_text);
		
		if (isDataFetched) {
			startPrimaryActivity();
		}
	}
	
	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			super.onBackPressed();

		} else {
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}
	
	private void showUserTutorial() {
		isViewingTutorial = true;
		
		setContentView(R.layout.user_tutorial_screen_slide);

		initView();
	}
	
	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return new UserTutorialFragment(position);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

	}	

	private void initView() {
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);	
	}
	

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		
			case R.id.button_splash_tutorial:
			case R.id.button_tutorial_next: {
				mPager.setCurrentItem(mPager.getCurrentItem() + 1);
				break;
			}
			
			case R.id.button_tutorial_skip:
			case R.id.button_tutorial_start_primary_activity: {
				if (isDataFetched) {
					isViewingTutorial = false;
					SecondScreenApplication.sharedInstance().setUserSeenTutorial();
					startPrimaryActivity();
				}
				break;
			}
		}
	}
	
}