package com.iyuba.music.adapter.study;

import android.annotation.TargetApi;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

/**
 * Created by 10202 on 2015/10/10.
 */
public class AnnouncerAdapter extends RecyclerView.Adapter<AnnouncerAdapter.MyViewHolder> {
    private ArrayList<Announcer> announcers;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public AnnouncerAdapter(Context context) {
        this.context = context;
        announcers = new ArrayList<>();
    }

    public void setAnnouncerList(ArrayList<Announcer> announcers) {
        this.announcers = announcers;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_announcer, parent, false));
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
        final Announcer announcer = announcers.get(position);
        holder.name.setText(announcer.getName());
        ImageUtil.loadAvatar(announcer.getUid(), holder.photo);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(21)
            public void onClick(View v) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    SocialManager.getInstance().pushFriendId(announcer.getUid());
                    context.startActivity(PersonalHomeActivity.buildIntent(context, Integer.parseInt(announcer.getUid()), "", 0));

                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            SocialManager.getInstance().pushFriendId(announcer.getUid());

                           context.startActivity(PersonalHomeActivity.buildIntent(context, Integer.parseInt(announcer.getUid()), "", 0));

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcers.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView name;
        CircleImageView photo;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.announcer_username);
            photo = (CircleImageView) view.findViewById(R.id.announcer_photo);
        }
    }
}
