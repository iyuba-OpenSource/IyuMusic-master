package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.imooclib.ImoocManager;
import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.me.FriendCenter;
import com.iyuba.music.adapter.discover.DiscoverAdapter;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.mainpanel.Discover;
import com.iyuba.music.file.FileBrowserActivity;
import com.iyuba.music.ground.AppGroundActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.dialog.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 侧边栏 - 发现 -
 */
public class DiscoverActivity extends BaseActivity {
    private DiscoverAdapter discoverAdapter;

    RecyclerView discover;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        discover = (RecyclerView) findViewById(R.id.discover_list);
        discover.setLayoutManager(new LinearLayoutManager(context));
        discoverAdapter = new DiscoverAdapter(context);
        discover.setAdapter(discoverAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        discoverAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Discover discover = discoverAdapter.getItem(position);
                if (discover != null) {

                    int textID = discover.getText();
                    String nameStr = getString(textID);
                    if (nameStr.equals("学一学")) {

                        ImoocManager.appId = ConstantManager.appId;
                        ArrayList<Integer> list = new ArrayList<>();

                        list.add(-2); //全部课程
                        list.add(-1); //最新课程
                        list.add(2); //英语四级
                        list.add(3); //VOA英语
                        list.add(4); //英语六级
                        list.add(7); //托福TOEFL
                        list.add(8); //考研英语一
                        list.add(9); //BBC英语
                        list.add(21); //新概念英语
                        list.add(22); //走遍美国
                        list.add(28); //学位英语
                        list.add(52); //考研英语二
                        list.add(52); //雅思
                        list.add(91); //中职英语
                        if (AccountManager.getInstance().checkUserLogin()) {
                            AccountManager.getInstance().setUser();
                        }
                        MobClassActivity.buildIntent(context, -2, true, list);
                        startActivity(new Intent(context, MobClassActivity.class));
                    } else if (nameStr.equals("视频")) {

                        startActivity(new Intent(context, VideoActivity.class));
                    } else if (nameStr.equals("爱语吧")) {

                        if (Constant.APP_NAME_PRIVACY.equalsIgnoreCase("隐私政策")) {
                            Toast.makeText(context, R.string.tips_company, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(new Intent(context, AppGroundActivity.class));
                    } else if (nameStr.equals("查生词")) {

                        startActivity(new Intent(context, WordSearchActivity.class));
                    } else if (nameStr.equals("经典谚语")) {

                        startActivity(new Intent(context, SayingActivity.class));
                    } else if (nameStr.equals("单词本")) {

                        startActivity(new Intent(context, WordListActivity.class));
                    } else if (nameStr.equals("文件管理")) {

                        startActivity(new Intent(context, FileBrowserActivity.class));
                    }
                }


/*                switch (position - 1) {
                    case 2:
                        if (Constant.APP_NAME_PRIVACY.equalsIgnoreCase("隐私政策")) {
                            Toast.makeText(context, R.string.tips_company, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(new Intent(context, AppGroundActivity.class));
                        break;

                    case 5:
                        startActivity(new Intent(context, WordSearchActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(context, SayingActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(context, WordListActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(context, FileBrowserActivity.class));
                        break;
             *//*       case 9:
                        startActivity(new Intent(context, LocalMusicActivity.class));
                        break;*//*
                    case 0:
                        //以前的彩蛋模式
//                        if (ConfigManager.getInstance().isEggShell()) {
//                            startActivity(new Intent(context, EggShellActivity.class));
//                        }
                        //学一学

                        ImoocManager.appId = ConstantManager.appId;
                        ArrayList<Integer> list = new ArrayList<>();

                        list.add(-2); //全部课程
                        list.add(-1); //最新课程
                        list.add(2); //英语四级
                        list.add(3); //VOA英语
                        list.add(4); //英语六级
                        list.add(7); //托福TOEFL
                        list.add(8); //考研英语一
                        list.add(9); //BBC英语
                        list.add(21); //新概念英语
                        list.add(22); //走遍美国
                        list.add(28); //学位英语
                        list.add(52); //考研英语二
                        list.add(52); //雅思
                        list.add(91); //中职英语
                        if (AccountManager.getInstance().checkUserLogin()) {
                            AccountManager.getInstance().setUser();
                        }
                        MobClassActivity.buildIntent(context, -2, true, list);
                        startActivity(new Intent(context, MobClassActivity.class));
                        break;
                    case 1:
                        //视频
                        startActivity(new Intent(context, VideoActivity.class));
                        break;
                    case 10:
                        //礼物
//                        http://vip." + ConstantManager.IYUBA_CN + "mycode.jsp?uid=5190895&appid=148&sign=c6781d62dd0107b4f472c320621063a4&username=20081818
                        if (AccountManager.getInstance().checkUserLogin()) {

                            Intent intent2 = new Intent();
                            intent2.setClass(context, WebViewActivity.class);
                            intent2.putExtra("url", "http://vip." + ConstantManager.IYUBA_CN + "mycode.jsp?"
                                    + "uid=" + AccountManager.getInstance().getUserId()
                                    + "&appid=" + ConstantManager.appId
                                    + "&sign=" + MD5.getMD5ofStr(AccountManager.getInstance().getUserId() + "iyuba" + ConstantManager.appId + date)
                                    + "username=" + TextAttr.encode(AccountManager.getInstance().getUserInfo().getUsername()));

                            intent2.putExtra("title",
                                    context.getString(R.string.oper_gift));
                            startActivity(intent2);
                        } else {

                            Intent intent2 = new Intent();
                            intent2.setClass(context, LoginActivity.class);
                            startActivity(intent2);
                        }
                        break;
                }*/
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_discover);
    }
}
