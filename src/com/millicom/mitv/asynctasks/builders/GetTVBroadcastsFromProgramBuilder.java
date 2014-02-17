
package com.millicom.mitv.asynctasks.builders;



import com.millicom.mitv.asynctasks.GetTVBroadcastsFromProgram;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.interfaces.ContentCallbackListener;
import com.mitv.Consts;



public class GetTVBroadcastsFromProgramBuilder
{	
	private String tvProgramId;
	
	
	
	public GetTVBroadcastsFromProgram build(
			ContentCallbackListener contentCallbackListener,
			ActivityCallbackListener activityCallBackListener)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Consts.URL_PROGRAMS);
		sb.append(tvProgramId);
		sb.append(Consts.API_BROADCASTS);
				
		GetTVBroadcastsFromProgram getTVBroadcastsFromProgram = new GetTVBroadcastsFromProgram(contentCallbackListener, activityCallBackListener, sb.toString());
		
		return getTVBroadcastsFromProgram;
	}



	public void setTvProgramId(String tvProgramId) 
	{
		this.tvProgramId = tvProgramId;
	}
}