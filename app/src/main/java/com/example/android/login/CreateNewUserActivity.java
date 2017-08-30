package com.example.android.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class CreateNewUserActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_username;
    private EditText et_useremail;
    private EditText et_password1;
    private EditText et_password2;
    private Button btn_signUp;
    private ArrayList<TextView> errorMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_new_user);
        et_username = (EditText)findViewById(R.id.et_username);
//        et_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//                    if(et_username.getText().toString().isEmpty()){
//                        errorMark.get(1).setVisibility(View.VISIBLE);
//                    }else{
//                        errorMark.get(1).setVisibility(View.GONE);
//                    }
//                }
//            }
//        });
        et_useremail = (EditText)findViewById(R.id.et_useremail);
//        et_useremail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//                    if(et_useremail.getText().toString().isEmpty()){
//                        errorMark.get(0).setVisibility(View.VISIBLE);
//                    }else{
//                        errorMark.get(0).setVisibility(View.GONE);
//                    }
//                }
//            }
//        });
        et_password1 = (EditText)findViewById(R.id.et_password);
        et_password2 = (EditText)findViewById(R.id.et_password2);
        btn_signUp = (Button)findViewById(R.id.btn_signup);
        btn_signUp.setOnClickListener(this);


//        errorMark = new ArrayList<>();
//        errorMark.add((TextView) findViewById(R.id.e1));
//        errorMark.add((TextView) findViewById(R.id.e2));
//        errorMark.add((TextView) findViewById(R.id.e3));
//        errorMark.add((TextView) findViewById(R.id.e4));
    }

    @Override
    public void onClick(View v) {
        if(!validate())return;
        String email = et_useremail.getText().toString();
        String pass1 = et_password1.getText().toString();
//        String pass2 = et_password2.getText().toString();
        String name = et_username.getText().toString();
//        if(email.equals("") || name.equals("") || pass1.equals("") || pass2.equals("")) {
//            if (email.equals("")) {
//                errorMark.get(0).setVisibility(View.VISIBLE);
//            } else{
//                errorMark.get(0).setVisibility(View.GONE);
//            }
//            if (name.equals("")) {
//                errorMark.get(1).setVisibility(View.VISIBLE);
//            }else{
//                errorMark.get(1).setVisibility(View.GONE);
//            }
//
//            if (pass1.equals("") || pass2.equals("")) {
//                errorMark.get(2).setVisibility(View.VISIBLE);
//                errorMark.get(3).setVisibility(View.VISIBLE);
//                findViewById(R.id.msg).setVisibility(View.VISIBLE);
//            } else{
//                errorMark.get(2).setVisibility(View.GONE);
//                errorMark.get(3).setVisibility(View.GONE);
//                findViewById(R.id.msg).setVisibility(View.GONE);
//            }
//            Toast.makeText(CreateNewUserActivity.this, "Blank Field..Please Enter", Toast.LENGTH_LONG).show();
//            return;
//        }
//        else
//        {
//            for(int i=0 ; i < 4; i++){
//                errorMark.get(i).setVisibility(View.GONE);
//            }
//            if( !pass1.equals(pass2)){
//                errorMark.get(2).setVisibility(View.VISIBLE);
//                errorMark.get(3).setVisibility(View.VISIBLE);
//                findViewById(R.id.msg).setVisibility(View.VISIBLE);
//                return;
//            }else{
                findViewById(R.id.msg).setVisibility(View.GONE);
                new CreateUser().execute(email,name,pass1);
//            }
//        }
    }

    private boolean validate() {
        boolean valid =true;
        String email = et_useremail.getText().toString();
        String pass1 = et_password1.getText().toString();
        String pass2 = et_password2.getText().toString();
        String name = et_username.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_useremail.setError("Enter a valid email address");
            valid = false;
        } else {
            et_useremail.setError(null);
        }
        if (name.isEmpty()) {
            et_username.setError("Enter a valid email address");
            valid = false;
        } else {
            et_username.setError(null);
        }
        if (pass1.isEmpty() || pass1.length() < 4 || pass1.length() > 10) {
            et_password1.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            et_password1.setError(null);
        }
        if (!pass2.equals(pass1)) {
            et_password2.setError("The passwords are different.");
            findViewById(R.id.msg).setVisibility(View.VISIBLE);
            valid = false;
        } else {
            et_password2.setError(null);
            findViewById(R.id.msg).setVisibility(View.GONE);
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.pull_in_left,R.anim.push_out_right);
        //finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
    }
    class CreateUser extends AsyncTask<String,String,String> {
        private ProgressDialog pdLoading = new ProgressDialog(CreateNewUserActivity.this);
        private StringBuffer buffer;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://105.102.48.221/test/createUser.php");
                // Add your data
                ArrayList<NameValuePair>nameValuePairs = new ArrayList<>(3);
                nameValuePairs.add(new BasicNameValuePair(AppContract.USER_EMAIL, params[0].trim()));
                nameValuePairs.add(new BasicNameValuePair(AppContract.USER_NAME, params[1].trim()));
                nameValuePairs.add(new BasicNameValuePair(AppContract.USER_PASS, params[2].trim()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
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
            }

            catch (Exception e)
            {
                Log.e("TAG", "error"+e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pdLoading.dismiss();
            if(buffer == null){
                Toast.makeText(CreateNewUserActivity.this, "Problem with server. Check later", Toast.LENGTH_LONG).show();
            }else {
                if (s.equals("Y")) {
                    Toast.makeText(CreateNewUserActivity.this, "New user successfully created", Toast.LENGTH_LONG).show();
                    finish();
                } else if( s.equals("E")){
                    Toast.makeText(CreateNewUserActivity.this, "This email is already used by another user", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(CreateNewUserActivity.this, "Problems when trying to store in database", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
