package com.mitv.customviews;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mitv.R;
import com.mitv.model.Broadcast;
import com.mitv.model.NotificationDbItem;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationDialogHandler;
import com.mitv.notification.NotificationService;
import com.mitv.utilities.AnimationUtilities;

public class ReminderView extends RelativeLayout implements OnClickListener {

	private static final String TAG = ReminderView.class.toString();

	private boolean mIsSet;
	private Broadcast mBroadcast;
	private Context mContext;
	private Activity mActivity;
	private int mNotificationId;
	private NotificationDataSource mNotificationDataSource;
	private ImageView mImageView;
	private LayoutInflater mInflater;

	public ReminderView(Context context) {
		super(context);
		setup(context);
	}

	public ReminderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context);
	}

	public ReminderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(context);
	}

	private void setup(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.element_reminder_view, this);
		
		this.mImageView = (ImageView) this.findViewById(R.id.element_reminder_image_View);
		this.mContext = context;
		this.mActivity = (Activity) context;
		this.mNotificationDataSource = new NotificationDataSource(mContext);
		
		this.setClickable(true);
		this.setOnClickListener(this);
	}

	public void setBroadcast(Broadcast broadcast) {
		this.mBroadcast = broadcast;
		
		if (!mBroadcast.hasStarted()) {
			NotificationDbItem dbItem = new NotificationDbItem();

			dbItem = mNotificationDataSource.getNotification(mBroadcast.getChannel().getChannelId(), mBroadcast.getBeginTimeMillisGmt());

			if (dbItem.getNotificationId() != 0) {
				Log.d(TAG, "dbItem: " + dbItem.getProgramTitle() + " " + dbItem.getNotificationId());
				mNotificationId = dbItem.getNotificationId();
				mIsSet = true;
			} else {
				mIsSet = false;
			}
			if (mIsSet) {
				mImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			} else {
				mImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_default));
			}
		} else {
			mImageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
		}
	}

	@Override
	public void onClick(View v) {
//		if(mBroadcast != null )
		if (mIsSet == false) {
			if (NotificationService.setAlarm(mContext, mBroadcast, mBroadcast.getChannel(), mBroadcast.getTvDateString())) {
				NotificationService.showSetNotificationToast(mActivity);
				mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_reminder_selected));

				NotificationDbItem dbItemRemind = mNotificationDataSource.getNotification(mBroadcast.getChannel().getChannelId(),
						mBroadcast.getBeginTimeMillisGmt());
				mNotificationId = dbItemRemind.getNotificationId();

				AnimationUtilities.animationSet(this);

				mIsSet = true;
			} else {
				Log.d(TAG, "!!! Setting notification faced an error !!!");
			}
		} else {
			if (mNotificationId != -1) {
				if (NotificationService.sToast != null) {
					NotificationService.sToast.cancel();
				}
				NotificationDialogHandler notificationDlg = new NotificationDialogHandler();
				notificationDlg.showRemoveNotificationDialog(mContext, mBroadcast, mNotificationId, yesNotificationProc(), noNotificationProc());
			} else {
				Log.d(TAG, "!!! Could not find such reminder in DB !!!");
			}
		}

	}

	public Runnable yesNotificationProc() {
		return new Runnable() {
			public void run() {
				ReminderView.this.mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_reminder_default));
				mIsSet = false;
			}
		};
	}

	public Runnable noNotificationProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

}
