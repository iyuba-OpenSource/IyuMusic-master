package com.iyuba.music.widget.original;

import android.content.Context;

import androidx.appcompat.widget.AppCompatEditText;

import android.text.Layout;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;

public class TextPage extends AppCompatEditText {
    private TextSelectCallBack textSelectCallBack;
    private boolean isCanSelect = false;
    private float[] oldXY;

    public TextPage(Context context) {
        super(context);
        initialize();
    }

    public TextPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TextPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        setGravity(Gravity.TOP);
        this.setBackgroundDrawable(null);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int action = event.getAction();
        Layout layout = getLayout();
        int line;
        int off;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                oldXY = new float[]{event.getX(), event.getY()};
                isCanSelect = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - oldXY[0]) > 5
                        && Math.abs(event.getY() - oldXY[1]) > 5) {
                    isCanSelect = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isCanSelect) {
                    line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                    off = layout.getOffsetForHorizontal(line, (int) event.getX());
                    String selectText = getSelectText(off);
                    if (!TextUtils.isEmpty(selectText)) {
                        this.setCursorVisible(true);
                        if (textSelectCallBack != null) {
                            textSelectCallBack.onSelectText(selectText);
                        }
                    } else {
                        this.setCursorVisible(false);
                        Selection.setSelection(getEditableText(), 0, 0);
                        if (textSelectCallBack != null) {
                            textSelectCallBack.onSelectText("");
                        }
                    }

                }
                break;
        }
        return true;
    }

    private String getSelectText(int currOff) {

        String original = getText().toString().replace("’", "'").replace("‘", "'");
        String letter;
        if (currOff >= original.length()) {
            return null;
        }
        int leftOff = currOff, rightOff = currOff;
        letter = String.valueOf(original.charAt(leftOff));
        if (!letter.matches("[a-zA-Z'-]")) {
            return null;
        }
        while (true) {
            leftOff--;
            if (leftOff < 0) {
                leftOff = 0;
                break;
            }
            letter = String.valueOf(original.charAt(leftOff));
            if (!letter.matches("[a-zA-Z'-]")) {
                leftOff++;
                break;
            }
        }
        while (true) {
            rightOff++;
            if (rightOff >= original.length()) {
                rightOff = original.length();
                break;
            }
            letter = String.valueOf(original.charAt(rightOff));
            if (!letter.matches("[a-zA-Z'-]")) {
                break;
            }
        }
        String endString = original.subSequence(leftOff, rightOff).toString()
                .trim();
        if (!TextUtils.isEmpty(endString)) {
            Selection.setSelection(getEditableText(), leftOff, rightOff);
        } else {
            Selection.setSelection(getEditableText(), 0, 0);
        }
        return endString;
    }

    /**
     * 设置取词监听
     *
     * @param textpageSelectTextCallBack
     */
    public void setTextpageSelectTextCallBack(TextSelectCallBack textpageSelectTextCallBack) {
        this.textSelectCallBack = textpageSelectTextCallBack;
    }
}
