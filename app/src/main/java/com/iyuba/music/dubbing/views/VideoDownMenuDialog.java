//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iyuba.music.dubbing.views;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.SimpleDListener;
import com.iyuba.headlinelibrary.R.color;
import com.iyuba.headlinelibrary.R.drawable;
import com.iyuba.headlinelibrary.R.id;
import com.iyuba.headlinelibrary.R.string;
import com.iyuba.headlinelibrary.data.model.VideoMiniData;
import com.iyuba.headlinelibrary.ui.video.RectangleProgressDrawable;
import com.iyuba.headlinelibrary.util.PathUtil;
import com.iyuba.module.toolbox.DensityUtil;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.music.R;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.vip.NewVipCenterActivity;
import com.iyuba.music.entity.article.Article;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;

import timber.log.Timber;

/**
 * mtv下载的弹窗
 */
public class VideoDownMenuDialog extends BottomSheetDialog {
    private static final String middle = "";
    private static final String zhMiddle = "_zh_";
    private static final String foMiddle = "_en_";
    private static final String mimeType = "video/mp4";
    private final DLManager mDLManager;
    private final VideoDownMenuDialog.ActionDelegate mDelegate;
    TextView mSeeTv;
    TextView mDownloadingTv;
    ImageView mVideoDownIv;
    ImageView mZhVideoDownIv;
    ImageView mForeignVideoDownIv;
    TextView mForeginVideoDownTv;
    String clickOrFinishFilename;


    private Article article;

    private String id;

    public VideoDownMenuDialog(@NonNull Context context, VideoDownMenuDialog.ActionDelegate delegate) {
        super(context);
        this.mDelegate = delegate;
        Window window = this.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        LayoutParams lp = window.getAttributes();
        lp.width = -1;
        lp.height = -2;
        window.setAttributes(lp);
        this.mDLManager = DLManager.getInstance();
    }


    public VideoDownMenuDialog(@NonNull Context context, VideoDownMenuDialog.ActionDelegate delegate, Article article) {
        super(context);
        this.mDelegate = delegate;
        Window window = this.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        LayoutParams lp = window.getAttributes();
        lp.width = -1;
        lp.height = -2;
        window.setAttributes(lp);
        this.mDLManager = DLManager.getInstance();
        this.article = article;
    }

    public VideoDownMenuDialog(@NonNull Context context, VideoDownMenuDialog.ActionDelegate delegate, String id) {
        super(context);
        this.mDelegate = delegate;
        Window window = this.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        LayoutParams lp = window.getAttributes();
        lp.width = -1;
        lp.height = -2;
        window.setAttributes(lp);
        this.mDLManager = DLManager.getInstance();
        this.id = id;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_video_down_menu);
        this.mSeeTv = (TextView) this.findViewById(R.id.text_see);
        this.mDownloadingTv = (TextView) this.findViewById(R.id.text_downloading);
        this.mVideoDownIv = (ImageView) this.findViewById(R.id.image_video);
        this.mZhVideoDownIv = (ImageView) this.findViewById(R.id.image_video_zh);
        this.mForeignVideoDownIv = (ImageView) this.findViewById(R.id.image_video_en);
        this.mForeginVideoDownTv = (TextView) this.findViewById(R.id.text_video_en);
        this.findViewById(R.id.image_close).setOnClickListener(this::clickClose);
        this.mSeeTv.setOnClickListener(this::clickSee);
        this.mVideoDownIv.setOnClickListener(this::clickVideo);
        this.mZhVideoDownIv.setOnClickListener(this::clickZhVideo);
        this.mForeignVideoDownIv.setOnClickListener(this::clickForeignVideo);
        int leftColor = this.getContext().getResources().getColor(color.colorPrimary);
        int rightColor = this.getContext().getResources().getColor(color.headline_download_progress_second_color);
        this.mDownloadingTv.setBackground(new RectangleProgressDrawable(leftColor, rightColor, DensityUtil.dp2px(this.getContext(), 8.0F), 0.0F));
        this.setupViews();
    }

    private void clickClose(View view) {
        this.dismiss();
    }

    @SuppressLint("TimberArgCount")
    private void clickSee(View view) {
        this.dismiss();
        Intent intent;
        if (TextUtils.isEmpty(this.clickOrFinishFilename)) {
            Timber.i("no filename to open", new Object[0]);
        }

        try {
            long id = this.getFileIdInMediaStore(this.clickOrFinishFilename);
            Timber.i("redmi device get id: %s filename: %s", new Object[]{id, this.clickOrFinishFilename});
            Uri contentUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id);
            intent = new Intent(Intent.ACTION_VIEW, contentUri);
            intent.setDataAndType(contentUri, "video/*");
            this.getContext().startActivity(intent);
        } catch (Exception var6) {
            Timber.e(var6);
        }
    }

    private void clickDownloadImage(Runnable action) {
        if (IyuUserManager.getInstance().checkUserLogin()) {
            if (IyuUserManager.getInstance().isVip()) {
                action.run();
            } else {
                getContext().startActivity(new Intent(getContext(),
                        NewVipCenterActivity.class));
                Toast.makeText(this.getContext(), string.headline_buy_vip_hint, Toast.LENGTH_SHORT).show();
            }
        } else {
            getContext().startActivity(new Intent(getContext(), LoginActivity.class));
            Toast.makeText(this.getContext(), string.headline_please_sign_in, Toast.LENGTH_SHORT).show();
        }

    }

    private void clickVideo(View view) {
        this.clickDownloadImage(this::clickVideoActual);
    }

    private void clickVideoActual() {

        String tag;
        if (article != null) {

            tag = "VideoMiniData_" + article.getId();
        } else {

            tag = "VideoMiniData_" + id;
        }
        String fileName = this.makeFileName(tag, "");
        DLTaskInfo dlTask = this.mDLManager.getDLTaskInfo(tag);
        if (dlTask == null) {
            if (this.checkInSystemGallery(fileName)) {
                this.clickOrFinishFilename = fileName;
                String stuff = this.getContext().getString(string.headline_no_subtitle_video);
                this.mSeeTv.setText(this.getContext().getString(string.headline_go_gallery_see, new Object[]{stuff}));
                this.mSeeTv.setVisibility(View.VISIBLE);
                return;
            }

            DLTaskInfo newTask = new DLTaskInfo();
            newTask.tag = tag;
            newTask.initalizeUrl(this.mDelegate.getVideoMiniData().getVideo());
            newTask.setFilePath(PathUtil.getMediaPath(this.getContext()));
            newTask.setFileName(fileName);
            newTask.setDListener(new VideoDownMenuDialog.SDListener(newTask, fileName));
            this.mDLManager.addDownloadTask(newTask);
        } else if (dlTask.fileName.equals(fileName)) {
            if (dlTask.isPausing() || dlTask.isError()) {
                dlTask.setDListener(new VideoDownMenuDialog.SDListener(dlTask, fileName));
                this.mDLManager.resumeTask(dlTask);
            }
        } else {
            Toast.makeText(this.getContext(), string.headline_downloading, Toast.LENGTH_SHORT).show();
        }

    }

    private void clickZhVideo(View view) {
        this.clickDownloadImage(this::clickZhVideoActual);
    }

    private void clickZhVideoActual() {

        String tag;
        if (article != null) {

            tag = "VideoMiniData_" + article.getId();
        } else {

            tag = "VideoMiniData_" + id;
        }
        String fileName = this.makeFileName(tag, "_zh_");
        DLTaskInfo dlTask = this.mDLManager.getDLTaskInfo(tag);
        if (dlTask == null) {
            if (this.checkInSystemGallery(fileName)) {
                this.clickOrFinishFilename = fileName;
                String stuff = this.getContext().getString(string.headline_zh_subtitle_video);
                this.mSeeTv.setText(this.getContext().getString(string.headline_go_gallery_see, new Object[]{stuff}));
                this.mSeeTv.setVisibility(View.VISIBLE);
                return;
            }

            DLTaskInfo newTask = new DLTaskInfo();
            newTask.tag = tag;
            newTask.initalizeUrl(this.mDelegate.getVideoMiniData().srtChVideo);
            newTask.setFilePath(PathUtil.getMediaPath(this.getContext()));
            newTask.setFileName(fileName);
            newTask.setDListener(new VideoDownMenuDialog.SDListener(newTask, fileName));
            this.mDLManager.addDownloadTask(newTask);
        } else if (dlTask.fileName.equals(fileName) && (dlTask.isPausing() || dlTask.isError())) {
            dlTask.setDListener(new VideoDownMenuDialog.SDListener(dlTask, fileName));
            this.mDLManager.resumeTask(dlTask);
        }

    }

    private void clickForeignVideo(View view) {
        this.clickDownloadImage(this::clickForeignVideoActual);
    }

    private void clickForeignVideoActual() {

        String tag;
        if (article != null) {

            tag = "VideoMiniData_" + article.getId();
        } else {

            tag = "VideoMiniData_" + id;
        }
        String fileName = this.makeFileName(tag, "_en_");
        DLTaskInfo dlTask = this.mDLManager.getDLTaskInfo(tag);
        if (dlTask == null) {
            if (this.checkInSystemGallery(fileName)) {
                this.clickOrFinishFilename = fileName;
                String stuff = this.mDelegate.isJP() ? this.getContext().getString(string.headline_jp_subtitle_video) : this.getContext().getString(string.headline_en_subtitle_video);
                this.mSeeTv.setText(this.getContext().getString(string.headline_go_gallery_see, new Object[]{stuff}));
                this.mSeeTv.setVisibility(View.VISIBLE);
                return;
            }

            DLTaskInfo newTask = new DLTaskInfo();
            newTask.tag = tag;
            newTask.initalizeUrl(this.mDelegate.getVideoMiniData().srtEngVideo);
            newTask.setFilePath(PathUtil.getMediaPath(this.getContext()));
            newTask.setFileName(fileName);
            newTask.setDListener(new VideoDownMenuDialog.SDListener(newTask, fileName));
            this.mDLManager.addDownloadTask(newTask);
        } else if (dlTask.fileName.equals(fileName) && (dlTask.isPausing() || dlTask.isError())) {
            dlTask.setDListener(new VideoDownMenuDialog.SDListener(dlTask, fileName));
            this.mDLManager.resumeTask(dlTask);
        }
    }

    private boolean isAllViewExist() {
        return this.mSeeTv != null && this.mDownloadingTv != null && this.mVideoDownIv != null && this.mZhVideoDownIv != null && this.mForeignVideoDownIv != null;
    }

    private void setupViews() {

        this.mSeeTv.setVisibility(View.GONE);
        this.mForeginVideoDownTv.setText(this.mDelegate.isJP() ? string.headline_jp_subtitle_video : string.headline_en_subtitle_video);

        String tag;
        if (article != null) {

            tag = "VideoMiniData_" + article.getId();
        } else {

            tag = "VideoMiniData_" + id;
        }
        DLTaskInfo taskInfo = this.mDLManager.getDLTaskInfo(tag);
        if (taskInfo != null) {
            if (taskInfo.fileName.contains("_zh_")) {
                this.setForeignVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "_en_")));
                this.setVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "")));
            } else if (taskInfo.fileName.contains("_en_")) {
                this.setZhVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "_zh_")));
                this.setVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "")));
            } else {
                this.setZhVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "_zh_")));
                this.setForeignVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "_en_")));
            }

            if (!taskInfo.isInit() && !taskInfo.isWaiting() && !taskInfo.isPreparing() && !taskInfo.isDownloading()) {
                if (!taskInfo.isPausing() && !taskInfo.isError()) {
                    if (taskInfo.isCompleted()) {
                        this.mDownloadingTv.setVisibility(View.GONE);
                        if (taskInfo.fileName.contains("_zh_")) {
                            this.setZhVideoIv(true);
                        } else if (taskInfo.fileName.contains("_en_")) {
                            this.setForeignVideoIv(true);
                        } else {
                            this.setVideoIv(true);
                        }
                    }
                } else {
                    this.mDownloadingTv.setVisibility(View.VISIBLE);
                    this.setDownloadingTvWithPercent(taskInfo.fileName, taskInfo.getCurrentPercentage());
                }
            } else {
                this.mDownloadingTv.setVisibility(View.VISIBLE);
                this.setDownloadingTvWithPercent(taskInfo.fileName, taskInfo.getCurrentPercentage());
                taskInfo.setDListener(new VideoDownMenuDialog.SDListener(taskInfo, taskInfo.fileName));
            }
        } else {
            Timber.i("no task for %s exists", new Object[]{tag});
            this.mDownloadingTv.setVisibility(View.GONE);
            this.setZhVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "_zh_")));
            this.setForeignVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "_en_")));
            this.setVideoIv(this.checkInSystemGallery(this.makeFileName(tag, "")));
        }

    }

    private void setDownloadingTvWithPercent(String fileName, int percentage) {
        String downStuff = "";
        if (fileName.contains("_zh_")) {
            downStuff = this.getContext().getString(string.headline_zh_subtitle_video);
        } else if (fileName.contains("_en_")) {
            downStuff = this.mDelegate.isJP() ? this.getContext().getString(string.headline_jp_subtitle_video) : this.getContext().getString(string.headline_en_subtitle_video);
        } else {
            downStuff = this.getContext().getString(string.headline_no_subtitle_video);
        }

        this.mDownloadingTv.setText(this.getContext().getString(string.headline_video_downloading_info, new Object[]{downStuff, percentage}));
        float ratio = (float) percentage / 100.0F;
        ((RectangleProgressDrawable) this.mDownloadingTv.getBackground()).setProgressRatioPosition(ratio);
    }

    private void setVideoIv(boolean downloaded) {
        if (downloaded) {
            this.mVideoDownIv.setImageResource(drawable.headline_ic_video_mini_downloaded);
        } else {
            this.mVideoDownIv.setImageResource(drawable.headline_ic_video_mini_undownload);
        }

    }

    private void setZhVideoIv(boolean downloaded) {
        if (downloaded) {
            this.mZhVideoDownIv.setImageResource(drawable.headline_ic_video_mini_zh_downloaded);
        } else {
            this.mZhVideoDownIv.setImageResource(drawable.headline_ic_video_mini_zh_undownload);
        }

    }

    private void setForeignVideoIv(boolean downloaded) {
        if (downloaded) {
            this.mForeignVideoDownIv.setImageResource(this.mDelegate.isJP() ? drawable.headline_ic_video_mini_jp_downloaded : drawable.headline_ic_video_mini_en_downloaded);
        } else {
            this.mForeignVideoDownIv.setImageResource(this.mDelegate.isJP() ? drawable.headline_ic_video_mini_jp_undownload : drawable.headline_ic_video_mini_en_undownload);
        }

    }

    private String makeFileName(String tag, String middle) {
        return tag + middle + ".mp4";
    }

    @SuppressLint("TimberArgCount")
    private boolean checkInSystemGallery(String fileName) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = this.getContext().getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, " _display_name = ?", new String[]{fileName}, null);
        boolean result = false;

        if (cursor != null && cursor.moveToNext()) {

            int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = cursor.getString(columnIndex);
            result = new File(path).exists();
            cursor.close();
        }
        return result;
    }

    private long getFileIdInMediaStore(String fileName) {

        String projection[] = {Media.DATA, Media._ID};
        Cursor cursor = this.getContext().getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, "_display_name = ?", new String[]{fileName}, null);
        long result = 0L;
        if (cursor != null && cursor.moveToNext()) {
            result = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
            cursor.close();
        } else {
            Timber.i("get file %s id in mediaStore cursor is null", new Object[]{fileName});
        }

        return result;
    }

    private class SDListener extends SimpleDListener {
        private final DLTaskInfo task;
        private final String fileName;

        public SDListener(DLTaskInfo task, String fileName) {
            this.task = task;
            this.fileName = fileName;
        }

        public void onStart(String fName, String realUrl, int fileLength) {
            VideoDownMenuDialog.this.mSeeTv.setVisibility(View.GONE);
            VideoDownMenuDialog.this.mDownloadingTv.setVisibility(View.VISIBLE);
            VideoDownMenuDialog.this.setDownloadingTvWithPercent(this.fileName, 0);
        }

        public void onProgress(int progress) {
            VideoDownMenuDialog.this.setDownloadingTvWithPercent(this.fileName, this.task.getCurrentPercentage());
        }

        public void onFinish(File file) {
            if (VideoDownMenuDialog.this.getContext() != null) {
                ContentValues values = new ContentValues();
                values.put("_display_name", this.fileName);
                values.put("mime_type", "video/mp4");
                if (VERSION.SDK_INT >= 29) {
                    values.put("relative_path", Environment.DIRECTORY_DCIM);
                } else {
                    values.put("_data", Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DCIM + "/" + this.fileName);
                }

                Uri uri = VideoDownMenuDialog.this.getContext().getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try {
                        OutputStream outputStream = VideoDownMenuDialog.this.getContext().getContentResolver().openOutputStream(uri);
                        (new FileInputStream(file)).getChannel().transferTo(0L, file.length(), Channels.newChannel(outputStream));
                        VideoDownMenuDialog.this.mDLManager.cancelTask(this.task, true);
                        VideoDownMenuDialog.this.mDownloadingTv.setVisibility(View.GONE);
                        VideoDownMenuDialog.this.mSeeTv.setVisibility(View.VISIBLE);
                        String stuff;
                        if (this.fileName.contains("_zh_")) {
                            VideoDownMenuDialog.this.clickOrFinishFilename = this.fileName;
                            stuff = VideoDownMenuDialog.this.getContext().getString(string.headline_zh_subtitle_video);
                            VideoDownMenuDialog.this.mSeeTv.setText(VideoDownMenuDialog.this.getContext().getString(string.headline_go_gallery_see, new Object[]{stuff}));
                            VideoDownMenuDialog.this.setZhVideoIv(true);
                        } else if (this.fileName.contains("_en_")) {
                            VideoDownMenuDialog.this.clickOrFinishFilename = this.fileName;
                            stuff = VideoDownMenuDialog.this.mDelegate.isJP() ? VideoDownMenuDialog.this.getContext().getString(string.headline_jp_subtitle_video) : VideoDownMenuDialog.this.getContext().getString(string.headline_en_subtitle_video);
                            VideoDownMenuDialog.this.mSeeTv.setText(VideoDownMenuDialog.this.getContext().getString(string.headline_go_gallery_see, new Object[]{stuff}));
                            VideoDownMenuDialog.this.setForeignVideoIv(true);
                        } else {
                            VideoDownMenuDialog.this.clickOrFinishFilename = this.fileName;
                            stuff = VideoDownMenuDialog.this.getContext().getString(string.headline_no_subtitle_video);
                            VideoDownMenuDialog.this.mSeeTv.setText(VideoDownMenuDialog.this.getContext().getString(string.headline_go_gallery_see, new Object[]{stuff}));
                            VideoDownMenuDialog.this.setVideoIv(true);
                        }
                    } catch (IOException var6) {
                        Timber.e(var6);
                    }
                }

            }
        }
    }

    public interface ActionDelegate {
        VideoMiniData getVideoMiniData();

        boolean isJP();
    }
}
