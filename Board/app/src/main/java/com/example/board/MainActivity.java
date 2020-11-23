package com.example.board;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    GridView gridView;
    private ScaleGestureDetector mScaleGestureDetector;
    boolean player = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myGridLayout = (GridLayout)findViewById(R.id.mygrid);
        myGridLayout.setUseDefaultMargins(false);
        myGridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        myGridLayout.setRowOrderPreserved(false);
        int numOfCol = myGridLayout.getColumnCount();
        int numOfRow = myGridLayout.getRowCount();
        myViews = new MyView[numOfCol*numOfRow];
        for(int yPos=0; yPos<numOfRow; yPos++){
            for(int xPos=0; xPos<numOfCol; xPos++){
                final MyView tView = new MyView(myGridLayout.getContext(), xPos, yPos);
                tView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isToggled = !tView.getToggle();
                        tView.setOn(isToggled, player);
                        player = !player;
                    }
                });
                //tView.setOnToggledListener(this);
                //tView.setOnToggledListener(null);
//                tView.setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    //On click function
//                    public void onClick(View view) {
//                        //Create the intent to start another activity
//                        boolean isToggled = !tView.getToggle();
//                        tView.setOn(isToggled, player);
//                        player = !player;
////                invalidate();
////
////                if(toggledListener != null){
////                    toggledListener.OnToggled(this, touchOn);
////                }
////
////                mDownTouch = true;
//                    }
//                });

                myViews[yPos*numOfCol + xPos] = tView;

                myGridLayout.addView(tView);
            }
        }
        myGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){
                @Override
                public void onGlobalLayout() {

                    final int MARGIN = 1;

                    int pWidth = myGridLayout.getWidth();
                    int pHeight = myGridLayout.getHeight();
                    int numOfCol = myGridLayout.getColumnCount();
                    int numOfRow = myGridLayout.getRowCount();
                    int w = pWidth/numOfCol;
                    int h = pHeight/numOfRow;

                    for(int yPos=0; yPos<numOfRow; yPos++){
                        for(int xPos=0; xPos<numOfCol; xPos++){
                            GridLayout.LayoutParams params =
                                    (GridLayout.LayoutParams)myViews[yPos*numOfCol + xPos].getLayoutParams();
                            params.width = w - 2*MARGIN;
                            params.height = h - 2*MARGIN;
                            params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                            myViews[yPos*numOfCol + xPos].setLayoutParams(params);
                        }
                    }
                }
            }
        );

    }

    private List<String> getStringData() {
        List<String> btn = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            btn.add(String.valueOf(i));
        }
        return btn;
    }

    MyView[] myViews;

    GridLayout myGridLayout;

//    boolean d = false;
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                d = false;
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                d = true;
//                break;
//            default:
//                break;
//        }
//
//        return false;
//    }
//
//    @Override
//    public void OnToggled(MyView v, boolean touchOn) {
//    if (d==false){
//
//
//
//        //get the id string
//        String idString = v.getIdX() + ":" + v.getIdY();
//
//        Toast.makeText(MainActivity.this,
//                "Toogled:\n" +
//                        idString + "\n" +
//                        touchOn,
//                Toast.LENGTH_SHORT).show();
//    }}

}