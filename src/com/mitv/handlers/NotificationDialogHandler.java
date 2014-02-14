package com.mitv.handlers;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.model.OldBroadcast;
import com.mitv.notification.NotificationService;

public class NotificationDialogHandler {
	
	private static final String TAG = "NotificationDialogHandler";
	
	public Runnable answerYes = null;
	public Runnable answerNo = null;
	
	public boolean showRemoveNotificationDialog(final Context context, OldBroadcast broadcast, final int notificationId, Runnable aProcedure, Runnable bProcedure) {
		answerYes = aProcedure;
		answerNo = bProcedure;
		
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
				answerNo.run();
				// do not remove the reminder
				dialog.dismiss();
			}
		});

		yesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// remove the reminder
				answerYes.run();
				Log.d(TAG,"notificationId: " + notificationId);
				
				NotificationService.removeNotification(context, notificationId);
				dialog.dismiss();
			}
		});
		dialog.show();
		return true;
	}
}
