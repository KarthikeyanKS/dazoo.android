package com.millicom.secondscreen.content;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.utilities.DateUtilities;
import com.millicom.secondscreen.utilities.ImageLoader;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.notification.DazooNotification;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.share.ShareAction;

public class SSSocialInteractionPanelCreator {

	private static final String	TAG		= "SSSocialInteractionPanelCreator";

	private Activity			mActivity;
	private ImageLoader			mImageLoader;
	private LinearLayout		mContainerView;
	private Broadcast			mBroadcast;
	private Channel				mChannel;

	private boolean				isSet	= false;
	private int					notificationId;

	public SSSocialInteractionPanelCreator(Activity activity, LinearLayout containerView, Broadcast broadcast, Channel channel) {
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
		this.mContainerView = containerView;
		this.mBroadcast = broadcast;
		this.mChannel = channel;
	}

	public void createPanel() {
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_social_panel, null);
		ImageView likeButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_like_button_iv);
		TextView likeButtonTv = (TextView) contentView.findViewById(R.id.block_social_panel_like_button_tv);
		ImageView shareButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_share_button_iv);
		final ImageView remindButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_remind_button_iv);

		shareButtonIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the share Action
				ShareAction.shareAction(mActivity, "subject", "link to share", mActivity.getResources().getString(R.string.share_action_title));
			}
		});

		remindButtonIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSet == false) {
					// add reminder
					isSet = true;
					remindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
					if (NotificationService.setAlarm(mActivity, mBroadcast, mChannel)) {
						showSetNotificationToast();
					} else {
						Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
					}
				} else {
					// remove reminder
					isSet = false;

					// get notificationId from the database
					int notificationId = 0;

					remindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
					showRemoveNotificationDialog(notificationId);
				}
			}
		});

		mContainerView.addView(contentView);
	}

	private void showSetNotificationToast() {
		LayoutInflater inflater = mActivity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.layout_notification_set_toast, (ViewGroup) mActivity.findViewById(R.id.notification_set_toast_container));

		final Toast toast = new Toast(mActivity.getApplicationContext());

		TextView text = (TextView) layout.findViewById(R.id.notification_set_toast_tv);
		text.setText(mActivity.getResources().getString(R.string.reminder_text_set));

		toast.setGravity(Gravity.BOTTOM, 0, 100);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	private void showRemoveNotificationDialog(final int notificationId) {
		String reminderText = "";
		if (Consts.DAZOO_PROGRAM_TYPE_TV_EPISODE.equals(mBroadcast.getProgram().getProgramType())) {
			reminderText = mActivity.getString(R.string.reminder_text_remove) + mBroadcast.getProgram().getTitle() + ", " + mActivity.getString(R.string.season) + " "
					+ mBroadcast.getProgram().getSeason().getNumber() + ", " + mActivity.getString(R.string.episode) + " " + mBroadcast.getProgram().getEpisode() + "?";
		} else if (Consts.DAZOO_PROGRAM_TYPE_MOVIE.equals(mBroadcast.getProgram().getProgramType())) {
			reminderText = mActivity.getString(R.string.reminder_text_remove) + mBroadcast.getProgram().getTitle() + "?";
		} else if (Consts.DAZOO_PROGRAM_TYPE_OTHER.equals(mBroadcast.getProgram().getProgramType())) {
			reminderText = mActivity.getString(R.string.reminder_text_remove) + mBroadcast.getProgram().getTitle() + "?";
		}

		final Dialog dialog = new Dialog(mActivity, R.style.remove_notification_dialog);
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

				NotificationService.removeNotification(mActivity, notificationId);

				dialog.dismiss();
			}
		});

		dialog.show();
	}
}
