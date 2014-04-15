
package com.mitv.utilities;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.managers.Appirater;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVBroadcast;



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
			final TVBroadcast broadcast) 
	{
		Appirater.significantEvent(activity);
		
		StringBuilder sb = new StringBuilder();
		sb.append(activity.getString(R.string.share_comment));
		sb.append(" ");
		sb.append(broadcast.getShareUrl());
		
		String subject = activity.getString(R.string.app_name);
		String shareBody = sb.toString();
		String title =  activity.getString(R.string.share_action_title);
		
		/* Display user with sharing alternatives */
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType(TEXT_PLAIN);
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		Intent chooserIntent = Intent.createChooser(intent, title);
		activity.startActivity(chooserIntent);
		
		/* Send sharing event to Google Analytics */
		TrackingGAManager.sharedInstance().sendUserSharedEvent(activity, broadcast);
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
	
	
	
	@SuppressLint("NewApi")
	public static boolean isActivityNotNullAndNotFinishingAndNotDestroyed(Activity activity)
	{
		boolean isActivityNotNullAndNotFinishingAndNotDestroyed;
	
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
			isActivityNotNullAndNotFinishingAndNotDestroyed = (activity != null && 
											      activity.isDestroyed() == false &&
											      activity.isFinishing() == false);
		}
		else
		{
			isActivityNotNullAndNotFinishingAndNotDestroyed = (activity != null && 
					                                           activity.isFinishing() == false);
		}
		
		return isActivityNotNullAndNotFinishingAndNotDestroyed;
	}
	
	
	
	/*
	 * IMPORTANT: Reenable permission on Manifest to use this function
	 */
//	public static int getActivityCount()
//	{
//		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
//
//		ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
//
//		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(3);
//
//		int activityCount = tasks.get(0).numActivities;
//
//		return activityCount;
//	}


	
    public static PackageInfo getPackageInfo()
	{
    	Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
    	
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
    
    
    
    public static ApplicationInfo getApplicationInfo()
	{
    	Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
    
    	String packageName = context.getPackageName();
    	
    	ApplicationInfo applicationInfo;
    	
		try 
		{
			applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
		}
		catch (NameNotFoundException nnfex) 
		{
			applicationInfo = null;
			
			Log.e(TAG, "Failed to get ApplicationInfo.", nnfex);
		}
		
		return applicationInfo;
	}
   
    
    
    public static boolean isFacebookAppInstalled()
    {
    	boolean isFacebookAppInstalled = false;
    	
    	PackageInfo packageInfo = getPackageInfo(Constants.FACEBOOK_APP_PACKAGE_NAME);
    	
    	if(packageInfo != null)
    	{
    		isFacebookAppInstalled = true;
    	}
    	
    	return isFacebookAppInstalled;
    }
    
    
    
    public static boolean isMinimumRequiredFacebookAppInstalled()
    {
    	boolean isMinimumRequiredFacebookAppInstalled = false;
    	
    	PackageInfo packageInfo = getPackageInfo(Constants.FACEBOOK_APP_PACKAGE_NAME);
    	
    	if(packageInfo != null)
    	{
    		int versionCode = packageInfo.versionCode;
    		
    		if(versionCode >= Constants.MINIMUM_REQUIRED_FACEBOOK_APP_VERSION_CODE)
    		{
    			isMinimumRequiredFacebookAppInstalled = true;
    		}
    	}
    	
    	return isMinimumRequiredFacebookAppInstalled;
    }
    
    
    
    private static PackageInfo getPackageInfo(String packageName) 
    {
        PackageInfo packageInfoToReturn = null;        
        
        Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
        
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        
        for(PackageInfo packageInfo : packs)
        {
        	if(packageInfo.packageName != null &&
        	   packageInfo.packageName.equalsIgnoreCase(packageName))
        	{
        		packageInfoToReturn = packageInfo;
        		break;
        	}
        }
                
        return packageInfoToReturn;
    }
    
    
    
    public static String getCurrentAppVersion()
    {
		return getPackageInfo().versionName;
    }
    
    
    
    
    /**
     * Returns the uppercased SHA-512 hash of a given string.
     * In the case of errors, an empty hash is returned.
     * @param password
     * @return 
     */
    public static String getSHA512PasswordHash(String password)
    {       
        if(password == null || password.length() <= 0)
        {
            Log.w(TAG, "Password hash function is returning an empty hash value.");
            return new String();
        }
        // No need for else
        
        MessageDigest md = null;

        try 
        {
            md = MessageDigest.getInstance("SHA-512");
        } 
        catch (NoSuchAlgorithmException nsae) 
        {
            Log.e(TAG, nsae.getMessage(), nsae);

            Log.w(TAG, "Password hash function is returning an empty hash value.");
            
            return new String();
        }
        
        if(md == null)
        {
            Log.w(TAG, "Password hash function is returning an empty hash value.");
            
            return new String();
        }
        // No need for else
		
        md.update(password.getBytes());
 
        byte byteData[] = md.digest();
 
        String base64encodedString = Base64.encodeToString(byteData, Base64.NO_WRAP);
        
        return base64encodedString;
    }
    
    
    
    public static boolean hideKeyboard(final Activity activity)
	{
    	boolean wasVisible = false;
    	
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
						wasVisible = imm.hideSoftInputFromWindow(ib, 0);
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
		
		return wasVisible;
	}
    
    
    
    @SuppressLint("NewApi")
	public static int getScreenWidth(final Activity activity) 
    {
		int screenWidth = 0;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
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
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
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
	
	
	
	/* Only use when adding a new computer to Facebook */
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