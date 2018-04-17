package com.tomasgiro.datademo.logindemo3;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class TakeAPicture extends AppCompatActivity {
    private static final int CROP_PIC_REQUEST_CODE = 234243;
    private static final int ACTION_BROWSE_PIC =13121 ;
    private static final String TAG = "TakeAPicture";
    String mCurrentPhotoPath;
    String pictureName;
    File imageFile;
    private int PICK_IMAGE=23114;
    private byte[] uploadedInputData;
    private AdView mAdView;
    private AdView mAdViewBig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_apicture);

        MobileAds.initialize(this,
                "ca-app-pub-3893039593729775~3197648567");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("0181B15AF8984258CEC14778AB1F2512")
                .build();
        mAdView.loadAd(adRequest);

        mAdViewBig = findViewById(R.id.adView2);
        
        mAdViewBig.loadAd(adRequest);



        if(getIntent().getStringExtra("method").equals("take")) {
            dispatchTakePictureIntent();

        }
        if(getIntent().getStringExtra("method").equals("upload")) {
            dispatchUploadPictureIntentOO();
        }




    }

    private void dispatchUploadPictureIntentOO() {


        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE);
        Log.i(TAG,"starting intent pick image");




    }


    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {


        @Override
        protected Long doInBackground(URL... urls) {
            byte[] bitmapdata=null;
            if(getIntent().getStringExtra("method").equals("take")) {
                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                bitmapdata = stream.toByteArray();
            } else if(getIntent().getStringExtra("method").equals("upload")) {
                bitmapdata = uploadedInputData;

            }


            OkHttpClient client = new OkHttpClient.Builder().readTimeout(1, TimeUnit.HOURS)
                    .connectTimeout(1,TimeUnit.HOURS)
                    .build();


            //Drawable drawable = getResources().getDrawable(R.drawable.lol);
            // Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();








            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "logo-square.jpeg",
                            RequestBody.create(MEDIA_TYPE_PNG, bitmapdata))
                    .build();

            Request request = new Request.Builder()
                    .url("http://ramp.studio:24000/predict_from_image")
                    .post(requestBody)

                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                final String text = response.body().string();

                Log.i("hi", text);
             /**
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
*/
                Intent intent = new Intent();
                Log.i("hi: resultSent:" ,text);

                FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                        new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"ResultFromServerCall: "+text);}}, SetOptions.merge()
                );

//---set the data to pass back---
                intent.putExtra("stringResult",text);
                intent.putExtra("bitmapData",bitmapdata);
                setResult(RESULT_OK, intent);
//---close the activity---
                finish();




            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Crashlytics.logException(e1);
                }
            }



            return null;
        }







    }



    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                imageFile=photoFile;
            } catch (IOException ex) {
                // Error occurred while creating the File
                Crashlytics.logException(ex);
                Log.i("hi", "Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.tomasgiro.datademo.logindemo3.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.i(TAG,"sending request take photo");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_CANCELED) {
                startActivity(new Intent(this, MaPokedex.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                        new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"TakePictureActionCancelled");}}, SetOptions.merge()
                );
            } else {
                Log.i("hi: ", "result received!");
                FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                        new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"TakePictureActionImageReceived");}}, SetOptions.merge()
                );
                Uri uri = Uri.fromFile(imageFile);
                File dir= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                dir.mkdirs();
                File file= new File(dir,"photoToUpload");
                Uri uri2= Uri.fromFile(file);
                Bitmap bmp = BitmapFactory.decodeFile(imageFile.getPath());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, bos);
                FileOutputStream fileOutputStream= null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bos.toByteArray());
                } catch (FileNotFoundException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                Random random= new Random();
                int integy= random.nextInt(100000);
                final String name=System.currentTimeMillis()+" "+FirebaseAuth.getInstance().getCurrentUser().getEmail()+String.valueOf(new Date(System.currentTimeMillis()))+integy+".jpg";
                FirebaseStorage.getInstance().getReference().child("A_toutes_photos").child(name).putFile(uri2)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                                        new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"TakePictureActionUploadSuccess to: "+name);}}, SetOptions.merge()
                                );
                            }
                        }).


                        addOnCompleteListener(
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                new DownloadFilesTask().execute();

                            }
                        }
                );


            }
        }
       if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
                // The document selected by the user won't be returned in the intent.
                // Instead, a URI to that document will be contained in the return intent
                // provided to this method as a parameter.
                // Pull that URI using resultData.getData().
           Log.i("hi: ","upload ches");
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    Log.i("hi: ", "Uri: " + uri.toString());

                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(uri);
                        uploadedInputData = getBytes(iStream);

                    } catch (FileNotFoundException e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }

                    imageFile= new File(uri.getPath());
                    mCurrentPhotoPath=imageFile.getAbsolutePath();

                    Random random= new Random();
                    final String name= System.currentTimeMillis()+" "+FirebaseAuth.getInstance().getCurrentUser().getEmail()+String.valueOf(new Date(System.currentTimeMillis()))+random.nextInt(100000);
                    FirebaseStorage.getInstance().getReference().child("A_toutes_photos").child(name).putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                                            new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"TakePictureActionUploadSuccess to: "+name);}}, SetOptions.merge()
                                    );
                                }
                            })





                            .addOnCompleteListener(
                            new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    new DownloadFilesTask().execute();

                                }
                            }
                    );

                }
            }else if (requestCode == PICK_IMAGE ){
           Log.i(TAG,"result from action pic image cancelled");
           Intent intent=new Intent(this,MaPokedex.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(intent);
       }


        if (requestCode == ACTION_BROWSE_PIC && resultCode==RESULT_OK) {
// Create the File where the photo should go
                data.getStringExtra("");
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    imageFile=photoFile;
                } catch (IOException ex) {
                    Crashlytics.logException(ex);
                    // Error occurred while creating the File
                    Log.i("hi", "Error occurred while creating the File");
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.tomasgiro.datademo.logindemo3.fileprovider",
                            photoFile);

                }
        }else if (requestCode == ACTION_BROWSE_PIC){
            Log.i(TAG,"result from action browse picture cancelled");
            Intent intent=new Intent(this,MaPokedex.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Log.i(TAG,"closing action browse pic");
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }



    private void doCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Crashlytics.logException(anfe);
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }



}
