package com.iyuba.music.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import androidx.drawerlayout.widget.DrawerLayout;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.fragment.MainFragment;
import com.iyuba.music.fragment.MainLeftFragment;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.request.account.AuditDateRequest;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.InterNetUtil;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.util.popup.TeenPopup;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.PrivicyDialog;
import com.iyuba.music.widget.SendBookPop;
import com.iyuba.music.widget.dialog.CustomDialog;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import cn.fly.verify.FlyVerify;
import cn.fly.verify.PreVerifyCallback;
import cn.fly.verify.common.exception.VerifyException;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 主页面
 */
public class MainActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_TASK_CODE = 1;
    private static final int WRITE_EXTERNAL_TASK_NO_EXE_CODE = 2;
    private static final int ACCESS_COARSE_LOCATION_TASK_CODE = 3;
    private Context context;
    private DrawerLayout drawerLayout;
    private View drawView, root;
    private TextView toolbarOper;
    private MaterialMenuView menu;
    private boolean isExit = false;// 是否点过退出
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    private TeenPopup teenPopup;

    private void initTeenPopup() {

        if (teenPopup == null) {

            teenPopup = new TeenPopup(MainActivity.this);
        }

        teenPopup.showPopupWindow();
    }

    private void dealTeen() {

        RxDataStore<Preferences> dataStore =
                new RxPreferenceDataStoreBuilder(MainActivity.this, "TEEN").build();
        Preferences.Key<String> pk_psw = PreferencesKeys.stringKey("PASSWORD");
        dataStore.data().map(new Function<Preferences, String>() {
                    @Override
                    public String apply(Preferences preferences) throws Exception {

                        return preferences.get(pk_psw);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (hour >= 22 || hour <= 6) {

                        initTeenPopup();
                    }

                    dataStore.dispose();
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {


                        dataStore.dispose();
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigManager.getInstance().setFirstStart(false);

        dealTeen();

        ChangePropery.setAppConfig(this);
        setContentView(R.layout.layout_main);
        context = this;
        if (InterNetUtil.isNetworkConnected(MainActivity.this)) {
            Log.e("网络判断", InterNetUtil.isNetworkConnected(MainActivity.this) + "");
        } else {
            Log.e("网络判断", "======");
        }

//        initPush();

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//            MainActivityPermissionsDispatcher.initLocationWithPermissionCheck(this);
//        }

        /**
         * 非正常情况退出app后，正在下载的文件会异常
         */
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LocalInfoOp localInfoOp = new LocalInfoOp();
                    int normalLength = localInfoOp.findDataByShouldContinue().size();//正常关闭的下载文件
                    int allLenth = localInfoOp.findDataByDownloading().size(); //所有下载文件个数包括异常的
                    Log.e("MainActivity", "下载歌曲" + allLenth + "正常" + normalLength);
                    if (allLenth > normalLength) {
                        localInfoOp.changeDownloadToStop();
                    }
                } catch (Exception var1) {
                    if (var1 != null) {
                        Log.e("MainActivity", "localInfoOp Exception " + var1.getMessage());
                    }
                }
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initBroadcast();
        initWidget();
        setListener();
        showFirstDialog();
        preVerify();

        if (ConfigManager.getInstance().isUpgrade()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showWhatsNew();
                }
            }, 500);
        }
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_NO_EXE_CODE);
//        }
        StartFragment.checkTmpFile();
        if (getIntent().getBooleanExtra("pushIntent", false)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainFragment) (getSupportFragmentManager().findFragmentById(R.id.content_frame))).setShowItem(2);
                }
            }, 200);
        }
        handler.postDelayed(new Runnable() {
            public void run() {
                checkForUpdate();
            }
        }, 1000);
        ((MusicApplication) getApplication()).pushActivity(this);


        if (!ConfigManager.getInstance().loadBoolean("firstSendbook") && ConfigManager.getInstance().loadInt("firstSendBookFlag") == 40) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SendBookPop sendBookPop = new SendBookPop(MainActivity.this, root);
                }
            }, 1000);
            ConfigManager.getInstance().putInt("firstSendBookFlag", 0);
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("pushIntent", false)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainFragment) (getSupportFragmentManager().findFragmentById(R.id.content_frame))).setShowItem(2);
                }
            }, 200);
        }
    }

    private void showFirstDialog() {
        PrivicyDialog privicyDialog = new PrivicyDialog(context);
        privicyDialog.initPrivicyDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            pressAgainExit();
        }
    }

    private void initBroadcast() {
        if (ConfigManager.getInstance().isUpgrade()) {
            if (ConfigManager.getInstance().isPush()) {
//                MiPushClient.enablePush(RuntimeManager.getContext());
            } else {
//                MiPushClient.disablePush(RuntimeManager.getContext());
            }
        }
    }

    protected void initWidget() {
        root = findViewById(R.id.root);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        menu = (MaterialMenuView) findViewById(R.id.material_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        MainLeftFragment mainLeftFragment = new MainLeftFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.left_drawer, mainLeftFragment).commitAllowingStateLoss();
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mainFragment).commitAllowingStateLoss();
        drawView = findViewById(R.id.left_drawer);
        AuditDateRequest.exeRequest(AuditDateRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
            }

            @Override
            public void onServerError(String msg) {
            }

            @Override
            public void response(Object object) {
                String result = (String) object;
                if ("1".equals(result)) {                    //审核中
                    toolbarOper.setVisibility(View.GONE);
                } else {
                    toolbarOper.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    protected void setListener() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu.getDrawable().getIconState().equals(MaterialMenuDrawable.IconState.BURGER)) {
                    drawerLayout.openDrawer(drawView);
                    menu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
                } else {
                    drawerLayout.closeDrawer(drawView);
                    menu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
                }
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchActivity.class));


            }
        });
        drawerLayout.addDrawerListener(new DrawerLayoutStateListener(this));
    }


    private void showWhatsNew() {
        ConfigManager.getInstance().setUpgrade(false);
        StartFragment.showVersionFeature(context);
    }

    private void checkForUpdate() {
        StartFragment.checkUpdate(context, new IOperationResult() {
            @Override
            public void success(Object object) {
                CustomDialog.updateDialog(context, object.toString());
            }

            @Override
            public void fail(Object object) {
            }
        });
        StartFragment.cleanLocalData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


    private void pressAgainExit() {
        if (isExit) {
            if (((MusicApplication) getApplication()).getPlayerService().isPlaying()) {   // 后台播放
//                直接返回桌面
//                Intent i = new Intent(Intent.ACTION_MAIN);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.addCategory(Intent.CATEGORY_HOME);
//                startActivity(i);
                ((MusicApplication) getApplication()).clearActivityList();
            } else {
                new LocalInfoOp().changeDownloadToStop();
                ((MusicApplication) getApplication()).exit();
            }
        } else {
            if (((MusicApplication) getApplication()).getPlayerService().isPlaying()) {//后台播放
                CustomToast.getInstance().showToast(R.string.alert_home, CustomToast.LENGTH_LONG);
            } else {
                CustomToast.getInstance().showToast(R.string.alert_exit, CustomToast.LENGTH_LONG);
            }
            doExitInOneSecond();
        }
    }


    private void doExitInOneSecond() {
        isExit = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isExit = false;
            }
        }, 2000);// 2秒内再点有效
    }

    private void setNetwork(Context context) {
        CustomSnackBar.make(root, context.getString(R.string.net_no_net)).warning(context.getString(R.string.net_set), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private static class DrawerLayoutStateListener extends
            DrawerLayout.SimpleDrawerListener {
        private final WeakReference<MainActivity> mWeakReference;

        public DrawerLayoutStateListener(MainActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        /**
         * 当导航菜单滑动的时候被执行
         */
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            if (mWeakReference.get() != null) {
                mWeakReference.get().menu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW, 2 - slideOffset);
            }
        }

        /**
         * 当导航菜单打开时执行
         */
        @Override
        public void onDrawerOpened(View drawerView) {
        }

        /**
         * 当导航菜单关闭时执行
         */
        @Override
        public void onDrawerClosed(View drawerView) {
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<MainActivity> {
        @Override
        public void handleMessageByRef(MainActivity mainActivity, Message msg) {

        }
    }


//    @SuppressLint("NeedOnRequestPermissionsResult")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
//
//    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    public void initLocation() {
//
//    }
//
//    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    public void locationDenied() {
//
//        CustomToast.getInstance().showToast("存储权限开通才可以正常使用app，请到系统设置中开启", 3000);
//
//    }

    private void initPush() {
//        InitPush mInitPush = InitPush.getInstance();//初始化改变
//        PushConfig config = new PushConfig();
//        //HUAWEI
//        config.HUAWEI_ID = getString(R.string.huawei_id);
//        config.HUAWEI_SECRET = getString(R.string.huawei_key);
//
//        config.MI_ID = getString(R.string.xiaomi_id);
//        config.MI_KEY = getString(R.string.xiaomi_key);
//        config.MI_SECRET = getString(R.string.xiaomi_secret);
//
//        //OPPO
//        config.OPPO_ID = getString(R.string.oppo_id);
//        config.OPPO_KEY = getString(R.string.oppo_key);
//        config.OPPO_SECRET = getString(R.string.oppo_secret);
//        config.OPPO_MASTER_SECRET = getString(R.string.oppo_push_secret);//新增
//        mInitPush.setInitPush(this, config);//包含了OPPO获取Token
//        mInitPush.isShowToast = false;//是否开启推送模块的toast,默认关闭
//        String token = mInitPush.getToken(mContext).token;//获取token的方法 可能为空!!!
//        mInitPush.setSignPushCallback(new InitPush.GetSignPush() {
//            @Override
//            public void signPush(String s, int i, String s1) {
//                ToastUtil.showToast(mContext,s + "\n" + s1);
//            }
//        });//非必要，返回token

    }

    private static final String TAG = "MainActivity";

    /**
     * 建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
     */
    private void preVerify() {
        //设置在1000-10000之内
        FlyVerify.setTimeOut(5000);
        //移动的debug tag 是CMCC-SDK,电信是CT_ 联通是PriorityAsyncTask
        FlyVerify.setDebugMode(true);
//        FlyVerify.setUseCache(true);
        FlyVerify.preVerify(new PreVerifyCallback() {
            @Override
            public void onComplete(Void data) {
                if (Constant.devMode) {
                    Toast.makeText(context, "预登录成功", Toast.LENGTH_LONG).show();
                }
                Log.e(TAG, "onComplete.isPreVerifyDone  onComplete");
                FlyVerify.autoFinishOAuthPage(false);
//                FlyVerify.setUiSettings(CustomizeUtils.customizeUi());
            }

            @Override
            public void onFailure(VerifyException e) {

                Log.e(TAG, "onFailure.isPreVerifyDone  onFailure");
                String errDetail = null;
                if (e != null) {
                    errDetail = e.getMessage();
                }
                Log.e(TAG, "onFailure errDetail " + errDetail);
                if (Constant.devMode) {
                    // 登录失败
                    Log.e(TAG, "preVerify failed", e);
                    // 错误码
                    int errCode = e.getCode();
                    // 错误信息
                    String errMsg = e.getMessage();
                    // 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
                    String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
                    if (!TextUtils.isEmpty(errDetail)) {
                        msg += "\n详细信息: " + errDetail;
                    }
                    Log.e(TAG, msg);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
