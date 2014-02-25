
package com.mitv.utilities;


import android.app.Activity;
import android.content.Intent;



public class ShareUtils 
{
	private static final String TEXT_PLAIN = "text/plain";
	
	
	
	public static void startShareActivity(Activity activity, String subject, String shareBody, String title) 
	{	
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		
		intent.setType(TEXT_PLAIN);
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		
		activity.startActivity(Intent.createChooser(intent, title));		
	}
}