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
import com.millicom.mitv.enums.ContentTypeEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVSearchResult;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.adapters.SearchPageListAdapter;
import com.mitv.customviews.InstantAutoComplete;
import com.mitv.handlers.SearchActivityListeners;

public class SearchPageActivity extends BaseActivity implements OnItemClickListener, OnEditorActionListener, OnClickListener, SearchActivityListeners {

	@SuppressWarnings("unused")
	private static final String TAG = SearchPageActivity.class.getName();

	private SearchPageListAdapter mAutoCompleteAdapter;
	private LinearLayout mSearchInstructionsContainer;

	private ActionBar mActionBar;
	private Menu mMenu;
	private InstantAutoComplete mEditTextSearch;
	private ImageView mEditTextClearBtn;
	private ProgressBar mProgressBar;

	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_searchpage_activity);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

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

	
	
	private void initSupportActionbar() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);

		MenuItem startSearchMenuItem = menu.findItem(R.id.action_start_search);
		startSearchMenuItem.setVisible(false);
		
		MenuItem searchField = menu.findItem(R.id.searchfield);
		searchField.setVisible(true);
		
		this.mMenu = menu;
		
		initAutoCompleteLayout();
		initAutoCompleteListeners();
		loadAutoCompleteView();

		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	navigateUp();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	private void initAutoCompleteLayout() {
		MenuItem searchField = mMenu.findItem(R.id.searchfield);
		View searchFieldView = MenuItemCompat.getActionView(searchField);
		
		mProgressBar = (ProgressBar) searchFieldView.findViewById(R.id.searchbar_progress);
		mEditTextClearBtn = (ImageView) searchFieldView.findViewById(R.id.searchbar_clear);
		mEditTextSearch = (InstantAutoComplete) searchFieldView.findViewById(R.id.searchbar_edittext);
		mEditTextSearch.requestFocus();

	}

	private void initAutoCompleteListeners() {
		mEditTextClearBtn.setOnClickListener(this);
		mEditTextSearch.setOnItemClickListener(this);
		mEditTextSearch.setOnEditorActionListener(this);

		mEditTextSearch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mEditTextSearch.showDropDown();
				return false;
			}
		});

	}
		
	private void loadAutoCompleteView() {
		mAutoCompleteAdapter = new SearchPageListAdapter(SearchPageActivity.this, this);
		mEditTextSearch.setThreshold(1);
	
		int width = GenericUtils.getScreenWidth(this);
		
		mEditTextSearch.setDropDownWidth(width);
		mEditTextSearch.setAdapter(mAutoCompleteAdapter);
		mEditTextSearch.setDropDownVerticalOffset(0);
	}

	private void initMainLayout() {
		mSearchInstructionsContainer = (LinearLayout) findViewById(R.id.search_page_instruction_container);
	}

	
	
	@Override
	public void showProgressLoading(boolean isLoading) 
	{
		if (isLoading) {
			mProgressBar.setVisibility(View.VISIBLE);
			mEditTextClearBtn.setVisibility(View.GONE);
		} else {
			mProgressBar.setVisibility(View.GONE);
			mEditTextClearBtn.setVisibility(View.VISIBLE);
		}
	}

	
	
	@Override
	public void isRecentListEmpty(boolean isEmpty) 
	{
		mSearchInstructionsContainer.setVisibility(View.VISIBLE);
	}

	
	
	private void showKeyboard() 
	{
		mHandler.post(new Runnable() 
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
			mEditTextSearch.setText("");
			mEditTextSearch.dismissDropDown();
			mEditTextSearch.setAdapter(new SearchPageListAdapter(SearchPageActivity.this, this));
			break;
		}
		case R.id.searchbar_edittext: {
			mEditTextSearch.showDropDown();
			break;
		}
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			triggerAutoComplete();
			if (mEditTextSearch.getText().toString().length() >= 1) 
			{
				GenericUtils.hideKeyboard(this);
			}
			return true;
		}
		return false;
	}

	private void triggerAutoComplete() {
		if(mEditTextSearch != null) { 
			String query = mEditTextSearch.getText().toString();
			int pos = query.length();
			mEditTextSearch.setSelection(pos);
			mAutoCompleteAdapter.getFilter().filter(query);
			mEditTextSearch.showDropDown();
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

	
	
	@Override
	protected void loadData() 
	{
		// TODO NewArc - Implement this
	}
}
