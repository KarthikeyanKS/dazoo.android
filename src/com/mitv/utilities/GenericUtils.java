
package com.mitv.utilities;



import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;



public class GenericUtils 
{
	private static final String	TAG	= "Utils";
	
	
	
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
			
			Log.e(TAG, "Failed to get PackageInfo", nnfex);
		}
		
		return pInfo;
	}
}