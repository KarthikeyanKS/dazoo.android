
package com.mitv.activities;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mitv.R;
import com.mitv.activities.authentication.LoginWithFacebookActivity;
import com.mitv.activities.authentication.LoginWithMiTVUserActivity;
import com.mitv.activities.authentication.SignUpWithEmailActivity;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;



public class SignUpSelectionActivity 
	extends BaseActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = SignUpSelectionActivity.class.getName();

	
	private RelativeLayout facebookContainer;
	private RelativeLayout signUpContainer;
	private Button loginButton;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_signin_activity);
		
		initViews();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
	
	
	@Override
	protected void loadData() 
	{
		// Do nothing (no data to load on this activity)
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		// Do nothing (no data to load on this activity)
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		// Do nothing (no data to load on this activity)
	}
	
	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.sign_up));

		facebookContainer = (RelativeLayout) findViewById(R.id.signin_facebook_container);
		
		facebookContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SignUpSelectionActivity.this, LoginWithFacebookActivity.class);
				
				startActivity(intent);
				
				finish();
			}
		});

		signUpContainer = (RelativeLayout) findViewById(R.id.signin_signup_email_container);
		
		signUpContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SignUpSelectionActivity.this, SignUpWithEmailActivity.class);
				
				startActivity(intent);
				
				finish();
			}
		});

		loginButton = (Button) findViewById(R.id.signin_login_btn);
		
		loginButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(SignUpSelectionActivity.this, LoginWithMiTVUserActivity.class);
				
				startActivity(intent);
				
				finish();
			}
		});
	}
}