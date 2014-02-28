
package com.millicom.mitv.activities.authentication;



import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.utilities.ToastHelper;
import com.mitv.Consts;
import com.mitv.R;



public class LoginWithFacebookActivity 
	extends BaseActivity 
{
	private static final String TAG = LoginWithFacebookActivity.class.getName();

	private static final int AJAX_STATUS_OK = 200;
	private static final int AJAX_STATUS_ERROR_400 = 400;
	private static final int AJAX_STATUS_ERROR_401 = 401;
	private static final int AJAX_STATUS_ERROR_403 = 403;
	
	
	private ActionBar actionBar;
	

	
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
		String facebookToken = FacebookHandle.getToken(LoginWithFacebookActivity.this);
		
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
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{	
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
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
			
			case SUCCEEDED_WITH_DATA:
			{
				Intent intent = new Intent(LoginWithFacebookActivity.this, getMostRecentTabActivity().getClass());

				intent.putExtra(Consts.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, true);

				startActivity(intent);
				
				finish();
				
				break;
			}
	
			case FAILED:
			default:
			{
				// TODO - Hardcoded string
				String message = "Facebook login was unsuccessful.";
				
				ToastHelper.createAndShowLikeToast(this, message);
				
				Intent intent = new Intent(LoginWithFacebookActivity.this, getMostRecentTabActivity().getClass());

				startActivity(intent);
				
				finish();
				
				break;
			}
		}
	}
	
	
	
	
	private FacebookHandle getFacebookHandle()
	{
		FacebookHandle facebookHandle = new FacebookHandle(this, Consts.APP_FACEBOOK_ID, Consts.APP_FACEBOOK_PERMISSIONS) 
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
			handle.sso(Consts.APP_FACEBOOK_SSO);
			
			AQuery aq = new AQuery(this);
	
			AQuery aquery = aq.auth(handle);
			
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
			
			aquery.ajax(Consts.APP_URL_FACEBOOK_GRAPH_ME, JSONObject.class, callback);
		}
		else
		{
			Log.e(TAG, "Facebook handle is null.");
		}
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode) 
		{
			case Consts.APP_FACEBOOK_SSO:
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
}