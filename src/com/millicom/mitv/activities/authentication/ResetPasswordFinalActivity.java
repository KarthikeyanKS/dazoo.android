
package com.millicom.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;



public class ResetPasswordFinalActivity 
	extends SSSignInSignupBaseActivity 
	implements OnClickListener
{
	@SuppressWarnings("unused")
	private static final String TAG = ResetPasswordFinalActivity.class.getName();

	
	private ActionBar actionBar;
	private Button loginBtn;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_resetpasswordfinal_activity);

		// add the activity to the list of running activities
		SecondScreenApplication.sharedInstance().getActivityList().add(this);

		initViews();
	}
	
	
	
	@Override
	protected void updateUI(REQUEST_STATUS status)
	{
		/* Have to have this method here since SSActivity has this method abstract */
	}

	
	
	@Override
	protected void loadPage() 
	{
		/* Have to have this method here since SSActivity has this method abstract */
	}

	
	
	private void initViews() 
	{
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.reset_password));

		loginBtn = (Button) findViewById(R.id.resetpassword_already_login_btn);
		loginBtn.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.resetpassword_already_login_btn:
			{
				Intent intentSignIn = new Intent(ResetPasswordFinalActivity.this, MiTVLoginActivity.class);
				startActivity(intentSignIn);
				break;
			}
		}
	}
}