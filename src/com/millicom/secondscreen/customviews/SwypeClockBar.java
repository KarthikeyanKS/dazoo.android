package com.millicom.secondscreen.customviews;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.manager.AppConfigurationManager;

public class SwypeClockBar extends LinearLayout implements OnSeekBarChangeListener {

	private Activity activity;
	private VerticalSeekBar seekBar;
	private ListView timeListView;
	private ImageView clockImage;
	private List<Integer> hours;
	private static final int hoursPerDay = 24;
	private static int firstHourOfDay;

	public SwypeClockBar(Context context) {
		super(context);
		setup(context);
	}

	public SwypeClockBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context);
	}

	@SuppressLint("NewApi")
	public SwypeClockBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(context);
	}

	public void setHour(int hour) {
		int indexOfHour = hourToProgress(hour);

		/* If hour was found */
		if (indexOfHour > 0) {
			seekBar.setProgress(indexOfHour);
		}
	}

	private int hourToProgress(int hour) {
		int indexOfHour = (hour - firstHourOfDay) % hoursPerDay;
		return indexOfHour;
	}

	private int progressToHour(int progress) {
		int hour = hours.get(progress);
		return hour;
	}

	public void setup(Context context) {
		this.activity = (Activity) context;
		firstHourOfDay = AppConfigurationManager.getInstance().getFirstHourOfTVDay();

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.swype_clock_bar, this);
		clockImage = (ImageView) this.findViewById(R.id.swype_clock_bar_clock_image);

		seekBar = (VerticalSeekBar) this.findViewById(R.id.timebar_seekbar);
		seekBar.setOnSeekBarChangeListener(this);

		// TODO use this way of accessing the activity directly in the
		// VerticalSeekBar class?
		seekBar.setActivity(activity);
		timeListView = (ListView) this.findViewById(R.id.timebar_listview);

		this.hours = generate24Hours();

		if (timeListView != null) {
			TimeListAdapter timeListAdapter = new TimeListAdapter(hoursPerDay, firstHourOfDay, hours);
			timeListView.setAdapter(timeListAdapter);
		}

		/*
		 * Start the view as invisible, when the height is known, update the
		 * height of each row and change visibility to visible
		 */
		this.setVisibility(View.INVISIBLE);
		this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// gets called after layout has been done but before display, so
				// we can get the view
				int selfHeigt = SwypeClockBar.this.timeListView.getHeight(); // Ahaha! Gotcha
				TimeListAdapter timeListAdapter = ((TimeListAdapter) SwypeClockBar.this.timeListView.getAdapter());
				timeListAdapter.setListViewHeight(selfHeigt);
				timeListAdapter.notifyDataSetChanged();

				SwypeClockBar.removeOnGlobalLayoutListener(SwypeClockBar.this, this);
				SwypeClockBar.this.setVisibility(View.VISIBLE);
			}

		});
	}

	@SuppressLint("NewApi")
	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
		if (Build.VERSION.SDK_INT < 16) {
			v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		} else {
			v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		}
	}

	private List<Integer> generate24Hours() {
		List<Integer> hours = new ArrayList<Integer>();
		int hour = firstHourOfDay;
		for (int i = 0; i < hoursPerDay; ++i) {
			hours.add(hour);
			hour = (hour + 1) % hoursPerDay;
		}

		return hours;
	}

	private class TimeListAdapter extends BaseAdapter {

		private List<Integer> hours;
		private int hoursPerDay = 24;
		private int firstHourOfDay = 6;
		private int listViewHeight;

		public void setListViewHeight(int listViewHeight) {
			this.listViewHeight = listViewHeight;
		}

		public TimeListAdapter(int hoursPerDay, int firstHourOfDay, List<Integer> hours) {
			this(hoursPerDay, firstHourOfDay, hours, -1);
		}

		public TimeListAdapter(int hoursPerDay, int firstHourOfDay, List<Integer> hours, int listViewHeight) {
			this.hoursPerDay = hoursPerDay;
			this.firstHourOfDay = firstHourOfDay;
			this.hours = hours;
			this.listViewHeight = listViewHeight;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public int getCount() {
			return hours.size();
		}

		@Override
		public Object getItem(int position) {
			return hours.get(position);
		}

		@Override
		public long getItemId(int position) {
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			if (rowView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = layoutInflater.inflate(R.layout.row_timebar, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) rowView.findViewById(R.id.row_tomebar_textview);
				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();

			final int hour = (Integer) getItem(position);

			// Set the text
			String hourString = String.format("%02d", hour);
			holder.textView.setText(hourString);
			holder.textView.setTextSize(12.5f);

			if (listViewHeight > 0) {
				int cellHeight = listViewHeight / hoursPerDay;

				AbsListView.LayoutParams params = (AbsListView.LayoutParams) rowView.getLayoutParams();
				if (params == null) {
					params = new AbsListView.LayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, cellHeight));
				}
				params.height = cellHeight;
				rowView.setLayoutParams(params);
			}

			return rowView;
		}

		private class ViewHolder {
			public TextView textView;
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int hour = progressToHour(progress);
		Intent intent = new Intent(Consts.INTENT_EXTRA_CLOCK_SELECTION);
		intent.putExtra(Consts.INTENT_EXTRA_CLOCK_SELECTION_VALUE, hour);
		SecondScreenApplication.getInstance().setSelectedHour(Integer.valueOf(hour));
		LocalBroadcastManager.getInstance(activity.getBaseContext()).sendBroadcast(intent);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}
}
