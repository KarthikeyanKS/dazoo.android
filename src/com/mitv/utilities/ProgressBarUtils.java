
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
		progressBar.setMax(broadcast.getBroadcastDurationInMinutes() + 1);

		// Calculate the current progress of the ProgressBar and update.
		int initialProgress = 0;

		int minutesLeft = broadcast.getRemainingMinutesUntilBroadcastEnds();

		// different representation of "X min left" for Spanish and all other languages
		
		String progressBarText;
		StringBuilder sb = new StringBuilder();
		
		Resources res = mActivity.getResources();
		
		String left = res.getString(R.string.left);
		String and = res.getString(R.string.and);
		String minutesWord = res.getString(R.string.minutes);
		String hourWordQuantified;
		
			
		
		if (minutesLeft > MINUTES_PER_HOUR) 
		{
			int hours = minutesLeft / MINUTES_PER_HOUR;
			int minutesComponent = minutesLeft % MINUTES_PER_HOUR;
			
			hourWordQuantified = res.getQuantityString(R.plurals.hour, hours);
			
			if(isSpanish()) {
				left = res.getQuantityString(R.string.left, hours);
				
				sb
				.append(left)
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
				.append(left);
				
			}
		} else {
			if(isSpanish()) {
				sb
				.append(left)
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
				.append(left);
			}
		}
		progressBarText = sb.toString();
		progressTxt.setText(progressBarText);
	
		
//		if (Locale.getDefault().getLanguage().endsWith("es")) 
//		{
//			if (minutesLeft > MINUTES_PER_HOUR) 
//			{
//				int hours = minutesLeft / MINUTES_PER_HOUR;
//				int minutes = minutesLeft - hours * MINUTES_PER_HOUR;
//				
//				progressTxt.setText(mActivity.getResources().getQuantityString(R.plurals.left, hours) + " " + hours + " " + 
//						mActivity.getResources().getQuantityString(R.plurals.hour, hours) + " " + 
//						mActivity.getResources().getString(R.string.and) + " " + minutes + " " + 
//						mActivity.getResources().getString(R.string.minutes));
//			}
//			else 
//			{
//				progressTxt.setText(mActivity.getResources().getString(R.string.left) + " " + String.valueOf(minutesLeft) + " " + 
//						mActivity.getResources().getString(R.string.minutes));
//			}
//		} 
//		else 
//		{
//			if (minutesLeft > MINUTES_PER_HOUR) 
//			{
//				int hours = minutesLeft / MINUTES_PER_HOUR;
//				
//				int minutes = minutesLeft - hours * MINUTES_PER_HOUR;
//				
//				progressTxt.setText(hours + " " + mActivity.getResources().getQuantityString(R.plurals.hour, hours) + " " + 
//						mActivity.getResources().getString(R.string.and) + " " + minutes + " " + 
//						mActivity.getResources().getString(R.string.minutes) + " " + 
//						mActivity.getResources().getString(R.string.left));
//			}
//			else 
//			{
//				progressTxt.setText(minutesLeft + " " + mActivity.getResources().getString(R.string.minutes) + " " + 
//						mActivity.getResources().getString(R.string.left));
//			}
//		}
		
		progressBar.setProgress(initialProgress + 1);
		progressTxt.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}
}