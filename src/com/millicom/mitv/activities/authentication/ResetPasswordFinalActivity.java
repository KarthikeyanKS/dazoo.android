
package com.millicom.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.millicom.mitv.activities.base.BaseLoginActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.enums.UIStatusEnum;
import com.mitv.R;



public class ResetPasswordFinalActivity 
	extends BaseLoginActivity 
	implements OnClickListener
{
	@SuppressWarnings("unused")
	private static final String TAG = ResetPasswordFinalActivity.class.getName();

	
	private Button loginBtn;

	
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

	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getResources().getString(R.string.reset_password));

		loginBtn = (Button) findViewById(R.id.resetpassword_already_login_btn);
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
				Intent intentSignIn = new Intent(ResetPasswordFinalActivity.this, LoginWithMiTVUserActivity.class);
				startActivity(intentSignIn);
				break;
			}
		}
	}
}