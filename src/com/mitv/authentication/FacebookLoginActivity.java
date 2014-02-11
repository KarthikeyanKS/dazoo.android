
package com.mitv.authentication;



import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.model.GraphUser;
import com.facebook.SessionState;
import com.millicom.asynctasks.FacebookLoginTask;
import com.mitv.Consts;
import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.content.activity.ActivityActivity;
import com.mitv.homepage.HomeActivity;
import com.mitv.manager.ContentManager;
import com.mitv.R;
import com.mitv.SecondScreenApplication;



public class FacebookLoginActivity 
	extends SSSignInSignupBaseActivity 
{
	private static final String	TAG	= "FacebookLoginActivity";
	
	
	private ActionBar				mActionBar;
	private boolean 				mIsFromActivity;
	
	private Session.StatusCallback 	statusCallback;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_facebooklogin_activity);

		Intent intent = getIntent();
		
		if (intent.hasExtra(Consts.INTENT_EXTRA_FROM_ACTIVITY)) 
		{
			mIsFromActivity = intent.getExtras().getBoolean(Consts.INTENT_EXTRA_FROM_ACTIVITY);
		}

		// add the activity to the list of running activities
		// TODO: Why is this needed?
		SecondScreenApplication.getInstance().getActivityList().add(this);

		initViews();

		// generation of the ssh key for the facebook
//		PackageInfo info;
//		try {
//			info = getPackageManager().getPackageInfo("com.mitv", PackageManager.GET_SIGNATURES);
//			for (Signature signature : info.signatures) {
//				MessageDigest md = MessageDigest.getInstance("SHA");
//				md.update(signature.toByteArray());
//				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//			}
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
		
		statusCallback = new Session.StatusCallback() 
		{
		    @Override
		    public void call(Session session, SessionState state, Exception exception) 
		    {
		        onSessionStateChange(session, state, exception);
		    }
		};
		
		openFacebookSession(this, true, statusCallback);
	}
	
	
	
	@Override
	protected void updateUI(REQUEST_STATUS status)
	{
		/* Have to have this method here since SSActivity has this method abstract */
	}

	
	
	@Override
	protected void loadPage() 
	{
		/* Have to have this method here since SSActivity has this method abstract */
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	
	
	private void initViews() 
	{
		mActionBar = getSupportActionBar();
		
		mActionBar.hide();
	}
	

	
	private void onSessionStateChange(
			final Session session, 
			SessionState state, 
			Exception exception) 
	{
		switch(state)
		{
			/*
			 * Indicates that the Session has not yet been opened and has no cached token. 
			 * Opening a Session in this state will involve user interaction.
			 */
			case CREATED:
			{
				Log.d(TAG, "Facebook state: CREATED");
			}
			break;
			
			/*
			 * Indicates that the Session has not yet been opened and has a cached token. 
			 * Opening a Session in this state will not involve user interaction. 
			 * If you are using Session from an Android Service, you must provide a TokenCachingStrategy implementation that contains a valid token to the Session constructor. 
			 * The resulting Session will be created in this state, and you can then safely call open, passing null for the Activity.
			 */
			case CREATED_TOKEN_LOADED:
			{
				Log.d(TAG, "Facebook state: CREATED_TOKEN_LOADED");
			}
			break;
			
			/*
			 * Indicates that the Session is in the process of opening.
			 */
			case OPENING:
			{
				Log.d(TAG, "Facebook state: OPENING");
				
				// Do nothing
			}
			break;
			
			/*
			 * Indicates that the Session is opened. In this state, the Session may be used with a Request
			 */
			case OPENED:
			{
				Log.d(TAG, "Facebook state: OPENED");
				
				Request request = Request.newMeRequest(session, new Request.GraphUserCallback() 
				{
					@Override
					public void onCompleted(GraphUser user, Response response) 
					{
						Intent intent;
						
						if(mIsFromActivity)
						{
							Log.d(TAG, "Returning to ActivityActivity");
							
							intent = new Intent(FacebookLoginActivity.this, ActivityActivity.class);
						}
						else
						{
							Log.d(TAG, "Returning to HomeActivity");
							
							intent = new Intent(FacebookLoginActivity.this, HomeActivity.class);
						}
						
						if (user != null)
						{
							String facebookSessionToken = session.getAccessToken();
							
							Log.d(TAG, "Facebook session token: " + facebookSessionToken);
							
							boolean tokenSuccessfullyObtained = getMiTVToken(facebookSessionToken);
							
							if(tokenSuccessfullyObtained) 
							{
								intent.putExtra(Consts.INTENT_EXTRA_LOG_IN_ACTION, true);
								
								ContentManager.updateContent();
							}
							else
							{
								// TODO: Toast message is in english
								Toast.makeText(FacebookLoginActivity.this, "Facebook login failed. Please try again later", Toast.LENGTH_SHORT).show();
								
								Log.d(TAG, "Failed to get token");
							}
						} 
						else 
						{
							// TODO: Toast message is in english
							Toast.makeText(FacebookLoginActivity.this, "Facebook login failed. Please try again later", Toast.LENGTH_SHORT).show();
							
							Log.d(TAG, "User is null");
						}
						
						startActivity(intent);
						
						finish();
					}
				});
				
				request.executeAsync();
			}
			break;
			
			/*
			 * Indicates that the Session is opened and that the token has changed. 
			 * In this state, the Session may be used with Request. 
			 * Every time the token is updated, StatusCallback is called with this value.
			 */
			case OPENED_TOKEN_UPDATED:
			{
				Log.d(TAG, "Facebook state: OPENED_TOKEN_UPDATED");
			}
			break;
			
			/*
			 * Indicates that the Session was closed normally.
			 */
			case CLOSED:
			{
				Log.d(TAG, "Facebook state: CLOSED");
			}
			break;
			
			/*
			 * Indicates that the Session is closed, and that it was not closed normally. 
			 * Typically this means that the open call failed, and the Exception parameter to StatusCallback will be non-null.
			 */
			case CLOSED_LOGIN_FAILED:
			{
				// TODO: Toast message is in english
				Toast.makeText(this, "Facebook login failed. Please try again later", Toast.LENGTH_SHORT).show();
				
				Log.d(TAG, "Facebook state: CLOSED_LOGIN_FAILED");
			}
			break;
			
			default:
			{
				// TODO: Toast message is in english
				Toast.makeText(this, "Facebook login failed. Please try again later", Toast.LENGTH_SHORT).show();
				
				Log.d(TAG, "Facebook state: ?");
			}
			break;
		}
	}

	
	
	private boolean getMiTVToken(String facebookSessionToken)
	{
		if (facebookSessionToken.length() > 0) 
		{
			FacebookLoginTask facebookLoginTask = new FacebookLoginTask();
			
			try
			{
				String responseStr = facebookLoginTask.execute(facebookSessionToken).get();
				
				if (TextUtils.isEmpty(responseStr) != true)
				{
					JSONObject fbJSON = new JSONObject(responseStr);
					
					String facebookToken = fbJSON.getString(Consts.API_TOKEN);
					
					if (facebookToken != null && TextUtils.isEmpty(facebookToken) != true)
					{
						// Save access token in the application
						((SecondScreenApplication) getApplicationContext()).setAccessToken(facebookToken);
						
						Log.d(TAG, "Token: " + facebookToken + " is saved");

						boolean result = AuthenticationService.storeUserInformation(FacebookLoginActivity.this, fbJSON);
						
						if (result) 
						{
							return true;
						} 
						else
						{
							// TODO : Log this
							return false;
						}
					}
					else
					{
						// TODO : Log this
						return false;
					}
				} 
				else 
				{
					// Toast.makeText(getApplicationContext(), "Error! Something went wrong while authorization via Facebook. Please, try again!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Error! Something went wrong while authorization via Facebook. Please, try again!");
					
					return false;
				}
			} 
			catch (InterruptedException e) 
			{
				Log.d(TAG, e.getMessage(), e);
				
				return false;
			} 
			catch (ExecutionException e)
			{
				Log.d(TAG, e.getMessage(), e);
				
				return false;
			} 
			catch (JSONException e) 
			{
				Log.d(TAG, e.getMessage(), e);
				
				return false;
			}
		}
		else 
		{
			Log.d(TAG, "Error! Facebook authorization: level get Token from Facebook");
			
			return false;
		}
	}
	
	

	
	private static Session openFacebookSession(
			Activity activity, 
			boolean allowLoginUI, 
			Session.StatusCallback statusCallback)
	{
		OpenRequest openRequest = new OpenRequest(activity);
		
		openRequest.setPermissions(Arrays.asList("email"));
		
		openRequest.setCallback(statusCallback);
		
		Session session = new Session.Builder(activity).build();
		
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) 
		{
			Session.setActiveSession(session);
			
			session.openForRead(openRequest);

			return session;
		}
		else
		{
			return null;
		}
	}
}
