package com.example.duelmultiplayergame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SetUpConnection extends Activity implements View.OnClickListener {

    Button btnBackConnect;
    Intent intent = null;

    // btn control
    Button btnPlayonOnline, btnPlayonOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_connection);

        btnBackConnect = (Button)this.findViewById(R.id.btnBackConnect);

        btnPlayonOnline = (Button)this.findViewById(R.id.btnPlayonOnline);
        btnPlayonOffline = (Button)this.findViewById(R.id.btnPlayonOffline);

        btnPlayonOffline.setOnClickListener(this);
        btnPlayonOnline.setOnClickListener(this);
        btnBackConnect.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle.getInt("CodeGame") == Constant_Name_Game.CARO.getValue()){
            Toast.makeText(this,"Caro", Toast.LENGTH_SHORT).show();
            intent = new Intent(SetUpConnection.this, GamePlay.class);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBackConnect:
                SetUpConnection.super.onBackPressed();
                break;
            case R.id.btnPlayonOffline:
                if (intent != null){
                    startActivity(intent);
                }
                else {
                }
                break;
            case R.id.btnPlayonOnline:
                // connect
                break;
        }
    }
}