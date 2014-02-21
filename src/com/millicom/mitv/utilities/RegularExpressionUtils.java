
package com.millicom.mitv.utilities;



import java.util.regex.Pattern;



public abstract class RegularExpressionUtils 
{
	@SuppressWarnings("unused")
	private static final String TAG = RegularExpressionUtils.class.getName();
	
	private final static Pattern EMAIL_ADDRESS_PATTERN	= Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
			+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	
	
	public static boolean checkEmail(String email) 
	{
		if(email != null)
		{
			return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
		}
		else
		{
			return false;
		}
	}
}