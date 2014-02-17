
package com.millicom.mitv.asynctasks;



import com.millicom.mitv.asynctasks.builders.GetTVBroadcastsFromProgramBuilder;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.model.OldBroadcast;



public class GetTVBroadcastsFromProgram 
	extends AsyncTaskWithRelativeURL<OldBroadcast>
{
	public static GetTVBroadcastsFromProgram newGetTVBroadcastsFromProgramTask(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String tvProgramId) 
	{
		GetTVBroadcastsFromProgramBuilder getTVBroadcastsFromProgramBuilder = new GetTVBroadcastsFromProgramBuilder();
		getTVBroadcastsFromProgramBuilder.setTvProgramId(tvProgramId);
		
		GetTVBroadcastsFromProgram getTVBroadcastsFromProgram = getTVBroadcastsFromProgramBuilder.build(contentCallbackListener, activityCallBackListener);
		
		return getTVBroadcastsFromProgram;
	}
	
	
	
	public GetTVBroadcastsFromProgram(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener,
			String url) 
	{
		super(contentCallbackListener, activityCallBackListener, RequestIdentifierEnum.BROADCASTS_FROM_PROGRAMS, OldBroadcast.class, HTTPRequestTypeEnum.HTTP_GET, url);
	}
}