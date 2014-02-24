package com.mitv.customviews;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.mitv.R;
import com.mitv.manager.AppConfigurationManager;

public class VerticalSeekBar extends SeekBar {

	private static final String tag = "VerticalSeekBarSmallThumb (internal)";

	private Activity activity;
	private Toast toast;
	private CountDownTimer timer;
	private int hoursPerDay;
	private int firstHourOfDay;

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

	public void setValues(Activity activity, int hoursPerDay, int firstHourOfDay) {
		this.activity = activity;
		this.hoursPerDay = hoursPerDay;
		this.firstHourOfDay = firstHourOfDay;
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.timebar_toast, (ViewGroup) activity.findViewById(R.id.timebar_toast_container));
		this.toast = new Toast(activity.getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
	}
	
	

	private void updateTextViewText() {
		int hour = (getProgress() + firstHourOfDay) % hoursPerDay;

		String hourString = String.format(Locale.getDefault(), "%02d:00", hour);

		FontTextView text = (FontTextView) toast.getView().findViewById(R.id.timebar_toast_textview);
		text.setText(hourString);

//		if (null == toast.getView().getWindowToken()) {
			toast.show();
			if (timer != null) {
				Log.d(tag, "cancel timer");
				timer.cancel();
			}
			timer = new CountDownTimer(9000, 1000) {
				public void onTick(long millisUntilFinished) {toast.show();}
				public void onFinish() {toast.show();}
	
			}.start();
//		}
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
			timer.cancel();
			
			new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					toast.cancel();
				}
			}.run();
			
			timer = null;
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