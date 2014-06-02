package com.mitv.activities.base;

import android.util.Log;

import com.mitv.SecondScreenApplication;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.enums.UIStatusEnum;

public class BaseContentCompetitionActivity extends BaseContentActivity
{
	private static final String TAG = BaseContentCompetitionActivity.class.getName();
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if(!SecondScreenApplication.isAppRestarting()) {
			loadDataWithConnectivityCheck();
		} else {
			Log.e(TAG, "Not loading data since we are restarting the app");
		}
	}



	@Override
	protected void updateUI(UIStatusEnum status) {
	}



	@Override
	protected void loadData() {
	}



	@Override
	protected boolean hasEnoughDataToShowContent() {
		return false;
	}


	@Override
	protected void onDataAvailable(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
	}
}
