package com.millicom.secondscreen.customviews;

import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.SeekBar;
import android.widget.TextView;

public class VerticalSeekBar extends SeekBar {

	private static final String tag = "VerticalSeekBarSmallThumb (internal)";

	private static final int millisDelay = 300;
	private static final int fadeOutAnimationDuration = 650;

	private static boolean aboutToHideTextView;

	private TextView textView;

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
		aboutToHideTextView = false;
	}

	public void setTextView(TextView textView) {
		this.textView = textView;
	}

	private void updateTextViewText() {
		int hoursPerDay = 24;
		// TODO read value from app config
		int firstHourOfDay = 6;
		int hour = (getProgress() + firstHourOfDay) % hoursPerDay;
		String hourString = String.format(Locale.getDefault(), "%02d:00", hour);
		if (textView != null) {
			textView.setVisibility(View.VISIBLE);
			textView.setText(hourString);
		}
	}

	private void hideTextView() {
		if (textView != null) {

			/* Don't post more than one "job" */
			if (!aboutToHideTextView) {
				aboutToHideTextView = true;
				Animation animationFadeOut = new AlphaAnimation(1, 0);
				animationFadeOut.setInterpolator(new AccelerateInterpolator());
																				
				animationFadeOut.setDuration(fadeOutAnimationDuration);
				animationFadeOut.setStartOffset(millisDelay);
				animationFadeOut.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {
						textView.setVisibility(View.GONE);
						aboutToHideTextView = false;
					}
				});

				textView.setAnimation(animationFadeOut);
				animationFadeOut.start();

			}
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

		if (event.getAction() == MotionEvent.ACTION_UP) {
			hideTextView();
		}

		return true;
	}

	/* Important to override, since width and height are switched!! */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
	}
}