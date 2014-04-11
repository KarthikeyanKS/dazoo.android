
package com.mitv.ui.helpers;



import com.mitv.R;
import com.mitv.SecondScreenApplication;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class ToastHelper 
{
	private static final String TAG = ToastHelper.class.getName();
	
	
	
	public static void createAndShowNoInternetConnectionToast()
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
	
		String message = context.getString(R.string.toast_internet_connection);
		
		createAndShowToast(context, message, Toast.LENGTH_SHORT);
	}
	
	
	
	public static void createAndShowShortToast(final String message)
	{
		if(message != null)
		{
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
			
			createAndShowToast(context, message, Toast.LENGTH_SHORT);
		}
		else
		{
			Log.w(TAG, "Attempting to show a null message in Toast");
		}
	}
	
	
	
	public static void createAndShowLongToast(final String message)
	{
		if(message != null)
		{
			Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
			
			createAndShowToast(context, message, Toast.LENGTH_LONG);
		}
		else
		{
			Log.w(TAG, "Attempting to show a null message in Toast");
		}
	}
	
	
	
	private static void createAndShowToast(
			final Context context,
			final String message,
			final int duration)
	{		
		Toast toast = Toast.makeText(context, message, duration);

		LinearLayout linearLayout = (LinearLayout) toast.getView();
		
		if(linearLayout != null)
		{
			TextView textView = (TextView) linearLayout.getChildAt(0);
			
			if(textView != null)
			{
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
			}
			else
			{
				Log.w(TAG, "TextView is null.");
			}
		}
		else
		{
			Log.w(TAG, "LinearLayout is null.");
		}
		
		toast.show();
	}
}