package com.clevergump.progresscircle.sample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.clevergump.progresscircle.library.ProgressCircle;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_URI = "http://www.sinaimg.cn/dy/slidenews/2_img/2016_12/57057_1741703_939382.jpg";
    private ImageView mIv;
    private ProgressCircle mCircle;
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListenterImpl mLoadingListener;
    private ImageLoadingProgressListenerImpl mProgressListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mIv = (ImageView) findViewById(R.id.iv);
        mCircle = (ProgressCircle) findViewById(R.id.progress_circle);
    }

    private void initData() {
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_loading)
                .showImageForEmptyUri(R.mipmap.ic_empty)
                .showImageOnFail(R.mipmap.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();

        mLoadingListener = new ImageLoadingListenterImpl(MainActivity.this);
        mProgressListener = new ImageLoadingProgressListenerImpl(MainActivity.this);
    }

    /**
     * 开始加载图片.
     * 当"START LOADING"按钮按下时会调用该方法
     * @param view
     */
    public void startLoadingImages(View view) {
        ImageLoader.getInstance().displayImage(IMAGE_URI, mIv, mDisplayImageOptions, mLoadingListener, mProgressListener);
    }

    /**
     * 清除图片在内存和硬盘上的缓存.
     * 当"CLEAR CACHE"按钮按下时会调用该方法
     * @param view
     */
    public void clearCache(View view) {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    /**
     * 图片加载过程的监听器
     */
    private static class ImageLoadingListenterImpl extends SimpleImageLoadingListener {
        private final WeakReference<MainActivity> mWeakRefOuterActivity;

        public ImageLoadingListenterImpl(MainActivity outerActivity) {
            mWeakRefOuterActivity = new WeakReference<MainActivity>(outerActivity);
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            showCircle();
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            hideCicle();
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            hideCicle();
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            hideCicle();
        }

        /**
         * 显示加载进度的圆
         */
        private void showCircle() {
            MainActivity outerActivity = mWeakRefOuterActivity.get();
            if (outerActivity != null) {
                outerActivity.mCircle.setVisibility(View.VISIBLE);
            }
        }

        /**
         * 隐藏加载进度的圆
         */
        private void hideCicle() {
            MainActivity outerActivity = mWeakRefOuterActivity.get();
            if (outerActivity != null) {
                outerActivity.mCircle.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 图片加载进度的监听器
     */
    private static class ImageLoadingProgressListenerImpl implements ImageLoadingProgressListener {
        private final WeakReference<MainActivity> mWeakRefOuterActivity;

        public ImageLoadingProgressListenerImpl(MainActivity outerActivity) {
            mWeakRefOuterActivity = new WeakReference<MainActivity>(outerActivity);
        }

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            MainActivity outerActivity = mWeakRefOuterActivity.get();
            if (outerActivity != null) {
                outerActivity.mCircle.setProgress(current, total);
            }
        }
    }
}