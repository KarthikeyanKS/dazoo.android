package com.mitv.adapters;

import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.Consts;
import com.mitv.R;
import com.mitv.like.LikeDialogHandler;
import com.mitv.model.TVLike;
import com.mitv.model.MiTVLikeEntity;
import com.mitv.myprofile.LikesCountInterface;
import com.mitv.storage.MiTVStore;
import com.mitv.tvguide.BroadcastPageActivity;

@SuppressLint("DefaultLocale")
public class LikesListAdapter extends BaseAdapter {

	private static final String		TAG				= "LikesListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<TVLike>	mLikes;
	private LikesCountInterface		mInterface;
	private String					mToken, mLikeIdToRemove, mLikeId;
	private int						currentPosition	= -1;

	public LikesListAdapter(Activity activity, ArrayList<TVLike> likes, String token, LikesCountInterface likesInterface) {
		this.mLikes = likes;
		this.mActivity = activity;
		this.mToken = token;
		this.mInterface = likesInterface;
	}

	@Override
	public int getCount() {
		if (mLikes != null) {
			return mLikes.size();
		} else return 0;
	}

	@Override
	public TVLike getItem(int position) {
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

		final TVLike like = getItem(position);

		if (like != null) {
			final MiTVLikeEntity entity = like.getEntity();
			if (entity != null) {

				/*Disabled headers for now*/
//				holder.mHeaderContainer.setVisibility(View.GONE);
//				holder.mDividerView.setVisibility(View.VISIBLE);
//				// Logic to show header with first character
//				if (position == 0 || entity.getTitle().toUpperCase().charAt(0) != getItem(position - 1).getEntity().getTitle().toUpperCase().charAt(0)) {
//					holder.mHeaderContainer.setVisibility(View.VISIBLE);
//					holder.mHeaderTv.setText("" + entity.getTitle().toUpperCase().charAt(0));
//
//				}
//				if (position != (getCount() - 1) && entity.getTitle().toUpperCase().charAt(0) != getItem(position + 1).getEntity().getTitle().toUpperCase().charAt(0)) {
//					holder.mDividerView.setVisibility(View.GONE);
//				}

				holder.mProgramTitleTv.setText(entity.getTitle());
				// Set appropriate description depending on program type
				String likeType = like.getLikeType();
				if (Consts.LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
					holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.sport));
				} else if (Consts.LIKE_TYPE_SERIES.equals(likeType)) {
					if (entity.getYear() != 0) {
						holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.tv_series) + " " + entity.getYear() + "-");
					} else {
						holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.tv_series));
					}
				} else if (Consts.LIKE_TYPE_PROGRAM.equals(likeType)) {
					if (Consts.LIKE_PROGRAM_PROGRAM_TYPE_MOVIE.equals(entity.getProgramType())) {
						holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.movie) + " " + entity.getYear());
					} else if (Consts.LIKE_PROGRAM_PROGRAM_TYPE_OTHER.equals(entity.getProgramType())) {
						holder.mProgramTypeTv.setText(entity.getCategory());
					}
				}

				// Navigate to the next broadcast
//				holder.mInformationContainer.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Log.d(TAG, "Channelid: " + like.getNextBroadcastChannelId() + " Begintimemillis: " + like.getNextBroadcastBegintimeMillis());
//						if (like.getNextBroadcastChannelId() != null && like.getNextBroadcastBegintimeMillis() != 0) {
//							String broadcastUrl = Consts.URL_NOTIFY_BROADCAST_PREFIX + like.getNextBroadcastChannelId() + Consts.NOTIFY_BROADCAST_URL_MIDDLE + like.getNextBroadcastBegintimeMillis();
//							Intent intent = new Intent(mActivity, BroadcastPageActivity.class);
//							intent.putExtra(Consts.INTENT_EXTRA_CHANNEL_ID, like.getNextBroadcastChannelId());
//							intent.putExtra(Consts.INTENT_EXTRA_BROADCAST_URL, broadcastUrl);
//							intent.putExtra(Consts.INTENT_EXTRA_FROM_NOTIFICATION, true);
//							intent.putExtra(Consts.INTENT_EXTRA_FROM_PROFILE, true);
//							mActivity.startActivity(intent);
//						}
//					}
//				});

				holder.mButtonContainer.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						currentPosition = (Integer) v.getTag();

						String likeType = like.getLikeType();
						String likeId = null;
						if (Consts.LIKE_TYPE_SERIES.equals(likeType)) {
							mLikeId = like.getEntity().getSeriesId();
						} else if (Consts.LIKE_TYPE_PROGRAM.equals(likeType)) {
							mLikeId = like.getEntity().getProgramId();
						} else if (Consts.LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
							mLikeId = like.getEntity().getSportTypeId();
						}

						LikeDialogHandler likeDlg = new LikeDialogHandler();
						likeDlg.showRemoveLikeDialog(mActivity, mLikeId, likeType, yesProc(), noProc());

					}
				});
			}
		}

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
