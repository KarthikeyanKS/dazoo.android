package com.millicom.secondscreen.content.myprofile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.annotation.TargetApi;

import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.authentication.DazooLoginActivity;
import com.millicom.secondscreen.authentication.FacebookLoginActivity;
import com.millicom.secondscreen.authentication.SignUpActivity;
import com.millicom.secondscreen.utilities.JSONUtilities;

public class MyProfileFragment extends Fragment {

	private static final String	TAG			= "MyProfileFragment";

	private View				mRootView;
	private RelativeLayout		mRemindersContainer, mLikesContainer, mMyChannelsContainer, mSettingsContainer, mMyProfileContainer, mSigninContainer, 
								mFacebookContainer, mSignUpContainer;
	private ProgressBar			mAvatarProgressBar;
	private ImageView			mAvatarImageView;
	private TextView			mUserNameTextView, mRemindersTextView, mLikesTextView, mMyChannelsTextView, mSettingsTextView, mRemindersCountTextView, 
								mLikesCountTextView, mMyChannelsCountTextView;
	private Button mLoginBtn;
	private String				userFirstName, userLastName;
	private boolean				mIsLoggeIn	= false;
	private String				mToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRetainInstance(true);

		mToken = ((SecondScreenApplication) getActivity().getApplicationContext()).getAccessToken();
		if (mToken != null && mToken.isEmpty() != true) {
			mIsLoggeIn = true;

			userFirstName = ((SecondScreenApplication) getActivity().getApplicationContext()).getUserFirstName();
			userLastName = ((SecondScreenApplication) getActivity().getApplicationContext()).getUserLastName();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_me_fragment, container, false);

		mMyProfileContainer = (RelativeLayout) mRootView.findViewById(R.id.mepage_person_container);

		// user information
		mAvatarImageView = (ImageView) mRootView.findViewById(R.id.mepage_avatar_iv);
		mAvatarProgressBar = (ProgressBar) mRootView.findViewById(R.id.mepage_avatar_progressbar);
		mUserNameTextView = (TextView) mRootView.findViewById(R.id.mepage_name_tv);

		// reminders
		mRemindersContainer = (RelativeLayout) mRootView.findViewById(R.id.mepage_reminders_container);
		mRemindersTextView = (TextView) mRootView.findViewById(R.id.mepage_reminders_title_tv);
		mRemindersCountTextView = (TextView) mRootView.findViewById(R.id.mepage_reminders_button_tv);
		mRemindersContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), RemindersActivity.class);
				startActivityForResult(intent, 1);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});

		// likes
		mLikesContainer = (RelativeLayout) mRootView.findViewById(R.id.mepage_likes_container);
		mLikesTextView = (TextView) mRootView.findViewById(R.id.mepage_likes_title_tv);
		mLikesCountTextView = (TextView) mRootView.findViewById(R.id.mepage_likes_button_tv);
		mLikesContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LikesActivity.class);
				startActivityForResult(intent, 1);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

			}
		});

		// my channels
		mMyChannelsContainer = (RelativeLayout) mRootView.findViewById(R.id.mepage_my_channels_container);
		mMyChannelsTextView = (TextView) mRootView.findViewById(R.id.mepage_my_channels_title_tv);
		mMyChannelsCountTextView = (TextView) mRootView.findViewById(R.id.mepage_my_channels_button_tv);
		mMyChannelsContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyChannelsActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

			}
		});

		// settings
		mSettingsContainer = (RelativeLayout) mRootView.findViewById(R.id.mepage_settings_container);
		mSettingsTextView = (TextView) mRootView.findViewById(R.id.mepage_settings_title_tv);
		mSettingsContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

			}
		});

		// sign in 
		mSigninContainer = (RelativeLayout) mRootView.findViewById(R.id.mepage_signin_container);
		mFacebookContainer = (RelativeLayout) mRootView.findViewById(R.id.mepage_signin_facebook_container);
		mFacebookContainer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FacebookLoginActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				
			}
		});
		
		mSignUpContainer = (RelativeLayout) mRootView.findViewById(R.id.mepage_signup_email_container);
		mSignUpContainer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SignUpActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);			
			}
		});
		
		
		mLoginBtn = (Button) mRootView.findViewById(R.id.mepage_login_btn);
		mLoginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), DazooLoginActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);			
			}
		});
		
		populateViews();

		return mRootView;
	}

	private void populateViews() {
		if (mIsLoggeIn) {
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

	// update the layout if any changes happend in the detail activity
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == Consts.INFO_UPDATE_REMINDERS) {
				// change the reminders counter
			} else if (resultCode == Consts.INFO_UPDATE_LIKES) {
				// change the likes counter
			} else if (resultCode == Consts.INFO_NO_UPDATE_REMINDERS) {
				// no change in reminders quantity
			} else if (resultCode == Consts.INFO_NO_UPDATE_LIKES) {
				// no change in likes quantity
			}
		}
	}

}
