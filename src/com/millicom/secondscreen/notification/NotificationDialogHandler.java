package com.millicom.secondscreen.notification;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;

public class NotificationDialogHandler {
	
	private static final String TAG = "NotificationDialogHandler";
	
	public Runnable answerYes = null;
	public Runnable answerNo = null;
	
	public boolean showRemoveNotificationDialog(final Context context, Broadcast broadcast, final int notificationId, Runnable aProcedure, Runnable bProcedure) {
		answerYes = aProcedure;
		answerNo = bProcedure;
		
		String reminderText = "";
		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(broadcast.getProgram().getProgramType())) {
			reminderText = context.getString(R.string.reminder_text_remove) + broadcast.getProgram().getTitle() + ", " + context.getString(R.string.season) + " "
					+ broadcast.getProgram().getSeason().getNumber() + ", " + context.getString(R.string.episode) + " " + broadcast.getProgram().getEpisodeNumber() + "?";
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
				NotificationService.removeNotification(context, notificationId);
				dialog.dismiss();
			}
		});
		dialog.show();
		return true;
	}
}
