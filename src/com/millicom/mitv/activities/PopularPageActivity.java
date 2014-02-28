package com.millicom.mitv.activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.base.BaseContentActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.R;
import com.mitv.adapters.PopularListAdapter;

public class PopularPageActivity extends BaseContentActivity implements
		OnClickListener {
	@SuppressWarnings("unused")
	private static final String TAG = PopularPageActivity.class.getName();

	private ListView listView;
	private PopularListAdapter adapter;
	private ArrayList<TVBroadcastWithChannelInfo> popularBroadcasts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_popular_list_activity);

		initViews();
	}

	private void initViews() {
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.popular));
		listView = (ListView) findViewById(R.id.popular_list_listview);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void loadData() {
		updateUI(UIStatusEnum.LOADING);

		ContentManager.sharedInstance()
				.getElseFetchFromServicePopularBroadcasts(this, false);
	}

	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult,
			RequestIdentifierEnum requestIdentifier) {
		switch (fetchRequestResult) {
		case SUCCESS: {
			popularBroadcasts = ContentManager.sharedInstance()
					.getFromCachePopularBroadcasts();

			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);

			break;
		}

		default: {
			updateUI(UIStatusEnum.FAILED);

			break;
		}
		}
	}

	@Override
	protected void updateUI(UIStatusEnum status) {
		super.updateUIBaseElements(status);

		switch (status) {
		case SUCCEEDED_WITH_DATA: {
			adapter = new PopularListAdapter(this, popularBroadcasts);
			listView.setAdapter(adapter);
			listView.setVisibility(View.VISIBLE);
			break;
		}

		default: {
			// Do nothing
			break;
		}
		}
	}

}