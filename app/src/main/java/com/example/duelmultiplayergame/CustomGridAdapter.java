package com.example.duelmultiplayergame;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomGridAdapter extends BaseAdapter {
    private List<String> listBtn;
    private LayoutInflater layoutInflater;
    private Context context;
    public CustomGridAdapter(Context context, List<String> listBtn){
        this.context = context;
        this.listBtn = listBtn;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return listBtn.size();
    }

    @Override
    public Object getItem(int position){
        return listBtn.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_layout, null);




            holder = (TextView) convertView.findViewById(R.id.text);


            convertView.setTag(holder);
        } else {
            holder = (TextView) convertView.getTag();
        }

        //holder.setText(this.listBtn.get(position));
        holder.setText("");
        DisplayMetrics metrics = holder.getResources().getDisplayMetrics();
        holder.setHeight(metrics.widthPixels);

        return convertView;
    }
}
