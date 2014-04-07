
package com.mitv.utilities;



import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.models.TVBroadcast;



public abstract class LanguageUtils
{
	private static final String TAG = LanguageUtils.class.getName();
	
	
	
	/*
	 * This method returns an ISO8601 compliant locale
	 */
	public static Locale getISO8601Locale()
	{
		Locale locale = new Locale("en", "dk");
		
		return locale;
	}
	
	
	
	public static Locale getCurrentLocale() 
	{
		Locale locale = null;
		
		if (SecondScreenApplication.sharedInstance() != null && SecondScreenApplication.sharedInstance().getApplicationContext() != null) 
		{
			if(Constants.FORCE_SPANISH_LOCALE)
			{
				locale = new Locale("es");
			}
			else
			{
				Context context = SecondScreenApplication.sharedInstance().getApplicationContext();

				locale = context.getResources().getConfiguration().locale;
			}
		}
		
		if (locale == null) 
		{
			locale = Locale.getDefault();

			Log.w(TAG, "Using default locale.");
		}

		return locale;
	}
	
	
	
	public static boolean isCurrentLocaleSpanish()
	{
		boolean isCurrentLocaleSpanish = (getCurrentLocale().getLanguage().endsWith("es"));
		
		return isCurrentLocaleSpanish;
	}
	
	
	
	
	public static String capitalize(
			final String input,
			final Locale locale)
	{
		StringBuilder sb = new StringBuilder();
		
		if(input != null && 
		   input.isEmpty() == false)
		{
			String firstChar = input.substring(0, 1);
			
			String remainingString = input.substring(1);
			
			sb.append(firstChar.toUpperCase(locale));
			sb.append(remainingString);
		}
		
		return sb.toString();
	}
	
	
	
	
	public static void setupProgressBar(
			Activity activity, 
			TVBroadcast broadcast, 
			ProgressBar progressBar, 
			TextView progressTxt)
	{
		int durationInMinutes = broadcast.getBroadcastDurationInMinutes();
		
		progressBar.setMax(durationInMinutes + 1);

		int minutesLeft = broadcast.getRemainingMinutesUntilBroadcastEnds();
		
		int progress = durationInMinutes - minutesLeft;

		// different representation of "X min left" for Spanish and all other languages
		
		String progressBarText;
		StringBuilder sb = new StringBuilder();
		
		Resources res = activity.getResources();
		
		String and = res.getString(R.string.and);
		String minutesWord = res.getString(R.string.minutes);
		String leftWordSingular = res.getString(R.string.left);
		String hourWordQuantified;
		
		boolean isCurrentLocaleSpanish = isCurrentLocaleSpanish();
			
		if (minutesLeft > DateUtils.TOTAL_MINUTES_IN_ONE_HOUR) 
		{
			int hours = minutesLeft / DateUtils.TOTAL_MINUTES_IN_ONE_HOUR;
			
			int minutesComponent = minutesLeft % DateUtils.TOTAL_MINUTES_IN_ONE_HOUR;
			
			hourWordQuantified = res.getQuantityString(R.plurals.hour, hours);
			
			if(isCurrentLocaleSpanish) 
			{
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
		} 
		else 
		{			
			if(isCurrentLocaleSpanish) 
			{
				String leftWordQuantifiedByMinutes = res.getQuantityString(R.plurals.left, minutesLeft);
				
				sb
				.append(leftWordQuantifiedByMinutes)
				.append(" ")
				.append(minutesLeft)
				.append(" ")
				.append(minutesWord);
			} 
			else 
			{
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