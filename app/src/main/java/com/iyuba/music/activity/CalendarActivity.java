package com.iyuba.music.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.model.ShareInfoRecord;
import com.iyuba.music.data.model.VoicesResult;
import com.iyuba.music.dubbing.utils.NetStateUtil;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.retrofit.IyumusicRetrofitNetwork;
import com.iyuba.music.retrofit.api.GetVioceApi;
import com.iyuba.music.util.InterNetUtil;
import com.iyuba.music.widget.CommonProgressDialog;
import com.othershe.calendarview.bean.DateBean;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.listener.OnSingleChooseListener;
import com.othershe.calendarview.utils.CalendarUtil;
import com.othershe.calendarview.weiget.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by carl shen on 2021/6/18
 * English Music, new study experience.
 */
public class CalendarActivity extends BaseInputActivity {
    private static final String TAG = CalendarActivity.class.getSimpleName();
    private GetVioceApi getVioceApi;

    TextView calendar_title;
    TextView chooseDate;
    CalendarView calendarView;
    private int[] cDate = CalendarUtil.getCurrentDate();
    private String curTime = "";
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        calendar_title = findViewById(R.id.calendar_title);
        calendarView = findViewById(R.id.calendar);
        chooseDate = findViewById(R.id.choose_date);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        title.setText("打卡报告");
        getVioceApi = IyumusicRetrofitNetwork.getVioceApi();
        calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView
//                .setSpecifyMap(map)
                .setStartEndDate("2016.1", "2028.12")
                .setDisableStartEndDate("2016.10.10", "2028.10.10")
                .setInitDate(cDate[0] + "." + cDate[1])
                .setSingleDate(cDate[0] + "." + cDate[1] + "." + cDate[2])
                .init();
        calendar_title.setText(cDate[0] + "年" + cDate[1] + "月");
        chooseDate.setText("今天的日期：" + cDate[0] + "年" + cDate[1] + "月" + cDate[2] + "日");

        calendarView.setOnPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                if (date == null) {
                    Log.e(TAG, "onPagerChanged data is null? ");
                    return;
                }
                calendar_title.setText(date[0] + "年" + date[1] + "月");
                Log.e(TAG, "onPagerChanged flag " + flag);
            }
        });

        calendarView.setOnSingleChooseListener(new OnSingleChooseListener() {
            @Override
            public void onSingleChoose(View view, DateBean date) {
                calendar_title.setText(date.getSolar()[0] + "年" + date.getSolar()[1] + "月");
                if (date.getType() == 1) {
                    chooseDate.setText("今天的日期：" + date.getSolar()[0] + "年" + date.getSolar()[1] + "月" + date.getSolar()[2] + "日");
                }
            }
        });
        if (NetStateUtil.isConnected(context)) {
            getCalendar(cDate);
        } else {
            showToast(R.string.please_check_network);
        }
    }

    private void getCalendar(int[] curDate) {
        if (curDate[1] < 10) {
            curTime = curDate[0] + "0" + curDate[1];
        } else {
            curTime = curDate[0] + "" + curDate[1];
        }
        Log.e(TAG, "getCalendar curTime " + curTime);
        getVioceApi.getCalendar(AccountManager.getInstance().getUserId(), Constant.APPID, curTime).enqueue(new Callback<VoicesResult>() {
            @Override
            public void onResponse(Call<VoicesResult> call, Response<VoicesResult> response) {
                dismissLoadingLayout();
                if (response == null || (response.body() == null)) {
                    Log.e(TAG, "getRankData onResponse is null.");
                    runOnUiThread(() -> showRankings(new ArrayList<>()));
                    return;
                }
                if("200".equals(response.body().result)) {
                    Log.e(TAG, "getRanking response.count " + response.body().count);
                    List<ShareInfoRecord> rankingList = response.body().record;
                    if (rankingList == null || rankingList.size() < 1) {
                        Log.e(TAG, "getRanking onNext empty.");
                    } else {
                        runOnUiThread(() -> showRankings(rankingList));
                        return;
                    }
                } else {
                    Log.e(TAG, "getRanking onNext empty.");
                }
                runOnUiThread(() -> showRankings(new ArrayList<>()));
            }

            @Override
            public void onFailure(Call<VoicesResult> call, Throwable t) {
                if (t != null) {
                    Log.e(TAG, "getMoreRanking onError " + t.getMessage());
                }
                dismissLoadingLayout();
                runOnUiThread(() -> showRankings(new ArrayList<>()));
            }
        });
    }

    public void showRankings(List<ShareInfoRecord> ranking) {
        HashMap<String, String> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        for (ShareInfoRecord record: ranking) {
            Log.e(TAG, "showCalendar record.createtime: " + record.createtime);
            try {
                Date date = sdf.parse(record.createtime);
//                Log.e(TAG, "showCalendar date: " + date);
//                Log.e(TAG, "showCalendar getYear: " + date.getYear());
                String item = (date.getYear()+1900) + "." + (date.getMonth() + 1) + "." + date.getDate();
                Log.e(TAG, "showCalendar item: " + item);
                if (record.scan > 0) {
                    map.put(item, "" + record.scan);
                } else {
                    map.put(item, "true");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        calendarView.setClockInStatus(map);
        if (flag > 0) {
            calendarView.nextMonth();
        } else if (flag < 0) {
            calendarView.lastMonth();
        } else {
            calendarView
                    .setStartEndDate("2016.1", "2028.12")
                    .setDisableStartEndDate("2016.10.10", "2028.10.10")
                    .setInitDate(cDate[0] + "." + cDate[1])
                    .setSingleDate(cDate[0] + "." + cDate[1] + "." + cDate[2])
                    .init();
        }
    }

    public void lastMonth(View view) {
//        calendarView.lastMonth();
        if (NetStateUtil.isConnected(context)) {
            flag = -1;
            showLoadingLayout();
            if (cDate[1] > 0) {
                cDate[1]--;
            } else {
                cDate[0]--;
                cDate[1]=12;
            }
            getCalendar(cDate);
        } else {
            showToast(R.string.please_check_network);
        }
    }

    public void nextMonth(View view) {
//        calendarView.nextMonth();
        if (NetStateUtil.isConnected(context)) {
            flag = 1;
            showLoadingLayout();
            if (cDate[1] < 12) {
                cDate[1]++;
            } else {
                cDate[0]++;
                cDate[1]=1;
            }
            getCalendar(cDate);
        } else {
            showToast(R.string.please_check_network);
        }
    }

    public void showLoadingLayout() {
        CommonProgressDialog.showProgressDialog(context);
    }

    public void dismissLoadingLayout() {
        CommonProgressDialog.dismissProgressDialog();
    }

    public void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

}
