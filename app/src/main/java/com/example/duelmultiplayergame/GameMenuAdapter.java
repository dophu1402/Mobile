package com.example.duelmultiplayergame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameMenuAdapter extends BaseAdapter {

    Context context; int layoutToBeInflated; ArrayList<GameDataBase.DbRecord> dbList;
    public GameMenuAdapter(Context context, ArrayList<GameDataBase.DbRecord> databaseList, int resource) {
        this.context = context; this.dbList = databaseList; layoutToBeInflated = resource;
    }

    @Override public int getCount() {
        return dbList.size();
    }
    @Override public GameDataBase.DbRecord getItem(int position) {
        return dbList.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutToBeInflated, null);
            holder = new ViewHolder();
            holder.NameGame = (TextView) row.findViewById(R.id.NameGame);
            holder.ImageGame = (ImageView) row.findViewById(R.id.ImageGame);
            holder.BackgroundGame = (LinearLayout)row.findViewById(R.id.BackgroundGame);

            row.setTag(holder);
        } else { // row was already created- no need to inflate and invoke findViewById getTag() returns the object originally stored in this view
            holder = (ViewHolder) row.getTag();
        }
//
        GameDataBase.DbRecord dbRec = getItem(position);
        holder.NameGame.setText(dbRec.nameGame);
        holder.ImageGame.setImageResource(dbRec.wallpaper);
        holder.BackgroundGame.setBackgroundResource(dbRec.color);

        Log.d("STATE", "getView: " + Integer.toString(dbRec.color) );

        row.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(context, SetUpConnection.class);
                Bundle bundle = new Bundle();

                int code = -1;
                if (position == 0){
                    code = Constant_Name_Game.CARO.getValue();
                }
                bundle.putInt("CodeGame", code);

                intent.putExtras(bundle);
                if (code != -1){
                    context.startActivity(intent);
                }
            }
        });

        return row;
    }// getView
    public class ViewHolder {
        TextView NameGame;
        ImageView ImageGame;
        LinearLayout BackgroundGame;
    }
}

