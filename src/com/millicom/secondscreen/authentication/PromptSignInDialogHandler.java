package com.millicom.secondscreen.authentication;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.like.LikeService;

public class PromptSignInDialogHandler {

	public Runnable	answerYes	= null;
	public Runnable	answerNo	= null;
	
	public boolean showPromptSignInDialog(final Context context, Runnable aProcedure, Runnable bProcedure) {
		answerYes = aProcedure;
		answerNo = bProcedure;

		final Dialog dialog = new Dialog(context, R.style.remove_notification_dialog);
		dialog.setContentView(R.layout.dialog_prompt_signin);
		dialog.setCancelable(false);
		
		Button cancelButton = (Button) dialog.findViewById(R.id.dialog_prompt_signin_button_cancel);
		Button signInButton = (Button) dialog.findViewById(R.id.dialog_prompt_signin_button_signin);
	
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// do not remove the like
				answerNo.run();
				dialog.dismiss();
			}
		});

		signInButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				answerYes.run();
				dialog.dismiss();
			}
		});
		dialog.show();
		return true;
	}
	
	
}
