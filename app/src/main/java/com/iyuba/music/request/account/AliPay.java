package com.iyuba.music.request.account;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.InterNetUtil;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.RandomUtil;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by 10202 on 2017/1/9.
 */

public class AliPay {
    private static String generateCode(String userId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return MD5.getMD5ofStr(userId + "iyuba" + df.format(System.currentTimeMillis()));
    }

    public static void exeRequest(String url, final IProtocolResponse response) {
        if (InterNetUtil.isNetworkConnected(MusicApplication.getApp())) {
            MyJsonRequest request = new MyJsonRequest(
                    url, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        apiEntity.setMessage(jsonObject.getString("message"));
                        if ("1".equals(jsonObject.getString("result"))) {
                            apiEntity.setData(TextAttr.decode(jsonObject.getString("orderInfo")));
                            apiEntity.setValue(jsonObject.getString("sign"));
                            apiEntity.setState(BaseApiEntity.SUCCESS);
                        } else {
                            apiEntity.setState(BaseApiEntity.FAIL);
                        }
                        response.response(apiEntity);
                    } catch (JSONException e) {
                        response.onServerError(RuntimeManager.getString(R.string.data_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    response.onServerError(VolleyErrorHelper.getMessage(error));
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl(String subject, String body, String cost, String month, String productId) {
        String originalUrl = "http://vip." + ConstantManager.IYUBA_CN + "chargeapinew.jsp";
        Map<String, Object> paras = new ArrayMap<>();
        paras.put("WIDseller_email", "iyuba@sina.com");
        paras.put("WIDout_trade_no", getOutTradeNo());
        paras.put("WIDsubject", subject);
        paras.put("WIDbody", body);
        paras.put("WIDtotal_fee", cost);
        paras.put("WIDdefaultbank", "");
        paras.put("app_id", Constant.APPID);
        paras.put("userId", AccountManager.getInstance().getUserId());
        paras.put("amount", month);
        paras.put("product_id", productId);
        paras.put("code", generateCode(AccountManager.getInstance().getUserId()));
        return ParameterUrl.setRequestParameter(originalUrl, paras);
    }

    private static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        key = key + Math.abs(RandomUtil.getInstance().random.nextInt());
        key = key.substring(0, 15);
        return key;
    }
}
