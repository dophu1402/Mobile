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

    // btn control
    Button btnPlayonOnline, btnPlayonBluetooth, btnPlayonOffline, btnPlayonWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_connection);

        btnBackConnect = (Button)this.findViewById(R.id.btnBackConnect);

        btnPlayonBluetooth = (Button)this.findViewById(R.id.btnPlayonBluetooth);
        btnPlayonWifi = (Button)this.findViewById(R.id.btnPlayonWifi);
        btnPlayonOnline = (Button)this.findViewById(R.id.btnPlayonOnline);
        btnPlayonOffline = (Button)this.findViewById(R.id.btnPlayonOffline);

        btnPlayonOffline.setOnClickListener(this);
        btnPlayonOnline.setOnClickListener(this);
        btnPlayonWifi.setOnClickListener(this);
        btnPlayonBluetooth.setOnClickListener(this);
        btnBackConnect.setOnClickListener(this);

        CustomButtonControl.visibilityGone(btnPlayonBluetooth);
        CustomButtonControl.visibilityGone(btnPlayonWifi);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle.getInt("CodeGame") == Constant_Name_Game.CARO.getValue()){
            Toast.makeText(this,"Caro", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBackConnect:
                if (CustomButtonControl.isVisibility(btnPlayonOnline)){
                    SetUpConnection.super.onBackPressed();
                }
                else {
                    CustomButtonControl.visibilityGone(btnPlayonWifi);
                    CustomButtonControl.visibilityGone(btnPlayonBluetooth);
                    CustomButtonControl.visibilityVisible(btnPlayonOnline);
                    CustomButtonControl.visibilityVisible(btnPlayonOffline);
                }
                break;
            case R.id.btnPlayonOffline:
                startActivity(new Intent(SetUpConnection.this, GamePlay.class));
                break;
            case R.id.btnPlayonOnline:
                CustomButtonControl.visibilityGone(btnPlayonOnline);
                CustomButtonControl.visibilityGone(btnPlayonOffline);
                CustomButtonControl.visibilityVisible(btnPlayonWifi);
                CustomButtonControl.visibilityVisible(btnPlayonBluetooth);
                break;
            case R.id.btnPlayonWifi:
                CustomButtonControl.visibilityGone(btnPlayonWifi);
                CustomButtonControl.visibilityGone(btnPlayonBluetooth);
                CustomButtonControl.visibilityVisible(btnPlayonOnline);
                CustomButtonControl.visibilityVisible(btnPlayonOffline);
                break;
            case R.id.btnPlayonBluetooth:
                CustomButtonControl.visibilityGone(btnPlayonWifi);
                CustomButtonControl.visibilityGone(btnPlayonBluetooth);
                CustomButtonControl.visibilityVisible(btnPlayonOnline);
                CustomButtonControl.visibilityVisible(btnPlayonOffline);
                break;
        }
    }
}