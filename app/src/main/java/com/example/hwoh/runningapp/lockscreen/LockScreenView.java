package com.example.hwoh.runningapp.lockscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.hwoh.runningapp.R;

/**
 * Created by hwoh on 2016-03-16.
 */
public class LockScreenView extends FrameLayout {
    private String DEBUG_TAG = "MainActivity";
    private Context mContext;
    float handleStart = 0.0f;
    float handleEnd = 0.0f;
    private ImageView mImgHandle, mLeftItem, mRightItem;
    private int page = 0;

    private OnTriggerListener mOnTriggerListener;

    public interface OnTriggerListener {
        int NO_HANDLE = 0;
        int CENTER_HANDLE = 1;

        public void onTrigger(View v, int target);

    }

    public LockScreenView(Context context) {
        super(context);
        init(context);
    }
    public LockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    //initializations
    private void init(Context context) {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.view_lockscreen, this, false);
        addView(v);

        mImgHandle = (ImageView)findViewById(R.id.Handle);
        mLeftItem = (ImageView)findViewById(R.id.leftItem);
        mRightItem = (ImageView)findViewById(R.id.rightItem);
        mImgHandle.setOnTouchListener(TouchListener);
    }
    public void setOnTriggerListener(OnTriggerListener listener) {
        mOnTriggerListener = listener;
    }
    private void dispatchTriggerEvent(int whichTarget) {
        if (mOnTriggerListener != null) {
            mOnTriggerListener.onTrigger(this, whichTarget);
        }
    }



    OnTouchListener TouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("hwoh", "onTouch");
            int action = event.getAction();
            int downX = 0;
            int moveX = 0;
            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                    mImgHandle.setImageResource(R.drawable.lock_btn_center_p);
                    mLeftItem.setImageResource(R.drawable.lock_btn_app_down_p);
                    mRightItem.setImageResource(R.drawable.lock_btn_unlock_p);

                    handleStart = 0;
                    handleEnd = 0;
                    downX = (int) event.getX();
                    return true;
                case (MotionEvent.ACTION_MOVE):
                    moveX = (int) event.getX();
                    //이동값이 0보다 작거나, ,이동 값이 터치영역의 높이에서 이미지만큼의 높이를 뺀것에 1/2 모드ㅏ 작어야함
                    handleStart = moveX - downX - (mImgHandle.getWidth() / 2);
                    handleEnd = moveX - (mImgHandle.getHeight() / 2);
                    //이동 애니메이션
                    Animation moveAni = new TranslateAnimation(handleStart, handleEnd, 0, 0);
                    moveAni.setDuration(200);
                    moveAni.setFillAfter(true);
                    mImgHandle.startAnimation(moveAni);

                    if(handleEnd < -220){
                        mImgHandle.setImageResource(android.R.color.transparent);
                        mLeftItem.setImageResource(R.drawable.lock_btn_app_down_s);
                    }else if(handleEnd > 220){
                        mImgHandle.setImageResource(android.R.color.transparent);
                        mRightItem.setImageResource(R.drawable.lock_btn_unlock_s);
                    }else{
                        mImgHandle.setImageResource(R.drawable.lock_btn_center_p);
                        mLeftItem.setImageResource(R.drawable.lock_btn_app_down_p);
                        mRightItem.setImageResource(R.drawable.lock_btn_unlock_p);
                    }
                    return true;
                case (MotionEvent.ACTION_UP):
                    mImgHandle.setImageResource(R.drawable.lock_btn_center_n);
                    mLeftItem.setImageResource(R.drawable.lock_btn_app_down_n);
                    mRightItem.setImageResource(R.drawable.lock_btn_unlock_n);


                    if(handleEnd < -220){
                        dispatchTriggerEvent(0);
                        Animation resetAni = new TranslateAnimation(-200, 0, 0, 0);
                        resetAni.setDuration(100);
                        resetAni.setFillAfter(false);
                        mImgHandle.startAnimation(resetAni);
                    }else if (handleEnd > 220){
                        dispatchTriggerEvent(1);
                        Animation resetAni = new TranslateAnimation(200, 0, 0, 0);
                        resetAni.setDuration(100);
                        resetAni.setFillAfter(false);
                        mImgHandle.startAnimation(resetAni);
                    }else{
                        Animation resetAni = new TranslateAnimation(handleEnd, 0, 0, 0);
                        resetAni.setDuration(100);
                        resetAni.setFillAfter(false);
                        mImgHandle.startAnimation(resetAni);
                    }
                    return true;
                default:
                    return true;
            }
        }
    };


}
