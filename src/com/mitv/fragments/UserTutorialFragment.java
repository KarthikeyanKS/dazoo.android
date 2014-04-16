package com.mitv.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mitv.R;

/**
 * Fragment for the user tutorial.
 * 
 * @author atsampikakis
 *
 */
public class UserTutorialFragment extends Fragment {

	private int currentPage;
	private View rootView;
	
	private static final int PAGE1 = 0;
	private static final int PAGE2 = 1;
	private static final int PAGE3 = 2;
	private static final int PAGE4 = 3;
	private static final int PAGE5 = 4;
	
	private ImageView imgView;
	private TextView splashText;
	private TextView headerText;
	private TextView infoText;

	private ImageView imageMobileTutorial;

	public static final String ARG_OBJECT = "object";
	
	
	public UserTutorialFragment(int position) {
		this.currentPage = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		rootView = inflater.inflate(R.layout.fragment_user_tutorial_layout, container, false);
		
		initViews();
		
		setView();
		
		return rootView;
	}
	
	private void setView() {
		
		switch (currentPage) {
			case PAGE1: {
				rootView.setBackgroundResource(R.drawable.splash_bg);
				imgView.setVisibility(View.VISIBLE);
				splashText.setVisibility(View.VISIBLE);
				break;
			}
			case PAGE2: {
				imageMobileTutorial.setImageResource(R.drawable.tutorial_mobile1);
				imageMobileTutorial.setVisibility(View.VISIBLE);
				headerText.setText(R.string.tutorial_header_page1);
				headerText.setVisibility(View.VISIBLE);
				infoText.setText(R.string.tutorial_info_page1);
				infoText.setVisibility(View.VISIBLE);
				break;
			}
			case PAGE3: {
				imageMobileTutorial.setImageResource(R.drawable.tutorial_mobile2);
				imageMobileTutorial.setVisibility(View.VISIBLE);
				headerText.setText(R.string.tutorial_header_page2);
				headerText.setVisibility(View.VISIBLE);
				infoText.setText(R.string.tutorial_info_page2);
				infoText.setVisibility(View.VISIBLE);
				break;
			}
			case PAGE4: {
				imageMobileTutorial.setImageResource(R.drawable.tutorial_mobile3);
				imageMobileTutorial.setVisibility(View.VISIBLE);
				headerText.setText(R.string.tutorial_header_page3);
				headerText.setVisibility(View.VISIBLE);
				infoText.setText(R.string.tutorial_info_page3);
				infoText.setVisibility(View.VISIBLE);
				break;
			}
			case PAGE5: {
				imageMobileTutorial.setImageResource(R.drawable.tutorial_mobile4);
				imageMobileTutorial.setVisibility(View.VISIBLE);
				headerText.setText(R.string.tutorial_header_page4);
				headerText.setVisibility(View.VISIBLE);
				infoText.setText(R.string.tutorial_info_page4);
				infoText.setVisibility(View.VISIBLE);
				break;
			}
			default: {
				break;
			}
		}
	}
	
	
	
	private void initViews() {
		rootView.setBackgroundResource(R.color.orange_background);
		
		/* PAGE 1 */
		imgView = (ImageView) rootView.findViewById(R.id.splash_screen_activity_logo_tutorial);
		splashText = (TextView) rootView.findViewById(R.id.splash_screen_activity_info_text_tutorial);
		
		/* HEADER & INFO */
		headerText = (TextView) rootView.findViewById(R.id.tutorial_header);
		infoText = (TextView) rootView.findViewById(R.id.tutorial_info);
		
		imageMobileTutorial = (ImageView) rootView.findViewById(R.id.image_mobile_tutorial);
	}

}
