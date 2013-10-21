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
		dialog.setContentView(R.layout.dialog_remove_notification);
		dialog.setCancelable(false);
		dialog.setTitle(context.getResources().getString(R.string.sign_in_to_dazoo_title));
		
		Button cancelButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_no);
		Button signInButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_yes);
		cancelButton.setText(context.getResources().getString(R.string.cancel));
		signInButton.setText(context.getResources().getString(R.string.sign_in));

		TextView textView = (TextView) dialog.findViewById(R.id.dialog_remove_notification_tv);
		textView.setText(context.getResources().getString(R.string.sign_in_to_dazoo_text));

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
