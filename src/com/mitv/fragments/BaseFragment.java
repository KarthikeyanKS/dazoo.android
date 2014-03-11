
package com.mitv.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.ActivityCallbackListener;
import com.mitv.utilities.GenericUtils;



public abstract class BaseFragment 
	extends Fragment
	implements ActivityCallbackListener, OnClickListener
{
	private static final String TAG = BaseFragment.class.getName();

	
	
	private RelativeLayout requestEmptyLayout;
	private RelativeLayout requestLoadingLayout;
	private RelativeLayout requestNoInternetConnectionLayout;
	private Button requestrequestNoInternetConnectionRetryButton;


	
	
	/* Abstract Methods */
	
	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();
	
	/* This method implementation should deal with changes after the data has been fetched */
	protected abstract void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier);
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	
	
	@Override
	public void onResume() 
	{	
		super.onResume();
		
		loadData();
	}
	
	
	
	/*
	 * This method checks for Internet connectivity on the background thread
	 */
	protected void loadDataWithConnectivityCheck()
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().checkNetworkConnectivity(this);
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
			
			default:
			{
				// The remaining cases should be handled by the subclasses
				onDataAvailable(fetchRequestResult, requestIdentifier);
				break;
			}
		}
	}

	
	
	protected void updateUIBaseElements(UIStatusEnum status) 
	{
		boolean activityNotNullOrFinishing = GenericUtils.isActivityNotNullOrFinishing(getActivity());
		
		if (activityNotNullOrFinishing) 
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
			
				case NO_CONNECTION_AVAILABLE:
				case FAILED:
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
						requestEmptyLayout.startAnimation(anim);
					}
					break;
				}
				
				case SUCCEEDED_WITH_DATA:
				default:
				{
					// Success or other cases should be handled by the subclasses
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
		}
	}
	
	
	
	public void initRequestCallbackLayouts(View view) 
	{
		requestLoadingLayout = (RelativeLayout) view.findViewById(R.id.request_loading_main_layout);

		requestEmptyLayout = (RelativeLayout) view.findViewById(R.id.request_empty_main_layout);

		requestNoInternetConnectionLayout = (RelativeLayout) view.findViewById(R.id.no_connection_layout);
		
		requestrequestNoInternetConnectionRetryButton = (Button) view.findViewById(R.id.no_connection_reload_button);
		
		if (requestrequestNoInternetConnectionRetryButton != null) 
		{
			requestrequestNoInternetConnectionRetryButton.setOnClickListener(this);
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
}
