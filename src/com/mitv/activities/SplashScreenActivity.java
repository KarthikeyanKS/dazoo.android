
package com.mitv.activities;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.pager.TutorialScreenSlidePagerAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.managers.TrackingManager;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;
import com.viewpagerindicator.CirclePageIndicator;



public class SplashScreenActivity 
	extends FragmentActivity 
	implements ViewCallbackListener, FetchDataProgressCallbackListener, OnClickListener
{	
	private static final String TAG = SplashScreenActivity.class.getName();
	
	
	private static final int PAGE1 = 0;
	private static final int PAGE2 = 1;
	private static final int PAGE3 = 2;
	private static final int PAGE4 = 3;
	private static final int PAGE5 = 4;
	
	
	private FontTextView progressTextView;
	private int fetchedDataCount = 0;
	
	private boolean isViewingTutorial;
	private boolean isDataFetched;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

	private RelativeLayout skipButtonContainer;
	private RelativeLayout startPrimaryActivityContainer;
	private ProgressBar skipButtonProgressBar;
	private ProgressBar startPrimaryButtonProgressBar;
	
	private TextView splash_button;
	private TextView next_button;
	
	private CirclePageIndicator titleIndicator;
	
	private boolean failedLoading = false;
	
	
	
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
				
		TrackingGAManager.reportActivityStart(this);
		TrackingGAManager.sharedInstance().sendUserNetworkTypeEvent();
	}

	
	
	@Override
	protected void onStop() 
	{
	    super.onStop();
		
	    TrackingGAManager.reportActivityStop(this);
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		if (Constants.USE_INITIAL_METRICS_ANALTYTICS) 
		{
			TrackingManager.sharedInstance().sendTestMeasureInitialLoadingScreenStarted(this.getClass().getSimpleName());
		}
		
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
	protected void onDestroy()
	{
		super.onDestroy();
		
		ContentManager.sharedInstance().setFetchDataProgressCallbackListener(null);

		View view = getWindow().getDecorView().getRootView();
		
		GenericUtils.unbindDrawables(view);
	}

	
	
	@Override
	public void onFetchDataProgress(int totalSteps, String message) 
	{
		if(isViewingTutorial == false)
		{
			fetchedDataCount++;
			
			StringBuilder sb = new StringBuilder();
			sb.append(fetchedDataCount)
			.append("/")
			.append(totalSteps)
			.append(" - ")
			.append(message);
			
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
		if (Constants.USE_INITIAL_METRICS_ANALTYTICS) 
		{
			TrackingManager.sharedInstance().sendTestMeasureInitialLoadingScreenOnResultReached(this.getClass().getSimpleName());
		}
		
		ContentManager.sharedInstance().unregisterListenerFromAllRequests(this);
		
		switch (fetchRequestResult) 
		{
			case API_VERSION_TOO_OLD: 
			{
				updateUI(UIStatusEnum.API_VERSION_TOO_OLD);
				break;
			}
			
			case SUCCESS: 
			{
				updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
				break;
			}
			
			case UNKNOWN_ERROR:
			{
				// Load HomeActivity anyway if the initial loading failed. The no data layout will handle re-fetches.
				failedLoading = true;
				updateUI(UIStatusEnum.FAILED);
				break;
			}
			
			default:
			{
				//Do nothing
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
			
			case NO_CONNECTION_AVAILABLE:
			{				
				if (!isViewingTutorial) 
				{
					startPrimaryActivity();
				}
				break;
			}
			
			default:
			{
				boolean isLocalDeviceCalendarOffSync = ContentManager.sharedInstance().isLocalDeviceCalendarOffSync();
				
				if(isLocalDeviceCalendarOffSync)
				{
					String message = getString(R.string.review_date_time_settings);

					ToastHelper.createAndShowLongToast(message);
					
					TrackingGAManager.sharedInstance().sendTimeOffSyncEvent();
				}
				
				isDataFetched = true;
				
				if (!isViewingTutorial)
				{
					if (Constants.USE_INITIAL_METRICS_ANALTYTICS) 
					{
						TrackingManager.sharedInstance().sendTestMeasureInitialLoadingScreenEnded(this.getClass().getSimpleName());
					}
					
					startPrimaryActivity();
				}
				break;
			}
		}
	}
	
	

	private void startPrimaryActivity() 
	{
		ContentManager.sharedInstance().getCacheManager().setDateUserLastOpenApp();
		
		Intent currentIntent = getIntent();

		boolean isFromNofication = currentIntent.getBooleanExtra(Constants.INTENT_NOTIFICATION_EXTRA_IS_FROM_NOTIFICATION, false);
		
		Intent nextIntent;
		
		if(isFromNofication == false)
		{
			nextIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
		}
		else
		{
			Class<?> activityClass = null;
			
			String activityClassName = currentIntent.getStringExtra(Constants.INTENT_NOTIFICATION_ACTIVITY_CLASS_NAME);
			
			try 
			{
				activityClass = Class.forName(activityClassName);
			} 
			catch (ClassNotFoundException e) 
			{
				// Do nothing
			}
			
			if(activityClass != null)
			{
				nextIntent = new Intent(SplashScreenActivity.this, activityClass);
				
				Bundle currentBundle = currentIntent.getExtras();
				
				if(currentBundle != null)
				{
					nextIntent.putExtras(currentBundle);
				}
			}
			else
			{
				nextIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
			}
		}
		
		if (failedLoading == false) 
		{
			nextIntent.putExtra(Constants.INTENT_EXTRA_IS_FROM_SPLASHSCREEN, true);
		}
		
		startActivity(nextIntent);
		
		finish();
	}
	
	
	
	private void showSplashScreen() 
	{
		isViewingTutorial = false;
		
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
		
		setContentView(R.layout.layout_tutorial_screen);

		initTutorialView();
	}
	
	

	private void initTutorialView() 
	{
		mPager = (ViewPager) findViewById(R.id.pager);
		
		mPagerAdapter = new TutorialScreenSlidePagerAdapter(getSupportFragmentManager());
		
		mPager.setAdapter(mPagerAdapter);
		
		skipButtonContainer = (RelativeLayout) findViewById(R.id.skip_button_container);
		startPrimaryActivityContainer = (RelativeLayout) findViewById(R.id.start_primary_button_container);
		
		skipButtonProgressBar = (ProgressBar) findViewById(R.id.skip_button_progressbar);
		startPrimaryButtonProgressBar = (ProgressBar) findViewById(R.id.start_primary_button_progressbar);
		
		splash_button = (TextView) findViewById(R.id.button_splash_tutorial);
		next_button = (TextView) findViewById(R.id.button_tutorial_next);
		
		updateViewForSelectedPage();
		
		/* ViewPageIndicator circle */
		titleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		
		titleIndicator.setViewPager(mPager);
		
		mPager.setOnPageChangeListener(new OnPageChangeListener() 
		{
			@Override
			public void onPageScrollStateChanged(int arg0) {}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}

			@Override
			public void onPageSelected(int arg0) 
			{
				titleIndicator.setCurrentItem(mPager.getCurrentItem());
				updateViewForSelectedPage();
			}
		});
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
		int paddingInDP = 30;
		int leftpx = GenericUtils.convertDPToPixels(paddingInDP);
		
		paddingInDP = 50;
		int rightpx = GenericUtils.convertDPToPixels(paddingInDP);
		
		paddingInDP = 10;
		int topBottompx = GenericUtils.convertDPToPixels(paddingInDP);
		
		int viewID = v.getId();
		
		switch (viewID) 
		{
			case R.id.button_splash_tutorial:
			case R.id.button_tutorial_next: 
			{
				mPager.setCurrentItem(mPager.getCurrentItem() + 1);
				break;
			}
			
			case R.id.skip_button_container: 
			{
				skipButtonContainer.setPadding(leftpx, topBottompx, rightpx, topBottompx);
				skipButtonProgressBar.setVisibility(View.VISIBLE);
				
				boolean isConnected = NetworkUtils.isConnected();
				
				if (isConnected) {
					TrackingManager.sharedInstance().sendUserTutorialExitEvent(mPager.getCurrentItem());				
				}

				finishTutorial();
				
				break;
			}
			
			case R.id.start_primary_button_container: 
			{
				startPrimaryActivityContainer.setPadding(leftpx, topBottompx, rightpx, topBottompx);
				startPrimaryButtonProgressBar.setVisibility(View.VISIBLE);
				
				boolean isConnected = NetworkUtils.isConnected();
				
				if (isConnected) 
				{
					TrackingManager.sharedInstance().sendUserTutorialExitEvent(PAGE5);				
				}
				
				finishTutorial();
				
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled onClick action.");
			}
		}
	}
	
	
	
	private void finishTutorial() 
	{
		SecondScreenApplication.sharedInstance().setIsViewingTutorial(false);
		
		boolean isConnected = NetworkUtils.isConnected();
		
		if (isDataFetched) 
		{
			startPrimaryActivity();	
		} 
		else 
		{
			isViewingTutorial = false;
			
			if (!isConnected) 
			{
				startPrimaryActivity();
			}
		}
	}
	
	
	
	private void updateViewForSelectedPage() 
	{	
		switch (mPager.getCurrentItem()) 
		{
			case PAGE1: 
			{
				splash_button.setVisibility(View.VISIBLE);
				
				skipButtonContainer.setVisibility(View.GONE);
				startPrimaryActivityContainer.setVisibility(View.GONE);
				next_button.setVisibility(View.GONE);
				break;
			}
			
			case PAGE2:
			case PAGE3:
			case PAGE4: 
			{
				skipButtonContainer.setVisibility(View.VISIBLE);
				next_button.setVisibility(View.VISIBLE);
				
				splash_button.setVisibility(View.GONE);
				startPrimaryActivityContainer.setVisibility(View.GONE);
				break;
			}
			
			case PAGE5: 
			{
				startPrimaryActivityContainer.setVisibility(View.VISIBLE);
				
				splash_button.setVisibility(View.GONE);
				skipButtonContainer.setVisibility(View.GONE);
				next_button.setVisibility(View.GONE);
				break;
			}
			
			default: 
			{
				break;
			}
		}
	}
	
}