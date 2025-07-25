package com.iyuba.music.activity.eggshell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.eggshell.loading_indicator.LoadingIndicatorList;
import com.iyuba.music.activity.eggshell.material_edittext.MaterialEdittextMainActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiActivity;
import com.iyuba.music.activity.eggshell.view_animations.MyActivity;
import com.iyuba.music.activity.eggshell.weight_monitor.WeightMonitorActivity;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by 10202 on 2015/12/2.
 */
public class EggShellActivity extends BaseActivity {
    private EggShellAdapter eggShellAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.egg_shell);
        context = this;
        ConfigManager.getInstance().setEggShell(true);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        RecyclerView eggShellList = (RecyclerView) findViewById(R.id.eggshell_list);
        eggShellList.setLayoutManager(new LinearLayoutManager(this));
        eggShellAdapter = new EggShellAdapter(context);
        eggShellList.setAdapter(eggShellAdapter);
        eggShellList.addItemDecoration(new DividerItemDecoration());
        eggShellList.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
    }

    @Override
    protected void setListener() {
        super.setListener();
        eggShellAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(context, MyActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(context, MaterialEdittextMainActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, LoadingIndicatorList.class));
                        break;
                    case 3:
                        startActivity(new Intent(context, WeightMonitorActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(context, MeizhiActivity.class));
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_eggshell);
    }
}
