package com.millicom.secondscreen.content.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.content.SSActivity;

public class PopularPageActivity extends SSActivity{
	
	private static final String TAG = "PopularPageActivity";
	private String token;
	
	// EXTENDED VIEW OF THE POPULAR BLOCK AT THE ACTIVITY PAGE
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.layout_activity_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		
	}

	

	@Override
	protected void updateUI(REQUEST_STATUS status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void loadPage() {
		// TODO Auto-generated method stub
		
	}

}
