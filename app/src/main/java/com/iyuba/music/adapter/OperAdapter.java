package com.iyuba.music.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.request.account.AuditDateRequest;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.Mathematics;

import java.util.ArrayList;


/**
 * Created by 10202 on 2015/10/10.
 */
public class OperAdapter extends RecyclerView.Adapter<OperAdapter.OperViewHolder> {
    private static final ArrayList<Integer> menuTextList;
    private static final ArrayList<Integer> menuIconList;

    private Context context;

    static {
        menuIconList = new ArrayList<>();
        menuTextList = new ArrayList<>();
    }

    public void setOldTextList() {
        menuIconList.clear();
        menuTextList.clear();

        menuIconList.add(R.drawable.vip_icon);
        menuTextList.add(R.string.oper_vip); //会员中心
        menuIconList.add(R.drawable.ic_app_group);
        menuTextList.add(R.string.oper_group); //官方群
        menuIconList.add(R.drawable.ic_message_center);
        menuTextList.add(R.string.oper_message_center); //消息中心
//        menuTextList.add(R.string.oper_small_video); //消息中心
        menuIconList.add(R.drawable.discover_icon);
        menuTextList.add(R.string.oper_discover);  //发现
//        menuIconList.add(R.drawable.me_icon);
//        menuTextList.add(R.string.oper_myself);

        menuIconList.add(R.drawable.official_accounts_icon);
        menuTextList.add(R.string.oper_wx); //关注公众号
        menuIconList.add(R.drawable.setting_icon);
        menuTextList.add(R.string.oper_setting); //设置
        menuIconList.add(R.drawable.night_icon);
        menuTextList.add(R.string.oper_night); //夜间模式

        notifyDataSetChanged();
        AuditDateRequest.exeRequest(AuditDateRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
            }

            @Override
            public void onServerError(String msg) {
            }

            @Override
            public void response(Object object) {
                String result = (String) object;
                if ("1".equals(result)) {                    //审核中

                } else {
                    if (menuTextList.contains(R.string.oper_discover))
                        return;
                    menuIconList.add(2, R.drawable.discover_icon);
                    menuTextList.add(2, R.string.oper_discover);  //发现
                }
                notifyDataSetChanged();
            }
        });
    }

    public void setNewTextList() {
        menuIconList.clear();
        menuTextList.clear();
        setOldTextList();

//        menuIconList.add(R.drawable.me_name_icon);  积分隐藏
//        menuTextList.add(R.string.chakan_jifen);

        notifyDataSetChanged();

    }

    private OperAdapter.OnItemClickListener mItemClickListener;

    public OperAdapter(Context context) {
        this.context = context;
        setOldTextList();
    }

    public void setItemClickListener(OperAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return menuIconList.size();
    }

    @Override
    public OperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ChangePropery.setAppConfig((Activity) context);

        return new OperViewHolder(LayoutInflater.from(context).inflate(R.layout.item_operlist, parent, false));
    }

    @Override
    public void onBindViewHolder(final OperViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClicked(v, menuTextList.get(holder.getAdapterPosition()));
                }
            }
        });
        holder.menuText.setText(RuntimeManager.getString(menuTextList.get(position)));

        holder.menuIcon.setImageResource(menuIconList.get(position));
        if (holder.menuText.getText().equals(RuntimeManager.getString(R.string.oper_night))) {
            holder.go.setVisibility(View.GONE);
            holder.menuResult.setVisibility(View.VISIBLE);
            holder.menuResult.setText(ConfigManager.getInstance().isNight() ? R.string.oper_night_on : R.string.oper_night_off);
        }
        if (holder.menuText.getText().equals(RuntimeManager.getString(R.string.oper_sleep))) {
            holder.go.setVisibility(View.GONE);
            holder.menuResult.setVisibility(View.VISIBLE);
            int sleepSecond = RuntimeManager.getApplication().getSleepSecond();
            if (sleepSecond == 0) {
                holder.menuResult.setText(R.string.sleep_no_set);
            } else {
                holder.menuResult.setText(Mathematics.formatTime(sleepSecond));
            }
        }
        if (!holder.menuText.getText().equals(RuntimeManager.getString(R.string.oper_night)) &&
                !holder.menuText.getText().equals(RuntimeManager.getString(R.string.oper_sleep))) {
            holder.menuResult.setVisibility(View.GONE);
            holder.go.setVisibility(View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
    }

    class OperViewHolder extends RecyclerView.ViewHolder {

        TextView menuText, menuResult;
        ImageView menuIcon;
        ImageView go;

        OperViewHolder(View itemView) {
            super(itemView);
            menuText = (TextView) itemView.findViewById(R.id.oper_text);
            menuIcon = (ImageView) itemView.findViewById(R.id.oper_icon);
            menuResult = (TextView) itemView.findViewById(R.id.oper_result);
            go = itemView.findViewById(R.id.oper_go);
        }
    }
}
