package com.example.youtubeconverter;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import retrofit2.http.GET;


public class MainActivity extends AppCompatActivity {

   private WebView webView;
   private FloatingActionButton floatingActionButton;
   private EditText url_tab;

   private int REQUEST_CODE = 1;
   private String youtubeLink = "";
   private String youtubeTitle = "";

   private ProgressBar progressBar;
   private ImageButton imageButton;


   private String video, video_url;

   private MediaController mediaController;
   private Uri uri2;
   private String URL, reelurl = "1";

   private int abc = 2;



   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       Initializing();
       CheckPermission();
       WebViewSettings();


       floatingActionButton.setOnClickListener(v -> {

           DownloadVideoYoutube();
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress){
                progressBar.setProgress(newProgress);

                if (newProgress == 100){
                    progressBar.setVisibility(View.GONE);

                    String currentUrl = view.getUrl();
                    youtubeTitle = view.getTitle();
                    url_tab.setText(view.getUrl());

                    if (currentUrl.contains("watch")){
                        youtubeLink = view.getUrl();
                        floatingActionButton.setVisibility(View.VISIBLE);

                    }else{
                        floatingActionButton.setVisibility(View.GONE);
                    }
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);

            }
        });

        imageButton.setOnClickListener(v -> {
            webView.loadUrl(url_tab.getText().toString().trim());


        });

        url_tab.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    String url_result = url_tab.getText().toString().trim();
                    if (url_result.contains(".com")){
                        webView.loadUrl("https://"+ url_result);
                    }else{
                        webView.loadUrl("https://www.google.com/search?q="+ url_result);
                    }
                    return true;
                }
                return false;
            }
        });


    }

    /* TODO on the way
    private void DownloadInstagramVideo() {


       URL = "https://www.instagram.com/reel/CWlgHHdsZsN/?utm_source=ig_web_copy_link";
       if (URL.equals("NULL")){
           Toast.makeText(MainActivity.this, "ERROR URL", Toast.LENGTH_SHORT).show();
       }else{
            String result2 = StringUtils.substringBefore(URL,"/?");
            URL = result2+"/?__a=1";
            //Log.d("linkleme", URL);

            processDataInstagram();
       }

    }

    private void processDataInstagram() {


        StringRequest request = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null) {

                    Log.d("linkleme", response);


                   // GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = new Gson();

                    MainURL mainURL = gson.fromJson(response, MainURL.class);

                    reelurl = mainURL.getGraphql().getShortcode_media().getVideo_url();

                    uri2 = Uri.parse(reelurl);


                }else{
                    Toast.makeText(MainActivity.this, "NULLLLL", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);




   }

     */


    private void WebViewSettings() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.youtube.com");
    }

    private void Initializing() {
        floatingActionButton = findViewById(R.id.floatingActionButton);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progessBar);
        url_tab = findViewById(R.id.url_tab);
        imageButton = findViewById(R.id.ImageButton);

        progressBar.setMax(100);
        progressBar.setProgress(0);

        floatingActionButton.setVisibility(View.GONE);

    }

    private void CheckPermission() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG,"Permission is granted");
            //File write logic here

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    public void DownloadVideoYoutube() {


        new YouTubeExtractor(this) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onExtractionComplete(@Nullable SparseArray<YtFile> files, @Nullable VideoMeta videoMeta) {
                if (files != null) {
                    // 720, 1080, 480
                    List<Integer> iTags = Arrays.asList(22, 137, 18);

                    for (Integer iTag : iTags) {

                        YtFile ytFile = files.get(iTag);

                        if (ytFile != null) {

                            String downloadUrl = ytFile.getUrl();
                            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                Toast.makeText(MainActivity.this, "İndirme İşlemi Başlandı", Toast.LENGTH_SHORT).show();
                                String title = youtubeTitle;
                                DownloadManager.Request request =new DownloadManager.Request(Uri.parse(downloadUrl));
                                request.setTitle(title);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title.trim()+ ".mp4");
                                @SuppressLint({"ServiceCast", "StaticFieldLeak"}) DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                request.allowScanningByMediaScanner();
                                request.setVisibleInDownloadsUi(true);

                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                downloadManager.enqueue(request);
                            }
                        }
                    }
                }
            }
        }.extract(youtubeLink);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();

        }
    }
}