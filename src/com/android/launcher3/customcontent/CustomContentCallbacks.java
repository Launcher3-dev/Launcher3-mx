package com.android.launcher3.customcontent;

public interface CustomContentCallbacks {

    // Custom content is completely shown. {@code fromResume} indicates whether this was caused
    // by a onResume or by scrolling otherwise.
    void onShow(boolean fromResume);

    // Custom content is completely hidden
    void onHide();

    // Custom content scroll progress changed. From 0 (not showing) to 1 (fully showing).
    void onScrollProgressChanged(float progress);

    // Indicates whether the user is allowed to scroll away from the custom content.
    boolean isScrollingAllowed();

}
