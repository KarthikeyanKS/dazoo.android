package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityFeedListAdapter extends BaseAdapter {

	private static final String	TAG	= "ActivityFeedListAdappter";
	private ArrayList<FeedItem>	mFeedList;
	private Activity			mActivity;
	private LayoutInflater		mLayoutInflater;
	private ImageLoader			mImageLoader;

	public ActivityFeedListAdapter(Activity activity, ArrayList<FeedItem> feedList) {
		this.mActivity = activity;
		this.mFeedList = feedList;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
	}

	@Override
	public int getCount() {
		if (mFeedList != null) {
			return mFeedList.size();
		} else return 0;
	}

	@Override
	public FeedItem getItem(int position) {
		if (mFeedList != null) {
			return mFeedList.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final FeedItem feedItem = getItem(position);

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.layout_tvguide_tag_list_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView.findViewById(R.id.tvguide_tag_list_item_name_tv);
			viewHolder.time = (TextView) rowView.findViewById(R.id.tvguide_tag_list_item_time_tv);
			viewHolder.mContainer = (RelativeLayout) rowView.findViewById(R.id.tvguide_tag_list_item_container);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (feedItem != null) {
			holder.title.setText(feedItem.getItemType() + " " + feedItem.getTitle());

			String itemType = feedItem.getItemType();

			if (Consts.DAZOO_FEED_ITEM_BROADCAST.equals(itemType)) {
				holder.time.setText(feedItem.getBroadcast().getBeginTime());
			} else if (Consts.DAZOO_FEED_ITEM_TYPE_RECOMMENDED_BROADCAST.equals(itemType)) {
				holder.time.setText(feedItem.getBroadcast().getBeginTime());
			} else if (Consts.DAZOO_FEED_ITEM_TYPE_POPULAR_BROADCASTS.equals(itemType)) {
				holder.time.setText("Number of broadcasts: " + String.valueOf(feedItem.getBroadcasts().size()));
			}
		}

		return rowView;
	}

	static class ViewHolder {
		RelativeLayout	mContainer;
		TextView		itemType;
		TextView		title;
		TextView		time;
	}

}
