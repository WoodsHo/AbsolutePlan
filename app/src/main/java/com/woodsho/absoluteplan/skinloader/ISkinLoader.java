package com.woodsho.absoluteplan.skinloader;

/**
 * Created by hewuzhao on 18/1/18.
 */

public interface ISkinLoader {
    void attach(ISkinUpdate observer);

    void detach(ISkinUpdate observer);

    void notifySkinUpdate();
}
