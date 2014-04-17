package com.mitv.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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

public class UserProfileConfigurationActivity extends BaseActivity implements ActivityWithTabs, OnClickListener {

	private ImageView avatarImageView;
	private FontTextView nameText;
	private FontTextView emailText;
	private View socialSwitchView;
	private View notificationsSwitchView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_user_configuration);

		initLayout();
		
		populateViews();
	}

	private void initLayout() {
		actionBar.setTitle(getString(R.string.user_profile_config_title));
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		avatarImageView = (ImageView) findViewById(R.id.user_config_avatar_image);
		nameText = (FontTextView) findViewById(R.id.user_config_edit_name);
		emailText = (FontTextView) findViewById(R.id.user_config_edit_email);
		
		socialSwitchView = findViewById(R.id.user_config_share_switch);
		notificationsSwitchView = findViewById(R.id.user_config_notifications_switch);
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
}