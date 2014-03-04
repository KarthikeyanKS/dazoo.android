
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

import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.sql.NotificationDataSource;
import com.millicom.mitv.models.sql.NotificationSQLElement;
import com.millicom.mitv.utilities.DialogHelper;
import com.millicom.mitv.utilities.NotificationHelper;
import com.millicom.mitv.utilities.ToastHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.utilities.AnimationUtilities;



public class ReminderView 
	extends RelativeLayout 
	implements OnClickListener 
{
	private static final String TAG = ReminderView.class.toString();

	
	private LayoutInflater inflater;
	private ImageView imageView;
	private Activity activity;
	private TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo;
	private int notificationId;
	private NotificationDataSource notificationDataSource;
	private boolean isSet;
	private View containerView;
	
	
	public ReminderView(Context context)
	{
		super(context);
		setup(context);
	}

	
	
	public ReminderView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);		
		setup(context);
	}

	
	
	public ReminderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		setup(context);
	}

	private void setup(Context context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.containerView = inflater.inflate(R.layout.element_reminder_view, this);
		
		this.imageView = (ImageView) this.findViewById(R.id.element_reminder_image_View);
		this.activity = (Activity) context;
		this.notificationDataSource = new NotificationDataSource(context);
		
		this.setClickable(true);
		this.setOnClickListener(this);
	}

	
	
	public void setBroadcast(TVBroadcastWithChannelInfo broadcast) 
	{
		this.tvBroadcastWithChannelInfo = broadcast;
		
		if (!tvBroadcastWithChannelInfo.isAiring())
		{
			NotificationSQLElement dbItem = notificationDataSource.getNotification(tvBroadcastWithChannelInfo.getChannel().getChannelId(), tvBroadcastWithChannelInfo.getBeginTime());

			if (dbItem != null && dbItem.getNotificationId() != 0) 
			{
				Log.d(TAG, "dbItem: " + dbItem.getProgramTitle() + " " + dbItem.getNotificationId());
				
				notificationId = dbItem.getNotificationId();
				
				isSet = true;
			} 
			else 
			{
				isSet = false;
			}
			if (isSet) 
			{
				imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_reminder_selected));
			} 
			else 
			{
				imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_reminder_default));
			}
			containerView.setBackgroundResource(R.drawable.background_color_selector);
		} else {
			imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
			containerView.setBackgroundColor(getResources().getColor(R.color.transparent));
		}
	}
	
	

	@Override
	public void onClick(View v) 
	{
		if(tvBroadcastWithChannelInfo != null && !tvBroadcastWithChannelInfo.isBroadcastAiringInOrInLessThan(Consts.MAXIMUM_REMINDER_TIME_FOR_SHOW)) 
		{
			if (isSet == false) 
			{
				NotificationHelper.setAlarm(activity, tvBroadcastWithChannelInfo);
				
				ToastHelper.showNotificationWasSetToast(activity);
				
				imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_reminder_selected));

				NotificationSQLElement dbItemRemind = notificationDataSource.getNotification(tvBroadcastWithChannelInfo.getChannel().getChannelId(), tvBroadcastWithChannelInfo.getBeginTime());
				
				notificationId = dbItemRemind.getNotificationId();

				AnimationUtilities.animationSet(this);

				isSet = true;
			} 
			else 
			{
				if (notificationId != -1)
				{
					DialogHelper.showRemoveNotificationDialog(activity, tvBroadcastWithChannelInfo, notificationId, yesNotificationProc(), noNotificationProc());
				}
				else
				{
					Log.w(TAG, "Could not find remainder in database.");
				}
			}
		}
	}

	
	
	public Runnable yesNotificationProc() 
	{
		return new Runnable()
		{
			public void run()
			{
				ReminderView.this.imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_reminder_default));
				
				isSet = false;
			}
		};
	}

	
	
	public Runnable noNotificationProc()
	{
		return new Runnable()
		{
			public void run(){}
		};
	}
}