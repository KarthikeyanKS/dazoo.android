
package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.UserLike;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.adapters.LikesListAdapter;
import com.mitv.interfaces.LikesCountInterface;



public class LikesActivity 
	extends BaseContentActivity 
	implements LikesCountInterface, OnClickListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = LikesActivity.class.getName();
	
	private ListView mListView;
	private LikesListAdapter mAdapter;
	private RelativeLayout mTabTvGuide;
	private RelativeLayout mTabActivity;
	private RelativeLayout mTabProfile;
	private View mTabDividerLeft;
	private View mTabDividerRight;
	private TextView mErrorTv;
	
	private boolean mIsChange = false;
	private int mCount = 0;
	private ArrayList<UserLike> mLikes;
	
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_likes_activity);
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();

		initLayout();
		
		populateLayout();
	}
	
	

	private void initLayout() 
	{
		actionBar.setTitle(getResources().getString(R.string.likes));
		actionBar.setDisplayHomeAsUpEnabled(true);

		mTabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		mTabTvGuide.setOnClickListener(this);
		mTabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		mTabActivity.setOnClickListener(this);
		mTabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		mTabProfile.setOnClickListener(this);
		
		mTabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		mTabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		mTabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		mTabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		mTabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		mTabProfile.setBackgroundColor(getResources().getColor(R.color.red));
	
		mListView = (ListView) findViewById(R.id.listview);
		mErrorTv = (TextView) findViewById(R.id.likes_error_tv);
	}

	
	
	private void populateLayout() 
	{
		Collections.sort(mLikes, new UserLike.UserLikeComparatorByTitle());
		
		mAdapter = new LikesListAdapter(this, mLikes, this);
		
		mListView.setAdapter(mAdapter);
	}

	
	
	@Override
	public void onBackPressed() 
	{
		Intent returnIntent = new Intent();
		
		if (mIsChange == true) 
		{
			setResult(Consts.INFO_UPDATE_LIKES, returnIntent);
			
			returnIntent.putExtra(Consts.INFO_UPDATE_LIKES_NUMBER, mCount);
		}
		
		super.onBackPressed();
		
		finish();
	}

	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void setCount(int count) 
	{
		mIsChange = true;
		
		mCount = count;
		
		if(count == 0) 
		{
			mErrorTv.setVisibility(View.VISIBLE);
		} 
		else 
		{
			mErrorTv.setVisibility(View.GONE);
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
				// TODO NewArc - Do something here?
				
				mErrorTv.setVisibility(View.GONE);
				break;
			}
	
			default:
			{
				mErrorTv.setVisibility(View.VISIBLE);
				break;
			}
		}
	}
	
	

}
