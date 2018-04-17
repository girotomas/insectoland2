package com.tomasgiro.datademo.logindemo3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class tryReturnJSON extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_return_json);


        Intent intent = new Intent();
        String text = "Result to be returned....";
//---set the data to pass back---
        intent.putExtra("stringResult",text);
        setResult(RESULT_OK, intent);
//---close the activity---
        finish();


    }
}
