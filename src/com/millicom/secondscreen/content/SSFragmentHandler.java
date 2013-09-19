package com.millicom.secondscreen.content;

import java.util.ArrayList;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.activity.ActivityFragment;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.SSSection;
import com.millicom.secondscreen.content.myprofile.MyProfileFragment;
import com.millicom.secondscreen.content.tvguide.TVGuideFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class SSFragmentHandler {
	private static SSFragmentHandler	mInstance;

	private final String				TAG	= "VPFragmentHandler";

	public static SSFragmentHandler getInstance() {
		if (mInstance == null) mInstance = new SSFragmentHandler();
		return mInstance;
	}

	public Fragment getFragmentBySectionId(SSSection section, ArrayList<Guide> mGuide) {

		Log.d(TAG, "Section ID : " + section.getId());

		Fragment fragment = null;
		Bundle bundle = new Bundle();

		if (section.getId().equals(Consts.SECTION_ID_TVGUIDE)) {
			fragment = new TVGuideFragment();
			// Pass the section
			bundle.putParcelableArrayList(Consts.INTENT_EXTRA_GUIDE, mGuide);
			fragment.setArguments(bundle);
		}
		else if (section.getId().equals(Consts.SECTION_ID_ACTIVITY)) {
			fragment = new ActivityFragment();

		}
		else if (section.getId().equals(Consts.SECTION_ID_ME)) {
			fragment = new MyProfileFragment();

		}
		return fragment;
	}
	
}
