package com.iyuba.music.adapter.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.entity.mainpanel.Discover;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class DiscoverAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_ITEM = 1;
    private static final ArrayList<Discover> discoverList;

    static {
        ArrayList<Integer> menuTextList = new ArrayList<>();
        ArrayList<Integer> menuIconList = new ArrayList<>();

        Discover blank = new Discover();
        blank.setType(TYPE_BLANK);

        menuIconList.add(R.drawable.ic_try_learn);
//        menuIconList.add(R.drawable.ic_try_watch);
//        menuIconList.add(R.drawable.ic_try_search);
        menuIconList.add(R.drawable.ic_main_video_press);

        menuIconList.add(R.drawable.ground_icon);
//        menuIconList.add(R.drawable.circle_icon);
//        menuIconList.add(R.drawable.message_icon);
//        menuIconList.add(R.drawable.friend_icon);
        menuIconList.add(R.drawable.word_search_icon);
        menuIconList.add(R.drawable.word_saying_icon);
        menuIconList.add(R.drawable.word_list_icon);
        menuIconList.add(R.drawable.file_oper_icon);
//        menuIconList.add(R.drawable.local_music_icon);


//        menuIconList.add(R.drawable.gifts_icon);


        menuTextList.add(R.string.oper_study);
//        menuTextList.add(R.string.oper_look);
//        menuTextList.add(R.string.oper_search);
        menuTextList.add(R.string.oper_movies);

        menuTextList.add(R.string.oper_ground);
//        menuTextList.add(R.string.oper_circle);
//        menuTextList.add(R.string.oper_message);
//        menuTextList.add(R.string.oper_recommend);

        menuTextList.add(R.string.word_search_title);
        menuTextList.add(R.string.word_saying_title);
        menuTextList.add(R.string.word_list_title);
        menuTextList.add(R.string.oper_file);
//        menuTextList.add(R.string.oper_local_music);

//        menuTextList.add(R.string.oper_gift);


        discoverList = new ArrayList<>();
        discoverList.add(blank);
        Discover item;
        for (int i = 0; i < menuIconList.size(); i++) {
            item = new Discover();
            item.setText(menuTextList.get(i));
            item.setDrawable(menuIconList.get(i));
            item.setType(TYPE_ITEM);
            discoverList.add(item);
            if (i == 3 || i == 11) {
                discoverList.add(blank);
            }
        }
       /* if (ConfigManager.getInstance().isEggShell()) {
            item = new Discover();
            item.setDrawable(R.drawable.eggshell_icon);
            item.setText(R.string.oper_eggshell);
            item.setType(TYPE_ITEM);
            discoverList.add(item);
        }*/
        discoverList.add(blank);
    }

    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public DiscoverAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    @Override
    public int getItemCount() {
        return discoverList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return discoverList.get(position).getType();
    }

    public Discover getItem(int position) {
        return discoverList.get(position);
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new DiscoverViewHolder(LayoutInflater.from(context).inflate(R.layout.item_discover, parent, false));
        } else if (viewType == TYPE_BLANK) {
            //inflate your layout and pass it to view holder
            return new BlankViewHolder(LayoutInflater.from(context).inflate(R.layout.item_blank, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecycleViewHolder holder, int position) {

        if (holder instanceof DiscoverViewHolder) {

            final DiscoverViewHolder viewHolder = (DiscoverViewHolder) holder;
            final Discover discover = getItem(position);
            viewHolder.setDiscover(discover);

            if (onRecycleViewItemClickListener != null) {
                viewHolder.rippleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        onRecycleViewItemClickListener.onItemClick(viewHolder.rippleView, holder.getBindingAdapterPosition());
                    }
                });
            }
            viewHolder.text.setText(context.getString(discover.getText()));
            viewHolder.icon.setImageResource(discover.getDrawable());
        }
    }

    public static class DiscoverViewHolder extends RecycleViewHolder {
        MaterialRippleLayout rippleView;
        TextView text;
        ImageView icon;

        Discover discover;

        DiscoverViewHolder(View view) {
            super(view);
            rippleView = (MaterialRippleLayout) view.findViewById(R.id.discover_ripple);
            text = (TextView) view.findViewById(R.id.discover_text);
            icon = (ImageView) view.findViewById(R.id.discover_icon);
        }

        public Discover getDiscover() {
            return discover;
        }

        public void setDiscover(Discover discover) {
            this.discover = discover;
        }
    }

    private static class BlankViewHolder extends RecycleViewHolder {

        BlankViewHolder(View view) {
            super(view);
        }
    }
}

