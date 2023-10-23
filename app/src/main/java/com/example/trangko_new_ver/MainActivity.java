package com.example.trangko_new_ver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    sleep(3000);
                    startActivity(new Intent(MainActivity.this, RegisterPage.class));
                    finish();
                }catch (Exception e){
                }
            }
        }; thread.start();
    }
}