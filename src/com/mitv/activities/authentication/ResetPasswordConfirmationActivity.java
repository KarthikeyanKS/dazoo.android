
package com.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;



public class ResetPasswordConfirmationActivity 
	extends BaseActivity 
	implements OnClickListener
{
	@SuppressWarnings("unused")
	private static final String TAG = ResetPasswordConfirmationActivity.class.getName();

	
	private RelativeLayout loginBtn;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_resetpasswordfinal_activity);

		initViews();
	}
	
	
	
	@Override
	protected void loadData() 
	{
		// TODO NewArc - Do something here?
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		// TODO NewArc - Implement this
		return false;
	}
	
	
	
	@Override
	public void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		if (fetchRequestResult.wasSuccessful()) 
		{
			updateUI(UIStatusEnum.SUCCESS_WITH_CONTENT);
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
			case SUCCESS_WITH_CONTENT:
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

	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.reset_password));

		loginBtn = (RelativeLayout) findViewById(R.id.resetpassword_already_login_btn);
		loginBtn.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v)
	{
		/* IMPORTANT always call super.onClick for subclasses of BaseActivity, else tabs wont work! */
		super.onClick(v);
		
		switch (v.getId())
		{
			case R.id.resetpassword_already_login_btn:
			{
				Intent intentSignIn = new Intent(ResetPasswordConfirmationActivity.this, LoginWithMiTVUserActivity.class);
				startActivity(intentSignIn);
				break;
			}
		}
	}
}