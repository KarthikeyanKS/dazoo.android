
package com.millicom.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.millicom.mitv.activities.HomeActivity;
import com.millicom.mitv.activities.MyChannelsActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.mitv.R;



public class FacebookMiTVLoginActivity 
	extends SignInBaseActivity 
	implements OnClickListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = FacebookMiTVLoginActivity.class.getName();

	
	private ActionBar mActionBar;
	private RelativeLayout mSetChannels;
	private RelativeLayout mConnectWithFriends;
	private RelativeLayout mTakeTour;
	private RelativeLayout mSkip;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_facebook_mitvlogin_activity);
		
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
	
	
	
	private void initViews() 
	{
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.app_name));

		mSetChannels = (RelativeLayout) findViewById(R.id.facebook_set_channels_container);
		mSetChannels.setOnClickListener(this);
		
		mConnectWithFriends = (RelativeLayout) findViewById(R.id.facebook_connect_with_friends_container);
		mConnectWithFriends.setOnClickListener(this);
		
		mTakeTour = (RelativeLayout) findViewById(R.id.facebook_take_tour_container);
		mTakeTour.setOnClickListener(this);
		
		mSkip = (RelativeLayout) findViewById(R.id.facebook_skip_container);
		mSkip.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		
		switch (id)
		{
			case R.id.facebook_set_channels_container:
			{
				Intent intent = new Intent(FacebookMiTVLoginActivity.this, MyChannelsActivity.class);
				
				startActivity(intent);
				
				break;
			}
			
			case R.id.facebook_skip_container:
			{
				// Go directly to Start page
				Intent intentHome = new Intent(FacebookMiTVLoginActivity.this, HomeActivity.class);
				
				startActivity(intentHome);
				
				finish();
				
				break;
			}
			
			default:
			case R.id.facebook_connect_with_friends_container:
			case R.id.facebook_take_tour_container:
			{
				// TODO To be determined
				break;
			}
		}
	}
}