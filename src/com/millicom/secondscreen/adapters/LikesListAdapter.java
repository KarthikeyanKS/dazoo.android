package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LikesListAdapter extends BaseAdapter {

	private static final String	TAG	= "LikesListAdapter";

	private LayoutInflater		mLayoutInflater;
	private Activity			mActivity;
	private ArrayList<Program>	mPrograms;

	private ImageLoader			mImageLoader;

	public LikesListAdapter(Activity mActivity, ArrayList<Program> mPrograms) {
		this.mPrograms = mPrograms;
		this.mActivity = mActivity;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
	}

	@Override
	public int getCount() {
		if (mPrograms != null) {
			return mPrograms.size();
		} else return 0;
	}

	@Override
	public Program getItem(int position) {
		if (mPrograms != null) {
			return mPrograms.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewHolder viewHolder = new ViewHolder();
			rowView = mLayoutInflater.inflate(R.layout.row_likes, null);

			viewHolder.mProgramIv = (ImageView) rowView.findViewById(R.id.row_likes_iv);
			viewHolder.mProgramTitleTv = (TextView) rowView.findViewById(R.id.row_likes_text_title_tv);
			viewHolder.mProgramTypeTv = (TextView) rowView.findViewById(R.id.row_likes_text_details_tv);
			viewHolder.mButtonContainer = (RelativeLayout) rowView.findViewById(R.id.row_likes_button_container);
			viewHolder.mButtonIcon = (ImageView) rowView.findViewById(R.id.row_likes_button_iv);
			viewHolder.mButtonTv = (TextView) rowView.findViewById(R.id.row_likes_button_tv);

			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		final Program program = getItem(position);
		if (program != null) {
			mImageLoader.displayImage(program.getPosterMUrl(), holder.mProgramIv, ImageLoader.IMAGE_TYPE.POSTER);
			holder.mProgramTitleTv.setText(program.getTitle());
			holder.mProgramTypeTv.setText("Genre");
		}

		return rowView;
	}

	public static class ViewHolder {
		public ImageView		mProgramIv;
		public TextView			mProgramTitleTv;
		public TextView			mProgramTypeTv;
		public RelativeLayout	mButtonContainer;
		public ImageView		mButtonIcon;
		public TextView			mButtonTv;
	}

}
