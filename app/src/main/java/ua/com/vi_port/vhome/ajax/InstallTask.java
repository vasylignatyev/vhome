package ua.com.vi_port.vhome.ajax;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vignatyev on 21.03.2016.
 */
public class InstallTask  extends AsyncTask<Void, Void, String> {
    ProgressDialog mProgressDialog;

    Context context;
    String url;

    public InstallTask(Context context, String url) {
        this.context = context;

        this.url = url;

    }

    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(context,
                "Download", " Downloading in progress..");
    }

    private String downloadapk() {
        String result = "";
        try {
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlendcoded");
            urlConnection.setRequestProperty("Accept", "*/*");

            urlConnection.connect();

            int status = urlConnection.getResponseCode();
            if( status != 200) {
                Log.d("MyApp", "Server HTTP status: " + status);
            }
            InputStream inputStream = urlConnection.getInputStream();

            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "vhome.apk");

           // if (!mFolder.exists()) {
            //    mFolder.mkdir();
            //}
            if (!file.exists()) {
                file.createNewFile();
            }

            Log.d("MyApp", "sdcard: " + sdcard.toString() + "file: " + file);

            FileOutputStream fileOutput = new FileOutputStream(file);


            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            result = "done";

        } catch ( MalformedURLException  e) {
            e.printStackTrace();
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected String doInBackground(Void... params) {
        String result = downloadapk();
        return result;
    }

    protected void onPostExecute(String result) {
        if (result.equals("done")) {
            mProgressDialog.dismiss();
            installApk();
        } else {
            mProgressDialog.dismiss();
            Toast.makeText(context, "Error while downloading",
                    Toast.LENGTH_LONG).show();

        }
    }

    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "vhome.apk");
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


}
