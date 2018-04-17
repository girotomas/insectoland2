package com.tomasgiro.datademo.logindemo3;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MaPokedex extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter pokedexAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[][] myDataset;
    private ArrayList<String> nomsDEspeces= new ArrayList<>();
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ma_pokedex);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_pokedex_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);



        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"MyPokedex");}}, SetOptions.merge()
        );


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference collectionReference =FirebaseFirestore.getInstance().collection("users").document(uid).collection("pokedex");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(documentSnapshots!=null) {
                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        nomsDEspeces.add(documentSnapshot.getId());
                        Log.i("hi: adding: ", documentSnapshot.getId());

                    }
                }
                myDataset= new String[1][nomsDEspeces.size()];
                for(int i=0; i<nomsDEspeces.size(); i++){
                    myDataset[0][i]=nomsDEspeces.get(i);
                }
                Log.i("hi: noms especes.size: ", String.valueOf(nomsDEspeces.size()));

                /**if(myDataset[0].length==0){
                    startActivity(new Intent(mRecyclerView.getContext(),WelcomeScreen.class));
                }
                else {
*/
                    pokedexAdapter = new MyPokedexAdapter(myDataset, getParent());
                    mRecyclerView.setAdapter(pokedexAdapter);



                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setItemViewCacheSize(20);
                    mRecyclerView.setDrawingCacheEnabled(true);
                    mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                    //new  DownloadCache(dir,myDataset).doInBackground();







              //  }

            }
        });






    }

    static void saveFile(byte[] bytes, String nomEspece,File dir,String extension) {
        try {
            Log.i("hi","---saving---");
            dir.mkdirs();
            File file =new File(dir, nomEspece+"."+extension);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // One of the group items (using the onClick attribute) was clicked
        // The item parameter passed here indicates which item it is
        // All other menu item clicks are handled by onOptionsItemSelected()
        Log.i("hi: ", "item clicked in menu!");
        if(item.getItemId()==R.id.action_favorite){

        }
        if(item.getItemId()==R.id.take_picture_action){
            FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                    new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"TakePictureAction");}}, SetOptions.merge()
            );
            Intent intent = new Intent(this, mRecyclerView.class);
            intent.putExtra("method","take");
            startActivity(intent);
        }
        if(item.getItemId()==R.id.action_settings){
            FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                    new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"Settings");}},SetOptions.merge()
            );
            startActivity(new Intent(this,Settings.class));
        }if (item.getItemId()== R.id.upload){
            FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                    new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"Upload");}}, SetOptions.merge()
            );
            Intent intent = new Intent(this, mRecyclerView.class);
            intent.putExtra("method","upload");
            startActivity(intent);
        }
        return false;
    }





}
