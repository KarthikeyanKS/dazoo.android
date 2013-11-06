package com.millicom.secondscreen.like;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.millicom.secondscreen.R;

public class LikeDialogHandler {

	public Runnable	answerYes	= null;
	public Runnable	answerNo	= null;

	public boolean showRemoveLikeDialog(final Context context, final String token, final String entityId, final String likeType, Runnable aProcedure, Runnable bProcedure) {
		answerYes = aProcedure;
		answerNo = bProcedure;

		final Dialog dialog = new Dialog(context, R.style.remove_notification_dialog);
		dialog.setContentView(R.layout.dialog_remove_notification);
		dialog.setCancelable(false);

		Button noButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_no);
		Button yesButton = (Button) dialog.findViewById(R.id.dialog_remove_notification_button_yes);

		TextView textView = (TextView) dialog.findViewById(R.id.dialog_remove_notification_tv);
		textView.setText(context.getResources().getString(R.string.like_remove_text));

		noButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// do not remove the like
				answerNo.run();
				dialog.dismiss();
			}
		});

		yesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// remove the like
				answerYes.run();
				LikeService.removeLike(token, entityId, likeType);
				dialog.dismiss();
			}
		});
		dialog.show();
		return true;
	}

}
