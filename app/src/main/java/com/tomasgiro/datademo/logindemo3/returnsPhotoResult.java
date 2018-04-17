package com.tomasgiro.datademo.logindemo3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class returnsPhotoResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_returns_photo_result);


        String text = " {\n" +
                "                                                                                  \"classes\": [\n" +
                "                                                                                    \"Taxon inconnu de la cl\\u00e9\", \n" +
                "                                                                                    \"Les Pi\\u00e9rides <Pieris>\", \n" +
                "                                                                                    \"Les Moustiques, Tipules et autres Dipt\\u00e8res N\\u00e9matoc\\u00e8res\", \n" +
                "                                                                                    \"Les Bourdons (autres) <Bombus>\", \n" +
                "                                                                                    \"Les Bourdons noirs \\u00e0 bande(s) jaune(s) et cul blanc <Bombus>\"\n" +
                "                                                                                  ], \n" +
                "                                                                                  \"probas\": [\n" +
                "                                                                                    0.46689923122489163, \n" +
                "                                                                                    0.03423345481219636, \n" +
                "                                                                                    0.024622683585762674, \n" +
                "                                                                                    0.024218162671743444, \n" +
                "                                                                                    0.017603419485350522\n" +
                "                                                                                  ]\n" +
                "                                                                                }";

        Intent intent = new Intent();
        Log.i("hi: resultSent:" ,text);
//---set the data to pass back---
        intent.putExtra("stringResult",text);
        setResult(RESULT_OK, intent);
//---close the activity---
        finish();
    }
}
