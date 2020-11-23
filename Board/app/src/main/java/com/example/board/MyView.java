package com.example.board;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

public class MyView extends AppCompatButton {

    public interface OnToggledListener {
        void OnToggled(MyView v, boolean touchOn);
    }

    boolean touchOn;
    boolean player;
    boolean mDownTouch = false;
    private OnToggledListener toggledListener;
    int idX = 0; //default
    int idY = 0; //default

    public MyView(Context context, int x, int y) {
        super(context);
        idX = x;
        idY = y;
        init();
    }

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public boolean getToggle() {
        return touchOn;
    }
    public boolean getPlayer() {
        return player;
    }

    public void setOn(boolean toggled, boolean player){
        this.player = player;
        touchOn = toggled;
    }

    private void init() {
        this.setBackgroundColor(Color.GRAY);
        touchOn = false;
        this.setOnClickListener(new OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                touchOn = !touchOn;
                invalidate();

//                if(toggledListener != null){
//                    toggledListener.OnToggled(this, touchOn);
//                }

                mDownTouch = true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (touchOn) {
            if (player == true)
                canvas.drawColor(Color.RED);
            else{
                canvas.drawColor(Color.BLUE);

            }
        } else {
            //do nothing
        }
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        super.onTouchEvent(event);
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
////                touchOn = !touchOn;
////                invalidate();
////                if (toggledListener != null) {
////                    toggledListener.OnToggled(this, touchOn);
////                }
////                mDownTouch = true;
//                return true;
//            case MotionEvent.ACTION_UP:
//                touchOn = !touchOn;
//                invalidate();
//                if (toggledListener != null) {
//                    toggledListener.OnToggled(this, touchOn);
//                }
//                mDownTouch = true;
//                if (mDownTouch) {
//                    mDownTouch = false;
//                    performClick();
//                    return true;
//                }
//        }
//        return false;
//    }


    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void setOnToggledListener(OnToggledListener listener){
        toggledListener = listener;
    }

    //Position
    public int getIdX(){
        return idX;
    }

    public int getIdY(){
        return idY;
    }

}