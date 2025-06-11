package com.iyuba.music.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;

/**
 * Created by 102 on 2016/10/10.
 */

public class ImageUtil {
    public static void loadAvatar(String userid, ImageView imageView) {
        String photoStamp = ConfigManager.getInstance().getUserPhotoTimeStamp();
        StringBuilder avatarUrl = new StringBuilder();
        avatarUrl.append("http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&size=big&uid=").append(userid);
        if (!TextUtils.isEmpty(photoStamp)) {
            avatarUrl.append('&').append(photoStamp);
        }

        loadImage(avatarUrl.toString(), imageView, R.drawable.default_photo);
    }

    public static void loadAvatar(ImageView imageView, String userid, String size) {
        String photoStamp = ConfigManager.getInstance().getUserPhotoTimeStamp();
        StringBuilder avatarUrl = new StringBuilder();
        avatarUrl.append("http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&uid=").append(userid).append("&size=").append(size);
        if (!TextUtils.isEmpty(photoStamp)) {
            avatarUrl.append('&').append(photoStamp);
        }
        loadImage(avatarUrl.toString(), imageView, R.drawable.default_photo);
    }

    public static void loadImage(String imageUrl, ImageView imageView) {
        loadImage(imageUrl, imageView, 0);
    }

    public static void loadImage(String imageUrl, ImageView imageView, int placeholderDrawableId) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setImageResource(placeholderDrawableId);
            return;
        }

        //android 版本小于5
        if (Build.VERSION.SDK_INT < 21) {

            Glide.with(MusicApplication.getApp()).load(imageUrl).placeholder(placeholderDrawableId)
                    .error(placeholderDrawableId).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);

            return;
        }

        Context context;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            context = imageView.getContext();
        } else {
            context = RuntimeManager.getContext();
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity != null && !activity.isFinishing())
                Glide.with(context).load(imageUrl).placeholder(placeholderDrawableId)
                        .error(placeholderDrawableId).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
        }
    }

    public static void loadImage(ImageView imageView, String imageUrl, int placeholderDrawableId, OnDrawableLoadListener listener) {
        loadImage(imageView, imageUrl, placeholderDrawableId, listener, true, true);
    }

    @SuppressLint("CheckResult")
    public static void loadImage(ImageView imageView, String imageUrl, int placeholderDrawableId,
                                 final OnDrawableLoadListener listener, boolean centerCrop, boolean cache) {
        if (imageView == null) {
            return;
        } else if (TextUtils.isEmpty(imageUrl)) {
            imageView.setImageResource(placeholderDrawableId);
            return;
        }
        Context context;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            context = imageView.getContext();
        } else {
            context = RuntimeManager.getContext();
        }

        RequestBuilder<Drawable> builder = Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholderDrawableId)
                .error(placeholderDrawableId)
                .skipMemoryCache(!cache)
                .diskCacheStrategy(cache ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.NONE);
        if (listener != null) {
            builder.listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                    listener.onFail(e);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    listener.onSuccess(resource);
                    return false;
                }
            });
        }
        (centerCrop ? builder.centerCrop() : builder.fitCenter()).into(imageView);
    }

    /**
     * 清空内存缓存
     * 内存缓存必须得在主线程
     *
     * @param activity Activity的Context
     */
    public static void clearMemoryCache(final Context activity) {
        Glide.get(activity).clearMemory();
    }

    /**
     * 清空磁盘缓存
     * 必须得在子线程运行
     *
     * @param context context
     */
    public static void clearDiskCache(final Context context) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        });
    }

    /**
     * 双清空缓存
     */
    public static void clearImageAllCache(Context context) {
        clearDiskCache(context);
        clearMemoryCache(context);
    }

    public static void destroy(Application application) {
        clearMemoryCache(application);
        Glide.with(application).onDestroy();
    }

    //专为加载个人头像使用
    public static void setHeadImage(String userId, Context mContext, int placeImage, ImageView imageView) {

        String imageUrl = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&size=big&uid=" + userId;

        Glide.with(mContext).load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(placeImage)
                .error(placeImage)
                .dontAnimate()  //防止加载网络图片变形
                .into(imageView);
    }

    public interface OnDrawableLoadListener {
        void onSuccess(Drawable drawable);

        void onFail(Exception e);
    }
}
