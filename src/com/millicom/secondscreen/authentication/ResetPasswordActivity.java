package com.millicom.secondscreen.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.JSONUtilities;
import com.millicom.secondscreen.utilities.PatternCheck;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswordActivity extends ActionBarActivity implements OnClickListener {

	private static final String	TAG	= "ResetPasswordActivity";
	private ActionBar			mActionBar;
	private Button				mDazooResetPassword;
	private EditText			mEmailResetPasswordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_resetpassword_activity);
		initViews();
	}

	private void initViews() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		final int actionBarColor = getResources().getColor(R.color.lightblue);
		mActionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mActionBar.setTitle(getResources().getString(R.string.reset_password));

		mDazooResetPassword = (Button) findViewById(R.id.resetpassword_button);
		mDazooResetPassword.setOnClickListener(this);
		mEmailResetPasswordEditText = (EditText) findViewById(R.id.resetpassword_email_edittext);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.resetpassword_button:
			String emailInput = mEmailResetPasswordEditText.getText().toString();
			// if (emailInput != null && emailInput.isEmpty() != true && PatternCheck.checkEmail(emailInput) == true) {
			if (emailInput != null && TextUtils.isEmpty(emailInput) != true && PatternCheck.checkEmail(emailInput) == true) {
				mEmailResetPasswordEditText.setEnabled(false);
				try {
					ResetPasswordTask resetPasswordTask = new ResetPasswordTask();
					int responseCode = resetPasswordTask.execute(emailInput).get();
					Log.d(TAG,"responseCode: " + responseCode);
					if (Consts.GOOD_RESPONSE_RESET_PASSWORD == responseCode) {
						Toast.makeText(getApplicationContext(), "The password is successfully reset. Check your mailbox!", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "Password is reset");
						
						Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordFinalActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
						
					} else if (Consts.BAD_RESPONSE == responseCode) {
						Toast.makeText(getApplicationContext(), "Error! Email is not found!", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "Error! Reset password : level backend");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else {
				mEmailResetPasswordEditText.setEnabled(true);
				Toast.makeText(getApplicationContext(), "Please enter a valid e-mail address", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Email input is required");
			}
			break;
		}
	}

	private class ResetPasswordTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Consts.MILLICOM_SECONDSCREEN_RESET_PASSWORD_URL);

				JSONObject holder = JSONUtilities.createJSONObjectWithKeysValues(Arrays.asList(Consts.MILLICOM_SECONDSCREEN_API_EMAIL), Arrays.asList(params[0]));
				StringEntity entity = new StringEntity(holder.toString());

				httpPost.setEntity(entity);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");

				HttpResponse response = client.execute(httpPost);
				return response.getStatusLine().getStatusCode();
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
