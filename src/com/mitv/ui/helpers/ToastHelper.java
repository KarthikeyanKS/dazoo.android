
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
	
	
	/* This is not used any longer. Please remove in the next iteration */
//	public static void createAndShowLikeToast(
//			final Activity activity,
//			String message)
//	{
//		Toast toast = new Toast(activity.getApplicationContext());
//		
//		LayoutInflater inflater = activity.getLayoutInflater();
//		
//		View layout = inflater.inflate(R.layout.toast_notification_and_like_set, (ViewGroup) activity.findViewById(R.id.notification_and_like_set_toast_container));
//
//		TextView text = (TextView) layout.findViewById(R.id.notification_and_like_set_toast_tv);
//		
//		text.setText(message);
//
//		if(android.os.Build.VERSION.SDK_INT >= 13) 
//		{
//			toast.setGravity(Gravity.BOTTOM, 0, ((int) activity.getResources().getDimension(R.dimen.bottom_tabs_height) + 5)); //200
//		} 
//		else 
//		{
//			toast.setGravity(Gravity.BOTTOM, 0, ((int) activity.getResources().getDimension(R.dimen.bottom_tabs_height) + 5)); //100
//		}
//		
//		toast.setDuration(Toast.LENGTH_SHORT);
//		toast.setView(layout);
//		
//		toast.show();
//	}

}