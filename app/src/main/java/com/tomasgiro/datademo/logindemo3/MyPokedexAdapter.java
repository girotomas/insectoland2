package com.tomasgiro.datademo.logindemo3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.internal.AnalyticsEvents;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomas on 10/03/2018.
 */


public class MyPokedexAdapter extends RecyclerView.Adapter<MyPokedexAdapter.ViewHolder> {
    private Activity mActivity;
    private String[][] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout mConstraintLayout;
        public TextView title;
        public TextView text;
        private FrameLayout textFrameLayout;
        private Button button;
        private ImageView imageView;
        private Button mesPhotos;
        private TextView grosTitre;
        public ViewHolder(ConstraintLayout mConstraintLayout) {
            super(mConstraintLayout);
            this.mConstraintLayout = mConstraintLayout;
            this.textFrameLayout = (FrameLayout) mConstraintLayout.getChildAt(1);
            this.imageView = (ImageView) ((FrameLayout) mConstraintLayout.getChildAt(0)).getChildAt(0);
            this.title= (TextView)  textFrameLayout.getChildAt(0);
            this.button =(Button) mConstraintLayout.getChildAt(2);
            this.mesPhotos = (Button) mConstraintLayout.getChildAt(3);
            this.grosTitre = (TextView) mConstraintLayout.getChildAt(4);



        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyPokedexAdapter(String[][] myDataset, Activity activity) {
        mDataset = myDataset;
        mActivity =activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyPokedexAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_pokedex_text_view, parent, false);

        ViewHolder vh = new ViewHolder(constraintLayout);
        vh.mConstraintLayout.setVisibility(View.INVISIBLE);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mConstraintLayout.setVisibility(View.INVISIBLE);
        final TextView title=(TextView) holder.title;
        final TextView text =(TextView) holder.text;
        holder.imageView.setImageDrawable(null);


        Pattern pattern =  Pattern.compile("(.*)<(.*)>.*");
        Matcher matcher = pattern.matcher(mDataset[0][position]);
        matcher.find();
        if(matcher.matches()) {
            title.setText(matcher.group(2));
            holder.grosTitre.setText(matcher.group(1));
            holder.button.setVisibility(View.VISIBLE);
            Log.i("hi", matcher.group(0));
            Log.i("hi", matcher.group(1));
            Log.i("hi", matcher.group(2));
        } else {
            holder.grosTitre.setText(mDataset[0][position]);
            holder.button.setVisibility(View.INVISIBLE);

            title.setText("");
        }

        Log.i("hi:","text set to: "+mDataset[0][position]);
        //text.setText("Infos: "+mDataset[1][position]);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),ShowWiki.class);
                intent.putExtra("nomEspece", mDataset[0][position]);
                Bundle bundle= new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,"call wiki");
                bundle.putString(FirebaseAnalytics.Param.CONTENT,mDataset[0][position]);
                FirebaseAnalytics.getInstance(view.getContext()).logEvent("call_wiki",bundle);
                view.getContext().startActivity(intent);

            }
        });

        holder.mesPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),Gallerie.class);
                intent.putExtra("nomEspece",mDataset[0][position]);
                Bundle bundle= new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,"see Gallery");
                bundle.putString(FirebaseAnalytics.Param.CONTENT,mDataset[0][position]);
                FirebaseAnalytics.getInstance(v.getContext()).logEvent("see_Gallery",bundle);
                v.getContext().startActivity(intent);
            }
        });
        boolean pass =false;
        String nomEspece = mDataset[0][position];
        File file = new File(holder.imageView.getContext().getExternalFilesDir("myList"),nomEspece+".jpg");
        if(file.exists()){
            holder.imageView.setImageDrawable(Drawable.createFromPath(file.getPath()));
            pass = true;
        }
        file = new File(holder.imageView.getContext().getExternalFilesDir("myList"),nomEspece+".png");
        if(file.exists()){
            holder.imageView.setImageDrawable(Drawable.createFromPath(file.getPath()));
            pass = true;
        }


        if (!pass) {
            try {
                StorageReference storageReferenceJPG = FirebaseStorage.getInstance().getReference().child("img_" + mDataset[0][position] + "0_" + ".jpg");
                Log.i("hi: ", "img_" + mDataset[0] + "0_" + ".jpg");
                storageReferenceJPG.getBytes(1024 * 1024 * 3).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {

                StorageReference storageReferencePNG = FirebaseStorage.getInstance().getReference().child("img_" + mDataset[0][position] + "0_" + ".png");
                Log.i("hi: ", "img_" + mDataset[0] + "0_" + ".png");

                storageReferencePNG.getBytes(1024 * 1024 * 3).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            holder.mConstraintLayout.setVisibility(View.VISIBLE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset[0].length;
    }
}