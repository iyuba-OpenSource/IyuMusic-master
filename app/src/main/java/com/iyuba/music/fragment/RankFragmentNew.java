package com.iyuba.music.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.iyuba.music.R;
import com.iyuba.music.adapter.RankListAdapter;
import com.iyuba.music.entity.RankBean;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.merequest.RankListenRequest;
import com.iyuba.music.request.merequest.RankReadRequest;
import com.iyuba.music.request.merequest.RankSpeakRequest;
import com.iyuba.music.request.merequest.RankStudyRequest;
import com.iyuba.music.request.merequest.RankTestRequest;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.music.widget.CircleImageView;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 排行榜
 */
public class RankFragmentNew extends BaseRankFragment {
    private static final String TAG = RankFragmentNew.class.getSimpleName();
    private Context mContext;
    private String uid;
    private String type;
    private String total;
    private String start;


    private String myImgSrc = "";
    private String myId = "";
    private String myRanking = "";
    private String myName = "";

    private String result = "";
    private String message = "";

    //阅读
    private String myWords = "";//文章数
    private String myWpm = "";
    private String myCnt = "";//单词数

    //听力和学习
    private String myTotalTime = "";
    private String myTotalWord = "";
    private String myTotalEssay = "";

    //口语
    private String myScores = "";//总分
    private String myCount = "";//总句子数

    //测试
    private String myTotalRight;//正确数
    private String myTotalTest;  //总题数

    private List<RankBean> rankBeans = new ArrayList<RankBean>();
    private RankBean champion; //第一名的详细数据

//    private RankAdapter rankListAdapter;


    private LayoutInflater inflater;
    private boolean scorllable = true;
    private static boolean isNight = false;


    private Pattern p;
    private Matcher m;

    TextView note;
    CircleImageView userImage;
    TextView userImageText;
    TextView userName;
    TextView myUsername;
    TextView userInfo;
    ListView rankListView;
    CircleImageView myImage;

    private View listFooter;

    private static final String RANKTYPE = "rank_type";
    private int dialog_position = 0;
    private String rankType;

    public String frag_text, frag_siteUrl;

    private View parent;

    private boolean isPrepared;
    private boolean mHasLoadedOnce;
    private IyubaDialog waitDialog;

    private RankListAdapter adapter;

    public static RankFragmentNew newInstance(String rank_type) {
        Bundle bundle = new Bundle();
        bundle.putString(RANKTYPE, rank_type);
        RankFragmentNew fragment = new RankFragmentNew();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (parent == null) {

            isPrepared = true;
            parent = inflater.inflate(R.layout.fragment_rank, null);

//            mContext = getActivity();
            note = parent.findViewById(R.id.rank_note);
            userImage = parent.findViewById(R.id.rank_user_image);
            userImageText = parent.findViewById(R.id.rank_user_image_text);
            userName = parent.findViewById(R.id.rank_user_name);
            myUsername = parent.findViewById(R.id.username);
            userInfo = parent.findViewById(R.id.rank_info);
            myImage = parent.findViewById(R.id.my_image);
            rankListView = parent.findViewById(R.id.rank_list);


            waitDialog = WaitingDialog.create(getActivity(), null);

            rankType = getArguments().getString(RANKTYPE);

            Log.e("RANKTYPE", rankType);
            note.setText("今天");


            uid = AccountManager.getInstance().getUserId() == null ? "0" : AccountManager.getInstance().getUserId();
            type = "D";
            total = "30";
            start = "0";
            listFooter = inflater.inflate(R.layout.comment_footer, null);
            rankListView.addFooterView(listFooter);
            lazyload();


        }

        //选择今天，本周，本月
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseHeartType(note.getText().toString());

            }
        });
        rankListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            // 当comment不为空且comment.size()不为0且没有完全加载
                            if (scorllable) mHanlder.sendEmptyMessage(0);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        return parent;
    }

    @Override
    protected void lazyload() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        mHanlder.sendEmptyMessage(0);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * 提供给用户选择动态类型的单选列表对话框
     */
    private void choseHeartType(String choose_type) {
        final String typeArray[] = new String[]{"今天", "本周"};

        for (int i = 0; i < typeArray.length; i++) {

            if (choose_type.equals(typeArray[i])) {

                dialog_position = i;
            }
        }

        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setSingleChoiceItems(typeArray,  //装载数组信息
                //默认选中选项
                dialog_position,
                //为列表添加监听事件
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (dialog_position != 0) {
                                    rankBeans.clear();
                                    type = "D";
                                    note.setText(typeArray[0]);
                                    mHanlder.sendEmptyMessage(0);
                                }
                                break;
                            case 1:
                                if (dialog_position != 1) {
                                    rankBeans.clear();
                                    type = "W";
                                    note.setText(typeArray[1]);
                                    mHanlder.sendEmptyMessage(0);
                                }
                                break;
                            case 2:
                                if (dialog_position != 2) {
                                    rankBeans.clear();
                                    type = "M";
                                    note.setText(typeArray[2]);
                                    mHanlder.sendEmptyMessage(0);
                                }
                                break;


                        }

                        dialog.cancel();  //用户选择后，关闭对话框
                    }
                }).create().show();


    }


    // 2.1 定义用来与外部activity交互，获取到宿主activity
    private RankFragmentInteraction listterner;


    // 1 定义了所有activity必须实现的接口方法
    public interface RankFragmentInteraction {
        void process(String str, String str2, String str3);
    }

    // 当FRagmen被加载到activity的时候会被回调
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mContext = activity;
        if (activity instanceof RankFragmentInteraction) {
            listterner = (RankFragmentInteraction) activity; // 2.2 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    private Handler mHanlder = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:

                    if (rankBeans.size() == 0) {
                        start = "0";
                    } else {
                        start = String.valueOf(adapter.getCount());
                    }
                    getInfoRead();

                    break;
                case 1:
                    if (!waitDialog.isShowing()) waitDialog.show();
                    break;
                case 2:
                    if (waitDialog.isShowing()) waitDialog.dismiss();
                    break;
                case 3:
                    String firstChar;
                    if (champion.getName() == null || "".equals(champion.getName())) {
                        firstChar = getFirstChar(champion.getUid());
                    } else {
                        firstChar = getFirstChar(champion.getName());
                    }
                    if (champion.getImgSrc().equals("http://static1." + ConstantManager.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                        userImage.setVisibility(View.INVISIBLE);
                        userImageText.setVisibility(View.VISIBLE);
                        p = Pattern.compile("[a-zA-Z]");
                        m = p.matcher(firstChar);
                        if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                            userImageText.setBackgroundResource(R.drawable.rank_blue);
                            userImageText.setText(firstChar);
                            myUsername.setText(myName);
                            if (champion.getName() == null || "".equals(champion.getName())) {
                                userName.setText(champion.getUid());
                            } else {
                                userName.setText(champion.getName());
                            }
                            setInfo();
                            if ((getActivity() != null) && (!getActivity().isDestroyed())) {
                                Glide.with(mContext).load(myImgSrc).placeholder(R.drawable.defaultavatar).into(myImage);
                            }
                            mHasLoadedOnce = true;
                        } else {

                            userImageText.setBackgroundResource(R.drawable.rank_green);
                            userImageText.setText(firstChar);
                            myUsername.setText(myName);
                            if (champion.getName() == null || "".equals(champion.getName())) {
                                userName.setText(champion.getUid());
                            } else userName.setText(champion.getName());
                            setInfo();
                            Glide.with(mContext).load(myImgSrc).placeholder(R.drawable.defaultavatar).into(myImage);
//                            GitHubImageLoader.Instace(mContext).setRawPic(myImgSrc, myImage,
//                                    R.drawable.defaultavatar);
                            mHasLoadedOnce = true;
                        }
                    } else {
                        userImageText.setVisibility(View.INVISIBLE);
                        userImage.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(myImgSrc).placeholder(R.drawable.noavatar_small).into(myImage);
//                        GitHubImageLoader.Instace(mContext).setRawPic(champion.getImgSrc(), userImage,
//                                R.drawable.noavatar_small);
                        if (champion.getName() == null || "".equals(champion.getName())) {
                            userName.setText(champion.getUid());
                        } else userName.setText(champion.getName());
                        myUsername.setText(myName);
                        setInfo();
                        Glide.with(mContext).load(myImgSrc).placeholder(R.drawable.defaultavatar).into(myImage);
                       /* GitHubImageLoader.Instace(mContext).setRawPic(myImgSrc, myImage,
                                R.drawable.defaultavatar);*/

                        mHasLoadedOnce = true;
                    }
                    break;


                case 4:
                    listFooter.setVisibility(View.GONE);
                    break;

                case 5:
                    ToastUtil.showToast(mContext, "与服务器连接异常，请稍后再试");
                    break;

                case 6:
                    listFooter.setVisibility(View.VISIBLE);
                    break;
                case 7:

                    if (adapter == null) {
                        adapter = new RankListAdapter(mContext, rankBeans, rankType);
                        rankListView.setAdapter(adapter);
                    } else if (champion.getRanking().equals("1")) {
                        adapter.resetList(rankBeans);
                        rankListView.setSelection(0);
                    } else {

                        adapter.addList(rankBeans);
                    }
                    break;

                case 8:
                    ToastUtil.showToast(getActivity(), "暂无更多数据");
                    break;

            }
        }
    };

    private void getInfoRead() {
        switch (rankType) {

            case "阅读":
                mHanlder.sendEmptyMessage(1);

                new RankReadRequest().exeRequest(RankReadRequest.generateUrl(uid, type, start, total), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {
                        {
                            mHanlder.sendEmptyMessage(2);
                            Log.e("RankActivity", "rankrank");
                            RankReadRequest response = (RankReadRequest) object;
                            myWpm = response.myWpm;
                            myImgSrc = response.myImgSrc;
                            myId = response.myId;
                            myRanking = response.myRanking;
                            myCnt = response.myCnt;
                            result = response.result;
                            message = response.message;
                            myName = response.myName;

                            if (myName == null || "".equals(myName)) myName = uid;
                            myWords = response.myWords;
                            rankBeans = response.rankBeans;


                            if (rankBeans.size() < 30) {
                                mHanlder.sendEmptyMessage(4);
                                scorllable = false;
                            } else {
                                mHanlder.sendEmptyMessage(6);
                            }
                            mHanlder.sendEmptyMessage(7);


                            champion = rankBeans.get(0);
                            if (champion.getRanking().equals("1")) {
                                mHanlder.sendEmptyMessage(3);
                            }

                            String r_frag_text = "我在" + ConstantManager.appName + " 阅读排行榜排名：" + myRanking;
                            String r_frag_siteUrl = "http://m." + ConstantManager.IYUBA_CN + "i/getRanking.jsp?uid=" + uid + "&appId=" + ConstantManager.appId + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + ConstantManager.appId) + "&topic=music&rankingType=reading";
                            listterner.process(rankType, r_frag_text, r_frag_siteUrl);
                        }
                    }
                });
                break;
            case "听力":
                mHanlder.sendEmptyMessage(1);
                String mode = "listening";

                new RankListenRequest().exeRequest(RankListenRequest.generateUrl(uid, type, start, total, mode), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {

                        //处理数据
                        RankListenRequest response = (RankListenRequest) object;
                        mHanlder.sendEmptyMessage(2);
                        myImgSrc = response.myImgSrc;
                        myId = response.myId;
                        myRanking = response.myRanking;
                        result = response.result;
                        message = response.message;
                        myName = response.myName;
                        if (myName == null || "".endsWith(myName)) myName = uid;

                        rankBeans = response.rankBeans;
                        champion = rankBeans.get(0);

                        myTotalTime = response.myTotalTime;
                        myTotalWord = response.myTotalWord;
                        myTotalEssay = response.myTotalEssay;

                        if (rankBeans.size() < 30) {
                            mHanlder.sendEmptyMessage(4);
                            scorllable = false;
                        } else {
                            mHanlder.sendEmptyMessage(6);
                        }
                        mHanlder.sendEmptyMessage(7);

                        if (champion.getRanking().equals("1")) {
                            mHanlder.sendEmptyMessage(3);
                        }


                        String l_frag_text = "我在" + ConstantManager.appName + " 听力排行榜排名：" + myRanking;
                        String l_frag_siteUrl = "http://m." + ConstantManager.IYUBA_CN + "i/getRanking.jsp?uid=" + uid + "&appId=" + ConstantManager.appId + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + ConstantManager.appId) + "&topic=music&rankingType=listening";
                        listterner.process(rankType, l_frag_text, l_frag_siteUrl);
                    }

                });
                break;
            case "口语":
                mHanlder.sendEmptyMessage(1);
                String topic = "music", topicid = "0", shuoshuotype = "2";


                new RankSpeakRequest().exeRequest(RankSpeakRequest.generateUrl(uid, type, start, total, topic, topicid, shuoshuotype), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {

                        //处理数据
                        RankSpeakRequest response = (RankSpeakRequest) object;
                        mHanlder.sendEmptyMessage(2);
                        myImgSrc = response.myImgSrc;
                        myId = response.myId;
                        myRanking = response.myRanking;
                        result = response.result;
                        message = response.message;
                        myName = response.myName;
                        if (myName == null || "".endsWith(myName)) myName = uid;

                        rankBeans = response.rankBeans;
                        if (rankBeans.size() == 0 || rankBeans == null) {
                            mHanlder.sendEmptyMessage(8);
                        }
                        champion = rankBeans.get(0);


                        myScores = response.myScores;//总分
                        myCount = response.myCount;//总文章数

                        if (rankBeans.size() < 30) {
                            mHanlder.sendEmptyMessage(4);
                            scorllable = false;
                        } else {
                            mHanlder.sendEmptyMessage(6);
                        }
                        mHanlder.sendEmptyMessage(7);

                        if (champion.getRanking().equals("1")) {
                            mHanlder.sendEmptyMessage(3);
                        }

                        String k_frag_text = "我在" + ConstantManager.appName + " 口语排行榜排名：" + myRanking;
                        String k_frag_siteUrl = "http://m." + ConstantManager.IYUBA_CN + "i/getRanking.jsp?uid=" + uid + "&appId=" + ConstantManager.appId + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + ConstantManager.appId) + "&topic=music&rankingType=speaking";
                        listterner.process(rankType, k_frag_text, k_frag_siteUrl);

                    }
                });
                break;
            case "学习":

                mHanlder.sendEmptyMessage(1);
                String modes = "all";
                new RankStudyRequest().exeRequest(RankStudyRequest.generateUrl(uid, type, start, total, modes), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {
                        {
                            //处理数据
                            RankStudyRequest response = (RankStudyRequest) object;
                            mHanlder.sendEmptyMessage(2);
                            myImgSrc = response.myImgSrc;
                            myId = response.myId;
                            myRanking = response.myRanking;
                            result = response.result;
                            message = response.message;
                            myName = response.myName;
                            if (myName == null || "".endsWith(myName)) myName = uid;

                            rankBeans = response.rankBeans;
                            champion = rankBeans.get(0);

                            myTotalTime = response.myTotalTime;
                            myTotalWord = response.myTotalWord;
                            myTotalEssay = response.myTotalEssay;

                            if (rankBeans.size() < 30) {
                                mHanlder.sendEmptyMessage(4);
                                scorllable = false;
                            } else {
                                mHanlder.sendEmptyMessage(6);
                            }
                            mHanlder.sendEmptyMessage(7);

                            if (champion.getRanking().equals("1")) {
                                mHanlder.sendEmptyMessage(3);
                            }

                            String x_frag_text = "我在" + ConstantManager.appName + " 学习排行榜排名：" + myRanking;
                            String x_frag_siteUrl = "http://m." + ConstantManager.IYUBA_CN + "i/getRanking.jsp?uid=" + uid + "&appId=" + ConstantManager.appId + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + ConstantManager.appId) + "&topic=music&rankingType=studying";
                            listterner.process(rankType, x_frag_text, x_frag_siteUrl);
                        }
                    }
                });
                break;
            case "测试":

                mHanlder.sendEmptyMessage(1);
                new RankTestRequest().exeRequest(RankTestRequest.generateUrl(uid, type, start, total), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {

                        //处理数据
                        RankTestRequest response = (RankTestRequest) object;
                        mHanlder.sendEmptyMessage(2);
                        myImgSrc = response.myImgSrc;
                        myId = response.myId;
                        myRanking = response.myRanking;
                        result = response.result;
                        message = response.message;
                        myName = response.myName;
                        if (myName == null || "".endsWith(myName)) myName = uid;

                        rankBeans = response.rankBeans;
                        champion = rankBeans.get(0);


                        myTotalRight = response.myTotalRight;//正确数
                        myTotalTest = response.myTotalTest;  //总题数

                        if (rankBeans.size() < 30) {
                            mHanlder.sendEmptyMessage(4);
                            scorllable = false;
                        } else {
                            mHanlder.sendEmptyMessage(6);
                        }
                        mHanlder.sendEmptyMessage(7);

                        if (champion.getRanking().equals("1")) {
                            mHanlder.sendEmptyMessage(3);
                        }

                        frag_text = "我在" + ConstantManager.appId + " 测试排行榜排名：" + myRanking;
                        frag_siteUrl = "http://m." + ConstantManager.IYUBA_CN + "i/getRanking.jsp?uid=" + uid + "&appId=" + ConstantManager.appId + "&sign=" + MD5.getMD5ofStr(uid + "ranking" + ConstantManager.appId) + "&topic=music&rankingType=testing";
                        listterner.process(rankType, frag_text, frag_siteUrl);
                    }

                });
                break;
        }

    }


    private void setInfo() {

        switch (rankType) {
            case "阅读":
                userInfo.setText("单词数: " + myWords + "，文章数: " + myCnt + "，wpm: " + myWpm + "，排名: " + myRanking);
                break;
            case "听力":
                userInfo.setText(myTotalTime + "分钟，文章数: " + myTotalEssay + "，单词数: " + myTotalWord + "，排名: " + myRanking);
                break;
            case "口语":
                double scores_avg = 0;
                if (myCount.equals("0")) {
                    scores_avg = 0;
                } else {

                    scores_avg = Double.valueOf(myScores) / Double.valueOf(myCount);
                }

                DecimalFormat df = new DecimalFormat("0.00");
                userInfo.setText("句子数: " + myCount + "，总分: " + myScores + "，平均分: " + df.format(scores_avg) + "，排名: " + myRanking);
                break;
            case "学习":

                double hour = Double.valueOf(myTotalTime) / 3600;

                DecimalFormat df1 = new DecimalFormat("0.00");

                userInfo.setText(df1.format(hour) + "小时，文章数: " + myTotalEssay + "，单词数: " + myTotalWord + "，排名: " + myRanking);
                break;
            case "测试":
                double min;
                if (Double.valueOf(myTotalTest) == 0) {
                    userInfo.setText("总题数: " + myTotalTest + "，正确数: " + myTotalRight + "，正确率: " + "0，排名: " + myRanking);
                } else {

                    min = Double.valueOf(myTotalRight) / Double.valueOf(myTotalTest);

                    DecimalFormat df2 = new DecimalFormat("0.00");

                    userInfo.setText("总题数:" + myTotalTest + "，正确数:" + myTotalRight + "，正确率:" + df2.format(min) + "，排名:" + myRanking);
                }
                break;

        }

    }

    private String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            p = Pattern.compile("[0-9]*");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }

}
