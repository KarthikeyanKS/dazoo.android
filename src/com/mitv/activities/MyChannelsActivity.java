
package com.mitv.activities;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.base.BaseActivityLoginRequired;
import com.mitv.adapters.list.MyChannelsListAdapter;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.MyChannelsCountInterface;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.comparators.TVChannelComparatorByName;
import com.mitv.models.comparators.TVChannelIdComparatorById;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.utilities.LanguageUtils;
import com.mitv.utilities.ListUtils;
import com.mitv.utilities.NetworkUtils;



public class MyChannelsActivity 
	extends BaseActivityLoginRequired 
	implements MyChannelsCountInterface, TextWatcher
{
	private static final String TAG = MyChannelsActivity.class.getName();

	private ListView listView;
	private TextView channelCountTextView;

	private EditText searchChannelField;
	private TextView editTextClearBtn;
	
	private MyChannelsListAdapter adapter;
	
	private String search;

	private List<TVChannel> allChannelObjects = new ArrayList<TVChannel>();
	private ArrayList<TVChannelId> myChannelIds = new ArrayList<TVChannelId>();
	
	/* The channels that have been checked, initialized to: myChannelIds */
	private ArrayList<TVChannelId> checkedChannelIds = new ArrayList<TVChannelId>();
	
	/* The list of channels that matches the search performed */
	private ArrayList<TVChannel> channelsMatchingSearch = new ArrayList<TVChannel>();
	private TVChannel selectedTVChannelFromSearch;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.layout_mychannels_activity);
		
		initLayout();
		
		registerAsListenerForRequest(RequestIdentifierEnum.TV_GUIDE_STANDALONE);
		
		boolean isGoingToMyChannelsFromSearch = ContentManager.sharedInstance().isGoingToMyChannelsFromSearch();
		
		if(isGoingToMyChannelsFromSearch) 
		{
			ContentManager.sharedInstance().setGoingToMyChannelsFromSearch(false);
			
			TVChannelId selectedTVChannelId = ContentManager.sharedInstance().getFromCacheSelectedTVChannelId();
			
			selectedTVChannelFromSearch = ContentManager.sharedInstance().getFromCacheTVChannelById(selectedTVChannelId);
		}
	}
	
	
	
	private void initLayout()
	{
		actionBar.setTitle(getString(R.string.myprofile_my_channels));
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				TrackingGAManager.sharedInstance().sendUserPressedChannelInMyChannelsActivity();
			}
		});
		
		channelCountTextView = (TextView) findViewById(R.id.mychannels_header_counter_tv);
		searchChannelField = (EditText) findViewById(R.id.mychannels_header_search_ev);
		
		editTextClearBtn = (TextView) findViewById(R.id.searchbar_clear_x);
		editTextClearBtn.setOnClickListener(this);
	}

	
	
	private void populateViews() 
	{	
		allChannelObjects = ContentManager.sharedInstance().getFromCacheTVChannelsAll();
		
		/* Sort all channels by name */
		Collections.sort(allChannelObjects, new TVChannelComparatorByName());
		
		/* Important, we need a copy, not the referenced list, since we dont want to change it. */
		myChannelIds = new ArrayList<TVChannelId>(ContentManager.sharedInstance().getFromCacheTVChannelIdsUsed());
		checkedChannelIds = myChannelIds;
		
		channelsMatchingSearch = new ArrayList<TVChannel>(allChannelObjects);
		
		setSelectedChannelCount(checkedChannelIds.size());
		
		adapter = new MyChannelsListAdapter(this, channelsMatchingSearch, checkedChannelIds);
		
		listView.setAdapter(adapter);
		
		if (allChannelObjects.isEmpty() == false)
		{
			searchChannelField.addTextChangedListener(this);
			
			if(selectedTVChannelFromSearch != null) 
			{
				searchChannelField.setText(selectedTVChannelFromSearch.getName());
			}
		}
	}
	

	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();	
		
		finish();
	}

	
	
	@Override
	public void onPause() 
	{
		TrackingGAManager.sharedInstance().sendUserMyChannelsPageSearchEvent(search);
		
		Log.e(TAG, "onPause invoked");
		
		updateMyChannels();
		
		super.onPause();
	}
	

	
	private void updateMyChannels() 
	{
		boolean channelsHaveChanged = channelsHaveChanged();
		
		if(channelsHaveChanged)
		{
			ArrayList<TVChannelId> tvChannelsForNewGuides = getOnlyNewTVChannelIds();
			
			if(tvChannelsForNewGuides.isEmpty() == false)
			{
				Log.e(TAG, "Adding " + tvChannelsForNewGuides.size() + " new channels");
				
				//ContentManager.sharedInstance().setNewTVChannelIdsAndFetchGuide(this, tvChannelsForNewGuides, checkedChannelIds);
			}
			else
			{
				Log.e(TAG, "Internal inconsitency - no new channels are to be added");
			}
		}
	}
	
	
	
	private ArrayList<TVChannelId> getOnlyNewTVChannelIds()
	{
		List<TVChannelId> idsInCache = ContentManager.sharedInstance().getFromCacheTVChannelIdsUsed();
		
		ArrayList<TVChannelId> onlyNewTVChannelIdsIfAny = new ArrayList<TVChannelId>();
		
		for(TVChannelId channelId : checkedChannelIds) 
		{
			if(idsInCache.contains(channelId) == false)
			{
				onlyNewTVChannelIdsIfAny.add(channelId);
			}
		}
		
		return onlyNewTVChannelIdsIfAny;
	}
	
	
	
	private boolean channelsHaveChanged() 
	{
		List<TVChannelId> idsInCache = ContentManager.sharedInstance().getFromCacheTVChannelIdsUsed();
		
		boolean listIdentical = ListUtils.deepEquals(idsInCache, checkedChannelIds, new TVChannelIdComparatorById());
		
		boolean channelsHaveChanged = (listIdentical == false);
		
		return channelsHaveChanged;
	}

	
	
	@Override
	public void setSelectedChannelCount(int count) 
	{
		channelCountTextView.setText(" " + String.valueOf(count));
	}
	

	
	@Override
	protected void loadData() 
	{
		allChannelObjects = ContentManager.sharedInstance().getFromCacheTVChannelsAll();
		
		/* Sort all channels by name */
		if(allChannelObjects != null) 
		{
			Collections.sort(allChannelObjects, new TVChannelComparatorByName());
		}
		
		/* Important, we need a copy, not the referenced list, since we dont want to change it. */
		myChannelIds = new ArrayList<TVChannelId>(ContentManager.sharedInstance().getFromCacheTVChannelIdsUsed());
		
		checkedChannelIds = myChannelIds;
		channelsMatchingSearch = new ArrayList<TVChannel>(allChannelObjects);
		updateUI(UIStatusEnum.LOADING);
		
		ContentManager.sharedInstance().getElseFetchFromServiceTVGuideUsingSelectedTVDate(this, false);
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean hasEnoughDataToShowContent = false;
		
		boolean isConnected = NetworkUtils.isConnected();

		if (isConnected) 
		{
			hasEnoughDataToShowContent = ContentManager.sharedInstance().getFromCacheHasUserTVChannelIds()
					 && ContentManager.sharedInstance().getFromCacheHasTVChannelsAll();
		}
		
		return hasEnoughDataToShowContent;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
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
			case USER_TOKEN_EXPIRED:
			{
				/* If the sessions has expired, finish this activity and resume the previous one */
				finish();
				break;
			}
		
			case SUCCESS_WITH_CONTENT:
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
	public void afterTextChanged(Editable editable)
	{
		search = editable.toString();
		
		if (search.contains(System.getProperty("line.separator"))) 
		{	
			search = search.replace(System.getProperty("line.separator"), "");
			
			searchChannelField.setText(search);
			
			getApplicationContext();
			
			InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			
			in.hideSoftInputFromWindow(searchChannelField.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

		channelsMatchingSearch.clear();
		
		if (!TextUtils.isEmpty(search)) 
		{		
			editTextClearBtn.setVisibility(View.VISIBLE);
			
			/* Go through list of all channels and add channels which name contains the searched string */
			for(TVChannel tvChannel : allChannelObjects) 
			{
				String channelName = tvChannel.getName();
				
				Locale locale = LanguageUtils.getCurrentLocale();
				
				if (channelName.toLowerCase(locale).contains(search.toLowerCase(locale))) 
				{
					channelsMatchingSearch.add(tvChannel);
				}
			}
		} 
		else 
		{
			/* If search string is empty show all channel objects */
			channelsMatchingSearch.addAll(allChannelObjects);
			
			editTextClearBtn.setVisibility(View.INVISIBLE);
		}

		adapter.setChannelsMatchingSearchAndRefreshAdapter(search, channelsMatchingSearch);
	}
	
	
	
	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		int viewID = v.getId();
		
		switch(viewID) 
		{
			case R.id.searchbar_clear_x: 
			{
				searchChannelField.setText("");
				
				editTextClearBtn.setVisibility(View.INVISIBLE);
				
				/* Hide keyboard when pressing clean button */
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchChannelField.getWindowToken(), 0);
				break;
			}
			
			default:
			{
				Log.w(TAG, "Unhandled onClick id");
				break;
			}
		}
	}

	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
