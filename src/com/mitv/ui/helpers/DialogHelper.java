
package com.mitv.ui.helpers;



import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.SplashScreenActivity;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.TVBroadcast;



public class DialogHelper 
{
	@SuppressWarnings("unused")
	private static final String	TAG	= DialogHelper.class.getName();
		
	

	public static void showRemoveNotificationDialog(
			final Activity activity, 
			final TVBroadcast broadcast, 
			final int notificationId, 
			final Runnable aProcedure, 
			final Runnable bProcedure)
	{
		final Runnable answerYes = aProcedure;
		final Runnable answerNo = bProcedure;
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		StringBuilder reminderSB = new StringBuilder();
		
		ProgramTypeEnum programType = broadcast.getProgram().getProgramType();
		
		switch (programType) 
		{
			case TV_EPISODE:
			{
				reminderSB.append(activity.getString(R.string.reminder_text_remove));
				reminderSB.append(broadcast.getProgram().getSeries().getName());
				reminderSB.append(", ");
				reminderSB.append(activity.getString(R.string.season));
				reminderSB.append(" ");
				reminderSB.append(broadcast.getProgram().getSeason().getNumber());
				reminderSB.append(", ");
				reminderSB.append(activity.getString(R.string.episode));
				reminderSB.append(" ");
				reminderSB.append(broadcast.getProgram().getEpisodeNumber());
				reminderSB.append("?");
				break;
			}
			
			case MOVIE:
			{
				reminderSB.append(activity.getString(R.string.reminder_text_remove));
				reminderSB.append(broadcast.getProgram().getTitle());
				reminderSB.append("?");
				break;
			}
			
			case SPORT:
			{
				reminderSB.append(activity.getString(R.string.reminder_text_remove));
				reminderSB.append(broadcast.getProgram().getTitle());
				reminderSB.append("?");
				break;
			}
			
			case OTHER:
			{
				reminderSB.append(activity.getString(R.string.reminder_text_remove));
				reminderSB.append(broadcast.getProgram().getTitle());
				reminderSB.append("?");
				break;
			}
			
			case UNKNOWN:
			default:
			{
				reminderSB.append(activity.getString(R.string.reminder_text_remove));
				break;
			}
		}
		
		dialog.setContentView(R.layout.dialog_remove_notification);
		dialog.setCancelable(false);

		Button noButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_no);
		Button yesButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_yes);

		TextView textView = (TextView) dialog.findViewById(R.id.dialog_remove_notification_tv);
		textView.setText(reminderSB.toString());

		noButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(answerNo != null)
				{
					answerNo.run();
				}
				
				dialog.dismiss();
			}
		});

		yesButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(answerYes != null)
				{
					answerYes.run();
				}
				
				NotificationHelper.removeNotification(activity, notificationId);
				dialog.dismiss();
			}
		});
		
		if(!activity.isFinishing()){
			 dialog.show();
		}
	}
	

	
	public static void showRemoveLikeDialog(
			final Activity activity,
			final Runnable aProcedure,
			final Runnable bProcedure)
	{
		final Runnable answerYes = aProcedure;
		final Runnable answerNo = bProcedure;
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_remove_notification);
		dialog.setCancelable(false);

		Button noButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_no);
		Button yesButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_yes);

		TextView textView = (TextView) dialog.findViewById(R.id.dialog_remove_notification_tv);
		textView.setText(activity.getResources().getString(R.string.like_remove_text));

		noButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(answerNo != null)
				{
					answerNo.run();
				}
				
				dialog.dismiss();
			}
		});

		yesButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(answerYes != null)
				{
					answerYes.run();
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
			final Runnable aProcedure, 
			final Runnable bProcedure) 
	{
		final Runnable answerYes = aProcedure;
		final Runnable answerNo	= bProcedure;
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_prompt_signin);
		dialog.setCancelable(false);
		
		Button cancelButton = (Button) dialog.findViewById(R.id.dialog_prompt_signin_button_cancel);
		Button signInButton = (Button) dialog.findViewById(R.id.dialog_prompt_signin_button_signin);
	
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(answerNo != null)
				{
					answerNo.run();
				}
				
				dialog.dismiss();
			}
		});

		signInButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(answerYes != null)
				{
					answerYes.run();
				}
				
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
		dialog.setCancelable(false);

		Button okButton = (Button) dialog.findViewById(R.id.dialog_prompt_update_button);
		
		okButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				final String appPackageName = activity.getPackageName(); 
				
				try 
				{
					Uri uri = Uri.parse("market://details?id=" + appPackageName);
					
					activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				} 
				catch (android.content.ActivityNotFoundException anfe) 
				{
					Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName);
					
					activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				}
			}
		});
		
		if(!activity.isFinishing())
		{
			 dialog.show();
		}
	}
	
	
	
	public static void showMandatoryFirstTimeInternetConnection(final SplashScreenActivity activity) 
	{
		final Dialog dialog = new Dialog(activity, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_prompt_no_connection);
		dialog.setCancelable(false);

		final Button retryButton = (Button) dialog.findViewById(R.id.dialog_prompt_retry_button);
		
		retryButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				ContentManager.sharedInstance().fetchFromServiceInitialCall(activity, activity);
				
				retryButton.setEnabled(false);
				
				dialog.dismiss();
			}
		});
		
		if(!activity.isFinishing())
		{
			 dialog.show();
		}
	}
}