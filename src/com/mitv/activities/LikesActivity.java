
package com.mitv.activities;



import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseActivityLoginRequired;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.LikesListAdapter;
import com.mitv.models.UserLike;
import com.mitv.models.comparators.UserLikeComparatorByTitle;
import com.mitv.utilities.NetworkUtils;



public class LikesActivity 
	extends BaseActivityLoginRequired 
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
		actionBar.setTitle(getString(R.string.likes));
		actionBar.setDisplayHomeAsUpEnabled(true);
	
		listView = (ListView) findViewById(R.id.listview);
		
		setEmptyLayoutDetailsMessage(getString(R.string.no_likes));
	}
	
	
		
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceUserLikes(this, false);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean isConnected = NetworkUtils.isConnected();

		if (isConnected) 
		{
			return ContentManager.sharedInstance().getFromCacheHasUserLikes();
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
			case USER_LIKES:
			case USER_ADD_LIKE:
			case USER_REMOVE_LIKE:
			{
				if (fetchRequestResult.wasSuccessful()) 
				{
					boolean cacheContainsLikes = (ContentManager.sharedInstance().getFromCacheUserLikes().isEmpty() == false);
					
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
				List<UserLike> userLikes = ContentManager.sharedInstance().getFromCacheUserLikes();
				
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
