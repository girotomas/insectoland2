package com.tomasgiro.datademo.logindemo3;

import android.*;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Trace;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

import static com.tomasgiro.datademo.logindemo3.ImageSee.verifyStoragePermissions;

/**
 * Created by Densebuis on 24/03/18.
 */

public class storeImage extends AsyncTask<Void,Void,Void>{

    private final Activity activity;
    private String uid;
    private String nomEspece;
    private String imageCode;
    public storeImage(Activity activity, String uid, String nomEspece, String imageCode){
        super();
        this.activity= activity;
        this.uid= uid;
        this.nomEspece = nomEspece;
        this.imageCode= imageCode;




    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.i("me: ", "CALLING ------- STOREIMAGE ------_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_-------------------------------------------------------------------------------*_");
        verifyStoragePermissions(activity);

        final Context context = activity.getApplicationContext();
        FirebaseStorage.getInstance().getReference().child("a_imagesUtilisateurs").child(uid).child(nomEspece).child(imageCode).getBytes(1024 * 1024 * 3).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i("me: ","onsucces");


                Bitmap icon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "title");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                verifyStoragePermissions(activity);

                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);


                OutputStream outstream;
                try {
                    outstream = context.getContentResolver().openOutputStream(uri);
                    icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                    outstream.close();
                } catch (Exception e) {
                    System.err.println(e.toString());

                }
                Log.i("me: ", String.valueOf(isExternalStorageWritable()));
                if (isExternalStorageWritable()) {
                    File fileFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File folder = new File(fileFolder, "Insectoland");
                    File subFolder = new File(folder, nomEspece);
                    subFolder.mkdirs();
                    File file = new File(subFolder, imageCode+".jpg");


                    FileOutputStream outputStream;

                    try {
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                        outputStream.close();
                        Log.i("me: ","sauvegardé");

                    } catch (Exception e) {
                        Log.i("me: ",e.getStackTrace().toString());
                        Log.i("me: ",e.getLocalizedMessage());
                    }

                    final MediaScannerConnection connection = new MediaScannerConnection(context.getApplicationContext(), null);
                    connection.connect();
                    connection.scanFile(context.getApplicationContext(), new String[]{file.getPath()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                        @Override
                        public void onMediaScannerConnected() {
                            Log.i("me:","connected to MediaShare");

                        }

                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            connection.disconnect();
                            Log.i("","done");
                        }

                    });

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("me: ", e.getStackTrace().toString());
                Log.i("me: ", e.getLocalizedMessage());
                Log.i("me: ", e.toString());
                for(StackTraceElement trace : e.getStackTrace()){
                    Log.i("me: ",trace.toString());
                }
            }
        });
        return null;
    }
}
