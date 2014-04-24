
package com.mitv.activities.authentication;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	private static final String TAG = ResetPasswordConfirmationActivity.class.getName();

	
	private RelativeLayout loginBtn;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (super.isRestartNeeded()) {
			return;
		}
		
		setContentView(R.layout.layout_resetpasswordfinal_activity);

		initViews();
	}
	
	
	
	@Override
	protected void loadData() 
	{
		// Do nothing
	}
	
	
	
	@Override
	protected boolean hasEnoughDataToShowContent()
	{
		return true;
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
	}

	
	
	private void initViews() 
	{
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle(getString(R.string.reset_password));

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
			
			default:
			{
				Log.w(TAG, "Unhandled onClick.");
				break;
			}
		}
	}
}