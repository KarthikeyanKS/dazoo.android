package com.millicom.secondscreen.content.myprofile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;

public class MyProfileFragment extends Fragment {

	private View		mRootView;
	private ProgressBar	mAvatarProgressBar;
	private ImageView	mAvatarImageView;
	private TextView	mUserNameTextView, mRemindersTextView, mLikesTextView, mMyChannelsTextView, mSettingsTextView;
	private ImageView	mRemindersBtn, mLikesBtn, mMyChannelsBtn, mSettingsBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		// get the parcelable profile info in a bundle

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_me_fragment, container, false);

		// user information
		mAvatarImageView = (ImageView) mRootView.findViewById(R.id.mepage_avatar_iv);
		mAvatarProgressBar = (ProgressBar) mRootView.findViewById(R.id.mepage_avatar_progressbar);
		mUserNameTextView = (TextView) mRootView.findViewById(R.id.mepage_name_tv);

		// reminders
		mRemindersTextView = (TextView) mRootView.findViewById(R.id.mepage_reminders_title_tv);
		mRemindersBtn = (ImageView) mRootView.findViewById(R.id.mepage_reminders_button_iv);
		mRemindersBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), RemindersActivity.class);
				startActivityForResult(intent, 1);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});

		// likes
		mLikesTextView = (TextView) mRootView.findViewById(R.id.mepage_likes_title_tv);
		mLikesBtn = (ImageView) mRootView.findViewById(R.id.mepage_likes_button_iv);
		mLikesBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LikesActivity.class);
				startActivityForResult(intent, 1);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

			}
		});

		// my channels
		mMyChannelsTextView = (TextView) mRootView.findViewById(R.id.mepage_my_channels_title_tv);
		mMyChannelsBtn = (ImageView) mRootView.findViewById(R.id.mepage_my_channels_button_iv);
		mMyChannelsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyChannelsActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

			}
		});

		// settings
		mSettingsTextView = (TextView) mRootView.findViewById(R.id.mepage_settings_title_tv);
		mSettingsBtn = (ImageView) mRootView.findViewById(R.id.mepage_settings_button_iv);
		mSettingsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

			}
		});

		mAvatarImageView.setImageResource(R.drawable.loadimage_2x);
		mUserNameTextView.setText("Erik Per Sven Ericsson");

		return mRootView;
	}

	// update the layout if any changes happend in the detail activity
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == Consts.INFO_UPDATE_REMINDERS) {
				// change the reminders counter
			}
			else if (resultCode == Consts.INFO_UPDATE_LIKES) {
				// change the likes counter
			}
			else if(resultCode == Consts.INFO_NO_UPDATE_REMINDERS){
				// no change in reminders quantity
			}
			else if(resultCode == Consts.INFO_NO_UPDATE_LIKES){
				// no change in likes quantity
			}
		}
	}

}
