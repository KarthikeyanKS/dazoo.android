
package com.millicom.mitv.activities;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.authentication.LoginWithMiTVUserActivity;
import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityWithTabs;
import com.mitv.R;
import com.mitv.customviews.FontTextView;
import com.mitv.notification.NotificationDataSource;
import com.mitv.storage.MiTVStore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class MyProfileActivity 
	extends BaseActivity 
	implements ActivityWithTabs, OnClickListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = MyProfileActivity.class.getName();

	private String userFirstName;
	private String userLastName;
	private String userAvatarUrl;
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

		if (ContentManager.sharedInstance().isLoggedIn()) 
		{
			userFirstName = ContentManager.sharedInstance().getFromStorageUserFirstname();
			userLastName = ContentManager.sharedInstance().getFromStorageUserLastname();
			
			// TDOO from where do we get the avatar?
			// mUserAvatarUrl = ((SecondScreenApplication) getApplicationContext()).getUserAvatarUrl();
		}
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		initViews();
		
		populateViews();
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

		signUpContainer = (RelativeLayout) findViewById(R.id.myprofile_signup_container);
		signUpContainer.setOnClickListener(this);

		loginContainer = (RelativeLayout) findViewById(R.id.myprofile_login_container);
		loginContainer.setOnClickListener(this);

		/* ONLY USED WHEN LOGGED IN */
		personalView = (RelativeLayout) findViewById(R.id.myprofile_person_container_signed_in);

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
		reminderCountTv.setText("(" + String.valueOf(notificationDataSource.getNumberOfNotifications()) + ")");

		if (ContentManager.sharedInstance().isLoggedIn())
		{
			personalView.setVisibility(View.VISIBLE);
			
			signInOrSignUpView.setVisibility(View.GONE);

			if (userAvatarUrl != null && TextUtils.isEmpty(userAvatarUrl) != true) 
			{
				ImageAware imageAware = new ImageViewAware(avatarImageView, false);
				ImageLoader.getInstance().displayImage(userAvatarUrl, imageAware);
			}

			// boolean refreshLikeIdsFromService =
			// DateUtilities.isElapsedTimeGreaterThan(MiTVStore.getInstance().getmLikeIdsFetchTimestamp(),
			// Consts.LIKE_IDS_REFRESH_INTERVAL_IN_MINUTES);
			//
			// if(refreshLikeIdsFromService)
			// {
			// // TODO: Refresh on a separate thread
			// LikeService.getLikeIdsList();
			// }

			if (MiTVStore.getInstance().getLikeIds() != null && MiTVStore.getInstance().getLikeIds().isEmpty() != true) {
				likesCountTv.setText("(" + String.valueOf(MiTVStore.getInstance().getLikeIds().size()) + ")");
			} else {
				likesCountTv.setText("(0)");
			}

			// boolean refreshChannelIdsFromService =
			// DateUtilities.isElapsedTimeGreaterThan(MiTVStore.getInstance().getmMyChannelIdsFetchTimestamp(),
			// Consts.CHANNEL_IDS_REFRESH_INTERVAL_IN_MINUTES);
			//
			// if(refreshChannelIdsFromService)
			// {
			// // ApiClient.getMyChannelIds();
			// }
			// No need for else

			if (MiTVStore.getInstance().getChannelIds() != null && MiTVStore.getInstance().getChannelIds().isEmpty() != true) {
				channelCountTv.setText("(" + String.valueOf(MiTVStore.getInstance().getChannelIds().size()) + ")");
			}

			if (userFirstName != null && userLastName != null && userFirstName.length() > 0 && userLastName.length() > 0) {
				userNameTextView.setText(userFirstName + " " + userLastName);
			} else {
				personalView.setVisibility(View.GONE);
			}

		} 
		else 
		{
			signInOrSignUpView.setVisibility(View.VISIBLE);

			personalView.setVisibility(View.GONE);
			likesContainer.setVisibility(View.GONE);
			channelsContainer.setVisibility(View.GONE);
			logoutContainer.setVisibility(View.GONE);
		}
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();

		finish();
	}

	
	
	@Override
	public void onClick(View v) 
	{
		/* IMPORTANT to call super so that the BaseActivity can handle the tab clicking */
		super.onClick(v);
		int id = v.getId();
		
		switch (id) 
		{
		case R.id.myprofile_likes_container: {
			// likes
			Intent intentLikes = new Intent(MyProfileActivity.this, LikesActivity.class);
			startActivityForResult(intentLikes, 0);

			break;
		}
		case R.id.myprofile_channels_container: {
			// my channels
			Intent intentMyChannels = new Intent(MyProfileActivity.this, MyChannelsActivity.class);
			startActivityForResult(intentMyChannels, 2);

			break;
		}
		case R.id.myprofile_reminders_container: {
			// reminders
			Intent intentReminders = new Intent(MyProfileActivity.this, RemindersActivity.class);
			startActivityForResult(intentReminders, 1);

			break;
		}

		case R.id.myprofile_login_container: {
			Intent intentLogin = new Intent(MyProfileActivity.this, LoginWithMiTVUserActivity.class);
			startActivity(intentLogin);

			break;
		}

		case R.id.myprofile_terms_of_use_container: {
			Intent intentLogin = new Intent(MyProfileActivity.this, TermsActivity.class);
			startActivity(intentLogin);

			break;
		}

		case R.id.myprofile_about_us_container: {
			Intent intentLogin = new Intent(MyProfileActivity.this, AboutUsActivity.class);
			startActivity(intentLogin);

			break;
		}

		case R.id.myprofile_signup_container: {
			Intent intentLogin = new Intent(MyProfileActivity.this, SignUpSelectionActivity.class);
			startActivity(intentLogin);

			break;
		}

		case R.id.myprofile_logout_container: 
		{
			// TODO
			ContentManager.sharedInstance().performLogout(this);

			startActivity(new Intent(MyProfileActivity.this, HomeActivity.class));
			
			break;
		}
		}
	}


	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceUserLikes(this, false);
	}

	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
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
				// Do nothing
				break;
			}
		}
	}
}
