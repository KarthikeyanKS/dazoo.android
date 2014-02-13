package com.mitv.utilities;

import android.app.Activity;
import android.content.Intent;

public class ShareUtils {
	public static void shareAction(Activity activity, String subject, String shareBody, String title) {
		
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		activity.startActivity(Intent.createChooser(sharingIntent, title));		
	}

}
