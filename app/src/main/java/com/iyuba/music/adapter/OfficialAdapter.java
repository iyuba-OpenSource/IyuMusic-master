package com.iyuba.music.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.music.R;
import com.iyuba.music.data.model.OfficialResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl shen on 2021/8/17
 * New English Music, new study experience.
 */
public class OfficialAdapter extends RecyclerView.Adapter<OfficialAdapter.OfficialViewHolder> {
    private List<OfficialResponse.OfficialAccount> mData;
    private OnOfficialClickListener mListener;
    private View binding ;
    private Context context;

    public OfficialAdapter(Context context) {
        this.context = context;
        this.mData = new ArrayList<>();
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = LayoutInflater.from(context).inflate(R.layout.item_official, parent,false);
        return new OfficialViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        OfficialResponse.OfficialAccount collect = mData.get(position);
        if (collect != null) {
            Log.e("OfficialPresenter", "onBindViewHolder image_url " + collect.image_url);
            Glide.with(holder.itemView.getContext())
                    .load(collect.image_url)
                    .centerCrop()
                    .placeholder(R.drawable.default_photo)
                    .into(holder.imageIv);
            holder.nameTv.setText(collect.title);
            holder.fromTv.setText(collect.newsfrom);
            String[] mCreateTime = collect.createTime.split(" ");
            if ((mCreateTime != null) && (mCreateTime.length > 0)) {
                holder.timeTv.setText(mCreateTime[0]);
            } else {
                holder.timeTv.setText(collect.createTime);
            }
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onOfficialClick(collect);
                }
            });
        } else {
            Log.e("OfficialPresenter", "onBindViewHolder OfficialAccount is null? ");
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public void setData(List<OfficialResponse.OfficialAccount> data) {
        if (data == null) {
            mData = new ArrayList<>();
        } else {
            mData = data;
        }
    }

    public void addData(List<OfficialResponse.OfficialAccount> data) {
        if (mData == null) {
            mData = data;
        } else {
            mData.addAll(data);
        }
    }

    public void setListener(OnOfficialClickListener mListener) {
        this.mListener = mListener;
    }

    class OfficialViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageIv;
        private TextView nameTv;
        private TextView fromTv;
        private TextView timeTv;
        public OfficialViewHolder(View itemView) {
            super(itemView);
            imageIv = itemView.findViewById(R.id.image_iv);
            nameTv = itemView.findViewById(R.id.name_tv);
            fromTv = itemView.findViewById(R.id.from_tv);
            timeTv = itemView.findViewById(R.id.time_tv);
        }
    }
}
