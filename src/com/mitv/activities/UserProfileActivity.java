
package com.mitv.activities;



import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.authentication.LoginWithMiTVUserActivity;
import com.mitv.activities.base.BaseContentActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ActivityWithTabs;
import com.mitv.models.TVChannelId;
import com.mitv.models.UserLike;
import com.mitv.models.sql.NotificationDataSource;
import com.mitv.ui.elements.FontTextView;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class UserProfileActivity 
	extends BaseContentActivity 
	implements ActivityWithTabs, OnClickListener 
{
	private static final String TAG = UserProfileActivity.class.getName();

	private RelativeLayout aboutContainer;
	private RelativeLayout termsContainer;

	/* Only used when the user is not logged in */
	private LinearLayout signInOrSignUpView;
	private RelativeLayout signUpContainer;
	private RelativeLayout loginContainer;

	/* Only used when the user is logged in */
	private ScrollView scrollView;
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
	
	private boolean isLoggedIn;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_my_profile);

		initLayout();
		
		registerAsListenerForRequest(RequestIdentifierEnum.TV_CHANNEL_IDS_USER_STANDALONE);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_LOGOUT);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_ADD_LIKE);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_REMOVE_LIKE);
		registerAsListenerForRequest(RequestIdentifierEnum.USER_LIKES);
	}
	

	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		
		populateViews();
	}



	@Override
	protected void loadData() 
	{
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();

		if (isLoggedIn) 
		{
			updateUI(UIStatusEnum.LOADING);
			String loadingMessage = getString(R.string.loading_message_user_profile_data);
			setLoadingLayoutDetailsMessage(loadingMessage);
			ContentManager.sharedInstance().getElseFetchFromServiceUserLikes(this, false);
		} 
		else
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
		}
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();

		if (isLoggedIn) 
		{
			return ContentManager.sharedInstance().getFromCacheHasUserLikes();
		}
		else
		{
			return true;
		}
	}

	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch(requestIdentifier)
		{
			case USER_LOGOUT:
			{
				// Do nothing
				break;
			}
	
			case USER_LIKES:
			{
				if (fetchRequestResult.wasSuccessful()) 
				{
					updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
				}
				else
				{
					updateUI(UIStatusEnum.FAILED);
				}
				break;
			}
	
			case TV_GUIDE_INITIAL_CALL:
			{
				if (fetchRequestResult.wasSuccessful()) 
				{
					Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
					intent.putExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_OUT, true);
					startActivity(intent);
				}
				else
				{
					updateUI(UIStatusEnum.FAILED);
				}
	
				break;
			}
	
			default:
			{
				Log.w(TAG, "Unknown request identifier");
				break;
			}
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
				scrollView.setVisibility(View.GONE);
				break;
			}
			
			case NO_CONNECTION_AVAILABLE:
			{
				scrollView.setVisibility(View.GONE);
				break;
			}
	
			case SUCCESS_WITH_NO_CONTENT:
			case SUCCESS_WITH_CONTENT: 
			{
				scrollView.setVisibility(View.VISIBLE);
				updateUserLikesGUI();
				break;
			}
	
			default: 
			{
				Log.w(TAG, "Unhandled UI status.");
				break;
			}
		}
	}

	
	
	private void initLayout() 
	{
		actionBar.setTitle(getResources().getString(R.string.myprofile_title));
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		
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
		
		NotificationDataSource notificationDataSource = new NotificationDataSource(this);

		StringBuilder numberOfNotificationsSB = new StringBuilder();
		numberOfNotificationsSB.append("(");
		numberOfNotificationsSB.append(notificationDataSource.getNotificationCount());
		numberOfNotificationsSB.append(")");

		reminderCountTv.setText(numberOfNotificationsSB.toString());

		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();

		if (isLoggedIn) 
		{
			String userAvatarImageURL = ContentManager.sharedInstance().getFromCacheUserProfileImage();

			ImageAware imageAware = new ImageViewAware(avatarImageView, false);

			SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(userAvatarImageURL, imageAware);

			String userFirstname = ContentManager.sharedInstance().getFromCacheUserFirstname();
			String userLastname = ContentManager.sharedInstance().getFromCacheUserLastname();

			StringBuilder sbUsernameText = new StringBuilder();
			sbUsernameText.append(userFirstname);
			sbUsernameText.append(" ");
			sbUsernameText.append(userLastname);

			userNameTextView.setText(sbUsernameText.toString());
			
			updateUserLikesGUI();

			List<TVChannelId> userChannelIds = ContentManager.sharedInstance().getFromCacheTVChannelIdsUser();
			
			if (userChannelIds != null && !userChannelIds.isEmpty()) 
			{
				StringBuilder userTVChannelIdsSB = new StringBuilder();
				userTVChannelIdsSB.append("(");
				userTVChannelIdsSB.append(userChannelIds.size());
				userTVChannelIdsSB.append(")");
				channelCountTv.setText(userTVChannelIdsSB.toString());
				channelCountTv.setVisibility(View.VISIBLE);
			} 
			else 
			{
				channelCountTv.setVisibility(View.GONE);
			}
		}
	}
	
	private void updateUserLikesGUI() 
	{
		List<UserLike> userLikes = ContentManager.sharedInstance().getFromCacheUserLikes();
		
		if (userLikes != null && !userLikes.isEmpty()) 
		{
			StringBuilder userLikesSB = new StringBuilder();
			userLikesSB.append("(");
			userLikesSB.append(userLikes.size());
			userLikesSB.append(")");
			likesCountTv.setText(userLikesSB.toString());
			likesCountTv.setVisibility(View.VISIBLE);
		} 
		else 
		{
			likesCountTv.setVisibility(View.GONE);
		}
	}

	private void performLogout() {
		/* Important that the user gets the direct feedback when logging out, assume that the logout to the BE succeeds */
		isLoggedIn = false;
		ContentManager.sharedInstance().performLogout(this);
		
		populateViews();
	}
	
	@Override
	public void onClick(View v) {
		/* IMPORTANT to call super so that the BaseActivity can handle the tab clicking */
		super.onClick(v);

		int id = v.getId();

		Intent intent = null;

		switch (id) {
		case R.id.myprofile_likes_container: {
			intent = new Intent(UserProfileActivity.this, LikesActivity.class);
			break;
		}
		case R.id.myprofile_channels_container: {
			intent = new Intent(UserProfileActivity.this, MyChannelsActivity.class);
			break;
		}
		case R.id.myprofile_reminders_container: {
			intent = new Intent(UserProfileActivity.this, RemindersActivity.class);
			break;
		}

		case R.id.myprofile_login_container: {
			intent = new Intent(UserProfileActivity.this, LoginWithMiTVUserActivity.class);
			break;
		}

		case R.id.myprofile_terms_of_use_container: {
			intent = new Intent(UserProfileActivity.this, TermsActivity.class);
			break;
		}

		case R.id.myprofile_about_us_container: {
			intent = new Intent(UserProfileActivity.this, AboutUsActivity.class);
			break;
		}

		case R.id.myprofile_signup_container: {
			intent = new Intent(UserProfileActivity.this, SignUpSelectionActivity.class);
			break;
		}

		case R.id.myprofile_logout_container: {
			performLogout();
			break;
		}
		}

		if (intent != null) {
			startActivity(intent);
		}
	}
}