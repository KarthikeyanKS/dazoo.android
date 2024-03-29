
package com.mitv.activities.authentication;



import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.GenericUtils;



public class LoginWithFacebookActivity 
	extends BaseActivity 
{
	private static final String TAG = LoginWithFacebookActivity.class.getName();

	private static final int AJAX_STATUS_OK = 200;
	
	
	private ActionBar actionBar;
	
	private String facebookToken;
	private boolean loginResponseHandled;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_facebooklogin_activity);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		loginResponseHandled = false;
		
		boolean isFacebookAppInstalled = GenericUtils.isFacebookAppInstalled();
		
		boolean isMinimumRequiredFacebookAppInstalled = GenericUtils.isMinimumRequiredFacebookAppInstalled();
		
		if(isFacebookAppInstalled && isMinimumRequiredFacebookAppInstalled == false)
		{
			String message = getString(R.string.update_facebook_app);
			
			ToastHelper.createAndShowShortToast(message);
			
			Intent intent = new Intent(LoginWithFacebookActivity.this, getMostRecentTabActivity().getClass());

			startActivity(intent);
			
			finish();
		}
		else
		{
			performFacebookAuthentication();
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		return true;
	}
	
	

	@Override
	protected void loadData() 
	{
		facebookToken = FacebookHandle.getToken(LoginWithFacebookActivity.this);
		
		if(facebookToken != null)
		{
			updateUI(UIStatusEnum.LOADING);
			String loadingMessage = getString(R.string.loading_message_facebook_login);
			setLoadingLayoutDetailsMessage(loadingMessage);
			ContentManager.sharedInstance().getUserTokenWithFacebookFBToken(this, facebookToken);
		}
		else
		{
			updateUI(UIStatusEnum.FAILED);
			
			Log.e(TAG, "Facebook token was null.");
		}
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
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
				if(!ContentManager.sharedInstance().tryStartReturnActivity(this) && !loginResponseHandled) 
				{
					String activityToReturnAfterLogin = getIntent().getStringExtra(Constants.INTENT_EXTRA_ACTIVITY_TO_RETURN_AFTER_LOGIN);
					
					Class<?> activityClassToReturn;
					
					if(activityToReturnAfterLogin != null)
					{
						try 
						{
							activityClassToReturn = Class.forName(activityToReturnAfterLogin);
						} 
						catch (ClassNotFoundException cnfex) 
						{
							Log.w(TAG, cnfex.getMessage());
							
							activityClassToReturn = HomeActivity.class;
						}
					}
					else
					{
						Activity mostRecentTabActivity = getMostRecentTabActivity();
						
						if(mostRecentTabActivity != null)
						{
							activityClassToReturn = mostRecentTabActivity.getClass();
						}
						else
						{
							activityClassToReturn = HomeActivity.class;
						}
					}
					
					Intent intent = new Intent(LoginWithFacebookActivity.this, activityClassToReturn);
					
					final Bundle bundle = getIntent().getExtras();
					
					if(bundle != null)
					{
						intent.putExtras(bundle);
						intent.removeExtra(Constants.INTENT_EXTRA_ACTIVITY_TO_RETURN_AFTER_LOGIN);
					}
					
					intent.putExtra(Constants.INTENT_EXTRA_ACTIVITY_USER_JUST_LOGGED_IN, true);
					
					startActivity(intent);
				}
				
				loginResponseHandled = true;
				
				finish();
				
				break;
			}
	
			case FAILED:
			default:
			{
				String message = getString(R.string.facebook_login_failed);
				
				ToastHelper.createAndShowShortToast(message);
				
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
	
	
	
	public static FacebookHandle getDefaultFacebookHandle(Activity activity)
	{
		FacebookHandle facebookHandle = new FacebookHandle(activity, Constants.APP_FACEBOOK_ID, Constants.APP_FACEBOOK_PERMISSIONS);
		
		return facebookHandle;
	}
	
	
	
	private FacebookHandle getFacebookHandle()
	{
		FacebookHandle facebookHandle = new FacebookHandle(this, Constants.APP_FACEBOOK_ID, Constants.APP_FACEBOOK_PERMISSIONS) 
		{
			@Override
			protected synchronized void failure(Context context, int code, String message)
			{
				updateUI(UIStatusEnum.FAILED);
				
				super.failure(context, code, message);
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