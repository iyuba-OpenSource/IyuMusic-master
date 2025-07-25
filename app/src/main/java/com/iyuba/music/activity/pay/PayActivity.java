package com.iyuba.music.activity.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.iyuba.music.BuildConfig;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.vip.OrderGenerateRequest;
import com.iyuba.music.activity.vip.PayResult;
import com.iyuba.music.activity.vip.RequestCallBack;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.event.BuyIyubiEvent;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.account.AliPay;
import com.iyuba.music.request.account.WxPay;
import com.iyuba.music.retrofit.IyumusicRetrofitNetwork;
import com.iyuba.music.retrofit.result.NotifyAliPayResponse;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
/**
 * Created by 10202 on 2017/1/9.
 */

public class PayActivity extends BaseActivity {
    //private static final String[] PAY_MONEY = {"0.01", "0.01", "0.01", "0.01", "0.01"};
    private static final String PAY_TYPE = "pay_type";
    private static final String PAY_DETAIL = "pay_detail";
    private static final String PAY_MONEY = "pay_money";
    private static final String PAY_GOODS = "pay_goods";
    private String payDetailString, payMoneyString, payGoods, payType;
    private TextView username, payDetail, payMoney;
    private ImageView wxSelected, baoSelected;
    private View wxView, baoView;
    private View paySure;
    private IyubaDialog waitingDialog;
    private boolean confirmMutex = true;
    private IWXAPI msgApi;
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    public static void launch(Activity context, String payDetail, String payMoney, String payGoods, String payType, int requestCode) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(PAY_TYPE, payType);
        intent.putExtra(PAY_DETAIL, payDetail);
        intent.putExtra(PAY_MONEY, payMoney);
        intent.putExtra(PAY_GOODS, payGoods);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_detail);
        context = this;
        payType = getIntent().getStringExtra(PAY_TYPE);
        payDetailString = getIntent().getStringExtra(PAY_DETAIL);
        payMoneyString = getIntent().getStringExtra(PAY_MONEY);
        if (BuildConfig.DEBUG){
            payMoneyString = "0.01";
            payType = "0";
            Log.e("PayActivity", "payByAlipay debug payType " + payType);
        }

        payGoods = getIntent().getStringExtra(PAY_GOODS);
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.pay_detail_going));
        msgApi = WXAPIFactory.createWXAPI(context, ConstantManager.WXID, false);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        username = (TextView) findViewById(R.id.pay_detail_user);
        payDetail = (TextView) findViewById(R.id.pay_detail_buy);
        payMoney = (TextView) findViewById(R.id.pay_detail_money);
        wxSelected = (ImageView) findViewById(R.id.pay_detail_wx_checked);
        baoSelected = (ImageView) findViewById(R.id.pay_detail_bao_checked);
        wxView = findViewById(R.id.pay_detail_wx);
        baoView = findViewById(R.id.pay_detail_bao);
        paySure = findViewById(R.id.pay_detail_sure);

        if (getPackageName().equals("com.iyuba.music")) {
            wxView.setVisibility(View.GONE);
        } else {
            wxView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void setListener() {
        super.setListener();
        wxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWxSelect();
            }
        });
        baoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBaoSelect();
            }
        });
        View.OnClickListener payListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        };
        toolbarOper.setOnClickListener(payListener);
        paySure.setOnClickListener(payListener);
    }

    private void pay() {
        if (wxSelected.getVisibility() == View.VISIBLE) {
            if (msgApi.isWXAppInstalled()) {
                wechatPay();
            } else {
                CustomToast.getInstance().showToast(R.string.pay_detail_no_wechat);
            }
        } else {
//            aliPay();
            if (confirmMutex) {
                confirmMutex = false;
                aliPayNew();
            } else {
                CustomToast.getInstance().showToast("请不要重复提交");
            }
        }
    }

    private void showWxSelect() {
        wxSelected.setVisibility(View.VISIBLE);
        wxSelected.setColorFilter(GetAppColor.getInstance().getAppColor());
        baoSelected.setVisibility(View.GONE);
    }

    private void showBaoSelect() {
        wxSelected.setVisibility(View.GONE);
        baoSelected.setVisibility(View.VISIBLE);
        baoSelected.setColorFilter(GetAppColor.getInstance().getAppColor());
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.pay_detail_title);
        toolbarOper.setText(R.string.pay_detail_oper);
        username.setText(AccountManager.getInstance().getUserInfo().getUsername());
        payDetail.setText(payDetailString);
        payMoney.setText(getString(R.string.pay_detail_money_content, payMoneyString));
        showBaoSelect();
    }

    private void wechatPay() {
        waitingDialog.show();
        WxPay.exeRequest(WxPay.generateUrl(payMoneyString, payGoods, payType), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(R.string.pay_detail_generate_failed);
                waitingDialog.dismiss();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(R.string.pay_detail_generate_failed);
                waitingDialog.dismiss();
            }

            @Override
            public void response(Object object) {
                waitingDialog.dismiss();
                BaseApiEntity result = (BaseApiEntity) object;
                if (BaseApiEntity.isSuccess(result)) {
                    PayReq req = (PayReq) result.getData();
                    msgApi.registerApp(ConstantManager.WXID);
                    msgApi.sendReq(req);
                } else {
                    CustomToast.getInstance().showToast(R.string.pay_detail_generate_failed);
                }
            }
        });
    }

    private void aliPay() {
        waitingDialog.show();
        String subject = TextAttr.encode(payDetail.getText().toString());
        String body = TextAttr.encode(payMoney.getText().toString());
        AliPay.exeRequest(AliPay.generateUrl(subject, body, payMoneyString, payGoods, payType), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(R.string.pay_detail_generate_failed);
                waitingDialog.dismiss();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(R.string.pay_detail_generate_failed);
                waitingDialog.dismiss();
            }

            @Override
            public void response(Object object) {
                BaseApiEntity baseApiEntity = (BaseApiEntity) object;
                waitingDialog.dismiss();
                if (BaseApiEntity.isSuccess(baseApiEntity)) {
                    final String payInfo = baseApiEntity.getData() + "&sign=\"" + baseApiEntity.getValue()
                            + "\"&" + "sign_type=\"RSA\"";
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(PayActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(payInfo, true);
                            handler.obtainMessage(0, result).sendToTarget();
                        }
                    };
                    ThreadPoolUtil.getInstance().execute(payRunnable);
                } else {
                    CustomToast.getInstance().showToast(R.string.pay_detail_generate_failed);
                }
            }
        });
    }

    private void aliPayNew() {
        waitingDialog.show();
        String subject = TextAttr.encode(payDetail.getText().toString());
        String body = TextAttr.encode(payMoney.getText().toString());
        RequestCallBack rc = result -> {
            waitingDialog.dismiss();
            confirmMutex = true;
            OrderGenerateRequest request = (OrderGenerateRequest) result;
            if ((request != null) && "200".equals(request.result)) {
                // 完整的符合支付宝参数规范的订单信息
                final String payInfo = request.alipayTradeStr;
                Log.e("PayActivity", "payByAlipay requestResult payInfo " + payInfo);
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(PayActivity.this);
                        // 调用支付接口，获取支付结果
                        Map<String, String> result = alipay.payV2(payInfo, true);
//                        IyumusicRetrofitNetwork.getVipServiceApi().NotifyAliPay(map);

                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                };
                ThreadPoolUtil.getInstance().execute(payRunnable);
            } else {
                CustomToast.getInstance().showToast(R.string.pay_detail_generate_failed);
            }
        };
        OrderGenerateRequest orderRequest = new OrderGenerateRequest(payType, subject, payMoneyString, body, "", Constant.APPID, String.valueOf(AccountManager.getInstance().getUserId()), payGoods, mOrderErrorListener, rc);
        Volley.newRequestQueue(getApplication()).add(orderRequest);
    }

    private Response.ErrorListener mOrderErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            new AlertDialog.Builder(PayActivity.this).setTitle("订单提交出现问题!").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmMutex = true;
                    dialog.dismiss();
                    PayActivity.this.finish();
                }
            }).show();
        }
    };

    private class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<PayActivity> {
        @Override
        public void handleMessageByRef(final PayActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Map<String, String> map = new HashMap<>();
                        map.put("data", ((Map<String, String>) msg.obj).toString());
                        IyumusicRetrofitNetwork.getVipServiceApi().NotifyAliPay(map).enqueue(new retrofit2.Callback<NotifyAliPayResponse>() {
                            @Override
                            public void onResponse(Call<NotifyAliPayResponse> call, retrofit2.Response<NotifyAliPayResponse> response) {
                                NotifyAliPayResponse bean = response.body();
                                if (bean != null) {
                                    Log.e("PayActivity", "NotifyAliPay onResponse code " + bean.code);
                                }
                            }
                            @Override
                            public void onFailure(Call<NotifyAliPayResponse> call, Throwable t) {
                                if (t != null) {
                                    Log.e("PayActivity", "NotifyAliPay onFailure " + t.getMessage());
                                }
                            }
                        });
                        UserInfo userInfo = AccountManager.getInstance().getUserInfo();
                        userInfo.setVipStatus("1");
                        if ("1".equals(payType)) {
                            if (TextUtils.isEmpty(userInfo.getIyubi())) {
                                userInfo.setIyubi(payMoneyString);
                            } else {
                                int iyu = 0;
                                try {
                                    iyu = Integer.parseInt(userInfo.getIyubi());
                                    iyu += Integer.parseInt(payMoneyString);
                                } catch (Exception var1) { }
                                userInfo.setIyubi(iyu + "");
                            }
                            EventBus.getDefault().post(new BuyIyubiEvent(payMoneyString, payGoods));
                        }
                        AccountManager.getInstance().setUserInfo(userInfo);
                        final MyMaterialDialog dialog = new MyMaterialDialog(activity.context);
                        dialog.setTitle(R.string.app_name).setMessage(R.string.pay_detail_success);
                        dialog.setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                activity.setResult(RESULT_OK);
                                activity.finish();
                            }
                        });
                        dialog.show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            CustomToast.getInstance().showToast("支付结果确认中");
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            CustomToast.getInstance().showToast(R.string.pay_detail_cancel);
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            CustomToast.getInstance().showToast("网络连接出错");
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            CustomToast.getInstance().showToast(R.string.pay_detail_fail);
                        }
                    }
                    break;
            }
        }
    }
}
