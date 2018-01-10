package com.woodsho.absoluteplan.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by hewuzhao on 17/12/9.
 */

public class PlanTask implements Parcelable {
    public String id;
    public int priority;
    public String title;
    public String describe;
    public long time;
    public int state; //0: normal; 1: finished

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcelable(Parcel in) {
        priority = in.readInt();
        title = in.readString();
        describe = in.readString();
        time = in.readLong();
        state = in.readInt();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(priority);
        dest.writeString(title);
        dest.writeString(describe);
        dest.writeLong(time);
        dest.writeInt(state);
        dest.writeString(id);
    }

    public static final Creator<PlanTask> CREATOR = new Creator<PlanTask>() {
        @Override
        public PlanTask createFromParcel(Parcel in) {
            PlanTask planTask = new PlanTask();
            planTask.readFromParcelable(in);
            return planTask;
        }

        @Override
        public PlanTask[] newArray(int size) {
            return new PlanTask[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlanTask) {
            PlanTask task = (PlanTask) obj;
            if (!TextUtils.equals(id, task.id))
                return false;

            if (priority != task.priority)
                return false;

            if (!TextUtils.equals(title, task.title))
                return false;

            if (!TextUtils.equals(describe, task.describe))
                return false;

            if (time != task.time)
                return false;

            if (state != task.state)
                return false;

            return true;
        }
        return super.equals(obj);
    }
}
