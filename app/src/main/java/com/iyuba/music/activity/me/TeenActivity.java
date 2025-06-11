package com.iyuba.music.activity.me;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.databinding.ActivityTeenBinding;
import com.iyuba.music.util.popup.CloseTeenPopup;
import com.iyuba.music.util.popup.PswPopup;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * 青少年模式页面
 */
public class TeenActivity extends AppCompatActivity {

    private ActivityTeenBinding binding;

    private RxDataStore<Preferences> dataStore;

    private Preferences.Key<String> pk_psw;
    private PswPopup pswPopup;
    private CloseTeenPopup closeTeenPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataStore =
                new RxPreferenceDataStoreBuilder(TeenActivity.this, "TEEN").build();

        pk_psw = PreferencesKeys.stringKey("PASSWORD");
        dataStore.data().map(new Function<Preferences, String>() {
                    @Override
                    public String apply(Preferences preferences) throws Exception {

                        return preferences.get(pk_psw);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (s != null) {

                        binding.teenButStart.setText("关闭青少年模式");
                    } else {

                        binding.teenButStart.setText("开启青少年模式");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (throwable instanceof NullPointerException) {

                            binding.teenButStart.setText("开启青少年模式");
                        }
                    }
                });

        binding.teenButStart.setOnClickListener(v -> {

            if (binding.teenButStart.getText().toString().equals("开启青少年模式")) {

                initPswPopup();
            } else {

                initCloseTeenPopup();
            }
        });
    }

    private void initPswPopup() {

        if (pswPopup == null) {

            pswPopup = new PswPopup(TeenActivity.this);
            pswPopup.setCallback(new PswPopup.Callback() {
                @Override
                public void set(String psw) {

                    dataStore.updateDataAsync(prefsIn -> {
                        MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                        mutablePreferences.set(pk_psw, psw);
                        return Single.just(mutablePreferences);
                    });
                    binding.teenButStart.setText("关闭青少年模式");
                    pswPopup.dismiss();
                }
            });
        }
        pswPopup.showPopupWindow();
    }

    /**
     * 关闭
     */
    private void initCloseTeenPopup() {

        if (closeTeenPopup == null) {

            closeTeenPopup = new CloseTeenPopup(TeenActivity.this);
            closeTeenPopup.setCallback(new CloseTeenPopup.Callback() {
                @Override
                public void set(String psw) {

                    dataStore.updateDataAsync(prefsIn -> {

                        MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                        String pswString = prefsIn.get(pk_psw);
                        if (pswString.equals(psw)) {


                            mutablePreferences.set(pk_psw, null);
                            return Single.just(mutablePreferences);
                        } else {

                            runOnUiThread(() -> Toast.makeText(MusicApplication.getApp(), "密码错误", Toast.LENGTH_SHORT).show());

                            return null;
                        }
                    });
                    closeTeenPopup.dismiss();
                }
            });
        }
        closeTeenPopup.showPopupWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dataStore != null) {

            dataStore.dispose();
        }
    }
}