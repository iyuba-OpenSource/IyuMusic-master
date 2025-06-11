package com.iyuba.music.adapter.study;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class SimpleNewsAdapter extends RecyclerView.Adapter<SimpleNewsAdapter.MyViewHolder> {
    private ArrayList<Article> newsList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;
    private int type;
    private boolean delete;
    private LocalInfoOp localInfoOp;
    private boolean deleteAll;

    public SimpleNewsAdapter(Context context) {
        this.context = context;
        newsList = new ArrayList<>();
        type = 0;
        this.delete = false;
        localInfoOp = new LocalInfoOp();
    }


    public SimpleNewsAdapter(Context context, int type) {
        this.context = context;
        newsList = new ArrayList<>();
        this.type = type;
        this.delete = false;
        localInfoOp = new LocalInfoOp();
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
        notifyDataSetChanged();
    }

    public void setDeleteAll() {
        if (deleteAll) {
            for (Article article : newsList) {
                article.setDelete(false);
            }
            deleteAll = false;
        } else {
            for (Article article : newsList) {
                article.setDelete(true);
            }
            deleteAll = true;
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener onItemClickListener) {
        onRecycleViewItemClickListener = onItemClickListener;
    }

    public void removeData(int pos) {
        newsList.remove(pos);
        notifyItemChanged(pos);
    }

    public ArrayList<Article> getDataSet() {
        return newsList;
    }

    public void setDataSet(ArrayList<Article> newses) {
        newsList = newses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_newslist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Article article = newsList.get(position);
        holder.setArticl(article);
        if (article != null) {
            holder.title.setText(article.getTitle());
            if ("209".equals(article.getApp()) && !"401".equals(article.getCategory())) {
                holder.singer.setText(context.getString(R.string.article_singer, article.getSinger()));
                holder.broadcaster.setText(context.getString(R.string.article_announcer, article.getBroadcaster()));
            } else {
                holder.singer.setText(article.getContent());
            }
            try {
                holder.time.setText(article.getTime().split(" ")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (type) {
                case 0:
                    holder.readCount.setText(context.getString(R.string.article_read_count, article.getReadCount()));
                    break;
                case 1:
                    try {
                        holder.readCount.setText(DateFormat.showTime(context, DateFormat.parseTime(article.getExpireContent())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    holder.broadcaster.setVisibility(View.GONE);
                    holder.readCount.setText(context.getString(R.string.article_search_info, article.getTitleFind(), article.getTextFind()));
                    break;
            }
            if (TextUtils.isEmpty(article.getApp())) {
                article.setApp(StudyManager.getInstance().getApp());
            }
            if (localInfoOp.findDataById(article.getApp(), article.getId()).getDownload() == 2) {
                final int id = article.getId();
                holder.download.setVisibility(View.VISIBLE);
                holder.pic.setVisibility(View.GONE);
                holder.timeBackground.setVisibility(View.GONE);
                holder.time.setVisibility(View.GONE);
                DownloadFile file;
                for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {
                    file = DownloadManager.getInstance().fileList.get(i);
                    if (file.id == id) {
                        switch (file.downloadState) {
                            case "start":
                                holder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
                                holder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                                if (file.fileSize != 0 && file.downloadSize != 0) {
                                    holder.download.setMax(file.fileSize);
                                    holder.download.setProgress(file.downloadSize);
                                } else {
                                    holder.download.setMax(1);
                                    holder.download.setProgress(0);
                                }
                                break;
                            case "half_finish":
                                holder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColorAccent());
                                holder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                                if (file.fileSize != 0 && file.downloadSize != 0) {
                                    holder.download.setMax(file.fileSize);
                                    holder.download.setProgress(file.downloadSize);
                                } else {
                                    holder.download.setMax(1);
                                    holder.download.setProgress(0);
                                }
                                break;
                            case "finish":
                                localInfoOp.updateDownload(file.id, article.getApp(), 1);
                                CustomToast.getInstance().showToast(R.string.article_download_success);
                                DownloadManager.getInstance().fileList.remove(file);
                                break;
                            case "fail":
                                localInfoOp.updateDownload(file.id, article.getApp(), 3);
                                CustomToast.getInstance().showToast(R.string.article_download_fail);
                                DownloadManager.getInstance().fileList.remove(file);
                                break;
                        }
                        holder.itemView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(position);
                            }
                        }, 500);
                        break;
                    }
                }
            } else {
                holder.download.setVisibility(View.GONE);
                holder.pic.setVisibility(View.VISIBLE);
                holder.timeBackground.setVisibility(View.VISIBLE);
                holder.time.setVisibility(View.VISIBLE);
            }
            if (delete) {
                holder.cb_delete.setVisibility(View.VISIBLE);
            } else {
                holder.cb_delete.setVisibility(View.GONE);
            }

            holder.cb_delete.setChecked(article.isDelete());


            if (article.getApp().equals("209") && !"401".equals(article.getCategory())) {
                ImageUtil.loadImage("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + article.getPicUrl(), holder.pic, R.drawable.default_music);
            } else {
                ImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music);
            }
            if (DownloadTask.checkFileExists(article)) {
                holder.downloadFlag.setImageResource(R.drawable.article_downloaded);
            } else {
                holder.downloadFlag.setImageResource(R.drawable.article_download);
            }
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class MyViewHolder extends RecycleViewHolder {

        TextView title, singer, broadcaster, time, readCount;
        ImageView pic, downloadFlag;
        CheckBox cb_delete;
        View timeBackground;
        RoundProgressBar download;

        Article articl;

        public Article getArticl() {
            return articl;
        }

        public void setArticl(Article articl) {
            this.articl = articl;
        }

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.article_title);
            singer = (TextView) view.findViewById(R.id.article_singer);
            broadcaster = (TextView) view.findViewById(R.id.article_announcer);
            time = (TextView) view.findViewById(R.id.article_createtime);
            pic = (ImageView) view.findViewById(R.id.article_image);
            downloadFlag = (ImageView) view.findViewById(R.id.article_download);
            readCount = (TextView) view.findViewById(R.id.article_readcount);
            cb_delete = (CheckBox) view.findViewById(R.id.item_delete);
            timeBackground = view.findViewById(R.id.article_createtime_background);
            download = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);


            downloadFlag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AccountManager.getInstance().checkUserLogin()) {
                        CustomToast.getInstance().showToast(R.string.article_download_request);
                        return;
                    }
                    //判断该文件正在下载中,防止再次点击扣除积分
                    for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {

                        DownloadFile file = DownloadManager.getInstance().fileList.get(i);
                        if (articl.getId() == file.id) {
                            return;
                        }
                    }


                    if (DownloadTask.checkFileExists(articl)) {
                        onRecycleViewItemClickListener.onItemClick(itemView, getBindingAdapterPosition());
                    } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        LocalInfoOp localInfoOp = new LocalInfoOp();
                        int songs = localInfoOp.findDataByDownloaded().size() + localInfoOp.findDataByDownloading().size();//本地歌曲数

                        if (songs < 10) {
                            new LocalInfoOp().updateDownload(articl.getId(), "209", 2);
                            DownloadFile downloadFile = new DownloadFile();
                            downloadFile.id = articl.getId();
                            downloadFile.downloadState = "start";
                            DownloadManager.getInstance().fileList.add(downloadFile);

                            new DownloadTask(articl).start();
                            notifyItemChanged(getBindingAdapterPosition());
                            return;
                        }

                        if (songs == 10) {
                            showNoNetDialog(articl, getBindingAdapterPosition());
                            return;
                        }

                        DownloadUtil.checkScore(articl.getId(), new IOperationResult() {
                            @Override
                            public void success(Object object) {
                                new LocalInfoOp().updateDownload(articl.getId(), articl.getApp(), 2);
                                DownloadFile downloadFile = new DownloadFile();
                                downloadFile.id = articl.getId();
                                downloadFile.downloadState = "start";
                                DownloadManager.getInstance().fileList.add(downloadFile);
                                new DownloadTask(articl).start();
                                notifyItemChanged(getBindingAdapterPosition());
                            }

                            @Override
                            public void fail(Object object) {

                            }
                        });

                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
            });

            cb_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newsList.get(getBindingAdapterPosition()).setDelete(cb_delete.isChecked());
                }
            });

            if (onRecycleViewItemClickListener != null) {
                itemView.setOnClickListener(v -> {
                    if (delete) {
                        cb_delete.setChecked(!cb_delete.isChecked());
                        if ((newsList != null) && (getBindingAdapterPosition() < newsList.size())) {
                            newsList.get(getBindingAdapterPosition()).setDelete(cb_delete.isChecked());
                        }
                    } else {
                        if ((newsList != null) && (getBindingAdapterPosition() < newsList.size())) {
//                            onRecycleViewItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                            onRecycleViewItemClickListener.onItemClick(itemView, getBindingAdapterPosition());
                        }
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onRecycleViewItemClickListener.onItemLongClick(itemView, getBindingAdapterPosition());
                        return true;
                    }
                });
            }
        }
    }


    public Article getItem(int position) {

        return newsList.get(position);
    }

    private void showNoNetDialog(final Article newArticle, final int newPos) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle("升级vip即可享受无限下载");
        materialDialog.setMessage("你已下载十首歌曲！以后每首歌曲需要扣除20积分");

        materialDialog.setPositiveButton("继续下载", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();

                DownloadUtil.checkScore(newArticle.getId(), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        new LocalInfoOp().updateDownload(newArticle.getId(), "209", 2);
                        DownloadFile downloadFile = new DownloadFile();
                        downloadFile.id = newArticle.getId();
                        downloadFile.downloadState = "start";
                        DownloadManager.getInstance().fileList.add(downloadFile);
                        new DownloadTask(newArticle).start();
                        notifyItemChanged(newPos);
                    }

                    @Override
                    public void fail(Object object) {

                    }
                });

            }
        });
        materialDialog.setNegativeButton("暂不下载", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                return;
            }
        });

        materialDialog.show();

    }
}
