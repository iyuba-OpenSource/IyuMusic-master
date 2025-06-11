package com.iyuba.music.activity.vip;

import android.app.Dialog;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.iyuba.imooclib.ui.web.Web;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.pay.BuyIyubiActivity;
import com.iyuba.music.data.Constant;
import com.iyuba.music.dubbing.utils.Share;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.event.BuyIyubiEvent;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.apprequest.QunRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.InterNetUtil;
import com.iyuba.music.util.ScreenUtils;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.recycleview.StationaryGridview;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 会员中心
 */
public class NewVipCenterActivity extends BaseActivity {
    private Context mContext;
    private RelativeLayout rl_buyforevervip;
    private StationaryGridview gv_tequan;
    private TextView localVip, goldVip;
    private String username;
    private String iyubi;
    private CustomDialog wettingDialog;
    private ImageView iv_back;
    private TabHost th;
    private ContextCompat contextCompat;
    private TextView tv_vip_html;

    private double price;


    private TextView tv_user_name, tv_aiyubi, tv_time;
    private CircleImageView circularImageView;
    private String orderInfo;

    RelativeLayout all_app_re_1;

    RelativeLayout all_app_re_2;

    RelativeLayout all_app_re_3;

    RelativeLayout all_app_re_4;

    RelativeLayout all_app_re_5;

    RelativeLayout my_app_re_1;

    RelativeLayout my_app_re_2;

    RelativeLayout my_app_re_3;

    RelativeLayout my_app_re_4;

    RelativeLayout gold_app_re_1;

    RelativeLayout gold_app_re_2;

    RelativeLayout gold_app_re_3;

    RelativeLayout gold_app_re_4;

    CheckBox all_app_cb_1;

    CheckBox all_app_cb_2;

    CheckBox all_app_cb_3;

    CheckBox all_app_cb_4;

    CheckBox all_app_cb_5;

    CheckBox my_app_cb_1;

    CheckBox my_app_cb_2;
    CheckBox my_app_cb_3;

    CheckBox my_app_cb_4;

    CheckBox gold_app_cb_1;

    CheckBox gold_app_cb_2;

    CheckBox gold_app_cb_3;

    CheckBox gold_app_cb_4;

    TextView tv_submit;

    TextView tv_submit_my_app;

    TextView tv_submit_gold;
    TextView tv_vip_mini;
    ImageButton imageServe;
    private int all_app_vip_month = -1, my_app_month = -1, gold_app_month = -1;


    private void initOperation() {

        iv_back = findViewById(R.id.iv_back);
        imageServe = findViewById(R.id.btn_serve);
        circularImageView = findViewById(R.id.image_head);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_aiyubi = findViewById(R.id.tv_aiyubi);
        tv_time = findViewById(R.id.tv_time);
        tv_vip_html = findViewById(R.id.tv_vip_html);
        th = findViewById(R.id.tabhost);
        gv_tequan = findViewById(R.id.gridview);
        all_app_re_1 = findViewById(R.id.all_app_re_1);
        all_app_cb_1 = findViewById(R.id.all_app_cb_1);
        all_app_re_2 = findViewById(R.id.all_app_re_2);
        all_app_cb_2 = findViewById(R.id.all_app_cb_2);
        all_app_re_3 = findViewById(R.id.all_app_re_3);
        all_app_cb_3 = findViewById(R.id.all_app_cb_3);
        all_app_re_4 = findViewById(R.id.all_app_re_4);
        all_app_cb_4 = findViewById(R.id.all_app_cb_4);
        all_app_re_5 = findViewById(R.id.all_app_re_5);
        all_app_cb_5 = findViewById(R.id.all_app_cb_5);
        tv_submit = findViewById(R.id.tv_submit);
        my_app_re_1 = findViewById(R.id.my_app_re_1);
        my_app_cb_1 = findViewById(R.id.my_app_cb_1);
        my_app_re_2 = findViewById(R.id.my_app_re_2);
        my_app_cb_2 = findViewById(R.id.my_app_cb_2);
        my_app_re_3 = findViewById(R.id.my_app_re_3);
        my_app_cb_3 = findViewById(R.id.my_app_cb_3);
        my_app_re_4 = findViewById(R.id.my_app_re_4);
        my_app_cb_4 = findViewById(R.id.my_app_cb_4);
        tv_submit_my_app = findViewById(R.id.tv_submit_my_app);
        goldVip = findViewById(R.id.gold_tv);
        gold_app_re_1 = findViewById(R.id.gold_app_re_1);
        gold_app_cb_1 = findViewById(R.id.gold_app_cb_1);
        gold_app_re_2 = findViewById(R.id.gold_app_re_2);
        gold_app_cb_2 = findViewById(R.id.gold_app_cb_2);
        gold_app_re_3 = findViewById(R.id.gold_app_re_3);
        gold_app_cb_3 = findViewById(R.id.gold_app_cb_3);
        gold_app_re_4 = findViewById(R.id.gold_app_re_4);
        gold_app_cb_4 = findViewById(R.id.gold_app_cb_4);
        tv_submit_gold = findViewById(R.id.tv_submit_gold);
        tv_vip_mini = findViewById(R.id.tv_gongzhonghao);


        my_app_cb_3 = findViewById(R.id.my_app_cb_3);
        tv_vip_mini = findViewById(R.id.tv_gongzhonghao);
        tv_vip_mini = findViewById(R.id.tv_gongzhonghao);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vip);
        mContext = this;

        /**
         * 设置view高度为statusbar的高度，并填充statusbar
         */

        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        View mStatusBar = findViewById(R.id.fillStatusBarView);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mStatusBar.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = statusBarHeight;
        mStatusBar.setLayoutParams(lp);


        initOperation();
        initView();
        setDefault();
        getQunRequest();
    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfo userInfo = AccountManager.getInstance().getUserInfo();
        if (userInfo == null) {
            Log.e("NewVipCenterActivity", "onResume userInfo is null. ");
            userInfo = new UserInfo();
//            Log.e("NewVipCenterActivity", "onResume userInfo " + userInfo.toString());
        }

        username = userInfo.getUsername();
        if (TextUtils.isEmpty(username)) {
            if (TextUtils.isEmpty(userInfo.getUid()) || "0".equals(userInfo.getUid())) {
                username = "未登录";
            } else {
                username = userInfo.getUid();
            }
        }

        iyubi = userInfo.getIyubi();
        if (TextUtils.isEmpty(iyubi) || "null".equalsIgnoreCase(iyubi)) {
            iyubi = "0";
        }
        if (!"0".equals(userInfo.getUid())) {
            ImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), circularImageView);
        } else {
            circularImageView.setImageResource(R.drawable.defaultavatar);
        }

        tv_user_name.setText(username);
        tv_aiyubi.setText("爱语币余额：" + iyubi);
        if (Integer.parseInt(userInfo.getVipStatus()) >= 1) {
            tv_time.setVisibility(View.VISIBLE);
            tv_time.setText(userInfo.getDeadline());
        } else {
            tv_time.setVisibility(View.GONE);
        }

    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> NewVipCenterActivity.this.finish());

        btn_buyiyuba = (Button) findViewById(R.id.btn_buyiyuba);
        tv_vip_html = (TextView) findViewById(R.id.tv_vip_html);


        tv_aiyubi = (TextView) findViewById(R.id.tv_aiyubi);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        circularImageView = findViewById(R.id.image_head);

        circularImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(AccountManager.getInstance().getUserId()) || "0".equals(AccountManager.getInstance().getUserId()) ||
                        !AccountManager.getInstance().checkUserLogin()) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
        imageServe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, imageServe);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.toolbarmenu, popup.getMenu());
                Menu menu = popup.getMenu();
                menu.getItem(0).setTitle(InterNetUtil.getBrandName() + "用户群:" + ConfigManager.getInstance().getQQGroup());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                        if (item.getItemId() == R.id.test_qq) {
                            joinQQGroup(ConfigManager.getInstance().getQQKey());
                        } else if (item.getItemId() == R.id.content_qq) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url + "445167605")));
                        } else if (item.getItemId() == R.id.tycnolge_qq) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url + "1771874056")));
                        } else if (item.getItemId() == R.id.tousu_qq) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url + "572828703")));
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
//        tv_vip_html.setOnClickListener(ocl);
        btn_buyiyuba.setOnClickListener(ocl);
        th = (TabHost) findViewById(R.id.tabhost);


        gv_tequan = (StationaryGridview) findViewById(R.id.gridview);
        localVip = (TextView) findViewById(R.id.view4);
        goldVip = (TextView) findViewById(R.id.gold_tv);


        my_app_re_1.setOnClickListener(ocl);
        my_app_re_2.setOnClickListener(ocl);
        my_app_re_3.setOnClickListener(ocl);
        my_app_re_4.setOnClickListener(ocl);
        all_app_re_1.setOnClickListener(ocl);
        all_app_re_2.setOnClickListener(ocl);
        all_app_re_3.setOnClickListener(ocl);
        all_app_re_4.setOnClickListener(ocl);
        all_app_re_5.setOnClickListener(ocl);

        gold_app_re_4.setOnClickListener(ocl);
        gold_app_re_3.setOnClickListener(ocl);
        gold_app_re_2.setOnClickListener(ocl);
        gold_app_re_1.setOnClickListener(ocl);

        tv_submit.setOnClickListener(ocl);
        tv_submit_gold.setOnClickListener(ocl);
        tv_submit_my_app.setOnClickListener(ocl);

        RelativeLayout re1 = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.vip_tab_item, null);

        TextView tv_1 = re1.findViewById(R.id.tv_item);
        ImageView iv_1 = re1.findViewById(R.id.iv_item);
        TextView tv_1_course = re1.findViewById(R.id.tv_course);

        tv_1.setText("全站VIP");

        tv_1_course.setText("（不含微课）");
        iv_1.setImageResource(R.drawable.all_vip);

        RelativeLayout re2 = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.vip_tab_item, null);
        TextView tv_2 = re2.findViewById(R.id.tv_item);
        TextView tv_2_course = re2.findViewById(R.id.tv_course);
        ImageView iv_2 = re2.findViewById(R.id.iv_item);
        tv_2.setText("本应用VIP");
        tv_2_course.setText("（不含微课）");
        iv_2.setImageResource(R.drawable.forever_vip);

        RelativeLayout re3 = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.vip_tab_item, null);
        TextView tv_3 = re3.findViewById(R.id.tv_item);
        TextView tv_3_course = re3.findViewById(R.id.tv_course);
        ImageView iv_3 = re3.findViewById(R.id.iv_item);
        tv_3.setText("黄金VIP");
        tv_3_course.setText("（全部微课）");
        iv_3.setImageResource(R.drawable.gold_vip);


        th.setup();
        th.addTab(th.newTabSpec("tab1").setIndicator(re1).setContent(R.id.view1));
        th.addTab(th.newTabSpec("tab2").setIndicator(re2).setContent(R.id.view2));
        th.addTab(th.newTabSpec("tab3").setIndicator(re3).setContent(R.id.view3));
        final MyGridAdapter mga = new MyGridAdapter(mContext);
        gv_tequan.setAdapter(mga);
        localVip.setText("1. 尊贵V标识\n" +
                "2. 同享全站会员无广告、高速下载、语音调速等特权\n" +
                "3. 看一看 无限下载\n" +
//                "4. 专享单词闯关模块\n" +
                "4. 普通用户每篇句子评测仅限3句，VIP用户无限制评测\n" +
                "5. 本应用VIP仅限Android平台本应用使用(不含微课)\n" +
                "6. 无限制保存视频到手机相册;\n" +
                "7. VIP更多功能，敬请期待");

        if (getPackageName().equals("com.iyuba.concept2")) {

            goldVip.setText(
                    "1. 微课模块: VOA英语Lisa博士、Rachel美语发音；Alex博士BBC英式发音；新概念英语全4册；四六级名师陈苏灵、李尚龙、石雷鹏、尹延、章敏等；托福、雅思、考研、学位英语、中职英语等名师讲解所有课程全部免费学习；\n" +
                            "2.尊享爱语吧旗下全站会员特权。"
            );

        } else {
            goldVip.setText(
                    "1.VOA英语Lisa博士、Rachel美语发音，Alex博士BBC英式发音；新概念英语全四册、走遍美国等名师讲解所有课程全部免费学习；\n2.尊享爱语吧旗下全站会员特权。"

            );

        }

        View view = th.getTabWidget().getChildAt(0);
        view.setBackgroundColor(0xfffdda94);
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.e("NewVipCenterActivity", "tabid = " + tabId);
                View view;
                switch (tabId) {
                    case "tab1":
                        view = th.getTabWidget().getChildAt(0);
                        view.setBackgroundColor(0xfffdda94);
                        view = th.getTabWidget().getChildAt(1);
                        view.setBackgroundColor(0x00FFFFFF);
                        view = th.getTabWidget().getChildAt(2);
                        view.setBackgroundColor(0x00FFFFFF);
                        break;
                    case "tab2":
                        view = th.getTabWidget().getChildAt(0);
                        view.setBackgroundColor(0x00FFFFFF);
                        view = th.getTabWidget().getChildAt(1);
                        view.setBackgroundColor(0xfffdda94);
                        view = th.getTabWidget().getChildAt(2);
                        view.setBackgroundColor(0x00FFFFFF);
                        break;
                    case "tab3":
                        view = th.getTabWidget().getChildAt(0);
                        view.setBackgroundColor(0x00FFFFFF);
                        view = th.getTabWidget().getChildAt(1);
                        view.setBackgroundColor(0x00FFFFFF);
                        view = th.getTabWidget().getChildAt(2);
                        view.setBackgroundColor(0xfffdda94);
                        break;
                }
            }
        });
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (ConstantManager.APP_SHARE_WXMINIPROGRAM < 1) {
                    ToastUtil.showToast(mContext, "对不起，分享暂时不支持");
                    return;
                }
                Log.e("NewVipCenterActivity", "createWXAPI is called.");
                if (!Share.isWXSmallAvailable(mContext)) {
                    ToastUtil.showToast(mContext, "您的手机暂时不支持跳转到微信小程序，谢谢！");
                    return;
                }
                IWXAPI api = WXAPIFactory.createWXAPI(mContext, ConstantManager.WX_KEY);
                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                req.userName = ConstantManager.WX_NAME;
                String minipath = String.format("pages/index/index?uid=%s&appid=%s", AccountManager.getInstance().getUserId(), Constant.APPID);
                Log.e("NewVipCenterActivity", " MiniProgram path " + minipath);
                req.path = minipath;
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
                api.sendReq(req);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        };
        String gongzhonghao = getResources().getString(R.string.vip_gongzhonghao);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(gongzhonghao);
        spannableStringBuilder.setSpan(clickableSpan, gongzhonghao.indexOf("立即领取"), gongzhonghao.indexOf("立即领取") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_vip_mini.setText(spannableStringBuilder);
        tv_vip_mini.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void buyVip(int month) {
        Intent intent = new Intent(mContext, PayOrderActivity.class);
        price = getSpend(month);

        intent.putExtra("type", month);
        intent.putExtra("out_trade_no", getOutTradeNo());
        intent.putExtra("orderinfo", "购买" + month + "个月全站会员");
        intent.putExtra("subject", "全站会员");
        intent.putExtra("body", "花费" + price + "元购买全站会员");
        intent.putExtra("price", price + "");  //价格
        startActivity(intent);
    }

    public double getSpend(int month) {
        double result = 99.9;
        switch (month) {
            case 1:
                result = 50;
                break;
            case 6:
                result = 198;
                break;
            case 12:
                result = 298;
                break;
            case 36:
                result = 588;
                break;
        }
        return result;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            Dialog dialog;
            switch (msg.what) {
                case 0:
                    if (AccountManager.getInstance().checkUserLogin()) {
                        final int month = msg.arg1;
                        buyVip(month);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(mContext, LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    private void setDefault() {
        all_app_vip_month = 1;
        setAllAppCheckBox(all_app_vip_month, 2);

        my_app_month = 1;
        setAllAppCheckBox(my_app_month, 1);

        gold_app_month = 1;
        setAllAppCheckBox(gold_app_month, 3);

    }

    private void getQunRequest() {
        QunRequest.exeRequest(QunRequest.generateFullUrl(StartFragment.getBrand(), AccountManager.getInstance().getUserId(), Constant.APPID), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                Log.e("NewVipCenterActivity", "getQunRequest onNetError " + msg);
            }

            @Override
            public void onServerError(String msg) {
                Log.e("NewVipCenterActivity", "getQunRequest onServerError " + msg);
            }

            @Override
            public void response(Object object) {
                if (object == null) {
                    Log.e("NewVipCenterActivity", "getQunRequest response is null.");
                    return;
                }
                BaseApiEntity result = (BaseApiEntity) object;
                Log.e("NewVipCenterActivity", "getQunRequest response getState " + result.getState());
                if (BaseApiEntity.SUCCESS == result.getState()) {
                    Log.e("NewVipCenterActivity", "getQunRequest response getData " + result.getData());
                    Log.e("NewVipCenterActivity", "getQunRequest response getValue " + result.getValue());
                }
            }
        });
    }

    private OnClickListener ocl = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == all_app_re_1) {
                all_app_vip_month = 1;
                setAllAppCheckBox(all_app_vip_month, 2);
            } else if (v == all_app_re_2) {
                all_app_vip_month = 6;
                setAllAppCheckBox(all_app_vip_month, 2);
            } else if (v == all_app_re_3) {
                all_app_vip_month = 12;
                setAllAppCheckBox(all_app_vip_month, 2);
            } else if (v == all_app_re_4) {
                all_app_vip_month = 36;
                setAllAppCheckBox(all_app_vip_month, 2);
            } else if (v == all_app_re_5) {
                all_app_vip_month = 36;
                setAllAppCheckBox(all_app_vip_month, 2);

            } else if (v == my_app_re_4) {
                my_app_month = 36;
                setAllAppCheckBox(my_app_month, 1);

            } else if (v == my_app_re_3) {
                my_app_month = 12;
                setAllAppCheckBox(my_app_month, 1);

            } else if (v == my_app_re_2) {
                my_app_month = 6;
                setAllAppCheckBox(my_app_month, 1);

            } else if (v == my_app_re_1) {
                my_app_month = 1;
                setAllAppCheckBox(my_app_month, 1);

            } else if (v == btn_buyiyuba) {
                buyIyubi();
            } else if (v == gold_app_re_1) {
                gold_app_month = 1;
                setAllAppCheckBox(gold_app_month, 3);

            } else if (v == gold_app_re_2) {
                gold_app_month = 3;
                setAllAppCheckBox(gold_app_month, 3);

            } else if (v == gold_app_re_3) {
                gold_app_month = 6;
                setAllAppCheckBox(gold_app_month, 3);

            } else if (v == gold_app_re_4) {
                gold_app_month = 12;
                setAllAppCheckBox(gold_app_month, 3);

            } else if (v == tv_vip_html) {

                Intent intent = new Intent(NewVipCenterActivity.this, Web.class);
                intent.putExtra("url", "http://vip." + ConstantManager.IYUBA_CN + "vip/vip.html");
                intent.putExtra("title", "全站VIP");
                startActivity(intent);
            } else if (v == tv_submit) {
                if (all_app_vip_month == -1) {
                    ToastUtil.showToast(mContext, "请选择需要开通的VIP!");
                } else {
                    handler.obtainMessage(0, all_app_vip_month, 0).sendToTarget();
                }

            } else if (v == tv_submit_my_app) {
                if (my_app_month == -1) {
                    ToastUtil.showToast(mContext, "请选择需要开通的VIP!");
                } else {
                    buyCurrVip(my_app_month);
                }

            } else if (v == tv_submit_gold) {

                if (gold_app_month == -1) {
                    ToastUtil.showToast(mContext, "请选择需要开通的VIP!");
                } else {
                    buyGoldVip(gold_app_month);
                }

            }


        }
    };
    private Button btn_buyiyuba;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BuyIyubiEvent event) {
        if (event == null) {
            return;
        }
        Log.e("NewVipCenterActivity", "onEvent amount " + event.amount);
        iyubi = event.amount;
        if (TextUtils.isEmpty(iyubi) || "null".equalsIgnoreCase(iyubi)) {
            iyubi = "0";
        }
        tv_aiyubi.setText("爱语币余额：" + iyubi);
    }

    private void buyIyubi() {
        Intent intent = new Intent();
        intent.setClass(mContext, BuyIyubiActivity.class);
        intent.putExtra("title", "爱语币充值");
        startActivity(intent);
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }

    public View composeLayout(String s, int i) {
        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView iv = new ImageView(this);
        iv.setImageResource(i);
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 15, 0, 0);

        layout.addView(iv, lp);
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setText(s);
        tv.setTextColor(0xFF598aad);
        tv.setTextSize(14);
        LinearLayout.LayoutParams lpo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpo.setMargins(0, 0, 0, 15);
        layout.addView(tv, lpo);
        return layout;
    }

    private String goldPrice;

    private void buyGoldVip(int vip_type) {


        if (AccountManager.getInstance().checkUserLogin()) {


            Intent intent = new Intent();
            String subject;
            String body;
            subject = "黄金会员";

            switch (vip_type) {
                case 1:
                    goldPrice = "98";
                    orderInfo = "购买1个月黄金会员";
                    break;
                case 3:
                    goldPrice = "288";
                    orderInfo = "购买3个月黄金会员";
                    break;
                case 6:
                    goldPrice = "518";
                    orderInfo = "购买6个月黄金会员";
                    break;
                case 12:
                    goldPrice = "998";
                    orderInfo = "购买12个月黄金会员";
                    break;
            }

            body = ConstantManager.appName + "-" + "花费" + goldPrice + "元购买" + ConstantManager.appName + "黄金会员";
            intent.setClass(this, PayOrderActivity.class);
            intent.putExtra("orderinfo", orderInfo);
            intent.putExtra("type", vip_type);
            intent.putExtra("out_trade_no", getOutTradeNo());
            intent.putExtra("subject", subject);
            intent.putExtra("body", body);
            intent.putExtra("price", goldPrice);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(mContext, LoginActivity.class);
            startActivity(intent);
        }

    }

    private void buyCurrVip(int month) {


        if (AccountManager.getInstance().checkUserLogin()) {
            Intent intent = new Intent();
            String subject;
            String body = "";

            subject = "本应用会员";

            switch (month) {
                case 1:
                    goldPrice = "25";
                    body = "花费" + goldPrice + "元购买本应用1个月会员";
                    orderInfo = "购买本应用1个月会员";
                    break;
                case 6:
                    goldPrice = "69";
                    body = "花费" + goldPrice + "元购买本应用6个月会员";
                    orderInfo = "购买本应用6个月会员";
                    break;
                case 12:
                    goldPrice = "99";
                    body = "花费" + goldPrice + "元购买本应用12个月会员";
                    orderInfo = "购买本应用12个月会员";
                    break;
                case 36:
                    goldPrice = "199";
                    orderInfo = "购买本应用36个月会员";
                    body = "花费" + goldPrice + "元购买本应用36个月会员";
                    break;
            }
            intent.putExtra("orderinfo", orderInfo); //只是显示下个页面订单信息
            intent.putExtra("body", body);
            intent.setClass(this, PayOrderActivity.class);
            intent.putExtra("type", month);
            intent.putExtra("out_trade_no", getOutTradeNo());
            intent.putExtra("subject", subject);
            intent.putExtra("price", goldPrice);
            startActivity(intent);


        } else {
            Intent intent = new Intent();
            intent.setClass(mContext, LoginActivity.class);
            startActivity(intent);
        }

    }

    private void setCheckBoxFalse(int flag) {
        if (flag == 1) {  //本应用vip
            my_app_cb_1.setChecked(false);
            my_app_cb_2.setChecked(false);
            my_app_cb_3.setChecked(false);
            my_app_cb_4.setChecked(false);

        } else if (flag == 2) { // 全站vip
            all_app_cb_1.setChecked(false);
            all_app_cb_2.setChecked(false);
            all_app_cb_3.setChecked(false);
            all_app_cb_4.setChecked(false);
            all_app_cb_5.setChecked(false);

        } else if (flag == 3) { //黄金会员
            gold_app_cb_1.setChecked(false);
            gold_app_cb_2.setChecked(false);
            gold_app_cb_3.setChecked(false);
            gold_app_cb_4.setChecked(false);
        }
    }


    private void setAllAppCheckBox(int month, int flag) {
        setCheckBoxFalse(flag);

        if (flag == 1) {
            if (month == 1) {
                my_app_cb_1.setChecked(true);
            } else if (month == 6) {
                my_app_cb_2.setChecked(true);
            }
            if (month == 12) {
                my_app_cb_3.setChecked(true);
            } else if (month == 36) {
                my_app_cb_4.setChecked(true);
            }
        } else if (flag == 2) {
            if (month == 1) {
                all_app_cb_1.setChecked(true);
            } else if (month == 6) {
                all_app_cb_2.setChecked(true);
            } else if (month == 12) {
                all_app_cb_3.setChecked(true);
            } else if (month == 36) {
                all_app_cb_4.setChecked(true);
            } else if (month == 36) {
                all_app_cb_5.setChecked(true);
            }
        } else if (flag == 3) {
            if (month == 1) {
                gold_app_cb_1.setChecked(true);
            } else if (month == 3) {
                gold_app_cb_2.setChecked(true);
            } else if (month == 6) {
                gold_app_cb_3.setChecked(true);
            } else if (month == 12) {
                gold_app_cb_4.setChecked(true);
            }
        }
    }

    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
