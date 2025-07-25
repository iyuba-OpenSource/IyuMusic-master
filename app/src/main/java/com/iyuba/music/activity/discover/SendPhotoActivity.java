package com.iyuba.music.activity.discover;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.comment.ContextManager;
import com.buaa.ct.comment.EmojiView;
import com.buaa.ct.imageselector.view.ImageSelectorActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseInputActivity;
import com.iyuba.music.activity.eggshell.meizhi.LocalPhotoActivity;
import com.iyuba.music.data.Constant;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.merequest.WriteStateRequest;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.nineoldandroids.animation.Animator;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;


/**
 * Created by 10202 on 2015/11/20.
 */
@RuntimePermissions
public class SendPhotoActivity extends BaseInputActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private MaterialEditText content;
    private IyubaDialog waittingDialog;
    private ArrayList<String> images;
    private EmojiView emojiView;
    private ImageView photo;
    private View photoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.setInstance(this);//评论模块初始化
        setContentView(R.layout.circle_photo);
        context = this;
        images = new ArrayList<>();
        initWidget();
        setListener();
        changeUIByPara();

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        photoContent = findViewById(R.id.photo_content);
        content = (MaterialEditText) findViewById(R.id.feedback_content);
        photo = (ImageView) findViewById(R.id.state_image);
        emojiView = (EmojiView) findViewById(R.id.emoji);
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.photo_on_way));
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (images.size() == 0) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        SendPhotoActivityPermissionsDispatcher.initLocationWithPermissionCheck(SendPhotoActivity.this);
                    }
                } else {
                    Intent intent = new Intent(context, LocalPhotoActivity.class);
                    intent.putExtra("url", ConstantManager.envir + "/temp.jpg");
                    startActivityForResult(intent, 101);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.state_send);
        title.setText(R.string.photo_title);
        emojiView.setmEtText(content);
        photo.setImageResource(R.drawable.circle_photo_add);
    }

    private void submit() {
        String contentString = content.getEditableText().toString();
        if (TextUtils.isEmpty(contentString)) {
            YoYo.with(Techniques.Shake).duration(500).playOn(content);
        } else if (!content.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(content);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
            waittingDialog.show();
            if (images.size() == 0) {
                WriteStateRequest.exeRequest(WriteStateRequest.generateUrl(AccountManager.getInstance().getUserId(), AccountManager.getInstance().getUserInfo().getUsername(),
                        content.getEditableText().toString()), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {
                        CustomToast.getInstance().showToast(msg);
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onServerError(String msg) {
                        CustomToast.getInstance().showToast(msg);
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void response(Object object) {
                        handler.sendEmptyMessage(1);
                        String result = (String) object;
                        if ("351".equals(result)) {
                            handler.sendEmptyMessage(0);
                            handler.sendEmptyMessage(1);
                        } else {
                            CustomToast.getInstance().showToast(R.string.photo_fail);
                        }
                    }
                });
            } else {
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        UploadFile.postImg("http://api." + Constant.IYUBA_COM + "v2/avatar/photo?uid="
                                        + AccountManager.getInstance().getUserId() + "&iyu_describe=" + ParameterUrl.encode(ParameterUrl.encode(content.getEditableText().toString())),
                                new File(images.get(0)), new IOperationResult() {
                                    @Override
                                    public void success(Object object) {
                                        handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(1);
                                    }

                                    @Override
                                    public void fail(Object object) {
                                        handler.sendEmptyMessage(1);
                                        handler.sendEmptyMessage(3);
                                    }
                                });
                    }
                });
            }
        }
    }

    private Bitmap saveImage(String path, String bitmapPath) {
        File picture = new File(path);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picture);
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 2;
            op.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap temp = BitmapFactory.decodeFile(bitmapPath, op);
            temp.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            photo.setImageBitmap(saveImage(ConstantManager.envir + "/temp.jpg", images.get(0)));
        } else if (resultCode == -2) {
            CustomToast.getInstance().showToast(R.string.storage_permission_cancel);
        } else if (requestCode == 101 && resultCode == 1) {//删除
            images = new ArrayList<>();
            photo.setImageResource(R.drawable.circle_photo_add);
            new File(ConstantManager.envir + "/temp.jpg").delete();
        }
    }

    @Override
    public void onBackPressed() {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.photo_title);
        materialDialog.setMessage(R.string.photo_exit);
        materialDialog.setPositiveButton(R.string.photo_exit_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                SendPhotoActivity.this.finish();
            }
        });
        materialDialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ContextManager.destory();
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<SendPhotoActivity> {
        @Override
        public void handleMessageByRef(final SendPhotoActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    YoYo.with(Techniques.ZoomOutUp).duration(1200).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            CustomToast.getInstance().showToast(R.string.photo_success);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            activity.handler.sendEmptyMessageDelayed(2, 300);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(activity.photoContent);
                    break;
                case 1:
                    activity.waittingDialog.dismiss();
                    break;
                case 2:
                    Intent intent = new Intent();
                    activity.setResult(1, intent);
                    activity.finish();
                    break;
                case 3:
                    CustomToast.getInstance().showToast(R.string.photo_fail);
                    break;
            }
        }
    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SendPhotoActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void initLocation() {
        ImageSelectorActivity.start(SendPhotoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, false);

    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void locationDenied() {
        CustomToast.getInstance().showToast("拍照或存储权限未开启，开启后可正常使用此功能");

    }
}
