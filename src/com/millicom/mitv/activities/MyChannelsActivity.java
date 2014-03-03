package com.millicom.mitv.activities;



import java.util.ArrayList;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVChannelId;
import com.mitv.R;
import com.mitv.adapters.MyChannelsListAdapter;
import com.mitv.interfaces.MyChannelsCountInterface;



public class MyChannelsActivity 
	extends BaseContentActivity 
	implements MyChannelsCountInterface, OnClickListener, TextWatcher
{
	private static final String TAG = MyChannelsActivity.class.getName();

	private Button getMyChannelsButton;
	private Button addToMyChannelsButton;
	private ListView listView;
	private TextView channelCountTextView;

	private EditText searchChannelField;
	
	private MyChannelsListAdapter adapter;
	

	private ArrayList<TVChannel> allChannelObjects = new ArrayList<TVChannel>();
	private ArrayList<TVChannelId> myChannelIds = new ArrayList<TVChannelId>();
	
	/* The channels that have been checked, initialized to: myChannelIds */
	private ArrayList<TVChannelId> checkedChannelIds = new ArrayList<TVChannelId>();
	
	/* The list of channels that matches the search performed */
	private ArrayList<TVChannel> channelsMatchingSearch = new ArrayList<TVChannel>();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_mychannels_activity);
		
		initLayout();
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
		} 
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}
	
	
	@Override
	protected void onStop() 
	{
		updateMyChannels();
		super.onStop();
	}

	
	
	@Override
	public void onBackPressed() 
	{
		updateMyChannels();
		super.onBackPressed();
		
		finish();
	}

	private void updateMyChannels() {
		//TODO NewArc verify that changes to check channels Ids in adapter has propagted back to this variable here, should be "code by reference"!
		ContentManager.sharedInstance().performSetUserChannels(this, checkedChannelIds);
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
		myChannelIds = ContentManager.sharedInstance().getFromCacheTVChannelIdsUser();
		
		checkedChannelIds = myChannelIds;
		channelsMatchingSearch = new ArrayList<TVChannel>(allChannelObjects);
		
		updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		
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
	public void afterTextChanged(Editable editable) {
		String search = editable.toString();
		if (search.contains(System.getProperty("line.separator"))) {
			search = search.replace(System.getProperty("line.separator"), "");
			searchChannelField.setText(search);
			InputMethodManager in = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
			in.hideSoftInputFromWindow(searchChannelField.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

		channelsMatchingSearch.clear();
		if (!TextUtils.isEmpty(search)) {
			
			/* Go through list of all channels and add channels which name contains the searched string */
			for(TVChannel tvChannel : allChannelObjects) {
				String channelName = tvChannel.getName();
				if (channelName.toLowerCase().contains(search.toLowerCase())) {
					channelsMatchingSearch.add(tvChannel);
				}
			}
		} else {
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
