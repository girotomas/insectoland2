package com.tomasgiro.datademo.logindemo3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeScreen extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        button =findViewById(R.id.take_a_picture);


    }

    public void onTakeAPicture(View view){
        Intent intent = new Intent(this,mRecyclerView.class);
        intent.putExtra("method", "take");
        startActivity(intent);
    }


    public void uploadPicture(View view){
        Intent intent = new Intent(this,mRecyclerView.class);
        intent.putExtra("method", "upload");
        startActivity(intent);
    }
}
