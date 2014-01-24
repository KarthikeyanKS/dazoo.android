package com.millicom.secondscreen.content.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.Consts.ENTITY_TYPE;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.SearchResultItem;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.customviews.InstantAutoComplete;

public class SearchPageActivity extends SSActivity implements OnItemClickListener, OnEditorActionListener, OnClickListener, SearchActivityListeners {

	private static final String TAG = "SearchPageActivity";

	private SearchPageListAdapter mAutoCompleteAdapter;
	private LinearLayout mSearchInstructionsContainer;

	private ImageView mBackButton;
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

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		// CUSTOMIZE DEFAULT ACTIONBAR

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
		hideKeyboard();
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
		inflater.inflate(R.menu.main, menu);

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
	
	@SuppressLint("NewApi")
	private int getScreenWidth() {
		int screenWidth = 0;
		if (Build.VERSION.SDK_INT >= 11) {
	        Point size = new Point();
	        try {
	            this.getWindowManager().getDefaultDisplay().getRealSize(size);
	            screenWidth = size.x;
	        } catch (NoSuchMethodError e) {
	            Log.i(TAG, "Error it can't work");
	        }

	    } else {
	        DisplayMetrics metrics = new DisplayMetrics();
	        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        screenWidth = metrics.widthPixels;
	    }
		return screenWidth;
	}

	private void loadAutoCompleteView() {
		mAutoCompleteAdapter = new SearchPageListAdapter(SearchPageActivity.this, this);
		mEditTextSearch.setThreshold(1);
	
		int width = getScreenWidth();
		
		mEditTextSearch.setDropDownWidth(width);
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
		mHandler.post(new Runnable() {
			public void run() {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				 triggerAutoComplete();
			}
		});
	}

	private void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
		}
		
}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideKeyboard();
	}

	@Override
	public void finish() {
		hideKeyboard();
		super.finish();
	}

	// Click listener for both recent list and search auto complete view
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

		SearchResultItem result = (SearchResultItem) adapterView.getItemAtPosition(position);
		
		if(result.getEntityType() != ENTITY_TYPE.CHANNEL) {
			// open the detail view for the individual broadcast
			Intent intent = new Intent(SearchPageActivity.this, BroadcastPageActivity.class);
	
			// we take one position less as we have a header view
			int adjustedPosition = position - 1;
			if(adjustedPosition < 0) {
				/* Don't allow negative values */
				adjustedPosition = 0;
			}
			
			Broadcast nextBroadcast = result.getNextBroadcast();
			
			intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, nextBroadcast.getBeginTimeMillisGmt());
			
			Channel channel = nextBroadcast.getChannel();
			String channelId = channel.getChannelId();
			intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, channelId);
			
			String date = nextBroadcast.getTvDateString();
			intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, date);
			
			startActivity(intent);
		} else {
			Toast.makeText(this, "Channel pressed, behavior unspecified", Toast.LENGTH_SHORT).show();
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
			if (mEditTextSearch.getText().toString().length() >= 1) {
				hideKeyboard();
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
