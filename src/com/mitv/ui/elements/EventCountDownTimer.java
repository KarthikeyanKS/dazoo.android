
package com.mitv.ui.elements;



import java.text.NumberFormat;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import com.mitv.utilities.DateUtils;



public class EventCountDownTimer 
	extends CountDownTimer
{
	private static final String TAG = EventCountDownTimer.class.getName();
	
	private static final long DEFAULT_COUNTDOWN_INTERVAL = DateUtils.TOTAL_MILLISECONDS_IN_ONE_SECOND*30;
	
	
	private String eventName;
	private TextView daysTextView;
	private TextView hoursTextView;
	private TextView minutesTextView;
	
	
	
	public EventCountDownTimer(
			final String eventName,
			final long millisecondsUntilEventStart,
			final TextView daysTextView,
			final TextView hoursTextView,
			final TextView minutesTextView)
	{
		this(eventName, millisecondsUntilEventStart, DEFAULT_COUNTDOWN_INTERVAL, daysTextView, hoursTextView, minutesTextView);
	}
	
	
	public EventCountDownTimer(
			final String eventName,
			final long millisInFuture, 
			final long countDownInterval,
			final TextView daysTextView,
			final TextView hoursTextView,
			final TextView minutesTextView)
	{
		super(millisInFuture, countDownInterval);
		
		this.eventName = eventName;
		
		this.daysTextView = daysTextView;
		this.hoursTextView = hoursTextView;
		this.minutesTextView = minutesTextView;
	}
	
	
	
	@Override
	public void onTick(long millisUntilFinished) 
    {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(2);
		
		int totalDaysLeft = (int) (millisUntilFinished / DateUtils.TOTAL_MILLISECONDS_IN_ONE_DAY);
		
		StringBuilder daysLeftSB = new StringBuilder();
		daysLeftSB.append(nf.format(totalDaysLeft));
		
		long totalHoursLeftInMilliseconds = millisUntilFinished - (totalDaysLeft*DateUtils.TOTAL_MILLISECONDS_IN_ONE_DAY);	
		int hoursLeft = (int) (totalHoursLeftInMilliseconds / DateUtils.TOTAL_MILLISECONDS_IN_ONE_HOUR);
				
		StringBuilder hoursLeftSB = new StringBuilder();
		hoursLeftSB.append(nf.format(hoursLeft));
		
		long totalMinutesLeftInMilliseconds = millisUntilFinished - (hoursLeft*DateUtils.TOTAL_MILLISECONDS_IN_ONE_HOUR);
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
    	
    	daysTextView.setText("00");
		hoursTextView.setText("00");
		minutesTextView.setText("00");
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
