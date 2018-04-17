package com.tomasgiro.datademo.logindemo3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class Messaging extends AppCompatActivity {
    private static final String TAG = "Messaging";
    EditText editText;
    FirebaseFirestore db;
    DocumentReference docRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        editText= findViewById(R.id.messageText);

        // Access a Cloud Firestore instance from your Activity

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("messages").document("mydoc");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    updateUI();


                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    private void updateUI() {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Log.i("hi", "changed something!");
                        Log.i("hi", document.getData().toString());
                        


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    void onSaveMessage(View view){
        Log.i("hi","working!");
        String myText = editText.getText().toString();
        HashMap<String, Object> message = new HashMap<String, Object>();
        message.put(String.valueOf(System.currentTimeMillis()),myText);
        db.collection("messages").document("mydoc").set(message, SetOptions.merge()).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Messaging.this,"succesfully saved !", Toast.LENGTH_LONG);
                    }
                }

        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("hi",e.getMessage());
                        Log.i("hi", "failure!");
                        Toast.makeText(Messaging.this,"Sorry an error occured the message was not saved! ",Toast.LENGTH_SHORT);
                    }
                }
        );
    }



}
