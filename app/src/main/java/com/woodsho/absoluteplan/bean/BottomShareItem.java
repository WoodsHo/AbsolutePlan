package com.woodsho.absoluteplan.bean;

/**
 * Created by hewuzhao on 18/2/16.
 */

public class BottomShareItem {
    private int id;
    private String title;
    private int icon;

    public BottomShareItem() {
    }

    public BottomShareItem(int id, String title, int icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
