package com.iyuba.music.activity.me;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.music.R;
import com.iyuba.music.entity.user.MostDetailInfo;
import com.iyuba.music.entity.user.UserIpAddress;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.request.merequest.GetIpAddressRequest;
import com.iyuba.music.request.merequest.ImproveUserRequest;
import com.iyuba.music.request.merequest.UserInfoDetailRequest;
import com.iyuba.music.widget.CustomToast;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by carl shen on 2020/10/29.
 */
public class ImproveUserActivity extends AppCompatActivity {
    protected AppCompatActivity context;
    private TextView tvGender, tvLocation, tvAge, tvTitle;
    private RelativeLayout rtCardSex, rtCardAge, rtCardArea, rtCardTitle;
    private TextView tvCommit;
    private ImageView imgClose;
    private String locProvince, locCity;
    private MostDetailInfo userDetailInfo;
    private String[] arrGender = new String[] {"男生", "女生"};
    private String[] arrAge = new String[] {"70s","80s", "90s", "95s","00s", "05s", "10s"};
    private String[] arrTitle = new String[] {"小学生", "初中生", "高中生","大学生", "研究生", "职员","其他"};
    boolean register = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_improve);
        context = this;
        initWidget();
        setListener();
        register = getIntent().getBooleanExtra("regist", false);
        if (register) {
            checkIPAddress();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!register) {
            UpdateUIByPara();
        }
    }

    protected void initWidget() {
        imgClose = findViewById(R.id.user_close);
        tvCommit = findViewById(R.id.tv_commit);
        rtCardSex = findViewById(R.id.card_sex);
        rtCardAge = findViewById(R.id.card_age);
        rtCardArea = findViewById(R.id.card_area);
        rtCardTitle = findViewById(R.id.card_title);
        tvGender = findViewById(R.id.tv_gender);
        tvLocation = findViewById(R.id.tv_location);
        tvAge = findViewById(R.id.tv_birthday);
        tvTitle = findViewById(R.id.tv_company);
    }

    protected void setListener() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserByPara();
            }
        });
        rtCardSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionPicker picker = new OptionPicker(context, arrGender);
                picker.setDividerRatio(WheelView.DividerConfig.FILL);
                picker.setShadowColor(Color.RED, 40);
                if (!TextUtils.isEmpty(tvGender.getText().toString().trim())) {
                    int index = 0;
                    for (int i = 0; i < arrGender.length; i++) {
                        if (tvGender.getText().toString().trim().equalsIgnoreCase(arrGender[i])) {
                            index = i;
                            break;
                        }
                    }
                    picker.setSelectedIndex(index);
                } else {
                    picker.setSelectedIndex(1);
                }
                picker.setCycleDisable(true);
                picker.setOffset(2);
                picker.setTextSize(16);
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
                        tvGender.setText(item);
                    }
                });
                picker.show();
            }
        });
        rtCardAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionPicker picker = new OptionPicker(context, arrAge);
                picker.setCanceledOnTouchOutside(false);
                picker.setDividerRatio(WheelView.DividerConfig.FILL);
                picker.setShadowColor(Color.RED, 40);
                if (!TextUtils.isEmpty(tvAge.getText().toString().trim())) {
                    int index = 0;
                    for (int i = 0; i < arrAge.length; i++) {
                        if (tvAge.getText().toString().trim().equalsIgnoreCase(arrAge[i])) {
                            index = i;
                            break;
                        }
                    }
                    picker.setSelectedIndex(index);
                } else {
                    picker.setSelectedIndex(3);
                }
                picker.setCycleDisable(true);
                picker.setOffset(3);
                picker.setTextSize(16);
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
                        tvAge.setText(item);
                    }
                });
                picker.show();
            }
        });
        rtCardArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressPickTask task = new AddressPickTask(context);
                task.setHideCounty(true);
                task.setCallback(new AddressPickTask.Callback() {
                    @Override
                    public void onAddressInitFailed() {
                        showToast("数据初始化失败");
                    }

                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        locProvince = province.getAreaName();
                        locCity = city.getAreaName();
                        if (county == null) {
                            showToast(province.getAreaName() + city.getAreaName());
                            tvLocation.setText(province.getAreaName() + city.getAreaName());
                        } else {
                            showToast(province.getAreaName() + city.getAreaName() + county.getAreaName());
                            tvLocation.setText(province.getAreaName() + city.getAreaName() + county.getAreaName());
                        }
                    }
                });
                if (TextUtils.isEmpty(locProvince) || TextUtils.isEmpty(locCity)) {
                    task.execute("北京市", "通州区");
                } else {
                    task.execute(locProvince, locCity);
                }
            }
        });
        rtCardTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionPicker picker = new OptionPicker(context, arrTitle);
                picker.setCanceledOnTouchOutside(false);
                picker.setDividerRatio(WheelView.DividerConfig.FILL);
                picker.setShadowColor(Color.RED, 40);
                if (!TextUtils.isEmpty(tvTitle.getText().toString().trim())) {
                    int index = 0;
                    for (int i = 0; i < arrTitle.length; i++) {
                        if (tvTitle.getText().toString().trim().equalsIgnoreCase(arrTitle[i])) {
                            index = i;
                            break;
                        }
                    }
                    picker.setSelectedIndex(index);
                } else {
                    picker.setSelectedIndex(3);
                }
                picker.setCycleDisable(true);
                picker.setOffset(3);
                picker.setTextSize(16);
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
                        tvTitle.setText(item);
                    }
                });
                picker.show();
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void checkIPAddress() {
        GetIpAddressRequest.exeRequest(GetIpAddressRequest.generateUrl(AccountManager.getInstance().getUserId()), new IProtocolResponse() {
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
                if (object == null) {
                    Log.e("ImproveUserActivity", "checkIPAddress response is null");
                    return;
                }
                UserIpAddress userIpInfo = (UserIpAddress) object;
                locProvince = userIpInfo.province;
                locCity = userIpInfo.city;
                tvLocation.setText(userIpInfo.province + " " + userIpInfo.city);
                Log.e("ImproveUserActivity", "userIpInfo== " + userIpInfo.toString());
            }
        });
    }

    protected void changeUserByPara() {
        String params = ImproveUserRequest.generateUrl(AccountManager.getInstance().getUserId(), tvGender.getText().toString().trim(),
                tvAge.getText().toString().trim(), locProvince, locCity, tvTitle.getText().toString().trim());
        ImproveUserRequest.exeRequest(params, new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                finish();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
                finish();
            }

            @Override
            public void response(Object object) {
                Log.e("ImproveUserActivity", "object== " + object.toString());
                String userId = ConfigManager.getInstance().loadString("userId");
                ConfigManager.getInstance().putBoolean("userId_" + userId, false);
                Log.e("ImproveUserActivity", "userId== " + ConfigManager.getInstance().loadBoolean("userId_" + userId, false));
                finish();
            }
        });
    }

    protected void UpdateUIByPara() {
        UserInfoDetailRequest.exeRequest(UserInfoDetailRequest.generateUrl(AccountManager.getInstance().getUserId()), new IProtocolResponse() {
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
                userDetailInfo = (MostDetailInfo) object;
                if (userDetailInfo == null) {
                    Log.e("ImproveUserActivity", "response userDetailInfo is null. ");
                    return;
                }
                userDetailInfo.format(context, userDetailInfo);
                Log.e("ImproveUserActivity", "userDetailInfo.age= " + userDetailInfo.age);
                Log.e("ImproveUserActivity", "userDetailInfo.resideLocation= " + userDetailInfo.resideLocation);
                if ("1".equalsIgnoreCase(userDetailInfo.gender)) {
                    tvGender.setText("男生");
                } else {
                    tvGender.setText("女生");
                }
                tvAge.setText(userDetailInfo.age);
                tvLocation.setText(userDetailInfo.resideLocation);
                tvTitle.setText(userDetailInfo.occupation);
                String[] location = userDetailInfo.resideLocation.split("\\s+");
                if (location != null) {
                    if (location.length > 1) {
                        locProvince = location[0];
                        locCity = location[1];
                    } else if (location.length > 0) {
                        locProvince = "";
                        locCity = userDetailInfo.resideLocation;
                    }
                }
            }
        });
    }

}
