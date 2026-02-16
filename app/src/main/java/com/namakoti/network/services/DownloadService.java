package com.namakoti.network.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.namakoti.utils.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class DownloadService extends IntentService{
    public static final int DOWNLOAD_ERROR = 10;
    public static final int DOWNLOAD_SUCCESS = 11;

    public DownloadService() {
        super("Download Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String music = intent.getStringExtra("url");
        String mNamamName = intent.getStringExtra("namam");
        initDownload(music,mNamamName);
    }

    private void initDownload(String music,String mNamamName) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.GOD_IMAGE_BASE_URL)
//                .build();
//
//        RequestInterface retrofitInterface = retrofit.create(RequestInterface.class);
//        Call<ResponseBody> request = retrofitInterface.downloadFile(music);
//        try {
//            downloadFile(request.execute().body(),mNamamName);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//        }
        int count;
        try {
            File cacheDir = new File(getCacheDir(), "mycache"); // Pass getFilesDir() and "MyFile" to read file
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            File f = new File(cacheDir, mNamamName + ".mp3");
            URL url = new URL(music);
            OutputStream output = null;
            InputStream input = null;
            input = new BufferedInputStream(url.openStream());
            output = new FileOutputStream(f);
            byte data[] = new byte[1024];
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);

            }
            onDownloadComplete();
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDownloadComplete() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.INTENT_FILE_DOWNLOADED_ACTION);
        getBaseContext().sendBroadcast(broadcastIntent);
    }
}
