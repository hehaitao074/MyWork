
package com.storm.fliplayout.helper;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * 图片缓存
 * 
 * @author WangYunzhen
 */
public class ImageMemoryCache {
    
    private static final String TAG = "ImageMemoryCache";

    private final int CAHCHE_SIZE = 32;

    private static LruCache<String, Bitmap> mLruCache;

    private LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache;

    private static ImageMemoryCache sImageMemoryCache;

    public static ImageMemoryCache getInstance() {
        if (sImageMemoryCache == null) {
            synchronized (ImageMemoryCache.class) {
                ImageMemoryCache instance = sImageMemoryCache;
                if (instance == null) {
                    synchronized (ImageMemoryCache.class) {
                        sImageMemoryCache = new ImageMemoryCache();
                    }
                }
            }
        }
        return sImageMemoryCache;
    }

    private ImageMemoryCache() {
        int memoryCacheSize = (int)(Runtime.getRuntime().maxMemory() / 16);
        mLruCache = new LruCache<String, Bitmap>(memoryCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (value != null)
                    return value.getRowBytes() * value.getHeight();
                else
                    return 0;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue,
                    Bitmap newValue) {
                if (oldValue != null) {
                    mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
                }
            }
        };
        mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(50, 0.75f, true) {

            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(
                    java.util.Map.Entry<String, SoftReference<Bitmap>> eldest) {
                if (size() > CAHCHE_SIZE) {
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 添加图片到缓存
     */
    public void addBitmapToMemory(String key, Bitmap bitmap) {
        if (bitmap == null)
            return;
        synchronized (mLruCache) {
            Log.d(TAG, "addBitmapToMemory");
            mLruCache.put(key, bitmap);
        }
    }

    /**
     * 根据key从缓存读取图片
     * 
     * @param key
     * @return
     */
    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = null;
        synchronized (mLruCache) {
            bitmap = mLruCache.get(key);
            if (bitmap != null) {
                mLruCache.remove(key);
                mLruCache.put(key, bitmap);
                return bitmap;
            }
        }
        synchronized (mSoftCache) {
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(key);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null) {
                    // 将图片移回LruCache
                    mLruCache.put(key, bitmap);
                    mSoftCache.remove(key);
                    return bitmap;
                } else {
                    mSoftCache.remove(key);
                }
            }
        }
        return null;
    }
    
    /**
     * 把图片从缓存文件移除
     * 
     * @param key
     */
    public void removeBitmapFromCache(String key){
        mLruCache.remove(key);
        mSoftCache.remove(key);
    }

    /**
     * 清除所有缓存文件
     */
    public void clearCache() {
        mSoftCache.clear();
    }

}
