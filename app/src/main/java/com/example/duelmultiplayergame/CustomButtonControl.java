package com.example.duelmultiplayergame;

import android.view.View;
import android.widget.Button;

public class CustomButtonControl {
    public static boolean isVisibility(Button btn){
        return btn.getVisibility() == View.VISIBLE;
    }
    public static void visibilityGone(Button btn){
        btn.setVisibility(View.GONE);
    }
    public static void visibilityVisible(Button btn){
        btn.setVisibility(View.VISIBLE);
    }
    public static void visibilityInvisible(Button btn){
        btn.setVisibility(View.INVISIBLE);
    }
    public static void toggleCollapsed(Button btn){
        if (CustomButtonControl.isVisibility(btn)){
            CustomButtonControl.visibilityGone(btn);
        }
        else {
            CustomButtonControl.visibilityVisible(btn);
        }
    }
    public static void toggleHided(Button btn){
        if (CustomButtonControl.isVisibility(btn)){
            CustomButtonControl.visibilityGone(btn);
        }
        else {
            CustomButtonControl.visibilityVisible(btn);
        }
    }
}
