package com.mitv.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.interfaces.MyChannelsCountInterface;
import com.mitv.model.OldTVChannel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class MyChannelsListAdapter extends BaseAdapter {

	private static final String			TAG				= "MyChannelsListAdapter";

	private LayoutInflater				mLayoutInflater;
	private Context						mContext;
	private ArrayList<OldTVChannel>			mChannels;
	private ArrayList<String>			mCheckedChannelIds;
	private boolean[]					mIsCheckedArray;
	private TextView					mCounterTextView;

	private int							currentPosition	= -1;
	private int							mSelectedCount;

	private MyChannelsCountInterface	mCountInterface;

	public MyChannelsListAdapter(Context context, ArrayList<OldTVChannel> channels, boolean[] isCheckedArray, MyChannelsCountInterface countInterface, int selectedCount, ArrayList<String> checkedChannelIds) {
		this.mContext = context;
		this.mChannels = channels;
		this.mCheckedChannelIds = checkedChannelIds;
		this.mIsCheckedArray = isCheckedArray;
		this.mCountInterface = countInterface;
		this.mSelectedCount = selectedCount;
	}

	@Override
	public int getCount() {
		if (mChannels != null) {
			return mChannels.size();
		} else return 0;
	}

	@Override
	public OldTVChannel getItem(int position) {
		if (mChannels != null) {
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

		final OldTVChannel channel = getItem(position);
		currentPosition = (Integer) holder.mChannelNameTv.getTag();

		holder.mChannelNameTv.setText(channel.getName());

		ImageAware imageAware = new ImageViewAware(holder.mChannelLogoIv, false);
		ImageLoader.getInstance().displayImage(channel.getImageUrl(), imageAware);

		holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (holder.mCheckBox.isPressed()) {
					mIsCheckedArray[position] = isChecked;
					if (isChecked) {
						mSelectedCount++;
						
						mCheckedChannelIds.add(channel.getChannelId());
						
					} else {
						mCheckedChannelIds.remove(channel.getChannelId());
						
						mSelectedCount--;
					}
					mCountInterface.setValues(mSelectedCount);
				}
			}
		});
		holder.mCheckBox.setChecked(mIsCheckedArray[position]);

		return rowView;
	}

	private static class ViewHolder {
		public TextView		mChannelNameTv;
		public ImageView	mChannelLogoIv;
		public CheckBox		mCheckBox;
	}

}
