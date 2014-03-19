
package com.mitv.activities;



import java.util.ArrayList;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.mitv.Constants;
import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.ContentTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.listadapters.SearchPageListAdapter;
import com.mitv.models.SearchResultsForQuery;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.TVChannelId;
import com.mitv.models.TVSearchResult;
import com.mitv.models.TVSearchResults;
import com.mitv.ui.elements.InstantAutoCompleteView;
import com.mitv.utilities.GenericUtils;



public class SearchPageActivity 
	extends BaseActivity 
	implements OnItemClickListener, OnEditorActionListener, OnClickListener, TextWatcher 
{
	private class SearchRunnable implements Runnable {

		private boolean cancelled = false;
		
		@Override
		public void run() {
			if(!cancelled) {
				String searchQuery = editTextSearch.getText().toString();
				setLoading(); // TODO NewArc set this sets loading in actionbar field, do it in view as well?
				Log.d(TAG, "Search was not cancelled, calling ContentManager search!!!");
				editTextSearch.setSearchComplete(false);
				ContentManager.sharedInstance().getElseFetchFromServiceSearchResultForSearchQuery(SearchPageActivity.this, false, searchQuery);
			} else {
				Log.d(TAG, "Search runnable was cancelled");
			}
		}

		public void cancel() {
			cancelled = true;
		}
	}
	

	private static final String TAG = SearchPageActivity.class.getName();

	
	private SearchPageListAdapter autoCompleteAdapter;

	private Menu menu;
	private InstantAutoCompleteView editTextSearch;
	private ImageView editTextClearBtn;
	private ProgressBar progressBar;
	private String searchQuery;
	
	private SearchRunnable lastSearchRunnable;
	private Handler delayedSearchHandler;
	private Handler keyboardHandler;

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_searchpage_activity);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		initSupportActionbar();
		
		keyboardHandler = new Handler();
		delayedSearchHandler = new Handler();
	}

	
	@Override
	public void onResume() {
		super.onResume();
		
		showKeyboard();
		triggerAutoComplete();
	}

	@Override
	public void onPause() {
		super.onPause();
		GenericUtils.hideKeyboard(this);
	}

	@Override
	public void onDestroy() {
		GenericUtils.hideKeyboard(this);
		super.onDestroy();
	}

	@Override
	public void finish() {
		GenericUtils.hideKeyboard(this);
		super.finish();
	}

	private void initSupportActionbar() {
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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

	private void initAutoCompleteLayout() {
		MenuItem searchField = menu.findItem(R.id.searchfield);

		View searchFieldView = MenuItemCompat.getActionView(searchField);

		progressBar = (ProgressBar) searchFieldView.findViewById(R.id.searchbar_progress);

		editTextClearBtn = (ImageView) searchFieldView.findViewById(R.id.searchbar_clear);

		editTextSearch = (InstantAutoCompleteView) searchFieldView.findViewById(R.id.searchbar_edittext);
		editTextSearch.setActivity(this);
		editTextSearch.requestFocus();
	}

	private void initAutoCompleteListeners() {
		editTextClearBtn.setOnClickListener(this);
		editTextSearch.setOnItemClickListener(this);
		editTextSearch.addTextChangedListener(this);
		editTextSearch.setOnEditorActionListener(this);
	}

	private void loadAutoCompleteView() {
		autoCompleteAdapter = new SearchPageListAdapter(this);
		editTextSearch.setThreshold(Constants.SEARCH_QUERY_LENGTH_THRESHOLD - 1);

		int width = GenericUtils.getScreenWidth(this);

		editTextSearch.setDropDownWidth(width);
		editTextSearch.setAdapter(autoCompleteAdapter);
		editTextSearch.setDropDownVerticalOffset(0);
	}


	public void showProgressLoading(boolean isLoading) {
		if (isLoading) {
			progressBar.setVisibility(View.VISIBLE);
			editTextClearBtn.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.GONE);
			editTextClearBtn.setVisibility(View.VISIBLE);
		}
	}

	private void showKeyboard() {
		keyboardHandler.post(new Runnable() {
			public void run() {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

				inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

				 performSearchAndTriggerAutocomplete();
			}
		});
	}

	// Click listener for both recent list and search auto complete view
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

		TVSearchResult result = (TVSearchResult) adapterView.getItemAtPosition(position);


		ContentTypeEnum resultResultType = result.getEntityType();


		switch (resultResultType) {
		case CHANNEL: {
			TVChannelId channelId = result.getEntity().getChannel().getChannelId();
			Intent intent;
			if (ContentManager.sharedInstance().isContainedInUsedChannelIds(channelId)) {
				ContentManager.sharedInstance().setSelectedTVChannelId(channelId);
				intent = new Intent(SearchPageActivity.this, ChannelPageActivity.class);
			} else {
				if (ContentManager.sharedInstance().isLoggedIn()) {
					intent = new Intent(SearchPageActivity.this, MyChannelsActivity.class);
				} else {
					intent = new Intent(SearchPageActivity.this, SignUpSelectionActivity.class);
				}
			}
			startActivity(intent);
			break;
		}
		default: {
			/* Open the detail view for the individual broadcast */
			TVBroadcastWithChannelInfo nextBroadcast = result.getNextBroadcast();
			if (nextBroadcast != null) {
				Intent intent = new Intent(SearchPageActivity.this, BroadcastPageActivity.class);
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(nextBroadcast);
				startActivity(intent);
			} else {
				Toast.makeText(this, "No upcoming broadcast", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.searchbar_clear: {
			editTextSearch.setText("");
			editTextSearch.dismissDropDown();
			
			//TODO NewArc is this needed? Feels unnessary
			editTextSearch.setAdapter(new SearchPageListAdapter(this));
			break;
		}
		case R.id.searchbar_edittext: {
			editTextSearch.showDropDown();
			break;
		}
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			performSearchAndTriggerAutocomplete();
			return true;
		}
		return false;
	}

	private void performSearchAndTriggerAutocomplete() {
		if (editTextSearch != null) {
			Log.d(TAG, "Creating new searchRunnable and starting count down");
			lastSearchRunnable = new SearchRunnable();
			delayedSearchHandler.postDelayed(lastSearchRunnable, Constants.DELAY_IN_MILLIS_UNTIL_SEARCH);
		}
	}

	private void triggerAutoComplete() {
		if (editTextSearch != null && searchQuery != null) {
			int pos = editTextSearch.getText().toString().length();
			editTextSearch.setSelection(pos);
			autoCompleteAdapter.getFilter().filter(searchQuery);
			editTextSearch.showDropDown();
		}
	}
	
	private void setLoading() {
		changeLoadingStatus(true);
	}

	private void setNotLoading() {
		changeLoadingStatus(false);
	}

	private void changeLoadingStatus(final boolean loading) {
		showProgressLoading(loading);
	}

	@Override
	protected void loadData() {
		// TODO NewArc - do we need anything here?
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		// TODO NewArc - Implement this
		return true;
	}
	
	

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		setNotLoading();
		
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
			
			SearchResultsForQuery searchResultsForQuery = ContentManager.sharedInstance().getFromCacheSearchResults();
			
			if (searchResultsForQuery != null) 
			{
				String searchQuery = searchResultsForQuery.getQueryString();
				
				TVSearchResults searchResultsObject = searchResultsForQuery.getSearchResults();

				ArrayList<TVSearchResult> searchResultItems = new ArrayList<TVSearchResult>(searchResultsObject.getResults());
				
				autoCompleteAdapter.setSearchResultItemsForQueryString(searchResultItems, searchQuery);
				editTextSearch.setSearchComplete(true);
				
				this.searchQuery = searchQuery;
				
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

		switch (status)
		{
			case SUCCESS_WITH_CONTENT: 
			{
				// TODO NewArc - Do something here?
				break;
			}
	
			default: {
				// TODO NewArc - Do something here?
				break;
			}
		}
	}

	
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

	
	@Override
	public void afterTextChanged(Editable editable) 
	{
		String searchQuery = editable.toString();
		
		if(lastSearchRunnable != null) {
			Log.d(TAG, "Text change, cancelling last searchRunnable");
			lastSearchRunnable.cancel();
		}
		
		if(!TextUtils.isEmpty(searchQuery) && searchQuery.length() >= Constants.SEARCH_QUERY_LENGTH_THRESHOLD) 
		{
			performSearchAndTriggerAutocomplete();
		}
	}
}
