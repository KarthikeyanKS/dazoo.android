
package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.UserLike;
import com.mitv.R;
import com.mitv.adapters.LikesListAdapter;



public class LikesActivity 
	extends BaseContentActivity 
	implements OnClickListener
{
	private static final String TAG = LikesActivity.class.getName();
	
	
	private ListView listView;
	private LikesListAdapter listAdapter;
	
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_likes_activity);
		
		initLayout();
	}
	
	
	
	private void initLayout() 
	{
		actionBar.setTitle(getResources().getString(R.string.likes));
		actionBar.setDisplayHomeAsUpEnabled(true);
	
		listView = (ListView) findViewById(R.id.listview);
		
		setEmptyLayoutDetailsMessage(getResources().getString(R.string.no_likes));
	}
	
	
		
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceUserLikes(this, false);
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		switch(requestIdentifier)
		{
			case USER_LIKES:
			case USER_ADD_LIKE:
			case USER_REMOVE_LIKE:
			{
				if (fetchRequestResult.wasSuccessful()) 
				{
					boolean cacheContainsLikes = (ContentManager.sharedInstance().getFromCacheUserLikes().isEmpty() == false);
					
					if(cacheContainsLikes)
					{
						updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
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
			case SUCCESS_WITH_NO_CONTENT:
			{
				// Do nothing (handled by parent activity)
				break;
			}
		
			case SUCCEEDED_WITH_DATA:
			{
				ArrayList<UserLike> userLikes = ContentManager.sharedInstance().getFromCacheUserLikes();
				
				Collections.sort(userLikes, new UserLike.UserLikeComparatorByTitle());
				
				listAdapter = new LikesListAdapter(this, userLikes);
				
				listView.setAdapter(listAdapter);
				
				break;
			}
	
			default:
			{
				Log.w(TAG, "Unhandled UIStatus");
				break;
			}
		}
	}
}
