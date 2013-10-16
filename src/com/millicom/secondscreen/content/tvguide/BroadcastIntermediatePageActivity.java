package com.millicom.secondscreen.content.tvguide;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.content.SSBroadcastPage;
import com.millicom.secondscreen.content.SSPageCallback;
import com.millicom.secondscreen.content.SSPageGetResult;
import com.millicom.secondscreen.content.model.Broadcast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BroadcastIntermediatePageActivity extends Activity {

	private String	mBroadcastUrl;
	private Broadcast mBroadcast;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		mBroadcastUrl = intent.getParcelableExtra(Consts.INTENT_EXTRA_BROADCAST_URL);

		if (mBroadcastUrl != null) {
			SSBroadcastPage.getInstance().getPage(null, mBroadcastUrl, new SSPageCallback() {
				@Override
				public void onGetPageResult(SSPageGetResult aPageGetResult) {

					mBroadcast = SSBroadcastPage.getInstance().getBroadcast();
					if (mBroadcast != null) {
						startNewActivity();
					}
				}
			});
		}
	}
	
	private void startNewActivity(){
		Intent intent = new Intent(this, BroadcastPageActivity.class);
		intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_BROADCAST, mBroadcast);
		startActivity(intent);
		finish();
	}
}
