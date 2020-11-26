package com.example.board;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ScaleGestureDetector mScaleGestureDetector;
    int pWidth;
    int pHeight;
    int totalTurns = 0;
    int numOfCol;
    int numOfRow;
    ChessSquare currentSquare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myGridLayout = (GridLayout)findViewById(R.id.myGrid);
        myGridLayout.setUseDefaultMargins(false);
        myGridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        myGridLayout.setRowOrderPreserved(false);
        numOfCol = myGridLayout.getColumnCount();
        numOfRow = myGridLayout.getRowCount();
        myViews = new MyView[numOfCol*numOfRow];

        for(int yPos=0; yPos<numOfRow; yPos++){
            for(int xPos=0; xPos<numOfCol; xPos++){
                final MyView tView = new MyView(myGridLayout.getContext(), xPos, yPos);
                tView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tView.getToggle() == false){
                            int player;
                            if (totalTurns % 2 == 0) {
                                player = 1;
                            }
                            else {
                                player = 2;
                            }
                            boolean isToggled = true;

                            if(currentSquare != null) {
                                myViews[currentSquare.idY*numOfCol + currentSquare.idX].setOffClicking();
                            }

                            currentSquare = new ChessSquare(isToggled, player, tView.getIdX(),tView.getIdY());
                            tView.setOn(isToggled, player);

                            int status = checkWinner(tView.getIdX(),tView.getIdY());

                            if (status!=0){
                                resultHandler(status);
                            }
                            totalTurns++;
                        }
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
        myGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
                @Override
                public void onGlobalLayout() {

                    final int MARGIN = 1;

                    pWidth = myGridLayout.getWidth();
                    pHeight = myGridLayout.getHeight();

//                    myGridLayout.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            GridLayout.LayoutParams mParam;
//                            mParam = (GridLayout.LayoutParams) myGridLayout.getLayoutParams();
//                            mParam.height=pWidth;
//                            myGridLayout.setLayoutParams(mParam);
//                            myGridLayout.postInvalidate();
//                        }
//                    });


                    int numOfCol = myGridLayout.getColumnCount();
                    int numOfRow = myGridLayout.getRowCount();
                    int w = pWidth/numOfCol;
                    int h = pHeight/numOfRow;

                    for(int yPos=0; yPos<numOfRow; yPos++){
                        for(int xPos=0; xPos<numOfCol; xPos++){
                            GridLayout.LayoutParams params =
                                    (GridLayout.LayoutParams)myViews[yPos*numOfCol + xPos].getLayoutParams();
//                            params.width = w - 2*MARGIN;
//                            params.height = h - 2*MARGIN;
                            //params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                            params.width = pWidth/numOfCol;
                            params.height = pWidth/numOfRow;
                            myViews[yPos*numOfCol + xPos].setLayoutParams(params);
                        }
                    }
                }
            }
        );


    }

//    private List<String> getStringData() {
//        List<String> btn = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            btn.add(String.valueOf(i));
//        }
//        return btn;
//    }

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

    private int checkWinner(int currentPosX, int currentPosY)
    {
        int status = 0;
        int check;

        //Create check board
        ArrayList<ChessSquare> tempSquares;
        ChessSquare[][] myBoard = new ChessSquare[numOfRow][numOfCol];
        for(int yPos = 0; yPos < numOfRow; yPos++) {
            for (int xPos = 0; xPos < numOfCol; xPos++) {
                myBoard[yPos][xPos] = new ChessSquare(myViews[yPos*numOfCol + xPos].getToggle(),myViews[yPos*numOfCol + xPos].getPlayer(), xPos, yPos);
            }
        }

        /////////////////////////
        //set player to check
        if(myBoard[currentPosY][currentPosX].player == 1)
        {
            status = 1;
        }
        else if (myBoard[currentPosY][currentPosX].player == 2)
        {
            status = 2;
        }

        ////////////////////////////////////
        //check horizontal

        tempSquares = new ArrayList<ChessSquare>();
        tempSquares.add(myBoard[currentPosY][currentPosX]);

        int countLeft = 0;
        int countRight = 0;
        int posLeft = currentPosX;
        int posRight = currentPosX;

        for (int i = currentPosX - 1; i >= 0; i--) {
            if (myBoard[currentPosY][i].touchOn == true &&
                    myBoard[currentPosY][i].player == myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[currentPosY][i]);
                posLeft--;
                countLeft++;
            }
            else{
                break;
            }
        }

        for (int i = currentPosX + 1; i < numOfCol; i++) {
            if (myBoard[currentPosY][i].touchOn == true &&
                    myBoard[currentPosY][i].player == myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[currentPosY][i]);
                posRight++;
                countRight++;
            }
            else{
                break;
            }
        }

        check = countLeft + countRight + 1;
        if(check > 5){
            setWinnerSquares(tempSquares);
            return status;
        }
        else if((countLeft + countRight + 1 == 5) && !(
                ((myBoard[currentPosY][posLeft - 1].player!=myBoard[currentPosY][currentPosX].player) && (posLeft - 1 >= 0) && (myBoard[currentPosY][posLeft - 1].touchOn))
                && ((myBoard[currentPosY][posRight + 1].player!=myBoard[currentPosY][currentPosX].player) && (posRight + 1 < numOfCol) && (myBoard[currentPosY][posRight + 1].touchOn))
        )) {
            setWinnerSquares(tempSquares);
            return status;
        }
        tempSquares.clear();

        ////////////////////////
        //check vertical

        tempSquares.add(myBoard[currentPosY][currentPosX]);

        int countTop = 0;
        int countBottom = 0;
        int posTop = currentPosX;
        int posBottom = currentPosX;

        for (int i = currentPosY - 1; i >= 0; i--) {
            if (myBoard[i][currentPosX].touchOn == true &&
                    myBoard[i][currentPosX].player==myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[i][currentPosX]);
                posTop--;
                countTop++;
            }
            else{
                break;
            }
        }

        for (int i = currentPosY + 1; i < numOfRow; i++) {
            if (myBoard[i][currentPosX].touchOn == true &&
                    myBoard[i][currentPosX].player==myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[i][currentPosX]);
                posBottom++;
                countBottom++;
            }
            else{
                break;
            }
        }

        check = countTop + countBottom + 1;
        if(check > 5){
            setWinnerSquares(tempSquares);
            return status;
        }
        else if((check == 5) && !(
                ((myBoard[posTop - 1][currentPosX].player!=myBoard[currentPosY][currentPosX].player) && (posTop - 1 >= 0) && (myBoard[posTop - 1][currentPosX].touchOn == true))
                        && ((myBoard[posBottom + 1][currentPosX].player!=myBoard[currentPosY][currentPosX].player) && (posBottom + 1 < numOfCol) && (myBoard[posBottom + 1][currentPosX].touchOn == true))
        )) {
            setWinnerSquares(tempSquares);
            return status;
        }

        tempSquares.clear();

        ///////////////////////////////
        //Check Primary Diagonal

        tempSquares.add(myBoard[currentPosY][currentPosX]);

        int countPriPrev = 0;
        int countPriAft = 0;
        int posPrevX = currentPosX;
        int posPrevY = currentPosY;
        int posAftX = currentPosX;
        int posAftY = currentPosY;


        for (int i = numOfRow - 1; i >= 0; i--) {
            if((posPrevX - 1 < 0) || (posPrevY - 1 < 0)) {
                break;
            }
            if (myBoard[posPrevY - 1][posPrevX - 1].touchOn == true &&
                    myBoard[posPrevY - 1][posPrevX - 1].player == myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[posPrevY - 1][posPrevX - 1]);
                posPrevX--;
                posPrevY--;
                countPriPrev++;
            }
            else{
                break;
            }
        }

        for (int i = 0; i < numOfRow; i++) {
            if((posAftX + 1 >= numOfCol) || (posAftY + 1 >= numOfRow)) {
                break;
            }
            if (myBoard[posAftY + 1][posAftX + 1].touchOn == true &&
                    myBoard[posAftY + 1][posAftX + 1].player==myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[posAftY + 1][posAftX + 1]);
                posAftX++;
                posAftY++;
                countPriAft++;
            }
            else{
                break;
            }
        }

        check = countPriPrev + countPriAft + 1;
        if(check > 5){
            setWinnerSquares(tempSquares);
            return status;
        }
        else if(check == 5)
        {
            if((posPrevX - 1 < 0) || (posPrevY - 1 < 0) || (posAftX + 1 >= numOfCol) || (posAftY + 1 >= numOfRow)) {
                setWinnerSquares(tempSquares);
                return status;
            }
            else {
                if(!(myBoard[posPrevY - 1][posPrevX - 1].touchOn) || !(myBoard[posAftY + 1][posAftX + 1].touchOn)){
                    setWinnerSquares(tempSquares);
                    return status;
                }
                else {
                    if(!((myBoard[posPrevY - 1][posPrevX - 1].player != myBoard[currentPosY][currentPosX].player) && (myBoard[posAftY + 1][posAftX + 1].player != myBoard[currentPosY][currentPosX].player))){
                        setWinnerSquares(tempSquares);
                        return status;
                    }
                }
            }
        }
        tempSquares.clear();

        /////////////////////////////////
        //Check Sub Diagonal

        tempSquares.add(myBoard[currentPosY][currentPosX]);

        int countSubPrev = 0;
        int countSubAft = 0;
        posPrevX = currentPosX;
        posPrevY = currentPosY;
        posAftX = currentPosX;
        posAftY = currentPosY;


        for (int i = numOfRow; i >= 0; i--) {
            if((posPrevX - 1 < 0) || (posPrevY + 1 >= numOfRow)) {
                break;
            }
            if (myBoard[posPrevY + 1][posPrevX - 1].touchOn == true &&
                    myBoard[posPrevY + 1][posPrevX - 1].player==myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[posPrevY + 1][posPrevX - 1]);
                posPrevY++;
                posPrevX--;
                countSubPrev++;
            }
            else{
                break;
            }
        }

        for (int i = 0; i < numOfRow; i++) {
            if((posAftX + 1 >= numOfCol) || (posAftY - 1 < 0)) {
                break;
            }
            if (myBoard[posAftY - 1][posAftX + 1].touchOn == true &&
                    myBoard[posAftY - 1][posAftX + 1].player == myBoard[currentPosY][currentPosX].player) {
                tempSquares.add(myBoard[posAftY - 1][posAftX + 1]);
                posAftY--;
                posAftX++;
                countSubAft++;
            }
            else{
                break;
            }
        }

        check = countSubPrev + countSubAft + 1;
        if(check > 5){
            setWinnerSquares(tempSquares);
            return status;
        }
        else if(check == 5)
        {
            if((posPrevX - 1 < 0) || (posPrevY + 1 >= numOfRow) || (posAftX + 1 >= numOfCol) || (posAftY - 1 < 0)) {
                setWinnerSquares(tempSquares);
                return status;
            }
            else {
                if(!(myBoard[posPrevY + 1][posPrevX - 1].touchOn) || !(myBoard[posAftY - 1][posAftX + 1].touchOn)){
                    setWinnerSquares(tempSquares);
                    return status;
                }
                else {
                    if(!((myBoard[posPrevY + 1][posPrevX - 1].player != myBoard[currentPosY][currentPosX].player) && (myBoard[posAftY - 1][posAftX + 1].player != myBoard[currentPosY][currentPosX].player))){
                        setWinnerSquares(tempSquares);
                        return status;
                    }
                }
            }
        }

        tempSquares.clear();


        status = 0;
        return status;
    }

    private void setWinnerSquares(List<ChessSquare> tempSquares)
    {
        for(int i = 0; i < tempSquares.size(); i++) {
            ChessSquare temp = tempSquares.get(i);
            myViews[temp.getIdY()*numOfCol + temp.getIdX()].setWinnerOn();
        }
    }

    private void resultHandler(int winner)
    {
        if(winner == 1){
            Toast.makeText(this, "P1 WIN", Toast.LENGTH_SHORT).show();
        }
        else if(winner == 2){
            Toast.makeText(this, "P2 WIN", Toast.LENGTH_SHORT).show();
        }
    }

//    private boolean isEndHorizontal(int currentPosX)
//    {
//
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}