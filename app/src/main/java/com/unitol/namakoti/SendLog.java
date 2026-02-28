/*
package com.unitol.namakoti;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

*/
/**
 * Created by Gangadhar on 9/28/2016.
 *//*

public class SendLog extends Activity implements View.OnClickListener {
    private static final String TAG = "SendLog";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate =========");
        requestWindowFeature(Window.FEATURE_NO_TITLE); // make a dialog without a titlebar
//        setFinishOnTouchOutside(false); // prevent users from dismissing the dialog by tapping outside
        setContentView(R.layout.send_log);

        Button send_logs = (Button) findViewById(R.id.send_logs);
        send_logs.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // respond to button clicks in your UI
//        extractLogToFile();
        sendLogFile();
    }

    private void sendLogFile() {
        Log.e(TAG, "sendLog=======");
        String fullName = extractLogToFile();
        Log.e(TAG, "fullName=======" + fullName);
        if (fullName == null)
            return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"gangadhar.3e@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Namakoti log file");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fullName));
        intent.putExtra(Intent.EXTRA_TEXT, "Log file attached."); // do this so some email clients don't complain about empty body.
        startActivity(intent);
        Log.e(TAG, "sendLog2222=======");
    }

    @Nullable
    private String extractLogToFile() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        // Make file name - file must be saved to external storage or it wont be readable by
        // the email app.
        File path = Environment.getExternalStorageDirectory();
        String fullName = path + "/namakoti_log.txt";

        // Extract to file.
        File file = new File(fullName);
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = "logcat -d -v time";
            Log.e(TAG, "cmd ======== " + cmd);

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            // write output stream
            writer = new FileWriter(file);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model + "\n");
            writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                Log.e(TAG, "buffer ======== " + buffer.toString());
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);
            Log.e(TAG, "buffer ======== " + buffer.toString());

            reader.close();
            writer.close();
        } catch (Exception e) {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                }

            // You might want to write a failure message to the log here.
            e.printStackTrace();
            return null;
        }

        return fullName;
    }
}
*/
