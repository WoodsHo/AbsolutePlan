package com.woodsho.absoluteplan.listener;

/**
 * Created by hewuzhao on 18/1/26.
 */

public interface PermissionListener {
    /**
     * 设置请求权限请求码
     */
    int getPermissionsRequestCode();

    /**
     * 设置需要请求的权限
     */
    String[] getPermissions();

    /**
     * 请求权限成功回调
     */
    void requestPermissionsSuccess();

    /**
     * 请求权限失败回调
     */
    void requestPermissionsFailed();
}
