
package com.mitv.activities;



import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.GATrackingManager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.listadapters.TutorialScreenSlidePagerAdapter;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.NetworkUtils;
import com.viewpagerindicator.CirclePageIndicator;



public class SplashScreenActivity 
	extends FragmentActivity 
	implements ViewCallbackListener, FetchDataProgressCallbackListener, OnClickListener
{	
	
	private static final String TAG = SplashScreenActivity.class.getName();
	
	
	private FontTextView progressTextView;
	private int fetchedDataCount = 0;
	
	private boolean hasUserSeenTutorial;
	private boolean isViewingTutorial;
	private boolean isDataFetched;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
		
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		isDataFetched = false;
		
		if(Constants.ENABLE_FIRST_TIME_TUTORIAL_VIEW) 
		{
			boolean hasUserSeenTutorial = SecondScreenApplication.sharedInstance().hasUserSeenTutorial();
			
			if (hasUserSeenTutorial)
			{
				showSplashScreen();
			}
			else 
			{
				showUserTutorial();
			}
		} 
		else 
		{
			showSplashScreen();
		}
				
		GATrackingManager.reportActivityStart(this);
	}

	@Override
	protected void onStop() {
	    super.onStop();
		GATrackingManager.reportActivityStop(this);
	}
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
				
		boolean isConnected = NetworkUtils.isConnected();
		
		isViewingTutorial = SecondScreenApplication.sharedInstance().getIsViewingTutorial();
		
		if(isConnected)
		{	
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
		if (!isViewingTutorial) 
		{
			fetchedDataCount++;
			
			StringBuilder sb = new StringBuilder();
			sb.append(fetchedDataCount);
			sb.append("/");
			sb.append(totalSteps);
			sb.append(" - ");
			sb.append(message);
			
			if (progressTextView != null) 
			{
				progressTextView.setText(sb.toString());
			}
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
					
					GATrackingManager.sharedInstance().sendTimeOffSyncEvent();
				}
				
				isDataFetched = true;
				
				if (!isViewingTutorial) 
				{
					startPrimaryActivity();
				}
				break;
			}
		}
	}
	
	

	private void startPrimaryActivity() 
	{
		if(SecondScreenApplication.isAppRestarting()) 
		{
			Log.d(TAG, "isAppRestarting is true => setting to false");
			SecondScreenApplication.setAppIsRestarting(false);
		}
		
		String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		
		SecondScreenApplication.sharedInstance().setDateUserLastOpenedApp(date);
		
		Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
		
		startActivity(intent);
		
		finish();
	}
	
	
	
	private void showSplashScreen() 
	{		
		setContentView(R.layout.layout_splash_screen_activity);
		
		progressTextView = (FontTextView) findViewById(R.id.splash_screen_activity_progress_text);
		
		if (isDataFetched) 
		{
			startPrimaryActivity();
		}
	}
	
	
	
	private void showUserTutorial() 
	{
		SecondScreenApplication.sharedInstance().setIsViewingTutorial(true);
		
		setContentView(R.layout.user_tutorial_screen_slide);

		initTutorialView();
	}
	
	

	private void initTutorialView() 
	{
		mPager = (ViewPager) findViewById(R.id.pager);
		
		mPagerAdapter = new TutorialScreenSlidePagerAdapter(getSupportFragmentManager());
		
		mPager.setAdapter(mPagerAdapter);
		
		/* ViewPageIndicator library */
		CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		
		titleIndicator.setViewPager(mPager);
	}
	
	
	
	@Override
	public void onBackPressed() 
	{
		if(mPager != null)
		{
			if (mPager.getCurrentItem() == 0) 
			{
				super.onBackPressed();
			} 
			else 
			{
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			}
		}
		else
		{
			super.onBackPressed();
		}
	}

	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();

		switch (id) 
		{
			case R.id.button_splash_tutorial:
			case R.id.button_tutorial_next: 
			{
				mPager.setCurrentItem(mPager.getCurrentItem() + 1);
				break;
			}
			
			case R.id.button_tutorial_skip:
			case R.id.button_tutorial_start_primary_activity: {
				if (isDataFetched) {
					
					if (hasUserSeenTutorial && isViewingTutorial) {
						SecondScreenApplication.sharedInstance().setTutorialToNeverShowAgain();
					}
					
					SecondScreenApplication.sharedInstance().setUserSeenTutorial();
					
					SecondScreenApplication.sharedInstance().setIsViewingTutorial(false);
					
					startPrimaryActivity();
				}
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled onClick action.");
			}
		}
	}
	
}