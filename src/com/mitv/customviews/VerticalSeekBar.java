package com.mitv.customviews;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.mitv.R;
import com.mitv.manager.AppConfigurationManager;

public class VerticalSeekBar extends SeekBar {

	private static final String tag = "VerticalSeekBarSmallThumb (internal)";
	private static final int SELECTED_HOUR_TEXTVIEW_DISPLAY_TIME = 1200;
	
	private Activity activity;
	private FontTextView selectedHourTextView;

	public VerticalSeekBar(Context context) {
		super(context);
		setup();
	}

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
	}

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	private void setup() {
	}
	
	public void setSelectedHourTextView(FontTextView selectedHourTextView) {
		this.selectedHourTextView = selectedHourTextView;
		selectedHourTextView.setVisibility(View.GONE);
	}

	public void setActivity(Activity activity) {
		this.activity = activity;

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.timebar_toast, (ViewGroup) activity.findViewById(R.id.timebar_toast_container));
	}

	private void updateTextViewText() {
		int hoursPerDay = 24;
		int firstHourOfDay = AppConfigurationManager.getInstance()
				.getFirstHourOfTVDay();
		int hour = (getProgress() + firstHourOfDay) % hoursPerDay;

		String hourString = String.format(Locale.getDefault(), "%02d:00", hour);

		selectedHourTextView.setText(hourString);
		selectedHourTextView.setVisibility(View.VISIBLE);

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				selectedHourTextView.setVisibility(View.GONE);
			}
		}, SELECTED_HOUR_TEXTVIEW_DISPLAY_TIME);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	protected void onDraw(Canvas c) {
		c.rotate(90);
		c.translate(0, -getWidth());
		super.onDraw(c);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			int max = getMax();
			int height = getHeight();
			int i = max - (int) (max * event.getY() / height);
			setProgress(getMax() - i);

			onSizeChanged(getWidth(), height, 0, 0);
			updateTextViewText();
			break;
		case MotionEvent.ACTION_UP: {
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			Log.e(tag, "ACTION_CANCEL");
			break;
		}
		}

		return true;
	}

	/* Important to override, since width and height are switched!! */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
	}
}