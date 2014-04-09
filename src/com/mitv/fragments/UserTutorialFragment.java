package com.mitv.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
	private LayoutInflater inflater;
	private ViewGroup container;
	
	private static final int PAGE1 = 0;
	private static final int PAGE2 = 1;
	private static final int PAGE3 = 2;
	private static final int PAGE4 = 3;
	private static final int PAGE5 = 4;
	
	private ImageView imgView;
	private TextView splash_text;
	private TextView header_text;
	private TextView info_text;

	private ImageView image_mobile_tutorial;

	public static final String ARG_OBJECT = "object";
	
	
	public UserTutorialFragment(int position) {
		this.currentPage = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		
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
				splash_text.setVisibility(View.VISIBLE);
				break;
			}
			case PAGE2: {
//				rootView.setBackgroundResource(R.drawable.tutorial_screen1);
				image_mobile_tutorial.setImageResource(R.drawable.tutorial_mobile1);
				image_mobile_tutorial.setVisibility(View.VISIBLE);
				header_text.setText(R.string.tutorial_header_page1);
				header_text.setVisibility(View.VISIBLE);
				info_text.setText(R.string.tutorial_info_page1);
				info_text.setVisibility(View.VISIBLE);
				break;
			}
			case PAGE3: {
//				rootView.setBackgroundResource(R.drawable.tutorial_screen2);
				image_mobile_tutorial.setImageResource(R.drawable.tutorial_mobile2);
				image_mobile_tutorial.setVisibility(View.VISIBLE);
				header_text.setText(R.string.tutorial_header_page2);
				header_text.setVisibility(View.VISIBLE);
				info_text.setText(R.string.tutorial_info_page2);
				info_text.setVisibility(View.VISIBLE);
				break;
			}
			case PAGE4: {
//				rootView.setBackgroundResource(R.drawable.tutorial_screen3);
				image_mobile_tutorial.setImageResource(R.drawable.tutorial_mobile3);
				image_mobile_tutorial.setVisibility(View.VISIBLE);
				header_text.setText(R.string.tutorial_header_page3);
				header_text.setVisibility(View.VISIBLE);
				info_text.setText(R.string.tutorial_info_page3);
				info_text.setVisibility(View.VISIBLE);
				break;
			}
			case PAGE5: {
//				rootView.setBackgroundResource(R.drawable.tutorial_screen4);
				image_mobile_tutorial.setImageResource(R.drawable.tutorial_mobile4);
				image_mobile_tutorial.setVisibility(View.VISIBLE);
				header_text.setText(R.string.tutorial_header_page4);
				header_text.setVisibility(View.VISIBLE);
				info_text.setText(R.string.tutorial_info_page4);
				info_text.setVisibility(View.VISIBLE);
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
		splash_text = (TextView) rootView.findViewById(R.id.splash_screen_activity_info_text_tutorial);
		
		/* HEADER & INFO */
		header_text = (TextView) rootView.findViewById(R.id.tutorial_header);
		info_text = (TextView) rootView.findViewById(R.id.tutorial_info);
		
		image_mobile_tutorial = (ImageView) rootView.findViewById(R.id.image_mobile_tutorial);
	}

}
