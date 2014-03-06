
package com.mitv.activities;



import net.hockeyapp.android.CrashManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.utilities.NetworkUtils;



public class SplashScreenActivity 
	extends ActionBarActivity 
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
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		
		boolean isConnected = NetworkUtils.isConnected();
		
		if(isConnected)
		{
			ContentManager.sharedInstance().fetchFromServiceInitialCall(this, this);
		}
		else
		{
			DialogHelper.showMandatoryFirstTimeInternetConnection(this);
		}
	}

	

	private void checkForCrashes() 
	{
		CrashManager.register(this, Constants.HOCKEY_APP_TOKEN);
	}

	
	
//	private void checkForUpdates() 
//	{
//		// Remove this for store builds!
//		UpdateManager.register(this, Consts.HOCKEY_APP_TOKEN);
//	}

	
	
	@Override
	protected void onResume() 
	{
		super.onResume();		
		
		checkForCrashes();
	}

		
	
	private void startPrimaryActivity() 
	{
		Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
		
		startActivity(intent);
		
		finish();
	}


	
	@Override
	public void onResult(final FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch (fetchRequestResult) 
		{
			case SUCCESS: 
			{
				startPrimaryActivity();
				break;
			}
			case INTERNET_CONNECTION_NOT_AVAILABLE:
			{
				DialogHelper.showMandatoryFirstTimeInternetConnection(this);
				break;
			}
			case API_VERSION_TOO_OLD: 
			{
				DialogHelper.showMandatoryAppUpdateDialog(this);
				break;
			}
			default:
			{
				DialogHelper.showMandatoryFirstTimeInternetConnection(this);
				break;
			}
		}
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
}

