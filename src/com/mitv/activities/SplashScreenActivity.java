
package com.mitv.activities;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.mitv.ContentManager;
import com.mitv.GATrackingManager;
import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.NetworkUtils;



public class SplashScreenActivity 
	extends Activity 
	implements ViewCallbackListener, FetchDataProgressCallbackListener
{	
	@SuppressWarnings("unused")
	private static final String TAG = SplashScreenActivity.class.getName();
	
	
	private FontTextView progressTextView;
	private int fetchedDataCount = 0;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_splash_screen_activity);
		
		progressTextView = (FontTextView) findViewById(R.id.splash_screen_activity_progress_text);
		
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
		fetchedDataCount++;
		
		StringBuilder sb = new StringBuilder();
		sb.append(fetchedDataCount);
		sb.append("/");
		sb.append(totalSteps);
		sb.append(" - ");
		sb.append(message);
		
		progressTextView.setText(sb.toString());
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
				
				startPrimaryActivity();
				break;
			}
		}
	}
	
	
	
	private void startPrimaryActivity() 
	{
		Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
		
		startActivity(intent);
		
		finish();
	}
}