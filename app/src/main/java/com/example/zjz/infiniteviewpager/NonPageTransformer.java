package com.example.zjz.infiniteviewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by ZJZ on 27/12/17.
 */

public class NonPageTransformer implements ViewPager.PageTransformer
{
    @Override
    public void transformPage(View page, float position)
    {
        page.setScaleX(0.999f);//hack
    }

    public static final ViewPager.PageTransformer INSTANCE = new NonPageTransformer();
}