package com.millicom.secondscreen.content.search;

import java.util.ArrayList;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import com.actionbarsherlock.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.millicom.secondscreen.Consts.REQUEST_STATUS;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.SSActivity;
import com.millicom.secondscreen.content.model.SearchResult;
import com.millicom.secondscreen.content.model.SearchResultItem;
import com.millicom.secondscreen.content.search.SearchTask.SearchResultCallback;
import com.millicom.secondscreen.customviews.FontEditText;

public class SearchPageActivity extends SSActivity {

	private static final String TAG = "SearchPageActivity";
	
	private int SEARCH_DELAY = 12000;
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
		mSearchBar.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mListView.getVisibility() == View.GONE) {
					mSearchInstructionsContainer.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
				}
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

					// search for products
					final String searchWord = mSearchBar.getText().toString();
					trySearch(searchWord);
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
						}
					});
				}
			}
		});

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
	
	private void trySearch(final String searchWord) {
		Log.e(TAG, "Try search, searching for: " + searchWord);
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
