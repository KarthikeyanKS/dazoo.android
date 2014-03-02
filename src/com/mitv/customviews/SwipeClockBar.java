package com.mitv.customviews;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
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

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.manager.AppConfigurationManager;
import com.mitv.manager.FontManager;
import com.mitv.utilities.DateUtilities;
import com.mitv.utilities.HardwareUtilities;

public class SwipeClockBar extends LinearLayout implements OnSeekBarChangeListener {

	private static final String TAG = "SwipeClockBar";
	public static final int SCREEN_HEIGHT_SMALL = 320;
	public static final int SCREEN_HEIGHT_TABLET = 1920;
	private static final int SELECTED_HOUR_TEXTVIEW_DISPLAY_TIME = 1200;

	private Activity mActivity;
	private VerticalSeekBar mSeekBar;
	private LinearLayout mClockbarContainer;
	private ListView mTimeListView;
	private FontTextView mClockIconTextView;
	private static List<Integer> mHours;
	private static final int mHoursPerDay = 24;
	private static int mFirstHourOfDay;
	private TimeListAdapter mListAdapter;
	private LayoutInflater mInflater;
	private float mSavedTextSize = -1;
	private boolean mIsToday;
	private boolean mSmallScreenMode;
	private int mScreenHeight;

	private boolean mIsHighlighted = false;

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
			mSeekBar.setProgress(indexOfHour);
		}
	}

	public static int hourToProgress(int hour) {
		int index;
		if (hour >= mFirstHourOfDay) {
			index = (hour - mFirstHourOfDay) % mHoursPerDay;
		}
		else {
			index = (mHoursPerDay - mFirstHourOfDay + hour);
		}
		return index;
	}

	public static int progressToHour(int progress) {
		int hour = mHours.get(progress);
		return hour;
	}

	public void setSelectedHourTextView(FontTextView selectedHourTextView) {
		mSeekBar.setSelectedHourTextView(selectedHourTextView);
	}

	public void setup(Context context) {
		this.mActivity = (Activity) context;
		mFirstHourOfDay = AppConfigurationManager.getInstance().getFirstHourOfTVDay();

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.swipe_clock_bar, this);

		mClockbarContainer = (LinearLayout) this.findViewById(R.id.swipe_clock_bar_container);

		this.mClockIconTextView = (FontTextView) this.findViewById(R.id.swipe_clock_bar_clock_icon_textview);
		this.mClockIconTextView.setOnClickListener(new OnClickListener() {

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

		this.mSeekBar = (VerticalSeekBar) this.findViewById(R.id.timebar_seekbar);
		this.mSeekBar.setOnSeekBarChangeListener(this);
		this.mSeekBar.setParentClockbar(this);

		// TODO use this way of accessing the activity directly in the
		// VerticalSeekBar class?
		this.mSeekBar.setActivity(mActivity);
		this.mTimeListView = (ListView) this.findViewById(R.id.timebar_listview);

		this.mHours = generate24Hours();

		this.mScreenHeight = HardwareUtilities.getScreenHeight((Activity) context);
		if(mScreenHeight <= SCREEN_HEIGHT_SMALL) {
			mSmallScreenMode = true;
		} else {
			mSmallScreenMode = false;
		}

		if (mTimeListView != null) {
			this.mListAdapter = new TimeListAdapter(mHoursPerDay, mFirstHourOfDay, mHours);
			this.mTimeListView.setAdapter(mListAdapter);
		}
		mListAdapter.setSelectedHour(SecondScreenApplication.getInstance().getSelectedHour() - mFirstHourOfDay);

		/*
		 * Start the view as invisible, when the height is known, update the
		 * height of each row and change visibility to visible
		 */
		this.setVisibility(View.INVISIBLE);
		this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// gets called after layout has been done but before it gets displayed, so we can get the height of the view
				int selfHeigt = SwipeClockBar.this.mTimeListView.getHeight();
				TimeListAdapter timeListAdapter = ((TimeListAdapter) SwipeClockBar.this.mTimeListView.getAdapter());
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
		int hour = mFirstHourOfDay;
		for (int i = 0; i < mHoursPerDay; ++i) {
			hours.add(hour);

			hour = (hour + 1) % mHoursPerDay;
		}

		return hours;
	}	

	private class TimeListAdapter extends BaseAdapter {

		private List<Integer> hours;
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
			this.hours = hours;
			this.listViewHeight = listViewHeight;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (hours != null) {
				count = hours.size();
			}

			if (mSmallScreenMode) {
				count = 12;
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
			if (hours != null) {
				int hourArrayIndex = positionInListView;

				if (mSmallScreenMode) {
					if (!showOddHour()) {
						hourArrayIndex = positionInListView * 2;
					} else {
						hourArrayIndex = positionInListViewTo24Index(positionInListView);
					}
				}

				hour = hours.get(hourArrayIndex);
			}
			return hour;
		}

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
				rowView = mInflater.inflate(R.layout.row_timebar, null);
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

			int colorId;

			int indexToCheckForColor = position;
			if(mSmallScreenMode) {
				indexToCheckForColor = 2 * position;
				if (showOddHour()) {
					indexToCheckForColor = positionInListViewTo24Index(position);
				}
			}

			if (mIsHighlighted) {
				if (indexToCheckForColor == index24OfSelectedHour) {
					colorId = R.color.white;
					rowView.setBackgroundColor(mActivity.getResources().getColor(R.color.red));
				} else if (mIsToday && isEarlier(hour, Integer.parseInt(DateUtilities.getCurrentHourString()))) {
					colorId = R.color.grey5;
					rowView.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));
				} else {
					colorId = R.color.red;
					rowView.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));
				}
			}
			else {
				if (indexToCheckForColor == index24OfSelectedHour) {
					colorId = R.color.white;
					rowView.setBackgroundColor(mActivity.getResources().getColor(R.color.grey4));
				} else if (mIsToday && isEarlier(hour, Integer.parseInt(DateUtilities.getCurrentHourString()))) {
					colorId = R.color.grey5;
					rowView.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));
				} else {
					colorId = R.color.grey2;
					rowView.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));
				}
			}

			int textColor = mActivity.getResources().getColor(colorId);

			holder.textView.setTextColor(textColor);

			if (listViewHeight > 0) {
				int cellHeight = listViewHeight / getCount();

				AbsListView.LayoutParams params = (AbsListView.LayoutParams) rowView.getLayoutParams();
				if (params == null) {
					params = new AbsListView.LayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, cellHeight));
				}
				params.height = cellHeight;
				rowView.setLayoutParams(params);

				// If this is the first view, calculate text size: DISABLED FOR NOW
//				if (mSavedTextSize < 0) {
//					mSavedTextSize = getTextSize(cellHeight, holder.textView, hourString);
					if (mSmallScreenMode) {
						mSavedTextSize = 12;
						holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSavedTextSize);
					} 
					else if (mScreenHeight > SCREEN_HEIGHT_TABLET) {
						mSavedTextSize = 34;
						holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSavedTextSize);
					}
//				}
//								holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSavedTextSize);

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
		mListAdapter.setSelectedHour(index);
		mListAdapter.notifyDataSetChanged();
	}


	private void broadcastClockSelectionChanged(int hour) {
		Intent intent = new Intent(Consts.INTENT_EXTRA_CLOCK_SELECTION);
		intent.putExtra(Consts.INTENT_EXTRA_CLOCK_SELECTION_VALUE, hour);
		SecondScreenApplication.getInstance().setSelectedHour(Integer.valueOf(hour));
		LocalBroadcastManager.getInstance(mActivity.getBaseContext()).sendBroadcast(intent);
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
		if (first < second && first >= mFirstHourOfDay) {
			return true;
		}
		else {
			return false;
		}
	}

	public void setToday(boolean isToday) {
		this.mIsToday = isToday;
	}

	public void highlightClockbar() {
		mIsHighlighted = true;
		mListAdapter.notifyDataSetChanged();
		mClockIconTextView.setTextColor(getResources().getColor(R.color.red));
		mClockbarContainer.setBackgroundResource(R.drawable.layout_rounded_corners_grey0_clockbar);
	}

	public void dehighlightClockbar() {
		mIsHighlighted = false;
		mListAdapter.notifyDataSetChanged();
		mClockIconTextView.setTextColor(getResources().getColor(R.color.grey5));
		mClockbarContainer.setBackgroundResource(R.color.transparent);
	}
}
