package com.millicom.secondscreen.content.search;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.model.SearchResultItem;
import com.millicom.secondscreen.customviews.InstantAutoComplete;

public class SearchPageActivity extends SSActivity implements OnItemClickListener, OnEditorActionListener, OnClickListener, SearchActivityListeners { 

	private static final String TAG = "SearchPageActivity";

	private SearchPageListAdapter mAutoCompleteAdapter;
	private LinearLayout mSearchInstructionsContainer;

	private RelativeLayout mBackButton;
	private ActionBar actionBar;
	private InstantAutoComplete mEditTextSearch;
	private RelativeLayout mEditTextClearBtn;
	private RelativeLayout mProgressBar;
	
	private Handler mHandler= new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.layout_searchpage_activity);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); 
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);
		
		// CUSTOMIZE DEFAULT ACTIONBAR
		
		initSupportActionbar();
		initAutoCompleteLayout();
		initAutoCompleteListeners();
		loadAutoCompleteView();
		initMainLayout();

	}
	
	@Override
	public void onResume() {
		super.onResume();
//		showKeyboard();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		hideKeyboard();
	}

	private void initSupportActionbar() {
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        
	    View customActionBarView = getLayoutInflater().inflate(R.layout.actionbar_search_activity, null);
	    actionBar.setCustomView(customActionBarView,
	                            new ActionBar.LayoutParams(
	                            		ActionBar.LayoutParams.MATCH_PARENT,
	                            		ActionBar.LayoutParams.MATCH_PARENT));
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	    
	
	    mBackButton = (RelativeLayout) actionBar.getCustomView().findViewById(R.id.actionbar_back);
	    mBackButton.setOnClickListener(this);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu items for use in the action bar
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.main, menu);
//		
//		MenuItem menuItem = menu.findItem(R.id.action_search);
//		menuItem.setVisible(false);
//		
//		return super.onCreateOptionsMenu(menu);
//	}
	
	
	private void initAutoCompleteLayout() {
	    mProgressBar = (RelativeLayout) actionBar.getCustomView().findViewById(R.id.searchbar_progress);
	    mEditTextClearBtn = (RelativeLayout) actionBar.getCustomView().findViewById(R.id.searchbar_clear);	    
	    mEditTextSearch = (InstantAutoComplete) actionBar.getCustomView().findViewById(R.id.searchbar_edittext);
	    mEditTextSearch.setHint(getString(R.string.search_hint));

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
	    mAutoCompleteAdapter = new SearchPageListAdapter(SearchPageActivity.this);
	    mEditTextSearch.setThreshold(1);	    
	    mEditTextSearch.setDropDownWidth(200); //TODO check this value
	    mEditTextSearch.setAdapter(mAutoCompleteAdapter);
	}

	private void initMainLayout() {
		mSearchInstructionsContainer = (LinearLayout) findViewById(R.id.search_page_instruction_container);
	}

	
	@Override
	public void showProgressLoading(boolean isLoading) {
		if (isLoading) {
			mProgressBar.setVisibility(View.VISIBLE);
			mEditTextClearBtn.setVisibility(View.GONE);
		} else {
			mProgressBar.setVisibility(View.GONE);
			mEditTextClearBtn.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void isRecentListEmpty(boolean isEmpty) {
		mSearchInstructionsContainer.setVisibility(View.VISIBLE);
	}
	
	private void showKeyboard() {
		mHandler.post(
			    new Runnable() {
			        public void run() {
			            InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			            triggerAutoComplete();
			        }
			    });
	}
	private void hideKeyboard() {
	    InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if(inputMethodManager != null)
	    	inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);	
	}
	

	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeyboard();
    }
    
	// Click listener for both recent list and search auto complete view
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    	
    	SearchResultItem result = (SearchResultItem) adapterView.getItemAtPosition(position);
    	Intent intent = new Intent();
    	intent.putExtra("stuff", "stuff from result"); // use result here
	  	startActivity(intent);
	  	
    }
    
    private void navigateUp() {
//		Intent upIntent = NavUtils.getParentActivityIntent(this);
//		NavUtils.navigateUpTo(this, upIntent);
    	finish();
    }
    
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.searchbar_clear:
				mEditTextSearch.setText("");
				mEditTextSearch.dismissDropDown();
				mEditTextSearch.setAdapter(new SearchPageListAdapter(SearchPageActivity.this));
				break;
			case R.id.actionbar_back:
				navigateUp();
				break;
				
			case R.id.searchbar_edittext:
				mEditTextSearch.showDropDown();
				break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        	triggerAutoComplete();
        	if (mEditTextSearch.getText().toString().length() >= 1) {
        		hideKeyboard();
        	}
            return true;
        }
        return false;
    }
	

	private void triggerAutoComplete() {
    	String query = mEditTextSearch.getText().toString();
    	int pos = query.length();
    	mEditTextSearch.setSelection(pos);
    	mAutoCompleteAdapter.getFilter().filter(query);
	    mEditTextSearch.showDropDown();
	}
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}


	@Override
	protected void updateUI(REQUEST_STATUS status) {
		/*
		 * Have to have this method here since SSActivity has this method
		 * abstract
		 */
	}

	@Override
	protected void loadPage() {
		/*
		 * Have to have this method here since SSActivity has this method
		 * abstract
		 */
	}
}
