
package com.millicom.mitv.activities;



import net.hockeyapp.android.CrashManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.FetchDataProgressCallbackListener;
import com.millicom.mitv.utilities.DialogHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.FontTextView;



public class SplashScreenActivity 
	extends ActionBarActivity 
	implements ActivityCallbackListener, FetchDataProgressCallbackListener
{	
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
		
		ContentManager.sharedInstance().fetchFromServiceInitialCall(this, this);
	}


	
	@Override
	protected void onPause() 
	{
		super.onPause();
	}
	
	

	private void checkForCrashes() 
	{
		CrashManager.register(this, Consts.HOCKEY_APP_TOKEN);
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

	
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
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
			case API_VERSION_TOO_OLD: 
			{
				DialogHelper.showMandatoryAppUpdateDialog(this);
				break;
			}
			default:
			{
				Log.w(TAG, "Unhandled fetch result rquest.");
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

