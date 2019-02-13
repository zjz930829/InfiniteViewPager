package com.example.zjz.infiniteviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;


/**
 * Created by ZJZ on 27/12/17.
 */

public class InfiniteViewPagerAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    List<String > lists;
    Context context;
    private int currentId;

    public InfiniteViewPagerAdapter(Context context, int currentId) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.currentId = currentId;
    }

    public void setLists(List<String> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final String stage = lists.get(position);
        View view = inflater.inflate(R.layout.item_infinite_viewpager, container, false);
        TextView text =  view.findViewById(R.id.text);
        text.setText(stage);
        ((ViewPager) container).addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == object;
    }


}
