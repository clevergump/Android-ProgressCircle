package com.clevergump.progresscircle.sample;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author zhangzhiyi
 * @version 1.0
 * @createTime 2016/3/25 15:37
 * @projectName Android-ProgressCircle
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
    }

    /**
     * 初始化 UIL图片加载框架
     */
    private void initImageLoader() {
        ImageLoaderConfiguration imageLoaderConfig = new ImageLoaderConfiguration.Builder(getApplicationContext())
                // 对于同一张图片有多种尺寸的图, 只存储其中一个
                .denyCacheImageMultipleSizesInMemory()
                // 内存缓存使用 LRU 算法进行存储和删除, 容量10MB
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                // 硬盘缓存容量 50MB
                .diskCacheSize(50 * 1024 * 1024)
                // 硬盘上缓存的图片名称是经过使用 md5 转换后的名称.
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                // 线程池按照LIFO的顺序进行图片的加载, 即: 越是后加入到线程池队列中的图片, 越是优先进行加载.
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 设置线程池最大并发数为5
                .threadPoolSize(5)
                // 打开调试日志的开关
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(imageLoaderConfig);
    }
}