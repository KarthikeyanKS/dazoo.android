package com.millicom.secondscreen.notification;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.SecondScreenApplication;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.utilities.DateUtilities;

public class NotificationService {

	private static final String	TAG	= "NotificationService";

	public static boolean resetAlarm(Context context, Broadcast broadcast, Channel channel, int notificationId){

		// call alarm manager to set the notification at the certain time
		Intent intent = new Intent("DAZOO_NOTIFICATION");
		intent.putExtra(Consts.INTENT_EXTRA_BROADCAST, broadcast);
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL, channel);
		intent.putExtra(Consts.INTENT_EXTRA_NOTIFICATION_ID, notificationId);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);

		Calendar calendar;
		try {
			calendar = DateUtilities.getTimeFifteenMinBefore(broadcast.getBeginTime());

			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

			// for testing
			// alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000), pendingIntent);

			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean setAlarm(Context context, Broadcast broadcast, Channel channel) {

		Random random = new Random();
		int notificationId = random.nextInt(Integer.MAX_VALUE);

		// call alarm manager to set the notification at the certain time
		Intent intent = new Intent("DAZOO_NOTIFICATION");
		intent.putExtra(Consts.INTENT_EXTRA_BROADCAST, broadcast);
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL, channel);
		intent.putExtra(Consts.INTENT_EXTRA_NOTIFICATION_ID, notificationId);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);

		Calendar calendar;
		try {
			calendar = DateUtilities.getTimeFifteenMinBefore(broadcast.getBeginTime());

			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

			// for testing
			// alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000), pendingIntent);

			NotificationDataSource notificationDataSource = new NotificationDataSource(context);

			NotificationDbItem dbNotification = new NotificationDbItem();
			dbNotification.setNotificationId(notificationId);
			dbNotification.setBroadcstUrl(Consts.NOTIFY_BROADCAST_URL_PREFIX + channel.getId() + Consts.NOTIFY_BROADCAST_URL_MIDDLE + broadcast.getBeginTimeMillis());
			dbNotification.setProgramId(broadcast.getProgram().getProgramId());
			dbNotification.setProgramTitle(broadcast.getProgram().getTitle());

			String programType = broadcast.getProgram().getProgramType();
			if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(programType)) {
				dbNotification.setProgramType(programType);
				dbNotification.setProgramSeason(broadcast.getProgram().getSeason().getNumber());
				dbNotification.setProgramEpisode(broadcast.getProgram().getEpisode());
			} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(programType)){
				dbNotification.setProgramType(programType);
				dbNotification.setProgramYear(Integer.parseInt(broadcast.getProgram().getYear()));
			} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(programType)){
				dbNotification.setProgramType(programType);
			}
			// TODO 
			//dbNotification.setProgramTag(broadcast.getProgram().getTags().get(0));
			dbNotification.setChannelName(channel.getName());
			dbNotification.setChannelId(channel.getChannelId());
			dbNotification.setBroadcastBeginTime(broadcast.getBeginTime());
			dbNotification.setBroadcastBeginTimeMillis(String.valueOf(broadcast.getBeginTimeMillis()));

			notificationDataSource.addNotification(dbNotification);

			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean showNotification(Context context, Broadcast broadcast, Channel channel, int notificationId) {

		String broadcastUrl = Consts.NOTIFY_BROADCAST_URL_PREFIX + channel.getChannelId() + Consts.NOTIFY_BROADCAST_URL_MIDDLE + broadcast.getBeginTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent broadcastPageIntent = new Intent(context, BroadcastPageActivity.class);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_BROADCAST_URL, broadcastUrl);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_BROADCAST, broadcast);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_CHANNEL, channel);
		broadcastPageIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		int number = ((SecondScreenApplication) context.getApplicationContext()).getNotificationNumber();

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(context.getResources().getString(R.string.app_name))
		.setContentText(
				context.getResources().getString(R.string.dazoo_notification_text_start) + " " + broadcast.getProgram().getTitle()
				+ context.getResources().getString(R.string.dazoo_notification_text_middle) + " " + channel.getName()
				+ context.getResources().getString(R.string.dazoo_notification_text_end)).setContentIntent(PendingIntent.getActivity(context, 0, broadcastPageIntent, 0))
				.setAutoCancel(true).setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_ALL) // default sound, vibration, light
				.setNumber(number);

		notificationManager.notify(notificationId, notificationBuilder.build());

		// update the number of fired notifications in the status bar
		((SecondScreenApplication) context.getApplicationContext()).setNotificationNumber(number + 1);

		// remove the notification from the database
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		notificationDataSource.deleteNotification(notificationId);

		return true;
	}

	public static boolean removeNotification(Context context, int notificationId) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(notificationId);

		// remove alarm
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, notificationId, intent, 0);
		alarmManager.cancel(sender);

		// remove notification from database
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		notificationDataSource.deleteNotification(notificationId);

		return true;
	}
	
	public static void showSetNotificationToast(Activity activity) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_notification_set, (ViewGroup) activity.findViewById(R.id.notification_set_toast_container));

		final Toast toast = new Toast(activity.getApplicationContext());

		TextView text = (TextView) layout.findViewById(R.id.notification_set_toast_tv);
		text.setText(activity.getResources().getString(R.string.reminder_text_set));

		toast.setGravity(Gravity.BOTTOM, 0, 200);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static void showRemoveNotificationDialog(final Context context, Broadcast broadcast, final int notificationId) {
		String reminderText = "";
		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(broadcast.getProgram().getProgramType())) {
			reminderText = context.getString(R.string.reminder_text_remove) + broadcast.getProgram().getTitle() + ", " + context.getString(R.string.season) + " "
					+ broadcast.getProgram().getSeason().getNumber() + ", " + context.getString(R.string.episode) + " " + broadcast.getProgram().getEpisode() + "?";
		} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(broadcast.getProgram().getProgramType())) {
			reminderText = context.getString(R.string.reminder_text_remove) + broadcast.getProgram().getTitle() + "?";
		} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(broadcast.getProgram().getProgramType())) {
			reminderText = context.getString(R.string.reminder_text_remove) + broadcast.getProgram().getTitle() + "?";
		}

		final Dialog dialog = new Dialog(context, R.style.remove_notification_dialog);
		dialog.setContentView(R.layout.dialog_remove_notification);
		dialog.setCancelable(false);

		Button noButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_no);
		Button yesButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_yes);

		TextView textView = (TextView) dialog.findViewById(R.id.dialog_remove_notification_tv);
		textView.setText(reminderText);

		noButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// do not remove the reminder
				dialog.dismiss();
			}
		});

		yesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// remove the reminder
				NotificationService.removeNotification(context, notificationId);

				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
