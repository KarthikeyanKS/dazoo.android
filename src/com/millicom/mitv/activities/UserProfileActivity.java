
package com.millicom.mitv.activities;



import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.authentication.LoginWithMiTVUserActivity;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityWithTabs;
import com.millicom.mitv.models.TVChannelId;
import com.millicom.mitv.models.UserLike;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.customviews.FontTextView;
import com.mitv.notification.NotificationDataSource;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class UserProfileActivity 
	extends BaseContentActivity 
	implements ActivityWithTabs, OnClickListener 
{
	private static final String TAG = UserProfileActivity.class.getName();

	
	private RelativeLayout aboutContainer;
	private RelativeLayout termsContainer;

	/* ONLY USED WHEN NOT LOGGED IN */
	private LinearLayout signInOrSignUpView;
	private RelativeLayout signUpContainer;
	private RelativeLayout loginContainer;

	/* ONLY USED WHEN LOGGED IN */
	private RelativeLayout personalView;
	private ImageView avatarImageView;
	private FontTextView userNameTextView;
	private RelativeLayout likesContainer;
	private RelativeLayout channelsContainer;
	private RelativeLayout remindersContainer;
	private RelativeLayout logoutContainer;

	private FontTextView likesCountTv;
	private FontTextView channelCountTv;
	private FontTextView reminderCountTv;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_my_profile);
		
		initViews();
	}
	
	
	
	@Override
	protected void loadData() 
	{
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		if(isLoggedIn)
		{
			updateUI(UIStatusEnum.LOADING);
			
			ContentManager.sharedInstance().getElseFetchFromServiceUserLikes(this, false);
		}
		else
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
		}
	}

	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			switch(requestIdentifier)
			{
				case USER_LIKES:
				{
					updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
					break;
				}
				
				case TV_GUIDE:
				{
					Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
					intent.putExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT, true);
					startActivity(intent);
					break;
				}
				
				default:
				{
					Log.w(TAG, "Unknown request identifier");
					break;
				}
			}	
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
			case LOADING:
			{
				// Do nothing
				break;
			}
			
			case SUCCESS_WITH_NO_CONTENT:
			case SUCCEEDED_WITH_DATA:
			{
				boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
				
				if (isLoggedIn)
				{
					signInOrSignUpView.setVisibility(View.GONE);
					personalView.setVisibility(View.VISIBLE);
					likesContainer.setVisibility(View.VISIBLE);
					channelsContainer.setVisibility(View.VISIBLE);
					logoutContainer.setVisibility(View.VISIBLE);
				}
				else
				{
					signInOrSignUpView.setVisibility(View.VISIBLE);
					personalView.setVisibility(View.GONE);
					likesContainer.setVisibility(View.GONE);
					channelsContainer.setVisibility(View.GONE);
					logoutContainer.setVisibility(View.GONE);
				}
				
				populateViews();
				break;
			}
	
			default:
			{
				Log.w(TAG, "Unhandled UI status.");
				break;
			}
		}
	}
	
	
	
	private void initViews() 
	{
		actionBar.setTitle(getResources().getString(R.string.myprofile_title));
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		aboutContainer = (RelativeLayout) findViewById(R.id.myprofile_about_us_container);
		aboutContainer.setOnClickListener(this);

		termsContainer = (RelativeLayout) findViewById(R.id.myprofile_terms_of_use_container);
		termsContainer.setOnClickListener(this);

		/* ONLY USED WHEN NOT LOGGED IN */
		signInOrSignUpView = (LinearLayout) findViewById(R.id.myprofile_sign_in_or_sign_up_container);

		/* ONLY USED WHEN LOGGED IN */
		personalView = (RelativeLayout) findViewById(R.id.myprofile_person_container_signed_in);
		
		signUpContainer = (RelativeLayout) findViewById(R.id.myprofile_signup_container);
		signUpContainer.setOnClickListener(this);

		loginContainer = (RelativeLayout) findViewById(R.id.myprofile_login_container);
		loginContainer.setOnClickListener(this);

		likesCountTv = (FontTextView) findViewById(R.id.myprofile_likes_count_tv);
		channelCountTv = (FontTextView) findViewById(R.id.myprofile_channels_count_tv);
		reminderCountTv = (FontTextView) findViewById(R.id.myprofile_reminders_count_tv);

		likesContainer = (RelativeLayout) findViewById(R.id.myprofile_likes_container);
		likesContainer.setOnClickListener(this);

		channelsContainer = (RelativeLayout) findViewById(R.id.myprofile_channels_container);
		channelsContainer.setOnClickListener(this);

		remindersContainer = (RelativeLayout) findViewById(R.id.myprofile_reminders_container);
		remindersContainer.setOnClickListener(this);

		logoutContainer = (RelativeLayout) findViewById(R.id.myprofile_logout_container);
		logoutContainer.setOnClickListener(this);

		avatarImageView = (ImageView) findViewById(R.id.myprofile_avatar_iv);
		userNameTextView = (FontTextView) findViewById(R.id.myprofile_name_tv);
	}

	
	
	private void populateViews() 
	{
		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		
		StringBuilder numberOfNotificationsSB = new StringBuilder();
		numberOfNotificationsSB.append("(");
		numberOfNotificationsSB.append(String.valueOf(notificationDataSource.getNotificationCount()));
		numberOfNotificationsSB.append(")");
		
		reminderCountTv.setText(numberOfNotificationsSB.toString());

		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
				
		if (isLoggedIn)
		{
			String userAvatarImageURL = ContentManager.sharedInstance().getFromStorageUserAvatarImageURL();
			
			ImageAware imageAware = new ImageViewAware(avatarImageView, false);
			
			ImageLoader.getInstance().displayImage(userAvatarImageURL, imageAware);

			
			String userFirstname = ContentManager.sharedInstance().getFromStorageUserFirstname();
			String userLastname = ContentManager.sharedInstance().getFromStorageUserLastname();
			
			StringBuilder sbUsernameText = new StringBuilder();
			sbUsernameText.append(userFirstname);
			sbUsernameText.append(" ");
			sbUsernameText.append(userLastname);
			
			userNameTextView.setText(sbUsernameText.toString());
			
			
			ArrayList<UserLike> userLikes = ContentManager.sharedInstance().getFromStorageUserLikes();
			
			StringBuilder userLikesSB = new StringBuilder();
			userLikesSB.append("(");
			userLikesSB.append(userLikes.size());
			userLikesSB.append(")");
			
			likesCountTv.setText(userLikesSB.toString());

			
			ArrayList<TVChannelId> userChannelIds = ContentManager.sharedInstance().getFromStorageTVChannelIdsUser();
			
			StringBuilder userTVChannelIdsSB = new StringBuilder();
			userTVChannelIdsSB.append("(");
			userTVChannelIdsSB.append(userChannelIds.size());
			userTVChannelIdsSB.append(")");
			
			channelCountTv.setText(userTVChannelIdsSB.toString());
		}
	}
	
	
	
	@Override
	public void onClick(View v) 
	{
		/* IMPORTANT to call super so that the BaseActivity can handle the tab clicking */
		super.onClick(v);
		
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.myprofile_likes_container: 
			{
				Intent intent = new Intent(UserProfileActivity.this, LikesActivity.class);
				
				startActivityForResult(intent, 0);
	
				break;
			}
			case R.id.myprofile_channels_container: 
			{
				Intent intent = new Intent(UserProfileActivity.this, MyChannelsActivity.class);

				startActivityForResult(intent, 2);
	
				break;
			}
			case R.id.myprofile_reminders_container:
			{
				Intent intent = new Intent(UserProfileActivity.this, RemindersActivity.class);
				
				startActivityForResult(intent, 1);
	
				break;
			}
	
			case R.id.myprofile_login_container: 
			{
				Intent intent = new Intent(UserProfileActivity.this, LoginWithMiTVUserActivity.class);
				
				startActivity(intent);
	
				break;
			}
	
			case R.id.myprofile_terms_of_use_container:
			{
				Intent intent = new Intent(UserProfileActivity.this, TermsActivity.class);
				
				startActivity(intent);
	
				break;
			}
	
			case R.id.myprofile_about_us_container: 
			{
				Intent intent = new Intent(UserProfileActivity.this, AboutUsActivity.class);
				
				startActivity(intent);
	
				break;
			}
	
			case R.id.myprofile_signup_container: 
			{
				Intent intent = new Intent(UserProfileActivity.this, SignUpSelectionActivity.class);
				
				startActivity(intent);
	
				break;
			}
	
			case R.id.myprofile_logout_container: 
			{
				ContentManager.sharedInstance().performLogout(this);
	
				break;
			}
		}
	}
}