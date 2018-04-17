package com.tomasgiro.datademo.logindemo3;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class Gallerie extends AppCompatActivity {
    String uid;
    String nomEspece;
    ArrayList<String> imageReferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallerie);

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        nomEspece = getIntent().getStringExtra("nomEspece");

        final GridView gridview = (GridView) findViewById(R.id.gridview);

        imageReferences = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users").document(uid).collection("myPictures").document(nomEspece).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                Map<String, Object> map = documentSnapshot.getData();

                for (String number : map.keySet()){
                    imageReferences.add(number);
                    Log.i("hi",number);
                }


                gridview.setAdapter(new ImageAdapter());
                Log.i("size array", String.valueOf(imageReferences.size()));


            }
        });









    }

    private class ImageAdapter extends BaseAdapter {

        @Override
        public boolean isEmpty() {
            return imageReferences.isEmpty();
        }

        public ImageAdapter(){
            super();
        }

        @Override
        public int getCount() {
            return imageReferences.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ConstraintLayout constraintLayout = (ConstraintLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.gallery_image,parent,false);


            final ImageView myImageView= (ImageView) constraintLayout.getChildAt(0);


            final StorageReference var = FirebaseStorage.getInstance().getReference().child("a_imagesUtilisateurs").child(uid).child(nomEspece).child(imageReferences.get(position));
            FirebaseStorage.getInstance().getReference().child("a_imagesUtilisateurs").child(uid).child(nomEspece).child(imageReferences.get(position)).getBytes(1024*1024*3).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize=16;


                    myImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length, options));

                    myImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(),ImageSee.class);
                            intent.putExtra("uid",uid);
                            intent.putExtra("nomEspece",nomEspece);
                            intent.putExtra("imageCode",imageReferences.get(position));
                            startActivity(intent);
                        }
                    });
                }
            });

            return constraintLayout;


        }
    }
}
