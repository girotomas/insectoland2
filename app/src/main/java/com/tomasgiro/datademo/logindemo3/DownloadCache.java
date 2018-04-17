package com.tomasgiro.datademo.logindemo3;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Densebuis on 24/03/18.
 */

public class DownloadCache extends AsyncTask<Void, Void, Void> {
    private File dir;
    private String [][] myDataset;
    public DownloadCache(File dir,String[][] myDataset){
        this.dir=dir;
        this.myDataset=myDataset;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Log.i("hi: ","beginning of saving");
        dir = new File(Environment.getExternalStorageDirectory(),"myList");
        dir.delete();
        dir.mkdirs();
        final ArrayList<Thread> threadList = new ArrayList<>();
        for ( int integer = 0; integer<myDataset[0].length; integer++) {
            final int value = integer;

            final String nomEspece = myDataset[0][value];
            if (true) {

                threadList.add(

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i("hi: ", "tel. espece" + myDataset[0][value]);

                            StorageReference storageReferenceJPG = FirebaseStorage.getInstance().getReference().child("img_" + myDataset[0][value] + "0_" + ".jpg");
                            Log.i("hi: ", "img_" + myDataset[0][value] + "0_" + ".jpg");
                            storageReferenceJPG.getBytes(1024 * 1024 * 3).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {

                                    MaPokedex.saveFile(bytes, nomEspece, dir,"jpg");
                                }
                            }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                @Override
                                public void onComplete(@NonNull Task<byte[]> task) {
                                    StorageReference storageReferencePNG = FirebaseStorage.getInstance().getReference().child("img_" + myDataset[0][value] + "0_" + ".png");
                                    Log.i("hi: ", "img_" + myDataset[0][value] + "0_" + ".png");

                                    storageReferencePNG.getBytes(1024 * 1024 * 3).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            MaPokedex.saveFile(bytes, nomEspece, dir,"png");


                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                        @Override
                                        public void onComplete(@NonNull Task<byte[]> task) {
                                            if (!threadList.isEmpty()) {
                                                threadList.get(0).run();
                                                threadList.remove(0);
                                            }
                                        }
                                    });

                                }
                            });
                        }

                    }
                ));
            }
        }
        if(!threadList.isEmpty()) {
            threadList.get(0).run();
            threadList.remove(0);
        }
        return null;
    }
}
