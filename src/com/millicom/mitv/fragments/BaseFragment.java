
package com.millicom.mitv.fragments;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.millicom.mitv.utilities.NetworkUtils;
import com.mitv.Consts;
import com.mitv.R;



public abstract class BaseFragment 
	extends Fragment
	implements ActivityCallbackListener
{
	private static final String TAG = BaseFragment.class.getName();

	
	public RelativeLayout mRequestFailedLayout;
	public RelativeLayout mRequestLoadingLayout;
	public RelativeLayout mRequestEmptyResponseLayout;
	public Button mRequestEmptyResponseButton;

	private boolean	mForceReload = false;

	
	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();

	
	
	public OnClickListener mOnEmptyResponseClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {}
	};


	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				Log.d(TAG, "FORCE RELOAD RECEIVED");

				mForceReload = true;
			}
		}, new IntentFilter(Consts.BROADCAST_HOMEPAGE));
	}
	
	
	
	/*
	 * This method checks for Internet connectivity on the background thread
	 */
	protected void loadDataWithConnectivityCheck()
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().checkNetworkConnectivity(this, getActivity());
		
		NetworkUtils.isConnectedAndHostIsReachable(getActivity());
	}

	
	
	protected void updateUIBaseElements(UIStatusEnum status) 
	{
		boolean activityNotNullOrFinishing = GenericUtils.isActivityNotNullOrFinishing(getActivity());
		
		if (activityNotNullOrFinishing == false) 
		{
			hideRequestStatusLayouts();

			Animation anim = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
			
			switch (status) 
			{	
				case LOADING:
				{
					if (mRequestLoadingLayout != null) 
					{
						mRequestLoadingLayout.setVisibility(View.VISIBLE);
					}
					break;
				}
			
				case NO_CONNECTION_AVAILABLE:
				case FAILED:
				{
					if (mRequestFailedLayout != null) 
					{
						mRequestFailedLayout.setVisibility(View.VISIBLE);
						mRequestFailedLayout.startAnimation(anim);
					}
					break;
				}
				
				case SUCCEEDED_WITH_DATA:
				{
					mForceReload = false;
					break;
				}
				
				case SUCCEEDED_WITH_EMPTY_DATA:
				{
					if (mRequestEmptyResponseLayout != null) 
					{
						mRequestEmptyResponseLayout.setVisibility(View.VISIBLE);
						mRequestEmptyResponseLayout.startAnimation(anim);
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
	
	
	
	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult) 
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
				break;
			}
		}
	}

	

	// Set the initial state of all request layouts to GONE
	public void hideRequestStatusLayouts() 
	{
		if (mRequestFailedLayout != null)
		{
			mRequestFailedLayout.setVisibility(View.GONE);
		}
		
		if (mRequestLoadingLayout != null)
		{
			mRequestLoadingLayout.setVisibility(View.GONE);
		}
		
		if (mRequestEmptyResponseLayout != null)
		{
			mRequestEmptyResponseLayout.setVisibility(View.GONE);
		}
	}
	
	
	public void initRequestCallbackLayouts(View v) 
	{
		mRequestFailedLayout = (RelativeLayout) v.findViewById(R.id.request_failed_main_layout);
		// mRequestFailedButton = (Button) v.findViewById(R.id.request_failed_reload_button);
		// mRequestFailedButton.setOnClickListener(mClickListener);

		mRequestEmptyResponseLayout = (RelativeLayout) v.findViewById(R.id.request_empty_main_layout);
		
		mRequestLoadingLayout = (RelativeLayout) v.findViewById(R.id.request_loading_main_layout);
	}
	
	

	public boolean shouldForceReload() 
	{
		return mForceReload;
	}
}
