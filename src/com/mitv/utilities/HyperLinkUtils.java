
package com.mitv.utilities;



import com.mitv.Constants;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.widget.TextView;



public class HyperLinkUtils 
{	
	private static class URLSpanWithoutUnderline extends URLSpan 
	{
		public URLSpanWithoutUnderline(String url) 
		{
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds)
		{
			super.updateDrawState(ds);
			
			ds.setUnderlineText(false);
		}
	}
	
	
	
	public static void stripUnderlines(TextView textView) 
	{
		Spannable s = (Spannable) textView.getText();
		
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		
		for (URLSpan span : spans) 
		{
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			
			s.removeSpan(span);
			
			span = new URLSpanWithoutUnderline(span.getURL());
			
			s.setSpan(span, start, end, 0);
		}
		
		textView.setText(s);
	}
	

	
	public static boolean checkIfMatchesDisqusURLOrFrontendURL(final String url)
	{
		boolean matchesDisqusURLOrFrontendURL = false;
		
		boolean matchesDisqusURL = false;
		boolean isFrontendURL = false;
		
		if (url != null && 
			url.isEmpty() == false)
		{
			int lastCharacterPosition = url.length()-1;
			
			String urlWithoutLastSlash;
					
			char buff = url.charAt(lastCharacterPosition);
			
			if (buff == '/')
			{
				urlWithoutLastSlash = url.substring(0, lastCharacterPosition);
			}
			else
			{
				urlWithoutLastSlash = url;
			}

			matchesDisqusURL = urlWithoutLastSlash.equalsIgnoreCase(Constants.DISQUS_COMMENTS_PAGE_URL);
			
			isFrontendURL = urlWithoutLastSlash.startsWith(Constants.URL_FRONTEND_ENVIRONMENT);
			
			matchesDisqusURLOrFrontendURL = (matchesDisqusURL || isFrontendURL);
		}
		
		return matchesDisqusURLOrFrontendURL;
	}
}
