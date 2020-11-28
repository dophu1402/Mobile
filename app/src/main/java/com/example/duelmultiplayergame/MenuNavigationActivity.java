package com.example.duelmultiplayergame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MenuNavigationActivity extends Activity {
    ListView listviewMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_navigation);

        listviewMenu  = (ListView)this.findViewById(R.id.listviewMenu);

//        ArrayAdapter<String> arrayAdaptesr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.pro);

        ArrayList database = new GameDataBase().dbList;
        GameMenuAdapter arrayAdapter = new GameMenuAdapter(this, database, R.layout.menu_button_layout);


        listviewMenu.setAdapter(arrayAdapter);

    }
}