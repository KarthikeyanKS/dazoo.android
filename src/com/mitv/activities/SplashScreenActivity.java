
package com.mitv.activities;



import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.utilities.NetworkUtils;



public class SplashScreenActivity 
	extends Activity 
	implements ActivityCallbackListener, FetchDataProgressCallbackListener
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
	}

	
	
	@Override
	protected void onResume() 
	{
		super.onResume();		
		
		if(Constants.USE_HOCKEY_APP_CRASH_REPORTS)
		{
			checkForCrashes();
		}
		
		if(Constants.USE_HOCKEY_APP_UPDATE_NOTIFICATIONS)
		{
			checkForUpdates();
		}
		
		boolean isConnected = NetworkUtils.isConnected();
		
		if(isConnected)
		{
			loadData();
		}
		else 
		{
			updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
		}
	}
	

	
	// Do not use this in Google Play builds
	private void checkForCrashes() 
	{
		CrashManager.register(this, Constants.HOCKEY_APP_TOKEN);
	}

	
	
	// Do not use this in Google Play builds
	private void checkForUpdates() 
	{
		UpdateManager.register(this, Constants.HOCKEY_APP_TOKEN);
	}

	
	
	private void startPrimaryActivity() 
	{
		Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
		
		startActivity(intent);
		
		finish();
	}


	
	@Override
	public void onFetchDataProgress(int totalSteps, String message) 
	{
		fetchedDataCount++;
		
		StringBuilder sb = new StringBuilder();
		sb.append(fetchedDataCount + "/" + totalSteps);
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
		switch (fetchRequestResult) 
		{
			case SUCCESS: 
			{
				updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
				break;
			}
			
			case API_VERSION_TOO_OLD: 
			{
				updateUI(UIStatusEnum.FAILED_VALIDATION);
				break;
			}
			
			case INTERNET_CONNECTION_NOT_AVAILABLE:
			default:
			{
				updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
				break;
			}
		}
	}
	
	
	
	protected void updateUI(UIStatusEnum status)
	{
		switch (status) 
		{
			case SUCCEEDED_WITH_DATA: 
			{
				startPrimaryActivity();
				break;
			}
			
			case FAILED_VALIDATION:
			{
				DialogHelper.showMandatoryAppUpdateDialog(this);
				break;
			}
			
			case NO_CONNECTION_AVAILABLE:
			default:
			{
				DialogHelper.showMandatoryFirstTimeInternetConnection(this);
				break;
			}
		}
	}
}

