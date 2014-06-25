
package com.mitv.activities;



import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.widget.ListView;

import com.mitv.R;
import com.mitv.activities.base.BaseActivityLoginRequired;
import com.mitv.adapters.list.LikesListAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.comparators.UserLikeComparatorByTitle;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.utilities.NetworkUtils;



public class LikesActivity 
	extends BaseActivityLoginRequired
{
	private static final String TAG = LikesActivity.class.getName();
	
	
	private ListView listView;
	private LikesListAdapter listAdapter;
	
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}

		setContentView(R.layout.layout_likes_activity);
		
		initLayout();
	}
	
	
	
	private void initLayout() 
	{
		actionBar.setTitle(getString(R.string.likes));
		actionBar.setDisplayHomeAsUpEnabled(true);
	
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TrackingGAManager.sharedInstance().sendUserPressedLikeInLikesActivity();
			}
		});

		
		setEmptyLayoutDetailsMessage(getString(R.string.no_likes));
	}
	
	
		
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceUserLikes(this, false);
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean isConnected = NetworkUtils.isConnected();

		if (isConnected) 
		{
			return ContentManager.sharedInstance().getCacheManager().containsUserLikes();
		}
		else
		{
			return false;
		}
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch(requestIdentifier)
		{
			case USER_LIKES_STANDALONE:
			case USER_ADD_LIKE:
			case USER_REMOVE_LIKE:
			{
				if (fetchRequestResult.wasSuccessful()) 
				{
					boolean cacheContainsLikes = (ContentManager.sharedInstance().getCacheManager().getUserLikes().isEmpty() == false);
					
					if(cacheContainsLikes)
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
					}
					else
					{
						updateUI(UIStatusEnum.SUCCESS_WITH_NO_CONTENT);
					}
				}
				else
				{
					updateUI(UIStatusEnum.FAILED);
				}
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled request identifier.");
				break;
			}
		}
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{
			case USER_TOKEN_EXPIRED:
			{
				/* If the sessions has expired, finish this activity and resume the previous one */
				finish();
				break;
			}
		
			case SUCCESS_WITH_CONTENT:
			{
				List<UserLike> userLikes = ContentManager.sharedInstance().getCacheManager().getUserLikes();
				
				Collections.sort(userLikes, new UserLikeComparatorByTitle());
				
				listAdapter = new LikesListAdapter(this, userLikes);
				
				listView.setAdapter(listAdapter);
				
				break;
			}
	
			default:
			{
				// Do nothing
				break;
			}
		}
	}
}
