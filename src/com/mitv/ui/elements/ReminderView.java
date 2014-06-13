
package com.mitv.ui.elements;



import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.mitv.Constants;
import com.mitv.R;
import com.mitv.managers.ContentManager;
import com.mitv.managers.RateAppManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.Notification;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Event;
import com.mitv.models.objects.mitvapi.competitions.EventBroadcast;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.NotificationHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.AnimationUtils;



public class ReminderView 
	extends RelativeLayout 
	implements OnClickListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = ReminderView.class.toString();

	
	private LayoutInflater inflater;
	private View containerView;
	private FontTextView iconView;
	private Activity activity;
	
	private Notification notification;
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
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.containerView = inflater.inflate(R.layout.element_reminder_view, this);
		
		this.iconView = (FontTextView) findViewById(R.id.element_reminder_image_View);
		
		this.activity = (Activity) context;
		
		this.notification = null;
		
		this.isSet = false;
	}
	
	
	
	public void setBroadcast(final TVBroadcastWithChannelInfo broadcast) 
	{
		if (broadcast != null && 
			broadcast.isAiring() == false &&
			broadcast.isEventAiringInLessThan(Constants.NOTIFY_MINUTES_BEFORE_THE_BROADCAST) == false)
		{
			String channelId = broadcast.getChannel().getChannelId().getChannelId();
			String programId = broadcast.getProgram().getProgramId();
			long beginTimeInMilliseconds = broadcast.getBeginTimeMillis();
			
			Notification notification = ContentManager.sharedInstance().getNotificationWithParameters(channelId, programId, beginTimeInMilliseconds);

			if (notification != null) 
			{
				this.notification = notification;
				this.isSet = true;
			}
			else 
			{
				this.notification = new Notification(broadcast);
				this.isSet = false;
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
			
			containerView.setClickable(true);
			containerView.setOnClickListener(this);
		} 
		else 
		{
			iconView.setTextColor(getResources().getColor(R.color.grey1));
			containerView.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			containerView.setClickable(true);
		}
	}
	
	
	
	public void setCompetitionEventBroadcast(
			final Competition competition,
			final Event event, 
			final EventBroadcast eventBroadcast, 
			final String channelName,
			final String channelURL) 
	{
		if (eventBroadcast != null && 
			eventBroadcast.isAiring() == false &&
			eventBroadcast.isEventAiringInLessThan(Constants.NOTIFY_MINUTES_BEFORE_THE_BROADCAST) == false)
		{
			String channelId = eventBroadcast.getChannelId();
			String programId = eventBroadcast.getProgramId();
			Long beginTimeInMilliseconds = eventBroadcast.getBeginTimeLocalInMillis();
			Long competitionId = event.getCompetitionId();
			Long eventId = event.getEventId();
			
			Notification notificationFromCache = ContentManager.sharedInstance().getNotificationWithParameters(channelId, programId, beginTimeInMilliseconds, competitionId, eventId);
			
			if (notificationFromCache != null)
			{	
				notification = notificationFromCache;
				isSet = true;
			} 
			else 
			{
				notification = new Notification(competition, event, eventBroadcast, channelName, channelURL);
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
			
			containerView.setClickable(true);
			containerView.setOnClickListener(this);
		} 
		else 
		{
			iconView.setTextColor(getResources().getColor(R.color.grey1));
			containerView.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			containerView.setClickable(true);
		}
	}
	
	
	
	
	public void setCompetitionEventBroadcast(
			final Competition competition,
			final Event event, 
			final EventBroadcast eventBroadcast, 
			final TVChannel channel) 
	{
		if (eventBroadcast != null && 
			eventBroadcast.isAiring() == false &&
			eventBroadcast.isEventAiringInLessThan(Constants.NOTIFY_MINUTES_BEFORE_THE_BROADCAST) == false)
		{
			String channelId = eventBroadcast.getChannelId();
			String programId = eventBroadcast.getProgramId();
			Long beginTimeInMilliseconds = eventBroadcast.getBeginTimeLocalInMillis();
			Long competitionId = event.getCompetitionId();
			Long eventId = event.getEventId();
			
			Notification notificationFromCache = ContentManager.sharedInstance().getNotificationWithParameters(channelId, programId, beginTimeInMilliseconds, competitionId, eventId);
			
			if (notificationFromCache != null)
			{	
				notification = notificationFromCache;
				isSet = true;
			} 
			else 
			{
				notification = new Notification(competition, event, eventBroadcast, channel);
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
			
			containerView.setClickable(true);
			containerView.setOnClickListener(this);
		} 
		else 
		{
			iconView.setTextColor(getResources().getColor(R.color.grey1));
			containerView.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			containerView.setClickable(true);
		}
	}
	
	
	
	public void setSizeOfIcon(final boolean useSmallSize) 
	{
		int size;
		
		if(useSmallSize) 
		{
			size = 28; /* The size is in sp */
		} 
		else 
		{
			size = 30; /* The is the default size is in sp */
		}
		
		iconView.setTextSize(size);
	}

	
	
	@Override
	public void onClick(View v) 
	{
		RateAppManager.significantEvent(activity);
		
		if (isSet == false) 
		{
			TrackingGAManager.sharedInstance().sendUserReminderEvent(notification, false);
						
			NotificationHelper.scheduleNotification(activity, notification);
			
			StringBuilder sb = new StringBuilder();
			sb.append(activity.getString(R.string.reminder_text_set_top));
			sb.append(" <b> ");
			sb.append(activity.getString(R.string.reminder_text_set_middle));
			sb.append(" </b> ");
			sb.append(activity.getString(R.string.reminder_text_set_bottom));
			
			Spanned spanned = Html.fromHtml(sb.toString());
			
			ToastHelper.createAndShowShortToast(spanned.toString());

			iconView.setTextColor(getResources().getColor(R.color.blue1));

			AnimationUtils.animationSet(this);

			isSet = true;
		} 
		else 
		{
			DialogHelper.showRemoveNotificationDialog(activity, notification, removeNotificationProcedure(), null);
		}
	}

	
	
	public Runnable removeNotificationProcedure() 
	{
		return new Runnable()
		{
			public void run()
			{
				TrackingGAManager.sharedInstance().sendUserReminderEvent(notification, true);
				
				iconView.setTextColor(getResources().getColor(R.color.grey3));
				
				isSet = false;
			}
		};
	}
}