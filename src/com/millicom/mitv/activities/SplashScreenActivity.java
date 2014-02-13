package com.millicom.mitv.activities;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.homepage.HomeActivity;

public class SplashScreenActivity extends ActionBarActivity implements ActivityCallbackListener {
	
	private static final String					TAG					= "SplashScreenActivity";
	private boolean mTimeHasElapsed = false;
	
	private static final int CALLS_BEFORE_STARTING_HOME_ACTIVITY = 2;
	private static final long MINUMUM_DISPLAY_TIME = 3000l;
	
	private int mBackendCallsCompletedCount = 0;
	private CountDownTimer mCountTimer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_splash_screen_activity);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		
		mCountTimer = new CountDownTimer(MINUMUM_DISPLAY_TIME, MINUMUM_DISPLAY_TIME) {
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
			@Override
			public void onFinish() {
				mTimeHasElapsed = true;
				startPrimaryActivity();
				
			}
		};
		mCountTimer.start();
		
		ContentManager.sharedInstance().fetchAppData(this);
	}


	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	private void checkForCrashes() {
		CrashManager.register(this, Consts.HOCKEY_APP_TOKEN);
	}

	private void checkForUpdates() {
		// Remove this for store builds!
		UpdateManager.register(this, Consts.HOCKEY_APP_TOKEN);
	}

	public void showUpdateDialog() {
		final Dialog dialog = new Dialog(this, R.style.remove_notification_dialog);
		dialog.setContentView(R.layout.dialog_prompt_update);
		dialog.setCancelable(false);

		Button okButton = (Button) dialog.findViewById(R.id.dialog_prompt_update_button);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String appPackageName = getPackageName(); 
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
				}
			}
		});
		dialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();		
		checkForCrashes();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	};

	private void startPrimaryActivity() {
		if(mTimeHasElapsed && mBackendCallsCompletedCount == CALLS_BEFORE_STARTING_HOME_ACTIVITY) {
			Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
			startActivity(intent);
		}
	}


	@Override
	public void onResult(final FetchRequestResultEnum fetchRequestResult) {
		switch (fetchRequestResult) {
		case SUCCESS: {
			startPrimaryActivity();
			break;
		}
		case API_VERSION_TOO_OLD: {
			showUpdateDialog();
			break;
		}
		default:
			// TODO show toast with error message? Implement a retry mechanism?
			break;
		}
	}
}

