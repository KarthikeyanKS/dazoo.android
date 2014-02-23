package com.millicom.mitv.activities;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.UserLike;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.LikesListAdapter;
import com.mitv.interfaces.LikesCountInterface;

public class LikesActivity extends BaseActivity implements ActivityCallbackListener, LikesCountInterface, OnClickListener {

	@SuppressWarnings("unused")
	private static final String TAG = LikesActivity.class.getName();
	
	private ActionBar			mActionBar;
	private boolean				mIsChange	= false;
	private ListView			mListView;
	private LikesListAdapter	mAdapter;
//	private String				token;
	private RelativeLayout		mTabTvGuide, mTabActivity, mTabProfile;
	private View mTabDividerLeft, mTabDividerRight;
	private TextView mErrorTv;
	private int mCount = 0;
	private ArrayList<UserLike> mLikes;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_likes_activity);
		
		
		// add the activity to the list of running activities
		SecondScreenApplication.sharedInstance().getActivityList().add(this);
		
//		token = ((SecondScreenApplication) getApplicationContext()).getAccessToken();
		fetchUserLikesData();
		initLayout();
		super.initCallbackLayouts();
		populateLayout();
	}
	
	private void fetchUserLikesData() {
		ContentManager.sharedInstance().getElseFetchFromServiceUserLikes(this, false);
	}
	
	

	private void initLayout() {
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(getResources().getString(R.string.likes));
		mActionBar.setDisplayHomeAsUpEnabled(true);

		// styling bottom navigation tabs
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
		if (mIsChange == true) {
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
	public void onClick(View v) 
	{
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.tab_tv_guide:
				// tab to home page
				Intent intentHome = new Intent(LikesActivity.this, HomeActivity.class);
				intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentHome);
				
				break;
			case R.id.tab_activity:
				// tab to home page
				Intent intentActivity = new Intent(LikesActivity.this, ActivityActivity.class);
				startActivity(intentActivity);
				
				break;
			case R.id.tab_me:
				Intent returnIntent = new Intent();
				if (mIsChange == true) {
					setResult(Consts.INFO_UPDATE_LIKES, returnIntent);
					returnIntent.putExtra(Consts.INFO_UPDATE_LIKES_NUMBER, mCount);
				}
				finish();
				
				break;
		}
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
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				// TODO NewArc - Do something here?
				break;
			}
	
			default:
			{
				// Do nothing
				break;
			}
		}
	}

	
	
	@Override
	protected void loadData() 
	{
		// TODO NewArc - Do something here?
	}
	
	
	
	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult) 
	{
		super.onResult(fetchRequestResult);
		
		switch(fetchRequestResult)
		{
			case SUCCESS:
			{
				// TODO NewArc - Do something here?
				break;
			}
			
			default:
			{
				mErrorTv.setVisibility(View.VISIBLE);
				break;
			}
		}
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			// Respond to the action bar's Up/Home button
			// update the likes list on Up/Home button press too
			case android.R.id.home:
			{
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				if (mIsChange == true) {
					setResult(Consts.INFO_UPDATE_LIKES, upIntent);
					upIntent.putExtra(Consts.INFO_UPDATE_LIKES_NUMBER, mCount);
				}
				NavUtils.navigateUpTo(this, upIntent);
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
}
