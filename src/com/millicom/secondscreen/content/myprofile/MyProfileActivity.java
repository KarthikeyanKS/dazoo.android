package com.millicom.secondscreen.content.myprofile;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.authentication.DazooLoginActivity;
import com.millicom.secondscreen.authentication.FacebookLoginActivity;
import com.millicom.secondscreen.authentication.SignUpActivity;
import com.millicom.secondscreen.content.activity.ActivityActivity;
import com.millicom.secondscreen.content.homepage.HomeActivity;
import com.millicom.secondscreen.content.search.SearchPageActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

public class MyProfileActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG			= "MyProfileFragment";

	private RelativeLayout		mRemindersContainer, mLikesContainer, mMyChannelsContainer, mSettingsContainer, mMyProfileContainer, mSigninContainer, mFacebookContainer, mSignUpContainer;
	private ProgressBar			mAvatarProgressBar;
	private ImageView			mAvatarImageView;
	private TextView			mUserNameTextView, mRemindersTextView, mLikesTextView, mMyChannelsTextView, mSettingsTextView, mRemindersCountTextView, mLikesCountTextView, mMyChannelsCountTextView;
	private Button				mLoginBtn;
	private String				userFirstName, userLastName;
	private boolean				mIsLoggedIn	= false;
	private String				mToken;
	private ActionBar			mActionBar;
	private TextView			mTxtTabTvGuide, mTxtTabPopular, mTxtTabFeed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_myprofile_activity);

		mToken = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		if (mToken != null && TextUtils.isEmpty(mToken) != true) {
			mIsLoggedIn = true;

			userFirstName = ((SecondScreenApplication) getApplicationContext()).getUserFirstName();
			userLastName = ((SecondScreenApplication) getApplicationContext()).getUserLastName();
		}
		initViews();
		populateViews();
	}

	private void initViews() {
		mTxtTabTvGuide = (TextView) findViewById(R.id.show_tvguide);
		mTxtTabTvGuide.setOnClickListener(this);
		mTxtTabPopular = (TextView) findViewById(R.id.show_activity);
		mTxtTabPopular.setOnClickListener(this);
		mTxtTabFeed = (TextView) findViewById(R.id.show_me);
		mTxtTabFeed.setOnClickListener(this);

		mTxtTabTvGuide.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabPopular.setTextColor(getResources().getColor(R.color.gray));
		mTxtTabFeed.setTextColor(getResources().getColor(R.color.orange));

		mActionBar = getSupportActionBar();

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setTitle(getResources().getString(R.string.myprofile_title));

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

	private void populateViews() {
		if (mIsLoggedIn) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				if (userFirstName != null && userLastName != null && userFirstName.isEmpty() != true && userLastName.isEmpty() != true) {
					mAvatarImageView.setImageResource(R.drawable.loadimage_2x);
					mUserNameTextView.setText(userFirstName + " " + userLastName);
				} else {
					mMyProfileContainer.setVisibility(View.GONE);
				}
			} else {
				if (userFirstName != null && userLastName != null && TextUtils.isEmpty(userFirstName) != true && TextUtils.isEmpty(userLastName) != true) {
					mAvatarImageView.setImageResource(R.drawable.loadimage_2x);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_search:
			Intent toSearchPage = new Intent(MyProfileActivity.this, SearchPageActivity.class);
			startActivity(toSearchPage);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_homepage, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.myprofile_likes_container:
			// likes
			Intent intentLikes = new Intent(MyProfileActivity.this, LikesActivity.class);
			startActivityForResult(intentLikes, 0);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.myprofile_reminders_container:
			// reminders
			Intent intentReminders = new Intent(MyProfileActivity.this, RemindersActivity.class);
			startActivityForResult(intentReminders, 1);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.myprofile_my_channels_container:
			// my channels
			Intent intentMyChannels = new Intent(MyProfileActivity.this, MyChannelsActivity.class);
			startActivityForResult(intentMyChannels, 2);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.myprofile_settings_container:
			// settings
			Intent intentSettings = new Intent(MyProfileActivity.this, SettingsActivity.class);
			startActivityForResult(intentSettings, 3);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.myprofile_signin_facebook_container:
			// facebook sign in
			Intent intentFacebookSignIn = new Intent(MyProfileActivity.this, FacebookLoginActivity.class);
			startActivity(intentFacebookSignIn);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.myprofile_signup_email_container:
			Intent intentSignUp = new Intent(MyProfileActivity.this, SignUpActivity.class);
			startActivity(intentSignUp);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.myprofile_login_btn:
			Intent intentLogin = new Intent(MyProfileActivity.this, DazooLoginActivity.class);
			startActivity(intentLogin);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
		case R.id.show_tvguide:
			// tab to home page
			Intent intentHome = new Intent(MyProfileActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			break;
		case R.id.show_activity:
			// tab to activity page
			Intent intentMe = new Intent(MyProfileActivity.this, ActivityActivity.class);
			startActivity(intentMe);
			break;
		case R.id.show_me:
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
}
