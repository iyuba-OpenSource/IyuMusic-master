package com.iyuba.music.activity.vip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;

/**
 * 按钮控件，在原生按钮基础上增加点击效果
 * 
 * @author lijingwei
 * 
 */
public class Button extends androidx.appcompat.widget.AppCompatButton {

	private Animation mAnimationAlpha;

	public Button(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public Button(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Button(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
   
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getBackground().setAlpha(100);
			invalidate();
			// mAnimationAlpha=new AlphaAnimation(1.0f,0.5f);
			// mAnimationAlpha.setDuration(1000);
			// startAnimation(mAnimationAlpha);
			// mAnimationAlpha.setAnimationListener(new AnimationListener() {
			//
			// @Override
			// public void onAnimationStart(Animation animation) {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// @Override
			// public void onAnimationRepeat(Animation animation) {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// @Override
			// public void onAnimationEnd(Animation animation) {
			// // TODO Auto-generated method stub
			// getBackground().setAlpha(100);
			// invalidate();
			// }
			// });

			break;
		case MotionEvent.ACTION_UP:
			getBackground().setAlpha(255);
			invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}

}
