
package com.millicom.mitv.utilities;



import java.util.Locale;
import java.util.Random;

import com.mitv.SecondScreenApplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;



public abstract class GenericUtils 
{
	private static final String TAG = GenericUtils.class.getName();
	
	
	
	public static int getRandomNumberBetween() 
	{
		return getRandomNumberBetween(0, Integer.MAX_VALUE);
	}
	
	
	
	public static int getRandomNumberBetween(
			final int min,
			final int max) 
	{
        Random foo = new Random();
        
        int randomNumber = foo.nextInt(max - min) + min;
        
        if(randomNumber == min) 
        {
            return min + 1;
        }
        else 
        {
            return randomNumber;
        }
    }
	
	
	
	public static Locale getCurrentLocale(final Context context)
	{
		Locale locale;
		
		if(context != null)
		{
			locale = context.getResources().getConfiguration().locale;
		}
		else
		{
			locale = Locale.getDefault();
			
			Log.w(TAG, "Context is null. Using default locale.");
		}
		
		return locale;
	}
	
	
	
	public static boolean isActivityNotNullOrFinishing(Activity activity)
	{
		boolean activityNotNullOrFinishing = (activity != null && 
											  activity.isFinishing() == false);
		
		return activityNotNullOrFinishing;
	}
	
	
	
    public static PackageInfo getPackageInfo(final Context context)
	{
		PackageInfo pInfo;
    	
		try 
		{
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		}
		catch (NameNotFoundException nnfex) 
		{
			pInfo = null;
			
			Log.e(TAG, "Failed to get PackageInfo.", nnfex);
		}
		
		return pInfo;
	}
    
    
    
    public static void hideKeyboard(final Activity activity)
	{
		if(activity != null)
		{
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			
			if(imm != null)
			{
				View view = activity.getCurrentFocus();
				
				if(view != null)
				{
					IBinder ib = view.getWindowToken();
					
					if(ib != null)
					{
						imm.hideSoftInputFromWindow(ib, 0);
					}
					// No need for else
				}
				// No need for else
			}
			else
			{
				Log.e(TAG, "InputMethodManager is null.");
			}
		}
		else
		{
			Log.e(TAG, "Activity is null.");
		}
	}
    
    
    
    @SuppressLint("NewApi")
	public static int getScreenWidth(final Activity activity) 
    {
		int screenWidth = 0;
		
		if (Build.VERSION.SDK_INT >= 11) 
		{
	        Point size = new Point();
	        
	        try 
	        {
	        	activity.getWindowManager().getDefaultDisplay().getRealSize(size);
	        	
	            screenWidth = size.x;
	        } 
	        catch (NoSuchMethodError nse) 
	        {
	        	Log.w(TAG, nse.getMessage(), nse);
	        }

	    } 
		else 
		{
	        DisplayMetrics metrics = new DisplayMetrics();
	        
	        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        
	        screenWidth = metrics.widthPixels;
	    }
		
		return screenWidth;
	}
	
    
    
	@SuppressLint("NewApi")
	public static int getScreenHeight(final Activity activity) 
	{
		int screenHeight = 0;
		
		if (Build.VERSION.SDK_INT >= 11) 
		{
	        Point size = new Point();
	      
	        try 
	        {
	        	activity.getWindowManager().getDefaultDisplay().getRealSize(size);
	        	
	            screenHeight = size.y;
	        } 
	        catch (NoSuchMethodError nse) 
	        {
	            Log.w(TAG, nse.getMessage(), nse);
	        }

	    } 
		else 
		{
	        DisplayMetrics metrics = new DisplayMetrics();
	        
	        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        
	        screenHeight = metrics.heightPixels;
	    }
		
		return screenHeight;
	}
	
	
	
	//TODO Change this to a pseudo unique own generated ID instead: http://stackoverflow.com/a/17625641
	@Deprecated
	public static String getDeviceId()
	{
		String deviceId = null;

		Context context = SecondScreenApplication.getInstance().getApplicationContext();

		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		if(mTelephonyMgr != null)
    	{
			deviceId = mTelephonyMgr.getDeviceId();
    	}
		else
		{
			Log.e(TAG, "TelephonyManager is null");
		}
		
		return deviceId;
	}
}