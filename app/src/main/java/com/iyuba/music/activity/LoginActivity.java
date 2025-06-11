package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.imooclib.ui.web.Web;
import com.iyuba.music.R;
import com.iyuba.music.activity.me.ImproveUserActivity;
import com.iyuba.music.adapter.me.AutoCompleteAdapter;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.user.HistoryLogin;
import com.iyuba.music.entity.user.HistoryLoginOp;
import com.iyuba.music.event.LoginEvent;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/19.
 */
public class LoginActivity extends BaseInputActivity {
    private static IOperationResult result;
    private MaterialAutoCompleteTextView username, userpwd;
    private TextView forgetPwd;
    private CheckBox autoLogin;
    private RoundTextView login;
    private IyubaDialog waitingDialog;


    private CheckBox login_rb_pri;

    TextView.OnEditorActionListener editor = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                login();
                return true;
            }
            return false;
        }
    };
    private View photo, loginMsg;

    public static void launch(Context context, IOperationResult result) {
        LoginActivity.result = result;
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        photo = findViewById(R.id.login_photo);
        loginMsg = findViewById(R.id.login_message);
        username = (MaterialAutoCompleteTextView) findViewById(R.id.username);
        userpwd = (MaterialAutoCompleteTextView) findViewById(R.id.userpwd);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        autoLogin = (CheckBox) findViewById(R.id.auto_login);
        autoLogin.setChecked(ConfigManager.getInstance().isAutoLogin());
        forgetPwd = (TextView) findViewById(R.id.forget_pwd);
        login = (RoundTextView) findViewById(R.id.login);
        AddRippleEffect.addRippleEffect(login);
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.login_on_way));


        login_rb_pri = findViewById(R.id.login_rb_pri);
        login_rb_pri.setMovementMethod(LinkMovementMethod.getInstance());

        String priStr = "登录即代表同意爱语吧的服务协议和隐私政策";
        SpannableString spannableString = new SpannableString(priStr);

        String url_Agreement = Constant.PROTOCOL_URL_IYUBA + Constant.APP_Name;
        ClickableSpan spanAgreement = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimary));//设置超链接的颜色
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                // 单击事件处理
                startActivity(Web.buildIntent(LoginActivity.this, url_Agreement, "用户协议"));
            }
        };

        String url_PrivacyPolicy = Constant.PROTOCOL_VIVO_PRIVACY + Constant.APP_Name;
        ClickableSpan spanPrivacyPolicy = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimary));//设置超链接的颜色
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                // 单击事件处理
                startActivity(Web.buildIntent(LoginActivity.this, url_PrivacyPolicy, "隐私政策"));
            }
        };
        spannableString.setSpan(spanAgreement, priStr.indexOf("服务协议"),
                priStr.indexOf("服务协议") + "服务协议".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(spanPrivacyPolicy, priStr.indexOf("隐私政策"),
                priStr.indexOf("隐私政策") + "隐私政策".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        login_rb_pri.setText(spannableString);
    }

    @Override
    protected void setListener() {
        super.setListener();
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login();
                    }
                }
        );
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userpwd.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", "http://m." + ConstantManager.IYUBA_CN + "m_login/inputPhonefp.jsp");
                intent.putExtra("title", forgetPwd.getText());
                startActivity(intent);
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, RegistActivity.class), 101);
            }
        });
        userpwd.setOnEditorActionListener(editor);
        setUserNameAutoLogin();
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.regist_oper);
        title.setText(R.string.login_title);
        photo.setAlpha(0);
        loginMsg.setAlpha(0);
        title.postDelayed(new Runnable() {
            public void run() {
                YoYo.with(Techniques.FadeInLeft).duration(1000).playOn(photo);
                YoYo.with(Techniques.FadeInRight).duration(1000).playOn(loginMsg);
            }
        }, 200);
    }

    private void setUserNameAutoLogin() {
        final HistoryLoginOp historyLoginOp = new HistoryLoginOp();
        final ArrayList<HistoryLogin> historyLogins = historyLoginOp.selectData();
        ArrayList<String> historyLoginNames = new ArrayList<>();
        for (HistoryLogin historyLogin : historyLogins) {
            historyLoginNames.add(historyLogin.getUserName());
        }
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(context, historyLoginNames);
        adapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String name = adapter.getItem(position).toString();
                username.setText(name);
                for (HistoryLogin historyLogin : historyLogins) {
                    if (historyLogin.getUserName().equals(name)) {
                        userpwd.setText(historyLogin.getUserPwd());
                    }
                }
                username.dismissDropDown();
            }

            @Override
            public void onItemLongClick(View view, int position) {//点击删除按钮
                historyLoginOp.deleteData(adapter.getItem(position).toString());
            }
        });
        username.setAdapter(adapter);
        username.setThreshold(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 1) {// 注册的返回结果
            String usernameString = data.getStringExtra("username");
            String userpwdString = data.getStringExtra("userpwd");
            username.setText(usernameString);
            userpwd.setText(userpwdString);
            waitingDialog.show();
            AccountManager.getInstance().login(username.getText().toString(), userpwd.getText().toString(), new IOperationResult() {
                @Override
                public void success(Object object) {
                    waitingDialog.dismiss();
                    ConfigManager.getInstance().setAutoLogin(autoLogin.isChecked());
                    Intent intent = new Intent();
                    setResult(2, intent);
                    if (result != null) {
                        result.success(null);
                    }
                    LoginActivity.this.finish();
                }

                @Override
                public void fail(Object object) {
                    CustomToast.getInstance().showToast(object.toString());
                    if (result != null) {
                        result.fail(null);
                    }
                    waitingDialog.dismiss();
                }
            });
        }
    }

    private void login() {
        if (username.isCharactersCountValid() && userpwd.isCharactersCountValid()) {

            if (!login_rb_pri.isChecked()) {

                Toast.makeText(LoginActivity.this, "请勾选服务协议和隐私政策", Toast.LENGTH_SHORT).show();
                return;
            }

            waitingDialog.show();
            AccountManager.getInstance().login(username.getText().toString(), userpwd.getText().toString(), new IOperationResult() {
                @Override
                public void success(Object object) {
                    waitingDialog.dismiss();
                    ConfigManager.getInstance().setAutoLogin(autoLogin.isChecked());
                    Intent intent = new Intent();
                    setResult(1, intent);
                    if (result != null) {
                        result.success(null);

                    }
                    AccountManager.getInstance().setLoginState(AccountManager.SIGN_IN);
                    EventBus.getDefault().post(new LoginEvent(username.getText().toString(), userpwd.getText().toString()));
                    String userId = ConfigManager.getInstance().loadString(ConfigManager.USER_ID);
                    Log.e("LoginActivity", "userId== " + userId);
                    if (!TextUtils.isEmpty(userId) && ConfigManager.getInstance().loadBoolean("userId_" + userId, true)) {
                        Intent userInfo = new Intent(context, ImproveUserActivity.class);
                        userInfo.putExtra("regist", false);
                        startActivity(userInfo);
                    }
                    LoginActivity.this.finish();
                }

                @Override
                public void fail(Object object) {
                    waitingDialog.dismiss();
                    if (result != null) {
                        result.fail(null);
                    }
                    if (object == null) {
                        CustomToast.getInstance().showToast(R.string.please_check_network);
                        return;
                    }
                    switch ((String) object) {
                        case "102":
                            CustomToast.getInstance().showToast(R.string.login_fail_name);
                            break;
                        case "103":
                            CustomToast.getInstance().showToast(R.string.login_fail_pass);
                            break;
                        case "0":
                            CustomToast.getInstance().showToast(R.string.login_fail_server);
                            break;
                        default:
                            CustomToast.getInstance().showToast(R.string.please_check_network);
                            break;
                    }
                }
            });
        } else if (!username.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(username);
        } else if (!userpwd.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userpwd);
        }
    }


}
