
package com.millicom.mitv.activities;



import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.FetchDataProgressCallbackListener;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.FontTextView;



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
		
		ContentManager.sharedInstance().fetchFromServiceAppData(this, this);
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

	
	
	private void checkForUpdates() 
	{
		// Remove this for store builds!
		UpdateManager.register(this, Consts.HOCKEY_APP_TOKEN);
	}

	
	
	public void showUpdateDialog() 
	{
		final Dialog dialog = new Dialog(this, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_prompt_update);
		dialog.setCancelable(false);

		Button okButton = (Button) dialog.findViewById(R.id.dialog_prompt_update_button);
		
		okButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				final String appPackageName = getPackageName(); 
				
				try 
				{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} 
				catch (android.content.ActivityNotFoundException anfe) 
				{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
				}
			}
		});
		
		dialog.show();
	}

	
	
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
	}


	
	@Override
	public void onResult(final FetchRequestResultEnum fetchRequestResult) 
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
				showUpdateDialog();
				break;
			}
			default:
			{
				// TODO show toast with error message? Implement a retry mechanism?
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
		String progressMessage = sb.toString();
		
		progressTextView.setText(progressMessage);
	}
}

