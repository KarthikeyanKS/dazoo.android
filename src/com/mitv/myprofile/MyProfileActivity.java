package com.mitv.myprofile;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.authentication.MiTVLoginActivity;
import com.mitv.authentication.SignInOrSignupWithFacebookActivity;
import com.mitv.content.SSActivity;
import com.mitv.content.activity.ActivityActivity;
import com.mitv.customviews.FontTextView;
import com.mitv.homepage.HomeActivity;
import com.mitv.manager.LoginManager;
import com.mitv.notification.NotificationDataSource;
import com.mitv.storage.MiTVStore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class MyProfileActivity extends SSActivity implements OnClickListener {

	private boolean mIsLoggedIn;
	private String mUserFirstName;
	private String mUserLastName;
	private String mUserAvatarUrl;
	private RelativeLayout mTabTvGuide, mTabActivity, mTabProfile;
	private RelativeLayout mAboutContainer;
	private RelativeLayout mTermsContainer;

	/* ONLY USED WHEN NOT LOGGED IN */
	private LinearLayout mSignInOrSignUpView;
	private RelativeLayout mSignUpContainer;
	private RelativeLayout mLoginContainer;

	/* ONLY USED WHEN LOGGED IN */
	private RelativeLayout mPersonalView;
	private ImageView mAvatarImageView;
	private FontTextView mUserNameTextView;
	private RelativeLayout mLikesContainer;
	private RelativeLayout mChannelsContainer;
	private RelativeLayout mRemindersContainer;
	private RelativeLayout mLogoutContainer;

	private FontTextView mLikesCountTv, mChannelCountTv, mReminderCountTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_my_profile);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		String token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();

		if (token != null && TextUtils.isEmpty(token) != true) {
			mIsLoggedIn = true;

			mUserFirstName = ((SecondScreenApplication) getApplicationContext()).getUserFirstName();
			mUserLastName = ((SecondScreenApplication) getApplicationContext()).getUserLastName();
			mUserAvatarUrl = ((SecondScreenApplication) getApplicationContext()).getUserAvatarUrl();
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

		View tabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		View tabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		tabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		tabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.myprofile_title));

		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		mAboutContainer = (RelativeLayout) findViewById(R.id.myprofile_about_us_container);
		mAboutContainer.setOnClickListener(this);

		mTermsContainer = (RelativeLayout) findViewById(R.id.myprofile_terms_of_use_container);
		mTermsContainer.setOnClickListener(this);

		/* ONLY USED WHEN NOT LOGGED IN */
		mSignInOrSignUpView = (LinearLayout) findViewById(R.id.myprofile_sign_in_or_sign_up_container);

		mSignUpContainer = (RelativeLayout) findViewById(R.id.myprofile_signup_container);
		mSignUpContainer.setOnClickListener(this);

		mLoginContainer = (RelativeLayout) findViewById(R.id.myprofile_login_container);
		mLoginContainer.setOnClickListener(this);

		/* ONLY USED WHEN LOGGED IN */
		mPersonalView = (RelativeLayout) findViewById(R.id.myprofile_person_container_signed_in);

		mLikesCountTv = (FontTextView) findViewById(R.id.myprofile_likes_count_tv);
		mChannelCountTv = (FontTextView) findViewById(R.id.myprofile_channels_count_tv);
		mReminderCountTv = (FontTextView) findViewById(R.id.myprofile_reminders_count_tv);

		mLikesContainer = (RelativeLayout) findViewById(R.id.myprofile_likes_container);
		mLikesContainer.setOnClickListener(this);

		mChannelsContainer = (RelativeLayout) findViewById(R.id.myprofile_channels_container);
		mChannelsContainer.setOnClickListener(this);

		mRemindersContainer = (RelativeLayout) findViewById(R.id.myprofile_reminders_container);
		mRemindersContainer.setOnClickListener(this);

		mLogoutContainer = (RelativeLayout) findViewById(R.id.myprofile_logout_container);
		mLogoutContainer.setOnClickListener(this);

		mAvatarImageView = (ImageView) findViewById(R.id.myprofile_avatar_iv);
		mUserNameTextView = (FontTextView) findViewById(R.id.myprofile_name_tv);

	}

	private void populateViews() {
		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		mReminderCountTv.setText("(" + String.valueOf(notificationDataSource.getNumberOfNotifications()) + ")");

		if (mIsLoggedIn) {
			mPersonalView.setVisibility(View.VISIBLE);
			mSignInOrSignUpView.setVisibility(View.GONE);
			
			if (mUserAvatarUrl != null && TextUtils.isEmpty(mUserAvatarUrl) != true) {
				ImageAware imageAware = new ImageViewAware(mAvatarImageView, false);
				ImageLoader.getInstance().displayImage(mUserAvatarUrl, imageAware);
			}

			if (MiTVStore.getInstance().getLikeIds() != null && MiTVStore.getInstance().getLikeIds().isEmpty() != true) {
				mLikesCountTv.setText("(" + String.valueOf(MiTVStore.getInstance().getLikeIds().size()) + ")");
			} else {
				mLikesCountTv.setText("(0)");
			}

			if (MiTVStore.getInstance().getAllChannelIds() != null && MiTVStore.getInstance().getAllChannelIds().isEmpty() != true) {
				mChannelCountTv.setText("(" + String.valueOf(MiTVStore.getInstance().getMyChannelIds().size()) + ")");
			}

			if (mUserFirstName != null && mUserLastName != null && mUserFirstName.length() > 0 && mUserLastName.length() > 0) {
				mUserNameTextView.setText(mUserFirstName + " " + mUserLastName);
			} else {
				mPersonalView.setVisibility(View.GONE);
			}

		} else {
			mSignInOrSignUpView.setVisibility(View.VISIBLE);
			
			mPersonalView.setVisibility(View.GONE);
			mLikesContainer.setVisibility(View.GONE);
			mChannelsContainer.setVisibility(View.GONE);
			mLogoutContainer.setVisibility(View.GONE);
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
			Intent intentLogin = new Intent(MyProfileActivity.this, MiTVLoginActivity.class);
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
			Intent intentLogin = new Intent(MyProfileActivity.this, SignInOrSignupWithFacebookActivity.class);
			startActivity(intentLogin);

			break;
		}

		case R.id.myprofile_logout_container: {
			LoginManager.logout();
			// clear all the running activities and start the application from
			// the whole beginning
			SecondScreenApplication.getInstance().clearActivityBacktrace();

			startActivity(new Intent(MyProfileActivity.this, HomeActivity.class));
			break;
		}

		case R.id.tab_tv_guide: {
			// tab to home page
			Intent intentHome = new Intent(MyProfileActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);

			break;
		}
		case R.id.tab_activity: {
			// tab to activity page
			Intent intentMe = new Intent(MyProfileActivity.this, ActivityActivity.class);
			startActivity(intentMe);

			break;
		}
		case R.id.tab_me: {
			// we are here: do nothing
			break;
		}
		}
	}

	@Override
	protected void updateUI(REQUEST_STATUS status) {
	}

	@Override
	protected void loadPage() {
	}

}
