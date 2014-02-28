package com.millicom.mitv.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.interfaces.SearchInterface;
import com.millicom.mitv.models.SearchResultsForQuery;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVSearchResult;
import com.millicom.mitv.models.TVSearchResults;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.adapters.SearchPageListAdapter;
import com.mitv.customviews.InstantAutoComplete;

public class SearchPageActivity extends BaseActivity implements OnItemClickListener, OnEditorActionListener, OnClickListener, SearchInterface, TextWatcher {
	@SuppressWarnings("unused")
	private static final String TAG = SearchPageActivity.class.getName();
	
	private static final int SEARCH_QUERY_LENGTH_THRESHOLD = 3;

	private SearchPageListAdapter autoCompleteAdapter;
	private LinearLayout searchInstructionsContainer;

	private Menu menu;
	private InstantAutoComplete editTextSearch;
	private ImageView editTextClearBtn;
	private ProgressBar progressBar;

	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_searchpage_activity);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		initMainLayout();

		initSupportActionbar();
	}

	@Override
	public void onResume() {
		super.onResume();

		showKeyboard();
	}

	@Override
	public void onPause() {
		super.onPause();

		GenericUtils.hideKeyboard(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		GenericUtils.hideKeyboard(this);
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

		editTextSearch = (InstantAutoComplete) searchFieldView.findViewById(R.id.searchbar_edittext);

		editTextSearch.addTextChangedListener(this);

		editTextSearch.requestFocus();
	}

	private void initAutoCompleteListeners() {
		editTextClearBtn.setOnClickListener(this);
		editTextSearch.setOnItemClickListener(this);
		editTextSearch.setOnEditorActionListener(this);

		editTextSearch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				editTextSearch.showDropDown();
				return false;
			}
		});

	}

	private void loadAutoCompleteView() {
		autoCompleteAdapter = new SearchPageListAdapter(SearchPageActivity.this, this);
		editTextSearch.setThreshold(1);

		int width = GenericUtils.getScreenWidth(this);

		editTextSearch.setDropDownWidth(width);
		editTextSearch.setAdapter(autoCompleteAdapter);
		editTextSearch.setDropDownVerticalOffset(0);
	}

	private void initMainLayout() {
		searchInstructionsContainer = (LinearLayout) findViewById(R.id.search_page_instruction_container);
	}

	@Override
	public void showProgressLoading(boolean isLoading) {
		if (isLoading) {
			progressBar.setVisibility(View.VISIBLE);
			editTextClearBtn.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.GONE);
			editTextClearBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void isRecentListEmpty(boolean isEmpty) {
		searchInstructionsContainer.setVisibility(View.VISIBLE);
	}

	private void showKeyboard() {
		handler.post(new Runnable() {
			public void run() {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

				inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

				// performSearchAndTriggerAutocomplete();
			}
		});
	}

	// Click listener for both recent list and search auto complete view
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

		TVSearchResult result = (TVSearchResult) adapterView.getItemAtPosition(position);

		if (result.getEntityType() != ContentTypeEnum.CHANNEL) {
			// open the detail view for the individual broadcast
			Intent intent = new Intent(SearchPageActivity.this, BroadcastPageActivity.class);
			intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, this.getClass().getName());

			// we take one position less as we have a header view
			int adjustedPosition = position - 1;
			if (adjustedPosition < 0) {
				/* Don't allow negative values */
				adjustedPosition = 0;
			}

			TVBroadcastWithChannelInfo nextBroadcast = result.getNextBroadcast();
			if (nextBroadcast != null) {
				ContentManager.sharedInstance().setSelectedBroadcastWithChannelInfo(nextBroadcast);
				startActivity(intent);
			} else {
				Toast.makeText(this, "No upcoming broadcast", Toast.LENGTH_SHORT).show();
			}
		} else {
			Intent intentMyChannels = new Intent(SearchPageActivity.this, MyChannelsActivity.class);
			startActivity(intentMyChannels);

		}
	}

	private void navigateUp() {
		// Intent upIntent = NavUtils.getParentActivityIntent(this);
		// NavUtils.navigateUpTo(this, upIntent);
		finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.searchbar_clear: {
			editTextSearch.setText("");
			editTextSearch.dismissDropDown();
			editTextSearch.setAdapter(new SearchPageListAdapter(SearchPageActivity.this, this));
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
			String searchQuery = editTextSearch.getText().toString();
			performSearch(searchQuery);

		}
	}

	private void triggerAutoComplete(String searchQuery) {
		if (editTextSearch != null) {
			int pos = editTextSearch.getText().toString().length();
			editTextSearch.setSelection(pos);
			autoCompleteAdapter.getFilter().filter(searchQuery);
			editTextSearch.showDropDown();
		}
	}

	public void performSearch(String searchQuery) {
		setLoading(); // TODO NewArc set this sets loading in actionbar field, do it in view as well?
		AjaxCallback<String> cb = new AjaxCallback<String>();
		ContentManager.sharedInstance().getElseFetchFromServiceSearchResultForSearchQuery(this, false, cb, searchQuery);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
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
		// TODO NewArc - Implement this
	}

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		setNotLoading();
		if (fetchRequestResult.wasSuccessful()) {
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
			SearchResultsForQuery searchResultsForQuery = ContentManager.sharedInstance().getFromCacheSearchResults();
			if (searchResultsForQuery != null) {
				String searchQuery = searchResultsForQuery.getQueryString();
				TVSearchResults searchResultsObject = searchResultsForQuery.getSearchResults();

				ArrayList<TVSearchResult> searchResultItems = new ArrayList<TVSearchResult>(searchResultsObject.getResults());
				autoCompleteAdapter.setSearchResultItems(searchResultItems);
				triggerAutoComplete(searchQuery);
			}
		} else {
			updateUI(UIStatusEnum.FAILED);
		}
	}

	@Override
	protected void updateUI(UIStatusEnum status) {
		super.updateUIBaseElements(status);

		switch (status) {
		case SUCCEEDED_WITH_DATA: {
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
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable editable) {
		String searchQuery = editable.toString();
		if(!TextUtils.isEmpty(searchQuery) && searchQuery.length() >= SEARCH_QUERY_LENGTH_THRESHOLD) {
			performSearchAndTriggerAutocomplete();
		}
	}
}
