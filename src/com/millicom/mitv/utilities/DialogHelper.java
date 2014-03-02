
package com.millicom.mitv.utilities;



import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcast;
import com.mitv.R;



public class DialogHelper 
{
	@SuppressWarnings("unused")
	private static final String	TAG	= DialogHelper.class.getName();
		
	

	public static void showRemoveNotificationDialog(
			final Context context, 
			final TVBroadcast broadcast, 
			final int notificationId, 
			final Runnable aProcedure, 
			final Runnable bProcedure)
	{
		final Runnable answerYes = aProcedure;
		final Runnable answerNo = bProcedure;
		final Dialog dialog = new Dialog(context, R.style.remove_notification_dialog);
		
		StringBuilder reminderSB = new StringBuilder();
		
		ProgramTypeEnum programType = broadcast.getProgram().getProgramType();
		
		switch (programType) 
		{
			case TV_EPISODE:
			{
				reminderSB.append(context.getString(R.string.reminder_text_remove));
				reminderSB.append(broadcast.getProgram().getTitle());
				reminderSB.append(", ");
				reminderSB.append(context.getString(R.string.season));
				reminderSB.append(" ");
				reminderSB.append(broadcast.getProgram().getSeason().getNumber());
				reminderSB.append(", ");
				reminderSB.append(context.getString(R.string.episode));
				reminderSB.append(" ");
				reminderSB.append(broadcast.getProgram().getEpisodeNumber());
				reminderSB.append("?");
				break;
			}
			
			case MOVIE:
			{
				reminderSB.append(context.getString(R.string.reminder_text_remove));
				reminderSB.append(broadcast.getProgram().getTitle());
				reminderSB.append("?");
				break;
			}
			
			case SPORT:
			{
				reminderSB.append(context.getString(R.string.reminder_text_remove));
				reminderSB.append(broadcast.getProgram().getTitle());
				reminderSB.append("?");
				break;
			}
			
			case OTHER:
			{
				reminderSB.append(context.getString(R.string.reminder_text_remove));
				reminderSB.append(broadcast.getProgram().getTitle());
				reminderSB.append("?");
				break;
			}
			
			case UNKNOWN:
			default:
			{
				reminderSB.append(context.getString(R.string.reminder_text_remove));
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
				
				NotificationHelper.removeNotification(context, notificationId);
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	

	
	public static void showRemoveLikeDialog(
			final Context context,
			final Runnable aProcedure,
			final Runnable bProcedure)
	{
		final Runnable answerYes = aProcedure;
		final Runnable answerNo = bProcedure;
		final Dialog dialog = new Dialog(context, R.style.remove_notification_dialog);
		
		dialog.setContentView(R.layout.dialog_remove_notification);
		dialog.setCancelable(false);

		Button noButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_no);
		Button yesButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_yes);

		TextView textView = (TextView) dialog.findViewById(R.id.dialog_remove_notification_tv);
		textView.setText(context.getResources().getString(R.string.like_remove_text));

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
		
		dialog.show();
	}
	
	
	
	public static void showPromptSignInDialog(
			final Context context, 
			final Runnable aProcedure, 
			final Runnable bProcedure) 
	{
		final Runnable answerYes = aProcedure;
		final Runnable answerNo	= bProcedure;
		final Dialog dialog = new Dialog(context, R.style.remove_notification_dialog);
		
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
		
		dialog.show();
	}
}