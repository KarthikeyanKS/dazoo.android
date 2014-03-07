
package com.mitv.utilities;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mitv.SecondScreenApplication;



public abstract class GenericUtils 
{
	private static final String TAG = GenericUtils.class.getName();
	
	
	private static final String TEXT_PLAIN = "text/plain";
	
	
	
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
	
	
	
	public static void startShareActivity(
			final Activity activity, 
			final String subject, 
			final String shareBody, 
			final String title) 
	{	
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		
		intent.setType(TEXT_PLAIN);
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		
		activity.startActivity(Intent.createChooser(intent, title));		
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
	
	
	
	public static int geDeviceDensityDPI()
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		int densityDpi = metrics.densityDpi;
		
		return densityDpi;
	}
	
	
	
	// TODO NewArc Deprecated - Should we keep this?
	@Deprecated
	public static void logFacebookKeyHash(final Context context)
	{
		PackageInfo info;
		
		try 
		{
			info = context.getPackageManager().getPackageInfo("com.mitv", PackageManager.GET_SIGNATURES);
			
			for (Signature signature : info.signatures) 
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				
				md.update(signature.toByteArray());
				
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} 
		catch (NameNotFoundException nnfex) 
		{
			Log.e(TAG, nnfex.getMessage(), nnfex);
		} 
		catch (NoSuchAlgorithmException nsex)
		{
			Log.e(TAG, nsex.getMessage(), nsex);
		}
	}
	
	
	
	
	// TODO NewArc Deprecated - Should we keep this?
	@Deprecated
	public static String replaceDashWithEnDash(String googleAnalyticsTrackingId) 
	{
		String replacedOrSame = googleAnalyticsTrackingId;
		
		boolean containsWrong = googleAnalyticsTrackingId.contains("-");
		
		if (containsWrong) 
		{
			Log.w(TAG, String.format(LanguageUtils.getCurrentLocale(), "GoogleAnalytics TrackingID (%s) contains ordinary dash instead of 'en dash' => replacing with 'en dash' chars!", googleAnalyticsTrackingId));
			
			replacedOrSame = googleAnalyticsTrackingId.replace("-", "�");
		}
		
		return replacedOrSame;
	}

	
	
	// TODO NewArc - Change this to a pseudo unique own generated ID instead: http://stackoverflow.com/a/17625641
	@Deprecated
	public static String getDeviceId()
	{
		String deviceId = null;

		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();

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