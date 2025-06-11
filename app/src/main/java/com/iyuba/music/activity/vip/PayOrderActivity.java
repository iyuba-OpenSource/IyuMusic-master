package com.iyuba.music.activity.vip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.iyuba.music.BuildConfig;
import com.iyuba.music.MyWebActivity;
import com.iyuba.music.R;
import com.iyuba.music.SplashActivity;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.data.Constant;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.retrofit.IyumusicRetrofitNetwork;
import com.iyuba.music.retrofit.result.NotifyAliPayResponse;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by howard9891 on 2016/10/28.
 */

public class PayOrderActivity extends BaseActivity {
    private TextView payorder_username;
    private TextView payorder_rmb_amount;
    private TextView payorder_orderinfo_de;
    private NoScrollListView methodList;
    private PayMethodAdapter methodAdapter;
    private Button payorder_submit_btn;
    private boolean confirmMutex = true;
    private static final String TAG = PayOrderActivity.class.getSimpleName();
    private Context mContext;
    private static final String Seller = "iyuba@sina.com";
    private String price;
    private String subject;
    private String body;
    private String amount;
    private int type;
    private String out_trade_no;
    private IWXAPI msgApi;


    private String mWeiXinKey = ConstantManager.WXID;


    private int selectPosition = 0;
    private Button button;
    private String productId;
    private String orderInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = getIntent();

        setContentView(R.layout.activity_buyvip);
        initWidget();
        setListener();
        changeUIByPara();

        price = intent.getStringExtra("price");
        if (BuildConfig.DEBUG) {
//            price = "0.01";
        }

        type = intent.getIntExtra("type", -1);
        subject = intent.getStringExtra("subject");
        body = intent.getStringExtra("body");
        out_trade_no = intent.getStringExtra("out_trade_no");
        orderInfo = intent.getStringExtra("orderinfo");
        productId = getProductId(subject);
        amount = getAmount(type);
        findView();
        payorder_orderinfo_de.setText(orderInfo);
//        mWeiXinKey = mContext.getString(R.string.weixin_key);
//        mWeiXinKey = Constant.mWeiXinKey;
//        mWXAPI = WXAPIFactory.createWXAPI(this, mWeiXinKey, true);
        msgApi = WXAPIFactory.createWXAPI(mContext, null);
        // 将该app注册到微信
        msgApi.registerApp(ConstantManager.WXID);
        //此app暂无申请微信支付的权限
    }


    @Override
    public void onResume() {
        super.onResume();
        payorder_username.setText(AccountManager.getInstance().getUserInfo().getUsername());
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText("购买会员");
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    private String getAmount(int type) {
        String amount;
        if (type == 0) {
            amount = "0";
        } else {
            amount = type + "";
        }
        return amount;
    }

    private String getProductId(String str) {
        String productId = "";
        if (str.equals("本应用会员")) {

            productId = "10";
        } else if (str.equals("全站会员")) {

            productId = "0";
        } else if (str.equals("黄金会员")) {

            productId = "21";
        }


        /*if (type == 0) {
            productId = "10";
        } else {
            productId = "0";
        }*/
        return productId;
    }

    private void findView() {

        TextView buyvip_tv_agreement = findViewById(R.id.buyvip_tv_agreement);
        if (buyvip_tv_agreement != null) {

            String priStr = "点击支付即代表您已充分阅读并同意《会员服务协议》";
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(priStr);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.GRAY), 0, priStr.indexOf("《"), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {

                    String url = "http://iuserspeech.iyuba.cn:9001/api/vipServiceProtocol.jsp?company=" + BuildConfig.AGREEMENT_COMPANY + "&type=app";


                    MyWebActivity.startActivity(PayOrderActivity.this, url, "会员服务协议");

               /*     Intent intent = new Intent("com.iyuba.JLPT1Listening.activity.MyWebActivity");
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", url);
                    bundle.putString("TOOLBAR_NAME", "会员服务协议");
                    intent.putExtras(bundle);
                    startActivity(intent);*/

                }
            }, priStr.indexOf("《"), priStr.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);

            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#3b76f4")), priStr.indexOf("《"), priStr.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
            buyvip_tv_agreement.setMovementMethod(new LinkMovementMethod());
            buyvip_tv_agreement.setText(spannableStringBuilder);
        }

        button = (Button) findViewById(R.id.btn_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        payorder_username = (TextView) findViewById(R.id.payorder_username_tv);
        payorder_username.setText(AccountManager.getInstance().getUserInfo().getUsername());
        payorder_rmb_amount = (TextView) findViewById(R.id.payorder_rmb_amount_tv);
        payorder_rmb_amount.setText(price + "元");
        methodList = (NoScrollListView) findViewById(R.id.payorder_methods_lv);
        payorder_submit_btn = (Button) findViewById(R.id.payorder_submit_btn);
        payorder_orderinfo_de = (TextView) findViewById(R.id.payorder_orderinfo_de);
        methodList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                methodAdapter.changeSelectPosition(position);
                methodAdapter.notifyDataSetChanged();
            }
        });
        methodAdapter = new PayMethodAdapter(this);
        methodList.setAdapter(methodAdapter);
        payorder_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmMutex) {
                    confirmMutex = false;
                    String newSubject;
                    String newbody;
                    try {
                        newSubject = URLEncoder.encode(subject, "UTF-8");
                        newbody = URLEncoder.encode(body, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        newSubject = "iyubi";
                        newbody = "iyubi";
                    }
                    switch (selectPosition) {
                        case PayMethodAdapter.PayMethod.WEIXIN:
                            payByAlipay(newbody, newSubject);
                            break;
                        case PayMethodAdapter.PayMethod.ALIPAY:
                            Log.e("PayOrderActivity", "weixin");
                            if (msgApi.isWXAppInstalled()) {
                                payByWeiXin();
                            } else {
                                ToastUtil.showToast(mContext, "您还未安装微信客户端");
                            }
                            break;
                      /*  case PayMethodAdapter.PayMethod.BANKCARD:
                            payByWeb();
                            break;*/
                    }
                }
            }
        });
    }

    private void payByAlipay(String body, String subject) {
        confirmMutex = true;
        RequestCallBack rc = result -> {
            OrderGenerateRequest request = (OrderGenerateRequest) result;
            if ((request != null) && "200".equals(request.result)) {
                // 完整的符合支付宝参数规范的订单信息
                final String payInfo = request.alipayTradeStr;
                Log.e(TAG, "payByAlipay requestResult payInfo " + payInfo);
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(PayOrderActivity.this);
                        // 调用支付接口，获取支付结果
                        Map<String, String> result = alipay.payV2(payInfo, true);
//                        IyumusicRetrofitNetwork.getVipServiceApi().NotifyAliPay(map);

                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        alipayHandler.sendMessage(msg);
                    }
                };
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            } else {
                validateOrderFail();
            }
        };
        OrderGenerateRequest orderRequest = new OrderGenerateRequest(productId, subject, price, body, "", Constant.APPID, String.valueOf(AccountManager.getInstance().getUserId()), amount, mOrderErrorListener, rc);
        Volley.newRequestQueue(getApplication()).add(orderRequest);
    }

    private void payByWeiXin() {
        confirmMutex = true;
        RequestCallBack rc = new RequestCallBack() {
            @Override
            public void requestResult(Request result) {
                OrderGenerateWeiXinRequest first = (OrderGenerateWeiXinRequest) result;
                if (first.isRequestSuccessful()) {
                    Log.e(TAG, "OrderGenerateWeiXinRequest success!");
                    Log.e("firtt", first.toString());
                    PayReq req = new PayReq();
                    req.appId = mWeiXinKey;
                    req.partnerId = first.partnerId;
                    req.prepayId = first.prepayId;
                    req.nonceStr = first.nonceStr;
                    req.timeStamp = first.timeStamp;
                    req.packageValue = "Sign=WXPay";
                    req.sign = buildWeixinSign(req, first.mchkey);
                    msgApi.sendReq(req);
                } else {
                    validateOrderFail();
                }
            }
        };
        String uid = String.valueOf(AccountManager.getInstance().getUserId());
        OrderGenerateWeiXinRequest request = new OrderGenerateWeiXinRequest(productId, mWeiXinKey, Constant.APPID, uid, price, amount, body, mOrderErrorListener, rc);
        Volley.newRequestQueue(getApplication()).add(request);

    }

    private String buildWeixinSign(PayReq payReq, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildWeixinStringA(payReq));
        sb.append("&key=").append(key);
        Log.i(TAG, sb.toString());
        return MD5.getMD5ofStr(sb.toString()).toUpperCase();
    }

    private String buildWeixinStringA(PayReq payReq) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(payReq.appId);
        sb.append("&noncestr=").append(payReq.nonceStr);
        sb.append("&package=").append(payReq.packageValue);
        sb.append("&partnerid=").append(payReq.partnerId);
        sb.append("&prepayid=").append(payReq.prepayId);
        sb.append("&timestamp=").append(payReq.timeStamp);
        return sb.toString();
    }

    /*private void payByWeb() {
        String url = "http://app." + Constant.IYUBA_CN + "wap/servlet/paychannellist?";
        url += "out_user=" + AccountManager.Instace(mContext).userId;
        url += "&appid=" + Constant.APPID;
        url += "&amount=" + 0;
        Intent intent = WebActivity.buildIntent(this, url, "订单支付");
        startActivity(intent);
        confirmMutex = true;
        finish();
    }*/

    private void validateOrderFail() {
        ToastUtil.showToast(mContext, "服务器正忙,请稍后再试!");
        PayOrderActivity.this.finish();
//        new AlertDialog.Builder(PayOrderActivity.this)
//                .setTitle("订单异常!")
//                .setMessage("订单验证失败!")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        PayOrderActivity.this.finish();
//                    }
//                })
//                .show();
    }

    private Response.ErrorListener mOrderErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
//            ToastUtil.showToast(mContext, "订单异常!");
//            PayOrderActivity.this.finish();
            new AlertDialog.Builder(PayOrderActivity.this).setTitle("订单提交出现问题!").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmMutex = true;
                    dialog.dismiss();
                    PayOrderActivity.this.finish();
                }
            }).show();
        }
    };


    private Handler alipayHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    confirmMutex = true;
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    Log.e(TAG, "resultstatus " + resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastUtil.showToast(PayOrderActivity.this, "如果vip状态没改变，请重新登录一下。");
                        Map<String, String> map = new HashMap<>();
                        map.put("data", ((Map<String, String>) msg.obj).toString());
                        IyumusicRetrofitNetwork.getVipServiceApi().NotifyAliPay(map).enqueue(new retrofit2.Callback<NotifyAliPayResponse>() {
                            @Override
                            public void onResponse(Call<NotifyAliPayResponse> call, retrofit2.Response<NotifyAliPayResponse> response) {
                                NotifyAliPayResponse bean = response.body();
                                if (bean != null) {
                                    Log.e("PayOrderActivity", "NotifyAliPay onResponse code " + bean.code);
                                }
                                getUserInfo();
                            }

                            @Override
                            public void onFailure(Call<NotifyAliPayResponse> call, Throwable t) {
                                if (t != null) {
                                    Log.e("PayOrderActivity", "NotifyAliPay onFailure " + t.getMessage());
                                }
                            }
                        });
                        new AlertDialog.Builder(PayOrderActivity.this).setTitle("提示").setMessage("支付成功").setNegativeButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                PayOrderActivity.this.finish();
                            }
                        }).show();

                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtil.showToast(PayOrderActivity.this, "支付结果确认中");
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            ToastUtil.showToast(PayOrderActivity.this, "您已取消支付");
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            ToastUtil.showToast(PayOrderActivity.this, "网络连接出错");
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            ToastUtil.showToast(PayOrderActivity.this, "支付失败");
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    private void getUserInfo() {

        AccountManager.getInstance().refreshVipStatus();

//        UserInfo userInfo = AccountManager.getInstance().getUserInfo();
//        userInfo.setVipStatus("1");
//        AccountManager.getInstance().setUserInfo(userInfo);
    }

}
