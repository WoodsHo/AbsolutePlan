package com.woodsho.absoluteplan.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hewuzhao on 17/12/24.
 */

public class OpenSource implements Parcelable {
    public String name;
    public String author;
    public String describe;
    public String link;

    public OpenSource() {}

    public OpenSource(String name, String author, String describe, String link) {
        this.name = name;
        this.author = author;
        this.describe = describe;
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcelable(Parcel in) {
        name = in.readString();
        author = in.readString();
        describe = in.readString();
        link = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(describe);
        dest.writeString(link);
    }

    public static final Creator<OpenSource> CREATOR = new Creator<OpenSource>() {
        @Override
        public OpenSource createFromParcel(Parcel source) {
            OpenSource openSource = new OpenSource();
            openSource.readFromParcelable(source);
            return openSource;
        }

        @Override
        public OpenSource[] newArray(int size) {
            return new OpenSource[size];
        }
    };
}
