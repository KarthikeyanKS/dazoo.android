package com.millicom.secondscreen.content;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.millicom.secondscreen.content.model.NotificationDbItem;
import com.millicom.secondscreen.notification.AlarmReceiver;
import com.millicom.secondscreen.notification.NotificationDataSource;
import com.millicom.secondscreen.notification.NotificationService;
import com.millicom.secondscreen.share.ShareAction;

public class SSSocialInteractionPanelCreator {

	private static final String	TAG	= "SSSocialInteractionPanelCreator";

	private Activity			mActivity;
	private ImageLoader			mImageLoader;
	private LinearLayout		mContainerView;
	private Broadcast			mBroadcast;
	private Channel				mChannel;
	private ImageView			remindButtonIv;

	private boolean				mIsSet;

	public SSSocialInteractionPanelCreator(Activity activity, LinearLayout containerView, Broadcast broadcast, Channel channel, boolean isSet) {
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage_2x);
		this.mContainerView = containerView;
		this.mBroadcast = broadcast;
		this.mChannel = channel;
		this.mIsSet = isSet;
	}

	public void createPanel() {
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_social_panel, null);
		ImageView likeButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_like_button_iv);
		TextView likeButtonTv = (TextView) contentView.findViewById(R.id.block_social_panel_like_button_tv);
		ImageView shareButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_share_button_iv);
		remindButtonIv = (ImageView) contentView.findViewById(R.id.block_social_panel_remind_button_iv);

		shareButtonIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the share Action
				ShareAction.shareAction(mActivity, "subject", "link to share", mActivity.getResources().getString(R.string.share_action_title));
			}
		});

		if (mIsSet == true) {
			remindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
		} else {
			remindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
		}

		remindButtonIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsSet == false) {
					// add reminder
					mIsSet = true;
					//remindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock_red));
					if (NotificationService.setAlarm(mActivity, mBroadcast, mChannel)) {
						NotificationService.showSetNotificationToast(mActivity);
					} else {
						Toast.makeText(mActivity, "Setting notification faced an error", Toast.LENGTH_SHORT).show();
					}
				} else {
					// remove reminder
					//mIsSet = false;

					NotificationDataSource notificationDataSource = new NotificationDataSource(mActivity);

					NotificationDbItem notificationDbItem = new NotificationDbItem();
					notificationDbItem = notificationDataSource.getNotification(mChannel.getChannelId(), Long.valueOf(mBroadcast.getBeginTimeMillis()));
					if (notificationDbItem != null) {
						
						//remindButtonIv.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_clock));
						
						NotificationService.showRemoveNotificationDialog(mActivity, mBroadcast, notificationDbItem.getNotificationId());
						
						
					} else {
						Toast.makeText(mActivity, "Could not find such reminder in DB", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		mContainerView.addView(contentView);
	}
}
