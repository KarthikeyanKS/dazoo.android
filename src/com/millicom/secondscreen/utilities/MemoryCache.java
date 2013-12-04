package com.millicom.secondscreen.utilities;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

    private static Map<String, Bitmap> cache            = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true)); // Last argument true for LRU ordering
    private static long                currentSize      = 0;                                                                             // current allocated size
    private static long                memoryCacheLimit = 1000000;                                                                       // max memory in bytes

    public MemoryCache() {
        setLimit(Runtime.getRuntime().maxMemory() / 4); // use 25% of available heap size

    }

    public void setLimit(long new_limit) {
        memoryCacheLimit = new_limit;
    }

    public Bitmap get(String id) {
        try {
            if (!cache.containsKey(id)) {
            	return null;
            }
            
            return cache.get(id);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void put(String id, Bitmap bitmap) {
        try {
            if (cache.containsKey(id)) currentSize -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            currentSize += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void checkSize() {
        // If size is to big or limit is over 75, clear array
        if (currentSize > memoryCacheLimit || cache.size() > 75) {
            clear();
            currentSize = 0;
        }
    }

    public void clear() {
        cache.clear();
    }

    long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null) return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}