package com.mitv.managers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mitv.Constants;
import com.mitv.R;

/*	
 * @source https://github.com/sbstrm/appirater-android
 * @license MIT/X11
 * 
 * Copyright (c) 2011-2013 sbstrm Y.K.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

public class RateAppManager {

	private static final String PREF_LAUNCH_COUNT = "launch_count";
	private static final String PREF_EVENT_COUNT = "event_count";
	private static final String PREF_REMINDER_COUNT = "reminder_count";
	private static final String PREF_RATE_CLICKED = "rateclicked";
	private static final String PREF_DONT_SHOW = "dontshow";
	private static final String PREF_DATE_REMINDER_PRESSED = "date_reminder_pressed";
	private static final String PREF_DATE_FIRST_LAUNCHED = "date_firstlaunch";
	private static final String PREF_APP_VERSION_CODE = "versioncode";
	private static final String PREFS_NAME = ".appirater";

	/* SOLUTION UNTIL BACKEND CAN DETERMINE IF THE RATE APP DIALOG SHOULD BE SHOWN */
	/*
	 * This is a boolean that get set to true after a limit of days has passed since first launch. at the same time it is set to true, the
	 * PREFS_ONE_SHOT_SHOW_DIALOG_RESULT boolean value is set, that evaluates if a high enough number of user events have happened.
	 * PREFS_ONE_SHOT_SHOW_DIALOG_RESULT is only set once, and that is if PREFS_ONE_SHOT_VALUE_SET was false and enough days has passed
	 * since first launch
	 */
	private static final String PREFS_HAS_SHOW_DIALOG_VALUE_BEEN_SET = "has_show_dialog_value_been_set";
	private static final String PREFS_SHOW_DIALOG_VALUE = "show_dialog_value";

	private static SharedPreferences getSharedPrefs(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + PREFS_NAME, 0);
		return prefs;
	}

	
	public static void appLaunched(Context context) {
		boolean testMode = context.getResources().getBoolean(R.bool.appirator_test_mode);
		if (testMode) {
			showRateDialog(context);
			return;
		}

		SharedPreferences prefs = getSharedPrefs(context);
		
		boolean dontShowDialog = prefs.getBoolean(PREF_DONT_SHOW, false) || prefs.getBoolean(PREF_RATE_CLICKED, false);
		if (dontShowDialog) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launchCount = prefs.getLong(PREF_LAUNCH_COUNT, 0);
		
		try {
			int appVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			if (prefs.getInt(PREF_APP_VERSION_CODE, 0) != appVersionCode) {
				// Reset the launch, reminder and event counters to help assure users are rating based on the latest version.
				// launchCount = 0;
				// editor.putLong(PREF_EVENT_COUNT, 0);
				// editor.putLong(PREF_LAUNCH_COUNT, launchCount);
				// editor.putLong(PREF_REMINDER_COUNT, 0);
			}
			editor.putInt(PREF_APP_VERSION_CODE, appVersionCode);
		} catch (Exception e) {
			// do nothing
		}

		launchCount++;
		editor.putLong(PREF_LAUNCH_COUNT, launchCount);

		// Get date of first launch
		long dateFirstLaunch = prefs.getLong(PREF_DATE_FIRST_LAUNCHED, 0);
		if (dateFirstLaunch == 0) {
			dateFirstLaunch = System.currentTimeMillis();
			editor.putLong(PREF_DATE_FIRST_LAUNCHED, dateFirstLaunch);
		}

		editor.apply();

		/* Show dialog if criteria is met */
		tryShowRateDialog(context);
	}

	private static boolean hasEnoughDaysPassed(Context context) {
		SharedPreferences prefs = getSharedPrefs(context);
		long dateFirstLaunch = prefs.getLong(PREF_DATE_FIRST_LAUNCHED, 0);
		long millisecondsToWait = context.getResources().getInteger(R.integer.appirator_days_until_prompt) * 24 * 60 * 60 * 1000L;
		boolean enoughDaysHavePassed = (System.currentTimeMillis() >= (dateFirstLaunch + millisecondsToWait));

		return enoughDaysHavePassed;

	}

	private static boolean hasAppBeenLaunchedEnoughTimes(Context context) {
		SharedPreferences prefs = getSharedPrefs(context);
		long launchCount = prefs.getLong(PREF_LAUNCH_COUNT, 0);
		boolean enoughLaunches = launchCount >= context.getResources().getInteger(R.integer.appirator_launches_until_prompt);
		return enoughLaunches;
	}

//	private static boolean hasEnoughEventsHappened(Context context) {
//		SharedPreferences prefs = getSharedPrefs(context);
//		long eventCount = prefs.getLong(PREF_EVENT_COUNT, 0);
//		boolean enoughEventsHaveHappened = (eventCount >= context.getResources().getInteger(R.integer.appirator_events_until_prompt));
//		return enoughEventsHaveHappened;
//	}

	private static boolean hasBeenRemindedOnceAlready(Context context) {
		boolean hasBeenRemindedOnceAlready = false;
		
		SharedPreferences prefs = getSharedPrefs(context);
		long dateReminderPressed = prefs.getLong(PREF_DATE_REMINDER_PRESSED, 0);
		if (dateReminderPressed > 0) {
			hasBeenRemindedOnceAlready = true;
		}
		
		return hasBeenRemindedOnceAlready;
	}
	
	private static boolean hasEnoughTimePassedSinceLastReminder(Context context) {
		boolean hasEnoughTimePassedSinceLastReminder = false;

		SharedPreferences prefs = getSharedPrefs(context);
		long dateReminderPressed = prefs.getLong(PREF_DATE_REMINDER_PRESSED, 0);
		if (dateReminderPressed > 0) {
			long remindMillisecondsToWait = context.getResources().getInteger(R.integer.appirator_days_before_reminding) * 24 * 60 * 60 * 1000L;
			hasEnoughTimePassedSinceLastReminder = (System.currentTimeMillis() >= (remindMillisecondsToWait + dateReminderPressed));
		}
		return hasEnoughTimePassedSinceLastReminder;
	}

	private static boolean hasBeenRemindedTooManyTimes(Context context) {
		SharedPreferences prefs = getSharedPrefs(context);
		int reminderCap = context.getResources().getInteger(R.integer.appirator_max_reminder_count);
		int reminderCount = prefs.getInt(PREF_REMINDER_COUNT, 0);

		boolean hasBeenRemindedTooManyTimes = reminderCount >= reminderCap;

		return hasBeenRemindedTooManyTimes;
	}

	public static void tryShowRateDialog(Context context) {
		boolean preventRateAppDialogBackend = ContentManager.sharedInstance().getFromCacheAppConfiguration().isPreventingRateAppDialog();
		boolean preventRateAppDialogLocal = !Constants.ENABLE_RATE_APP_DIALOG;
		if (preventRateAppDialogBackend || preventRateAppDialogLocal) {
			return;
		}

		boolean showDialog;

		SharedPreferences prefs = getSharedPrefs(context);
		SharedPreferences.Editor editor = prefs.edit();

		boolean persistentShowDialogValueHasBeenSet = prefs.getBoolean(PREFS_HAS_SHOW_DIALOG_VALUE_BEEN_SET, false);

		if (persistentShowDialogValueHasBeenSet) {
			/* Read value from preferences */
			showDialog = prefs.getBoolean(PREFS_SHOW_DIALOG_VALUE, false);
		} else {
			/* Show dialog value has not yet been set */
			
			if (hasEnoughDaysPassed(context)) {
				if(hasAppBeenLaunchedEnoughTimes(context)) {
					showDialog = true;
				} else {
					showDialog = false;
				}
				editor.putBoolean(PREFS_SHOW_DIALOG_VALUE, showDialog);
				editor.putBoolean(PREFS_HAS_SHOW_DIALOG_VALUE_BEEN_SET, true);
				editor.apply();
			} else {
				/* Not enough days have passed yet, so don't show dialog */
				showDialog = false;
			}
		}

		if (showDialog && hasBeenRemindedOnceAlready(context)) {
			if (hasEnoughTimePassedSinceLastReminder(context)) {
				boolean capNumberOfReminders = context.getResources().getBoolean(R.bool.appirator_cap_number_of_reminders);

				if (capNumberOfReminders) {
					if (!hasBeenRemindedTooManyTimes(context)) {
						showDialog = true;
					}
				} else {
					showDialog = true;
				}
			} else {
				/* Not enough time has passed... */
				showDialog = false;
			}
		}


		
		if (showDialog) {
			showRateDialog(context);
		}
	}

	@SuppressLint("NewApi")
	public static void significantEvent(Context context) {
		boolean testMode = context.getResources().getBoolean(R.bool.appirator_test_mode);
		SharedPreferences prefs = getSharedPrefs(context);
		if (!testMode && (prefs.getBoolean(PREF_DONT_SHOW, false) || prefs.getBoolean(PREF_RATE_CLICKED, false))) {
			return;
		}

		long eventCount = prefs.getLong(PREF_EVENT_COUNT, 0);
		eventCount++;
		prefs.edit().putLong(PREF_EVENT_COUNT, eventCount).apply();

		/* Show dialog if criteria is met */
		tryShowRateDialog(context);
	}

	private static void rateApp(Context context, final SharedPreferences.Editor editor) {
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(context.getString(R.string.appirator_market_url), context.getPackageName()))));
		if (editor != null) {
			editor.putBoolean(PREF_RATE_CLICKED, true);
			editor.apply();
		}
	}

	@SuppressLint("NewApi")
	private static void showRateDialog(final Context context) {
		final SharedPreferences prefs = getSharedPrefs(context);
		final SharedPreferences.Editor editor = prefs.edit();

		final Dialog dialog = new Dialog(context);

		LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.appirater, null);

		Button rateButton = (Button) layout.findViewById(R.id.rate);
		rateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TrackingGAManager.sharedInstance().sendUserPressedRateInRateDialogEvent();
				rateApp(context, editor);
				dialog.dismiss();
			}
		});

		Button rateLaterButton = (Button) layout.findViewById(R.id.rateLater);
		rateLaterButton.setText(context.getString(R.string.rate_later));
		rateLaterButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TrackingGAManager.sharedInstance().sendUserPressedRemindLaterInRateDialogEvent();
				if (editor != null) {
					/* Update number of times "remind me later" has been pressed */
					long reminderCount = prefs.getLong(PREF_REMINDER_COUNT, 0);
					reminderCount++;
					editor.putLong(PREF_REMINDER_COUNT, reminderCount);

					editor.putLong(PREF_DATE_REMINDER_PRESSED, System.currentTimeMillis());
					editor.apply();
				}
				dialog.dismiss();
			}
		});

		Button cancelButton = (Button) layout.findViewById(R.id.cancel);
		cancelButton.setText(context.getString(R.string.rate_cancel));
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TrackingGAManager.sharedInstance().sendUserPressedNoThanksInRateDialogEvent();
				if (editor != null) {
					editor.putBoolean(PREF_DONT_SHOW, true);
					editor.apply();
				}
				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(layout);
		dialog.show();
	}
}