
package com.mitv.activities.authentication;



import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.ui.helpers.ToastHelper;



public class LoginWithFacebookActivity 
	extends BaseActivity 
{
	private static final String TAG = LoginWithFacebookActivity.class.getName();

	private static final int AJAX_STATUS_OK = 200;
	private static final int AJAX_STATUS_ERROR_400 = 400;
	private static final int AJAX_STATUS_ERROR_401 = 401;
	private static final int AJAX_STATUS_ERROR_403 = 403;
	
	
	private ActionBar actionBar;
	
	private String facebookToken;
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_facebooklogin_activity);
		
		actionBar = getSupportActionBar();
		
		actionBar.hide();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		performFacebookAuthentication();
	}
	
	

	@Override
	protected void loadData() 
	{
		facebookToken = FacebookHandle.getToken(LoginWithFacebookActivity.this);
		
		if(facebookToken != null)
		{
			updateUI(UIStatusEnum.LOADING);
			
			ContentManager.sharedInstance().getUserTokenWithFacebookFBToken(this, facebookToken);
		}
		else
		{
			updateUI(UIStatusEnum.FAILED);
			
			Log.e(TAG, "Facebook token was null.");
		}
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return false;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{	
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
		}
		else
		{
			updateUI(UIStatusEnum.FAILED);
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
				// Do nothing
				break;
			}
			
			case SUCCESS_WITH_CONTENT:
			{
				if(!ContentManager.sharedInstance().tryStartReturnActivity(this)) {
					Activity mostRecentTabActivity = getMostRecentTabActivity();
					Intent intent = new Intent(LoginWithFacebookActivity.this, mostRecentTabActivity.getClass());
					intent.putExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, true);
					startActivity(intent);
				} else {
					// TODO NewArc: Do we need to do something here???
				}
				
				finish();
				
				break;
			}
	
			case FAILED:
			default:
			{
				// TODO User Feedback - Hardcoded string for user message
				String message = "Facebook login was unsuccessful.";
				
				ToastHelper.createAndShowLikeToast(this, message);
				
				Intent intent = new Intent(LoginWithFacebookActivity.this, getMostRecentTabActivity().getClass());

				startActivity(intent);
				
				finish();
				
				break;
			}
		}
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode) 
		{
			case Constants.APP_FACEBOOK_SSO:
			{
				FacebookHandle handle = getFacebookHandle();
				
				if (handle != null) 
				{
					handle.onActivityResult(requestCode, resultCode, data);
				}
				else
				{
					Log.e(TAG, "Facebook handle is null.");
				}
				
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled request code: " + requestCode);
				
				break;
			}
		}
	}
	
	
	
	private FacebookHandle getFacebookHandle()
	{
		FacebookHandle facebookHandle = new FacebookHandle(this, Constants.APP_FACEBOOK_ID, Constants.APP_FACEBOOK_PERMISSIONS) 
		{
			@Override
			public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) 
			{
				int statusCode = status.getCode();

				switch (statusCode) 
				{
					case AJAX_STATUS_ERROR_400:
					case AJAX_STATUS_ERROR_401:
					case AJAX_STATUS_ERROR_403:
					{
						return true;
					}
	
					default:
					{
						break;
					}
				}

				return super.expired(cb, status);
			}


			@Override
			public boolean reauth(final AbstractAjaxCallback<?, ?> cb) 
			{
				return super.reauth(cb);
			}
		};

		return facebookHandle;
	}
	
	

	
	private void performFacebookAuthentication() 
	{
		FacebookHandle handle = getFacebookHandle();

		if(handle != null)
		{
			handle.sso(Constants.APP_FACEBOOK_SSO);
			
			AQuery aq = new AQuery(this);
	
			AQuery aquery = aq.auth(handle);
			
			
			
			aquery.ajax(Constants.APP_URL_FACEBOOK_GRAPH_ME, JSONObject.class, getFacebookAuthenticationCallback());
		}
		else
		{
			Log.e(TAG, "Facebook handle is null.");
		}
	}
	
	
	
	private AjaxCallback<JSONObject> getFacebookAuthenticationCallback()
	{
		AjaxCallback<JSONObject> callback = new AjaxCallback<JSONObject>()
		{
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) 
			{	
				int statusCode = status.getCode();

				switch(statusCode)
				{
					case AJAX_STATUS_OK:
					{
						loadData();
						break;
					}
	
					default:
					{
						Log.w(TAG, "Unhandled status code code: " + statusCode);
	
						updateUI(UIStatusEnum.FAILED);
						break;
					}
				}
			}


			@Override
			public void failure(int code, String message) 
			{
				Log.d(TAG, "Authorization was canceled by user");

				updateUI(UIStatusEnum.FAILED);
			}
		};
		
		return callback;
	}
}