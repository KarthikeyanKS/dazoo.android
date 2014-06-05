package com.mitv.activities;

import org.jraf.android.backport.switchwidget.Switch;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ActivityWithTabs;
import com.mitv.managers.ContentManager;
import com.mitv.ui.elements.FontTextView;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class UserProfileConfigurationActivity extends BaseActivity implements ActivityWithTabs, OnCheckedChangeListener {

	private static final String TAG = UserProfileConfigurationActivity.class.getName();
	
	private ImageView avatarImageView;
	private FontTextView nameText;
	private FontTextView emailText;
	private Switch socialSwitchView;
	private Switch notificationsSwitchView;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_user_configuration);

		initLayout();
		
		populateViews();
	}

	
	
	private void initLayout() 
	{
		actionBar.setTitle(getString(R.string.user_profile_config_title));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		avatarImageView = (ImageView) findViewById(R.id.user_config_avatar_image);
		nameText = (FontTextView) findViewById(R.id.user_config_edit_name);
		emailText = (FontTextView) findViewById(R.id.user_config_edit_email);
		
		socialSwitchView = (Switch) findViewById(R.id.user_config_share_switch);
		socialSwitchView.setOnCheckedChangeListener(this);
		notificationsSwitchView = (Switch) findViewById(R.id.user_config_notifications_switch);
		notificationsSwitchView.setOnCheckedChangeListener(this);
	}

	
	
	private void populateViews() {
		/* Image */
		ImageAware imageAware = new ImageViewAware(avatarImageView, false);
		String userAvatarImageURL = ContentManager.sharedInstance().getFromCacheUserProfileImage();
		SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(userAvatarImageURL, imageAware);
		
		/* HIDE image until we have backend support */
		avatarImageView.setVisibility(View.GONE);
		
		/* HIDE everything regarding share until we have backend support */
		LinearLayout shareContainer = (LinearLayout) findViewById(R.id.user_config_share_container);
		shareContainer.setVisibility(View.GONE);
		
		RelativeLayout shareSeparatorBar = (RelativeLayout) findViewById(R.id.user_config_share_bar_container);
		shareSeparatorBar.setVisibility(View.GONE);
		
		/* Name */
		String userFirstname = ContentManager.sharedInstance().getFromCacheUserFirstname();
		String userLastname = ContentManager.sharedInstance().getFromCacheUserLastname();

		StringBuilder sbUsernameText = new StringBuilder();
		sbUsernameText.append(userFirstname);
		sbUsernameText.append(" ");
		sbUsernameText.append(userLastname);

		nameText.setText(sbUsernameText.toString());
		
		/* Email */
		String userEmail = ContentManager.sharedInstance().getFromCacheUserEmail();
		emailText.setText(userEmail);

	}

	@Override
	protected void updateUI(UIStatusEnum status) {
		super.updateUIBaseElements(status);

		switch (status) {
		case SUCCESS_WITH_CONTENT: {
			break;
		}
		default: {
			break;
		}
		}

	}

	@Override
	protected void loadData() {
	}

	@Override
	protected boolean hasEnoughDataToShowContent() {
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();
		return isLoggedIn;
	}

	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		// TODO Auto-generated method stub

	}
	
	

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int viewId = buttonView.getId();
		switch (viewId) {
		case R.id.user_config_notifications_switch: {
			//TODO store settings to Backend
			Log.d(TAG, String.format("Notifications - checked: %s", isChecked ? "TRUE" : "FALSE"));
			break;
		}
		case R.id.user_config_share_switch: {
			//TODO store settings to Backend
			Log.d(TAG, String.format("Share - checked: %s", isChecked ? "TRUE" : "FALSE"));
			break;
		}
		default: {
			break;
		}
		}
		
	}
}