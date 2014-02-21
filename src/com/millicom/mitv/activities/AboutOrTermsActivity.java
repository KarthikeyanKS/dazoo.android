package com.millicom.mitv.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
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

import com.mitv.Consts.REQUEST_STATUS;
import com.mitv.R;
import com.mitv.SecondScreenApplication;
import com.mitv.customviews.FontTextView;


public class AboutOrTermsActivity 
	extends BaseActivity 
	implements OnClickListener 
{
	@SuppressWarnings("unused")
	private static final String TAG = AboutOrTermsActivity.class.getName();

	
	private boolean isAboutView;
	private FontTextView headerTv;
	private FontTextView infoTv;
	private FontTextView linkTv;
	private ActionBar actionBar;
	private RelativeLayout versionNumberContainer;
	private FontTextView versionNumberTv;

	
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_about_or_terms);

		this.isAboutView = this.getClass().equals(AboutUsActivity.class);
		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		initLayout();
		
		populateViews();
		
		super.initCallbackLayouts();
	}

	
	
	private void initLayout()
	{
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// styling bottom navigation tabs
		RelativeLayout tabTvGuide = (RelativeLayout) findViewById(R.id.tab_tv_guide);
		tabTvGuide.setOnClickListener(this);
		RelativeLayout tabActivity = (RelativeLayout) findViewById(R.id.tab_activity);
		tabActivity.setOnClickListener(this);
		RelativeLayout tabProfile = (RelativeLayout) findViewById(R.id.tab_me);
		tabProfile.setOnClickListener(this);

		View tabDividerLeft = (View) findViewById(R.id.tab_left_divider_container);
		View tabDividerRight = (View) findViewById(R.id.tab_right_divider_container);

		tabDividerLeft.setBackgroundColor(getResources().getColor(R.color.tab_divider_default));
		tabDividerRight.setBackgroundColor(getResources().getColor(R.color.tab_divider_selected));

		tabTvGuide.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabActivity.setBackgroundColor(getResources().getColor(R.color.yellow));
		tabProfile.setBackgroundColor(getResources().getColor(R.color.red));

		headerTv = (FontTextView) findViewById(R.id.about_and_terms_header);
		infoTv = (FontTextView) findViewById(R.id.about_and_terms_info);
		linkTv = (FontTextView) findViewById(R.id.about_and_terms_link);
		
		versionNumberContainer = (RelativeLayout) findViewById(R.id.about_and_terms_version_number_container);
		versionNumberTv = (FontTextView) findViewById(R.id.about_and_terms_version_number_tv);
	}

	
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			// Respond to the action bar's Up/Home button
			// update the likes list on Up/Home button press too
			case android.R.id.home:
			{
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				NavUtils.navigateUpTo(this, upIntent);
				return true;
			}
		}
		
		return super.onOptionsItemSelected(item);
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

	
	
	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
		
		switch (id) 
		{
			case R.id.tab_tv_guide:
				// tab to home page
				Intent intentHome = new Intent(AboutOrTermsActivity.this, HomeActivity.class);
				intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentHome);
	
				break;
			case R.id.tab_activity:
				// tab to home page
				Intent intentActivity = new Intent(AboutOrTermsActivity.this, ActivityActivity.class);
				startActivity(intentActivity);
	
				break;
			case R.id.tab_me:
				Intent intentMe = new Intent(AboutOrTermsActivity.this, MyProfileActivity.class);
				startActivity(intentMe);
	
				break;
		}
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
	protected void updateUI(REQUEST_STATUS status) {}


	
	@Override
	protected void loadPage() {}
}