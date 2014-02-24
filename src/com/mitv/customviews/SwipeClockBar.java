package com.mitv.customviews;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
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
import com.millicom.mitv.utilities.DateUtils;
import com.millicom.mitv.utilities.GenericUtils;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.manager.FontManager;

public class SwipeClockBar extends LinearLayout implements OnSeekBarChangeListener {

	private static final String TAG = SwipeClockBar.class.getName();
	public static final int SCREEN_HEIGHT_SMALL = 320;
	public static final int SCREEN_HEIGHT_TABLET = 1280;
	private static final int HOURS_PER_DAY = 24;
	
	private Activity activity;
	private VerticalSeekBar seekBar;
	private ListView timeListView;
	private FontTextView clockIconTextView;
	private static List<Integer> hoursOfTheDay;
	private static int firstHourOfDay;
	private TimeListAdapter listAdapter;
	private LayoutInflater inflater;
	private float savedTextSize = -1;
	private boolean isToday;
	private boolean smallScreenMode;
	private int screenHeight;

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
		if (indexOfHour > 0) {
			seekBar.setProgress(indexOfHour);
		}
	}

	public static int hourToProgress(int hour) {
		int index;
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

	public void setup(Context context) {
		this.activity = (Activity) context;
		firstHourOfDay = ContentManager.sharedInstance().getFromStorageFirstHourOfTVDay();

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.swipe_clock_bar, this);
		
		this.clockIconTextView = (FontTextView) this.findViewById(R.id.swipe_clock_bar_clock_icon_textview);

		this.seekBar = (VerticalSeekBar) this.findViewById(R.id.timebar_seekbar);
		this.seekBar.setOnSeekBarChangeListener(this);

		// TODO use this way of accessing the activity directly in the
		// VerticalSeekBar class?
		this.seekBar.setActivity(activity);
		this.timeListView = (ListView) this.findViewById(R.id.timebar_listview);

		this.hoursOfTheDay = generate24Hours();

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

			String fontName;
			int colorId;

			int indexToCheckForColor = position;
				if(smallScreenMode) {
					indexToCheckForColor = 2 * position;
				if (showOddHour()) {
					indexToCheckForColor = positionInListViewTo24Index(position);
				}
			}

			if (indexToCheckForColor == index24OfSelectedHour) {
				colorId = R.color.white;
				fontName = FontManager.FONT_BOLD;
				rowView.setBackgroundColor(activity.getResources().getColor(R.color.grey4));
			} else if (isToday && isEarlier(hour, DateUtils.getCurrentHourOn24HourFormat())) {
				colorId = R.color.grey2;
				fontName = FontManager.FONT_LIGHT;
				rowView.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
			} else {
				colorId = R.color.black;
				fontName = FontManager.FONT_LIGHT;
				rowView.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
			}

			int textColor = activity.getResources().getColor(colorId);
			Typeface textFont = FontManager.getTypefaceStatic(activity, fontName);

			holder.textView.setTextColor(textColor);
			holder.textView.setTypeface(textFont);

			if (listViewHeight > 0) {
				int cellHeight = listViewHeight / getCount();

				AbsListView.LayoutParams params = (AbsListView.LayoutParams) rowView.getLayoutParams();
				if (params == null) {
					params = new AbsListView.LayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, cellHeight));
				}
				params.height = cellHeight;
				rowView.setLayoutParams(params);

				// If this is the first view, calculate text size
				if (savedTextSize < 0) {
					savedTextSize = getTextSize(cellHeight, holder.textView, hourString);
					if (smallScreenMode) {
						savedTextSize = 12;
					} else if (screenHeight >= SCREEN_HEIGHT_TABLET) {
						savedTextSize = 34;
					}
				}
				holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, savedTextSize);

				rowView.setVisibility(View.VISIBLE);
			}
			return rowView;
		}

		private class ViewHolder {
			public FontTextView textView;
		}

		private int getTextSize(int cellHeight, TextView textView, String string) {
			int width = cellHeight;
			int height = cellHeight;
			CharSequence text = string;

			// Get the text view's paint object
			TextPaint textPaint = textView.getPaint();

			float mMaxTextSize = 50;
			float mMinTextSize = 12;

			// Bisection method: fast & precise
			float lower = mMinTextSize;
			float upper = mMaxTextSize;

			float targetTextSize = (lower + upper) / 2;
			int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

			while (upper - lower > 1) {
				targetTextSize = (lower + upper) / 2;
				textHeight = getTextHeight(text, textPaint, width, targetTextSize);
				if (textHeight > height) {
					upper = targetTextSize;
				}
				else {
					lower = targetTextSize;
				}
			}

			targetTextSize = lower;
			textHeight = getTextHeight(text, textPaint, width, targetTextSize);
			
			return textHeight;
		}
	}
	


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int hour = progressToHour(progress);
		broadcastClockSelectionChanged(hour);
		
		int indexToStyle = progress;
		
		styleSelectedHour(indexToStyle);
	}
		
	private void styleSelectedHour(int index) {
		listAdapter.setSelectedHour(index);
		listAdapter.notifyDataSetChanged();
	}
	

	private void broadcastClockSelectionChanged(int hour) {
		Intent intent = new Intent(Consts.INTENT_EXTRA_CLOCK_SELECTION);
		intent.putExtra(Consts.INTENT_EXTRA_CLOCK_SELECTION_VALUE, hour);
		ContentManager.sharedInstance().setSelectedHour(Integer.valueOf(hour));
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

	// Set the text size of the text paint object and use a static layout to render text off screen before measuring
	private int getTextHeight(CharSequence source, TextPaint originalPaint, int width, float textSize) {
		// modified: make a copy of the original TextPaint object for measuring
		// (apparently the object gets modified while measuring, see also the
		// docs for TextView.getPaint() (which states to access it read-only)
		TextPaint paint = new TextPaint(originalPaint);
		// Update the text paint object
		paint.setTextSize(textSize);
		// Measure using a static layout
		StaticLayout layout = new StaticLayout(source, paint, width, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
		return layout.getHeight();
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
}
