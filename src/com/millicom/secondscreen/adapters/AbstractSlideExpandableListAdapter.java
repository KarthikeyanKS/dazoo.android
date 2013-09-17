package com.millicom.secondscreen.adapters;

import com.millicom.secondscreen.customviews.ExpandCollapseAnimation;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Wraps a ListAdapter to give it expandable list view functionality.
 * The main thing it does is add a listener to the getToggleButton
 * which expands the getExpandableView for each list item.
 *
 * @author Slickelito
 */
public abstract class AbstractSlideExpandableListAdapter implements ListAdapter {
    private ListAdapter wrapped;
    private static final int COLLAPSED = -100;
    private int expandedViewIndex = COLLAPSED;
    private ListView listView;
    private static final int ANIMATION_SPEED = 300;

    public AbstractSlideExpandableListAdapter(ListAdapter wrapped) {
        this.wrapped = wrapped;
    }

    public AbstractSlideExpandableListAdapter(ListAdapter wrapped, ListView listView) {
        this.wrapped = wrapped;
        this.listView = listView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return wrapped.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int i) {
        return wrapped.isEnabled(i);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        wrapped.registerDataSetObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        wrapped.unregisterDataSetObserver(dataSetObserver);
    }

    @Override
    public int getCount() {
        return wrapped.getCount();
    }

    @Override
    public Object getItem(int i) {
        return wrapped.getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return wrapped.getItemId(i);
    }

    @Override
    public boolean hasStableIds() {
        return wrapped.hasStableIds();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = wrapped.getView(position, view, viewGroup);
        enableFor(view, position);
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return wrapped.getItemViewType(i);
    }

    @Override
    public int getViewTypeCount() {
        return wrapped.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }


    /**
     * This method is used to get the Button view that should
     * expand or collapse the Expandable View.
     * <br/>
     * Normally it will be implemented as:
     * <pre>
     * return (Button)parent.findViewById(R.id.expand_toggle_button)
     * </pre>
     *
     * A listener will be attached to the button which will
     * either expand or collapse the expandable view
     *
     * @see getExpandableView
     * @param parent the list view item
     * @return a child of parent which is a button
     */
    public abstract RelativeLayout getExpandToggleButton(View parent);

    /**
     * This method is used to get the view that will be hidden
     * initially and expands or collapse when the ExpandToggleButton
     * is pressed @see getExpandToggleButton
     * <br/>
     * Normally it will be implemented as:
     * <pre>
     * return parent.findViewById(R.id.expandable)
     * </pre>
     *
     * @see getExpandToggleButton
     * @param parent the list view item
     * @return a child of parent which is a view (or often ViewGroup)
     *  that can be collapsed and expanded
     */
    
    private static View lastOpen = null;
    
    public abstract View getExpandableView(View parent);

    public void enableFor(View parent, int position) {
        RelativeLayout more = getExpandToggleButton(parent);
        View itemToolbar = getExpandableView(parent);
        enableFor(more, itemToolbar, position);
    }

    //This is a bit messy but it works 
    public void enableFor(View relativeLayout, final View target, final int pos) {

        //Last expanded Index (position)
        if(expandedViewIndex == pos){
        	
            target.setVisibility(View.VISIBLE);
            target.requestLayout();

            //Last open View
            lastOpen = target;
            
        }else{
            target.setVisibility(View.GONE);			
        }

        //Standard Listener
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
                //Keeps user from spamming a button - don't allow click while under animation
                if(view.getAnimation() == null){

                    //Remove old animations
                    view.setAnimation(null);
                    int type;
                    
                    if(target.getVisibility() == View.VISIBLE){
                        
                    	type = ExpandCollapseAnimation.COLLAPSE;
                        expandedViewIndex = COLLAPSED;
                        
                    }else{
                    	
                        type = ExpandCollapseAnimation.EXPAND;
                        expandedViewIndex = pos;
                    }
                    
                    Animation anim = new ExpandCollapseAnimation(target, ANIMATION_SPEED, type);

                    if(type == ExpandCollapseAnimation.EXPAND) {
                        
                    	if(lastOpen != null && lastOpen != target && lastOpen.getVisibility() == View.VISIBLE) {
                            lastOpen.startAnimation(new ExpandCollapseAnimation(lastOpen, ANIMATION_SPEED, ExpandCollapseAnimation.COLLAPSE));
                        }
                        
                        lastOpen = target;
                        
                    } else if(lastOpen == view) {
                        lastOpen = null;
                    }

                    // Hax
                    if(type == ExpandCollapseAnimation.EXPAND && pos == (wrapped.getCount() - 1)){
                        anim.setDuration(5);
                        anim.setAnimationListener(new AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                listView.setSelection((wrapped.getCount() -1));
                            }
                        });
                    }
                    view.startAnimation(anim);
                }
            }
        });
    }


}
