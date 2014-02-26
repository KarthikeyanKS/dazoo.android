
package com.millicom.mitv.activities;



import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
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

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVSearchResult;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.adapters.SearchPageListAdapter;
import com.mitv.customviews.InstantAutoComplete;
import com.mitv.handlers.SearchActivityListeners;



public class SearchPageActivity 
	extends BaseActivity 
	implements OnItemClickListener, OnEditorActionListener, OnClickListener, SearchActivityListeners 
{
	@SuppressWarnings("unused")
	private static final String TAG = SearchPageActivity.class.getName();

	
	private SearchPageListAdapter autoCompleteAdapter;
	private LinearLayout searchInstructionsContainer;

	private Menu menu;
	private InstantAutoComplete editTextSearch;
	private ImageView editTextClearBtn;
	private ProgressBar progressBar;

	private Handler handler = new Handler();

	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_searchpage_activity);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		initMainLayout();
		
		initSupportActionbar();
	}

	
	
	@Override
	public void onResume() 
	{
		super.onResume();
		
		showKeyboard();
	}

	
	
	@Override
	public void onPause() 
	{
		super.onPause();
		
		GenericUtils.hideKeyboard(this);
	}
	
	
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		
		GenericUtils.hideKeyboard(this);
	}
	
	

	@Override
	public void finish() 
	{
		GenericUtils.hideKeyboard(this);

		super.finish();
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
		
		editTextClearBtn = (ImageView) searchFieldView.findViewById(R.id.searchbar_clear);
		
		editTextSearch = (InstantAutoComplete) searchFieldView.findViewById(R.id.searchbar_edittext);
		
		editTextSearch.requestFocus();
	}

	
	
	private void initAutoCompleteListeners()
	{
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
	public void showProgressLoading(boolean isLoading) 
	{
		if (isLoading) {
			progressBar.setVisibility(View.VISIBLE);
			editTextClearBtn.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.GONE);
			editTextClearBtn.setVisibility(View.VISIBLE);
		}
	}

	
	
	@Override
	public void isRecentListEmpty(boolean isEmpty) 
	{
		searchInstructionsContainer.setVisibility(View.VISIBLE);
	}

	
	
	private void showKeyboard() 
	{
		handler.post(new Runnable() 
		{
			public void run() 
			{
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				
				inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				
				triggerAutoComplete();
			}
		});
	}

	

	// Click listener for both recent list and search auto complete view
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

		TVSearchResult result = (TVSearchResult) adapterView.getItemAtPosition(position);
		
		if(result.getEntityType() != ContentTypeEnum.CHANNEL) {
			// open the detail view for the individual broadcast
			Intent intent = new Intent(SearchPageActivity.this, BroadcastPageActivity.class);
			intent.putExtra(Consts.INTENT_EXTRA_RETURN_ACTIVITY_CLASS_NAME, this.getClass().getName());
	
			// we take one position less as we have a header view
			int adjustedPosition = position - 1;
			if(adjustedPosition < 0) {
				/* Don't allow negative values */
				adjustedPosition = 0;
			}
			
			TVBroadcastWithChannelInfo nextBroadcast = result.getNextBroadcast();
			if(nextBroadcast != null) {
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
			triggerAutoComplete();
			if (editTextSearch.getText().toString().length() >= 1) 
			{
				GenericUtils.hideKeyboard(this);
			}
			return true;
		}
		return false;
	}

	private void triggerAutoComplete() {
		if(editTextSearch != null) { 
			String query = editTextSearch.getText().toString();
			int pos = query.length();
			editTextSearch.setSelection(pos);
			autoCompleteAdapter.getFilter().filter(query);
			editTextSearch.showDropDown();
		}
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		
		finish();
	}
	
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		
	}

	
	
	@Override
	protected void loadData() 
	{
		// TODO NewArc - Implement this
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
				break;
			}
	
			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}
	}
}
