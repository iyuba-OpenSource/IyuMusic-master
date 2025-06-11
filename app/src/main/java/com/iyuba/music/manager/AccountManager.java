package com.iyuba.music.manager;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.HistoryLogin;
import com.iyuba.music.entity.user.HistoryLoginOp;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.request.account.LoginRequest;
import com.iyuba.music.request.merequest.PersonalInfoRequest;
import com.iyuba.music.util.AdTimeCheck;
import com.iyuba.music.widget.CustomToast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import personal.iyuba.personalhomelibrary.PersonalHome;


/**
 * Created by 10202 on 2015/11/18.
 */
public class AccountManager {
    private static AccountManager instance;

    public static final int SIGN_OUT = 0x01;
    public static final int SIGN_IN = 0x02;
    private static Context mContext;

    private String money; //钱包==全局
    @LoginState
    private int loginState;
    private UserInfo userInfo;
    private String userId; // 用户ID
    private String visitorId;
    private String userName; // 用户姓名
    private String userPwd; // 用户密码

    private boolean isGetPosition = false;
    private double latitude = 39.9;
    private double longitude = 116.3;


    private AccountManager() {
        loginState = SIGN_OUT;
        visitorId = ConfigManager.getInstance().loadString("visitorId");
    }

    public static AccountManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public static synchronized AccountManager Instace(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    public void setLoginState() {
        loginState = SIGN_IN;
    }

    public boolean checkUserLogin() {
        if (!TextUtils.isEmpty(ConfigManager.getInstance().loadString(ConfigManager.USER_ID))) {
            loginState = SIGN_IN;
        }
        return loginState == SIGN_IN;
    }

    public void loginOut() {
        new UserInfoOp().delete(userId);
        loginState = SIGN_OUT;
        userId = ""; // 用户ID
        visitorId = "";
        userName = ""; // 用户姓名
        userPwd = ""; // 用户密码
        userInfo = new UserInfo();
        IyuUserManager.getInstance().logout();
        saveUserNameAndPwd();
        ConfigManager.getInstance().putString("visitorId", "");
        ConfigManager.getInstance().setAutoLogin(false);
    }

    public void saveUserNameAndPwd() {
        ConfigManager.getInstance().putString(ConfigManager.USER_NAME, userName);
        ConfigManager.getInstance().putString(ConfigManager.USER_PASS, userPwd);
        ConfigManager.getInstance().putString(ConfigManager.USER_ID, userId);
        if (!TextUtils.isEmpty(userId)) {
            HistoryLogin login = new HistoryLogin();
            login.setUserid(Integer.parseInt(userId));
            login.setUserName(userName);
            login.setUserPwd(userPwd);
            login.setLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));
            new HistoryLoginOp().saveData(login);
        }
    }

    public String[] getNameAndPwdFromSp() {
        return new String[]{
                ConfigManager.getInstance().loadString(ConfigManager.USER_NAME),
                ConfigManager.getInstance().loadString(ConfigManager.USER_PASS)};
    }

    public void login(String userName, String userPwd, final IOperationResult rc) {
        this.userName = userName;
        this.userPwd = userPwd;
        String[] paras = new String[]{userName, userPwd, String.valueOf(getLongitude())
                , String.valueOf(getLatitude())};
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            LoginRequest.exeRequest(LoginRequest.generateUrl(paras), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    if (rc != null) {
                        rc.fail(msg);
                    }
                }

                @Override
                public void onServerError(String msg) {
                    if (rc != null) {
                        rc.fail(msg);
                    }
                }

                @Override
                public void response(Object object) {
                    BaseApiEntity apiEntity = (BaseApiEntity) object;
                    if (BaseApiEntity.isSuccess(apiEntity)) {
                        loginState = SIGN_IN;

                        UserInfoOp userInfoOp = new UserInfoOp();
                        UserInfo tempResult = (UserInfo) apiEntity.getData();
                        userId = tempResult.getUid();

                        if (userInfoOp.selectDataByName(getInstance().userName) == null) {
                            saveUserNameAndPwd();
                        }
                        if (userInfo == null) {
                            userInfo = tempResult;
                        } else {
                            userInfo.update(tempResult);
                        }
                        Log.e("userinfo==", apiEntity.toString());
                        userInfoOp.saveData(userInfo);
                        setMoney(tempResult.getMoney()); //储存钱包
                        if (RuntimeManager.getInstance().isShowSignInToast()) {
                            RuntimeManager.getInstance().setShowSignInToast(false);
                            CustomToast.getInstance().showToast(RuntimeManager.getContext().getString(
                                    R.string.login_success, userInfo.getUsername()));
                        }
                        if (rc != null) {
//                            InitPush.getInstance().registerToken(RuntimeManager.getContext(), Integer.parseInt(userInfo.getUid()));
                            rc.success(apiEntity.getMessage());
                        }

                        setUser();
                    } else if (BaseApiEntity.isFail(apiEntity)) {
                        loginState = SIGN_OUT;
                        ConfigManager.getInstance().setAutoLogin(false);
                        if (rc != null) {
                            rc.fail(apiEntity.getMessage());
                        }
                    } else {
                        loginState = SIGN_OUT;
                        if (rc != null) {
                            rc.fail(RuntimeManager.getString(R.string.login_error));
                        }
                    }
                }
            });
        } else if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            rc.fail(RuntimeManager.getString(R.string.net_speed_slow));
        } else {
            rc.fail(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public void silentLogin(String userName, String userPwd, final IOperationResult rc) {
        this.userName = userName;
        this.userPwd = userPwd;
        String[] paras = new String[]{userName, userPwd, "", ""};
        LoginRequest.exeRequest(LoginRequest.generateUrl(paras), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                if (rc != null) {
                    rc.fail(msg);
                }
            }

            @Override
            public void onServerError(String msg) {
                if (rc != null) {
                    rc.fail(msg);
                }
            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                if (BaseApiEntity.isSuccess(apiEntity)) {
                    loginState = SIGN_IN;
                    if (rc != null) {
                        rc.success(apiEntity.getMessage());
                    }
                }
            }
        });
    }

    public void getPersonalInfo(final IOperationResult result) {
        PersonalInfoRequest.exeRequest(PersonalInfoRequest.generateUrl(getUserId(), getUserId()), userInfo, new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                if (result != null) {
                    result.fail(null);
                }
            }

            @Override
            public void onServerError(String msg) {
                if (result != null) {
                    result.fail(null);
                }
            }

            @Override
            public void response(Object object) {

                BaseApiEntity baseApiEntity = (BaseApiEntity) object;


                if (((BaseApiEntity) object).getData() != null) {

                    userInfo.update((UserInfo) ((BaseApiEntity) object).getData());
                }
                if (BaseApiEntity.isSuccess(baseApiEntity)) {
                    if (result != null) {
                        result.success(null);
                    }
                    new UserInfoOp().saveData(userInfo);
                } else {
                    if (result != null) {
                        result.fail(null);
                    }
                }
            }
        });
    }

    public void refreshVipStatus() {
        String[] paras = new String[]{userName, userPwd, String.valueOf(getLongitude())
                , String.valueOf(getLatitude())};
        LoginRequest.exeRequest(LoginRequest.generateUrl(paras), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                Log.e("VIP信息", object.toString());
                if (BaseApiEntity.isSuccess(apiEntity)) {
                    UserInfo temp = (UserInfo) apiEntity.getData();
                    userInfo.setIyubi(temp.getIyubi());
                    userInfo.setVipStatus(temp.getVipStatus());
                    userInfo.setDeadline(temp.getDeadline());
                    new UserInfoOp().saveData(userInfo);
                }
            }
        });
    }

    public String getUserId() {
        if (loginState == SIGN_IN) {
            return userId;
//        } else if (!TextUtils.isEmpty(visitorId)) {
//            return visitorId;
        } else {
            return "0";
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        userName = name;
    }

    public void setUserPwd(String pwd) {
        userPwd = pwd;
    }

    public void setUserId(String id) {
        userId = id;
    }

    public boolean needGetVisitorID() {
        return TextUtils.isEmpty(visitorId) && ConfigManager.getInstance().loadBoolean("gotVisitor", true);
    }

    public void setLoginState(@LoginState int state) {
        loginState = state;
        if (loginState == SIGN_IN) {
            userId = ConfigManager.getInstance().loadString(ConfigManager.USER_ID);
        }
    }

    public void getGPS() {
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) RuntimeManager.getContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setLocation(location);
        }
    }

    private void setLocation(@Nullable Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            isGetPosition = true;
        } else {
            latitude = 39.9;
            longitude = 116.3;
            isGetPosition = false;
        }
    }

    public double getLatitude() {
        if (!isGetPosition) {
            getGPS();
        }
        return latitude;
    }

    public double getLongitude() {
        if (!isGetPosition) {
            getGPS();
        }
        return longitude;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @IntDef({SIGN_OUT, SIGN_IN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginState {
    }

    private static class SingleInstanceHelper {
        private static AccountManager instance = new AccountManager();
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setUser() {

        String userid = ConfigManager.getInstance().loadString(ConfigManager.USER_ID);

        if (userid == null || userid.equals("") || userid.equals("0")) {

            return;
        }

        User user = new User();
        user.vipStatus = userInfo.getVipStatus();
        user.name = userInfo.getUsername();
        user.uid = Integer.parseInt(userInfo.getUid());
        if (!AdTimeCheck.setAd()) {
            user.vipStatus = "1";
        } else {
            user.vipStatus = userInfo.getVipStatus();
        }
        IyuUserManager.getInstance().setCurrentUser(user);
        setPersonalInfo();
    }

    public void setPersonalInfo() {
        //个人中心
//        用户登录时配置！！！
//        用户登录时配置！！！
        PersonalHome.setSaveUserinfo((AccountManager.Instace(mContext).userId == null) ? 0 : Integer.parseInt(AccountManager.Instace(mContext).userId), AccountManager.Instace(mContext).userName,
                (AccountManager.Instace(mContext).userInfo != null) ? AccountManager.Instace(mContext).userInfo.getVipStatus() : "0");


    }
}
