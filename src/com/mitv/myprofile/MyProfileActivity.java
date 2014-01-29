package com.mitv.myprofile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.authentication.DazooLoginActivity;
import com.mitv.authentication.FacebookLoginActivity;
import com.mitv.authentication.SignUpActivity;
import com.mitv.content.SSActivity;
import com.mitv.content.activity.ActivityActivity;
import com.mitv.homepage.HomeActivity;
import com.mitv.notification.NotificationDataSource;
import com.mitv.search.SearchPageActivity;
import com.mitv.storage.DazooStore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class MyProfileActivity extends SSActivity implements OnClickListener {

	private static final String	TAG			= "MyProfileFragment";

	private RelativeLayout		mRemindersContainer, mLikesContainer, mMyChannelsContainer, mSettingsContainer, mMyProfileContainer, mSigninContainer, mFacebookContainer, mSignUpContainer;

	private View mTabDividerLeft, mTabDividerRight;
	private ProgressBar			mAvatarProgressBar;
	private ImageView			mAvatarImageView;
	private TextView			mUserNameTextView, mRemindersTextView, mLikesTextView, mMyChannelsTextView, mSettingsTextView, mRemindersCountTextView, mLikesCountTextView, mMyChannelsCountTextView;
	private Button				mLoginBtn;
	private String				userFirstName, userLastName, userAvatarUrl;
	private boolean				mIsLoggedIn	= false;
	private String				mToken;
	private ActionBar			mActionBar;
	private RelativeLayout		mTabTvGuide, mTabActivity, mTabProfile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.layout_myprofile_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		mToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();

		if (mToken != null && TextUtils.isEmpty(mToken) != true) {
			mIsLoggedIn = true;

			userFirstName = ((SecondScreenApplication) getApplicationContext()).getUserFirstName();
			userLastName = ((SecondScreenApplication) getApplicationContext()).getUserLastName();
			userAvatarUrl = ((SecondScreenApplication) getApplicationContext()).getUserAvatarUrl();
		}

		initViews();
		populateViews();
	}
	
	private void initViews() {
		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		mTabProfile.setOnClickListener(this);
		
		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));

		mActionBar = getSupportActionBar();
		mActionBar.setTitle(getResources().getString(R.string.myprofile_title));

		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		final int actionBarColor = getResources().getColor(R.color.blue1);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mMyProfileContainer = (RelativeLayout) findViewById(R.id.myprofile_person_container);

		// user information
		mAvatarImageView = (ImageView) findViewById(R.id.myprofile_avatar_iv);
		mAvatarProgressBar = (ProgressBar) findViewById(R.id.myprofile_avatar_progressbar);
		mUserNameTextView = (TextView) findViewById(R.id.myprofile_name_tv);

		// reminders
		mRemindersContainer = (RelativeLayout) findViewById(R.id.myprofile_reminders_container);
		mRemindersTextView = (TextView) findViewById(R.id.myprofile_reminders_title_tv);
		mRemindersCountTextView = (TextView) findViewById(R.id.myprofile_reminders_button_tv);
		mRemindersContainer.setOnClickListener(this);

		// likes
		mLikesContainer = (RelativeLayout) findViewById(R.id.myprofile_likes_container);
		mLikesTextView = (TextView) findViewById(R.id.myprofile_likes_title_tv);
		mLikesCountTextView = (TextView) findViewById(R.id.myprofile_likes_button_tv);
		mLikesContainer.setOnClickListener(this);

		// my channels
		mMyChannelsContainer = (RelativeLayout) findViewById(R.id.myprofile_my_channels_container);
		mMyChannelsTextView = (TextView) findViewById(R.id.myprofile_my_channels_title_tv);
		mMyChannelsCountTextView = (TextView) findViewById(R.id.myprofile_my_channels_button_tv);
		mMyChannelsContainer.setOnClickListener(this);

		// settings
		mSettingsContainer = (RelativeLayout) findViewById(R.id.myprofile_settings_container);
		mSettingsTextView = (TextView) findViewById(R.id.myprofile_settings_title_tv);
		mSettingsContainer.setOnClickListener(this);

		// sign in
		mSigninContainer = (RelativeLayout) findViewById(R.id.myprofile_signin_container);
		mFacebookContainer = (RelativeLayout) findViewById(R.id.myprofile_signin_facebook_container);
		mFacebookContainer.setOnClickListener(this);

		mSignUpContainer = (RelativeLayout) findViewById(R.id.myprofile_signup_email_container);
		mSignUpContainer.setOnClickListener(this);

		mLoginBtn = (Button) findViewById(R.id.myprofile_login_btn);
		mLoginBtn.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	private void populateViews() {
		mRemindersTextView.setText(getResources().getString(R.string.icon_clock) + " " + getResources().getString(R.string.reminders));
		mSettingsTextView.setText(getResources().getString(R.string.icon_settings) + " " + getResources().getString(R.string.settings));

		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		mRemindersCountTextView.setText("(" + String.valueOf(notificationDataSource.getNumberOfNotifications()) + ")");

		if (mIsLoggedIn) {
			if (userAvatarUrl != null && TextUtils.isEmpty(userAvatarUrl) != true) {
				ImageAware imageAware = new ImageViewAware(mAvatarImageView, false);
				ImageLoader.getInstance().displayImage(userAvatarUrl, imageAware);
			}

			mLikesTextView.setText(getResources().getString(R.string.icon_heart) + " " + getResources().getString(R.string.likes));
			if (DazooStore.getInstance().getLikeIds() != null && DazooStore.getInstance().getLikeIds().isEmpty() != true) {
				mLikesCountTextView.setText("(" + String.valueOf(DazooStore.getInstance().getLikeIds().size()) + ")");
			}
			else {
				mLikesCountTextView.setText("(0)");
			}

			mMyChannelsTextView.setText(getResources().getString(R.string.icon_blocks) + " " + getResources().getString(R.string.my_channels));
			if (DazooStore.getInstance().getAllChannelIds() != null && DazooStore.getInstance().getAllChannelIds().isEmpty() != true) {
				mMyChannelsCountTextView.setText("(" + String.valueOf(DazooStore.getInstance().getMyChannelIds().size()) + ")");
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				if (userFirstName != null && userLastName != null && userFirstName.isEmpty() != true && userLastName.isEmpty() != true) {
					mAvatarImageView.setImageResource(R.color.white);
					mUserNameTextView.setText(userFirstName + " " + userLastName);
				} else {
					mMyProfileContainer.setVisibility(View.GONE);
				}
			} else {
				if (userFirstName != null && userLastName != null && TextUtils.isEmpty(userFirstName) != true && TextUtils.isEmpty(userLastName) != true) {
					mAvatarImageView.setImageResource(R.color.white);
					mUserNameTextView.setText(userFirstName + " " + userLastName);
				} else {
					mMyProfileContainer.setVisibility(View.GONE);
				}
			}
		} else {
			mLikesContainer.setVisibility(View.GONE);
			mMyChannelsContainer.setVisibility(View.GONE);
			mMyProfileContainer.setVisibility(View.GONE);
			mSigninContainer.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		finish();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.myprofile_likes_container:
			// likes
			Intent intentLikes = new Intent(MyProfileActivity.this, LikesActivity.class);
			startActivityForResult(intentLikes, 0);
			
			break;
		case R.id.myprofile_reminders_container:
			// reminders
			Intent intentReminders = new Intent(MyProfileActivity.this, RemindersActivity.class);
			startActivityForResult(intentReminders, 1);
			
			break;
		case R.id.myprofile_my_channels_container:
			// my channels
			Intent intentMyChannels = new Intent(MyProfileActivity.this, MyChannelsActivity.class);
			startActivityForResult(intentMyChannels, 2);
			
			break;
		case R.id.myprofile_settings_container:
			// settings
			Intent intentSettings = new Intent(MyProfileActivity.this, SettingsActivity.class);
			startActivityForResult(intentSettings, 3);
			
			break;
		case R.id.myprofile_signin_facebook_container:
			// facebook sign in
			Intent intentFacebookSignIn = new Intent(MyProfileActivity.this, FacebookLoginActivity.class);
			startActivity(intentFacebookSignIn);
			
			break;
		case R.id.myprofile_signup_email_container:
			Intent intentSignUp = new Intent(MyProfileActivity.this, SignUpActivity.class);
			startActivity(intentSignUp);
			
			break;
		case R.id.myprofile_login_btn:
			Intent intentLogin = new Intent(MyProfileActivity.this, DazooLoginActivity.class);
			startActivity(intentLogin);
			
			break;
		case R.id.tab_tv_guide:
			// tab to home page
			Intent intentHome = new Intent(MyProfileActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			// tab to activity page
			Intent intentMe = new Intent(MyProfileActivity.this, ActivityActivity.class);
			startActivity(intentMe);
			
			break;
		case R.id.tab_me:
			// we are here: do nothing
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode == Consts.INFO_UPDATE_LIKES) {
				int likesNumber = data.getIntExtra(Consts.INFO_UPDATE_LIKES_NUMBER, 0);
				mLikesCountTextView.setText("(" + String.valueOf(likesNumber) + ")");
			}
			break;
		case 1:
			if (resultCode == Consts.INFO_UPDATE_REMINDERS) {
				int remindersNumber = data.getIntExtra(Consts.INFO_UPDATE_REMINDERS_NUMBER, 0);
				mRemindersCountTextView.setText("(" + String.valueOf(remindersNumber) + ")");
			}
			break;
		case 2:
			if (resultCode == Consts.INFO_UPDATE_MYCHANNELS) {
				int myChannelsNumber = data.getIntExtra(Consts.INFO_UPDATE_MYCHANNELS_NUMBER, 0);
				mMyChannelsCountTextView.setText("(" + String.valueOf(myChannelsNumber) + ")");
			}
			break;
		case 3:
			if (resultCode == Consts.INFO_UPDATE_LOGOUT) {
				mIsLoggedIn = false;
				populateViews();
			}
			break;
		}
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {}

	@Override
	protected void loadPage() {}
}