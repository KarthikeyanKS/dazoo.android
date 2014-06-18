
package com.mitv.ui.helpers;



import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.SignUpSelectionActivity;
import com.mitv.enums.NotificationTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.Notification;
import com.mitv.ui.elements.FontTextView;
import com.mitv.utilities.GenericUtils;



public class DialogHelper 
{
	private static final String	TAG	= DialogHelper.class.getName();		
	

	
	public static void showDialog(
			final Activity activity,
			final String title,
			final String message,
			final String confirmButtonText,
			final String cancelButtonText,
			final Runnable confirmProcedure, 
			final Runnable cancelProcedure) 
	{
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_prompt_generic);

		FontTextView titleTextview = (FontTextView) dialog.findViewById(R.id.dialog_title);
		titleTextview.setText(title);
		
		FontTextView messageTextview = (FontTextView) dialog.findViewById(R.id.dialog_message);
		messageTextview.setText(message);
		
		Button confirmButton = (Button) dialog.findViewById(R.id.dialog_button_confirm);
		confirmButton.setText(confirmButtonText);
		
		confirmButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(confirmProcedure != null)
				{
					confirmProcedure.run();
				}
				
				dialog.dismiss();
			}
		});
		
		Button cancelButton = (Button) dialog.findViewById(R.id.dialog_button_cancel);
		cancelButton.setText(cancelButtonText);
		
		cancelButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(cancelProcedure != null)
				{
					cancelProcedure.run();
				}
				
				dialog.dismiss();
			}
		});
		
		if(GenericUtils.isActivityNotNullAndNotFinishingAndNotDestroyed(activity))
		{
			 dialog.show();
		}
		else
		{
			Log.w(TAG, "View is finishing. Dialog will not be shown");
		}
	}
		

	
	public static void showRemoveNotificationDialog(
			final Activity activity,
			final Notification notification, 
			final Runnable yesProcedure, 
			final Runnable noProcedure)
	{
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		StringBuilder reminderSB = new StringBuilder();
		
		NotificationTypeEnum notificationType = notification.getNotificationType();
		
		switch (notificationType)
		{
			case TV_BROADCAST:
			{
				ProgramTypeEnum programType = notification.getBroadcastProgramType();

				switch (programType)
				{
					case TV_EPISODE:
					{
						reminderSB.append(activity.getString(R.string.reminder_text_remove));
						
						reminderSB.append(" ");
						reminderSB.append(notification.getBroadcastTitle());
						reminderSB.append(", ");
						reminderSB.append(notification.getBroadcastProgramDetails());
						reminderSB.append("?");
						break;
					}
					
					case MOVIE:
					case SPORT:
					case OTHER:
					{
						reminderSB.append(activity.getString(R.string.reminder_text_remove));
						reminderSB.append(" ");
						reminderSB.append(notification.getBroadcastTitle());
						reminderSB.append("?");
						break;
					}
					
					case UNKNOWN:
					default:
					{
						reminderSB.append(activity.getString(R.string.reminder_text_remove));
						reminderSB.append(" ");
						break;
					}
				}
				
				break;
			}
			
			case COMPETITION_EVENT_WITH_EMBEDED_CHANNEL:
			case COMPETITION_EVENT_WITH_LOCAL_CHANNEL:
			{
				reminderSB.append(activity.getString(R.string.reminder_text_remove));
				reminderSB.append(" ");
				reminderSB.append(notification.getBroadcastTitle());
				reminderSB.append("?");
				
				break;
			}
			
			default:
			{
				reminderSB.append(activity.getString(R.string.reminder_text_remove));
				reminderSB.append(" ");
				break;
			}
		}
		
		dialog.setContentView(R.layout.dialog_remove_notification);

		Button noButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_no);
		Button yesButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_yes);

		TextView textView = (TextView) dialog.findViewById(R.id.dialog_remove_notification_tv);
		textView.setText(reminderSB.toString());

		noButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(noProcedure != null)
				{
					noProcedure.run();
				}
				
				dialog.dismiss();
			}
		});

		yesButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(yesProcedure != null)
				{
					yesProcedure.run();
				}
				
				int notificationId = notification.getNotificationId();
				
				NotificationHelper.removeNotification(activity, notificationId);
				
				dialog.dismiss();
			}
		});
		
		if(!activity.isFinishing())
		{
			 dialog.show();
		}
	}
	

	
	public static void showRemoveLikeDialog(
			final Activity activity,
			final Runnable yesProcedure,
			final Runnable noProcedure)
	{
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_remove_notification);

		Button noButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_no);
		Button yesButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_yes);

		TextView textView = (TextView) dialog.findViewById(R.id.dialog_remove_notification_tv);
		textView.setText(activity.getString(R.string.like_remove_text));

		noButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(noProcedure != null)
				{
					noProcedure.run();
				}
				
				dialog.dismiss();
			}
		});

		yesButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(yesProcedure != null)
				{
					yesProcedure.run();
				}
				
				dialog.dismiss();
			}
		});
		
		if(!activity.isFinishing())
		{
			 dialog.show();
		}
	}
	
	
	
	public static void showPromptSignInDialog(
			final Activity activity,
			final String message,
			final Runnable yesProcedure,
			final Runnable noProcedure) 
	{
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_prompt_signin);
		
		TextView dialogMessageText = (TextView) dialog.findViewById(R.id.dialog_prompt_signin_tv);
		
		dialogMessageText.setText(message);
		
		Button cancelButton = (Button) dialog.findViewById(R.id.dialog_prompt_signin_button_cancel);
		Button signInButton = (Button) dialog.findViewById(R.id.dialog_prompt_signin_button_signin);
	
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(noProcedure != null)
				{
					noProcedure.run();
				}
				
				dialog.dismiss();
			}
		});

		signInButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(yesProcedure != null)
				{
					yesProcedure.run();
				}
				
				dialog.dismiss();
			}
		});
		
		if(!activity.isFinishing())
		{
			 dialog.show();
		}
	}
	
	
	
	public static void showPromptTokenExpiredDialog(final Activity activity) 
	{
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_prompt_token_expired);

		Button cancelButton = (Button) dialog.findViewById(R.id.dialog_prompt_signin_button_cancel);
		Button signInButton = (Button) dialog.findViewById(R.id.dialog_prompt_signin_button_signin);
	
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Do nothing
			}
		});

		signInButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(activity, SignUpSelectionActivity.class);	
				
				activity.startActivity(intent);

				dialog.dismiss();
			}
		});
		
		if(!activity.isFinishing())
		{
			 dialog.show();
		}
	}
	
	
	
	public static void showMandatoryAppUpdateDialog(final Activity activity) 
	{
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_prompt_update);

		Button okButton = (Button) dialog.findViewById(R.id.dialog_prompt_update_button);
		
		okButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				final String appPackageName = activity.getPackageName(); 
				
				Uri uri;
				
				try
				{
					String marketUrl = activity.getString(R.string.market_url_for_google_play_market_details);
					
					uri = Uri.parse(marketUrl + appPackageName);
				} 
				catch (android.content.ActivityNotFoundException anfe) 
				{
					String marketUrl = activity.getString(R.string.http_url_for_google_play_market_details);
					
					uri = Uri.parse(marketUrl + appPackageName);
				}
				
				activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});
		
		if(!activity.isFinishing())
		{
			 dialog.show();
		}
	}
}