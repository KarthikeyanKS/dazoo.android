
package com.millicom.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.mitv.R;



public class SignInOrSignupWithFacebookActivity 
	extends SignInBaseActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = SignInOrSignupWithFacebookActivity.class.getName();

	
	private ActionBar actionBar;
	private RelativeLayout mFacebookContainer, signUpContainer;
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
		// TODO NewArc - Do something here?
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		} 
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				// TODO NewArc - Do something here?
				break;
			}
	
			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			// Respond to the action bar's Up/Home button
			// update the likes list on Up/Home button press too
			case android.R.id.home:
			{
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				startActivity(upIntent);
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}

	
	
	private void initViews() 
	{
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.sign_up));

		mFacebookContainer = (RelativeLayout) findViewById(R.id.signin_facebook_container);
		
		mFacebookContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SignInOrSignupWithFacebookActivity.this, FacebookLoginActivity.class);
				
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
				Intent intent = new Intent(SignInOrSignupWithFacebookActivity.this, SignUpWithEmailActivity.class);
				startActivity(intent);
			}
		});

		loginButton = (Button) findViewById(R.id.signin_login_btn);
		
		loginButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(SignInOrSignupWithFacebookActivity.this, MiTVLoginActivity.class);
				
				startActivity(intent);
			}
		});
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		
		finish();
	}
}