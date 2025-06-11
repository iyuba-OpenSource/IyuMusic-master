package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.me.ImproveUserActivity;
import com.iyuba.music.event.LoginEvent;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.request.account.RegistByPhoneRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by carl shen on 2021/3/18
 * New English News, new study experience.
 */
public class RegistSubmitActivity extends BaseInputActivity {
	public static String PhoneNum = "phoneNumb";
	public static String UserName = "UserName";
	public static String PassWord = "PassWord";
	public static String RegisterMob = "RegisterMob";
	private int registerMob;
	private TextView registerTip;
	private EditText userNameEditText, passWordEditText;
	private Button submitButton;
	private String phonenumb, userName, passWord;
	private IyubaDialog waittingDialog;
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
		setContentView(R.layout.regist_layout_phone);
		context = this;
		initWidget();
		setListener();
		changeUIByPara();
	}

	@Override
	protected void initWidget() {
		super.initWidget();
		userNameEditText = (EditText) findViewById(R.id.regist_phone_username);
		passWordEditText = (EditText) findViewById(R.id.regist_phone_paswd);
		submitButton = (Button) findViewById(R.id.regist_phone_submit);
		userNameEditText.setOnEditorActionListener(editor);
		passWordEditText.setOnEditorActionListener(editor);
		waittingDialog = WaitingDialog.create(context, context.getString(R.string.regist_on_way));
	}

	@Override
	protected void setListener() {
		super.setListener();
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (verification()) {// 验证通过
					// 开始注册
					handler.sendEmptyMessage(0);// 在handler中注册
				}
			}
		});
	}
	@Override
	protected void changeUIByPara() {
		super.changeUIByPara();
		title.setText(R.string.login_oper);
		phonenumb = getIntent().getExtras().getString(PhoneNum);
		userName = getIntent().getExtras().getString(UserName);
		passWord = getIntent().getExtras().getString(PassWord);
		registerMob = getIntent().getExtras().getInt(RegisterMob, 0);
		if (registerMob > 0) {
			userNameEditText.setText(userName);
			passWordEditText.setText(passWord);
			passWordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			registerTip = findViewById(R.id.register_tip);
			registerTip.setText("这是默认的用户名，密码是手机号后6位，您可以修改。");
		}
	}

	/**
	 * 验证
	 */
	public boolean verification() {
		userName = userNameEditText.getText().toString();
		passWord = passWordEditText.getText().toString();
		if (!checkUserId(userName)) {
			userNameEditText.setError(context.getString(R.string.regist_check_username_1));
			return false;
		}
		if (!checkUserName(userName)) {
			userNameEditText.setError(context.getString(R.string.regist_check_username_2));
			return false;
		}
		if (!checkUserPwd(passWord)) {
			passWordEditText.setError(context.getString(R.string.regist_check_userpwd_1));
			return false;
		}
		return true;
	}

	/**
	 * 匹配用户名1
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkUserId(String userId) {
		if (userId.length() < 3 || userId.length() > 20)
			return false;
		return true;
	}

	/**
	 * 匹配用户名2 验证非手机号 邮箱号
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkUserName(String userId) {
		if (userId
				.matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
			return false;
		}
		if (userId.matches("^(1)\\d{10}$")) {
			return false;
		}

		return true;
	}

	/**
	 * 匹配密码
	 * 
	 * @param userPwd
	 * @return
	 */
	public boolean checkUserPwd(String userPwd) {
		if (userPwd.length() < 6 || userPwd.length() > 20)
			return false;
		return true;
	}

	private void regist() {
		RegistByPhoneRequest.exeRequest(RegistByPhoneRequest.generateUrl(new String[]{userName, passWord, phonenumb}), new IProtocolResponse() {
			@Override
			public void onNetError(String msg) {
				Log.e(RegisterMob, "regist onNetError = " + msg);
			}

			@Override
			public void onServerError(String msg) {
				Log.e(RegisterMob, "regist onServerError = " + msg);
			}

			@Override
			public void response(Object object) {
				int result = (int) object;
				Log.e(RegisterMob, "regist response result = " + result);
				if (result == 111) {
					CustomToast.getInstance().showToast(R.string.regist_success);
					handler.sendEmptyMessage(4);
					handler.sendEmptyMessage(6);
					ConfigManager.getInstance().putString(ConfigManager.USER_NAME, userName);
					ConfigManager.getInstance().putString(ConfigManager.USER_PASS, passWord);
				} else if (result == 112) {
//					CustomToast.getInstance().showToast(R.string.regist_userid_same);
					handler.sendEmptyMessage(3);
				} else {
//					CustomToast.getInstance().showToast(R.string.regist_fail);
					handler.sendEmptyMessage(1);// 弹出错误提示
				}
			}
		});
	}

	private void gotoLogin() {
		AccountManager.getInstance().login(userName, passWord, new IOperationResult() {
					@Override
					public void success(Object object) {
						// just set login state, need login to get detail info
						AccountManager.getInstance().setLoginState(AccountManager.SIGN_IN);
						EventBus.getDefault().post(new LoginEvent(userName, passWord));
						Log.e(RegisterMob, "gotoLogin success + " + object);
						Intent intent = new Intent(context, ImproveUserActivity.class);
						intent.putExtra("regist", true);
						startActivity(intent);
						finish();
					}

					@Override
					public void fail(Object object) {
						Log.e(RegisterMob, "gotoLogin fail + " + object);
						finish();
					}
				});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				handler.sendEmptyMessage(5);
				regist();
				break;
			case 1:
				handler.sendEmptyMessage(4);
				CustomToast.getInstance().showToast(R.string.regist_fail);
				break;
			case 2:
				CustomToast.getInstance().showToast(R.string.regist_success);
				break;
			case 3:
				handler.sendEmptyMessage(4);
				CustomToast.getInstance().showToast(R.string.regist_userid_exist);
				break;
			case 4:
				waittingDialog.dismiss();
				break;
			case 5:
				waittingDialog.show();
				break;
			case 6:
				gotoLogin();
				break;
			default:
				break;
			}
		}
	};
}
