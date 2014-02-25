
package com.mitv.utilities;



import java.util.Locale;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.millicom.mitv.models.TVBroadcast;
import com.mitv.R;



public class ProgressBarUtils
{
	private static final int MINUTES_PER_HOUR = 60;
	
	public static void setupProgressBar(Activity mActivity, TVBroadcast broadcast, ProgressBar progressBar, TextView progressTxt)
	{
		progressBar.setMax(broadcast.getBroadcastDurationInMinutes() + 1);

		// Calculate the current progress of the ProgressBar and update.
		int initialProgress = 0;

		int timeLeft = broadcast.getRemainingMinutesUntilBroadcastEnds();

		// different representation of "X min left" for Spanish and all other languages
		if (Locale.getDefault().getLanguage().endsWith("es")) 
		{
			if (timeLeft > MINUTES_PER_HOUR) 
			{
				int hours = timeLeft / MINUTES_PER_HOUR;
				int minutes = timeLeft - hours * MINUTES_PER_HOUR;
				
				progressTxt.setText(mActivity.getResources().getQuantityString(R.plurals.left, hours) + " " + hours + " " + 
						mActivity.getResources().getQuantityString(R.plurals.hour, hours) + " " + 
						mActivity.getResources().getString(R.string.and) + " " + minutes + " " + 
						mActivity.getResources().getString(R.string.minutes));
			}
			else 
			{
				progressTxt.setText(mActivity.getResources().getString(R.string.left) + " " + String.valueOf(timeLeft) + " " + 
						mActivity.getResources().getString(R.string.minutes));
			}
		} 
		else 
		{
			if (timeLeft > MINUTES_PER_HOUR) 
			{
				int hours = timeLeft / MINUTES_PER_HOUR;
				
				int minutes = timeLeft - hours * MINUTES_PER_HOUR;
				
				progressTxt.setText(hours + " " + mActivity.getResources().getQuantityString(R.plurals.hour, hours) + " " + 
						mActivity.getResources().getString(R.string.and) + " " + minutes + " " + 
						mActivity.getResources().getString(R.string.minutes) + " " + 
						mActivity.getResources().getString(R.string.left));
			}
			else 
			{
				progressTxt.setText(timeLeft + " " + mActivity.getResources().getString(R.string.minutes) + " " + 
						mActivity.getResources().getString(R.string.left));
			}
		}
		
		progressBar.setProgress(initialProgress + 1);
		progressTxt.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}
}