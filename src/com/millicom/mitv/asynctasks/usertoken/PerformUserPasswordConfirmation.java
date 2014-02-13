
package com.millicom.mitv.asynctasks.usertoken;



import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.millicom.mitv.models.DummyData;
import com.mitv.Consts;
import com.mitv.model.TVLike;



public class PerformUserPasswordConfirmation extends AsyncTaskWithUserToken<DummyData> 
{
	private static final String URL_SUFFIX = Consts.URL_RESET_AND_CONFIRM_PASSWORD;
	
	public PerformUserPasswordConfirmation(ContentCallbackListener contentCallbackListener, ActivityCallbackListener activityCallBackListener) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.USER_RESET_PASSWORD_SEND_CONFIRM_PASSWORD, DummyData.class, URL_SUFFIX);
	}
	

	@Override
	protected Void doInBackground(String... params) 
	{
		/* Parse JSON data using GSON */
		
		return null;
	}
}
