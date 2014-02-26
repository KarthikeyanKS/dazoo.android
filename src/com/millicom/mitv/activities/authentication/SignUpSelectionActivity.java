
package com.millicom.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.millicom.mitv.activities.FeedActivity;
import com.millicom.mitv.activities.base.BaseLoginActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.mitv.Consts;
import com.mitv.R;



public class SignUpSelectionActivity 
	extends BaseLoginActivity
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
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
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
				
				intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, FeedActivity.class.getName());
				
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
				
				intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, FeedActivity.class.getName());
				
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
				
				intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, FeedActivity.class.getName());
				
				startActivity(intent);
				
				finish();
			}
		});
	}
}