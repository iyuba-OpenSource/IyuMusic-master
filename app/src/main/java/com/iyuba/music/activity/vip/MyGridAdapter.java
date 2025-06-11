package com.iyuba.music.activity.vip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;


/**
 * @Description:gridview的Adapter
 */
public class MyGridAdapter extends BaseAdapter {
    private Context mContext;
    public String[] img_text = {"无广告", "尊贵标识", "调节语速", "高速下载", "查看解析", "智慧化评测",
            "PDF导出", "全部应用", "换话费",};
    public int[] imgs = {R.drawable.tequan1, R.drawable.tequan2,
            R.drawable.tequan3, R.drawable.tequan4,
            R.drawable.tequan5, R.drawable.tequan6,
            R.drawable.tequan7, R.drawable.tequan8,
            R.drawable.tequan9};
    public String[] imgHint = {"应用无广告，只为学习生", "尊享V标识，社区您为贵", "自由调语速，句句能听懂", "万台服务器，就近高速下", "所有测试题，独家配解析", "个性化评测，智慧化学习", "下载无限制，不再需积分", "数十款应用，听说读写通", "天天得积分，免费兑话费"};

    public MyGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void setImageText(String[] imageText) {
        img_text = imageText.clone();
    }

    public void setImgs(int[] imgs) {
        this.imgs = imgs.clone();
    }

    public void setHint(String[] imgHint) {
        this.imgHint = imgHint.clone();
    }

    public String getHint(int i) {
        return this.imgHint[i];
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
        iv.setBackgroundResource(imgs[position]);
        tv.setText(img_text[position]);
        return convertView;
    }
}
