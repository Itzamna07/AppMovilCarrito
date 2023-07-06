package com.example.embebidos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    Button btn1, btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abririnterfaz1();
            };
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abririnterfaz2();
            };
        });
    }

    public void abririnterfaz1(){
        Intent siguiente = new Intent(Menu.this,MainActivity.class);
        startActivity(siguiente);
        finish();
    }

    public void abririnterfaz2(){
        Intent siguiente2 = new Intent(Menu.this,Giroscopio.class);
        startActivity(siguiente2);
        finish();
    }
}