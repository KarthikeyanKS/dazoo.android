package com.mitv.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.mitv.GATrackingManager;

public class CustomGoogleCampaignReceiver extends BroadcastReceiver {

	@SuppressWarnings("unused")
	private static final String TAG = CustomGoogleCampaignReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String campaignData = null;

		Bundle extrasBundle = intent.getExtras();

		if (extrasBundle != null) {

			campaignData = extrasBundle.getString("referrer");
		}

		/* Pass the extras from the intent to the Google Analytics receiver */
		new CampaignTrackingReceiver().onReceive(context, intent);
		
		GATrackingManager.sharedInstance().sendGooglePlayCampaignToAnalytics(campaignData);
	}
}
