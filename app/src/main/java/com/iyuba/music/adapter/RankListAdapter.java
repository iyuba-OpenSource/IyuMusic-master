package com.iyuba.music.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.iyuba.music.R;
import com.iyuba.music.entity.RankBean;
import com.iyuba.music.manager.ConstantManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RankListAdapter extends BaseAdapter {

    private Context mContext;
    private List<RankBean> rankUserList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Pattern p;
    private Matcher m;
    private String rankType;

    public RankListAdapter(Context mContext, List<RankBean> rankUserList, String rankType) {
        this.mContext = mContext;
        this.rankUserList.addAll(rankUserList);
        this.rankType = rankType;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return rankUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return rankUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(rankUserList.get(position).getSort());
    }

    public void resetList(List<RankBean> list) {
        rankUserList.clear();
        rankUserList.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<RankBean> list) {

        if (list.size() > 0) {
            int old_sort = Integer.valueOf(rankUserList.get(rankUserList.size() - 1).getRanking());
            int new_sore = Integer.valueOf(list.get(0).getRanking());
            if (new_sore <= old_sort) {
                return;
            }
        }

        rankUserList.addAll(list);


        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rank_info_item, null);
        }


        RankBean ru = rankUserList.get(position);
        String firstChar;
        if (ru.getName() == null || "".equals(ru.getName()))
            ru.setName(ru.getUid());
        ImageView rankLogoImage = ViewHolder.get(convertView, R.id.rank_logo_image);
        TextView rankLogoText = ViewHolder.get(convertView, R.id.rank_logo_text);
        ImageView userImage = ViewHolder.get(convertView, R.id.user_image);
        TextView userImageText = ViewHolder.get(convertView, R.id.user_image_text);
        TextView userName = ViewHolder.get(convertView, R.id.rank_user_name);
        TextView userInfo = ViewHolder.get(convertView, R.id.rank_user_info);
        TextView userWords = ViewHolder.get(convertView, R.id.rank_user_words);

        firstChar = getFirstChar(ru.getName());
        switch (ru.getRanking()) {
            case "1":
                rankLogoText.setVisibility(View.INVISIBLE);
                rankLogoImage.setVisibility(View.VISIBLE);
                rankLogoImage.setImageResource(R.drawable.rank_gold);

                if (ru.getImgSrc().equals("http://static1." + ConstantManager.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());

                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());

                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    }
                } else {
                    userImage.setVisibility(View.VISIBLE);
                    userImageText.setVisibility(View.INVISIBLE);
                    Glide.with(mContext).load(ru.getImgSrc()).placeholder(R.drawable.noavatar_small).into(userImage);
//                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
//                            R.drawable.noavatar_small);
                    userName.setText(ru.getName());

                    setinfo(userInfo, userWords, ru);
//                    userInfo.setText( "News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                    userWords.setText("Words:" +ru.getWords());
                }
                break;
            case "2":
                rankLogoText.setVisibility(View.INVISIBLE);
                rankLogoImage.setVisibility(View.VISIBLE);
                rankLogoImage.setImageResource(R.drawable.rank_silvery);

                if (ru.getImgSrc().equals("http://static1." + ConstantManager.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());
                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText( "News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());

                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText( "News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    }
                } else {
                    userImage.setVisibility(View.VISIBLE);
                    userImageText.setVisibility(View.INVISIBLE);
                    Glide.with(mContext).load(ru.getImgSrc()).placeholder(R.drawable.noavatar_small).into(userImage);
//                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
//                            R.drawable.noavatar_small);
                    userName.setText(ru.getName());

                    setinfo(userInfo, userWords, ru);
//                    userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                    userWords.setText("Words:" +ru.getWords());
                }
                break;
            case "3":
                rankLogoText.setVisibility(View.INVISIBLE);
                rankLogoImage.setVisibility(View.VISIBLE);
                rankLogoImage.setImageResource(R.drawable.rank_copper);

                if (ru.getImgSrc().equals("http://static1." + ConstantManager.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());

                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText(  "News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());
                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" + ru.getWords());
                    }
                } else {
                    userImageText.setVisibility(View.INVISIBLE);
                    userImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(ru.getImgSrc()).placeholder(R.drawable.noavatar_small).into(userImage);
//                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
//                            R.drawable.noavatar_small);
                    userName.setText(ru.getName());

                    setinfo(userInfo, userWords, ru);
//                    userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                    userWords.setText("Words:" +ru.getWords());
                }
                break;
            case "4":
                ;
            case "5":

                rankLogoImage.setVisibility(View.INVISIBLE);
                rankLogoText.setVisibility(View.VISIBLE);
                rankLogoText.setText(ru.getRanking());
                rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                rankLogoText.setSingleLine(true);
                //rankLogoText.setSelected(true);
                //rankLogoText.setFocusable(true);
                // rankLogoText.setFocusableInTouchMode(true);

                if (ru.getImgSrc().equals("http://static1." + ConstantManager.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());


                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());

                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText( "News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    }
                } else {
                    userImageText.setVisibility(View.INVISIBLE);
                    userImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(ru.getImgSrc()).placeholder(R.drawable.noavatar_small).into(userImage);
//                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
//                            R.drawable.noavatar_small);
                    userName.setText(ru.getName());

                    setinfo(userInfo, userWords, ru);
//                    userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                    userWords.setText("Words:" +ru.getWords());
                }
                break;
            default:
                rankLogoImage.setVisibility(View.INVISIBLE);
                rankLogoText.setVisibility(View.VISIBLE);
                rankLogoText.setText(ru.getRanking());
                rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                rankLogoText.setSingleLine(true);
                rankLogoText.setSelected(true);
                rankLogoText.setFocusable(true);
                rankLogoText.setFocusableInTouchMode(true);
                //抢占了item的焦点 不能点击

                if (ru.getImgSrc().equals("http://static1." + ConstantManager.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    userImage.setVisibility(View.INVISIBLE);
                    userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        userImageText.setBackgroundResource(R.drawable.rank_blue);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());

                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    } else {
                        userImageText.setBackgroundResource(R.drawable.rank_green);
                        userImageText.setText(firstChar);
                        userName.setText(ru.getName());

                        setinfo(userInfo, userWords, ru);
//                        userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                    }
                } else {
                    userImageText.setVisibility(View.INVISIBLE);
                    userImage.setVisibility(View.VISIBLE);

                    Glide.with(mContext).load(ru.getImgSrc()).placeholder(R.drawable.noavatar_small).into(userImage);
//                    GitHubImageLoader.Instace(mContext).setRawPic(ru.getImgSrc(), userImage,
//                            R.drawable.noavatar_small);
                    userName.setText(ru.getName());

                    setinfo(userInfo, userWords, ru);
//                    userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                    userWords.setText("Words:" +ru.getWords());
                }
                break;

        }

        return convertView;
    }

    private String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            p = Pattern.compile("[0-9]*");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }


    private void setinfo(TextView tv1, TextView tv2, RankBean rankUser) {


        switch (rankType) {
            case "阅读":

                tv1.setText("文章数:" + rankUser.getCnt() + "\nWPM:" + rankUser.getWpm());
                tv2.setText("单词数:" + rankUser.getWords());

                break;
            case "听力":
                tv1.setText("文章数:" + rankUser.getTotalEssay() + "\n单词数:" + rankUser.getTotalWord());

                int min = Integer.valueOf(rankUser.getTotalTime()) / 60;
                tv2.setText(min + "分钟"); //学习时间
                break;
            case "口语":


                double scores_avg = 0;
                if (rankUser.getCount().equals("0")) {
                    scores_avg = 0;
                } else {

                    scores_avg = Double.valueOf(rankUser.getScores()) / Double.valueOf(rankUser.getCount());
                }

                DecimalFormat df = new DecimalFormat("0.00");

                tv1.setText("总分:" + rankUser.getScores() + "\n平均分:" + df.format(scores_avg));
                tv2.setText("句子数:" + rankUser.getCount());
                break;
            case "学习":

                tv1.setText("文章数:" + rankUser.getTotalEssay()+ "\n单词数:" + rankUser.getTotalWord());

                double hour = Double.valueOf(rankUser.getTotalTime()) / 3600;
                DecimalFormat df2 = new DecimalFormat("0.00");

                tv2.setText(df2.format(hour) + "小时"); //学习时间
                break;
            case "测试":


                double right = 0;
                if (Double.valueOf(rankUser.getTotalTest()) == 0) {
                    right = 0 ;
                } else {
                    right = Double.valueOf(rankUser.getTotalRight()) / Double.valueOf(rankUser.getTotalTest());
                }


                DecimalFormat df3 = new DecimalFormat("0.00");

                tv2.setText("总题数:" + rankUser.getTotalTest());

                tv1.setText("正确数:" + rankUser.getTotalRight() + "\n正确率:" + df3.format(right));
                break;

        }
    }

}
