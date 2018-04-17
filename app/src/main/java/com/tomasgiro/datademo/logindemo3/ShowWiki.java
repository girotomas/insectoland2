package com.tomasgiro.datademo.logindemo3;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowWiki extends AppCompatActivity {
    String insectName;
    WebView myWebView;
    String pageId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wiki);
        insectName = getIntent().getExtras().getString("nomEspece");
        new RetrievePageId().execute();





    }


    class RetrievePageId extends  AsyncTask<String,Void,Void>{

        private String responseString;

        @Override
        protected Void doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Pattern pattern = Pattern.compile(".*<(.*?)>.*");
            Log.i("hi: ",pattern.pattern());
            Matcher matcher = pattern.matcher(insectName);
            matcher.find();
            String queryName;
            try {
                queryName = matcher.group(1);
            }
             catch (Exception e){
                e.printStackTrace();
                queryName="";
             }
                Log.i("hi: queryName= ",queryName);
            Request request = new Request.Builder()
                    .url("https://"+getString(R.string.language_code)+".wikipedia.org/w/api.php?action=query&titles="+queryName+"&format=json")
                    .build();
            Response response = null;
            pageId = null;
            try {
                response = client.newCall(request).execute();
                responseString =response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Log.i("hi: ",responseString);

                JSONObject jsonObject = new JSONObject(responseString);
                Log.i("hi:",jsonObject.toString());
                pageId = (String) ((JSONObject)((JSONObject) jsonObject.get("query")).get("pages")).keys().next();
                Log.i("hi:",pageId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateUi();







            return null;
        }
    }

    private  void updateUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myWebView= (WebView) findViewById(R.id.webview);
                myWebView.loadUrl("https://"+getString(R.string.language_code)+".m.wikipedia.org/w/index.php?curid="+pageId);

            }
        });

    }
}
