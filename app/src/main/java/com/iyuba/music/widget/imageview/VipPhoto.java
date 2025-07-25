package com.iyuba.music.widget.imageview;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iyuba.music.R;
import com.iyuba.music.util.ImageUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2017/2/27.
 */

public class VipPhoto extends RelativeLayout {
    private CircleImageView circleImageView;
    private ImageView vipStatus;
    private Context context;

    public VipPhoto(Context context) {
        super(context);
        initWidget(context);
    }

    public VipPhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWidget(context);
    }

    public VipPhoto(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget(context);
    }

    private void initWidget(Context context) {
        this.context = context;
        View root = LayoutInflater.from(context).inflate(R.layout.vip_photo, null);
        circleImageView = (CircleImageView) root.findViewById(R.id.vip_photo_img);
        vipStatus = (ImageView) root.findViewById(R.id.vip_photo_status);
        addView(root);

    }

    public void setPhotoChange(String userid, boolean isVip) {
        ImageUtil.setHeadImage(userid, context, R.drawable.default_photo, circleImageView);

        if (isVip) {
            vipStatus.setVisibility(VISIBLE);
        } else {
            vipStatus.setVisibility(GONE);
        }
    }


    public void setVipStateVisible(String userid, boolean isVip) {
        ImageUtil.loadAvatar(userid, circleImageView);
        if (isVip) {
            vipStatus.setVisibility(VISIBLE);
        } else {
            vipStatus.setVisibility(GONE);
        }
    }

    public void setVisitor() {
        circleImageView.setImageResource(R.drawable.default_photo);
        vipStatus.setVisibility(GONE);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (circleImageView != null) {
            circleImageView.setOnClickListener(l);
        }
        if (vipStatus != null) {
            vipStatus.setOnClickListener(l);
        }
    }
}
