package com.mitv.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.ChannelPageActivity;
import com.millicom.mitv.enums.BroadcastTypeEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVChannelGuide;
import com.millicom.mitv.models.TVDate;
import com.millicom.mitv.models.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class TVGuideListAdapter extends AdListAdapter<TVChannelGuide> {
	@SuppressWarnings("unused")
	private static final String TAG = TVGuideListAdapter.class.getName();

	private LayoutInflater layoutInflater;
	private Activity activity;
	private TVDate tvDate;
	private int currentHour;
	private int rowWidth = -1;

	@SuppressLint("NewApi")
	public TVGuideListAdapter(Activity activity, ArrayList<TVChannelGuide> guide, TVDate date, int hour, boolean isToday) {
		super(Consts.JSON_AND_FRAGMENT_KEY_GUIDE, activity, guide);
		this.activity = activity;
		this.tvDate = date;
		this.currentHour = hour;

		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getViewTypeCount() {
		return super.getViewTypeCount() + 1;
	}

	public View getViewForGuideCell(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			rowView = layoutInflater.inflate(R.layout.row_tvguide_list, null);

			ViewHolder viewHolder = new ViewHolder();

			viewHolder.container = (RelativeLayout) rowView.findViewById(R.id.item_container);
			viewHolder.channelLogo = (ImageView) rowView.findViewById(R.id.tvguide_channel_iv);
			viewHolder.textView = (TextView) rowView.findViewById(R.id.tvguide_program_line_live);

			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if (rowWidth < 0) {
			/*
			 * Start the view as invisible, when the height is known, update the height of each row and change visibility to visible
			 */
			holder.textView.setVisibility(View.INVISIBLE);
			holder.textView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					// gets called after layout has been done but before it gets displayed, so we can get the height of the view
					int width = holder.textView.getWidth();

					TVGuideListAdapter guideListAdapter = ((TVGuideListAdapter) TVGuideListAdapter.this);

					guideListAdapter.setRowWidth(width);
					guideListAdapter.notifyDataSetChanged();

					TVGuideListAdapter.removeOnGlobalLayoutListener(holder.textView, this);

					holder.textView.setVisibility(View.VISIBLE);
				}

			});
		}

		final TVChannelGuide guide = getItem(position);

		if (guide.getImageUrl() != null) {
			ImageAware imageAware = new ImageViewAware(holder.channelLogo, false);

			ImageLoader.getInstance().displayImage(guide.getImageUrl(), imageAware);
		} else {
			holder.channelLogo.setImageResource(R.color.white);
		}

		holder.container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, ChannelPageActivity.class);
				intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, guide.getChannelId().getChannelId());

				ContentManager.sharedInstance().setSelectedTVChannelId(guide.getChannelId());
				ContentManager.sharedInstance().setSelectedHour(currentHour);
				activity.startActivity(intent);
			}
		});

		ArrayList<TVBroadcast> broadcasts = guide.getBroadcasts();

		String stringIconMovie = activity.getResources().getString(R.string.icon_movie) + " ";

		String stringIconLive = activity.getResources().getString(R.string.icon_live) + " ";

		String textForThreeBroadcasts = "";

		int textIndexToMarkAsOngoing = 0;

		int textStartIndexToMarkAsOngoing = 0;

		if (broadcasts != null && broadcasts.size() > 0) {
			int indexOfNearestBroadcast = TVBroadcast.getClosestBroadcastIndex(broadcasts, currentHour, tvDate, 0);

			if (indexOfNearestBroadcast != -1) {
				ArrayList<TVBroadcast> nextBroadcasts = TVBroadcast.getBroadcastsFromPosition(broadcasts, indexOfNearestBroadcast, Consts.TV_GUIDE_NEXT_PROGRAMS_NUMBER);

				for (int j = 0; j < Math.min(nextBroadcasts.size(), 3); j++) {
					TVBroadcast broadcast = nextBroadcasts.get(j);

					TVProgram program = broadcast.getProgram();

					ProgramTypeEnum programType = program.getProgramType();

					BroadcastTypeEnum broadcastType = broadcast.getBroadcastType();

					String rowInfo = broadcast.getBeginTimeHourAndMinuteLocalAsString();

					rowInfo += "   ";

					String showName = program.getTitle();

					switch (programType) {
					case MOVIE: {
						rowInfo += stringIconMovie;
						break;
					}
					case TV_EPISODE: {
						showName = program.getSeries().getName();
						break;
					}
					default: {
						if (broadcastType == BroadcastTypeEnum.LIVE) {
							rowInfo += stringIconLive;
						}
						break;
					}
					}

					rowInfo += showName;

					TextPaint testPaint = holder.textView.getPaint();

					float textWidth = testPaint.measureText(rowInfo);

					int limitIndex = rowInfo.length() - 1;

					boolean deletedChars = false;

					String toShow = "";

					if (rowWidth > 0) {
						int maxTextWidth = (int) (rowWidth * 0.9);

						/* Calculate max amount of characters that fits */
						while (textWidth > maxTextWidth && limitIndex > 0) {
							deletedChars = true;

							limitIndex--;

							rowInfo = rowInfo.substring(0, limitIndex);

							textWidth = testPaint.measureText(rowInfo);
						}

						String ellipsisString = "...";

						if (deletedChars) {
							rowInfo = rowInfo.replace(rowInfo.substring(limitIndex - 3, rowInfo.length()), ellipsisString);
						}

						toShow = rowInfo;
					}

					if (broadcast.isBroadcastCurrentlyAiring()) {
						textStartIndexToMarkAsOngoing = textForThreeBroadcasts.length();

						textIndexToMarkAsOngoing = textForThreeBroadcasts.length() + toShow.length();
					}

					textForThreeBroadcasts += toShow + "\n";
				}

				Spannable wordtoSpan = new SpannableString(textForThreeBroadcasts);

				Resources resources = SecondScreenApplication.sharedInstance().getApplicationContext().getResources();

				if (textIndexToMarkAsOngoing > 0) {
					wordtoSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.red)), textStartIndexToMarkAsOngoing, textIndexToMarkAsOngoing, 0);
				}

				wordtoSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.grey3)), textIndexToMarkAsOngoing + 1, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				holder.textView.setText(wordtoSpan, TextView.BufferType.SPANNABLE);

			}
		}

		if (textForThreeBroadcasts.equals("")) {
			rowView.setVisibility(View.INVISIBLE);
		} else {
			rowView.setVisibility(View.VISIBLE);
		}

		return rowView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/* Superclass AdListAdapter will create view if this is a position of an ad. */
		View rowView = super.getView(position, convertView, parent);

		if (rowView == null) {
			rowView = getViewForGuideCell(position, convertView, parent);
		}

		return rowView;
	}

	static class ViewHolder {
		public RelativeLayout container;
		public ImageView channelLogo;
		public TextView textView;
	}

	public void refreshList(int selectedHour) {
		currentHour = selectedHour;

		ContentManager.sharedInstance().setSelectedHour(currentHour);
		notifyDataSetChanged();
	}

	public void setRowWidth(int width) {
		this.rowWidth = width;
	}

	@SuppressLint("NewApi")
	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
		if (Build.VERSION.SDK_INT < 16) {
			v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		} else {
			v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		}
	}
}
