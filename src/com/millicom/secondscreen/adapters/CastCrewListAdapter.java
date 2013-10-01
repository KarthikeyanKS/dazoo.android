package com.millicom.secondscreen.adapters;

import java.util.ArrayList;

import com.millicom.secondscreen.R;
import com.millicom.secondscreen.adapters.LikesListAdapter.ViewHolder;
import com.millicom.secondscreen.content.model.Cast;
import com.millicom.secondscreen.utilities.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CastCrewListAdapter extends BaseAdapter{

	private Activity mActivity;
	private ArrayList<Cast> mCast;
	private ImageLoader mImageLoader;
	private LayoutInflater mLayoutInflater;
	
	public CastCrewListAdapter(Activity activity, ArrayList<Cast> cast){
		this.mActivity = activity;
		this.mCast = cast;
		this.mImageLoader = new ImageLoader(mActivity, R.drawable.loadimage);
	}
	
	@Override
	public int getCount() {
		if(mCast !=null){
			return mCast.size();
		} else return 0;
	}

	@Override
	public Cast getItem(int position) {
		if (mCast != null){
			return mCast.get(position);
		} else return null;
	}

	@Override
	public long getItemId(int arg0) {
		return -1;
	}

	@Override
	public View getView(int  position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewHolder viewHolder = new ViewHolder();
			rowView = mLayoutInflater.inflate(R.layout.row_block_cast_and_crew_list, null);
			viewHolder.poster = (ImageView) rowView.findViewById(R.id.row_block_cast_and_crew_list_iv);
			viewHolder.posterProgressBar = (ProgressBar) rowView.findViewById(R.id.row_block_cast_and_crew_list_progressbar);
			viewHolder.actorName = (TextView) rowView.findViewById(R.id.row_block_cast_and_crew_list_name_tv);
			viewHolder.characterName = (TextView) rowView.findViewById(R.id.row_block_cast_and_crew_list_details_character_name_tv);
			
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		Cast cast = getItem(position);
		
		mImageLoader.displayImage(cast.getPosterUrl(), holder.poster, holder.posterProgressBar, ImageLoader.IMAGE_TYPE.POSTER);
		holder.actorName.setText(cast.getActorName());
		holder.characterName.setText(cast.getCharacterName());

		
		return rowView;
	}

	static class ViewHolder{
		ImageView poster;
		ProgressBar posterProgressBar;
		TextView actorName;
		TextView characterName;
	}
	
}
