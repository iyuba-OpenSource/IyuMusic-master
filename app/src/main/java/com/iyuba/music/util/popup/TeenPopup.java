package com.iyuba.music.util.popup;

import android.content.Context;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.databinding.PopupTeenBinding;

import razerdp.basepopup.BasePopupWindow;

public class TeenPopup extends BasePopupWindow {

    private PopupTeenBinding binding;

    public TeenPopup(Context context) {
        super(context);

        View view = createPopupById(R.layout.popup_teen);
        binding = PopupTeenBinding.bind(view);
        setContentView(binding.getRoot());

        setOutSideDismiss(false);
        setBackPressEnable(false);
    }
}
