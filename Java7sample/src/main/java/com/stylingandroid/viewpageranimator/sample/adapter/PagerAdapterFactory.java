package com.stylingandroid.viewpageranimator.sample.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapterFactory {
    private final Context context;

    public PagerAdapterFactory(Context context) {
        this.context = context;
    }

    public PagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
        List<PagerItem> items = new ArrayList<>();
        items.add(new PagerItem("Item 1", Color.RED));
        items.add(new PagerItem("Item 2", Color.GREEN));
        items.add(new PagerItem("Item 3", Color.BLUE));
        return new PagerAdapter(fragmentManager, context, items);
    }
}
