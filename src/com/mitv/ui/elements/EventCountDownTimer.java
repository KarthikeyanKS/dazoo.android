
package com.mitv.ui.elements;



import java.text.NumberFormat;

import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.utilities.DateUtils;



public class EventCountDownTimer 
	extends CountDownTimer
{
	private static final String TAG = EventCountDownTimer.class.getName();
	
	private static final int MAXIMUM_VALUE_FOR_COUNT_VIEW = 99;
	private static final String VALUE_FOR_COUNT_VIEW_OVERFLOW = "99+";
	
	private static final long DEFAULT_COUNTDOWN_INTERVAL = DateUtils.TOTAL_MILLISECONDS_IN_ONE_SECOND*30;
	
	
	private String eventName;
	private TextView daysTextView;
	private TextView hoursTextView;
	private TextView minutesTextView;
	
	private TextView daysTitleTextView;
	private TextView hoursTitleTextView;
	private TextView minutesTitleTextView;
	
	private TextView title;
	private RelativeLayout container;
	
	public EventCountDownTimer(
			final String eventName,
			final long millisecondsUntilEventStart,
			final TextView daysTextView,
			final TextView hoursTextView,
			final TextView minutesTextView,
			final TextView remainingTimeInDaysTitle,
			final TextView remainingTimeInHoursTitle,
			final TextView remainingTimeInMinutesTitle,
			final TextView title,
			final RelativeLayout container)
	{
		this(eventName, millisecondsUntilEventStart, DEFAULT_COUNTDOWN_INTERVAL, daysTextView, hoursTextView, minutesTextView, remainingTimeInDaysTitle, remainingTimeInHoursTitle, remainingTimeInMinutesTitle);
		this.container = container;
		this.title = title;
	}
	
	
	
	public EventCountDownTimer(
			final String eventName,
			final long millisInFuture, 
			final long countDownInterval,
			final TextView daysTextView,
			final TextView hoursTextView,
			final TextView minutesTextView,
			final TextView remainingTimeInDaysTitle,
			final TextView remainingTimeInHoursTitle,
			final TextView remainingTimeInMinutesTitle)
	{
		super(millisInFuture, countDownInterval);
		
		this.eventName = eventName;
		
		this.daysTextView = daysTextView;
		this.hoursTextView = hoursTextView;
		this.minutesTextView = minutesTextView;
		
		this.daysTitleTextView = remainingTimeInDaysTitle;
		this.hoursTitleTextView = remainingTimeInHoursTitle;
		this.minutesTitleTextView = remainingTimeInMinutesTitle;
	}
	
	
	
	@Override
	public void onTick(long millisUntilFinished) 
    {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(2);
		
		int totalDaysLeft = (int) (millisUntilFinished / DateUtils.TOTAL_MILLISECONDS_IN_ONE_DAY);
		
		StringBuilder daysLeftSB = new StringBuilder();
		
		if(totalDaysLeft <= MAXIMUM_VALUE_FOR_COUNT_VIEW)
		{
			daysLeftSB.append(nf.format(totalDaysLeft));
		}
		else
		{
			daysLeftSB.append(VALUE_FOR_COUNT_VIEW_OVERFLOW);
		}
		
		long totalHoursLeftInMilliseconds = millisUntilFinished - (totalDaysLeft*DateUtils.TOTAL_MILLISECONDS_IN_ONE_DAY);	
		int hoursLeft = (int) (totalHoursLeftInMilliseconds / DateUtils.TOTAL_MILLISECONDS_IN_ONE_HOUR);
				
		StringBuilder hoursLeftSB = new StringBuilder();
		hoursLeftSB.append(nf.format(hoursLeft));
		
		long totalMinutesLeftInMilliseconds = totalHoursLeftInMilliseconds - (hoursLeft*DateUtils.TOTAL_MILLISECONDS_IN_ONE_HOUR);
		int minutesLeft = (int) (totalMinutesLeftInMilliseconds / DateUtils.TOTAL_MILLISECONDS_IN_ONE_MINUTE); 
		
		StringBuilder minutesLeftSB = new StringBuilder();
		minutesLeftSB.append(nf.format(minutesLeft));
		
		daysTextView.setText(daysLeftSB);
		hoursTextView.setText(hoursLeftSB);
		minutesTextView.setText(minutesLeftSB);
    }
	
	

	@Override
    public void onFinish() 
    {
    	Log.d(TAG, "Event " + eventName + " is now starting.");
    	
    	daysTextView.setVisibility(View.GONE);
		hoursTextView.setVisibility(View.GONE);
		minutesTextView.setVisibility(View.GONE);
		
		daysTitleTextView.setVisibility(View.GONE);
		hoursTitleTextView.setVisibility(View.GONE);
		minutesTitleTextView.setVisibility(View.GONE);
		
		title.setVisibility(View.GONE);
		if (container != null) {
			int dpHeight = 0;
			if (container.getId() == R.id.competition_count_down_area) {
				dpHeight = 70;
			}
			else if (container.getId() == R.id.competition_count_down_layout) {
				dpHeight = 150;
			}
			
			if (dpHeight > 0) {
			LayoutParams parameters = container.getLayoutParams();
			
			
			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, SecondScreenApplication.sharedInstance().getApplicationContext().getResources().getDisplayMetrics());
			
			parameters.height = height;
			
			container.setLayoutParams(parameters);
			}
		}
    }
	
	
	
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(daysTextView.getText());
		sb.append(" ");
		sb.append(hoursTextView.getText());
		sb.append(" ");
		sb.append(minutesTextView.getText());
		
		return sb.toString();
	}
}
