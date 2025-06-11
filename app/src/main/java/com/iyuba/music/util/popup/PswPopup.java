package com.iyuba.music.util.popup;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.databinding.PopupPswBinding;

import razerdp.basepopup.BasePopupWindow;

public class PswPopup extends BasePopupWindow {

    private PopupPswBinding binding;

    private Callback callback;

    public PswPopup(Context context) {
        super(context);

        View view = createPopupById(R.layout.popup_psw);
        binding = PopupPswBinding.bind(view);
        setContentView(binding.getRoot());

        binding.pswButCancel.setOnClickListener(v -> dismiss());
        binding.pswButConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (binding.pswEtPsw.getText().length() == 0) {

                    Toast.makeText(MusicApplication.getApp(), "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.pswEtPsw2.getText().length() == 0) {

                    Toast.makeText(MusicApplication.getApp(), "请输入重复密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!binding.pswEtPsw.getText().toString().equals(binding.pswEtPsw2.getText().toString())) {

                    Toast.makeText(MusicApplication.getApp(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (callback != null) {

                    callback.set(binding.pswEtPsw.getText().toString());
                }
            }
        });
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void set(String psw);
    }
}
