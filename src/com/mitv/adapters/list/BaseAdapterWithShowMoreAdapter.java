
package com.mitv.adapters.list;



import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mitv.R;



public abstract class BaseAdapterWithShowMoreAdapter 
	extends BaseAdapter
{	
	@SuppressWarnings("unused")
	private static final String TAG = BaseAdapterWithShowMoreAdapter.class.getName();

	
	protected LayoutInflater layoutInflater;
	protected Activity activity;
	protected boolean enableMoreViewAtBottom;
	
	private String viewBottomMessage;
	private Runnable viewBottomConfirmProcedure;
	
	
	
	public BaseAdapterWithShowMoreAdapter(
			final Activity activity,
			final boolean enableMoreViewAtBottom,
			final String viewBottomMessage,
			final Runnable viewBottomConfirmProcedure)
	{
		super();
		
		this.activity = activity;

		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.enableMoreViewAtBottom = enableMoreViewAtBottom;
		
		this.viewBottomMessage = viewBottomMessage;
		
		this.viewBottomConfirmProcedure = viewBottomConfirmProcedure;
	}
	
	
	
	@Override
	public int getCount()
	{
		int count = 0;
		
		if(enableMoreViewAtBottom)
		{
			count++;
		}
		
		return count;
	}
	
	
	
	@Override
	public int getViewTypeCount()
	{
		if(enableMoreViewAtBottom)
		{
			return 2;
		}
		else
		{
			return 1;
		}
	}
	
	
	
	protected boolean isShowMoreView(int position)
	{
		boolean isShowMoreView = false;
		
		if(enableMoreViewAtBottom)
		{
			isShowMoreView = (position == getCount()-1);
		}
		
		return isShowMoreView;
	}
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		if (rowView == null)
		{	
			rowView = layoutInflater.inflate(R.layout.row_show_more_list_item, null);
				
			ViewHolderMoreItems viewHolder = new ViewHolderMoreItems();
				
			viewHolder.show_more = (TextView) rowView.findViewById(R.id.show_more);
			viewHolder.containerLayout = (RelativeLayout) rowView.findViewById(R.id.show_more_container_layout);
			
			rowView.setTag(viewHolder);
		}

		final ViewHolderMoreItems holder = (ViewHolderMoreItems) rowView.getTag();
			
		if (holder != null)
		{
			holder.show_more.setText(viewBottomMessage);
			
			holder.containerLayout.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					if(viewBottomConfirmProcedure != null)
					{
						viewBottomConfirmProcedure.run();
					}
				}
			}); 
		}
		
		return rowView;
	}
	
	
	
	
	private static class ViewHolderMoreItems
	{
		private RelativeLayout containerLayout;
		private TextView show_more;
	}
}
