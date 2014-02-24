
package com.millicom.mitv.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.R;



public abstract class BaseFragment 
	extends Fragment
	implements ActivityCallbackListener, OnClickListener
{
	private static final String TAG = BaseFragment.class.getName();

	
	public RelativeLayout requestFailedLayout;
	public RelativeLayout requestLoadingLayout;
	public RelativeLayout requestEmptyResponseLayout;
	public Button requestFailedButton;

	
	
	/* Abstract Methods */
	
	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();
	
	/* This method implementation should deal with changes after the data has been fetched */
	protected abstract void onDataAvailable(FetchRequestResultEnum fetchRequestResult);
	
	
	
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
	final public void onResult(FetchRequestResultEnum fetchRequestResult) 
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
				onDataAvailable(fetchRequestResult);
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
					if (requestFailedLayout != null) 
					{
						requestFailedLayout.setVisibility(View.VISIBLE);
						requestFailedLayout.startAnimation(anim);
					}
					break;
				}
				
				case SUCCEEDED_WITH_DATA:
				{
					break;
				}
				
				case SUCCEEDED_WITH_EMPTY_DATA:
				{
					if (requestEmptyResponseLayout != null) 
					{
						requestEmptyResponseLayout.setVisibility(View.VISIBLE);
						requestEmptyResponseLayout.startAnimation(anim);
					}
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
		if (requestFailedLayout != null)
		{
			requestFailedLayout.setVisibility(View.GONE);
		}
		
		if (requestLoadingLayout != null)
		{
			requestLoadingLayout.setVisibility(View.GONE);
		}
		
		if (requestEmptyResponseLayout != null)
		{
			requestEmptyResponseLayout.setVisibility(View.GONE);
		}
	}
	
	
	
	public void initRequestCallbackLayouts(View v) 
	{
		requestFailedLayout = (RelativeLayout) v.findViewById(R.id.request_failed_main_layout);
		requestFailedButton = (Button) v.findViewById(R.id.request_failed_reload_button);
		requestFailedButton.setOnClickListener(this);

		requestEmptyResponseLayout = (RelativeLayout) v.findViewById(R.id.request_empty_main_layout);
		
		requestLoadingLayout = (RelativeLayout) v.findViewById(R.id.request_loading_main_layout);
	}
	
	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();

		switch (id)
		{
			case R.id.request_failed_reload_button:
			{
				loadDataWithConnectivityCheck();
			}
		}
	}
}
