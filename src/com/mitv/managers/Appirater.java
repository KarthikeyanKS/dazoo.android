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

public class Appirater {

	private static final String PREF_LAUNCH_COUNT = "launch_count";
	private static final String PREF_EVENT_COUNT = "event_count";
	private static final String PREF_REMINDER_COUNT = "reminder_count";
	private static final String PREF_RATE_CLICKED = "rateclicked";
	private static final String PREF_DONT_SHOW = "dontshow";
	private static final String PREF_DATE_REMINDER_PRESSED = "date_reminder_pressed";
	private static final String PREF_DATE_FIRST_LAUNCHED = "date_firstlaunch";
	private static final String PREF_APP_VERSION_CODE = "versioncode";
	private static final String PREFS_NAME = ".appirater";


	private static SharedPreferences getSharedPrefs(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + PREFS_NAME, 0);
		return prefs;
	}

	public static void appLaunched(Context context) {
		boolean testMode = context.getResources().getBoolean(R.bool.appirator_test_mode);
	
		SharedPreferences prefs = getSharedPrefs(context);
		if (!testMode && (prefs.getBoolean(PREF_DONT_SHOW, false) || prefs.getBoolean(PREF_RATE_CLICKED, false))) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		if (testMode) {
			showRateDialog(context);
			return;
		}

		// Increment launch counter
		long launchCount = prefs.getLong(PREF_LAUNCH_COUNT, 0);

		// Get events counter
		long eventCount = prefs.getLong(PREF_EVENT_COUNT, 0);

		// Get date of first launch
		long dateFirstLaunch = prefs.getLong(PREF_DATE_FIRST_LAUNCHED, 0);

		try {
			int appVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			if (prefs.getInt(PREF_APP_VERSION_CODE, 0) != appVersionCode) {
				// Reset the launch, reminder and event counters to help assure users are rating based on the latest version.
				launchCount = 0;
				eventCount = 0;
				editor.putLong(PREF_EVENT_COUNT, eventCount);
				editor.putLong(PREF_LAUNCH_COUNT, launchCount);
				editor.putLong(PREF_REMINDER_COUNT, 0);
			}
			editor.putInt(PREF_APP_VERSION_CODE, appVersionCode);
		} catch (Exception e) {
			// do nothing
		}

		launchCount++;
		editor.putLong(PREF_LAUNCH_COUNT, launchCount);

		if (dateFirstLaunch == 0) {
			dateFirstLaunch = System.currentTimeMillis();
			editor.putLong(PREF_DATE_FIRST_LAUNCHED, dateFirstLaunch);
		}
		
		editor.commit();
	}
	
	private static void tryShowRateDialog(Context context) {
		boolean useAndInsteadOfOrAsBooleanOperatorBetweenEventAndDaysUntil = context.getResources().getBoolean(R.bool.appirator_use_logical_and_instead_of_or_for_event_and_days_until_condition);
		boolean capNumberOfReminders = context.getResources().getBoolean(R.bool.appirator_cap_number_of_reminders);
		int reminderCap = context.getResources().getInteger(R.integer.appirator_max_reminder_count);
		
		SharedPreferences prefs = getSharedPrefs(context);
		
		long launchCount = prefs.getLong(PREF_LAUNCH_COUNT, 0);

		// Get events counter
		long eventCount = prefs.getLong(PREF_EVENT_COUNT, 0);

		// Get date of first launch
		long dateFirstLaunch = prefs.getLong(PREF_DATE_FIRST_LAUNCHED, 0);

		// Get reminder date pressed
		long dateReminderPressed = prefs.getLong(PREF_DATE_REMINDER_PRESSED, 0);
		
		// Get reminder counter
		long reminderCount = prefs.getLong(PREF_REMINDER_COUNT, 0);
		
		// Wait at least n days or m events before opening
		if (launchCount >= context.getResources().getInteger(R.integer.appirator_launches_until_prompt)) {
			long millisecondsToWait = context.getResources().getInteger(R.integer.appirator_days_until_prompt) * 24 * 60 * 60 * 1000L;

			boolean showDialog;
			boolean enoughDaysHavePassed = (System.currentTimeMillis() >= (dateFirstLaunch + millisecondsToWait));
			boolean enoughEventsHaveHappened = (eventCount >= context.getResources().getInteger(R.integer.appirator_events_until_prompt));

			if (useAndInsteadOfOrAsBooleanOperatorBetweenEventAndDaysUntil) {
				showDialog = enoughDaysHavePassed && enoughEventsHaveHappened;
			} else {
				showDialog = enoughDaysHavePassed || enoughEventsHaveHappened;
			}

			if (showDialog) {
				if(dateReminderPressed > 0) {
					long remindMillisecondsToWait = context.getResources().getInteger(R.integer.appirator_days_before_reminding) * 24 * 60 * 60 * 1000L;
	
					boolean enoughTimePassedSinceLastReminder = (System.currentTimeMillis() >= (remindMillisecondsToWait + dateReminderPressed));
					
					if(enoughTimePassedSinceLastReminder) {
						if(capNumberOfReminders) {
							boolean reminderAmountCapReached = reminderCount >= reminderCap; //TODO read from prefs
							if(reminderAmountCapReached) {
								showDialog = false;
							}
						}
					} else {
						showDialog = false;
					}
				}
			}
			
			if (showDialog) {
				showRateDialog(context);
			}
		}
	}

	public static void rateApp(Context context) {
		SharedPreferences prefs = getSharedPrefs(context);
		SharedPreferences.Editor editor = prefs.edit();
		rateApp(context, editor);
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
			editor.commit();
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
				rateApp(context, editor);
				dialog.dismiss();
			}
		});

		Button rateLaterButton = (Button) layout.findViewById(R.id.rateLater);
		rateLaterButton.setText(context.getString(R.string.rate_later));
		rateLaterButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {
					
					/* Update number of times "remind me later" has been pressed */
					long reminderCount = prefs.getLong(PREF_REMINDER_COUNT, 0);
					reminderCount++;
					editor.putLong(PREF_REMINDER_COUNT, reminderCount);
					
					editor.putLong(PREF_DATE_REMINDER_PRESSED, System.currentTimeMillis());
					editor.commit();
				}
				dialog.dismiss();
			}
		});

		Button cancelButton = (Button) layout.findViewById(R.id.cancel);
		cancelButton.setText(context.getString(R.string.rate_cancel));
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {
					editor.putBoolean(PREF_DONT_SHOW, true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(layout);
		dialog.show();
	}
}