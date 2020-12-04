package com.example.duelmultiplayergame;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

public class MyView extends AppCompatButton {

    public interface OnToggledListener {
        void OnToggled(MyView v, boolean touchOn);
    }

    boolean touchOn;
    int player; //player = 0 -> no player, = 1 -> x; = 2 -> o
    boolean mDownTouch = false;
    boolean clicking;
    boolean winner;
    private OnToggledListener toggledListener;
    int idX = 0; //default
    int idY = 0; //default

    public MyView(Context context, int x, int y) {
        super(context);
        idX = x;
        idY = y;
        init();
    }

    public void setTouched() {
        touchOn = true;
    }

    public void reset(){
        touchOn = false;
        player = 0;
        clicking = false;
        winner = false;

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
    public int getPlayer() {
        return player;
    }

    public void setOn(boolean toggled, int p){
        player = p;
        touchOn = toggled;
        clicking = true;
    }

    public void setOffClicking()
    {
        clicking = false;
    }

    public void setWinnerOn() { winner = true; }


    private void init() {
        this.setBackgroundResource(R.drawable.carosquare);
        player = 0;
        touchOn = false;
        clicking = false;
        winner = false;
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
            if(winner) {
                if (player == 1){
                    this.setBackgroundResource(R.drawable.xwin);
                }
                else if(player == 2) {
                    this.setBackgroundResource(R.drawable.owin);
                }
            }
            else if(clicking){
                if (player == 1){
                    this.setBackgroundResource(R.drawable.xonclick);
                }
                else if(player == 2) {
                    this.setBackgroundResource(R.drawable.oonclick);
                }
            }
            else {
                if (player == 1){
                    this.setBackgroundResource(R.drawable.xnormal);
                }
                else if(player == 2) {
                    this.setBackgroundResource(R.drawable.onormal);
                }
            }
            invalidate();
        } else {
            this.setBackgroundResource(R.drawable.carosquare);
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