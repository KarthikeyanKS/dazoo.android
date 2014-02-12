package com.millicom.mitv.asynctasks;

import com.mitv.Consts;

/**
 * This class is used to get the default TVChannels (around 16). It is used if you are not logged in. Then the ID
 * for each channel in the list is used as channel IDs for constructing the GetGuide request.
 * @author consultant_hdme
 *
 */
public class GetTVChannelsDefault extends GetTVChannelsBase {
	
	private static final String URL_SUFFIX = Consts.URL_CHANNELS_DEFAULT;
	
	public GetTVChannelsDefault() {
		super(URL_SUFFIX);
	}
}
