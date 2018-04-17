package com.tomasgiro.datademo.logindemo3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomas on 10/03/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "MyAdapter";
    private final Context context;
    private String[][] mDataset;
    private final Activity activity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout mConstraintLayout;
        public TextView title;
        public TextView resume;
        public ImageView imageView;
        public Button button;
        public ImageView wikiImage;
        public TextView nomLatin;
        public ViewHolder(ConstraintLayout mConstraintLayout) {
            super(mConstraintLayout);
            this.mConstraintLayout = mConstraintLayout;
            this.title = (TextView) mConstraintLayout.getChildAt(1);
            this.resume = (TextView) mConstraintLayout.getChildAt(2);
            this.imageView = (ImageView) mConstraintLayout.getChildAt(0);
            this.button = (Button) mConstraintLayout.getChildAt(3);
            this.wikiImage = (ImageView) mConstraintLayout.getChildAt(4);
            this.nomLatin = (TextView) mConstraintLayout.getChildAt(5);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[][] myDataset, Activity activity) {
        mDataset = myDataset;
        this.activity = activity;
        this.context= activity.getApplicationContext();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        ViewHolder vh = new ViewHolder(constraintLayout);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(position==5) {
            TextView title = (TextView) holder.mConstraintLayout.getChildAt(1);
            title.setText(R.string.opinion);
            final Button confirm = (Button) holder.mConstraintLayout.getChildAt(3);
            confirm.setText(R.string.ask_here_by_email);
            holder.wikiImage.setVisibility(View.INVISIBLE);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"girotomas@gmail.com"});
                    i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.hi_leonard_could_you));
                    i.putExtra(Intent.EXTRA_SUBJECT, "Insectoland");
                    try {
                        activity.startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }


                }

            });
        }else{
            TextView title = (TextView) holder.mConstraintLayout.getChildAt(1);
            title.setText(mDataset[0][position]);
            final TextView resume = (TextView) holder.mConstraintLayout.getChildAt(2);
            resume.setText(context.getString(R.string.probability) + round(Double.parseDouble(mDataset[1][position]), 2));
            final Button confirm = (Button) holder.mConstraintLayout.getChildAt(3);



            Pattern pattern =  Pattern.compile("(.*)<(.*)>.*");
            Matcher matcher = pattern.matcher(mDataset[0][position]);
            matcher.find();
            if(matcher.matches()) {
                title.setText(matcher.group(1));
                holder.nomLatin.setText(matcher.group(2));
                holder.wikiImage.setVisibility(View.VISIBLE);

                Log.i("hi", matcher.group(0));
                Log.i("hi", matcher.group(1));
                Log.i("hi", matcher.group(2));
            } else {
                holder.wikiImage.setVisibility(View.INVISIBLE);

                title.setText(mDataset[0][position]);
                holder.nomLatin.setText("");

            }


            /**
             * confirmation de que c'est la bonne espèce
             */
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    ProgressDialog dialog = ProgressDialog.show(view.getContext(), "Loading",
                            "", true);
                    final Random random = new Random();
                    final int integer = random.nextInt(10000000);


                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(uid).collection("pokedex").document(mDataset[0][position]);










                    FirebaseStorage.getInstance().getReference().child("a_imagesUtilisateurs/"+uid+"/"+mDataset[0][position]+"/"+integer).putBytes(mRecyclerView.getBitmapData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            final Intent intent = new Intent(view.getContext(),MaPokedex.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            FirebaseFirestore.getInstance().collection("users").document(uid).collection("myPictures").document(mDataset[0][position]).set(new HashMap<String, Object>()
                                                                                                                                                           {{
                                                                                                                                                               put(String.valueOf(integer),"1");
                                                                                                                                                           }}
                                    , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {


                                            if(!documentSnapshot.exists()){
                                                documentReference.set(new HashMap<String, Object>() {{
                                                                          put("mDataset[0][position]","1");
                                                                      }}
                                                );
                                                Log.i("hi: ","new species added: "+mDataset[0][position]);
                                            }
                                            else{
                                                Log.i("hi: ","you all ready have the species!");
                                            }
                                            new storeImage(activity,uid,mDataset[0][position],String.valueOf(integer)).doInBackground();
                                            view.getContext().startActivity(intent);

                                        }
                                    });
                                }
                            });


                        }
                    });



                }
            });


            holder.wikiImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("hi","click");
    
                    Intent intent = new Intent(view.getContext(),ShowWiki.class);
                    intent.putExtra("nomEspece",mDataset[0][position]);
                    view.getContext().startActivity(intent);

                }
            });
            Log.i(TAG,"fetching the image from Firebase: "+"img_" + mDataset[0][position] + "0_" + ".jpg");

            StorageReference storageReferenceJPG = FirebaseStorage.getInstance().getReference().child("img_" + mDataset[0][position] + "0_" + ".jpg");
            Log.i("hi: ", "img_" + mDataset[0][position] + "0_" + ".jpg");
            storageReferenceJPG.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                }
            });

            StorageReference storageReferencePNG = FirebaseStorage.getInstance().getReference().child("img_" + mDataset[0][position] + "0_" + ".png");
            Log.i("hi: ", "img_" + mDataset[0] + "0_" + ".png");

            storageReferencePNG.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                }
            });}
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset[0].length+1;
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}