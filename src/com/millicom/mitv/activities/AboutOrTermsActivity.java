
package com.millicom.mitv.activities;



import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.mitv.R;
import com.mitv.customviews.FontTextView;



public abstract class AboutOrTermsActivity 
	extends BaseActivity 
	implements OnClickListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = AboutOrTermsActivity.class.getName();

	
	private boolean isAboutView;
	private FontTextView headerTv;
	private FontTextView infoTv;
	private FontTextView linkTv;
	private RelativeLayout versionNumberContainer;
	private FontTextView versionNumberTv;

	
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_about_or_terms);

		this.isAboutView = this.getClass().equals(AboutUsActivity.class);
		
		initLayout();
		
		populateViews();
		
		super.initCallbackLayouts();
	}

	
	
	private void initLayout()
	{
		actionBar.setDisplayHomeAsUpEnabled(true);

		headerTv = (FontTextView) findViewById(R.id.about_and_terms_header);
		infoTv = (FontTextView) findViewById(R.id.about_and_terms_info);
		linkTv = (FontTextView) findViewById(R.id.about_and_terms_link);
		
		versionNumberContainer = (RelativeLayout) findViewById(R.id.about_and_terms_version_number_container);
		versionNumberTv = (FontTextView) findViewById(R.id.about_and_terms_version_number_tv);
	}
	
	private void populateViews() 
	{
		String title = "";
		String headerText = "";
		String infoText = "";
		String linkText = "";
		
		if (isAboutView) 
		{
			title = getString(R.string.about_title);
			headerText = getString(R.string.about_header);
			infoText = getString(R.string.about_info);
			linkText = getString(R.string.about_link);
			
			versionNumberContainer.setVisibility(View.VISIBLE);
			
			String appVersion = "";
			
			try 
			{
				PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				appVersion = getString(R.string.settings_version) + " " + pinfo.versionName;
			} 
			catch (NameNotFoundException e) 
			{
				e.printStackTrace();
			}
			
			versionNumberTv.setText(appVersion);
		} 
		else 
		{
			title = getString(R.string.terms_title);
			headerText = getString(R.string.terms_header);
			infoText = getString(R.string.terms_info);
			linkText = getString(R.string.terms_link);
			
			versionNumberContainer.setVisibility(View.GONE);
		}

		actionBar.setTitle(title);
		headerTv.setText(headerText);
		infoTv.setText(infoText);
		linkTv.setText(Html.fromHtml(linkText));
		linkTv.setMovementMethod(LinkMovementMethod.getInstance());
		
		stripUnderlines(linkTv);
	}
	
	private class URLSpanNoUnderline extends URLSpan 
	{
		public URLSpanNoUnderline(String url)
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

	
	
	private void stripUnderlines(TextView textView) 
	{
		Spannable s = (Spannable) textView.getText();
		
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		
		for (URLSpan span : spans) 
		{
			int start = s.getSpanStart(span);
			
			int end = s.getSpanEnd(span);
			
			s.removeSpan(span);
			
			span = new URLSpanNoUnderline(span.getURL());
			
			s.setSpan(span, start, end, 0);
		}
		
		textView.setText(s);
	}

	
	
	@Override
	protected void loadData() 
	{
		// TODO NewArc - Implement this
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCEEDED_WITH_DATA);
		} 
		else
		{
			updateUI(UIStatusEnum.FAILED);
		}
	}

	

	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);

		switch (status) 
		{	
			case SUCCEEDED_WITH_DATA:
			{
				// TODO NewArc - Do something here?
				break;
			}
	
			default:
			{
				// TODO NewArc - Do something here?
				break;
			}
		}
	}
}