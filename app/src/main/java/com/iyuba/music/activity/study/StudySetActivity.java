package com.iyuba.music.activity.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.iyuba.music.widget.roundview.RoundRelativeLayout;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

/**
 * Created by 10202 on 2015/12/5.
 */
public class StudySetActivity extends BaseActivity implements View.OnClickListener {
    private RoundRelativeLayout playMode, lyricMode, nextMode, autoRound, downLoad, headplugPlay, headplugPause, originalSize, mediaButton;
    private SwitchButton currAutoRound, currHeadplugPlay, currHeadplugPause;
    private TextView currPlayMode, currNextMode, currDownLoad, currLyricMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_setting);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        playMode = (RoundRelativeLayout) findViewById(R.id.study_set_playmode);
        AddRippleEffect.addRippleEffect(playMode);
        lyricMode = (RoundRelativeLayout) findViewById(R.id.study_lyric_playmode);
        AddRippleEffect.addRippleEffect(lyricMode);
        nextMode = (RoundRelativeLayout) findViewById(R.id.study_set_nextmode);
        AddRippleEffect.addRippleEffect(nextMode);
        autoRound = (RoundRelativeLayout) findViewById(R.id.study_set_auto_round);
        AddRippleEffect.addRippleEffect(autoRound);
        downLoad = (RoundRelativeLayout) findViewById(R.id.study_set_download);
        AddRippleEffect.addRippleEffect(downLoad);
        headplugPlay = (RoundRelativeLayout) findViewById(R.id.study_set_headplug_play);
        AddRippleEffect.addRippleEffect(headplugPlay);
        headplugPause = (RoundRelativeLayout) findViewById(R.id.study_set_headplug_pause);
        AddRippleEffect.addRippleEffect(headplugPause);
        originalSize = (RoundRelativeLayout) findViewById(R.id.study_set_original_size);
        AddRippleEffect.addRippleEffect(originalSize);
        mediaButton = (RoundRelativeLayout) findViewById(R.id.study_set_media_button);
        AddRippleEffect.addRippleEffect(mediaButton);
        currPlayMode = (TextView) findViewById(R.id.study_set_playmode_current);
        currLyricMode = (TextView) findViewById(R.id.study_lyric_playmode_current);
        currNextMode = (TextView) findViewById(R.id.study_set_nextmode_current);
        currDownLoad = (TextView) findViewById(R.id.study_set_download_current);
        currAutoRound = (SwitchButton) findViewById(R.id.study_set_auto_round_current);
        currHeadplugPlay = (SwitchButton) findViewById(R.id.study_set_headplug_play_current);
        currHeadplugPause = (SwitchButton) findViewById(R.id.study_set_headplug_pause_current);
    }

    @Override
    protected void setListener() {
        super.setListener();
        playMode.setOnClickListener(this);
        lyricMode.setOnClickListener(this);
        nextMode.setOnClickListener(this);
        autoRound.setOnClickListener(this);
        downLoad.setOnClickListener(this);
        headplugPlay.setOnClickListener(this);
        headplugPause.setOnClickListener(this);
        originalSize.setOnClickListener(this);
        mediaButton.setOnClickListener(this);
        currPlayMode.setOnClickListener(this);
        currNextMode.setOnClickListener(this);
        currDownLoad.setOnClickListener(this);

        currAutoRound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setAutoRound(isChecked);
                if (isChecked) {
                    currAutoRound.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currAutoRound.setBackColorRes(R.color.background_light);
                }
            }
        });
        currHeadplugPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setAutoPlay(isChecked);
                if (isChecked) {
                    currHeadplugPlay.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currHeadplugPlay.setBackColorRes(R.color.background_light);
                }
            }
        });
        currHeadplugPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setAutoStop(isChecked);
                if (isChecked) {
                    currHeadplugPause.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currHeadplugPause.setBackColorRes(R.color.background_light);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        currPlayMode.setText(getPlayMode(ConfigManager.getInstance().getStudyMode()));
        currLyricMode.setText(getLyricMode(ConfigManager.getInstance().getLyricMode()));
        currNextMode.setText(getNextMode(ConfigManager.getInstance().getStudyPlayMode()));
        currDownLoad.setText(getDownload(ConfigManager.getInstance().getDownloadMode()));
        currAutoRound.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isAutoRound());
        currHeadplugPlay.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isAutoPlay());
        currHeadplugPause.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isAutoStop());
        if (currAutoRound.isChecked()) {
            currAutoRound.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currAutoRound.setBackColorRes(R.color.background_light);
        }
        if (currHeadplugPlay.isChecked()) {
            currHeadplugPlay.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currHeadplugPlay.setBackColorRes(R.color.background_light);
        }
        if (currHeadplugPause.isChecked()) {
            currHeadplugPause.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currHeadplugPause.setBackColorRes(R.color.background_light);
        }
        title.setText(R.string.setting_study_set);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.study_set_playmode) {
            popPlayModeDialog();
        } else if (id == R.id.study_lyric_playmode) {
            popLyricModeDialog();
        } else if (id == R.id.study_set_playmode_current) {
            popPlayModeDialog();
        } else if (id == R.id.study_set_nextmode) {
            popNextModeDialog();
        } else if (id == R.id.study_set_nextmode_current) {
            popNextModeDialog();
        } else if (id == R.id.study_set_download) {
            popDownloadDialog();
        } else if (id == R.id.study_set_download_current) {
            popDownloadDialog();
        } else if (id == R.id.study_set_auto_round) {
            currAutoRound.setChecked(!currAutoRound.isChecked());
            ConfigManager.getInstance().setAutoRound(currAutoRound.isChecked());
        } else if (id == R.id.study_set_headplug_play) {
            currHeadplugPlay.setChecked(!currHeadplugPlay.isChecked());
            ConfigManager.getInstance().setAutoPlay(currHeadplugPlay.isChecked());
        } else if (id == R.id.study_set_headplug_pause) {
            currHeadplugPause.setChecked(!currHeadplugPause.isChecked());
            ConfigManager.getInstance().setAutoStop(currHeadplugPause.isChecked());
        } else if (id == R.id.study_set_original_size) {
            startActivity(new Intent(context, OriginalSizeActivity.class));
        } else if (id == R.id.study_set_media_button) {
            startActivity(new Intent(context, MediaButtonControlActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post("play_state");
    }

    private void popPlayModeDialog() {
        final MyMaterialDialog groupDialog = new MyMaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_playmode);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.type)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != ConfigManager.getInstance().getStudyMode()) {
                    ConfigManager.getInstance().setStudyMode(position);
                    currPlayMode.setText(getPlayMode(position));
                    ((MusicApplication) getApplication()).getPlayerService().startPlay(
                            StudyManager.getInstance().getCurArticle(), true);
                    ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
                }
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(ConfigManager.getInstance().getStudyMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupDialog.dismiss();
            }
        });
        groupDialog.show();
    }

    private void popLyricModeDialog() {
        final MyMaterialDialog groupDialog = new MyMaterialDialog(context);
        groupDialog.setTitle(R.string.study_lyric_playmode);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.lyricMode)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != ConfigManager.getInstance().getLyricMode()) {
                    ConfigManager.getInstance().setLyricMode(position);
                    currLyricMode.setText(getLyricMode(position));
                }
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(ConfigManager.getInstance().getLyricMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupDialog.dismiss();
            }
        });
        groupDialog.show();
    }

    private void popNextModeDialog() {
        final MyMaterialDialog groupDialog = new MyMaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_nextmode);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.mode)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (ConfigManager.getInstance().getStudyPlayMode() != position) {
                    ConfigManager.getInstance().setStudyPlayMode(position);
                    currNextMode.setText(getNextMode(position));
                }
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(ConfigManager.getInstance().getStudyPlayMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupDialog.dismiss();
            }
        });
        groupDialog.show();
    }


    private void popDownloadDialog() {
        final MyMaterialDialog groupDialog = new MyMaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_download);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.download)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != ConfigManager.getInstance().getDownloadMode()) {
                    ConfigManager.getInstance().setDownloadMode(position);
                    currDownLoad.setText(getDownload(position));
                }
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(ConfigManager.getInstance().getDownloadMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupDialog.dismiss();
            }
        });
        groupDialog.show();
    }

    private String getPlayMode(int order) {
        String[] playModeGroup = context.getResources().getStringArray(R.array.type);
        return playModeGroup[order];
    }

    private String getLyricMode(int order) {
        String[] playModeGroup = context.getResources().getStringArray(R.array.lyricMode);
        return playModeGroup[order];
    }

    private String getNextMode(int order) {
        String[] nextModeGroup = context.getResources().getStringArray(R.array.mode);
        return nextModeGroup[order];
    }

    private String getDownload(int order) {
        String[] downloadGroup = context.getResources().getStringArray(R.array.download);
        return downloadGroup[order];
    }
}
