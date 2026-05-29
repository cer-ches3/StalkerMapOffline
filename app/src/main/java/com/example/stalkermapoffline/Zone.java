package com.example.stalkermapoffline;

import android.graphics.RectF;

public class Zone {
    public String name;
    public RectF bounds;

    public Zone(String name, float left, float top, float right, float bottom) {
        this.name = name;
        this.bounds = new RectF(left, top, right, bottom);
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x,y);
    }
}
