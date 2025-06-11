package com.iyuba.music.dubbing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.music.R;
import com.iyuba.music.activity.vip.NewVipCenterActivity;
import com.iyuba.music.data.model.TalkLesson;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.manager.AccountManager;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/11/29 0029.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendHolder> {

    private VoaCallback mVoaCallback;
    private ArrayList<MusicVoa> mVoaList;
    private ArrayList<MusicVoa> mVoaListAll;

    public RecommendAdapter() {
        mVoaListAll = new ArrayList<>();
        this.mVoaList = new ArrayList<>();
    }

    @Override
    public RecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voa, parent, false);
        return new RecommendHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommendHolder holder, int position) {

        try {
            MusicVoa voa = mVoaList.get(position);
            Glide.with(holder.itemView.getContext())
                    .load(voa.pic)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(holder.pic);

            if (voa.title != null) {
                holder.titleCn.setText(voa.title);
            }
            holder.readCount.setText("" + voa.readCount);
            holder.setListener(voa);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mVoaList.size();
    }

    public void setVoaCallback(VoaCallback mVoaCallback) {
        this.mVoaCallback = mVoaCallback;
    }

    public void setVoaList(ArrayList<MusicVoa> mVoaList, int voaId) {
        mVoaListAll = mVoaList;
        this.mVoaList.clear();
        for (MusicVoa talkLesson : mVoaList) {
            if (talkLesson.voaId != voaId) {
                this.mVoaList.add(talkLesson);
            }
        }
    }

    class RecommendHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleCn;
        TextView readCount;

        RecommendHolder(View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.pic);
            titleCn = itemView.findViewById(R.id.titleCn);
            readCount = itemView.findViewById(R.id.readCount);
        }

        public void setListener(final MusicVoa voa) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if ((AccountManager.getInstance().getUserInfo() == null) || "0".equals(AccountManager.getInstance().getUserInfo().getVipStatus())) {
                        new AlertDialog.Builder(itemView.getContext())
                                .setTitle("提示")
                                .setMessage("开通会员可以体验全部配音课程")
                                .setNeutralButton("立即开通", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        itemView.getContext().startActivity(new Intent(itemView.getContext(),
                                                NewVipCenterActivity.class));
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else {

                        Intent intent = new Intent();
                        intent.setClass(v.getContext(), LessonPlayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra(LessonPlayActivity.VOA, voa);
                        intent.putExtra(LessonPlayActivity.VOA_LIST, mVoaListAll);
                        v.getContext().startActivity(intent);

                    /*    itemView.getContext().startActivity(LessonPlayActivity.buildIntent(itemView.getContext(),
                                voa, mVoaListAll));//给的应该是全的*/
                    }
                }
            });
        }
    }

    interface VoaCallback {
        void onVoaClicked(TalkLesson voa);
    }
}
