package com.millicom.secondscreen.adapters;

import java.util.ArrayList;
import java.util.Locale;

import com.millicom.secondscreen.Consts;
import com.millicom.secondscreen.R;
import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.DazooLike;
import com.millicom.secondscreen.content.model.DazooLikeEntity;
import com.millicom.secondscreen.content.model.Program;
import com.millicom.secondscreen.content.myprofile.LikesCountInterface;
import com.millicom.secondscreen.content.tvguide.BroadcastPageActivity;
import com.millicom.secondscreen.content.tvguide.ChannelPageActivity;
import com.millicom.secondscreen.like.LikeDialogHandler;
import com.millicom.secondscreen.storage.DazooStore;
import com.millicom.secondscreen.utilities.ImageLoader;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class LikesListAdapter extends BaseAdapter {

	private static final String		TAG				= "LikesListAdapter";

	private LayoutInflater			mLayoutInflater;
	private Activity				mActivity;
	private ArrayList<DazooLike>	mLikes;
	private LikesCountInterface		mInterface;
	private ImageLoader				mImageLoader;
	private String					mToken;
	private int						currentPosition	= -1;

	public LikesListAdapter(Activity activity, ArrayList<DazooLike> likes, String token, LikesCountInterface likesInterface) {
		this.mLikes = likes;
		this.mActivity = activity;
		this.mImageLoader = new ImageLoader(activity, R.color.white);
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
			viewHolder.mHeaderContainer = (LinearLayout) rowView.findViewById(R.id.row_likes_header_container);
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

		final DazooLike like = getItem(position);
		if (like != null) {
			final DazooLikeEntity entity = like.getEntity();
			if (entity != null) {

				holder.mHeaderContainer.setVisibility(View.GONE);
				holder.mDividerView.setVisibility(View.VISIBLE);
				// Logic to show header with first character
				if (position == 0 || entity.getTitle().toUpperCase().charAt(0) != getItem(position - 1).getEntity().getTitle().toUpperCase().charAt(0)) {
					holder.mHeaderContainer.setVisibility(View.VISIBLE);
					holder.mHeaderTv.setText("" + entity.getTitle().toUpperCase().charAt(0));

				}
				if (position != (getCount() - 1) && entity.getTitle().toUpperCase().charAt(0) != getItem(position + 1).getEntity().getTitle().toUpperCase().charAt(0)) {
					holder.mDividerView.setVisibility(View.GONE);
				}

				holder.mProgramTitleTv.setText(entity.getTitle());
				// Set appropriate description depending on program type
				String likeType = like.getLikeType();
				if (Consts.DAZOO_LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
					holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.sport));
				} else if (Consts.DAZOO_LIKE_TYPE_SERIES.equals(likeType)) {
					if(entity.getYear()!=0){
					holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.tv_series) + " " + entity.getYear() + "-");
					} else {
						holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.tv_series));
					}
				} else if (Consts.DAZOO_LIKE_TYPE_PROGRAM.equals(likeType)) {
					if (Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_MOVIE.equals(entity.getProgramType())) {
						holder.mProgramTypeTv.setText(mActivity.getResources().getString(R.string.movie) + " " + entity.getYear());
					} else if (Consts.DAZOO_LIKE_PROGRAM_PROGRAM_TYPE_OTHER.equals(entity.getProgramType())) {
						holder.mProgramTypeTv.setText(entity.getCategory());
					}
				}

				holder.mInformationContainer.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});

				holder.mButtonContainer.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						currentPosition = (Integer) v.getTag();
						String likeType = like.getLikeType();
						String likeId = null;
						if (Consts.DAZOO_LIKE_TYPE_SERIES.equals(likeType)) {
							likeId = like.getEntity().getSeriesId();
						} else if (Consts.DAZOO_LIKE_TYPE_PROGRAM.equals(likeType)) {
							likeId = like.getEntity().getProgramId();
						} else if (Consts.DAZOO_LIKE_TYPE_SPORT_TYPE.equals(likeType)) {
							likeId = like.getEntity().getSportTypeId();
						}

						LikeDialogHandler likeDlg = new LikeDialogHandler();
						likeDlg.showRemoveLikeDialog(mActivity, mToken, likeId, likeType, yesProc(), noProc());

					}
				});
			}
		}

		return rowView;
	}

	public static class ViewHolder {
		public LinearLayout		mHeaderContainer;
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
				mLikes.remove(currentPosition);
				
				// remove like from the DazooStore
				DazooStore.getInstance().getLikeIds().remove(currentPosition);
				
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

}
