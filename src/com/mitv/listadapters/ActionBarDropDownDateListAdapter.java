
package com.mitv.listadapters;



import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.managers.FontManager;
import com.mitv.models.objects.mitvapi.TVDate;
import com.mitv.utilities.DateUtils;



public class ActionBarDropDownDateListAdapter 
extends BaseAdapter 
implements SpinnerAdapter 
{
	@SuppressWarnings("unused")
	private static final String TAG = ActionBarDropDownDateListAdapter.class.getName();


	private static final int NO_SELECTION = -1;

	private Context context;
	private List<TVDate> tvDates;

	private int selectedIndex = NO_SELECTION;



	public ActionBarDropDownDateListAdapter(Context context, List<TVDate> mDays) 
	{
		this.context = context;
		this.tvDates = mDays;
	}



	@Override
	public int getCount() 
	{
		int count = 0;

		if (tvDates != null) 
		{
			count = tvDates.size();
		}
		return count;
	}



	@Override
	public TVDate getItem(int position) 
	{
		TVDate tvDate = null;

		if (tvDates != null) 
		{
			tvDate = tvDates.get(position);
		}

		return tvDate;
	}



	@Override
	public long getItemId(int position) 
	{
		return -1;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		return getViewForHeaderOrRow(position, convertView, parent, true);
	}



	private View getViewForHeaderOrRow(int position, View convertView, ViewGroup parent, boolean isHeader)
	{
		View row = convertView;

		if (row == null) 
		{
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (isHeader) 
			{
				row = inflater.inflate(R.layout.actionbar_dropdown_list_date_header, parent, false);
			} 
			else 
			{
				row = inflater.inflate(R.layout.actionbar_dropdown_list_date_item, parent, false);
			}
		}

		TextView dayName;
		TextView dayAndMonth = null;
		View lastDividerDropdownDates;

		if (isHeader) 
		{
			dayName = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_header_name);
		} 
		else
		{
			dayName = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_item_name);
			dayAndMonth = (TextView) row.findViewById(R.id.layout_actionbar_dropdown_list_date_item_number);

			lastDividerDropdownDates = (View) row.findViewById(R.id.last_divider_dropdown_dates);

			if (position == 6) 
			{
				lastDividerDropdownDates.setVisibility(View.VISIBLE);
			}

			/* This check is needed, otherwise the divider will appear in the first element in the list */
			if (position == 0) 
			{
				lastDividerDropdownDates.setVisibility(View.GONE);
			}

			/* Make the selected text position bold */
			if (position == selectedIndex) 
			{
				Typeface bold = FontManager.getFontBold(context);
				dayName.setTypeface(bold);
			}
		}

		if (selectedIndex != NO_SELECTION) 
		{
			TVDate tvDate = getItem(position);

			try 
			{
				Calendar calendar = tvDate.getStartOfTVDayCalendar();
				if (isHeader) 
				{
					StringBuilder sb = new StringBuilder();
					sb.append(tvDate.getDisplayName());
					sb.append(" ");
					sb.append(DateUtils.buildDayAndMonthCompositionAsString(calendar, false));
					dayName.setText(sb.toString());
				}
				else 
				{
					dayName.setText(tvDate.getDisplayName());
					dayAndMonth.setText(DateUtils.buildDayAndMonthCompositionAsString(calendar, false));
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				dayName.setText("");
				if (isHeader == false) 
				{
					dayAndMonth.setText("");
				}
			}

			dayName.setVisibility(View.VISIBLE);
			if (isHeader == false) 
			{
				dayAndMonth.setVisibility(View.VISIBLE);
			}

		} 
		else 
		{
			dayName.setVisibility(View.GONE);
			if (isHeader == false) 
			{
				dayAndMonth.setVisibility(View.GONE);
			}
		}

		return row;
	}



	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) 
	{
		return getViewForHeaderOrRow(position, convertView, parent, false);
	}



	public void setSelectedIndex(int index) 
	{
		this.selectedIndex = index;
	}
}
