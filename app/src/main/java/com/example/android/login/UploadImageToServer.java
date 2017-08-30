package com.example.android.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.extras.Base64;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by SRT on 8/29/2017.
 */

public class UploadImageToServer extends AsyncTask<String,String,String> {
    private Context mContext;
    private Bitmap mBitmap;
    private String email;
    StringBuffer buffer;


    public UploadImageToServer(String email,Bitmap bmp,Context context){
        this.email=email;
        this.mBitmap=bmp;
        this.mContext=context;
    }
    @Override
    protected String doInBackground(String... params) {
        Log.d("TAG", "Starting Upload...");
        final String url = "http://105.102.48.221/test/profile_image.php";

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(2);

            nameValuePairs.add(new BasicNameValuePair(AppContract.USER_EMAIL, email));
            nameValuePairs.add(new BasicNameValuePair(AppContract.USER_IMAGE,convertBitmapToString(mBitmap) ));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);

            InputStream inputStream = response.getEntity().getContent();

            // Convert response to string using String Buffer
            byte[] data = new byte[256];
            buffer = new StringBuffer();

            int len = 0;
            while (-1 != (len = inputStream.read(data)) )
            {
                buffer.append(new String(data, 0, len));
            }

            inputStream.close();

            return String.valueOf(buffer.charAt(0));
        } catch (Exception e) {
            Log.d("TAG","Error in http connection " + e.toString());

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s.equals("Y")){
            Toast.makeText(mContext,"Image saved",Toast.LENGTH_LONG).show();
        } else if(s.equals("N")){
            Toast.makeText(mContext,"Image don't saved",Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(mContext,"email doesn't exist",Toast.LENGTH_LONG).show();
        }
    }

    public String convertBitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
