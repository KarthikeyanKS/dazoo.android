
package com.mitv.utilities;



import java.util.Locale;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.millicom.mitv.models.TVBroadcast;
import com.mitv.R;



public class ProgressBarUtils
{
	private static final int MINUTES_PER_HOUR = 60;
	
	private static boolean isSpanish() {
		boolean isSpanish = (Locale.getDefault().getLanguage().endsWith("es")) ;
		return isSpanish;
	}
	
	public static void setupProgressBar(Activity mActivity, TVBroadcast broadcast, ProgressBar progressBar, TextView progressTxt)
	{
		int durationInMinutes = broadcast.getBroadcastDurationInMinutes();
		progressBar.setMax(durationInMinutes + 1);

		int minutesLeft = broadcast.getRemainingMinutesUntilBroadcastEnds();
		
		// Calculate the current progress of the ProgressBar and update.
		int progress = durationInMinutes - minutesLeft;

		// different representation of "X min left" for Spanish and all other languages
		
		String progressBarText;
		StringBuilder sb = new StringBuilder();
		
		Resources res = mActivity.getResources();
		
	
		String and = res.getString(R.string.and);
		String minutesWord = res.getString(R.string.minutes);
		String leftWordSingular = res.getString(R.string.left);
		String hourWordQuantified;
		
			
		if (minutesLeft > MINUTES_PER_HOUR) 
		{
			
			int hours = minutesLeft / MINUTES_PER_HOUR;
			int minutesComponent = minutesLeft % MINUTES_PER_HOUR;
			
			hourWordQuantified = res.getQuantityString(R.plurals.hour, hours);
			
			if(isSpanish()) {
				String leftWordQuantifiedByHours = res.getQuantityString(R.plurals.left, hours);
				
				sb
				.append(leftWordQuantifiedByHours)
				.append(" ")
				.append(hours)
				.append(" ")
				.append(hourWordQuantified)
				.append(" ")
				.append(and)
				.append(" ")
				.append(minutesComponent)
				.append(" ")
				.append(minutesWord);
				
			} else {
				sb
				.append(hours)
				.append(" ")
				.append(hourWordQuantified)
				.append(" ")
				.append(and)
				.append(" ")
				.append(minutesComponent)
				.append(" ")
				.append(minutesWord)
				.append(" ")
				.append(leftWordSingular);
				
			}
		} else {			
			if(isSpanish()) {
				String leftWordQuantifiedByMinutes = res.getQuantityString(R.plurals.left, minutesLeft);
				
				sb
				.append(leftWordQuantifiedByMinutes)
				.append(" ")
				.append(minutesLeft)
				.append(" ")
				.append(minutesWord);
			} else {
				sb
				.append(minutesLeft)
				.append(" ")
				.append(minutesWord)
				.append(" ")
				.append(leftWordSingular);
			}
		}
		progressBarText = sb.toString();
		progressTxt.setText(progressBarText);
	
		progressBar.setProgress(progress + 1);
		progressTxt.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}
}