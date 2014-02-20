package com.mitv.utilities;

import java.util.Locale;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.millicom.mitv.models.Broadcast;
import com.mitv.R;

public class ProgressBarUtils {
	public static void setupProgressBar(Activity mActivity, Broadcast broadcast, ProgressBar progressBar, TextView progressTxt){
		progressBar.setMax(broadcast.getDurationInMinutes() + 1);

		// Calculate the current progress of the ProgressBar and update.
		int initialProgress = 0;

		initialProgress = broadcast.minutesSinceStart();
		int timeLeft = broadcast.getDurationInMinutes() - initialProgress;

		// different representation of "X min left" for Spanish and all other languages
		if (Locale.getDefault().getLanguage().endsWith("es")) {
			if (timeLeft > 60) {
				int hours = timeLeft / 60;
				int minutes = timeLeft - hours * 60;
				progressTxt.setText(mActivity.getResources().getQuantityString(R.plurals.left, hours) + " " + hours + " " + 
						mActivity.getResources().getQuantityString(R.plurals.hour, hours) + " " + 
						mActivity.getResources().getString(R.string.and) + " " + minutes + " " + 
						mActivity.getResources().getString(R.string.minutes));
			}
			else {
				progressTxt.setText(mActivity.getResources().getString(R.string.left) + " " + String.valueOf(timeLeft) + " " + 
						mActivity.getResources().getString(R.string.minutes));
			}
		} 
		else {
			if (timeLeft > 60) {
				int hours = timeLeft / 60;
				int minutes = timeLeft - hours * 60;
				progressTxt.setText(hours + " " + mActivity.getResources().getQuantityString(R.plurals.hour, hours) + " " + 
						mActivity.getResources().getString(R.string.and) + " " + minutes + " " + 
						mActivity.getResources().getString(R.string.minutes) + " " + 
						mActivity.getResources().getString(R.string.left));
			}
			else {
				progressTxt.setText(timeLeft + " " + mActivity.getResources().getString(R.string.minutes) + " " + 
						mActivity.getResources().getString(R.string.left));
			}
		}
		progressBar.setProgress(initialProgress + 1);
		progressTxt.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
	}
}
