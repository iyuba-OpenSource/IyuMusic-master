package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.model.ChangeNameResponse;
import com.iyuba.music.data.model.LoginResponse;
import com.iyuba.music.event.LoginEvent;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.retrofit.IyumusicRetrofitNetwork;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.TextAttr;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by carl shen on 2021/3/18
 * English Music, new study experience.
 */
public class ChangeNameActivity extends BaseInputActivity {
    public static String RegisterMob = "RegisterMob";
    private String userName;
    private String passWord;
    private int changeFlag = 0;
    TextView changeName;
    LinearLayout passwordConfirm;
    LinearLayout nameChange;
    EditText passwordInput;
    EditText nameInput;
    Button passwordSubmit;
    Button nameSubmit;
    TextView passwordForget;
    TextView.OnEditorActionListener editor = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        changeName = findViewById(R.id.change_name);
        passwordConfirm = findViewById(R.id.password_confirm);
        nameChange = findViewById(R.id.name_change);
        passwordInput = findViewById(R.id.password_input);
        nameInput = findViewById(R.id.name_input);
        passwordSubmit = findViewById(R.id.password_submit);
        nameSubmit = findViewById(R.id.name_submit);
        passwordForget = findViewById(R.id.password_forget);

        changeFlag = getIntent().getExtras().getInt(RegisterMob, 0);
        if (changeFlag > 0) {
            passwordConfirm.setVisibility(View.GONE);
            nameChange.setVisibility(View.VISIBLE);
        } else {
            passwordConfirm.setVisibility(View.VISIBLE);
            nameChange.setVisibility(View.GONE);
        }
        passwordInput.setOnEditorActionListener(editor);
    }

    @Override
    protected void setListener() {
        super.setListener();
        back.setOnClickListener(v -> finishChangeActivity());
        passwordSubmit.setOnClickListener(v -> clickSubmitPassword());
        nameSubmit.setOnClickListener(v -> clickSubmit());
        passwordForget.setOnClickListener(v -> startWeb());
    }
    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.user_change);
        userName = ConfigManager.getInstance().loadString(ConfigManager.USER_NAME);
        if (!TextUtils.isEmpty(userName)) {
            changeName.setText(getText(R.string.user_name) + "  " + userName);
        }
    }

    void clickSubmitPassword() {
        passWord = passwordInput.getText().toString().trim();
        Log.e("ChangeNameActivity", "clickSubmitPassword passWord " + passWord);
        if (!checkPasswordLength(passWord)) {
            showToast(R.string.register_check_pwd_1);
            return;
        }
        if (passWord.equals(ConfigManager.getInstance().loadString(ConfigManager.USER_PASS))) {
            showToast("密码验证成功，请输入新用户名。");
            passwordConfirm.setVisibility(View.GONE);
            nameChange.setVisibility(View.VISIBLE);
            changeFlag = 1;
        } else {
            Map<String, String> para = new ArrayMap<>();
            para.put("protocol", "11001");
            para.put("platform", "android");
            para.put("username", TextAttr.encode(TextAttr.encode(userName)));
            para.put("password", MD5.getMD5ofStr(passWord));
            para.put("x", "");
            para.put("y", "");
            para.put("appid", Constant.APPID);
            para.put("format", "json");
            para.put("sign", MD5.getMD5ofStr("11001" + userName + MD5.getMD5ofStr(passWord) + "iyubaV2"));
            IyumusicRetrofitNetwork.getApiComService().LoginUser(para).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if ((response == null) || response.body() == null) {
                        Log.e("ChangeNameActivity", "clickSubmitPassword onResponse is null. ");
                        showToast("密码验证失败！请重新输入密码或者稍后再试。");
                        return;
                    }
                    LoginResponse info = response.body();
                    Log.e("ChangeNameActivity", "clickSubmitPassword onResponse result " + info.result);
                    if (101 == info.result) {
                        showToast("密码验证成功，请输入新用户名。");
                        passwordConfirm.setVisibility(View.GONE);
                        nameChange.setVisibility(View.VISIBLE);
                        changeFlag = 1;
                    } else if (103 == info.result) {
                        showToast("密码验证失败，请重新输入密码，或者点击忘记密码找回密码。");
                    } else {
                        showToast("密码验证失败！请重新输入密码或者稍后再试。");
                    }
                }
                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    if (t != null) {
                        Log.e("ChangeNameActivity", "clickSubmitPassword fail = " + t.getMessage());
                    }
                    showToast("密码验证失败！请检查网络或者重新输入。");
                }
            });
        }
    }
    void clickSubmit() {
        String userNewName = nameInput.getText().toString();
        if (!checkUsernameLength(userNewName)) {
            showToast(R.string.register_check_username_1);
            return;
        }
        if (userNewName.equals(userName)) {
            showToast("新用户名和旧用户名相同。");
            return;
        }
        changeFlag = 2;
        Map<String, String> map = new HashMap<>();
        map.put("protocol", "10012");
        map.put("uid", AccountManager.getInstance().getUserId());
        map.put("username", userNewName);
        map.put("oldUsername", userName);
        map.put("appId", Constant.APP_ID + "");
        map.put("sign", MD5.getMD5ofStr("10012" + AccountManager.getInstance().getUserId() + "iyubaV2"));
        IyumusicRetrofitNetwork.getApiComService().ChangeUserName(map).enqueue(new Callback<ChangeNameResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChangeNameResponse> call, @NonNull Response<ChangeNameResponse> response) {
                if ((response == null) || response.body() == null) {
                    showToast(R.string.edit_failure);
                    Log.e("ChangeNameActivity", "ChangeUserName onResponse is null. ");
                    return;
                }
                if (response.isSuccessful()) {
                    ChangeNameResponse info = response.body();
                    Log.e("ChangeNameActivity", "ChangeUserName onResponse result " + info.result);
                    switch (info.result) {
                        case "121":
                            showToast(R.string.edit_success);
                            AccountManager.getInstance().setUserName(userNewName);
                            ConfigManager.getInstance().putString(ConfigManager.USER_NAME, userNewName);
                            EventBus.getDefault().post(new LoginEvent(userNewName, passWord));
                            finish();
                            break;
                        case "0":
                        case "000":
                            showToast("用户名已存在,请重新填写!");
                            break;
                        default:
                            showToast(R.string.net_no_net);
                            break;
                    }
                } else {
                    showToast(R.string.edit_failure);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChangeNameResponse> call, @NonNull Throwable t) {
                if(t != null) {
                    Log.e("ChangeNameActivity", "ChangeUserName onFailure " + t.getMessage());
                }
                showToast("请求失败，请检查网络或者再次尝试。");
            }
        });
    }

    private boolean checkUsernameLength(String username) {
        return ((username != null) && username.length() >= 3 && username.length() <= 15);
    }

    private boolean checkPasswordLength(String userPwd) {
        return ((userPwd != null) && userPwd.length() >= 6 && userPwd.length() <= 20);
    }

    public void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public void startWeb() {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", "http://m." + ConstantManager.IYUBA_CN + "m_login/inputPhonefp.jsp");
        intent.putExtra("title", getResources().getString(R.string.login_find_password));
        startActivity(intent);
    }

    public void finishChangeActivity() {
        if (changeFlag > 0) {
            changeFlag = 0;
            passwordConfirm.setVisibility(View.VISIBLE);
            nameChange.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

}
