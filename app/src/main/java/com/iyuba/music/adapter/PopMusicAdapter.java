package com.iyuba.music.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.event.MainFragmentEvent;
import com.iyuba.music.event.StudyEvent;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.FavorRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.MusicListPop;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by 10202 on 2015/10/10.
 */
public class PopMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> list;
    private Context context;

    private String type;
    private Handler handler;

    private boolean flag = false;

    public PopMusicAdapter(List<Article> list, Context context, Handler handler) {
        this.list = list;
        this.context = context;
        this.handler = handler;
    }


    public PopMusicAdapter(List<Article> list, Context context, Handler handler, boolean flag) {
        this.list = list;
        this.context = context;
        this.handler = handler;
        this.flag = flag;
    }

    public void setType(String type) {

        this.type = type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new OperViewHolder(LayoutInflater.from(context).inflate(R.layout.pop_adapter_item, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Article article = list.get(position);
        OperViewHolder operViewHolder = (OperViewHolder) holder;
        operViewHolder.setArticle(article);
        if (flag) {

            if (article.getId() == StudyManager.getInstance().getCurArticle().getId() &&
                    type.equals(StudyManager.getInstance().getListFragmentPos())) {
                operViewHolder.songname.setTextColor(context.getResources().getColor(com.buaa.ct.imageselector.R.color.green_70));
                operViewHolder.singer.setTextColor(context.getResources().getColor(com.buaa.ct.imageselector.R.color.green_70));

            } else {
                operViewHolder.songname.setTextColor(Color.parseColor("#333333"));
                operViewHolder.singer.setTextColor(Color.parseColor("#999999"));
            }
        }

        operViewHolder.songname.setText(article.getTitle());
        operViewHolder.singer.setText("- " + article.getSinger());
    }


    @Override
    public int getItemCount() {

        return list.size();
    }

    public List<Article> getList() {
        return list;
    }

    public void setList(List<Article> list) {
        this.list = list;
    }

    public class OperViewHolder extends RecyclerView.ViewHolder {

        //通用
        TextView songname, singer;
        ImageView delImage;
        MaterialRippleLayout root;

        Article article;

        public OperViewHolder(View itemView) {
            super(itemView);
            songname = itemView.findViewById(R.id.songname);
            singer = itemView.findViewById(R.id.singer);
            delImage = itemView.findViewById(R.id.delimage);
            root = itemView.findViewById(R.id.root);


            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (delImage.getContext().getClass().getName().toString().contains("StudyActivity")) {
                        EventBus.getDefault().post(new StudyEvent(list, getBindingAdapterPosition(), type));
                    } else {
                        EventBus.getDefault().post(new MainFragmentEvent(list, getBindingAdapterPosition(), type));

                    }
                }
            });

            delImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (article.getId() == StudyManager.getInstance().getCurArticle().getId() &&
                            type.equals(StudyManager.getInstance().getListFragmentPos())) {

                        return;
                    }

                    if (flag) {

                        list.remove(getBindingAdapterPosition());
                        StudyManager.getInstance().getCurArticleList().remove(getBindingAdapterPosition());
                        notifyDataSetChanged();


                        Message message = new Message();
                        message.arg1 = list.size();
                        message.what = 4;
                        handler.sendMessage(message);

                    } else {
                        if (type.equals(MusicListPop.FAVOR_TYPE)) {
                            delFavorOne(getBindingAdapterPosition());

                        } else if (type.equals(MusicListPop.LISTEN_TYPE)) {
                            delListenOne(getBindingAdapterPosition());

                        }
                    }
                }
            });
        }

        public Article getArticle() {
            return article;
        }

        public void setArticle(Article article) {
            this.article = article;
        }
    }


    /**
     * 删除最近听过一首
     */
    private void delListenOne(int position) {

        LocalInfoOp localInfoOp = new LocalInfoOp();
        localInfoOp.deleteSee(list.get(position).getId(), list.get(position).getApp());
        list.remove(position);
        notifyDataSetChanged();

        Message message = new Message();
        message.arg1 = list.size();
        message.what = 1;
        handler.sendMessage(message);
    }


    /**
     * 删除收藏中的一首
     */
    private void delFavorOne(final int position) {
        final LocalInfoOp localInfoOp = new LocalInfoOp();
        final String app = StudyManager.getInstance().getApp();
        if (localInfoOp.findDataById(app, list.get(position).getId()).getFavourite() == 1) {
            FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), list.get(position).getId(), "del"), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {

                }

                @Override
                public void onServerError(String msg) {

                }

                @Override
                public void response(Object object) {
                    if (object.toString().equals("del")) {
                        localInfoOp.updateFavor(list.get(position).getId(), app, 0);
                        CustomToast.getInstance().showToast(R.string.article_favor_cancel);

                        list.remove(position);
                        notifyDataSetChanged();


                        Message message = new Message();
                        message.arg1 = list.size();
                        message.what = 2;
                        handler.sendMessage(message);

                    }
                }
            });
        }
    }


}
