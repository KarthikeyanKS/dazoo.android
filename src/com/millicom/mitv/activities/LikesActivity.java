
package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.UserLike;
import com.mitv.R;
import com.mitv.adapters.LikesListAdapter;
import com.mitv.interfaces.LikesCountInterface;



public class LikesActivity 
	extends BaseContentActivity 
	implements LikesCountInterface, OnClickListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = LikesActivity.class.getName();
	
	
	private ListView listView;
	private LikesListAdapter listAdapter;
	private TextView errorTv;
	
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_likes_activity);
		
		initViews();
	}
	
	
	
	private void initViews() 
	{
		actionBar.setTitle(getResources().getString(R.string.likes));
		actionBar.setDisplayHomeAsUpEnabled(true);
	
		listView = (ListView) findViewById(R.id.listview);
		errorTv = (TextView) findViewById(R.id.likes_error_tv);
	}
	
	
	
	@Override
	public void setCount(int count) 
	{
		if(count == 0) 
		{
			errorTv.setVisibility(View.VISIBLE);
		} 
		else 
		{
			errorTv.setVisibility(View.GONE);
		}
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
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
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
			case SUCCEEDED_WITH_DATA:
			{
				ArrayList<UserLike> userLikes = ContentManager.sharedInstance().getFromCacheUserLikes();
				
				Collections.sort(userLikes, new UserLike.UserLikeComparatorByTitle());
				
				listAdapter = new LikesListAdapter(this, userLikes, this);
				
				listView.setAdapter(listAdapter);
				
				errorTv.setVisibility(View.GONE);
				break;
			}
	
			default:
			{
				errorTv.setVisibility(View.VISIBLE);
				break;
			}
		}
	}
}
