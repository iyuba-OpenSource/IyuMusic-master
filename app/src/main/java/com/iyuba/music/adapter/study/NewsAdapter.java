package com.iyuba.music.adapter.study;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.FavorRequest;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.banner.BannerView;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/10/10.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<Article> newsList;
    private ArrayList<BannerEntity> adPicUrl;
    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private LocalInfoOp localInfoOp;

    public NewsAdapter(Context context) {
        this.context = context;
        newsList = new ArrayList<>();
        adPicUrl = new ArrayList<>();
        localInfoOp = new LocalInfoOp();
    }

    private static void getAppointArticle(final Context context, String id) {
        NewsesRequest.exeRequest(NewsesRequest.generateUrl(id), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                for (Article temp : netData) {
                    temp.setApp(ConstantManager.appId);
                }
                new ArticleOp().saveData(netData);
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setListFragmentPos("SongCategoryFragment.class");
                StudyManager.getInstance().setLesson("music");
                StudyManager.getInstance().setSourceArticleList(netData);
                StudyManager.getInstance().setCurArticle(netData.get(0));
                context.startActivity(new Intent(context, StudyActivity.class));
            }
        });
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener onItemClickListener) {
        onRecycleViewItemClickListener = onItemClickListener;
    }

    public void setDataSet(ArrayList<Article> newses) {
        newsList = newses;
        notifyDataSetChanged();
    }

    public void setAdSet(ArrayList<BannerEntity> ads) {
        adPicUrl = ads;
        notifyItemChanged(0);
    }

    public void removeData(int position) {
        newsList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeData(int[] position) {
        for (int i : position) {
            newsList.remove(i);
            notifyItemRemoved(i);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private Article getItem(int position) {
        return newsList.get(position - 1);
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ChangePropery.setAppConfig((Activity) context);
        if (viewType == TYPE_ITEM) {
            return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_newslist, parent, false));
        } else if (viewType == TYPE_HEADER) {
            //banner广告
            return new AdViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ad, parent, false), context);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
        final int pos = position;
        if (holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            final Article article = getItem(position);
            if (onRecycleViewItemClickListener != null) {
                newsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecycleViewItemClickListener.onItemClick(newsViewHolder.itemView, pos - 1);
                    }
                });
            }
            newsViewHolder.title.setText(article.getTitle());
            newsViewHolder.singer.setText(context.getString(R.string.article_singer, article.getSinger()));
            newsViewHolder.broadcaster.setText(context.getString(R.string.article_announcer, article.getBroadcaster()));
            newsViewHolder.time.setText(article.getTime().split(" ")[0]);
            newsViewHolder.readCount.setText(context.getString(R.string.article_read_count, article.getReadCount()));

            ImageUtil.loadImage("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + article.getPicUrl(),
                    newsViewHolder.pic, R.drawable.default_music);


            if (localInfoOp.findDataById(article.getApp(), article.getId()).getFavourite() == 1) {
                newsViewHolder.article_favor.setImageResource(R.drawable.favor_true);
            } else {
                newsViewHolder.article_favor.setImageResource(R.drawable.favor);
            }
            //收藏
            newsViewHolder.article_favor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AccountManager.getInstance().checkUserLogin()) {
                        CustomToast.getInstance().showToast(R.string.article_favor_request);
//                        context.startActivity(new Intent(context, LoginActivity.class));
                        return;
                    }
                    if (localInfoOp.findDataById(article.getApp(), article.getId()).getFavourite() == 1) {
                        FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), article.getId(), "del"), new IProtocolResponse() {
                            @Override
                            public void onNetError(String msg) {
                                CustomToast.getInstance().showToast(R.string.article_favor_cancel_fail);
                            }

                            @Override
                            public void onServerError(String msg) {
                                CustomToast.getInstance().showToast(R.string.article_favor_cancel_fail);
                            }

                            @Override
                            public void response(Object object) {
                                if (object.toString().equals("del")) {
                                    localInfoOp.updateFavor(article.getId(), article.getApp(), 0);
                                    CustomToast.getInstance().showToast(R.string.article_favor_cancel);


                                    newsViewHolder.article_favor.setImageResource(R.drawable.favor);
                                }
                            }
                        });
                    } else {
                        FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), article.getId(), "insert"), new IProtocolResponse() {
                            @Override
                            public void onNetError(String msg) {
                                CustomToast.getInstance().showToast(R.string.article_favor_fail);
                            }

                            @Override
                            public void onServerError(String msg) {
                                CustomToast.getInstance().showToast(R.string.article_favor_fail);
                            }

                            @Override
                            public void response(Object object) {
                                if (object.toString().equals("insert")) {
                                    localInfoOp.updateFavor(article.getId(), article.getApp(), 1);
                                    CustomToast.getInstance().showToast(R.string.article_favor);


                                    newsViewHolder.article_favor.setImageResource(R.drawable.favor_true);
                                }
                            }
                        });
                    }

                }
            });

            newsViewHolder.downloadFlag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (!AccountManager.getInstance().checkUserLogin()) {
                        CustomToast.getInstance().showToast(R.string.article_download_request);
                        return;
                    }
                    //判断该文件正在下载中,防止再次点击扣除积分
                    for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {

                        DownloadFile file = DownloadManager.getInstance().fileList.get(i);
                        if (article.getId() == file.id) {
                            Toast.makeText(context, "正在下载中，请稍等。", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (DownloadTask.checkFileExists(article)) {
                        Toast.makeText(context, "已经下载完成。", Toast.LENGTH_SHORT).show();
                        return;
//                        onRecycleViewItemClickListener.onItemClick(newsViewHolder.itemView, pos);

                    } else {


                        LocalInfoOp localInfoOp = new LocalInfoOp();
                        int songs = localInfoOp.findDataByDownloaded().size() + localInfoOp.findDataByDownloading().size();


                        if (songs < 10) {

                            new LocalInfoOp().updateDownload(article.getId(), article.getApp(), 2);
                            DownloadFile downloadFile = new DownloadFile();
                            downloadFile.id = article.getId();
                            downloadFile.downloadState = "start";
                            DownloadManager.getInstance().fileList.add(downloadFile);
                            Toast.makeText(context, "开始下载，请稍等。", Toast.LENGTH_SHORT).show();

                            new DownloadTask(article).start();
                            notifyItemChanged(pos);
                            return;
                        }

                        if (songs == 10) {
                            showNoNetDialog(article, pos);
                            return;
                        }

                        DownloadUtil.checkScore(article.getId(), new IOperationResult() {
                            @Override
                            public void success(Object object) {
                                new LocalInfoOp().updateDownload(article.getId(), article.getApp(), 2);
                                DownloadFile downloadFile = new DownloadFile();
                                downloadFile.id = article.getId();
                                downloadFile.downloadState = "start";
                                DownloadManager.getInstance().fileList.add(downloadFile);
                                new DownloadTask(article).start();
                                notifyItemChanged(pos);
                            }

                            @Override
                            public void fail(Object object) {

                            }
                        });

                    }
                }
            });

            if (localInfoOp.findDataById(article.getApp(), article.getId(), false).getDownload() == 2) {

                final int id = article.getId();
                newsViewHolder.download.setVisibility(View.VISIBLE);
                newsViewHolder.pic.setVisibility(View.GONE);
                newsViewHolder.timeBackground.setVisibility(View.GONE);
                newsViewHolder.time.setVisibility(View.GONE);
                DownloadFile file;
                for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {
                    file = DownloadManager.getInstance().fileList.get(i);
                    if (file.id == id) {
                        switch (file.downloadState) {
                            case "start":
                                newsViewHolder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
                                newsViewHolder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                                if (file.fileSize != 0 && file.downloadSize != 0) {
                                    newsViewHolder.download.setMax(file.fileSize);
                                    newsViewHolder.download.setProgress(file.downloadSize);
                                } else {
                                    newsViewHolder.download.setMax(1);
                                    newsViewHolder.download.setProgress(0);
                                }

                                Log.e("下载开始", "=======");
                                break;
                            case "half_finish":
                                newsViewHolder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColorAccent());
                                newsViewHolder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                                if (file.fileSize != 0 && file.downloadSize != 0) {
                                    newsViewHolder.download.setMax(file.fileSize);
                                    newsViewHolder.download.setProgress(file.downloadSize);
                                } else {
                                    newsViewHolder.download.setMax(1);
                                    newsViewHolder.download.setProgress(0);
                                }
                                Log.e("下载一半", "=======");
                                break;
                            case "finish":
                                localInfoOp.updateDownload(file.id, article.getApp(), 1);
                                Log.e("下载完成", "=======");
                                CustomToast.getInstance().showToast(R.string.article_download_success);
                                DownloadManager.getInstance().fileList.remove(file);

                                break;
                            case "fail":
                                localInfoOp.updateDownload(file.id, article.getApp(), 3);
                                CustomToast.getInstance().showToast(R.string.article_download_fail);
                                DownloadManager.getInstance().fileList.remove(file);
                                Log.e("下载失败", "=======");
                                break;
                        }
                        newsViewHolder.itemView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(pos);
                            }
                        }, 500);
                        break;
                    }
                }
            } else {
                newsViewHolder.download.setVisibility(View.GONE);
                newsViewHolder.pic.setVisibility(View.VISIBLE);
                newsViewHolder.timeBackground.setVisibility(View.VISIBLE);
                newsViewHolder.time.setVisibility(View.VISIBLE);
            }

            if (DownloadTask.checkFileExists(article)) {
                newsViewHolder.downloadFlag.setImageResource(R.drawable.article_downloaded);
            } else {
                newsViewHolder.downloadFlag.setImageResource(R.drawable.article_download);
            }
        } else if (holder instanceof AdViewHolder) {
            AdViewHolder headerViewHolder = (AdViewHolder) holder;
            headerViewHolder.setBannerData(adPicUrl);
        }
    }

    private static class NewsViewHolder extends RecycleViewHolder {
        TextView title, singer, broadcaster, time, readCount;
        ImageView pic, downloadFlag, article_favor;
        View timeBackground;
        RoundProgressBar download;

        NewsViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.article_title);
            singer = (TextView) view.findViewById(R.id.article_singer);
            broadcaster = (TextView) view.findViewById(R.id.article_announcer);
            time = (TextView) view.findViewById(R.id.article_createtime);
            pic = (ImageView) view.findViewById(R.id.article_image);
            downloadFlag = (ImageView) view.findViewById(R.id.article_download);
            readCount = (TextView) view.findViewById(R.id.article_readcount);
            timeBackground = view.findViewById(R.id.article_createtime_background);
            download = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
            article_favor = (ImageView) view.findViewById(R.id.article_favor);
        }
    }

    private static class AdViewHolder extends RecycleViewHolder {
        BannerView bannerView;
        RelativeLayout root;
        Context context1;

        AdViewHolder(View view, Context context1) {
            super(view);
            bannerView = (BannerView) view.findViewById(R.id.banner);
            bannerView.setSelectItemColor(GetAppColor.getInstance().getAppColor());
            this.context1 = context1;
            root = (RelativeLayout) view.findViewById(R.id.root);
        }

        void setBannerData(final List<BannerEntity> datas) {


            bannerView.initData(datas, new IOnClickListener() {
                @Override
                public void onClick(View view, Object message) {

                    BannerEntity data = (BannerEntity) message;
                    Log.e("bannernew", data.getOwnerid() + "==");
                    switch (data.getOwnerid()) {
                        case "0":
                            Intent intent = new Intent(view.getContext(), WebViewActivity.class);
                            intent.putExtra("url", data.getName());
                            view.getContext().startActivity(intent);
                            break;
                        case "1":
                            Article tempArticle = new ArticleOp().findById(ConstantManager.appId, Integer.parseInt(data.getName()));
                            if (tempArticle.getId() == 0) {
                                getAppointArticle(view.getContext(), data.getName());
                            } else {
                                StudyManager.getInstance().setStartPlaying(true);
                                StudyManager.getInstance().setListFragmentPos("NewsFragment.class");
                                StudyManager.getInstance().setLesson("music");
                                ArrayList<Article> articles = new ArrayList<>();
                                articles.add(tempArticle);
                                StudyManager.getInstance().setSourceArticleList(articles);
                                StudyManager.getInstance().setCurArticle(tempArticle);
                                view.getContext().startActivity(new Intent(view.getContext(), StudyActivity.class));
                            }
                            break;
                        case "2":
                            CustomToast.getInstance().showToast(R.string.app_intro);
                            break;
                    }
                }
            });
        }
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
                        new LocalInfoOp().updateDownload(newArticle.getId(), newArticle.getApp(), 2);
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
