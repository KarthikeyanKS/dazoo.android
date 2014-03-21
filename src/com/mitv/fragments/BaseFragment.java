
package com.mitv.fragments;



import android.os.Bundle;
import android.os.Parcelable;
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
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.ui.elements.UndoBarController;
import com.mitv.ui.elements.UndoBarController.UndoListener;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.NetworkUtils;



public abstract class BaseFragment 
	extends Fragment
	implements ViewCallbackListener, OnClickListener, UndoListener
{
	private static final String TAG = BaseFragment.class.getName();

	
	protected View undoBarlayoutView;
	private UndoBarController undoBarController;
	
	private RelativeLayout requestEmptyLayout;
	private RelativeLayout requestLoadingLayout;
	private RelativeLayout requestFailedLayout;
	private RelativeLayout requestNoInternetConnectionLayout;
	private Button requestrequestNoInternetConnectionRetryButton;


	
	/* Abstract Methods */
	
	/* This method implementation should update the user interface according to the received status */
	protected abstract void updateUI(UIStatusEnum status);

	/* This method implementation should load all the necessary data from the webservice */
	protected abstract void loadData();
	
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
	}
	
	
	
	@Override
	public void onResume() 
	{	
		super.onResume();
		
		loadDataWithConnectivityCheck();
	}
	
	
	
	@Override
	public void onUndo(Parcelable token) 
	{
		if (undoBarController != null) 
		{
			undoBarController.hideUndoBar(true);
			undoBarController = new UndoBarController(undoBarlayoutView, this);
		} 
		else 
		{
			Log.w(TAG, "Undo bar component is null.");
		}
		
		loadDataWithConnectivityCheck();
	}
	
	
	
	/*
	 * This method checks for Internet connectivity on the background thread
	 */
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
			
			default:
			{
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
			
				case FAILED:
				{
					if (requestFailedLayout != null) {
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
						requestEmptyLayout.startAnimation(anim);
					}
					break;
				}
				
				case SUCCESS_WITH_CONTENT:
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
		
		if(requestFailedLayout != null) 
		{
			requestFailedLayout.setVisibility(View.GONE);
		}
	}
	
	
	
	public void initRequestCallbackLayouts(View view) 
	{
		requestLoadingLayout = (RelativeLayout) view.findViewById(R.id.request_loading_transparent);

		requestEmptyLayout = (RelativeLayout) view.findViewById(R.id.request_empty_main_layout);

		requestFailedLayout = (RelativeLayout) view.findViewById(R.id.request_failed_main_layout);
		
		requestNoInternetConnectionLayout = (RelativeLayout) view.findViewById(R.id.no_connection_layout);
		
		requestrequestNoInternetConnectionRetryButton = (Button) view.findViewById(R.id.no_connection_reload_button);
		
		if (requestrequestNoInternetConnectionRetryButton != null) 
		{
			requestrequestNoInternetConnectionRetryButton.setOnClickListener(this);
		}
		
		undoBarlayoutView = getActivity().findViewById(R.id.undobar);

		if (undoBarlayoutView != null) 
		{
			undoBarController = new UndoBarController(undoBarlayoutView, this);
		} 
		else 
		{
			Log.w(TAG, "Undo bar element not present.");
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
