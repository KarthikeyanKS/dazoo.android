package com.millicom.secondscreen.content;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.CastCrewListAdapter;
import com.millicom.secondscreen.content.model.Cast;
import com.millicom.secondscreen.utilities.ImageLoader;

public class SSCastCrewBlockPopulator {

	private Activity		mActivity;
	private LinearLayout	mContainerView;

	public SSCastCrewBlockPopulator(Activity activity, LinearLayout containerView) {
		this.mActivity = activity;
		this.mContainerView = containerView;
	}
	
	public void createBlock(ArrayList<Cast> cast){
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.block_cast_and_crew, null);
		TextView titleTv = (TextView) contentView.findViewById(R.id.block_cast_and_crew_title_tv);
		ListView listview = (ListView) contentView.findViewById(R.id.listview);
		Button button = (Button) contentView.findViewById(R.id.block_cast_and_crew_button);
		
		CastCrewListAdapter castCrewListAdapter = new CastCrewListAdapter(mActivity, cast);
		listview.setAdapter(castCrewListAdapter);
		
		mContainerView.addView(contentView);
	}

	
}
