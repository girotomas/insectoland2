package com.tomasgiro.datademo.logindemo3;

import android.*;
import android.Manifest;
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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.content.FileProvider.getUriForFile;


public class ImageSee extends AppCompatActivity {
    private static final String TAG ="" ;
    ShareActionProvider mShareActionProvider;
    Intent mIntent;
    byte[] myBytes;
    String uid;
    String nomEspece;
    String imageCode;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_see);



        image = (ImageView) findViewById(R.id.imageView4);
        uid = getIntent().getStringExtra("uid");
        nomEspece = getIntent().getStringExtra("nomEspece");
        imageCode = getIntent().getStringExtra("imageCode");



        FirebaseStorage.getInstance().getReference().child("a_imagesUtilisateurs").child(uid).child(nomEspece).child(imageCode).getBytes(1024 * 1024 * 3).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                myBytes =bytes;

            }
        });





/**

                String albumName = "insectoland";





                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "title");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        values);





                Log.i("hi",Environment.getExternalStorageState());
                File imageFolder = new File(Environment.getExternalStorageDirectory(),"dir");
                imageFolder.mkdirs();
                File newFile =   new File(imageFolder,"pics.jpg"); //new File(imagePath, "image");
                Log.i("hi: ",newFile.getAbsolutePath());
                //imagePath.mkdirs();
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("hi ", String.valueOf(newFile.exists())+newFile.isFile()+

                newFile.canWrite());

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                    Log.i("hi", String.valueOf(fileOutputStream.getChannel().position()));
                    fileOutputStream.write(bytes);
                    Log.i("hi", String.valueOf(fileOutputStream.getChannel().position()));
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri contentUri = FileProvider.getUriForFile(image.getContext(), "com.tomasgiro.datademo.logindemo3.fileprovider",newFile);
                Log.i("hi",contentUri.getPath());
                try {
                    Log.i("hi",newFile.getCanonicalPath());
                    Log.i("hi",newFile.getPath());
                    Log.i("hi",newFile.getAbsolutePath());
                    Log.i("hi",String.valueOf(fileList()));
                    for (String string : fileList()){
                        Log.i("hi",string);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );

                intent.setDataAndType(contentUri,getContentResolver().getType(contentUri));


                // setShareIntent(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
                mIntent=intent;


*/




        }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.share);


        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.see_image_menu, menu);
        // Locate MenuItem with ShareActionProvider

        // Fetch and store ShareActionProvider

        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share:
                doIntent();
                return true;
            case R.id.delete:
                delete();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delete() {
        Log.i("hi","deleting");
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid).collection("myPictures").document(nomEspece);

// Remove the 'capital' field from the document
        Map<String,Object> updates = new HashMap<>();
        updates.put(imageCode, FieldValue.delete());

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {

                                                             FirebaseStorage.getInstance().getReference().child("a_imagesUtilisateurs").child(uid).child(nomEspece).child(imageCode).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                     Toast.makeText(getApplicationContext(),R.string.suppression_ok,Toast.LENGTH_LONG).show();
                                                                     finish();
                                                                 }
                                                             });



                                                         }
                                                         // ...
                                                         // ...
                                                     });

    }


    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("hi: ", "Directory not created");
        }
        return file;
    }




    public void doIntent1(){
        FirebaseStorage.getInstance().getReference().child("a_imagesUtilisateurs").child(uid).child(nomEspece).child(imageCode).getBytes(1024*1024*3).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));


                Bitmap icon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "title");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");


                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);


                OutputStream outstream;
                try {
                    outstream = getContentResolver().openOutputStream(uri);
                    icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                    outstream.close();
                } catch (Exception e) {
                    System.err.println(e.toString());
                }

                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Image"));

            }

        });
    }

    public void doIntent() {
        verifyStoragePermissions(this);


        if(isExternalStorageWritable()){
            File fileFolder =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File folder =new File(fileFolder,"Insectoland");
            folder.mkdirs();
            File fileSubFolder = new File(folder,"yololo.jpg");


            FileOutputStream outputStream;

            try {
                outputStream = new FileOutputStream(fileSubFolder);
                outputStream.write(myBytes);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            File[] files =getExternalFilesDir("yooo").listFiles();
            for (File mfile : files){
                Log.i("hi",mfile.getAbsolutePath());
                Log.i("hi",mfile.getPath());
            }
            Log.i("", String.valueOf(files.length));

/**
            final MediaScannerConnection connection = new MediaScannerConnection(getApplicationContext(),null);
            connection.connect();
            connection.scanFile(getApplicationContext(), new String[]{fileSubFolder.getPath()},new String[]{ MimeTypeMap.getFileExtensionFromUrl(fileSubFolder.getName())}, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {

                }

                @Override
                public void onScanCompleted(String s, Uri uri) {
                    connection.disconnect();
                }
            });
*/

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri =FileProvider.getUriForFile(getApplicationContext(),"com.tomasgiro.datademo.logindemo3.fileprovider",fileSubFolder);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION| Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.setData(uri);
            startActivity(Intent.createChooser(intent,"Share ..."));




        }


    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
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

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}