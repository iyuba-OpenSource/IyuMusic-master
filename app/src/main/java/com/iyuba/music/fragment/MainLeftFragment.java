package com.iyuba.music.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.Gson;
import com.iyuba.module.movies.event.IMovieGoVipCenterEvent;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.AboutActivity;
import com.iyuba.music.activity.ChangeNameActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.RegistSubmitActivity;
import com.iyuba.music.activity.SettingActivity;
import com.iyuba.music.activity.SignActivity;
import com.iyuba.music.activity.WxOfficialAccountActivity;
import com.iyuba.music.activity.discover.DiscoverActivity;
import com.iyuba.music.activity.discover.Video2Activity;
import com.iyuba.music.activity.me.ChangePhotoActivity;
import com.iyuba.music.activity.me.CreditActivity;
import com.iyuba.music.activity.me.ImproveUserActivity;
import com.iyuba.music.activity.me.VipCenterActivity;
import com.iyuba.music.activity.me.WriteStateActivity;
import com.iyuba.music.activity.vip.NewVipCenterActivity;
import com.iyuba.music.adapter.OperAdapter;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.model.RegisterMobResponse;
import com.iyuba.music.entity.StudyTimeBeanNew;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.event.LoginEvent;
import com.iyuba.music.event.LoginOutEventbus;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.request.merequest.StudyTimeBeanRequest;
import com.iyuba.music.retrofit.IyumusicRetrofitNetwork;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.LoginResult;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.SignInDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.mob.MobSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.fly.verify.FlyVerify;
import cn.fly.verify.VerifyCallback;
import cn.fly.verify.common.exception.VerifyException;
import cn.fly.verify.datatype.UiSettings;
import cn.fly.verify.datatype.VerifyResult;
import cn.fly.verify.ui.component.CommonProgressDialog;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.event.UserNameChangeEvent;
import personal.iyuba.personalhomelibrary.event.UserPhotoChangeEvent;
import personal.iyuba.personalhomelibrary.ui.groupChat.GroupChatManageActivity;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 10202 on 2015/12/29.
 */
public class MainLeftFragment extends BaseFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private Context context;
    private View root;
    //侧边栏
    private UserInfo userInfo;
    private MaterialRippleLayout login, noLogin, sign, signIn;
    private RecyclerView menuList;
    private OperAdapter operAdapter;
    private VipPhoto personalPhoto;
    private TextView personalName, personalGrade, personalCredits, personalFollow, personalFan;
    private TextView personalSign;
    private TextView signInHint;
    private RoundTextView signInHandle;
    private ImageView signInIcon;
    private SignInDialog signInDialog;
    //底部
    private View about, exit;

    private String loadFiledHint = "打卡加载失败";
    private IyubaDialog waitingDialog;

    private boolean isChange = false;
    private static final String TAG = "MainLeftFragment";
    private int defaultUi = 1;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.layout_main_left, null);
        initWidget();
        setOnClickListener();
//        getPersonalInfo();
        autoLogin();
        waitingDialog = WaitingDialog.create(context, "");
        EventBus.getDefault().register(this);
        return root;
    }

    private void initWidget() {
        menuList = (RecyclerView) root.findViewById(R.id.oper_list);
        login = (MaterialRippleLayout) root.findViewById(R.id.personal_login);
        noLogin = (MaterialRippleLayout) root.findViewById(R.id.personal_nologin);
        sign = (MaterialRippleLayout) root.findViewById(R.id.sign_layout);
        signIn = (MaterialRippleLayout) root.findViewById(R.id.sign_in_layout);
        personalPhoto = (VipPhoto) root.findViewById(R.id.personal_photo);
        personalName = (TextView) root.findViewById(R.id.personal_name);
        personalGrade = (TextView) root.findViewById(R.id.personal_grade);
        personalCredits = (TextView) root.findViewById(R.id.personal_credit);
        personalFollow = (TextView) root.findViewById(R.id.personal_follow);
        personalFan = (TextView) root.findViewById(R.id.personal_fan);
        personalSign = (TextView) root.findViewById(R.id.personal_sign);
        about = root.findViewById(R.id.about);
        exit = root.findViewById(R.id.exit);
        signInIcon = (ImageView) root.findViewById(R.id.sign_in_icon);
        signInHint = (TextView) root.findViewById(R.id.sign_in_hint);
        signInHandle = (RoundTextView) root.findViewById(R.id.sign_in_handle);
        AddRippleEffect.addRippleEffect(signInHandle);
        operAdapter = new OperAdapter(context);
    }

    private void setOnClickListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                PersonalHome.setMainPath(MainLeftFragment.class.getName());
                AccountManager.getInstance().setPersonalInfo();
                startActivity(PersonalHomeActivity.buildIntent(getActivity(), Integer.parseInt(AccountManager.getInstance().getUserId()), "", 0));
            }
        });
        noLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(context, LoginActivity.class), 101);
                clickLogin();
            }
        });
        personalName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    Intent userInfo = new Intent(context, ChangeNameActivity.class);
                    userInfo.putExtra(ChangeNameActivity.RegisterMob, 0);
                    startActivity(userInfo);
                } else {
                    clickLogin();
                }
            }
        });
//        personalPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                if (AccountManager.getInstance().checkUserLogin()) {
////
////                    startActivity(new Intent(context, ChangePhotoActivity.class));
////                }
//            }
//        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, WriteStateActivity.class));
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AboutActivity.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccountManager.getInstance().checkUserLogin()) {

                    AccountManager.getInstance().setLoginState(AccountManager.SIGN_OUT);
                    AccountManager.getInstance().loginOut();
                    operAdapter.setOldTextList();
                    changeUIResumeByPara();
                    EventBus.getDefault().post(new LoginOutEventbus());
//                    RuntimeManager.getApplication().exit();
                } else {
                    ToastUtil.showToast(context, "您还没有登录呢！");
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signGo();
            }
        });
        signInHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signGo();
            }
        });


        // menuList.getItemAnimator().setChangeDuration(0); 或者采用这个方案
        menuList.setLayoutManager(new LinearLayoutManager(context));
//        menuList.addItemDecoration(new DividerItemDecoration());

        menuList.setAdapter(operAdapter);
        ((SimpleItemAnimator) menuList.getItemAnimator()).setSupportsChangeAnimations(false);
        operAdapter.setItemClickListener((view, text) -> {
            if (text == R.string.oper_vip) {
                if (AccountManager.getInstance().checkUserLogin()) {
//                        startActivity(new Intent(context, VipCenterActivity.class));
                    startActivity(new Intent(context, NewVipCenterActivity.class));

                } else {
//                        CustomDialog.showLoginDialog(context, false, () -> startActivity(new Intent(context, VipCenterActivity.class)));
                    clickLogin();
                }
            } else if (text == R.string.oper_group) {
                Log.e(TAG, "GroupChatManageActivity.checkUserLogin " + AccountManager.getInstance().checkUserLogin());
                if (AccountManager.getInstance().checkUserLogin()) {
                    GroupChatManageActivity.start(context, ConfigManager.GROUP_ID,
                            ConfigManager.GROUP_NAME, true);
                } else {
//                        ToastUtil.showToast(context, "请先登录");
                    clickLogin();
                }
            } else if (text == R.string.oper_message_center) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    PersonalHome.setMainPath(MainLeftFragment.class.getName());
                    AccountManager.getInstance().setPersonalInfo();
                    startActivity(new Intent(context, MessageActivity.class));
                } else {
//                        ToastUtil.showToast(context, "请先登录");
                    clickLogin();
                }
                //
//                    case 0:  送书
//                        Intent i = new Intent(context, SendBookActivity.class);
//                        context.startActivity(i);
//                        break;

//                    case 1:
//                        HeadlinesRuntimeManager.setApplicationContext(RuntimeManager.getContext());
//                        String userid = "0";
//                        if (AccountManager.getInstance().checkUserLogin()) {
//                            userid = AccountManager.getInstance().getUserId();
//                        }
//                        startActivity(MainHeadlinesActivity.getIntent2Me(context, userid, "209", "music", (DownloadService.checkVip() ? "1" : "0")));
//                        startActivity(new Intent(context, AppGroundActivity.class));
//                        break;

//                case 3: //视频模块
//                    startActivity(new Intent(context, VideoActivity.class));
//
//                    break;
//                case 3: //微课
//
//                    ImoocManager.appId = ConstantManager.appId;
//                    ArrayList<Integer> list = new ArrayList<>();
//
//                    list.add(-2); //全部课程
//                    list.add(-1); //最新课程
//                    list.add(2); //英语四级
//                    list.add(3); //VOA英语
//                    list.add(4); //英语六级
//                    list.add(7); //托福TOEFL
//                    list.add(8); //考研英语一
//                    list.add(9); //BBC英语
//                    list.add(21); //新概念英语
//                    list.add(22); //走遍美国
//                    list.add(28); //学位英语
//                    list.add(52); //考研英语二
//                    list.add(52); //雅思
//                    list.add(91); //中职英语
//
//
//                    ImoocManager.appId = ConstantManager.appId;
//                    MobClassActivity.buildIntent(context, -2, true, list);
//                    startActivity(new Intent(context, MobClassActivity.class));
//                    break;
            } else if (text == R.string.oper_discover) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    startActivity(new Intent(context, DiscoverActivity.class));
                } else {
                    clickLogin();
                }
            } else if (text == R.string.oper_small_video) {
                startActivity(new Intent(context, Video2Activity.class));
                    /*   case 4://我的
                    if (AccountManager.getInstance().checkUserLogin()) {
                        startActivity(new Intent(context, MeActivity.class));
                    } else {
                        clickLogin();
                    }
                    break;*/

//                    case 5: 睡眠模式
//                        startActivity(new Intent(context, SleepActivity.class));
//                        break;
            } else if (text == R.string.oper_wx) {
                startActivity(new Intent(context, WxOfficialAccountActivity.class));
            } else if (text == R.string.oper_setting) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    startActivity(new Intent(context, SettingActivity.class));
                } else {
                    clickLogin();
                }
            } else if (text == R.string.oper_night) {

                ConfigManager.getInstance().setNight(!ConfigManager.getInstance().isNight());
                ChangePropery.updateNightMode(ConfigManager.getInstance().isNight());
                Intent intent = new Intent(ChangePropertyBroadcast.FLAG);
                intent.putExtra(ChangePropertyBroadcast.SOURCE, getActivity().getClass().getSimpleName());
                context.sendBroadcast(intent);
            } else if (text == R.string.chakan_jifen) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    startActivity(new Intent(context, CreditActivity.class));
                } else {
                    clickLogin();
                }
            } else if (text == R.string.user_official) {//                    if (AccountManager.getInstance().checkUserLogin()) {
//                        Intent userInfo = new Intent(context, CalendarActivity.class);
//                        startActivity(userInfo);
//                    } else {
//                        clickLogin();
//                    }
//                    break;
//                case 10:
                final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(context);
                mMaterialDialog.setTitle(R.string.app_name)
                        .setMessage(R.string.personal_logout_textmore)
                        .setPositiveButton(R.string.personal_logout_exit, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
//                                    InitPush.getInstance().unRegisterToken(context, Integer.parseInt(AccountManager.getInstance().getUserId()));
                                AccountManager.getInstance().loginOut();

                                changeUIResumeByPara();
                            }
                        })
                        .setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
            }
        });
    }

    private void changeUIResumeByPara() {
        handler.removeCallbacksAndMessages(null);
        if (AccountManager.getInstance().getUserId().equals("0")) {
            login.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
            sign.setVisibility(View.GONE);
            signIn.setVisibility(View.GONE);
        } else {
            login.setVisibility(View.VISIBLE);
            noLogin.setVisibility(View.GONE);
            sign.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
            if (AccountManager.getInstance().checkUserLogin()) {
                Log.e("===", AccountManager.getInstance().getMoney() + "---");
                try {
                    if (TextUtils.isEmpty(AccountManager.getInstance().getMoney())) {
                        personalGrade.setText("钱包：" + "0.0" + "元");
                    } else {
                        double money = Double.parseDouble(AccountManager.getInstance().getMoney()) * 0.01;
                        personalGrade.setText("钱包：" + floatToString(money) + "元");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    personalGrade.setText("钱包：" + "0.0" + "元");
                }
                signInIcon.setImageResource(R.drawable.sign_in_icon);
                signInHandle.setText(R.string.oper_sign_in_handle);
                signInHint.setText(R.string.oper_sign_in);
                if (AccountManager.getInstance().getUserInfo() == null) {
                    userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
                    AccountManager.getInstance().setUserInfo(userInfo);
                }
                int vipInt = Integer.parseInt(AccountManager.getInstance().getUserInfo().getVipStatus());
                if (isChange) {
                    personalPhoto.setPhotoChange(AccountManager.getInstance().getUserInfo().getUid(), vipInt >= 1);
                    isChange = false;
                } else {
                    personalPhoto.setVipStateVisible(AccountManager.getInstance().getUserInfo().getUid(), vipInt >= 1);
                }
                if (userInfo == null || userInfo.getUid() == null || !userInfo.getUid().equals(AccountManager.getInstance().getUserId())) {
                    getPersonalInfo();
                }
            } else {
                signInIcon.setImageResource(R.drawable.temp_account_icon);
                signInHandle.setText(R.string.oper_visitor_handle);
                signInHint.setText(R.string.login_da_ka);
                personalPhoto.setVisitor();
                String visitorId = AccountManager.getInstance().getUserId();
                userInfo = new UserInfo();
                userInfo.setUid(visitorId);
                userInfo.setUsername(visitorId);
                AccountManager.getInstance().setUserInfo(userInfo);
                getPersonalInfo();
            }
        }
        int sleepSecond = ((MusicApplication) getActivity().getApplication()).getSleepSecond();
        if (sleepSecond != 0) {
            handler.sendEmptyMessage(1);
        }
    }

    private void autoLogin() {
        Log.e(TAG, "autoLogin == " + ConfigManager.getInstance().isAutoLogin());
        if (ConfigManager.getInstance().isAutoLogin()) {        // 自动登录
            String[] userNameAndPwd = AccountManager.getInstance().getNameAndPwdFromSp();
            Log.e(TAG, "autoLogin userId == " + ConfigManager.getInstance().loadString(ConfigManager.USER_ID));
            if (!TextUtils.isEmpty(userNameAndPwd[0]) && !TextUtils.isEmpty(ConfigManager.getInstance().loadString(ConfigManager.USER_ID))) {
                AccountManager.getInstance().setLoginState(AccountManager.SIGN_IN);
                userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
                if ((userInfo != null)) {
                    AccountManager.getInstance().setUserInfo(userInfo);
                    AccountManager.getInstance().setUser();
                    operAdapter.setNewTextList();
                    updatePersonalInfoView();
                }
                if (!TextUtils.isEmpty(userNameAndPwd[1])) {
                    AccountManager.getInstance().login(userNameAndPwd[0], userNameAndPwd[1],
                            new IOperationResult() {
                                @Override
                                public void success(Object message) {
                                    Activity parent = getActivity();
//                                    Log.e("===", message.toString() + "---");
                                    if ("add".equals(message.toString()) && parent != null && !parent.isDestroyed()) {
                                        CustomSnackBar.make(root, context.getString(R.string.personal_daily_login)).info(context.getString(R.string.credit_check), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(context, CreditActivity.class));
                                            }
                                        });
                                    }

                                    getPersonalInfo();
//                                    updatePerson‘alInfoView();
                                    String userId = ConfigManager.getInstance().loadString(ConfigManager.USER_ID);
                                    Log.e("MainLeftFragment", "userId== " + userId);
                                    AccountManager.getInstance().setUserInfo(userInfo);
                                    AccountManager.getInstance().setUser();
                                    if (!TextUtils.isEmpty(userId) && ConfigManager.getInstance().loadBoolean("userId_" + userId, true)) {
                                        Intent userInfo = new Intent(context, ImproveUserActivity.class);
                                        userInfo.putExtra("regist", false);
                                        startActivity(userInfo);
                                    }
                                    if (AccountManager.getInstance().checkUserLogin()) {
                                        Log.e("===", AccountManager.getInstance().getMoney() + "---");
                                        try {
                                            double money = Double.parseDouble(AccountManager.getInstance().getMoney()) * 0.01;
                                            personalGrade.setText("钱包：" + floatToString(money) + "元");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            personalGrade.setText("钱包：" + "0.0" + "元");
                                        }
                                    }

                                }

                                @Override
                                public void fail(Object message) {
                                    Log.e(TAG, "autoLogin  fail== ");
                                }
                            });

                }
            } else {
                AccountManager.getInstance().setLoginState(AccountManager.SIGN_OUT);
            }
        } else {
            String[] userNameAndPwd = AccountManager.getInstance().getNameAndPwdFromSp();
            Log.e(TAG, "autoInfo userId == " + ConfigManager.getInstance().loadString(ConfigManager.USER_ID));
            if (!TextUtils.isEmpty(userNameAndPwd[0]) && !TextUtils.isEmpty(ConfigManager.getInstance().loadString(ConfigManager.USER_ID))) {
                AccountManager.getInstance().setLoginState(AccountManager.SIGN_IN);
                userInfo = new UserInfoOp().selectData(ConfigManager.getInstance().loadString(ConfigManager.USER_ID));
                if (userInfo != null) {
                    AccountManager.getInstance().setUserInfo(userInfo);
                    updatePersonalInfoView();
                    operAdapter.setNewTextList();
                }
            } else {
                AccountManager.getInstance().setLoginState(AccountManager.SIGN_OUT);
            }
        }
    }

    //获取个人信息
    private void getPersonalInfo() {
//        AccountManager.getInstance().getPersonalInfo(new IOperationResult() {
//            @Override
//            public void success(Object object) {
//                userInfo = AccountManager.getInstance().getUserInfo();
//                updatePersonalInfoView();
//                changeUIResumeByPara();
//            }
//
//            @Override
//            public void fail(Object object) {
//                userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
//                if (userInfo != null && !TextUtils.isEmpty(userInfo.getFollowing())) {     // 获取不到时采用历史数据
//                    AccountManager.getInstance().setUserInfo(userInfo);
//                } else {
//                    userInfo = AccountManager.getInstance().getUserInfo();
//                }
//                updatePersonalInfoView();
//            }
//        });
        String id = AccountManager.getInstance().getUserId();
        Map<String, String> para = new HashMap<>();
        para.put("protocol", "20001");
        para.put("platform", "android");
        para.put("id", id);
        para.put("myid", id);
        para.put("appid", Constant.APPID);
        para.put("format", "json");
        para.put("sign", com.iyuba.music.util.MD5.getMD5ofStr("20001" + id + "iyubaV2"));
        IyumusicRetrofitNetwork.getApiComService().userInfoApi(para).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                if (response == null) {
//                    Toast.makeText(context, R.string.edit_failure, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "ChangeUserName onResponse is null. ");
                    return;
                }
                if (response.isSuccessful()) {
                    UserInfo info = response.body();
                    if (info != null) {
                        if (userInfo == null) {
                            userInfo = info;
                        } else {
                            userInfo.update(info);
                        }
                        new UserInfoOp().saveData(userInfo);
                        AccountManager.getInstance().setUserInfo(userInfo);
                        AccountManager.getInstance().setUser();
                        updatePersonalInfoView();
                        changeUIResumeByPara();
                        Log.e(TAG, "ChangeUserName onResponse result " + info.toString());
                        return;
                    }
                }
                Log.e(TAG, "ChangeUserName onResponse body is empty.");
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                if (t != null) {
                    Log.e(TAG, "ChangeUserName onFailure " + t.getMessage());
                }
            }
        });
    }

    private void setPersonalInfoContent() {
        personalName.setText(userInfo.getUsername());
//        personalGrade.setText(context.getString(R.string.personal_grade,
//                UserInfo.getLevelName(context, TextUtils.isEmpty(userInfo.getIcoins()) ? 0 : Integer.parseInt(userInfo.getIcoins()))));
        personalCredits.setText(context.getString(R.string.personal_credits, TextUtils.isEmpty(userInfo.getIcoins()) ? "0" : userInfo.getIcoins()));
        if (TextUtils.isEmpty(userInfo.getText()) || "null".equals(userInfo.getText())) {
            personalSign.setText(R.string.personal_nosign);
        } else {
            personalSign.setText(userInfo.getText());
        }
        int follow = TextUtils.isEmpty(userInfo.getFollowing()) ? 0 : Integer.parseInt(userInfo.getFollowing());
        if (follow > 1000) {
            personalFollow.setText(context.getString(R.string.personal_follow, follow / 1000 + "k"));
            personalFollow.setTextColor(GetAppColor.getInstance().getAppColor());
        } else {
            personalFollow.setText(context.getString(R.string.personal_follow, String.valueOf(follow)));
        }
        int follower = TextUtils.isEmpty(userInfo.getFollower()) ? 0 : Integer.parseInt(userInfo.getFollower());
        if (follower > 10000) {
            personalFan.setText(context.getString(R.string.personal_fan, follower / 10000 + "w"));
            personalFan.setTextColor(GetAppColor.getInstance().getAppColor());
        } else {
            personalFan.setText(context.getString(R.string.personal_fan, String.valueOf(follower)));
        }

        if (AccountManager.getInstance().checkUserLogin()) {
            signInIcon.setImageResource(R.drawable.sign_in_icon);
            signInHandle.setText(R.string.oper_sign_in_handle);
            signInHint.setText(R.string.oper_sign_in);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 1) {// 登录的返回结果
            getPersonalInfo();
            CustomSnackBar.make(root, context.getString(R.string.personal_daily_login)).info(context.getString(R.string.credit_check), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, CreditActivity.class));
                }
            });
        } else if (requestCode == 101 && resultCode == 2) {// 登录+注册的返回结果
            getPersonalInfo();
            CustomSnackBar.make(root, context.getString(R.string.personal_change_photo)).info(context.getString(R.string.app_accept), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, ChangePhotoActivity.class));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("首页", "onResume");
        changeUIResumeByPara();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        operAdapter.setItemClickListener(null);
        login.setOnClickListener(null);
        noLogin.setOnClickListener(null);
        personalPhoto.setOnClickListener(null);
        sign.setOnClickListener(null);
        about.setOnClickListener(null);
        exit.setOnClickListener(null);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onBackPressed() {
        if (signInDialog != null && signInDialog.isShown()) {
            signInDialog.dismiss();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    private void updatePersonalInfoView() {
        Activity parent = getActivity();
        if ((userInfo != null) && (parent != null) && !parent.isDestroyed()) {
            personalPhoto.setVipStateVisible(userInfo.getUid(), userInfo.getVipStatus() != null && Integer.parseInt(userInfo.getVipStatus()) >= 1);
            setPersonalInfoContent();
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<MainLeftFragment> {
        @Override
        public void handleMessageByRef(final MainLeftFragment fragment, Message msg) {
            switch (msg.what) {
                case 1:
                    fragment.operAdapter.notifyItemChanged(4);
                    fragment.handler.sendEmptyMessageDelayed(1, 1000);
                    break;
            }
        }


    }

    private void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private long getDays() {
        //东八区;
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance(Locale.CHINA);
        long intervalMilli = now.getTimeInMillis() - cal.getTimeInMillis();
        long xcts = intervalMilli / (24 * 60 * 60 * 1000);
        return xcts;
    }

    /**
     * 打卡判断
     */
    private void signGo() {

        if (AccountManager.getInstance().checkUserLogin()) {
            waitingDialog.show();

            String uid = AccountManager.getInstance().getUserId();


            final String url = String.format(Locale.CHINA,
                    "http://daxue." + ConstantManager.IYUBA_CN + "ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uid, getDays());

            StudyTimeBeanRequest.exeRequest(url, new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.getInstance().showToast(msg);
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.getInstance().showToast(msg);
                }

                @Override
                public void response(Object object) {
                    try {
                        if (null != waitingDialog) {
                            if (waitingDialog.isShowing()) {
                                waitingDialog.dismiss();
                            }
                        }


                        final StudyTimeBeanNew bean = new Gson().fromJson(object.toString(), StudyTimeBeanNew.class);

                        if ("1".equals(bean.getResult())) {
                            final int time = Integer.parseInt(bean.getTotalTime());
                            int signStudyTime = 3 * 60;

                            if (time < signStudyTime) {
                                toast(String.format(Locale.CHINA, "打卡失败，当前已学习%d秒，\n满%d分钟可打卡", time, signStudyTime / 60));
                            } else {
                                //打卡
                                Intent intent = new Intent(getActivity(), SignActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            toast(loadFiledHint);
                        }
                    } catch (Exception e) {
                        Log.e("异常", e.toString());
                        e.printStackTrace();
                        toast(loadFiledHint + "！！");
                    }
                }
            });


//                    signInDialog = new SignInDialog(context);
//                    signInDialog.show();
        } else {
//            startActivityForResult(new Intent(context, LoginActivity.class), 101);
            clickLogin();
        }

    }

    private String floatToString(double fNumber) {


        DecimalFormat myformat = new java.text.DecimalFormat("0.00");

        String str = myformat.format(fNumber);
        return str;
    }


    /**
     * 美剧-下载开通会员
     *
     * @param event
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMovieGoVipCenterEvent event) {

        if (AccountManager.getInstance().checkUserLogin()) {
            startActivity(new Intent(context, VipCenterActivity.class));
        } else {
//            CustomDialog.showLoginDialog(context, false, new IOperationFinish() {
//                @Override
//                public void finish() {
//                    startActivity(new Intent(context, LoginActivity.class));
//                }
//            });
            clickLogin();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(UserNameChangeEvent event) {
        new UserInfoOp().updataByName(AccountManager.getInstance().getUserId(), event.newName);
        userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
        personalName.setText(userInfo.getUsername());

        AccountManager.getInstance().getUserInfo().setUsername(event.newName);
        isChange = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(UserPhotoChangeEvent event) {
        isChange = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(LoginEvent event) {
        userInfo = AccountManager.getInstance().getUserInfo();
        if (userInfo == null) {
            Log.e(TAG, "LoginEvent userInfo is null?");
        } else {
            updatePersonalInfoView();
            operAdapter.setNewTextList();
            changeUIResumeByPara();
        }
        getPersonalInfo();
    }

    void clickLogin() {
        CommonProgressDialog.dismissProgressDialog();
        MobSDK.submitPolicyGrantResult(true);
        FlyVerify.submitPolicyGrantResult(true);
        boolean isVerifySupport = FlyVerify.isVerifySupport();
        Log.e(TAG, "FlyVerify.isVerifySupport()  " + isVerifySupport);
        if (isVerifySupport) {
            verify();
        } else {
            if (context != null) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        }
    }

    /**
     * 免密登录
     */
    private void verify() {

        CommonProgressDialog.showProgressDialog(context);


        UiSettings.Builder builder = new UiSettings.Builder();
        builder.setSwitchAccText("其他方式登录");
        builder.setLoginBtnTextSize(14);
        builder.setImmersiveTheme(true);
        builder.setImmersiveStatusTextColorBlack(true);
        builder.setCusAgreementNameId1("《用户协议》");
        String userUrl = Constant.PROTOCOL_URL_IYUBA + Constant.APP_Name;
        builder.setCusAgreementUrl1(userUrl);
        builder.setCusAgreementNameId2("《隐私协议》");
        String priUrl = Constant.PROTOCOL_VIVO_PRIVACY + Constant.APP_Name;
        builder.setCusAgreementUrl2(priUrl);
        FlyVerify.setUiSettings(builder.build());

        FlyVerify.verify(new VerifyCallback() {
            @Override
            public void onComplete(VerifyResult verifyResult) {

                CommonProgressDialog.dismissProgressDialog();
                // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
                Log.e(TAG, "onComplete called");
                tokenToPhone(verifyResult);
            }

            @Override
            public void onFailure(VerifyException e) {

                CommonProgressDialog.dismissProgressDialog();
                Log.e(TAG, "onFailure called");
                startActivity(new Intent(context, LoginActivity.class));
                if (Constant.devMode) {
                    showExceptionMsg(e);
                }
            }

            @Override
            public void onOtherLogin() {
                CommonProgressDialog.dismissProgressDialog();
                // 用户点击“其他登录方式”，处理自己的逻辑
                Log.e(TAG, "onOtherLogin called");
                startActivity(new Intent(context, LoginActivity.class));
            }

            @Override
            public void onUserCanceled() {
                CommonProgressDialog.dismissProgressDialog();
                FlyVerify.finishOAuthPage();
                // 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
                Log.e(TAG, "onUserCanceled called");
            }

        });
    }

    private void tokenToPhone(VerifyResult data) {
        if (data != null) {
            Log.e(TAG, "data.getOperator()  " + data.getOperator());
            Log.e(TAG, "data.getOpToken()  " + data.getOpToken());
            Log.e(TAG, "data.getToken()  " + data.getToken());
            CommonProgressDialog.showProgressDialog(context);
//            mMainPresenter.registerToken(data.getToken(), data.getOpToken(), data.getOperator());
            Map<String, String> map = new HashMap<>();
            map.put("protocol", "10010");
            try {
                map.put("token", URLEncoder.encode(data.getToken(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                map.put("token", data.getToken());
            }
            map.put("opToken", data.getOpToken());
            map.put("operator", data.getOperator());
            map.put("appId", Constant.APP_ID + "");
            if (context.getPackageName().equals("com.iyuba.music")) {
                map.put("appkey", Constant.SMSAPPID);
            } else {
                map.put("appkey", Constant.SMSAPPID2);
            }
            IyumusicRetrofitNetwork.getApiComService().registerByMob(map).enqueue(new Callback<RegisterMobResponse>() {
                @Override
                public void onResponse(@NonNull Call<RegisterMobResponse> call, @NonNull Response<RegisterMobResponse> response) {
                    if ((response == null) || response.body() == null) {
                        Log.e(TAG, "registerByMob onResponse is null. ");
                        goResultActivity(null);
                        return;
                    }
                    RegisterMobResponse info = response.body();
                    Log.e(TAG, "registerByMob onResponse result " + info.isLogin);
                    if (1 == info.isLogin) {
                        goResultActivity(new LoginResult());
                        Toast.makeText(context, "您已经登录成功，可以进行学习了。", Toast.LENGTH_SHORT).show();
                        // already login, need update user info
                        if (info.userinfo != null) {
                            userInfo = info.userinfo;
                            new UserInfoOp().saveData(userInfo);
                            AccountManager.getInstance().setUserInfo(info.userinfo);
                            AccountManager.getInstance().setUserName(info.userinfo.getUsername());
                            AccountManager.getInstance().setUserPwd("");
                            AccountManager.getInstance().setUserId(info.userinfo.getUid());
                            AccountManager.getInstance().saveUserNameAndPwd();
                            AccountManager.getInstance().setLoginState();
                            AccountManager.getInstance().setUserInfo(userInfo);
                            AccountManager.getInstance().setUser();
                            getPersonalInfo();

                        } else {
                            Log.e(TAG, "registerByMob onNext response.userinfo is null.");
                        }
                        AccountManager.getInstance().setLoginState(AccountManager.SIGN_IN);
                        operAdapter.setNewTextList();
                        updatePersonalInfoView();
                        changeUIResumeByPara();
                    } else {
                        if (info.res != null) {
                            // register by this phone
                            RegisterMobResponse.MobBean mobBean = info.res;
                            LoginResult loginResult = new LoginResult();
                            loginResult.setPhone(mobBean.phone);
                            goResultActivity(loginResult);
                        } else {
                            Log.e(TAG, "registerByMob onNext response.res is null.");
                            goResultActivity(null);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RegisterMobResponse> call, @NonNull Throwable t) {
                    if (t != null) {
                        Log.e(TAG, "registerByMob onFailure " + t.getMessage());
                    }
                    goResultActivity(null);
                }
            });
        } else {
            CommonProgressDialog.dismissProgressDialog();
            Log.e(TAG, "tokenToPhone data is null.");
            startActivity(new Intent(context, LoginActivity.class));
        }
    }

    public void showExceptionMsg(VerifyException e) {
        // 登录失败
        if (defaultUi == 1) {
            //失败之后不会自动关闭授权页面，需要手动关闭
            FlyVerify.finishOAuthPage();
        }
        CommonProgressDialog.dismissProgressDialog();
        Log.e(TAG, "verify failed", e);
        // 错误码
        int errCode = e.getCode();
        // 错误信息
        String errMsg = e.getMessage();
        // 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
        Throwable t = e.getCause();
        String errDetail = null;
        if (t != null) {
            errDetail = t.getMessage();
        }

        String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
        if (!TextUtils.isEmpty(errDetail)) {
            msg += "\n详细信息: " + errDetail;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void goResultActivity(LoginResult data) {
        if (data == null) {
            Log.e(TAG, "goResultActivity LoginResult is null. ");
            Toast.makeText(context, "请使用用户名密码登录。", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(context, LoginActivity.class));
        } else if (!TextUtils.isEmpty(data.getPhone())) {
            ConfigManager.getInstance().setAutoLogin(true);
            String randNum = "" + System.currentTimeMillis();
            String user = "iyuba" + randNum.substring(randNum.length() - 4) + data.getPhone().substring(data.getPhone().length() - 4);
            String pass = data.getPhone().substring(data.getPhone().length() - 6);
            Log.e(TAG, "goResultActivity.user  " + user);
            ConfigManager.getInstance().putString(ConfigManager.USER_NAME, user);
            ConfigManager.getInstance().putString(ConfigManager.USER_PASS, pass);
            Intent intent = new Intent(context, RegistSubmitActivity.class);
            intent.putExtra(RegistSubmitActivity.PhoneNum, data.getPhone());
            intent.putExtra(RegistSubmitActivity.UserName, user);
            intent.putExtra(RegistSubmitActivity.PassWord, pass);
            intent.putExtra(RegistSubmitActivity.RegisterMob, 1);
            startActivity(intent);
        } else {
            ConfigManager.getInstance().setAutoLogin(true);
            Log.e(TAG, "goResultActivity LoginResult is ok. ");
        }
        FlyVerify.finishOAuthPage();
        CommonProgressDialog.dismissProgressDialog();
    }

}

