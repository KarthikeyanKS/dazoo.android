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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.JSONUtilities;

public class MyProfileFragment extends Fragment {

	private static final String	TAG	= "MyProfileFragment";

	private View				mRootView;
	private ProgressBar			mAvatarProgressBar;
	private ImageView			mAvatarImageView;
	private TextView			mUserNameTextView, mRemindersTextView, mLikesTextView, mMyChannelsTextView, mSettingsTextView;
	private ImageView			mRemindersBtn, mLikesBtn, mMyChannelsBtn, mSettingsBtn;
	private Activity			mActivity;
	private ArrayList<String>	mMyChannelsIds;
	private ArrayList<String> mChannelIdsListToBeAdded;
	private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		// get the parcelable profile info in a bundle

		mActivity = getActivity();
		context = mActivity.getApplicationContext();
		String userToken = ((SecondScreenApplication) context).getAccessToken();

		// check if we have registered user and user token is valid
		if (userToken != null && userToken.isEmpty() != true) {
			// get user channels
			GetMyChannelsTask getMyChannelsTask = new GetMyChannelsTask();
			getMyChannelsTask.execute(userToken);
		}
		
		// add channels to the user account "My channels" database
		if(userToken != null && userToken.isEmpty() != true){
			AddChannelToMyChannelsTask addChannelToMyChannelsTask = new AddChannelToMyChannelsTask();
			
			//fake list of channelIds to be added
			mChannelIdsListToBeAdded = new ArrayList<String>();
			mChannelIdsListToBeAdded.add("98c9c7cb-76de-4ad8-b6cd-021e7b927ba7");
			mChannelIdsListToBeAdded.add("ba09d322-6164-4457-89c0-64520214ac30");
			
			String channelsJSON = JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID, mChannelIdsListToBeAdded);
			addChannelToMyChannelsTask.execute(userToken, channelsJSON);
		}
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

	// fetch the "My channels" of the logged in user
	private class GetMyChannelsTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet();
				String userToken = params[0];
				String urlWithToken = Consts.MILLICOM_SECONDSCREEN_MY_CHANNELS_URL + Consts.REQUEST_PARAMETER_SEPARATOR;
				List<NameValuePair> urlParams = new LinkedList<NameValuePair>();
				urlParams.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_TOKEN, userToken));

				String urlParamsString = URLEncodedUtils.format(urlParams, "utf-8");
				urlWithToken += urlParamsString;

				Log.d(TAG, "Get My Channels request url:" + urlWithToken);

				httpGet.setURI(new URI(urlWithToken));
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == Consts.GOOD_RESPONSE) {
					HttpEntity entityHttp = response.getEntity();
					InputStream inputStream = entityHttp.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					inputStream.close();
					// JSONObject jObj = new JSONObject(sb.toString());
					JSONArray jArray = new JSONArray(sb.toString());
					if (jArray != null) {
						mMyChannelsIds = new ArrayList<String>();
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject channelIdJSON = jArray.getJSONObject(i);
							mMyChannelsIds.add(channelIdJSON.getString(Consts.MILLICOM_SECONDSCREEN_API_CHANNEL_ID));
							Log.d(TAG, "ChannelId json: " + channelIdJSON.toString());
						}
					}
					return true;
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}
	}

	// add the channel to the "My channel"
	private class AddChannelToMyChannelsTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			try {
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

				HttpClient client = new DefaultHttpClient();

				SchemeRegistry registry = new SchemeRegistry();
				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme(Consts.MILLICON_SECONDSCREEN_HTTP_SCHEME, socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

				// Set verifier
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_MY_CHANNELS_URL);
				httpPost.setHeader("Content-type", "application/json; charset=utf-8");
				httpPost.setHeader("Accept", "application/json");

				String token = params[0];
				String myChannelsJSON = params[1];

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);
				nameValuePair.add(new BasicNameValuePair(Consts.MILLICOM_SECONDSCREEN_API_TOKEN, token));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

				StringEntity jsonEntity = new StringEntity(myChannelsJSON);
				httpPost.setEntity(jsonEntity);

				HttpResponse response = httpClient.execute(httpPost);

				return response.getStatusLine().getStatusCode();
			} catch (ClientProtocolException e) {
				System.out.println("CPE" + e);
			} catch (IOException e) {
				System.out.println("IOE" + e);
			}
			return 0;
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
