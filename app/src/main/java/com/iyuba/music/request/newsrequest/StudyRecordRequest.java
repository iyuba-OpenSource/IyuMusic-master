package com.iyuba.music.request.newsrequest;

import androidx.collection.ArrayMap;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.article.StudyRecord;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetMAC;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;
import com.umeng.commonsdk.debug.E;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by 10202 on 2015/10/8.
 */
public class StudyRecordRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {

        Log.e("StudyRecordRequest", url);
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {

                        BaseApiEntity apiEntity = new BaseApiEntity();
                        Log.e("StudyRecordRequest", jsonObject.toString() + "==");
                        if (jsonObject.getInt("result") == 1) {
                            apiEntity.setState(BaseApiEntity.SUCCESS);
                            if (jsonObject.getInt("jifen") == 0) {
                                apiEntity.setMessage("no");
                            } else {
                                apiEntity.setMessage("add");
                            }
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

    public static String generateUrl(String uid, StudyRecord studyRecord) {
        String originalUrl = "http://daxue." + ConstantManager.IYUBA_CN + "ecollege/updateStudyRecordNew.jsp";
        String device = android.os.Build.BRAND + android.os.Build.MODEL
                + android.os.Build.DEVICE;
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("appId", ConstantManager.appId);
        para.put("Lesson", studyRecord.getLesson());
        para.put("LessonId", studyRecord.getId());
        para.put("BeginTime", TextAttr.encode(studyRecord.getStartTime()));
        para.put("EndTime", TextAttr.encode(studyRecord.getEndTime()));
        para.put("EndFlg", studyRecord.getFlag());
        para.put("uid", uid);

        LogUtil.e("时间判断", StudyManager.getInstance().getAllTime() + "开始时间" + StudyManager.getInstance().getStartTime() + "结束时间" + DateFormat.formatTime(Calendar.getInstance().getTime()));

        //根据听歌时长计算单词数，使用系统时间
        try {
            long starTime = 0;
            if (!TextUtils.isEmpty(StudyManager.getInstance().getStartTime())) {
                starTime = DateFormat.getDateFromStr(StudyManager.getInstance().getStartTime());
            }
            long endTime = DateFormat.getDateFromStr(DateFormat.formatTime(Calendar.getInstance().getTime()));
            long spanTime = (endTime - starTime) / 1000;

            int allWords = Integer.parseInt(StudyManager.getInstance().getStudyWord());
            long newWords = allWords * spanTime / StudyManager.getInstance().getAllTime();
            LogUtil.e("时间判断", StudyManager.getInstance().getAllTime() + "开始时间" + starTime + "结束时间" + endTime + "时间间隔" + spanTime + "单词数" + newWords);


            if (newWords > allWords) {
                newWords = allWords;
            }
            para.put("TestWords", newWords);
        } catch (Exception e) {
            e.printStackTrace();
        }

        para.put("Device", TextAttr.encode(device));
        try {
            para.put("DeviceId", GetMAC.getMAC());
        } catch (Exception arg) {
            para.put("DeviceId", "");
        }
        para.put("TestNumber", ConfigManager.getInstance().getStudyMode());
        para.put("platform", "android");
        para.put("sign", MD5.getMD5ofStr(uid + studyRecord.getStartTime() + DateFormat.formatYear(Calendar.getInstance().getTime())));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }


    /**
     *
     * MTV 视频播放
     * @param uid
     * @param studyRecord
     * @return
     */
    public static String getMTVUrl(String uid, StudyRecord studyRecord) {
        String originalUrl = "http://daxue." + ConstantManager.IYUBA_CN + "ecollege/updateStudyRecordNew.jsp";
        String device = android.os.Build.BRAND + android.os.Build.MODEL
                + android.os.Build.DEVICE;
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("appId", ConstantManager.appId);
        para.put("Lesson", studyRecord.getLesson());
        para.put("LessonId", studyRecord.getId());
        para.put("BeginTime", TextAttr.encode(studyRecord.getStartTime()));
        para.put("EndTime", TextAttr.encode(studyRecord.getEndTime()));
        para.put("EndFlg", studyRecord.getFlag());
        para.put("uid", uid);


        //根据听歌时长计算单词数，使用系统时间
        try {
            long starTime = DateFormat.getDateFromStr(studyRecord.getStartTime());
            long endTime = DateFormat.getDateFromStr(studyRecord.getEndTime());
            long spanTime = (endTime - starTime) / 1000;

            int allWords = studyRecord.getLrcNum();
            long newWords = allWords * spanTime / studyRecord.getAllTime();
            LogUtil.e("时间判断", studyRecord.getAllTime() + "开始时间" + starTime + "结束时间" + endTime + "时间间隔" + spanTime + "单词数" + allWords);


            if (newWords > allWords) {
                newWords = allWords;
            }
            para.put("TestWords", newWords);
        } catch (Exception e) {
            e.printStackTrace();
        }

        para.put("Device", TextAttr.encode(device));
        try {
            para.put("DeviceId", GetMAC.getMAC());
        } catch (Exception arg) {
            para.put("DeviceId", "");
        }
        para.put("TestNumber", ConfigManager.getInstance().getStudyMode());
        para.put("platform", "android");
        para.put("sign", MD5.getMD5ofStr(uid + studyRecord.getStartTime() + DateFormat.formatYear(Calendar.getInstance().getTime())));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
