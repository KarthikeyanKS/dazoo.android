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

public class AboutOrTermsActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = AboutOrTermsActivity.class.getName();
	private boolean mIsAboutView; // else Terms view
	private FontTextView mHeaderTv, mInfoTv, mLinkTv;
	private ActionBar mActionBar;
	private RelativeLayout mVersionNumberContainer;
	private FontTextView mVersionNumberTv;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about_or_terms);

		this.mIsAboutView = this.getClass().equals(AboutUsActivity.class);
		
		
		// add the activity to the list of running activities
		SecondScreenApplication.getInstance().getActivityList().add(this);

		initLayout();
		populateViews();
		super.initCallbackLayouts();
	}

	private void initLayout() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mHeaderTv = (FontTextView) findViewById(R.id.about_and_terms_header);
		mInfoTv = (FontTextView) findViewById(R.id.about_and_terms_info);
		mLinkTv = (FontTextView) findViewById(R.id.about_and_terms_link);
		
		mVersionNumberContainer = (RelativeLayout) findViewById(R.id.about_and_terms_version_number_container);
		mVersionNumberTv = (FontTextView) findViewById(R.id.about_and_terms_version_number_tv);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		// update the likes list on Up/Home button press too
		case android.R.id.home:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			NavUtils.navigateUpTo(this, upIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void populateViews() {
		String title = "";
		String headerText = "";
		String infoText = "";
		String linkText = "";
		if (mIsAboutView) {
			title = getString(R.string.about_title);
			headerText = getString(R.string.about_header);
			infoText = getString(R.string.about_info);
			linkText = getString(R.string.about_link);
			
			mVersionNumberContainer.setVisibility(View.VISIBLE);
			
			String appVersion = "";
			try {
				PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				appVersion = getString(R.string.settings_version) + " " + pinfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			mVersionNumberTv.setText(appVersion);
		} else {
			title = getString(R.string.terms_title);
			headerText = getString(R.string.terms_header);
			infoText = getString(R.string.terms_info);
			linkText = getString(R.string.terms_link);
			
			mVersionNumberContainer.setVisibility(View.GONE);
		}

		mActionBar.setTitle(title);
		mHeaderTv.setText(headerText);
		mInfoTv.setText(infoText);
		mLinkTv.setText(Html.fromHtml(linkText));
		mLinkTv.setMovementMethod(LinkMovementMethod.getInstance());
		stripUnderlines(mLinkTv);
	}
	
	private class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}

	private void stripUnderlines(TextView textView) {
		Spannable s = (Spannable) textView.getText();
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		for (URLSpan span : spans) {
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			s.removeSpan(span);
			span = new URLSpanNoUnderline(span.getURL());
			s.setSpan(span, start, end, 0);
		}
		textView.setText(s);
	}



	@Override
	protected void updateUI(REQUEST_STATUS status) {
	}

	@Override
	protected void loadPage() {
	}

}
