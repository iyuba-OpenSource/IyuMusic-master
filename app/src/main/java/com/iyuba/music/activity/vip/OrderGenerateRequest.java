package com.iyuba.music.activity.vip;

import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class OrderGenerateRequest extends BaseJsonObjectRequest {
    private static final String TAG = OrderGenerateRequest.class.getSimpleName();
    private static final String oldApi = "http://vip." + ConstantManager.IYUBA_CN + "alipay.jsp?";
    private static final String newApi = "http://vip." + ConstantManager.IYUBA_CN + "chargeapinew.jsp?";
    //    public String productId;
    public String result;
    public String message;
    public String alipayTradeStr;
//    public String orderInfo;
//    public String orderSign;

    public OrderGenerateRequest(String productId, String subject,
                                String total_fee, String body, String defaultbank, String app_id, String userId, String amount,
                                ErrorListener el, final RequestCallBack rc) {
//        this.productId = productId;
        super(Method.POST, oldApi + "&WIDsubject=" + subject + "&WIDbody=" + body + "&WIDtotal_fee=" + total_fee
                + "&app_id=" + app_id + "&userId=" + userId + "&amount=" + amount + "&product_id=" + productId
                + "&code=" + generateCode(userId), el);

        Log.e(TAG, oldApi + "&WIDsubject=" + subject + "&WIDbody=" + body + "&WIDtotal_fee=" + total_fee
                + "&app_id=" + app_id + "&userId=" + userId + "&amount=" + amount + "&product_id=" + productId
                + "&code=" + generateCode(userId));
        setResListener(new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObjectRoot) {
                try {
                    if (jsonObjectRoot.has("result")) {
                        result = jsonObjectRoot.getString("result");
                    }
                    if (jsonObjectRoot.has("message")) {
                        message = jsonObjectRoot.getString("message");
                    }
                    if (jsonObjectRoot.has("alipayTradeStr")) {
                        alipayTradeStr = jsonObjectRoot.getString("alipayTradeStr");
                    }
//                    if (isRequestSuccessful()) {
//                        orderInfo = URLDecoder.decode(jsonObjectRoot.getString("orderInfo"),
//                                "utf-8");
//                        orderSign = jsonObjectRoot.getString("sign");
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rc.requestResult(OrderGenerateRequest.this);
            }
        });
    }

    @Override
    public boolean isRequestSuccessful() {
        return "1".equals(result);
    }

    private static String generateCode(String userId) {
        String code = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        code = MD5.getMD5ofStr(userId + "iyuba" + df.format(System.currentTimeMillis()));
        return code;
    }

}
