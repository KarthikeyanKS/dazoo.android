
package com.mitv.ui.elements;



import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.sql.NotificationDataSource;
import com.mitv.models.sql.NotificationSQLElement;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.NotificationHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.AnimationUtils;



public class ReminderView 
	extends RelativeLayout 
	implements OnClickListener 
{
	private static final String TAG = ReminderView.class.toString();

	
	private LayoutInflater inflater;
	private FontTextView iconView;
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

	
	private void setup(Context context) 
	{
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.containerView = inflater.inflate(R.layout.element_reminder_view, this);
		
		this.iconView = (FontTextView) this.findViewById(R.id.element_reminder_image_View);
		
		this.activity = (Activity) context;
		
		this.notificationDataSource = new NotificationDataSource(context);
	}
	
	
	public void setBroadcast(TVBroadcastWithChannelInfo broadcast) 
	{
		this.tvBroadcastWithChannelInfo = broadcast;
		
		if (tvBroadcastWithChannelInfo != null && 
			tvBroadcastWithChannelInfo.isAiring() == false &&
			tvBroadcastWithChannelInfo.isBroadcastAiringInOrInLessThan(Constants.MAXIMUM_REMINDER_TIME_FOR_SHOW) == false)
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
				iconView.setTextColor(getResources().getColor(R.color.blue1));
			} 
			else 
			{
				iconView.setTextColor(getResources().getColor(R.color.grey3));
			}
			
			containerView.setBackgroundResource(R.drawable.background_color_selector);
			
			this.setClickable(true);
			this.setOnClickListener(this);
		} 
		else 
		{
			iconView.setTextColor(getResources().getColor(R.color.grey1));
			containerView.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			this.setClickable(false);
		}
	}
	
	

	@Override
	public void onClick(View v) 
	{
		if (isSet == false) 
		{
			NotificationHelper.scheduleAlarm(activity, tvBroadcastWithChannelInfo);

			ToastHelper.showNotificationWasSetToast(activity);

			iconView.setTextColor(getResources().getColor(R.color.blue1));

			NotificationSQLElement dbItemRemind = notificationDataSource.getNotification(tvBroadcastWithChannelInfo.getChannel().getChannelId(), tvBroadcastWithChannelInfo.getBeginTime());

			notificationId = dbItemRemind.getNotificationId();

			AnimationUtils.animationSet(this);

			isSet = true;
		} 
		else 
		{
			if (notificationId != -1)
			{
				DialogHelper.showRemoveNotificationDialog(activity, tvBroadcastWithChannelInfo, notificationId, yesNotificationProcedure(), null);
			}
			else
			{
				Log.w(TAG, "Could not find remainder in database.");
			}
		}
	}

	
	
	public Runnable yesNotificationProcedure() 
	{
		return new Runnable()
		{
			public void run()
			{
				iconView.setTextColor(getResources().getColor(R.color.grey3));
				
				isSet = false;
			}
		};
	}
}