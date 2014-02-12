package com.millicom.mitv.asynctasks;

import com.mitv.Consts;

/**
 * This class is used to get all the around hundreds of TVChannels. It is used if you are logged in, in combination
 * with the call that fetched the Channel IDS for that specific user.
 * @author consultant_hdme
 *
 */
public class GetTVChannelsAll extends GetTVChannelsBase {
	
	private static final String URL_SUFFIX = Consts.URL_CHANNELS_ALL;
	
	public GetTVChannelsAll() {
		super(URL_SUFFIX);
	}
}
