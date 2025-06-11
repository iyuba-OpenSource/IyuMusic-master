package com.iyuba.music.fragmentAdapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by carl shen on 2020/9/18.
 */
public class DubbingAdapter extends RecyclerView.Adapter<DubbingAdapter.MyViewHolder> {
    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private ArrayList<MusicVoa> mList = new ArrayList<>();
    private boolean IscanDL = true;

    public DubbingAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
    }

    public DubbingAdapter(Context context, boolean IscanDL) {
        this.context = context;
        this.IscanDL = IscanDL;
        mList = new ArrayList<>();
    }

    public void setData(ArrayList<MusicVoa> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dubbing_news, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final int pos = position;
        if (onRecycleViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
        final MusicVoa article = mList.get(position);
        if (ConfigManager.getInstance().getLanguage() == 2 ||
                (ConfigManager.getInstance().getLanguage() == 0 && Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage()))) {
            holder.title.setText(article.title);
        } else {
            if (TextUtils.isEmpty(article.titleCn)) {
                holder.title.setText(article.title);
            } else {
                holder.title.setText(article.titleCn);
            }
        }
        holder.title.setTextColor(GetAppColor.getInstance().getAppColor());
        holder.content.setText(article.descCn);
        holder.time.setText(article.publishTime.split(" ")[0]);
        holder.readCount.setText(context.getString(R.string.article_read_count, "" + article.readCount));
        if (IscanDL) {
            holder.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (DownloadTask.checkFileExists(article)) {
//                        onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
//                    } else {
//                        new LocalInfoOp().updateDownload(article.voaId, article.pic, 2);
//                        DownloadFile downloadFile = new DownloadFile();
//                        downloadFile.id = article.voaId;
//                        downloadFile.downloadState = "start";
//                        DownloadManager.getInstance().fileList.add(downloadFile);
//                        new DownloadTask(article).start();
//                        CustomToast.getInstance().showToast(R.string.article_download_start);
//                    }
                }
            });
        }
        ImageUtil.loadImage(article.pic, holder.pic, R.drawable.default_music);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView title, content, time, readCount;
        ImageView pic;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.article_title);
            content = (TextView) view.findViewById(R.id.article_content);
            time = (TextView) view.findViewById(R.id.article_createtime);
            pic = (ImageView) view.findViewById(R.id.article_image);
            readCount = (TextView) view.findViewById(R.id.article_readcount);
        }
    }
}
