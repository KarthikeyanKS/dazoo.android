package com.mitv.utilities;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.mitv.Consts;
import com.mitv.R;

public class StringUtilities {

	// Returns a spannable string with a red LIVE indicator in front of the text
		public static SpannableStringBuilder getLiveAppendedStringBuilder(Context context, String string) {
			String liveString = context.getString(R.string.event_is_live_indicator);
			SpannableString liveSpannable = new SpannableString(liveString);
			liveSpannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), 0, liveString.length(), 0);
			SpannableString eventSpannable = new SpannableString(" - " + string);
			SpannableStringBuilder builder = new SpannableStringBuilder();
			builder.append(liveSpannable);
			builder.append(eventSpannable);
			return builder;
		}

		// Make sure a string is valid
		public static boolean isValidString(String string) {
			//if (string == null || string.equals("{}") || string.isEmpty() || string.equals("null")) return false;
			if (string == null || string.equals("{}") || TextUtils.isEmpty(string) || string.equals("null")) return false;
			return true;
		}
		
		public static float pixelsToSp(Context context, Float px) {
		    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		    return px/scaledDensity;
		}
		
		String decodeUTF8(byte[] bytes)
		{
			return new String(bytes, Consts.UTF8_CHARSET);
		}
}
