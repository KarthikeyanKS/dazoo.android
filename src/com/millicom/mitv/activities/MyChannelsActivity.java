package com.millicom.mitv.activities;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.mitv.R;
import com.mitv.adapters.MyChannelsListAdapter;
import com.mitv.interfaces.MyChannelsCountInterface;
import com.mitv.model.OldTVChannel;
import com.mitv.storage.MiTVStore;



public class MyChannelsActivity 
	extends BaseActivity 
	implements MyChannelsCountInterface, OnClickListener 
{
	private static final String TAG = MyChannelsActivity.class.getName();

	
	private ActionBar mActionBar;
	private Button mGetMyChannelsButton;
	private Button mAddToMyChannelsButton;
	private ListView mListView;
	private TextView mChannelCountTv;
	private RelativeLayout mTabTvGuide;
	private RelativeLayout mTabProfile;
	private RelativeLayout mTabActivity;
	private View mTabDividerLeft;
	private View mTabDividerRight;
	private EditText mSearchChannelInputEditText;
	
	private MyChannelsListAdapter mAdapter;
	
	private ArrayList<OldTVChannel>	mChannels = new ArrayList<OldTVChannel>();
	private ArrayList<String> mCheckedChannelsIds = new ArrayList<String>();
	private HashMap<String, OldTVChannel> mChannelsMap = new HashMap<String, OldTVChannel>();
	
	private boolean mIsChange = false;
	private boolean[] mIsCheckedArray;
	private int	mChannelCounter = 0;
//	private boolean	mIsChanged = false;
	private int	mCount = 0;

	private ArrayList<OldTVChannel> mChannelInfoToDisplay = new ArrayList<OldTVChannel>();
	private Map<String, OldTVChannel> mChannelInfoMap = new HashMap<String, OldTVChannel>();

	private ArrayList<String> myChannelIds = new ArrayList<String>();
	private ArrayList<String> mAllChannelsIds = new ArrayList<String>();

	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_mychannels_activity);
		
		initLayout();
	}
	
	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	
	
	private void initLayout()
	{
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(getResources().getString(R.string.myprofile_my_channels));
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
		mChannelCountTv = (TextView) findViewById(R.id.mychannels_header_counter_tv);
		mSearchChannelInputEditText = (EditText) findViewById(R.id.mychannels_header_search_ev);
	}

	
	
	private void populateViews() 
	{
		mChannelsMap = MiTVStore.getInstance().getChannels();
		
		if (mChannelsMap != null && 
		    mChannelsMap.isEmpty() != true)
		{
			int allChannelsIndex = 0;
			
			for (Entry<String, OldTVChannel> entry : mChannelsMap.entrySet())
			{
				mChannels.add(entry.getValue());
				mAllChannelsIds.add(mChannels.get(allChannelsIndex).getChannelId());
				allChannelsIndex++;
			}

			for (int i = 0; i < mChannels.size(); i++)
			{
				mChannelInfoMap.put(mChannels.get(i).getName().toLowerCase(Locale.getDefault()), mChannels.get(i));
				mChannelInfoToDisplay.add(mChannels.get(i));
			}

			mIsCheckedArray = new boolean[mAllChannelsIds.size()];

			if (ContentManager.sharedInstance().isLoggedIn()) 
			{
				//TODO no need for fetching channels right, should already have all.
				// (not needed) get user channels
//				if (getUserMyChannelsIdsJSON()) 
//				{
//					mChannelCounter = myChannelIds.size();
//					mChannelCountTv.setText(" " + String.valueOf(mChannelCounter));
//					mAdapter = new MyChannelsListAdapter(this, mChannelInfoToDisplay, mIsCheckedArray, this, mChannelCounter, mCheckedChannelsIds);
//					mListView.setAdapter(mAdapter);
//				}
			}
			else 
			{
				// Toast.makeText(getApplicationContext(), "You have to be logged in to perform this action :)", Toast.LENGTH_SHORT).show();
				Log.w(TAG, "Login action is required to be done before this");
			}

			mSearchChannelInputEditText.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {
					String search = s.toString();
					if (search.contains(System.getProperty("line.separator"))) {
						search = search.replace(System.getProperty("line.separator"), "");
						mSearchChannelInputEditText.setText(search);
						InputMethodManager in = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
						in.hideSoftInputFromWindow(mSearchChannelInputEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
					if (search.length() > 0) {
						if (search.length() > 3) {
							search = search.substring(0, 3);
						}
						mChannelInfoToDisplay.clear();
						for (Map.Entry<String, OldTVChannel> entry : mChannelInfoMap.entrySet()) {
							String key = entry.getKey();
							OldTVChannel channel = entry.getValue();
							if (key.toLowerCase().contains(search.toLowerCase())) {
								// mark the correct channels in the global list

								mChannelInfoToDisplay.add(channel);
								mAdapter.notifyDataSetChanged();
							}
						}
					} else {
						// mark the correct channels in the global list

						mChannelInfoToDisplay.clear();
						mChannelInfoToDisplay.addAll(mChannels);
						mAdapter.notifyDataSetChanged();
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {}
			});
		} 
		else
		{
			updateUI(UIStatusEnum.LOADING);
			
			loadData();
		}
	}
	
	
	
//	private void tryToUpdateChannelList() 
//	{
//		if (mIsChanged) 
//		{
//			updateChannelList();
//		}
//	}

	
	
//	private void updateChannelList() 
//	{
//		mCount = mCheckedChannelsIds.size();
//		
//		if (ApiClient.updateMyChannelsList(JSONUtilities.createJSONArrayWithOneJSONObjectType(Consts.CHANNEL_CHANNEL_ID, mCheckedChannelsIds))) {
//
//			// clear guides
//			MiTVStore.getInstance().clearChannelGuides();
//			// update the my channels list
//			ApiClient.getMyChannelIds();
////			MiTVCore.getInstance(context, dateIndex)
//
//			LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Consts.INTENT_EXTRA_MY_CHANNELS_CHANGED));
//
//		} else {
//			Log.d(TAG, "Channel list is not updated!");
//		}
//		mIsChanged = false;
//	}

	@Override
	protected void onStop() 
	{
		super.onStop();
		// update channel list if user come back to the My Profile via Home Button

		//TODO use content manager here somehow
//		tryToUpdateChannelList();
	}

	
	
	@Override
	public void onBackPressed() 
	{
		//TODO use content manager here somehow
//		tryToUpdateChannelList();
//		Intent returnIntent = new Intent();
//		if (mIsChanged == true) {
//			setResult(Consts.INFO_UPDATE_MYCHANNELS, returnIntent);
//			returnIntent.putExtra(Consts.INFO_UPDATE_MYCHANNELS_NUMBER, mCount);
//		}
		super.onBackPressed();
		
		finish();
	}

	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);	
	}

	
	
//	private boolean getUserMyChannelsIdsJSON() 
//	{
//		ApiClient.getMyChannelIds();
//		
//		myChannelIds = MiTVStore.getInstance().getChannelIds();
//		
//		if (myChannelIds != null && !myChannelIds.isEmpty()) 
//		{
//			mCheckedChannelsIds = myChannelIds;
//			
//			for (int i = 0; i < myChannelIds.size(); i++) 
//			{
//				if (mAllChannelsIds.contains(myChannelIds.get(i))) 
//				{
//					mIsCheckedArray[mAllChannelsIds.indexOf(myChannelIds.get(i))] = true;
//				}
//			}
//			return true;
//		} 
//		else
//		{
//			return false;
//		}
//	}

	
	
	@Override
	public void setValues(int count) 
	{
		mChannelCountTv.setText(" " + String.valueOf(count));
//		mIsChanged = true;
	}
	
	

	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.tab_tv_guide:
			//TODO use content manager here instead
//			tryToUpdateChannelList();
			Intent intentHome = new Intent(MyChannelsActivity.this, HomeActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentHome);
			
			break;
		case R.id.tab_activity:
			//TODO use content manager here instead
//			tryToUpdateChannelList();
			Intent intentActivity = new Intent(MyChannelsActivity.this, FeedActivity.class);
			startActivity(intentActivity);
			
			break;
		case R.id.tab_me:
			//TODO use content manager here instead
//			tryToUpdateChannelList();
//			Intent returnIntent = new Intent();
//			if (mIsChanged == true) {
//				setResult(Consts.INFO_UPDATE_MYCHANNELS, returnIntent);
//				returnIntent.putExtra(Consts.INFO_UPDATE_MYCHANNELS_NUMBER, mCount);
//			}
			finish();
			
			break;
		}

	}


	
	@Override
	protected void loadData() 
	{
		updateUI(UIStatusEnum.LOADING);
		
		//TODO NO need to fetch data right? should already be in storage!
//		ContentManager.sharedInstance().getElse
//		SSChannelPage.getInstance().getPage(Consts.URL_MY_CHANNEL_IDS, new SSPageCallback() 
//		{
//			@Override
//			public void onGetPageResult(SSPageGetResult aPageGetResult)
//			{
//				ArrayList<OldTVChannel> mAllChannels = SSChannelPage.getInstance().getChannels();
//
//				if (mAllChannels != null && mAllChannels.isEmpty() != true) 
//				{
//					Log.d(TAG, "ALL Channels: " + mAllChannels.size());
//					
//					// store the list channels (used in the my profile/my guide)
//					MiTVStoreOperations.saveChannels(mAllChannels);
//
//					updateUI(REQUEST_STATUS.SUCCESSFUL);
//				}
//			}
//
//		});
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult) 
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
				populateViews();
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
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			// Respond to the action bar's Up/Home button
			// update the channel list on Up/Home button press too
			case android.R.id.home:
			{
//				tryToUpdateChannelList();
				
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				
//				if(mIsChanged == true) 
//				{
//					setResult(Consts.INFO_UPDATE_MYCHANNELS, upIntent);
//					
//					upIntent.putExtra(Consts.INFO_UPDATE_MYCHANNELS_NUMBER, mCount);
//				}
//				
				NavUtils.navigateUpTo(this, upIntent);
				
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
}
