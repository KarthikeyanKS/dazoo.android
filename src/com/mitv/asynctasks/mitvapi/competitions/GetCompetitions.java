


package com.mitv.asynctasks.mitvapi.competitions;



import com.mitv.Constants;
import com.mitv.asynctasks.AsyncTaskBase;
import com.mitv.enums.HTTPRequestTypeEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ContentCallbackListener;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.objects.mitvapi.competitions.Competition;



public class GetCompetitions 
	extends AsyncTaskBase<Competition[]>
{
	@SuppressWarnings("unused")
	private static final String TAG = GetCompetitions.class.getName();
	
	
	private static String url = Constants.URL_COMPETITIONS_FULL;
	
	
	
	private static RequestIdentifierEnum getRequestIdentifier(boolean standalone)
	{
		RequestIdentifierEnum requestIdentifier;
		
		if(standalone)
		{
			requestIdentifier = RequestIdentifierEnum.COMPETITIONS_ALL_STANDALONE;
		}
		else
		{
			requestIdentifier = RequestIdentifierEnum.COMPETITIONS_ALL_INITIAL;
		}
		
		return requestIdentifier;
	}
	
	
	
	public GetCompetitions(
			ContentCallbackListener contentCallbackListener,
			ViewCallbackListener activityCallbackListener,
			boolean standalone,
			int retryThreshold)
	{
		super(contentCallbackListener, activityCallbackListener, getRequestIdentifier(standalone), Competition[].class, HTTPRequestTypeEnum.HTTP_GET, url, true, retryThreshold);
	}
}
