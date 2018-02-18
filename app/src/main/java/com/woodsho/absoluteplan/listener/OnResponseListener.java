package com.woodsho.absoluteplan.listener;

/**
 * Created by hewuzhao on 18/2/15.
 */

public interface OnResponseListener {
    void onSuccess();

    void onCancel();

    void onFail(String message);
}
