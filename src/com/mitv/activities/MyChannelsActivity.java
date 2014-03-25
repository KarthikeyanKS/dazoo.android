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
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseActivityLoginRequired;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.interfaces.MyChannelsCountInterface;
import com.mitv.listadapters.MyChannelsListAdapter;
import com.mitv.models.TVChannel;
import com.mitv.models.TVChannelId;
import com.mitv.models.comparators.TVChannelComparatorByName;
import com.mitv.models.comparators.TVChannelIdComparatorById;
import com.mitv.utilities.LanguageUtils;
import com.mitv.utilities.ListUtils;
import com.mitv.utilities.NetworkUtils;



public class MyChannelsActivity 
	extends BaseActivityLoginRequired 
	implements MyChannelsCountInterface, OnClickListener, TextWatcher
{
	@SuppressWarnings("unused")
	private static final String TAG = MyChannelsActivity.class.getName();

	private ListView listView;
	private TextView channelCountTextView;

	private EditText searchChannelField;
	
	private MyChannelsListAdapter adapter;

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
		if(isGoingToMyChannelsFromSearch) {
			ContentManager.sharedInstance().setGoingToMyChannelsFromSearch(false);
			TVChannelId selectedTVChannelId = ContentManager.sharedInstance().getFromCacheSelectedTVChannelId();
			selectedTVChannelFromSearch = ContentManager.sharedInstance().getFromCacheTVChannelById(selectedTVChannelId);
		}
	}
	
	
	
	private void initLayout()
	{
		actionBar.setTitle(getResources().getString(R.string.myprofile_my_channels));
		actionBar.setDisplayHomeAsUpEnabled(true);


		listView = (ListView) findViewById(R.id.listview);
		channelCountTextView = (TextView) findViewById(R.id.mychannels_header_counter_tv);
		searchChannelField = (EditText) findViewById(R.id.mychannels_header_search_ev);
	}

	
	
	private void populateViews() 
	{		
		setSelectedChannelCount(checkedChannelIds.size());
		
		adapter = new MyChannelsListAdapter(this, channelsMatchingSearch, checkedChannelIds);
		listView.setAdapter(adapter);
		if (allChannelObjects != null && !allChannelObjects.isEmpty())
		{
			searchChannelField.addTextChangedListener(this);
			if(selectedTVChannelFromSearch != null) {
				searchChannelField.setText(selectedTVChannelFromSearch.getName());
			}
		} 
		else
		{
			updateUI(UIStatusEnum.FAILED);
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
		updateMyChannels();
		super.onPause();
	}
	

	private void updateMyChannels() {
		if(channelsHaveChanged()) {
			ArrayList<TVChannelId> tvChannelsForNewGuides = getOnlyNewTVChannelIds();
			ContentManager.sharedInstance().setNewTVChannelIdsAndFetchGuide(this, tvChannelsForNewGuides, checkedChannelIds);
		}
	}
	
	private ArrayList<TVChannelId> getOnlyNewTVChannelIds() {
		List<TVChannelId> idsInCache = ContentManager.sharedInstance().getFromCacheTVChannelIdsUser();
		ArrayList<TVChannelId> onlyNewTVChannelIdsIfAny = new ArrayList<TVChannelId>();
		for(TVChannelId channelId : checkedChannelIds) {
			if(!idsInCache.contains(channelId)) {
				onlyNewTVChannelIdsIfAny.add(channelId);
			}
		}
		return onlyNewTVChannelIdsIfAny;
	}
	
	private boolean channelsHaveChanged() {
		List<TVChannelId> idsInCache = ContentManager.sharedInstance().getFromCacheTVChannelIdsUser();
		boolean listIdentical = ListUtils.deepEquals(idsInCache, checkedChannelIds, new TVChannelIdComparatorById());
		boolean channelsHaveChanged = !listIdentical;
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
		myChannelIds = new ArrayList<TVChannelId>(ContentManager.sharedInstance().getFromCacheTVChannelIdsUser());
		
		checkedChannelIds = myChannelIds;
		channelsMatchingSearch = new ArrayList<TVChannel>(allChannelObjects);
		
		updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);	
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		boolean isConnected = NetworkUtils.isConnected();

		if (isConnected) 
		{
			boolean hasEnoughDataToShowContent = ContentManager.sharedInstance().getFromCacheHasUserTVChannelIds()
					 && ContentManager.sharedInstance().getFromCacheHasTVChannelsAll();
			return hasEnoughDataToShowContent;
		}
		else
		{
			return false;
		}
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
		String search = editable.toString();
		
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
		}

		adapter.setChannelsMatchingSearchAndRefreshAdapter(channelsMatchingSearch);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
