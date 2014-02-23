
package com.millicom.mitv.activities.authentication;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.utilities.RegularExpressionUtils;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.utilities.JSONUtilities;



public class ResetPasswordActivity 
	extends SignInBaseActivity 
	implements OnClickListener
{
	private static final String TAG = ResetPasswordActivity.class.getName();
	
	
	private ActionBar			mActionBar;
	private Button				mMiTVResetPassword;
	private EditText			mEmailResetPasswordEditText;
	private TextView			mErrorTextView;
	private String 				mBadResponseString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_resetpassword_activity);
		

		// add the activity to the list of running activities
		SecondScreenApplication.sharedInstance().getActivityList().add(this);

		initViews();
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				// TODO NewArc - Do something here?
				break;
			}
	
			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}
	}

	
	
	@Override
	protected void loadData() 
	{
		// TODO NewArc - Do something here?
	}
	
	

	private void initViews() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mActionBar.setTitle(getResources().getString(R.string.reset_password));

		mMiTVResetPassword = (Button) findViewById(R.id.resetpassword_button);
		mMiTVResetPassword.setOnClickListener(this);
		mEmailResetPasswordEditText = (EditText) findViewById(R.id.resetpassword_email_edittext);
		mErrorTextView = (TextView) findViewById(R.id.resetpassword_error_tv);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.resetpassword_button:
			String emailInput = mEmailResetPasswordEditText.getText().toString();
			// if (emailInput != null && emailInput.isEmpty() != true && PatternCheck.checkEmail(emailInput) == true) {
			if (emailInput != null && TextUtils.isEmpty(emailInput) != true && RegularExpressionUtils.checkEmail(emailInput) == true) {
				mEmailResetPasswordEditText.setEnabled(false);
				try {
					ResetPasswordTask resetPasswordTask = new ResetPasswordTask();
					int responseCode = resetPasswordTask.execute(emailInput).get();
					Log.d(TAG, "responseCode: " + responseCode);
					if (Consts.GOOD_RESPONSE_RESET_PASSWORD == responseCode || Consts.GOOD_RESPONSE == responseCode) {
						//Toast.makeText(getApplicationContext(), "The password is successfully reset. Check your mailbox!", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "Password is reset");

						Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordFinalActivity.class);
						startActivity(intent);
						finish();

					} else if (Consts.BAD_RESPONSE == responseCode) {
						//Toast.makeText(getApplicationContext(), "Error! Email is not found!", Toast.LENGTH_SHORT).show();
						if (Consts.BAD_RESPONSE_STRING_EMAIL_NOT_FOUND.equals(mBadResponseString)) {
							mErrorTextView.setText(getResources().getString(R.string.reset_password_email_not_found));
						}
						mEmailResetPasswordEditText.setEnabled(true);
						Log.d(TAG, "Bad response: " + mBadResponseString);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else {
				mEmailResetPasswordEditText.setEnabled(true);
				//Toast.makeText(getApplicationContext(), "Please enter a valid e-mail address", Toast.LENGTH_SHORT).show();
				mErrorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_incorrect));
				Log.d(TAG, "Email input is required");
			}
			break;
		}
	}

	private class ResetPasswordTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			try {
				// HttpClient client = new DefaultHttpClient();
				HttpClient client = new DefaultHttpClient();
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

				SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				socketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				registry.register(new Scheme("https", socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);

				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
				// Set verifier
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
				HttpPost httpPost = new HttpPost(Consts.URL_RESET_PASSWORD_SEND_EMAIL);

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.API_EMAIL), Arrays.asList(params[0]));
				StringEntity entity = new StringEntity(holder.toString());

				httpPost.setEntity(entity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				// HttpResponse response = client.execute(httpPost);
				HttpResponse response = httpClient.execute(httpPost);
				
				int responseCode = response.getStatusLine().getStatusCode();
				if (responseCode == Consts.GOOD_RESPONSE_RESET_PASSWORD ||  responseCode == Consts.GOOD_RESPONSE) {
					return responseCode;
				}
				else if (responseCode == Consts.BAD_RESPONSE) {
					HttpEntity httpentity = response.getEntity();
					mBadResponseString = EntityUtils.toString(httpentity);
					return responseCode;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Consts.BAD_RESPONSE;
		}
	}

}
