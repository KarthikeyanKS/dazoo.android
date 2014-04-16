

package com.mitv.utilities;



import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import com.mitv.SecondScreenApplication;
import com.mitv.managers.FontManager;
import com.mitv.ui.elements.CustomTypefaceSpan;



public class SpannableUtils 
{
	public static Spannable getCustomFontSpannable(String title, String matchedString) 
	{
		Spannable spannable = null;

		if (title != null && matchedString != null && title.length() > 0 && matchedString.length() > 0) 
		{
			String titleLowercaseOnly = title.toLowerCase(LanguageUtils.getCurrentLocale());

			String matchedStringLowercaseOnly = matchedString.toLowerCase(LanguageUtils.getCurrentLocale());

			int matchedStringStartIndex = titleLowercaseOnly.indexOf(matchedStringLowercaseOnly);

			if (matchedStringStartIndex >= 0) 
			{
				/* Title contains matchedString */
				String beforeBold = title.substring(0, matchedStringStartIndex);

				int toBoldStartIndex = beforeBold.length();
				
				int toBoldEndIndex = toBoldStartIndex + matchedString.length();
				
				String toBold = title.substring(toBoldStartIndex, toBoldEndIndex);

				int afterBoldStartIndex = beforeBold.length() + toBold.length();
				
				String afterBold = title.substring(afterBoldStartIndex, title.length());
				
				spannable = getCustomFontSpannableUsingThreeStrings(beforeBold, toBold, afterBold);
			}
		}

		return spannable;
	}
	
	
	
	private static Spannable getCustomFontSpannableUsingThreeStrings(String beforeBold, String toBold, String afterBold) 
	{
		Context context = SecondScreenApplication.sharedInstance().getApplicationContext();
		
		int partOneStart = 0;
		int partOneEnd = beforeBold.length();
		int partTwoStart = partOneEnd;
		int partTwoEnd = partTwoStart + toBold.length();
		int partThreeStart = partTwoEnd;
		int partThreeEnd = partThreeStart + afterBold.length();

		// Create a new spannable with the two strings
		Spannable spannable = new SpannableString(beforeBold + toBold + afterBold);

		// Set the custom typeface to span over a section of the spannable object
		spannable.setSpan(new CustomTypefaceSpan(FontManager.getFontLight(context)), partOneStart, partOneEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan(FontManager.getFontBold(context)), partTwoStart, partTwoEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan(FontManager.getFontLight(context)), partThreeStart, partThreeEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannable;
	}
}
