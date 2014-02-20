package com.mitv.adapters;

import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.SuppressLint;
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

import com.millicom.mitv.models.gson.UserLike;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.handlers.LikeDialogHandler;
import com.mitv.interfaces.LikesCountInterface;
import com.mitv.model.OldMiTVLikeEntity;
import com.mitv.storage.MiTVStore;

@SuppressLint("DefaultLocale")
public class LikesListAdapter extends BaseAdapter {

	private static final String		TAG				= "LikesListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<UserLike>	mLikes;
	private LikesCountInterface		mInterface;
	private String					mToken, mLikeIdToRemove, mLikeId;
	private int						currentPosition	= -1;

	public LikesListAdapter(Activity activity, ArrayList<UserLike> likes, LikesCountInterface likesInterface) {
		this.mLikes = likes;
		this.mActivity = activity;
//		this.mToken = token;
		this.mInterface = likesInterface;
	}

	@Override
	public int getCount() {
		if (mLikes != null) {
			return mLikes.size();
		} else return 0;
	}

	@Override
	public UserLike getItem(int position) {
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
			viewHolder.mHeaderContainer = (RelativeLayout) rowView.findViewById(R.id.row_likes_header_container);
			viewHolder.mHeaderTv = (TextView) rowView.findViewById(R.id.row_likes_header_textview);
			viewHolder.mInformationContainer = (RelativeLayout) rowView.findViewById(R.id.row_likes_text_container);
			viewHolder.mProgramTitleTv = (TextView) rowView.findViewById(R.id.row_likes_text_title_tv);
			viewHolder.mProgramTypeTv = (TextView) rowView.findViewById(R.id.row_likes_text_details_tv);
			viewHolder.mButtonContainer = (RelativeLayout) rowView.findViewById(R.id.row_likes_button_container);
			viewHolder.mButtonIcon = (ImageView) rowView.findViewById(R.id.row_likes_button_iv);
			viewHolder.mButtonContainer.setTag(Integer.valueOf(position));
			viewHolder.mDividerView = (View) rowView.findViewById(R.id.row_likes_header_divider);
			Log.d(TAG, "set tag: " + Integer.valueOf(position));
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		final UserLike like = getItem(position);

		//TODO implement this
//		if (like != null) {
//			final OldMiTVLikeEntity entity = like.getEntity();
//			if (entity != null) {
//
//				holder.mProgramTitleTv.setText(entity.getTitle());
//				// Set appropriate description depending on program type
//				String likeType = like.getLikeType();
//				if (Consts.LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
//					holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.sport));
//				} else if (Consts.LIKE_TYPE_SERIES.equals(likeType)) {
//					if (entity.getYear() != 0) {
//						holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.tv_series) + " " + entity.getYear() + "-");
//					} else {
//						holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.tv_series));
//					}
//				} else if (Consts.LIKE_TYPE_PROGRAM.equals(likeType)) {
//					if (Consts.LIKE_PROGRAM_PROGRAM_TYPE_MOVIE.equals(entity.getProgramType())) {
//						holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.movie) + " " + entity.getYear());
//					} else if (Consts.LIKE_PROGRAM_PROGRAM_TYPE_OTHER.equals(entity.getProgramType())) {
//						holder.mProgramTypeTv.setText(entity.getCategory());
//					}
//				}
//
//				holder.mButtonContainer.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						currentPosition = (Integer) v.getTag();
//
//						String likeType = like.getLikeType();
//						String likeId = null;
//						if (Consts.LIKE_TYPE_SERIES.equals(likeType)) {
//							mLikeId = like.getEntity().getSeriesId();
//						} else if (Consts.LIKE_TYPE_PROGRAM.equals(likeType)) {
//							mLikeId = like.getEntity().getProgramId();
//						} else if (Consts.LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
//							mLikeId = like.getEntity().getSportTypeId();
//						}
//
//						LikeDialogHandler likeDlg = new LikeDialogHandler();
//						likeDlg.showRemoveLikeDialog(mActivity, mLikeId, likeType, yesProc(), noProc());
//
//					}
//				});
//			}
//		}

		return rowView;
	}

	public static class ViewHolder {
		public RelativeLayout		mHeaderContainer;
		public TextView			mHeaderTv;
		public RelativeLayout	mInformationContainer;
		public TextView			mProgramTitleTv;
		public TextView			mProgramTypeTv;
		public RelativeLayout	mButtonContainer;
		public ImageView		mButtonIcon;
		public View				mDividerView;
	}

	public Runnable yesProc() {
		return new Runnable() {
			public void run() {
				if (mLikes.size() > currentPosition) {
					mLikes.remove(currentPosition);
				}
			
				removeLikeId();
				mInterface.setCount(mLikes.size());
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

	private void removeLikeId() {

		Iterator iterator = MiTVStore.getInstance().getLikeIds().iterator();
		String strElement = "";
		while (iterator.hasNext()) {
			strElement = (String) iterator.next();
			if (strElement.equals(mLikeId)) {
				iterator.remove();
			}
		}
	}

}
