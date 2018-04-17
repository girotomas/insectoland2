package com.tomasgiro.datademo.logindemo3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WhyUsage extends AppCompatActivity {
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_why_usage);
        editText= (EditText) findViewById(R.id.editText2);
        Crashlytics.getInstance().crash();

    }

    public void onClick(View view){
        Map<String, Object> myMap= new HashMap<String, Object>();
        Map<String,Object> mySecMap= new HashMap();
        mySecMap.put("user",FirebaseAuth.getInstance().getCurrentUser().getEmail().intern());
        mySecMap.put("message",editText.getText().toString());
        mySecMap.put("timestamp", System.currentTimeMillis());

        myMap.
            put(String.valueOf(new Date(System.currentTimeMillis())),mySecMap );

        FirebaseFirestore.getInstance().collection("comments2").document("comments2").set(myMap, SetOptions.merge()

                ).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getApplicationContext(), MaPokedex.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }
        );
    }
}
