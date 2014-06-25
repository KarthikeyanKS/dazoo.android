
package com.mitv.activities;



import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.mitv.Constants;
import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.DeploymentEndpointTypeEnum;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.ui.elements.FontTextView;
import com.mitv.utilities.GenericUtils;
import com.mitv.utilities.HyperLinkUtils;



public abstract class AboutOrTermsActivity 
	extends BaseActivity
{
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
		
		if (isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_about_or_terms);

		this.isAboutView = this.getClass().equals(AboutUsActivity.class);
		
		initLayout();
		
		populateViews();
	}

	
	
	private void initLayout()
	{
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		headerTv = (FontTextView) findViewById(R.id.about_and_terms_header);
		infoTv = (FontTextView) findViewById(R.id.about_and_terms_info);
		linkTv = (FontTextView) findViewById(R.id.about_and_terms_link);
		
		versionNumberContainer = (RelativeLayout) findViewById(R.id.about_and_terms_version_number_container);
		versionNumberTv = (FontTextView) findViewById(R.id.about_and_terms_version_number_tv);
	}

	
	
	private void populateViews() 
	{
		String title;
		String headerText;
		String infoText;
		String linkText;
		
		if (isAboutView) 
		{
			title = getString(R.string.about_title);
			headerText = getString(R.string.about_header);
			infoText = getString(R.string.about_info);
			linkText = getString(R.string.about_link);
			
			versionNumberContainer.setVisibility(View.VISIBLE);
			
			StringBuilder appVersionSB = new StringBuilder();
			
			PackageInfo packageInfo = GenericUtils.getPackageInfo();
			
			if(packageInfo != null)
			{
				appVersionSB.append(getString(R.string.settings_version));
				appVersionSB.append(" ");
				appVersionSB.append(packageInfo.versionName);
				
				if(Constants.DEPLOYMENT_DOMAIN_USED != DeploymentEndpointTypeEnum.PRODUCTION)
				{
					appVersionSB.append(" (");
					appVersionSB.append(Constants.DEPLOYMENT_DOMAIN_USED.toString());
					appVersionSB.append(")");
				}
			}
			
			versionNumberTv.setText(appVersionSB.toString());
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
		
		HyperLinkUtils.stripUnderlines(linkTv);
	}

		
		
	@Override
	protected void loadData() {/* Do nothing (no data to load on this activity) */}
	
	
	
	@Override
	protected void loadDataInBackground()
	{
		Log.w(TAG, "Not implemented in this class");
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return true;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {/* Do nothing (no data to load on this activity) */}

	

	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
	}
}