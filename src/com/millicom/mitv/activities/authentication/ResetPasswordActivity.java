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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseLoginActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.utilities.RegularExpressionUtils;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.utilities.JSONUtilities;

public class ResetPasswordActivity extends BaseLoginActivity implements
		OnClickListener {
	private static final String TAG = ResetPasswordActivity.class.getName();

	private Button miTVResetPassword;
	private EditText emailResetPasswordEditText;
	private TextView errorTextView;
	
	
	private String badResponseString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_resetpassword_activity);

		initViews();
	}

	@Override
	protected void loadData() {
		
		String email = emailResetPasswordEditText.getText().toString();
		if(!TextUtils.isEmpty(email) && RegularExpressionUtils.checkEmail(email)) {
			emailResetPasswordEditText.setEnabled(false); //TODO NewArc do we need to disable the edit text field???
			ContentManager.sharedInstance().performResetPassword(this, email);
		} else {
			/* Password is either null, empty or invalid */
			emailResetPasswordEditText.setEnabled(true); //TODO NewArc do we need to disable/reenable the edit text field???
			
			errorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_incorrect));
		}
	}

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult,
			RequestIdentifierEnum requestIdentifier) {
		if (fetchRequestResult.wasSuccessful()) {
			Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordFinalActivity.class);
			startActivity(intent);
		} else {
			updateUI(UIStatusEnum.FAILED);
		}
	}

	@Override
	protected void updateUI(UIStatusEnum status) {
		super.updateUIBaseElements(status);

		switch (status) {
		case SUCCEEDED_WITH_DATA: {
			// TODO NewArc - Do something here?
			break;
		}
		case FAILED: {
			errorTextView.setText(getResources().getString(R.string.signup_with_email_error_email_incorrect));
			break;
		}

		default: {
			// TODO NewArc - Do something here?
			break;
		}
		}
	}

	private void initViews() {
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.reset_password));

		miTVResetPassword = (Button) findViewById(R.id.resetpassword_button);
		miTVResetPassword.setOnClickListener(this);
		emailResetPasswordEditText = (EditText) findViewById(R.id.resetpassword_email_edittext);

		errorTextView = (TextView) findViewById(R.id.resetpassword_error_tv);
	}

	
	
	@Override
	public void onClick(View v) {
		/*
		 * IMPORTANT always call super.onClick for subclasses of BaseActivity,
		 * else tabs wont work!
		 */
		super.onClick(v);
		int id = v.getId();

		switch (id) {
		case R.id.resetpassword_button:
			loadData();
			break;
		}
	}


}
