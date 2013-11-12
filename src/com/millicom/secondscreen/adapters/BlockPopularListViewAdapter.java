package com.millicom.secondscreen.adapters;

import java.text.ParseException;
import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.TVGuideTagListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.FeedItem;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BlockPopularListViewAdapter extends BaseAdapter{
	
	private static final String TAG = "BlockPopularListViewAdapter";
	
	private LayoutInflater		mLayoutInflater;
	private Activity mActivity;
	private ArrayList<Broadcast> mPopularBroadcasts;
	private ImageLoader			mImageLoader;
	
	public BlockPopularListViewAdapter(Activity activity, ArrayList<Broadcast> popularBroadcasts){
		this.mActivity = activity;
		this.mPopularBroadcasts = popularBroadcasts;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
	}

	@Override
	public int getCount() {
		if (mPopularBroadcasts!=null){
			return mPopularBroadcasts.size();
		} else return 0;
	}

	@Override
	public Broadcast getItem(int position) {
		if (mPopularBroadcasts!=null){
			return mPopularBroadcasts.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		final Broadcast broadcast = getItem(position);
		
		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = mLayoutInflater.inflate(R.layout.block_feed_popular_listitem, null);
			ViewHolder viewHolder = new ViewHolder();
			
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if(broadcast!=null){
		
		}
		
		holder.mContainer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcast.getBeginTimeMillis());
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, broadcast.getChannel().getChannelId());
				// TODO GET THE DATE FROM BEGIN TIME OF THE BROADCAST
				//intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, mDate.getDate());


				mActivity.startActivity(intent);
				mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});
		
		
		return rowView;
	}
	
	private static class ViewHolder{
		RelativeLayout mContainer;
		ImageView mPoster;
		TextView mTitle;
		TextView mChannelName;
		TextView mTime;
		TextView mDetails;
		ProgressBar mProgressBar;
	}
}
