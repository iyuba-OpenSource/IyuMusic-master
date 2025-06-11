package com.iyuba.music.activity.me;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.View;

import com.buaa.ct.imageselector.view.ImageCropActivity;
import com.buaa.ct.imageselector.view.ImageSelectorActivity;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiPhotoActivity;
import com.iyuba.music.data.Constant;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.TakePictureUtil;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.view.AddRippleEffect;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by 10202 on 2016/2/18.
 */
@RuntimePermissions
public class ChangePhotoActivity extends BaseActivity {

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private CircleImageView photo;
    private RoundTextView change;
    private ContextMenu menu;
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menu.show();
        }
    };
    private View root;

    private IyubaDialog waittingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_photo);
        context = this;

        waittingDialog = WaitingDialog.create(context, null);
        initWidget();
        setListener();
        changeUIByPara();


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            ChangePhotoActivityPermissionsDispatcher.initLocationWithPermissionCheck(this);
        }

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        root = findViewById(R.id.root);
        photo = (CircleImageView) findViewById(R.id.photo);
        change = (RoundTextView) findViewById(R.id.change);
        AddRippleEffect.addRippleEffect(change);
        menu = new ContextMenu(context);
        initContextMunu();
    }

    @Override
    protected void setListener() {
        super.setListener();
        photo.setOnClickListener(ocl);
        change.setOnClickListener(ocl);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.changephoto_title);
        ImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), photo);
    }

    private void initContextMunu() {
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.changephoto_camera));
        list.add(context.getString(R.string.changephoto_gallery));
        list.add(context.getString(R.string.changephoto_see));
        menu.setInfo(list, new IOperationResultInt() {
            @Override
            public void performance(int index) {
                if (index == 0) {

                    if (PermissionUtils.hasSelfPermissions(context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                        TakePictureUtil.photoPath = ImageSelectorActivity.startCameraDirect(context);

                    } else {

                        CustomToast.getInstance().showToast("拍照或存储权限未开启，开启后可正常使用此功能", 3000);
                    }


                } else if (index == 1) {

                    if (PermissionUtils.hasSelfPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ImageSelectorActivity.start(ChangePhotoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, false, true, true);

                    } else {
                        CustomToast.getInstance().showToast("存储权限未开启，开启后可正常使用此功能", 3000);
                    }


                } else if (index == 2) {
                    Intent intent = new Intent(context, MeizhiPhotoActivity.class);
                    intent.putExtra("url", "http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&size=big&uid=" + AccountManager.getInstance().getUserId());
                    context.startActivity(intent);
                }
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
//            case PHOTO_REQUEST_TAKEPHOTO:
//                startPhotoZoom(Uri.fromFile(new File(TakePictureUtil.photoPath)), 150);
//                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom1(data.getData(), 150);
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImageSelectorActivity.REQUEST_CAMERA:
                    ImageCropActivity.startCrop(this, TakePictureUtil.photoPath);
                    break;
                case ImageSelectorActivity.REQUEST_IMAGE:
                    if (resultCode == RESULT_OK) {
                        ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                        TakePictureUtil.photoPath = images.get(0);
                        photo.setImageBitmap(getImage());
                        startUploadThread();
                    }
                    break;
                case ImageCropActivity.REQUEST_CROP:
                    TakePictureUtil.photoPath = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                    photo.setImageBitmap(getImage());
                    startUploadThread();
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == ImageSelectorActivity.REQUEST_CAMERA) {
                CustomToast.getInstance().showToast(R.string.changephoto_camera_cancel);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    private Bitmap getImage() {
        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(TakePictureUtil.photoPath, op);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startUploadThread() {
        waittingDialog.show();
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                UploadFile.postImg("http://api." + Constant.IYUBA_COM + "v2/avatar?uid="
                        + AccountManager.getInstance().getUserId(), new File(TakePictureUtil.photoPath), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        waittingDialog.dismiss();
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void fail(Object object) {
                        waittingDialog.dismiss();
                        handler.sendEmptyMessage(1);
                    }
                });
            }
        });
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<ChangePhotoActivity> {
        @Override
        public void handleMessageByRef(final ChangePhotoActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    CustomToast.getInstance().showToast(R.string.changephoto_success);
                    CustomSnackBar.make(activity.root, activity.getString(R.string.changephoto_intro)).info(activity.getString(R.string.credit_check), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(new Intent(activity, CreditActivity.class));
                        }
                    });
                    ConfigManager.getInstance().setUserPhotoTimeStamp();
                    break;
                case 1:
                    CustomToast.getInstance().showToast(R.string.changephoto_fail);
                    break;
            }
        }
    }

    private void startPhotoZoom1(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);

        Bitmap photo = intent.getExtras().getParcelable("data");
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(intent.getData(), proj, null, null, null);
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        TakePictureUtil.photoPath = cursor.getString(column_index);
        Log.e("startPhotoZoom", TakePictureUtil.photoPath);

    }


    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            Drawable drawable = new BitmapDrawable(bitmap);
            //SaveImage.saveImage(tempFilePath, photo);
            photo.setImageDrawable(drawable);


            startUploadThread();
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ChangePhotoActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void initLocation() {

    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void locationDenied() {


    }
}