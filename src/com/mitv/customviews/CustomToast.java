
package com.mitv.customviews;



import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class CustomToast 
{
	private static final String TAG = CustomToast.class.getName();
	
	
	public static void createAndShowToast(
			final Context context,
			final String message)
	{
		Toast toast = new Toast(context);
		toast.setText(message);
		toast.setDuration(Toast.LENGTH_LONG);

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