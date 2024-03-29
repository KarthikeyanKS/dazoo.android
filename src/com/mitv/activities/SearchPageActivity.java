
package com.mitv.activities;



import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.activities.competition.EventPageActivity;
import com.mitv.adapters.list.SearchPageListAdapter;
import com.mitv.asynctasks.other.SearchRunnable;
import com.mitv.enums.ContentTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.SearchResultsForQuery;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVChannelId;
import com.mitv.models.objects.mitvapi.TVSearchResult;
import com.mitv.models.objects.mitvapi.TVSearchResults;
import com.mitv.ui.elements.InstantAutoCompleteView;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.GenericUtils;



public class SearchPageActivity 
	extends BaseActivity 
	implements OnItemClickListener, OnEditorActionListener, OnClickListener, TextWatcher 
{
	private static final String TAG = SearchPageActivity.class.getName();

	
	private SearchPageListAdapter autoCompleteAdapter;

	private Menu menu;
	private InstantAutoCompleteView editTextSearch;
	private TextView editTextClearBtn;
	private ProgressBar progressBar;
	private String lastSearchQuery;
	
	private SearchRunnable lastSearchRunnable;
	private Handler delayedSearchHandler;
	private Handler keyboardHandler;
	private LinearLayout searchInstructionsView;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_searchpage_activity);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		this.searchInstructionsView = (LinearLayout) findViewById(R.id.search_page_instruction_container);

		initSupportActionbar();
		
		keyboardHandler = new Handler();
		delayedSearchHandler = new Handler();
	}

	
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		showKeyboard();
		
		triggerAutoComplete();
	}

	
	@Override
	public void onPause()
	{
		super.onPause();
		TrackingGAManager.sharedInstance().sendUserSearchEvent(lastSearchQuery);
		GenericUtils.hideKeyboard(this);
	}
	

	@Override
	public void onBackPressed() 
	{
		GenericUtils.hideKeyboard(this);
		super.onBackPressed();
	}


	private void initSupportActionbar()
	{
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.actionbar_menu, menu);

		MenuItem startSearchMenuItem = menu.findItem(R.id.action_start_search);
		startSearchMenuItem.setVisible(false);

		MenuItem searchField = menu.findItem(R.id.searchfield);
		searchField.setVisible(true);

		this.menu = menu;

		initAutoCompleteLayout();
		initAutoCompleteListeners();
		loadAutoCompleteView();

		return true;
	}

	
	private void initAutoCompleteLayout() 
	{
		MenuItem searchField = menu.findItem(R.id.searchfield);

		View searchFieldView = MenuItemCompat.getActionView(searchField);

		progressBar = (ProgressBar) searchFieldView.findViewById(R.id.searchbar_progress);

		editTextClearBtn = (TextView) searchFieldView.findViewById(R.id.searchbar_clear);

		editTextSearch = (InstantAutoCompleteView) searchFieldView.findViewById(R.id.searchbar_edittext);
		editTextSearch.setActivity(this);
		editTextSearch.requestFocus();
	}

	
	
	private void initAutoCompleteListeners() 
	{
		editTextClearBtn.setOnClickListener(this);
		editTextSearch.setOnItemClickListener(this);
		editTextSearch.addTextChangedListener(this);
		editTextSearch.setOnEditorActionListener(this);
	}

	
	
	private void loadAutoCompleteView() 
	{
		autoCompleteAdapter = new SearchPageListAdapter(this);
		editTextSearch.setThreshold(Constants.SEARCH_QUERY_LENGTH_THRESHOLD - 1);

		int width = GenericUtils.getScreenWidth(this);

		editTextSearch.setDropDownWidth(width);
		editTextSearch.setAdapter(autoCompleteAdapter);
		editTextSearch.setDropDownVerticalOffset(0);
	}


	public void showProgressLoading(boolean isLoading) 
	{
		if (isLoading) 
		{
			progressBar.setVisibility(View.VISIBLE);
			editTextClearBtn.setVisibility(View.GONE);
		} 
		else
		{
			progressBar.setVisibility(View.GONE);
			editTextClearBtn.setVisibility(View.VISIBLE);
		}
	}

	
	private void showKeyboard() 
	{
		keyboardHandler.post(new Runnable() 
		{
			public void run() 
			{
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

				inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

				performSearchAndTriggerAutocomplete();
			}
		});
	}


	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{
		TVSearchResult result = (TVSearchResult) adapterView.getItemAtPosition(position);

		ContentTypeEnum resultResultType = result.getEntityType();

		String hitName;
		
		switch (resultResultType) 
		{
			case CHANNEL: 
			{
				TVChannel tvChannel = result.getEntity().getChannel();
				TVChannelId channelId = tvChannel.getChannelId();
				
				hitName = tvChannel.getName();
				
				Intent intent;
				
				if (ContentManager.sharedInstance().getCacheManager().isContainedInUsedChannelIds(channelId)) 
				{
					ContentManager.sharedInstance().getCacheManager().setSelectedTVChannelId(channelId);
					intent = new Intent(SearchPageActivity.this, ChannelPageActivity.class);
				} 
				else
				{
					if (ContentManager.sharedInstance().getCacheManager().isLoggedIn())
					{
						ContentManager.sharedInstance().getCacheManager().setSelectedTVChannelId(channelId);
						ContentManager.sharedInstance().setGoingToMyChannelsFromSearch(true);
						intent = new Intent(SearchPageActivity.this, MyChannelsActivity.class);
					} 
					else
					{
						intent = new Intent(SearchPageActivity.this, SignUpSelectionActivity.class);
					}
				}
				startActivity(intent);
				break;
			}
			
			default:
			{
				/* Open the detail view for the individual broadcast */
				TVBroadcastWithChannelInfo nextBroadcast = result.getNextBroadcast();
				
				if (nextBroadcast != null) 
				{
					hitName = nextBroadcast.getTitle();
					
					boolean isCompetitionEvent = false;
					long competitionId = 0;
					long eventId = 0;
					
					Intent intent;
					
					List<String> tags = nextBroadcast.getProgram().getTags();

					if (tags != null && !tags.isEmpty()) 
					{
						for (int i = 0; i < tags.size(); i++) 
						{
							if (tags.get(i).equals(Constants.FIFA_TAG_ID))
							{
								isCompetitionEvent = true;
								
								eventId = nextBroadcast.getEventId();

								/*
								 * TODO: Hard coded competition ID used here.
								 * 
								 */
								competitionId = Constants.FIFA_COMPETITION_ID;
								
								break;
							}
						}
					}
					
					if(isCompetitionEvent)
					{
						if (competitionId > 0 && eventId > 0) 
						{
							intent = new Intent(SearchPageActivity.this, EventPageActivity.class);

							intent.putExtra(Constants.INTENT_COMPETITION_ID, competitionId);
							intent.putExtra(Constants.INTENT_COMPETITION_EVENT_ID, eventId);
						}
						else
						{
							Log.w(TAG, "Competition search result had values competitionId: " + competitionId + " and eventId: " + eventId);
							
							intent = new Intent(SearchPageActivity.this, BroadcastPageActivity.class);
							
							ContentManager.sharedInstance().getCacheManager().pushToSelectedBroadcastWithChannelInfo(nextBroadcast);
						}
					}
					else
					{
						intent = new Intent(SearchPageActivity.this, BroadcastPageActivity.class);
						
						ContentManager.sharedInstance().getCacheManager().pushToSelectedBroadcastWithChannelInfo(nextBroadcast);
					}
					
					startActivity(intent);
				} 
				else 
				{
					hitName = "";
					
					String message = getString(R.string.search_no_upcoming_broadcasts);
					
					ToastHelper.createAndShowShortToast(message);
				}
				break;
			}
		}

		TrackingGAManager.sharedInstance().sendUserSearchResultPressedEvent(lastSearchQuery, hitName, position);
		
		GenericUtils.hideKeyboard(this);
	}

	
	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		switch (v.getId()) 
		{
			case R.id.searchbar_clear: 
			{
				editTextSearch.setText("");
				editTextSearch.dismissDropDown();
				editTextClearBtn.setVisibility(View.GONE);
				break;
			}
			
			case R.id.searchbar_edittext: 
			{
				editTextSearch.showDropDown();
				break;
			}
		}
	}

	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (actionId == EditorInfo.IME_ACTION_SEARCH) 
		{
			TrackingGAManager.sharedInstance().sendUserPressedSearchButtonOnKeyBoard();
			performSearchAndTriggerAutocomplete();
			return true;
		}
		
		return false;
	}

	
	private void performSearchAndTriggerAutocomplete()
	{
		if (editTextSearch != null)
		{
			Log.d(TAG, "Creating new searchRunnable and starting count down");
			lastSearchRunnable = new SearchRunnable(this, editTextSearch);
			delayedSearchHandler.postDelayed(lastSearchRunnable, Constants.DELAY_IN_MILLIS_UNTIL_SEARCH);
		}
	}

	
	private void triggerAutoComplete() 
	{
		if (editTextSearch != null && lastSearchQuery != null) 
		{
			Filter filter = autoCompleteAdapter.getFilter();
			filter.filter(lastSearchQuery);
			editTextSearch.showDropDown();
			
			searchInstructionsView.setVisibility(View.GONE);
		}
	}
	
	
	public void setLoading() 
	{
		changeLoadingStatus(true);
	}

	
	private void setNotLoading() 
	{
		changeLoadingStatus(false);
	}

	
	private void changeLoadingStatus(final boolean loading) 
	{
		showProgressLoading(loading);
	}

	
	
	@Override
	protected void loadData() 
	{
		/* Do nothing */
	}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return true;
	}
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		setNotLoading();
		
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
			
			SearchResultsForQuery searchResultsForQuery = ContentManager.sharedInstance().getCacheManager().getSearchResults();
			
			if (searchResultsForQuery != null) 
			{
				String searchQuery = searchResultsForQuery.getQueryString();
				
				TVSearchResults searchResultsObject = searchResultsForQuery.getSearchResults();

				ArrayList<TVSearchResult> searchResultItems = new ArrayList<TVSearchResult>(searchResultsObject.getResults());
				
				autoCompleteAdapter.setSearchResultItemsForQueryString(searchResultItems, searchQuery);
				
				editTextSearch.setSearchComplete(true);
				
				this.lastSearchQuery = searchQuery;
				
				triggerAutoComplete();
			}
		}
		else if(fetchRequestResult == FetchRequestResultEnum.SEARCH_CANCELED_BY_USER) 
		{
			Log.d(TAG, "Search canceled by user");
			
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
	}

	
	
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

	
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

	
	
	@Override
	public void afterTextChanged(Editable editable) 
	{
		String searchQuery = editable.toString();
		
		if(lastSearchRunnable != null) 
		{
			Log.d(TAG, "Text change, cancelling last searchRunnable");
			lastSearchRunnable.cancel();
		}
		
		editTextSearch.setSearchComplete(false);
		
		if(!TextUtils.isEmpty(searchQuery) && searchQuery.length() >= Constants.SEARCH_QUERY_LENGTH_THRESHOLD) 
		{
			performSearchAndTriggerAutocomplete();
		} 
		else 
		{
			autoCompleteAdapter.setSearchResultItemsForQueryString(null, null);
			autoCompleteAdapter.clear();
			searchInstructionsView.setVisibility(View.VISIBLE);
		}
	}
}
