package com.mitv.notification;

import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.gson.TVProgram;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.model.NotificationDbItem;

public class NotificationService
{
	private static final String	TAG	= NotificationService.class.getName();
	
	public static Toast sToast;

	
	public static void setAlarm(Context context, TVBroadcastWithChannelInfo broadcast, int notificationId)
	{
		// Call alarm manager to set the notification at the certain time
		Intent intent = getAlarmIntent(notificationId, broadcast);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);

		Calendar calendar = broadcast.getBroadcastBeginTimeForNotification();

		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}
	
	
	private static Intent getAlarmIntent(int notificationId, TVBroadcastWithChannelInfo broadcast) 
	{
		Intent intent = new Intent(Consts.INTENT_NOTIFICATION);

		intent.putExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_BEGINTIMEMILLIS, broadcast.getBeginTimeMillis());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_CHANNELID, broadcast.getChannel().getChannelId().getChannelId());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_NOTIFICIATION_ID, notificationId);
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_CHANNEL_NAME, broadcast.getChannel().getName());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_CHANNEL_LOGO_URL, broadcast.getChannel().getImageUrl());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_NAME, broadcast.getProgram().getTitle());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_BROADCAST_HOUR_AND_MINUTE_TIME, broadcast.getBeginTimeHourAndMinuteCompositionAsString());
		intent.putExtra(Consts.INTENT_ALARM_EXTRA_DATE_DATE, broadcast.getBeginTimeDateRepresentation());
		
		return intent;
	}

	
	
	public static void setAlarm(Context context, TVBroadcastWithChannelInfo broadcast) 
	{
		Random random = new Random();
		int notificationId = random.nextInt(Integer.MAX_VALUE);
		Log.d(TAG,"NOTIFICATION ID: " + notificationId);

		// Call alarm manager to set the notification at the certain time
		Intent intent = getAlarmIntent(notificationId, broadcast);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, 0);

		Calendar calendar = broadcast.getBroadcastBeginTimeForNotification();

		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

		NotificationDataSource notificationDataSource = new NotificationDataSource(context);

		NotificationDbItem dbNotification = new NotificationDbItem();
		dbNotification.setNotificationId(notificationId);
		dbNotification.setBroadcstUrl(Consts.URL_NOTIFY_BROADCAST_PREFIX + broadcast.getChannel().getChannelId() + Consts.NOTIFY_BROADCAST_URL_MIDDLE + broadcast.getBeginTimeMillis());
		dbNotification.setProgramId(broadcast.getProgram().getProgramId());

		TVProgram program = broadcast.getProgram();
		String titleString = null;

		if(program.getSeries() != null)
		{
			titleString = program.getSeries().getName();
		} 
		else 
		{
			titleString = program.getTitle();
		}

		dbNotification.setProgramTitle(titleString);

		String programType = broadcast.getProgram().getProgramType().name();

		if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(programType)) 
		{
			dbNotification.setProgramType(programType);
			dbNotification.setProgramSeason(broadcast.getProgram().getSeason().getNumber().toString());
			dbNotification.setProgramEpisodeNumber(broadcast.getProgram().getEpisodeNumber());
		} 
		else if (Consts.PROGRAM_TYPE_MOVIE.equals(programType))
		{
			dbNotification.setProgramType(programType);
			dbNotification.setProgramYear(broadcast.getProgram().getYear());
			dbNotification.setProgramGenre(broadcast.getProgram().getGenre());
		} 
		else if (Consts.PROGRAM_TYPE_OTHER.equals(programType))
		{
			dbNotification.setProgramType(programType);
			dbNotification.setProgramCategory(broadcast.getProgram().getCategory());
		} 
		else if (Consts.PROGRAM_TYPE_SPORT.equals(programType)) 
		{
			dbNotification.setProgramType(programType);
			dbNotification.setProgramCategory(broadcast.getProgram().getSportType().getName()); //Use category for sport type name 
			dbNotification.setProgramGenre(broadcast.getProgram().getTournament()); //And genre for tournament name
		}

		dbNotification.setChannelName(broadcast.getChannel().getName());
		dbNotification.setChannelId(broadcast.getChannel().getChannelId().getChannelId());
		dbNotification.setChannelLogoUrl(broadcast.getChannel().getImageUrl());
		//			dbNotification.setBroadcastBeginTimeStringLocal(broadcast.getBeginTimeStringGmt());
		dbNotification.setBroadcastBeginTimeMillisGmtAsString(String.valueOf(broadcast.getBeginTimeMillis()));

		notificationDataSource.addNotification(dbNotification);
	}

	public static boolean showNotification (Context context, long broadcastBeginTimeMillis, String broadcastHourAndMinuteRepresentation, String broadcastName, String channelId, String channelName, String channelLogoUrl, 
			String dateDate, int notificationId){

		String broadcastUrl = Consts.URL_NOTIFY_BROADCAST_PREFIX + channelId + Consts.NOTIFY_BROADCAST_URL_MIDDLE + broadcastBeginTimeMillis;
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent broadcastPageIntent = new Intent(context, BroadcastPageActivity.class);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_BROADCAST_BEGINTIMEINMILLIS, broadcastBeginTimeMillis);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, channelId);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_CHANNEL_CHOSEN_DATE, dateDate);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_CHANNEL_LOGO_URL, channelLogoUrl);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_BROADCAST_URL, broadcastUrl);
		broadcastPageIntent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);
		broadcastPageIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.mitv_notification_large_icon);


		NotificationCompat.Builder notificationBuilder;
		
			notificationBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.mitv_notification_small_icon)
			.setLargeIcon(largeIcon)
			.setContentTitle(broadcastName)
			.setContentText(broadcastHourAndMinuteRepresentation + " " + channelName)
			.setContentIntent(PendingIntent.getActivity(context, 1, broadcastPageIntent, PendingIntent.FLAG_UPDATE_CURRENT))
			.setAutoCancel(true)
			.setWhen(System.currentTimeMillis())
			.setDefaults(Notification.DEFAULT_ALL); // default sound, vibration, light
			notificationManager.notify(notificationId, notificationBuilder.build());
		

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
		Intent intent = new Intent(Consts.INTENT_NOTIFICATION);
		PendingIntent sender = PendingIntent.getBroadcast(context, notificationId, intent, 0);
		alarmManager.cancel(sender);

		// remove notification from database
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		notificationDataSource.deleteNotification(notificationId);

		return true;
	}

	public static Toast showSetNotificationToast(Activity activity) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_notification_and_like_set, (ViewGroup) activity.findViewById(R.id.notification_and_like_set_toast_container));

		sToast = new Toast(activity.getApplicationContext());

		String toastText = String.format("%s %s %s %s %s", 
				activity.getResources().getString(R.string.reminder_text_set_top), 
				"<b>", 
				activity.getResources().getString(R.string.reminder_text_set_middle), 
				"</b>", 
				activity.getResources().getString(R.string.reminder_text_set_bottom));

		TextView text = (TextView) layout.findViewById(R.id.notification_and_like_set_toast_tv);
		text.setText(Html.fromHtml(toastText));

		if (android.os.Build.VERSION.SDK_INT >= 13) {
			sToast.setGravity(Gravity.BOTTOM, 0, ((int) activity.getResources().getDimension(R.dimen.bottom_tabs_height) + 10)); //200
		} 
		else {
			sToast.setGravity(Gravity.BOTTOM, 0, ((int) activity.getResources().getDimension(R.dimen.bottom_tabs_height) + 10)); //100
		}
		sToast.setDuration(Toast.LENGTH_SHORT);
		sToast.setView(layout);
		sToast.show();
		return sToast;
	}

	public static void showRemoveNotificationDialog(final Context context, TVBroadcast broadcast, final int notificationId) {
		String reminderText = "";
		if (Consts.PROGRAM_TYPE_TV_EPISODE.equals(broadcast.getProgram().getProgramType())) {
			reminderText = context.getString(R.string.reminder_text_remove) + broadcast.getProgram().getTitle() + ", " + context.getString(R.string.season) + " "
					+ broadcast.getProgram().getSeason().getNumber() + ", " + context.getString(R.string.episode) + " " + broadcast.getProgram().getEpisodeNumber() + "?";
		} else if (Consts.PROGRAM_TYPE_MOVIE.equals(broadcast.getProgram().getProgramType())) {
			reminderText = context.getString(R.string.reminder_text_remove) + broadcast.getProgram().getTitle() + "?";
		} else if (Consts.PROGRAM_TYPE_OTHER.equals(broadcast.getProgram().getProgramType())) {
			reminderText = context.getString(R.string.reminder_text_remove) + broadcast.getProgram().getTitle() + "?";
		} else if (Consts.PROGRAM_TYPE_SPORT.equals(broadcast.getProgram().getProgramType())) {
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
