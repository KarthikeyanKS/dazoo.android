
package com.mitv.customviews;



import com.mitv.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class ToastHelper 
{
	private static final String TAG = ToastHelper.class.getName();
	
	
	public static void createAndShowToast(
			final Context context,
			final String message)
	{
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);

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
	
	
	
	public static void createAndShowLikeToast(
			final Activity activity,
			final String message)
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		
		View layout = inflater.inflate(R.layout.toast_notification_and_like_set, (ViewGroup) activity.findViewById(R.id.notification_and_like_set_toast_container));

		final Toast toast = new Toast(activity.getApplicationContext());

		TextView text = (TextView) layout.findViewById(R.id.notification_and_like_set_toast_tv);
		
		text.setText(message);

		if (android.os.Build.VERSION.SDK_INT >= 13) 
		{
			toast.setGravity(Gravity.BOTTOM, 0, ((int) activity.getResources().getDimension(R.dimen.bottom_tabs_height) + 5)); //200
		} 
		else 
		{
			toast.setGravity(Gravity.BOTTOM, 0, ((int) activity.getResources().getDimension(R.dimen.bottom_tabs_height) + 5)); //100
		}
		
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
}