package com.iyuba.music.activity.eggshell.meizhi;

/**
 * Created by 10202 on 2016/4/21.
 * 查看大图
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MeizhiPhotoActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_TASK_CODE = 1;
    private static String EXTRA_IAMGE_URL = "url";
    protected Context context;
    private PhotoView photoView;
    private ContextMenu menu;
    private String url;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Glide.get(context).clearMemory();
            Glide.with(MeizhiPhotoActivity.this).load(url).into(photoView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.meizhi_photo);
        context = this;
        initWidget();
        setListener();
        Intent intent = getIntent();
        url = intent.getStringExtra(MeizhiPhotoActivity.EXTRA_IAMGE_URL);


        new Thread() {
            @Override
            public void run() {
                super.run();
                Glide.get(context).clearDiskCache();
                handler.sendEmptyMessage(1);
            }
        }.start();


        ((MusicApplication) getApplication()).pushActivity(this);
    }

    protected void initWidget() {
        photoView = (PhotoView) findViewById(R.id.iv_girl);
        menu = new ContextMenu(context);
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.photo_download));
        menu.setInfo(list, new IOperationResultInt() {
            @Override
            public void performance(int index) {
                if (index == 0) {

                    Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            try {
                                saveFile(resource, Calendar.getInstance().getTimeInMillis() + ".jpg");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                }
            }
        });
    }

    protected void setListener() {
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.M) && ContextCompat.checkSelfPermission(MeizhiPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MeizhiPhotoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_TASK_CODE);
                } else {
                    menu.show();
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((MusicApplication) getApplication()).popActivity(this);
    }

    private void saveFile(Bitmap bm, String fileName) throws IOException {
        File folder = new File(ConstantManager.imgFile);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File myCaptureFile = new File(ConstantManager.imgFile, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        intent.setData(uri);
        context.sendBroadcast(intent);
        CustomToast.getInstance().showToast(R.string.photo_downloaded);
    }

    @Override
    public void onBackPressed() {
        if (menu.isShown()) {
            menu.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_TASK_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                menu.show();
            } else {
                final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                materialDialog.setTitle(R.string.storage_permission);
                materialDialog.setMessage(R.string.storage_permission_content);
                materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MeizhiPhotoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_STORAGE_TASK_CODE);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.show();
            }
        }
    }
}
