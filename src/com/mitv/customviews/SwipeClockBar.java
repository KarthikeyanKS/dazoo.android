package com.mitv.customviews;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.interfaces.SwipeClockTimeSelectedCallbackListener;
import com.millicom.mitv.utilities.DateUtils;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.manager.FontManager;

public class SwipeClockBar extends LinearLayout implements OnSeekBarChangeListener {

	private static final String TAG = SwipeClockBar.class.getName();
	public static final int SCREEN_HEIGHT_SMALL = 320;
	public static final int SCREEN_HEIGHT_TABLET = 1920;
	private static final int HOURS_PER_DAY = 24;
	private static final int HOUR_NOT_FOUND_IN_LIST_OF_HOURS = -1;

	private static final int SELECTED_HOUR_TEXTVIEW_DISPLAY_TIME = 1200;
	
	private Activity activity;
	private VerticalSeekBar seekBar;
	private ListView timeListView;
	private SwipeClockTimeSelectedCallbackListener timeSelectedListener;
	private FontTextView clockIconTextView;
	private static List<Integer> hoursOfTheDay;
	private static int firstHourOfDay;
	private TimeListAdapter listAdapter;
	private LayoutInflater inflater;
	private float savedTextSize = -1;
	private boolean isToday;
	private boolean smallScreenMode;

	private LinearLayout clockbarContainer;
	private int screenHeight;

	private boolean isHighlighted = false;

	public SwipeClockBar(Context context) {
		super(context);
		setup(context);
	}

	public SwipeClockBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context);
	}

	@SuppressLint("NewApi")
	public SwipeClockBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(context);
	}

	public void setHour(int hour) {
		int indexOfHour = hourToProgress(hour);

		/* If hour was found */
		if (indexOfHour > HOUR_NOT_FOUND_IN_LIST_OF_HOURS) {
			seekBar.setProgress(indexOfHour);
			styleSelectedHour(indexOfHour);
		}
	}

	public static int hourToProgress(int hour) {
		int index = HOUR_NOT_FOUND_IN_LIST_OF_HOURS;
		if (hour >= firstHourOfDay) {
			index = (hour - firstHourOfDay) % HOURS_PER_DAY;
		}
		else {
			index = (HOURS_PER_DAY - firstHourOfDay + hour);
		}
		return index;
	}

	public static int progressToHour(int progress) {
		int hour = hoursOfTheDay.get(progress);
		return hour;
	}
	
	
	public void setTimeSelectedListener(SwipeClockTimeSelectedCallbackListener timeSelectedListener) {
		this.timeSelectedListener = timeSelectedListener;
	}

	public void setSelectedHourTextView(FontTextView selectedHourTextView) {
		seekBar.setSelectedHourTextView(selectedHourTextView);
	}

	public void setup(Context context) {
		this.activity = (Activity) context;
		int firstHourOfDay = ContentManager.sharedInstance().getFromCacheFirstHourOfTVDay();
		SwipeClockBar.firstHourOfDay = firstHourOfDay;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.swipe_clock_bar, this);

		clockbarContainer = (LinearLayout) this.findViewById(R.id.swipe_clock_bar_container);

		this.clockIconTextView = (FontTextView) this.findViewById(R.id.swipe_clock_bar_clock_icon_textview);
		this.clockIconTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				highlightClockbar();
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						dehighlightClockbar();
					}
				}, SELECTED_HOUR_TEXTVIEW_DISPLAY_TIME);
			}
		});

		this.seekBar = (VerticalSeekBar) this.findViewById(R.id.timebar_seekbar);
		this.seekBar.setOnSeekBarChangeListener(this);
		this.seekBar.setParentClockbar(this);

		this.seekBar.setValues(HOURS_PER_DAY, firstHourOfDay);
		this.timeListView = (ListView) this.findViewById(R.id.timebar_listview);

		SwipeClockBar.hoursOfTheDay = generate24Hours();

		this.screenHeight = GenericUtils.getScreenHeight((Activity) context);
		
		if(screenHeight <= SCREEN_HEIGHT_SMALL) {
			smallScreenMode = true;
		} else {
			smallScreenMode = false;
		}
		
		if (timeListView != null) {
			this.listAdapter = new TimeListAdapter(HOURS_PER_DAY, firstHourOfDay, hoursOfTheDay);
			this.timeListView.setAdapter(listAdapter);
		}

		/*
		 * Start the view as invisible, when the height is known, update the
		 * height of each row and change visibility to visible
		 */
		this.setVisibility(View.INVISIBLE);
		this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// gets called after layout has been done but before it gets displayed, so we can get the height of the view
				int selfHeigt = SwipeClockBar.this.timeListView.getHeight();
				TimeListAdapter timeListAdapter = ((TimeListAdapter) SwipeClockBar.this.timeListView.getAdapter());
				timeListAdapter.setListViewHeight(selfHeigt);
				timeListAdapter.notifyDataSetChanged();

				SwipeClockBar.removeOnGlobalLayoutListener(SwipeClockBar.this, this);
				SwipeClockBar.this.setVisibility(View.VISIBLE);
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
		for (int i = 0; i < HOURS_PER_DAY; ++i) {
				hours.add(hour);
		
			hour = (hour + 1) % HOURS_PER_DAY;
		}

		return hours;
	}	

	private class TimeListAdapter extends BaseAdapter {

		private static final int HOUR_COUNT_VISIBLE_IF_SMALL_SCREEN = 12;
		
		private List<Integer> hoursOfTheDay;
		private int hoursPerDay = 24;
		private int firstHourOfDay = 6;
		private int listViewHeight;
		private int index24OfSelectedHour;

		private void setSelectedHour(int indexOfSelectedHour) {
			this.index24OfSelectedHour = indexOfSelectedHour;
		}

		public void setListViewHeight(int listViewHeight) {
			this.listViewHeight = listViewHeight;
		}

		public TimeListAdapter(int hoursPerDay, int firstHourOfDay, List<Integer> hours) {
			this(hoursPerDay, firstHourOfDay, hours, -1);
		}

		public TimeListAdapter(int hoursPerDay, int firstHourOfDay, List<Integer> hours, int listViewHeight) {
			this.index24OfSelectedHour = -1; /* not set */
			this.hoursPerDay = hoursPerDay;
			this.firstHourOfDay = firstHourOfDay;
			this.hoursOfTheDay = hours;
			this.listViewHeight = listViewHeight;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (hoursOfTheDay != null) {
				count = hoursOfTheDay.size();
			}
			
			if (smallScreenMode) {
				count = HOUR_COUNT_VISIBLE_IF_SMALL_SCREEN;
				if(showOddHour()) {
					count++;
				}
			}
			return count;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}


		@Override
		public Object getItem(int positionInListView) {
			Integer hour = -1;
			if (hoursOfTheDay != null) {
				int hourArrayIndex = positionInListView;

				if (smallScreenMode) {
					if (!showOddHour()) {
						hourArrayIndex = positionInListView * 2;
					} else {
						hourArrayIndex = positionInListViewTo24Index(positionInListView);
					}
				}

				hour = hoursOfTheDay.get(hourArrayIndex);
			}
			return hour;
		}
		
		/* DO NOT TOUCH THIS 'ALGORITH' */
		private int positionInListViewTo24Index(int positionInListView) {
			int hourArrayIndex = positionInListView;
			if ((positionInListView * 2) < index24OfSelectedHour) {
				hourArrayIndex = positionInListView * 2;
			} else {
				if( ( (2*positionInListView)-1 ) == index24OfSelectedHour) {
					hourArrayIndex = (2 * positionInListView) - 1;
				} else {
					hourArrayIndex = (positionInListView - 1) * 2;
				}
			}
			
			return hourArrayIndex;
		}

		@Override
		public long getItemId(int position) {
			return -1;
		}

		private boolean evenPosition(int position) {
			return (position % 2 == 0);
		}

		private boolean oddPosition(int position) {
			return !evenPosition(position);
		}

		private boolean showOddHour() {
			return oddPosition(index24OfSelectedHour);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			if (rowView == null) {
				rowView = inflater.inflate(R.layout.row_timebar, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.textView = (FontTextView) rowView.findViewById(R.id.row_timebar_textview);
				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();

			final int hour = (Integer) getItem(position);
			rowView.setVisibility(View.INVISIBLE);
			// Set the text
			String hourString = String.format(SecondScreenApplication.getCurrentLocale(), "%02d", hour);
			holder.textView.setText(hourString);

			int textColorId;
			int parentBackgroundColorId;
			
			int indexToCheckForColor = position;
				if(smallScreenMode) {
					indexToCheckForColor = 2 * position;
				if (showOddHour()) {
					indexToCheckForColor = positionInListViewTo24Index(position);
				}
			}

			if (isHighlighted) {
				if (indexToCheckForColor == index24OfSelectedHour) {
					textColorId = R.color.white;
					parentBackgroundColorId = R.color.red;
				} else if (isToday && isEarlier(hour, DateUtils.getCurrentHourOn24HourFormat())) {
					textColorId = R.color.grey5;
					parentBackgroundColorId = R.color.transparent;
				} else {
					textColorId = R.color.red;
					parentBackgroundColorId = R.color.transparent;
				}
			} else {
				if (indexToCheckForColor == index24OfSelectedHour) {
					textColorId = R.color.white;
					parentBackgroundColorId = R.color.grey4;
				} else if (isToday && isEarlier(hour, DateUtils.getCurrentHourOn24HourFormat())) {
					textColorId = R.color.grey5;
					parentBackgroundColorId = R.color.transparent;
				} else {
					textColorId = R.color.grey2;
					parentBackgroundColorId = R.color.transparent;
				}
			}
			

			int textColor = activity.getResources().getColor(textColorId);
			int parentBackgroundColor = activity.getResources().getColor(parentBackgroundColorId);

			holder.textView.setTextColor(textColor);
			rowView.setBackgroundColor(parentBackgroundColor);

			if (listViewHeight > 0) {
				int cellHeight = listViewHeight / getCount();

				AbsListView.LayoutParams params = (AbsListView.LayoutParams) rowView.getLayoutParams();
				if (params == null) {
					params = new AbsListView.LayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, cellHeight));
				}
				params.height = cellHeight;
				rowView.setLayoutParams(params);

				if (smallScreenMode) {
					savedTextSize = 12;
				} else if (screenHeight > SCREEN_HEIGHT_TABLET) {
					savedTextSize = 34;
				}
				holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, savedTextSize);

				rowView.setVisibility(View.VISIBLE);
			}
			return rowView;
		}

		private class ViewHolder {
			public FontTextView textView;
		}
	}



	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int hour = progressToHour(progress);
		
		if(timeSelectedListener != null) {
			timeSelectedListener.onTimeChange(hour);
		}
		
		int indexToStyle = progress;

		styleSelectedHour(indexToStyle);
	}

	private void styleSelectedHour(int index) {
		listAdapter.setSelectedHour(index);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	/* If first is earlier than second, return true. */
	private boolean isEarlier(int first, int second) {
		if (first < second && first >= firstHourOfDay) {
			return true;
		}
		else {
			return false;
		}
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}

	public void highlightClockbar() {
		isHighlighted = true;
		listAdapter.notifyDataSetChanged();
		clockIconTextView.setTextColor(getResources().getColor(R.color.red));
		clockbarContainer.setBackgroundResource(R.drawable.layout_rounded_corners_grey0_clockbar);
	}

	public void dehighlightClockbar() {
		isHighlighted = false;
		listAdapter.notifyDataSetChanged();
		clockIconTextView.setTextColor(getResources().getColor(R.color.grey5));
		clockbarContainer.setBackgroundResource(R.color.transparent);
	}
}
