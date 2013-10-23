package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.content.model.DazooLikeEntity;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.like.LikeDialogHandler;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
	private ArrayList<DazooLike>	mLikes;

	private ImageLoader			mImageLoader;
	private String mToken;
	private int currentPosition = -1;

	public LikesListAdapter(Activity activity, ArrayList<DazooLike> likes, String token) {
		this.mLikes = likes;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(activity, R.drawable.loadimage);
		this.mToken = token;
	}

	@Override
	public int getCount() {
		if (mLikes != null) {
			return mLikes.size();
		} else return 0;
	}

	@Override
	public DazooLike getItem(int position) {
		if (mLikes != null) {
			return mLikes.get(position);
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
			viewHolder.mButtonContainer.setTag(Integer.valueOf(position));
			Log.d(TAG,"set tag: " + Integer.valueOf(position));
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		final DazooLike like = getItem(position);
		if (like != null) {
			final DazooLikeEntity entity = like.getEntity();
			holder.mProgramTitleTv.setText(entity.getTitle());
			holder.mProgramTypeTv.setText(entity.getEntityType());
			mImageLoader.displayImage(entity.getPosterMUrl(), holder.mProgramIv, ImageLoader.IMAGE_TYPE.THUMBNAIL);
			
			holder.mButtonContainer.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					currentPosition = (Integer) v.getTag();
					LikeDialogHandler likeDlg = new LikeDialogHandler();
					likeDlg.showRemoveLikeDialog(mActivity, mToken, entity.getEntityId(), yesProc(), noProc());
					
				}
			});
		}

		return rowView;
	}

	public static class ViewHolder {
		public ImageView		mProgramIv;
		public TextView			mProgramTitleTv;
		public TextView			mProgramTypeTv;
		public RelativeLayout	mButtonContainer;
		public ImageView		mButtonIcon;
	}
	
	public Runnable yesProc() {
		return new Runnable() {
			public void run() {
				mLikes.remove(currentPosition);
				notifyDataSetChanged();
			}
		};
	}

	public Runnable noProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

}
