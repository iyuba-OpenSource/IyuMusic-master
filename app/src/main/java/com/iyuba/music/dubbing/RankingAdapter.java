package com.iyuba.music.dubbing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.music.R;
import com.iyuba.music.data.model.Ranking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingHolder> {

    private List<Ranking> mRankingList;
    private RankingCallback mRankingCallback;
    private Context context;

    public RankingAdapter() {
        mRankingList = new ArrayList<>();
    }

    @Override
    public RankingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_talk, parent, false);
        return new RankingHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingHolder holder, int position) {

        Ranking ranking = mRankingList.get(position);
        setRanking(holder.tRank, position, holder.itemView.getContext());
        Glide.with(holder.itemView.getContext())
                .load(ranking.imgSrc)
                .placeholder(R.drawable.loading)
                .into(holder.iPhoto);
        holder.tThumbs.setText(String.valueOf(ranking.agreeCount));
        holder.tUsername.setText(ranking.userName);
        holder.tTime.setText(ranking.createDate);
        holder.tvScore.setText(Math.round(ranking.score) + "分");
        //不应该进行点赞操作，接口也不好使
//        holder.rlAgree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mRankingCallback!=null){
//                    mRankingCallback.onClickThumbs(ranking.id);
//                }
//            }
//        });
    }

    private void setRanking(TextView tRank, int position, Context context) {
        switch (position) {
            case 0:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_first));
                break;
            case 1:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_second));
                break;
            case 2:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_third));
                break;
            default:
                tRank.setText(String.valueOf(position + 1));
                tRank.setBackground(null);
                break;
        }
    }

    public void setRankingList(List<Ranking> mRankingList) {
        this.mRankingList = mRankingList;
        // Collections.sort(this.mRankingList, new SortByScore());
    }

    public void setRankingCallback(RankingCallback mRankingCallback) {
        this.mRankingCallback = mRankingCallback;
    }

    @Override
    public int getItemCount() {
        return mRankingList.size();
    }

    class RankingHolder extends RecyclerView.ViewHolder {

        TextView tRank;
        CircleImageView iPhoto;
        TextView tUsername;
        TextView tTime;
        TextView tThumbs;
        TextView tvScore;
        RelativeLayout rlAgree;
        ImageView iv_agree;

        RelativeLayout ranking_layout;

        RankingHolder(View itemView) {
            super(itemView);

            tRank = itemView.findViewById(R.id.rank);
            iPhoto = itemView.findViewById(R.id.photo);
            tUsername = itemView.findViewById(R.id.username_tv);
            tTime = itemView.findViewById(R.id.time_tv);
            rlAgree = itemView.findViewById(R.id.rl_agree);
            tThumbs = itemView.findViewById(R.id.thumbs_num);
            iv_agree = itemView.findViewById(R.id.iv_agree);
            tvScore = itemView.findViewById(R.id.tv_score);
            ranking_layout = itemView.findViewById(R.id.ranking_layout);

            ranking_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClickLayout();
                }
            });
        }


        void onClickLayout() {
            Ranking ranking = mRankingList.get(getAdapterPosition());
            mRankingCallback.onClickLayout(ranking, getAdapterPosition());    //根据分数进行排序 2018.7.16
        }

    }

    interface RankingCallback {
        void onClickThumbs(int id);

        void onClickLayout(Ranking ranking, int pos);
    }

    //根据分数从高到低进行排序 modified 2018.7.16
    private class SortByScore implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Ranking s1 = (Ranking) o1;
            Ranking s2 = (Ranking) o2;
            if (s1.score < s2.score) //小的在后面
                return 1;
            return -1;
        }
    }
}
