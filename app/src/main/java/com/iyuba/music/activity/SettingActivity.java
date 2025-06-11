package com.iyuba.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.module.commonvar.CommonVars;
import com.iyuba.music.R;
import com.iyuba.music.activity.discover.WordSetActivity;
import com.iyuba.music.activity.me.TeenActivity;
import com.iyuba.music.activity.study.StudySetActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.model.ClearUserResponse;
import com.iyuba.music.file.FileUtil;
import com.iyuba.music.fragment.ClearUserFragment;
import com.iyuba.music.fragment.IDialogResultListener;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.retrofit.IyumusicRetrofitNetwork;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.iyuba.music.widget.roundview.RoundRelativeLayout;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 10202 on 2015/11/26.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private RoundRelativeLayout feedback, helpUse, wordSet, studySet, share, skin, versionFeature, moreApp, updateDomain;
    private RoundRelativeLayout language, push, clearPic, clearAudio;
    private TextView currLanguage, picCache, audioCache, currSkin;
    private SwitchButton currPush;
    private RoundTextView logout;
    private RoundTextView cancellation;
    private RoundTextView protocol;
    private RoundTextView usage;
    private static final String TAG = SettingActivity.class.getSimpleName();
    private ClearUserFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        RoundRelativeLayout setting_rl_teen = findViewById(R.id.setting_rl_teen);
        setting_rl_teen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SettingActivity.this, TeenActivity.class ));
            }
        });

        share = (RoundRelativeLayout) findViewById(R.id.setting_share);
        AddRippleEffect.addRippleEffect(share);
        skin = (RoundRelativeLayout) findViewById(R.id.setting_skin);
        skin.setVisibility(View.GONE);
//        AddRippleEffect.addRippleEffect(skin);
        versionFeature = (RoundRelativeLayout) findViewById(R.id.setting_version_feature);
        versionFeature.setVisibility(View.GONE);
//        AddRippleEffect.addRippleEffect(versionFeature);
        feedback = (RoundRelativeLayout) findViewById(R.id.setting_feedback);
        AddRippleEffect.addRippleEffect(feedback);
        moreApp = (RoundRelativeLayout) findViewById(R.id.setting_more_app);
//        moreApp.setVisibility(View.GONE); //vivo平台不能推荐的应用超过6个，单独隐藏
        AddRippleEffect.addRippleEffect(moreApp);
        helpUse = (RoundRelativeLayout) findViewById(R.id.setting_help_use);
        AddRippleEffect.addRippleEffect(helpUse);
        updateDomain = (RoundRelativeLayout) findViewById(R.id.setting_update_domain);
        AddRippleEffect.addRippleEffect(updateDomain);
        wordSet = (RoundRelativeLayout) findViewById(R.id.setting_word_set);
        AddRippleEffect.addRippleEffect(wordSet);
        studySet = (RoundRelativeLayout) findViewById(R.id.setting_study_set);
        AddRippleEffect.addRippleEffect(studySet);
        language = (RoundRelativeLayout) findViewById(R.id.setting_language);
        AddRippleEffect.addRippleEffect(language);
        push = (RoundRelativeLayout) findViewById(R.id.setting_push);
        AddRippleEffect.addRippleEffect(push);
        clearPic = (RoundRelativeLayout) findViewById(R.id.setting_pic_clear);
        AddRippleEffect.addRippleEffect(clearPic);
        clearAudio = (RoundRelativeLayout) findViewById(R.id.setting_audio_clear);
        AddRippleEffect.addRippleEffect(clearAudio);
        picCache = (TextView) findViewById(R.id.setting_pic_cache);
        audioCache = (TextView) findViewById(R.id.setting_audio_cache);
        currLanguage = (TextView) findViewById(R.id.setting_curr_language);
        currSkin = (TextView) findViewById(R.id.setting_curr_skin);
        currPush = (SwitchButton) findViewById(R.id.setting_curr_push);
        logout = (RoundTextView) findViewById(R.id.setting_logout);
        cancellation = (RoundTextView) findViewById(R.id.setting_cancel);
        protocol = (RoundTextView) findViewById(R.id.setting_protocol);
        AddRippleEffect.addRippleEffect(logout);
        usage = (RoundTextView) findViewById(R.id.setting_usage);
        usage.setOnClickListener(this);
        if (Constant.APP_NAME_PRIVACY.equalsIgnoreCase("隐私政策")) {
            usage.setVisibility(View.VISIBLE);
        } else {
            usage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        skin.setOnClickListener(this);
        versionFeature.setOnClickListener(this);
        feedback.setOnClickListener(this);
        helpUse.setOnClickListener(this);
        wordSet.setOnClickListener(this);
        studySet.setOnClickListener(this);
        language.setOnClickListener(this);
        currLanguage.setOnClickListener(this);
        push.setOnClickListener(this);
        clearPic.setOnClickListener(this);
        clearAudio.setOnClickListener(this);
        currSkin.setOnClickListener(this);
        logout.setOnClickListener(this);
        cancellation.setOnClickListener(this);
        protocol.setOnClickListener(this);
        share.setOnClickListener(this);
        updateDomain.setOnClickListener(this);
        moreApp.setOnClickListener(this);
        currPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPushState();
                if (isChecked) {
                    currPush.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                    CustomToast.getInstance().showToast(R.string.setting_push_on);
                } else {
                    currPush.setBackColorRes(R.color.background_light);
                    CustomToast.getInstance().showToast(R.string.setting_push_off);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.setting_title);
        if (ConfigManager.getInstance().isPush()) {
            currPush.setCheckedImmediatelyNoEvent(true);
            currPush.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currPush.setCheckedImmediatelyNoEvent(false);
            currPush.setBackColorRes(R.color.background_light);
        }
        handler.sendEmptyMessage(1);
    }

    protected void changeUIResumeByPara() {
        currLanguage.setText(getLanguage(ConfigManager.getInstance().getLanguage()));
//        currSkin.setText(getSkin(SkinManager.getInstance().getCurrSkin()));

        if (AccountManager.getInstance().checkUserLogin()) {
            logout.setVisibility(View.VISIBLE);
            cancellation.setVisibility(View.VISIBLE);
        } else {
            logout.setVisibility(View.GONE);
            cancellation.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (changeProperty) {
            startActivity(new Intent(context, MainActivity.class));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.setting_version_feature) {
            StartFragment.showVersionFeature(context);
        } else if (id == R.id.setting_skin || id == R.id.setting_curr_skin) {
            startActivity(new Intent(context, SkinActivity.class));
        } else if (id == R.id.setting_share) {/*String text = getResources().getString(R.string.setting_share_content,
                        ConstantManager.appName)
                        + "：http://app." + ConstantManager.IYUBA_CN + "android/androidDetail.jsp?id="
                        + ConstantManager.appId;
                Intent shareInt = new Intent(Intent.ACTION_SEND);
                shareInt.setType("text*//*");
                shareInt.putExtra(Intent.EXTRA_TEXT, text);
                shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareInt.putExtra("sms_body", text);
                startActivity(Intent.createChooser(shareInt, context.getString(R.string.setting_share_ways)));*/
            share();
        } else if (id == R.id.setting_feedback) {
            startActivity(new Intent(context, FeedbackActivity.class));
        } else if (id == R.id.setting_help_use) {
            Intent intent = new Intent(context, HelpUseActivity.class);
            intent.putExtra("UsePullDown", true);
            startActivity(intent);
        } else if (id == R.id.setting_language || id == R.id.setting_curr_language) {
            popLanguageDialog();
        } else if (id == R.id.setting_push) {
            currPush.setChecked(!currPush.isChecked());
            setPushState();
        } else if (id == R.id.setting_update_domain) {
            requestNewDomain();
        } else if (id == R.id.setting_audio_clear) {
            CustomDialog.clearDownload(context, R.string.article_clear_cache_hint, new IOperationResult() {
                @Override
                public void success(Object object) {
                    ThreadPoolUtil.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            FileUtil.clearFileDir(RuntimeManager.getApplication().getProxy().getCacheFolder());
                        }
                    });
                    handler.sendEmptyMessage(3);
                }

                @Override
                public void fail(Object object) {

                }
            });
        } else if (id == R.id.setting_pic_clear) {
            ImageUtil.clearImageAllCache(this);
            ThreadPoolUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    FileUtil.clearFileDir(new File(ConstantManager.crashFolder));
                    FileUtil.clearFileDir(new File(ConstantManager.updateFolder));
                    FileUtil.clearFileDir(new File(ConstantManager.imgFile));
                }
            });
            handler.sendEmptyMessage(2);
        } else if (id == R.id.setting_word_set) {
            startActivity(new Intent(context, WordSetActivity.class));
        } else if (id == R.id.setting_study_set) {
            startActivity(new Intent(context, StudySetActivity.class));
        } else if (id == R.id.setting_more_app) {
            Intent intent;
            if (Constant.APP_HUAWEI_PRIVACY) {
                CustomToast.getInstance().showToast("暂时没有此项功能。");
                return;
            }
            intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", "http://app." + ConstantManager.IYUBA_CN + "android");
            intent.putExtra("title", context.getString(R.string.setting_moreapp));
            startActivity(intent);
        } else if (id == R.id.setting_logout) {
            logout();
        } else if (id == R.id.setting_cancel) {
            cancellation();
        } else if (id == R.id.setting_protocol) {
            clickProtocol();
        } else if (id == R.id.setting_usage) {
            clickUsage();
        }
    }

    private void requestNewDomain() {

        String hubUrl = "http://111.198.52.105:8085/api/getDomain.jsp?appId=" + Constant.APPID
                + "&short1=" + Constant.IYUBA_CN.replace("/", "")
                + "&short2=" + Constant.IYUBA_COM.replace("/", "");
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(10, TimeUnit.SECONDS).
                readTimeout(10, TimeUnit.SECONDS).
                writeTimeout(10, TimeUnit.SECONDS).build();
        okhttp3.Request request = new okhttp3.Request.Builder().headers(new Headers.Builder().build()).url(hubUrl).build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(context, "您更新服务器设置失败，请稍后重试或者联系客服");
                    }
                });
                if (e != null) {
                    Log.e("WelcomeActivity", "github onFailure = " + e.getMessage());
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                if ((response == null) || (response.body() == null)) {
                    Log.e("WelcomeActivity", "github onResponse null? ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(context, "您更新服务器设置失败，请稍后重试或者联系客服");
                        }
                    });
                    return;
                }
                try {
                    String data = response.body().string();
                    Log.e("WelcomeActivity", "github response data = " + data);
                    JSONObject jsonRoot = new JSONObject(data);
                    int updateflg = jsonRoot.optInt("updateflg");
                    if (updateflg == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(context, "已更新");
                            }
                        });
                        return;
                    }
                    String short1 = jsonRoot.optString("short1");
                    if (!TextUtils.isEmpty(short1) && !short1.equalsIgnoreCase(ConfigManager.getInstance().getDomainShort())) {
                        ConfigManager.getInstance().setDomainShort(short1);
                        CommonVars.domain = short1;
                        Constant.IYUBA_CN = short1 + "/";
                        Log.e("WelcomeActivity", "github response update domain = " + Constant.IYUBA_CN);
//                        HeadNewsApplication.setDomainShort();
                    }
                    String short2 = jsonRoot.optString("short2");
                    if (!TextUtils.isEmpty(short2) && !short2.equalsIgnoreCase(ConfigManager.getInstance().getDomainLong())) {
                        ConfigManager.getInstance().setDomainLong(short2);
                        CommonVars.domainLong = short2;
                        Constant.IYUBA_COM = short2 + "/";
                        Log.e("WelcomeActivity", "github response update domainLong = " + Constant.IYUBA_COM);
//                        HeadNewsApplication.setDomainLong();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(context, "您已经成功更新服务器设置");
                        }
                    });
                    return;
                } catch (Exception e) {
                    if (e != null) {
                        Log.e("WelcomeActivity", "github response Exception = " + e.getMessage());
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(context, "您更新服务器设置失败，请稍后重试或者联系客服");
                    }
                });
            }
        });
    }

    private void setPushState() {
        ConfigManager.getInstance().setPush(!ConfigManager.getInstance().isPush());
//        if (ConfigManager.getInstance().isPush()) {
//            MiPushClient.enablePush(context);
//        } else {
//            MiPushClient.disablePush(context);
//        }
    }

    private void popLanguageDialog() {
        final MyMaterialDialog languageDialog = new MyMaterialDialog(context);
        languageDialog.setTitle(R.string.setting_language_hint);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.language)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (ConfigManager.getInstance().getLanguage() != position) {
                    onLanguageChanged(position);
                }
                languageDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(ConfigManager.getInstance().getLanguage());
        languageList.setAdapter(adapter);
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageDialog.setContentView(root);
        languageDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageDialog.dismiss();
            }
        });
        languageDialog.show();
    }

    private void onLanguageChanged(int language) {
        ConfigManager.getInstance().setLanguage(language);
        ChangePropery.updateLanguageMode(language);
        Intent intent = new Intent(ChangePropertyBroadcast.FLAG);
        intent.putExtra(ChangePropertyBroadcast.SOURCE, this.getClass().getSimpleName());
        sendBroadcast(intent);
    }

    private String getLanguage(int language) {
        String[] languages = context.getResources().getStringArray(R.array.language);
        return languages[language];
    }

    private String getSkin(String skin) {
        int pos = GetAppColor.getInstance().getSkinFlg(skin);
        String[] skins = context.getResources().getStringArray(R.array.flavors);
        return skins[pos];
    }

    private void logout() {
        final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(context);
        mMaterialDialog.setTitle(R.string.app_name);
        if (AccountManager.getInstance().checkUserLogin()) {
            mMaterialDialog.setMessage(R.string.personal_logout_textmore)
                    .setPositiveButton(R.string.personal_logout_exit, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
//                            InitPush.getInstance().unRegisterToken(context, Integer.parseInt(AccountManager.getInstance().getUserId()));
                            AccountManager.getInstance().loginOut();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });

        } else {
            mMaterialDialog.setMessage(R.string.personal_no_login)
                    .setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
        }
        mMaterialDialog.show();
    }

    private void cancellation() {
        final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(context);
        mMaterialDialog.setTitle(R.string.app_name);
        if (AccountManager.getInstance().checkUserLogin()) {
            mMaterialDialog.setMessage(R.string.clear_user_alert)
                    .setPositiveButton(R.string.personal_logout, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
//                            InitPush.getInstance().unRegisterToken(context, Integer.parseInt(AccountManager.getInstance().getUserId()));
//                            AccountManager.getInstance().loginOut();
//                            finish();
                            startClearUser();
                        }
                    })
                    .setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });

        } else {
            mMaterialDialog.setMessage(R.string.personal_no_login)
                    .setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
        }
        mMaterialDialog.show();
    }

    private void startClearUser() {
        fragment = new ClearUserFragment();
        fragment.show(getFragmentManager(), "ClearUser");
        fragment.setOnResult(new IDialogResultListener() {
            @Override
            public void onDataResult(Object result) {
                if (result == null) {
                    Log.e(TAG, "clickClearUser onDataResult is null.");
                    CustomToast.getInstance().showToast(R.string.register_check_pwd_1);
                    return;
                }
                String userPassword = (String) result;
                Log.e(TAG, "onDataResult userPassword " + userPassword);
                if (userPassword.length() < 6 || userPassword.length() > 20) {
                    Log.e(TAG, "clickClearUser onDataResult length is not ok.");
                    CustomToast.getInstance().showToast(R.string.register_pwd_hint);
                    return;
                }
                // no need check, as user may change password by web page
                String sign = MD5.getMD5ofStr(11005 + AccountManager.getInstance().getUserName() + MD5.getMD5ofStr(userPassword) + "iyubaV2");
                IyumusicRetrofitNetwork.getApiComService().clearUser("11005", AccountManager.getInstance().getUserName(),
                        MD5.getMD5ofStr(userPassword), sign, "json").enqueue(new Callback<ClearUserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ClearUserResponse> call, @NonNull Response<ClearUserResponse> response) {
                        if ((response == null) || (response.body() == null)) {
                            CustomToast.getInstance().showToast(R.string.user_cancel_fail);
                            Log.e(TAG, "ChangeUserName onResponse is null. ");
                            return;
                        }
                        ClearUserResponse info = response.body();
                        Log.e(TAG, "ChangeUserName onResponse result " + info.getResult());
                        if ("101".equals(info.getResult())) {
//                            InitPush.getInstance().unRegisterToken(context, Integer.parseInt(AccountManager.getInstance().getUserId()));
                            AccountManager.getInstance().loginOut();
                            CustomToast.getInstance().showToast(R.string.user_cancel_success);
                            ConfigManager.getInstance().putString(ConfigManager.USER_NAME, "");
                            ConfigManager.getInstance().putString(ConfigManager.USER_PASS, "");
                            finish();
                        } else if ("102".equals(info.getResult())) {
                            CustomToast.getInstance().showToast("该用户名不存在");
                        } else if ("103".equals(info.getResult())) {
                            CustomToast.getInstance().showToast("用户名密码不匹配");
                        } else {
                            CustomToast.getInstance().showToast(R.string.user_cancel_fail);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ClearUserResponse> call, @NonNull Throwable t) {
                        if (t != null) {
                            Log.e(TAG, "ChangeUserName onFailure " + t.getMessage());
                        }
                        CustomToast.getInstance().showToast("注销失败！请检查网络或稍后再试。");
                    }
                });
            }
        });
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<SettingActivity> {
        @Override
        public void handleMessageByRef(final SettingActivity activity, Message msg) {
            switch (msg.what) {
                case 1:
                    activity.audioCache.setText(FileUtil.formetFileSize(FileUtil.getFolderSize(RuntimeManager.getApplication().getProxy().getCacheFolder())));
                    long size = 0;
                    size += FileUtil.getGlideCacheSize(activity);
                    size += FileUtil.getFolderSize(new File(ConstantManager.crashFolder));
                    size += FileUtil.getFolderSize(new File(ConstantManager.updateFolder));
                    size += FileUtil.getFolderSize(new File(ConstantManager.imgFile));
                    activity.picCache.setText(FileUtil.formetFileSize(size));
                    break;
                case 2:
                    activity.picCache.setText("0B");
                    break;
                case 3:
                    activity.audioCache.setText("0B");
                    break;
            }
        }
    }

    private void share() {
        //关闭sso授权
        ShareSDK.removeCookieOnAuthorize(true);
        String text = "讲真、你与学霸只有一个" + "听歌学英语" + "的距离";
        String siteUrl = "http://app." + ConstantManager.IYUBA_CN + "android/androidDetail.jsp?id="
                + Constant.APPID;
        String imageUrl = "http://app." + ConstantManager.IYUBA_CN + "android/images/iyumusic/iyumusic.png";


        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("听歌学英语");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//         oks.setImagePath(getResources().getDrawable(R.drawable.ic_launcher));
        // imageUrl是、We、b图片路径，sina需要开通权限
        oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("爱语吧的这款应用" + "听歌学英语" + "真的很不错啊~推荐！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("听歌学英语");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.e("okCallbackonError", "onError" + arg2.toString());
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Log.e("okCallbackonComplete", "onComplete");

            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("share_cancel", "" + arg1);
                Log.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(SettingActivity.this);
    }

    private void clickProtocol() {
        if (Constant.APP_NAME_PRIVACY.equalsIgnoreCase("隐私政策")) {
            Intent intent = WebActivity.buildIntent(context, Constant.PROTOCOL_VIVO_PRIVACY + Constant.APP_Name, "用户隐私协议");
            context.startActivity(intent);
        } else {
            Intent intent = WebActivity.buildIntent(context, Constant.PROTOCOL_URL_IYUBA + Constant.APP_Name, "用户隐私协议");
            context.startActivity(intent);
        }
    }

    private void clickUsage() {
        Intent intent = WebActivity.buildIntent(context, Constant.PROTOCOL_VIVO_USAGE + Constant.APP_Name, "用户协议");
        context.startActivity(intent);
    }

}
