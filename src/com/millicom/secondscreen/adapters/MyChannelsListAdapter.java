package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyChannelsListAdapter extends BaseAdapter {
	
	private static final String TAG = "MyChannelsListAdapter";
	
	private LayoutInflater			mLayoutInflater;
	private Context mContext;
	private ArrayList<Channel> mChannels;
	private boolean [] mIsCheckedArray;
	
	private ImageLoader				mImageLoader;
	private int						currentPosition	= -1;
	
	public MyChannelsListAdapter(Context context, ArrayList<Channel> channels, boolean [] isCheckedArray){
		this.mContext = context;
		this.mChannels = channels;
		this.mIsCheckedArray = isCheckedArray;
		this.mImageLoader = new ImageLoader(context, R.drawable.loadimage);
	}

	@Override
	public int getCount() {
		if(mChannels !=null){
			return mChannels.size();
		} else return 0;
	}

	@Override
	public Channel getItem(int position) {
		if(mChannels !=null){
			return mChannels.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View rowView = convertView;

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// inflate different layouts depending on the program type
			rowView = mLayoutInflater.inflate(R.layout.row_mychannels, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mChannelNameTv = (TextView) rowView.findViewById(R.id.row_mychannels_channel_text_title_tv);
			viewHolder.mChannelLogoIv = (ImageView) rowView.findViewById(R.id.row_mychannels_channel_iv);
			viewHolder.mCheckBox = (CheckBox) rowView.findViewById(R.id.row_mychannels_checkbox);
			viewHolder.mChannelNameTv.setTag(Integer.valueOf(position));
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		Channel channel = getItem(position);
		currentPosition = (Integer)holder.mChannelNameTv.getTag();
		
		holder.mChannelNameTv.setText(channel.getName());
			
		mImageLoader.displayImage(channel.getLogoLUrl(), holder.mChannelLogoIv, ImageLoader.IMAGE_TYPE.THUMBNAIL);
	
		holder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mIsCheckedArray[position] = isChecked;
			}	
		});
		holder.mCheckBox.setChecked(mIsCheckedArray[position]);
		
		
		return rowView;
	}
	
	private static class ViewHolder{
		public TextView mChannelNameTv;
		public ImageView mChannelLogoIv;
		public CheckBox mCheckBox;
	}

}
