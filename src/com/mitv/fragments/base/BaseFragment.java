
package com.mitv.fragments.base;



import java.util.Calendar;
import java.util.Timer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.ui.elements.FontTextView;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.DateUtils;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;



public abstract class BaseFragment 
	extends Fragment
	implements ViewCallbackListener, OnClickListener
{
	private static final String TAG = BaseFragment.class.getName();

	
	private RelativeLayout requestSuccessfulLayout;
	private RelativeLayout requestEmptyLayout;
	private RelativeLayout requestLoadingLayout;
	private FontTextView requestEmptyLayoutDetails;
	private FontTextView requestLoadingLayoutDetails;
	private RelativeLayout requestFailedLayout;
	private RelativeLayout requestNoInternetConnectionLayout;
	private Button requestrequestNoInternetConnectionRetryButton;
	private Button requestFailedRetryButton;

	/* Initially null, but set to the current device time 
	 * The assignment is done in the cases SUCCESS_WITH_NO_CONTENT or SUCCESS_WITH_CONTENT of the updateUIBaseElements **/
	private Calendar lastDataUpdatedCalendar;
	
	/* Timer for re-fetching data in the background while the user is on the same activity */
	private Timer backgroundLoadTimer;

	/* Time value for the background timer.
	 * The initial value is -1 if not used */
	private int backgroundLoadTimerValueInSeconds;
	

	private boolean loadedFromBackground;


	
	/* Abstract Methods */
	
	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();
	
	/* This method implementation is OPTIONAL */
	protected abstract void loadDataInBackground();
	
	/*
	 * This method implementation should return true if all the data necessary to show the content view can be obtained
	 * from cache without the need of external service calls
	 */
	protected abstract boolean hasEnoughDataToShowContent();
	
	/* This method implementation should deal with changes after the data has been fetched */
	protected abstract void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier);
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		lastDataUpdatedCalendar = null;
		
		backgroundLoadTimerValueInSeconds = -1;
	}
	
	
	
	@Override
	public void onResume() 
	{	
		super.onResume();
		
		setBackgroundLoadingTimer();
		
		loadDataWithConnectivityCheck();
	}
	
	
	
	@Override
	public void onPause() 
	{
		super.onPause();
 
		if(backgroundLoadTimer != null)
		{
			backgroundLoadTimer.cancel();
		}
	}
	
	
	
	@Override
	public void onDestroyView() 
	{
		super.onDestroyView();
		
		ContentManager.sharedInstance().unregisterListenerFromAllRequests(this);
	}
	
	
	
	protected void registerAsListenerForRequest(RequestIdentifierEnum requestIdentifier)
	{
		ContentManager.sharedInstance().registerListenerForRequest(requestIdentifier, this);
	}

	
	
	protected void loadDataWithConnectivityCheck()
	{
		boolean isConnected = NetworkUtils.isConnected();

		if (isConnected) 
		{
			boolean hasInitialData = ContentManager.sharedInstance().getFromCacheHasInitialData();

			if (hasInitialData) 
			{
				loadData();
			} 
			else 
			{
				updateUI(UIStatusEnum.LOADING);
				ContentManager.sharedInstance().fetchFromServiceInitialCall(this, null);
			}
		} 
		else 
		{
			if (hasEnoughDataToShowContent()) 
			{
				loadData();
			} 
			else 
			{
				updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
			}
		}
	}
	
	
	
	@Override
	final public void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch(fetchRequestResult)
		{
			case INTERNET_CONNECTION_AVAILABLE:
			{
				loadData();
				break;
			}
			
			case INTERNET_CONNECTION_NOT_AVAILABLE:
			{
				updateUI(UIStatusEnum.NO_CONNECTION_AVAILABLE);
				break;
			}
			
			case API_VERSION_TOO_OLD:
			{
				updateUI(UIStatusEnum.API_VERSION_TOO_OLD);
				break;
			}
			
			case FORBIDDEN:
			{
				updateUI(UIStatusEnum.USER_TOKEN_EXPIRED);
				break;
			}
			
			case SUCCESS:
			case SUCCESS_WITH_NO_CONTENT:
			default:
			{
				onDataAvailable(fetchRequestResult, requestIdentifier);
				break;
			}
		}
	}

	
	
	protected void updateUIBaseElements(UIStatusEnum status) 
	{
		boolean activityNotNullAndNotFinishing = GenericUtils.isActivityNotNullAndNotFinishingAndNotDestroyed(getActivity());
		
		if (activityNotNullAndNotFinishing) 
		{
			hideRequestStatusLayouts();

			Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
			
			switch (status) 
			{	
				case LOADING:
				{
					if (requestLoadingLayout != null) 
					{
						requestLoadingLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
				
				case API_VERSION_TOO_OLD: 
				{
					DialogHelper.showMandatoryAppUpdateDialog(getActivity());
					break;
				}
				
				case USER_TOKEN_EXPIRED:
				{
					DialogHelper.showPromptTokenExpiredDialog(getActivity());
					break;
				}
			
				case FAILED:
				{
					if (requestFailedLayout != null) 
					{
						requestFailedLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
				
				case NO_CONNECTION_AVAILABLE:
				{
					if (requestNoInternetConnectionLayout != null) 
					{
						requestNoInternetConnectionLayout.setVisibility(View.VISIBLE);
						requestNoInternetConnectionLayout.startAnimation(anim);
					}
					break;
				}
				
				case SUCCESS_WITH_NO_CONTENT:
				{
					if (requestEmptyLayout != null) 
					{
						requestEmptyLayout.setVisibility(View.VISIBLE);
						requestEmptyLayoutDetails.setVisibility(View.VISIBLE);
						requestEmptyLayout.startAnimation(anim);
					}
					
					lastDataUpdatedCalendar = DateUtils.getNowWithGMTTimeZone();
					
					break;
				}
				
				case SUCCESS_WITH_CONTENT:
				default:
				{
					if (requestSuccessfulLayout != null) 
					{
						requestSuccessfulLayout.setVisibility(View.VISIBLE);
					}
					
					if(loadedFromBackground)
					{
						String message = getString(R.string.generic_content_updated);
						
						ToastHelper.createAndShowShortToast(message);
						
						loadedFromBackground = false;
					}
					
					lastDataUpdatedCalendar = DateUtils.getNowWithGMTTimeZone();
					
					break;
				}
			}
		}
		else
		{
			Log.w(TAG, "Activity is null or finishing. No UI elements will be changed.");
		}
	}
	
	
	

	// Set the initial state of all request layouts to GONE
	public void hideRequestStatusLayouts() 
	{
		if (requestSuccessfulLayout != null) 
		{
			requestSuccessfulLayout.setVisibility(View.GONE);
		}
		
		if (requestLoadingLayout != null) 
		{
			requestLoadingLayout.setVisibility(View.GONE);
		}
		
		if(requestNoInternetConnectionLayout != null)
		{
			requestNoInternetConnectionLayout.setVisibility(View.GONE);
		}

		if (requestEmptyLayout != null) 
		{
			requestEmptyLayout.setVisibility(View.GONE);
			requestEmptyLayoutDetails.setVisibility(View.GONE);
		}
		
		if(requestFailedLayout != null) 
		{
			requestFailedLayout.setVisibility(View.GONE);
		}
	}
	
	
	
	public void initRequestCallbackLayouts(View view) 
	{
//		requestSuccessfulLayout = (RelativeLayout) view.findViewById(R.id.request_successful_layout);
		
		requestLoadingLayout = (RelativeLayout) view.findViewById(R.id.request_loading_not_transparent);
		
		requestLoadingLayoutDetails = (FontTextView) view.findViewById(R.id.request_loading_details_tv);

		requestEmptyLayout = (RelativeLayout) view.findViewById(R.id.request_empty_main_layout);

		requestEmptyLayoutDetails = (FontTextView) view.findViewById(R.id.request_empty_details_tv);
		
		requestNoInternetConnectionLayout = (RelativeLayout) view.findViewById(R.id.no_connection_layout);
		
		requestFailedLayout = (RelativeLayout) view.findViewById(R.id.request_failed_main_layout);
		
		requestFailedRetryButton = (Button) view.findViewById(R.id.request_failed_reload_button);
		
		if(requestFailedRetryButton != null) 
		{
			requestFailedRetryButton.setOnClickListener(this);
		}
		
		requestrequestNoInternetConnectionRetryButton = (Button) view.findViewById(R.id.no_connection_reload_button);
		
		if (requestrequestNoInternetConnectionRetryButton != null) 
		{
			requestrequestNoInternetConnectionRetryButton.setOnClickListener(this);
		}
	}
	
	
	
	protected void setEmptyLayoutDetailsMessage(String message)
	{
		if (requestEmptyLayoutDetails != null)
		{
			requestEmptyLayoutDetails.setText(message);
			requestEmptyLayoutDetails.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	protected void setLoadingLayoutDetailsMessage(String message)
	{
		if (requestLoadingLayoutDetails != null)
		{
			requestLoadingLayoutDetails.setText(message);
		}
	}
	
	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();

		switch (id)
		{
			case R.id.no_connection_reload_button:
			{
				TrackingGAManager.sharedInstance().sendUserNoConnectionRetryLayoutButtomPressed(getClass().getSimpleName());
				
				loadDataWithConnectivityCheck();
				
				break;
			}
			
			case R.id.request_failed_reload_button:
			{
				TrackingGAManager.sharedInstance().sendUserNoDataRetryLayoutButtomPressed(getClass().getSimpleName());
				
				loadDataWithConnectivityCheck();
				
				break;
			}
			
			default: 
			{
				Log.w(TAG, "Unknown onClick action");
				break;
			}
		}
	}
	
	
	
	protected boolean wasActivityDataUpdatedMoreThan(int minutes)
	{
		boolean wasDataUpdatedMoreThan = false;
		
		if(lastDataUpdatedCalendar != null)
		{
			Calendar lastDataUpdatedCalendarWithincrement = (Calendar) lastDataUpdatedCalendar.clone();
			lastDataUpdatedCalendarWithincrement.add(Calendar.MINUTE, minutes);
			
			Calendar now = DateUtils.getNowWithGMTTimeZone();
			
			wasDataUpdatedMoreThan = lastDataUpdatedCalendarWithincrement.before(now);
		}
		
		return wasDataUpdatedMoreThan;
	}
	
	
	
	private void setBackgroundLoadingTimer()
	{
		if(backgroundLoadTimerValueInSeconds > -1)
		{
			int backgroundTimerValue = (int) (backgroundLoadTimerValueInSeconds*DateUtils.TOTAL_MILLISECONDS_IN_ONE_SECOND);
		
			backgroundLoadTimer = new Timer();
			
			backgroundLoadTimer.schedule(new java.util.TimerTask()
			{
				@Override
				public void run()
				{
					loadedFromBackground = true;
					
					loadDataInBackground();
				}
			}, backgroundTimerValue, backgroundTimerValue);
		}
	}
	
	
	
	protected void setBackgroundLoadTimerValueInSeconds(int value)
	{
		backgroundLoadTimerValueInSeconds = value;
	}
}