package com.millicom.secondscreen.customviews;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.manager.AppConfigurationManager;

public class VerticalSeekBar extends SeekBar {

	private static final String tag = "VerticalSeekBarSmallThumb (internal)";

	private Activity activity;
	private Toast toast;

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

	public void setActivity(Activity activity) {
		this.activity = activity;
		
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.timebar_toast, (ViewGroup) activity.findViewById(R.id.timebar_toast_container));
		this.toast = new Toast(activity.getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
	}
	
	private void updateTextViewText() {
		int hoursPerDay = 24;
		int firstHourOfDay = AppConfigurationManager.getInstance().getFirstHourOfTVDay();
		int hour = (getProgress() + firstHourOfDay) % hoursPerDay;
		String hourString = String.format(Locale.getDefault(), "%02d:00", hour);

		TextView text = (TextView) toast.getView().findViewById(R.id.timebar_toast_textview);
		text.setText(hourString);
		
		if (null == toast.getView().getWindowToken())
		{
			toast.show();
		}
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
		case MotionEvent.ACTION_UP: {
			int max = getMax();
			int height = getHeight();
			int i = max - (int) (max * event.getY() / height);
			setProgress(getMax() - i);

			onSizeChanged(getWidth(), height, 0, 0);
			updateTextViewText();
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