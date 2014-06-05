
package com.mitv.activities;



import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.RelativeLayout;

import com.mitv.R;
import com.mitv.activities.authentication.LoginWithFacebookActivity;
import com.mitv.activities.authentication.LoginWithMiTVUserActivity;
import com.mitv.activities.authentication.SignUpWithEmailActivity;
import com.mitv.activities.base.BaseActivityWithoutSearchOption;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;
import com.mitv.ui.elements.FontTextView;
import com.mitv.utilities.HyperLinkUtils;



public class SignUpSelectionActivity 
	extends BaseActivityWithoutSearchOption
{
	@SuppressWarnings("unused")
	private static final String TAG = SignUpSelectionActivity.class.getName();
	
	
	private RelativeLayout facebookContainer;
	private RelativeLayout signUpContainer;
	private RelativeLayout loginButton;
	private FontTextView termsOfService;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_signin_activity);
		
		initViews();
	}
	
	
	@Override
	protected void loadData() 
	{
		/* Do nothing (no data to load on this activity) */
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return true;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		/* Do nothing (no data to load on this activity) */
	}
	
	
	
	@Override
	protected void updateUI(UIStatusEnum status) 
	{
		super.updateUIBaseElements(status);
	}
	
	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getString(R.string.sign_up));

		facebookContainer = (RelativeLayout) findViewById(R.id.signin_facebook_container);
		
		facebookContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SignUpSelectionActivity.this, LoginWithFacebookActivity.class);
				
				startActivity(intent);
				
				finish();
			}
		});

		signUpContainer = (RelativeLayout) findViewById(R.id.signin_signup_email_container);
		
		signUpContainer.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SignUpSelectionActivity.this, SignUpWithEmailActivity.class);
				
				startActivity(intent);
			}
		});

		loginButton = (RelativeLayout) findViewById(R.id.signin_login_btn);
		
		loginButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(SignUpSelectionActivity.this, LoginWithMiTVUserActivity.class);
				
				startActivity(intent);
			}
		});
		
		termsOfService = (FontTextView) findViewById(R.id.signin_terms_link);

		String linkText = getString(R.string.sign_up_terms_link);
		
		termsOfService.setText(Html.fromHtml(linkText));
		termsOfService.setMovementMethod(LinkMovementMethod.getInstance());
		
		HyperLinkUtils.stripUnderlines(termsOfService);
	}
}