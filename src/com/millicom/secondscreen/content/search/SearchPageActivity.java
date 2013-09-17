package com.millicom.secondscreen.content.search;

import com.millicom.secondscreen.R;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class SearchPageActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_searchpage);
		
		initViews();
	}

	private void initViews() {
		// styling the Action Bar
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setCustomView(R.layout.layout_actionbar_searchpage);
		
		final int actionBarColor = getResources().getColor(R.color.lightblue);
		actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
		
	}
}
