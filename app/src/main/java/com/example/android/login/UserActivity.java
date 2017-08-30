package com.example.android.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String email;
    private String info;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private ImageView profileImageView;

    private DrawerLayout drawerLayout;
    private LinearLayout mainLayout;
    private ListView menuLateral;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private LateralMenu lateralMenu;
    private NavigationView navigationView;

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

//        View view;
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        view = inflater.inflate(R.layout.nav_header_main, null);
//
//
//        tvUserName = (TextView) view.findViewById(R.id.name);
//        tvUserEmail = (TextView) view.findViewById(R.id.email);
//        profileImageView = (ImageView)view.findViewById(R.id.profile_image);
//        profileImageView.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString(AppContract.USER_EMAIL,"");
        if(email.isEmpty()) {
            Intent intent = getIntent();
            email = intent.getStringExtra(AppContract.USER_EMAIL);
        }

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar,R.string.naviagtion_drawer_open,R.string.naviagtion_drawer_close);
        mDrawerLayout.setDrawerListener(toogle);
        toogle.syncState();

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lateralMenu = new LateralMenu(this,this,navigationView);
        new GetDataJSON().execute(email);

    }
    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
//        else {
//            super.onBackPressed();
//        }
    }


    private void showInfoInLateralMenu(){

        Log.d("TAG","show: " + info);

        try {
            JSONObject jsonObject = new JSONObject(info);

            JSONArray jsonArray = jsonObject.getJSONArray("result");

            JSONObject data = jsonArray.getJSONObject(0);
            lateralMenu.setData(data);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AppContract.USER_EMAIL,data.getString(AppContract.USER_EMAIL));
            editor.commit();
        } catch (JSONException e) {
            Log.d("TAG", "error: "+e.toString());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppContract.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImageView =(ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
                profileImageView.setImageBitmap(bitmap);
                new UploadImageToServer(email, bitmap,this).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.btn_logout){
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().commit();
            setResult(RESULT_OK);
            finish();
            return false;
        }
        Toast.makeText(this," " +item.getTitle() ,Toast.LENGTH_LONG).show();
        return false;
    }


    class GetDataJSON extends AsyncTask<String, Void, String> {

        private ProgressDialog pdLoading = new ProgressDialog(UserActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tDownloading your data...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        private StringBuffer buffer;
        @Override
        protected String doInBackground(String... params) {

            InputStream inputStream = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://105.102.48.221/test/getInfo.php");

                Log.d("TAG","email: " + email);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair(AppContract.USER_EMAIL, email));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponse = httpclient.execute(httppost);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedEncodingEx", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                return  sBuilder.toString();

            } catch (Exception e) {
                Log.e("StringBuilding & Buffer", "Error converting result " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            Log.d("TAG","onPost");
            pdLoading.dismiss();
            info=result;
            showInfoInLateralMenu();

        }
    }
}
