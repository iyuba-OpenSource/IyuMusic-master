package com.iyuba.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.adapter.PopMusicAdapter;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.event.ChangePlayMode;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.MusicListPop;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class MusicPopFragment extends Fragment {


    private RecyclerView recyclerView;
    private PopMusicAdapter popMusicAdapter;
    private TextView tv_songnum, tv_style;
    private ImageView image_style_play, image_delete_all;


    private List<Article> list = new ArrayList<>();
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;

    private Context context;

    private String type;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    tv_songnum.setText("(" + msg.arg1 + "首)");
                    break;
                case 2:
                    tv_songnum.setText("(" + msg.arg1 + "首)");
                    break;
                case 3:

                    changeAdapter();
                    break;

                case 4:
                    tv_songnum.setText("(" + msg.arg1 + "首)");
                    break;
                case 5:
//                    viewPager.setCurrentItem(0);
//                    changeAdapter();
                    break;
                default:
                    break;
            }
        }
    };


    public static MusicPopFragment newInstance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);

        MusicPopFragment musicPopFragment = new MusicPopFragment();
        musicPopFragment.setArguments(bundle);
        return musicPopFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_adapter_item_first, null);


        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();

        initView(view);

        return view;
    }


    private void initView(View view) {


        image_delete_all = view.findViewById(R.id.image_delete_all);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration());


        popMusicAdapter = new PopMusicAdapter(list, context, handler);
        recyclerView.setAdapter(popMusicAdapter);


        type = getArguments().getString("type");


        tv_songnum = view.findViewById(R.id.text_songnum);


        image_delete_all.setVisibility(View.INVISIBLE);


        tv_style = view.findViewById(R.id.text_style);
        image_style_play = view.findViewById(R.id.image_style);


        if (type.equals(MusicListPop.FAVOR_TYPE)) {

            tv_style.setText("收藏列表");
            getDataFavor();

            image_style_play.setVisibility(View.GONE);
        } else if (type.equals(MusicListPop.LISTEN_TYPE)) {
            tv_style.setText("最近播放");
            getDataLast();
            image_style_play.setVisibility(View.GONE);
        } else {
            tv_style.setText("试听列表");
            getDataOut();
            image_style_play.setVisibility(View.VISIBLE);


            image_style_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeStyle();
                    EventBus.getDefault().post(new ChangePlayMode());
                }
            });
            //切换播放模式
            tv_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeStyle();
                    EventBus.getDefault().post(new ChangePlayMode());
                }
            });
        }

        tv_songnum.setText("(" + list.size() + "首)");
    }


    private void changeStyle() {
        int nextMusicType = ConfigManager.getInstance().getStudyPlayMode();
        nextMusicType = (nextMusicType + 1) % 3;
        ConfigManager.getInstance().setStudyPlayMode(nextMusicType);
        StudyManager.getInstance().generateArticleList();

        setStyle(nextMusicType);
    }


    private void setStyle(int nextMusicType) {
        switch (nextMusicType) {
            case 0:
                image_style_play.setImageResource(R.drawable.single_replay);
                tv_songnum.setVisibility(View.GONE);
                break;
            case 1:
                image_style_play.setImageResource(R.drawable.list_play);
                tv_songnum.setVisibility(View.VISIBLE);
                break;
            case 2:
                image_style_play.setImageResource(R.drawable.random_play);
                tv_songnum.setVisibility(View.VISIBLE);
                break;


        }
    }


    /**
     * 收藏数据
     */
    private void getDataFavor() {

        list.clear();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByFavourite();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getFavTime());
            list.add(article);
        }
        popMusicAdapter.setType(type);
        popMusicAdapter.notifyDataSetChanged();
    }


    /**
     * 最近播放
     */
    private void getDataLast() {


        list.clear();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByListen();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getSeeTime());
            list.add(article);
        }
        popMusicAdapter.setType(type);
        popMusicAdapter.notifyDataSetChanged();

    }


    public void getDataOut() {
        list.clear();
        list = StudyManager.getInstance().getCurArticleList();
        type = StudyManager.getInstance().getListFragmentPos();
        popMusicAdapter.setType(type);
        popMusicAdapter.notifyDataSetChanged();
    }


    public void changeAdapter() {

        popMusicAdapter.notifyDataSetChanged();


    }

}
