package com.woodsho.absoluteplan.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class SideItem implements Parcelable {
    public int id;
    public String iconId;
    public String title;
    public int count;

    public SideItem() {}

    public SideItem(int id, String iconId, String title, int count) {
        this.id = id;
        this.iconId = iconId;
        this.title = title;
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcelable(Parcel in) {
        id = in.readInt();
        iconId = in.readString();
        title = in.readString();
        count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(iconId);
        dest.writeString(title);
        dest.writeInt(count);
    }

    public static final Creator<SideItem> CREATOR = new Creator<SideItem>() {
        @Override
        public SideItem createFromParcel(Parcel source) {
            SideItem sideItem = new SideItem();
            sideItem.readFromParcelable(source);
            return sideItem;
        }

        @Override
        public SideItem[] newArray(int size) {
            return new SideItem[size];
        }
    };
}
