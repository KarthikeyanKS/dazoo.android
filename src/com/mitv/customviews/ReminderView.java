
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
import com.millicom.mitv.utilities.DialogHelper;
import com.millicom.mitv.utilities.ToastHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.notification.NotificationDataSource;
import com.mitv.notification.NotificationSQLElement;
import com.mitv.notification.NotificationService;
import com.mitv.utilities.AnimationUtilities;



public class ReminderView 
	extends RelativeLayout 
	implements OnClickListener 
{
	private static final String TAG = ReminderView.class.toString();

	
	private LayoutInflater inflater;
	private ImageView imageView;
	private Activity activity;
	private Context context;
	private TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo;
	private int notificationId;
	private NotificationDataSource notificationDataSource;
	private boolean isSet;

	
	
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

	
	
	private void setup(Context context) 
	{
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.element_reminder_view, this);
		
		this.imageView = (ImageView) this.findViewById(R.id.element_reminder_image_View);
		this.context = context;
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
			NotificationSQLElement dbItem = notificationDataSource.getNotification(tvBroadcastWithChannelInfo.getChannel().getChannelId().getChannelId(), tvBroadcastWithChannelInfo.getBeginTime());

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
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_selected));
			} 
			else 
			{
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_default));
			}
		} 
		else 
		{
			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_dissabled));
		}
	}
	
	

	@Override
	public void onClick(View v) 
	{
		if(tvBroadcastWithChannelInfo != null && !tvBroadcastWithChannelInfo.isBroadcastAiringInOrInLessThan(Consts.MAXIMUM_REMINDER_TIME_FOR_SHOW)) 
		{
			if (isSet == false) 
			{
				NotificationService.setAlarm(context, tvBroadcastWithChannelInfo);
				
				ToastHelper.showNotificationWasSetToast(activity);
				
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_selected));

				NotificationSQLElement dbItemRemind = notificationDataSource.getNotification(tvBroadcastWithChannelInfo.getChannel().getChannelId().getChannelId(), tvBroadcastWithChannelInfo.getBeginTime());
				
				notificationId = dbItemRemind.getNotificationId();

				AnimationUtilities.animationSet(this);

				isSet = true;
			} 
			else 
			{
				if (notificationId != -1)
				{
//					if (NotificationService.sToast != null)
//					{
//						NotificationService.sToast.cancel();
//					}
					
					DialogHelper.showRemoveNotificationDialog(context, tvBroadcastWithChannelInfo, notificationId, yesNotificationProc(), noNotificationProc());
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
				ReminderView.this.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_default));
				
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