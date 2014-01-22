package com.millicom.secondscreen.content.search;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.model.SearchResult;
import com.millicom.secondscreen.content.model.SearchResultItem;
import com.millicom.secondscreen.content.search.SearchTask.SearchResultCallback;
import com.millicom.secondscreen.customviews.FontEditText;

public class SearchPageActivity extends SSActivity implements TextWatcher, OnEditorActionListener {

	private static final String TAG = "SearchPageActivity";

	private int SEARCH_DELAY = 1000;
	private Handler mHandler;
	private FontEditText mSearchBar;
	private ListView mListView;
	private SearchPageListAdapter mListAdapter;
	private LinearLayout mSearchInstructionsContainer;
	private SearchTask mSearchTask;
	private boolean mCancelSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_searchpage_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		mHandler = new Handler();

		initViews();
	}

	private void initViews() {
		// styling the Action Bar
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setCustomView(R.layout.actionbar_searchpage);

		final int actionBarColor = getResources().getColor(R.color.blue1);
		actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

		mSearchInstructionsContainer = (LinearLayout) findViewById(R.id.search_page_instruction_container);
		mSearchBar = (FontEditText) findViewById(R.id.search_page_edittext);
		// mSearchBar.addTextChangedListener(this);
		mSearchBar.setOnEditorActionListener(this);

		mListView = (ListView) findViewById(R.id.search_page_listview);

		mListAdapter = new SearchPageListAdapter();
		mListView.setAdapter(mListAdapter);
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

	private void trySearch() {
		Log.e(TAG, "Try search");
		Runnable searchRunnable = new Runnable() {

			@Override
			public void run() {
				if (!mSearchTask.isCancelled()) {
					Log.e(TAG, "Executing search task");
					mSearchTask.execute();
				} else {
					Log.e(TAG, "Skipping search since it is canceled");
				}
			}
		};

		mHandler.postDelayed(searchRunnable, SEARCH_DELAY);
	}

	public void initSearchTask() {
		final String searchWord = mSearchBar.getText().toString();
		mSearchTask = null;
		mSearchTask = new SearchTask(searchWord, new SearchResultCallback() {
			@Override
			public void onResult(SearchResult result) {

				SearchPageListAdapter listAdapter = SearchPageActivity.this.mListAdapter;

				boolean noResult = true;

				if (result != null) {
					String suggestion = result.getSuggestion(); // TODO
																// use
																// this?
					ArrayList<SearchResultItem> items = result.getItems();

					if (items != null && !items.isEmpty()) {
						noResult = false;
						listAdapter.setSearchResultItems(items);
					}

					Log.e(TAG, "Suggestion: " + suggestion);
				}

				if (noResult) {
					// TODO display "no result found"
				}

				listAdapter.notifyDataSetChanged();
				hideInstructionView();
				hideKeyboard();
			}
		});
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (event != null) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				initSearchTask();
				mSearchTask.execute();
			}
		}
		return false;
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchBar.getWindowToken(), 0);
	}
	
	private void showKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mSearchBar, 0);
	}

	private void hideInstructionView() {
		if (mListView.getVisibility() == View.GONE) {
			mSearchInstructionsContainer.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	public void afterTextChanged(Editable s) {
		if (s != null && s.length() > 0) {

			// stop any current search thread
			if (mSearchTask != null && !mSearchTask.isCancelled()) {
				mSearchTask.cancel(true);
				Log.e(TAG, "Cancel search task");
			}
			initSearchTask();
			trySearch();
		}
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
