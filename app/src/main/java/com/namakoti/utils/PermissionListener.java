package com.namakoti.utils;

public interface PermissionListener {
    void onPermissionAccepted(int code);

    void onPermissionDenied(int code);

}