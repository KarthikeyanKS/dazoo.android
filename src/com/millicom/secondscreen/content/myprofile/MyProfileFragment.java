package com.millicom.secondscreen.content.myprofile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.ImageDownloadThread;

public class MyProfileFragment extends Fragment {

	private View		mRootView;
	private ProgressBar	mAvatarProgressBar;
	private ImageView	mAvatarImageView;
	private TextView	mNameTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		// get the parcelable profile info in a bundle

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_me, container, false);

		mAvatarImageView = (ImageView) mRootView.findViewById(R.id.mepage_avatar_iv);
		mAvatarProgressBar = (ProgressBar) mRootView.findViewById(R.id.mepage_avatar_progressbar);
		mNameTextView = (TextView) mRootView.findViewById(R.id.mepage_name_tv);

		// if ( avatarUrl != null) {
		// ImageDownloadThread getavatar = new ImageDownloadThread(mAvatarImageView, mAvatarProgressBar);
		// getavatar.execute(avatarUrl);
		// } else {
		mAvatarImageView.setImageResource(R.drawable.loadimage_2x);
		// }

		mNameTextView.setText("Erik Per Sven Ericsson");

		return mRootView;
	}

}
