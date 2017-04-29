package com.stylingandroid.viewpageranimator.sample.adapter;

import java.io.Serializable;

class PagerItem implements Serializable {
    private final String title;
    private final int colour;

    PagerItem(String title, int colour) {
        this.title = title;
        this.colour = colour;
    }

    String getTitle() {
        return title;
    }

    int getColour() {
        return colour;
    }
}
