package com.mitv.utilities;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ResetViewImageloader extends ImageLoader {

    private volatile static ResetViewImageloader instance;

    /** Returns singleton class instance */
    public static ResetViewImageloader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ResetViewImageloader();
                }
            }
        }
        return instance;
    }
}
