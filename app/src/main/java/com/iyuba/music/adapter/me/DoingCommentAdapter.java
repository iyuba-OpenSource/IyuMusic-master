package com.iyuba.music.adapter.me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.entity.doings.DoingComment;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.ArrayList;
import java.util.Date;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

/**
 * Created by 10202 on 2015/10/10.
 */
public class DoingCommentAdapter extends RecyclerView.Adapter<DoingCommentAdapter.MyViewHolder> {
    private ArrayList<DoingComment> doings;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public DoingCommentAdapter(Context context) {
        this.context = context;
        doings = new ArrayList<>();
    }

    public void setDoingList(ArrayList<DoingComment> doings) {
        this.doings = doings;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void addData(int position, DoingComment comment) {
        doings.add(position, comment);
        notifyItemInserted(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_doing_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.itemView, pos);
            }
        });
        final DoingComment doing = doings.get(position);
        holder.doingUserName.setText(doing.getUsername());
        holder.doingContent.setText(doing.getMessage());
        holder.doingTime.setText(DateFormat.showTime(context, new Date(Long.parseLong(doing.getDateline()) * 1000)));
        holder.doingPhoto.setVipStateVisible(doing.getUid(), doing.getVip() >= 1);
        holder.doingPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().pushFriendId(doing.getUid());


                context.startActivity(PersonalHomeActivity.buildIntent(context, Integer.parseInt(doing.getUid()), "", 0));

            }
        });
    }

    @Override
    public int getItemCount() {
        return doings.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView doingUserName, doingTime;
        JustifyTextView doingContent;
        VipPhoto doingPhoto;

        public MyViewHolder(View view) {
            super(view);
            doingUserName = (TextView) view.findViewById(R.id.doing_username);
            doingContent = (JustifyTextView) view.findViewById(R.id.doing_content);
            doingTime = (TextView) view.findViewById(R.id.doing_dateline);
            doingPhoto = (VipPhoto) view.findViewById(R.id.doing_photo);
        }
    }
}
