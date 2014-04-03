
package com.mitv.listadapters;



import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.interfaces.MyChannelsCountInterface;
import com.mitv.models.TVChannel;
import com.mitv.models.TVChannelId;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;



public class MyChannelsListAdapter 
extends BaseAdapter 
{
	@SuppressWarnings("unused")
	private static final String TAG = MyChannelsListAdapter.class.getName();


	private LayoutInflater layoutInflater;
	private ArrayList<TVChannel> channelsMatchingSearch;
	private ArrayList<TVChannelId> checkedChannelIds;

	private MyChannelsCountInterface mCountInterface;

	public MyChannelsListAdapter(MyChannelsCountInterface countInterface, ArrayList<TVChannel> channels, ArrayList<TVChannelId> checkedChannelIds) {
		this.channelsMatchingSearch = channels;
		this.checkedChannelIds = checkedChannelIds;
		this.mCountInterface = countInterface;

		layoutInflater = (LayoutInflater) SecondScreenApplication.sharedInstance().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setChannelsMatchingSearchAndRefreshAdapter(ArrayList<TVChannel> channelsMatchingSearch) {
		this.channelsMatchingSearch = channelsMatchingSearch;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (channelsMatchingSearch != null) {
			count = channelsMatchingSearch.size();
		}
		return count;
	}

	@Override
	public TVChannel getItem(int position) {
		TVChannel channel = null;
		if (channelsMatchingSearch != null) {
			channel = channelsMatchingSearch.get(position);
		}
		return channel;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			rowView = layoutInflater.inflate(R.layout.row_mychannels, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.channelName = (TextView) rowView.findViewById(R.id.row_mychannels_channel_text_title_tv);
			viewHolder.channelLogo = (ImageView) rowView.findViewById(R.id.row_mychannels_channel_iv);
			viewHolder.button = (RelativeLayout) rowView.findViewById(R.id.row_mychannels_channel_button);
			viewHolder.buttonTv = (TextView) rowView.findViewById(R.id.row_mychannels_channel_button_tv);
			viewHolder.channelName.setTag(Integer.valueOf(position));
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		final TVChannel channel = getItem(position);

		holder.channelName.setText(channel.getName());

		ImageAware imageAware = new ImageViewAware(holder.channelLogo, false);
		SecondScreenApplication.sharedInstance().getImageLoaderManager().displayImageWithResetViewOptions(channel.getImageUrl(), imageAware);

		final boolean checked;
		if (checkedChannelIds.contains(channel.getChannelId())) {
			checked = true;
		}
		else {
			checked = false;
		}

		final Resources resources = SecondScreenApplication.sharedInstance().getResources();
		
		if (checked) {
			holder.buttonTv.setText(resources.getString(R.string.remover));
			holder.button.setBackgroundResource(R.drawable.background_color_selector_mychannels_grey);
		}
		else {
			holder.buttonTv.setText("+  " + resources.getString(R.string.agregar));
			holder.button.setBackgroundResource(R.drawable.background_color_selector_mychannels_blue);
		}

		holder.button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean checked = checkedChannelIds.contains(channel.getChannelId());
				TVChannel channelByTag = (TVChannel) channel;
				if (channelByTag != null) {
					TVChannelId channelId = channelByTag.getChannelId();
					if (checked) {
						checkedChannelIds.remove(channelId);
						holder.buttonTv.setText("+  " + resources.getString(R.string.agregar));
						holder.button.setBackgroundResource(R.drawable.background_color_selector_mychannels_blue);
					} else {
						checkedChannelIds.add(channelId);
						holder.buttonTv.setText("✓ " + resources.getString(R.string.agregado)); //TODO: Update icon when it is in font.
						holder.button.setBackgroundResource(R.drawable.layout_rounded_corners_blue0);
					}
					int checkedChannelsCount = checkedChannelIds.size();
					mCountInterface.setSelectedChannelCount(checkedChannelsCount);
				}
			}
		});

		return rowView;
	}

	//	@Override
	//	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	//		CheckBox checkBox = (CheckBox) buttonView;
	//		if (checkBox.isPressed()) {
	//			TVChannel channelByTag = (TVChannel) checkBox.getTag();
	//			if (channelByTag != null) {
	//				TVChannelId channelId = channelByTag.getChannelId();
	//				if (isChecked) {
	//					checkedChannelIds.add(channelId);
	//				} else {
	//					checkedChannelIds.remove(channelId);
	//				}
	//				int checkedChannelsCount = checkedChannelIds.size();
	//				mCountInterface.setSelectedChannelCount(checkedChannelsCount);
	//			}
	//		}
	//	}

	private static class ViewHolder {
		public TextView channelName;
		public ImageView channelLogo;
		public RelativeLayout button;
		public TextView buttonTv;
	}

}
