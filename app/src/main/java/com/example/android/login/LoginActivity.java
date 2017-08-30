package com.example.android.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_login;


    private EditText et_useremail,et_password;
    private TextView tv_signup;
    SharedPreferences app_preferences ;
    private CheckBox check;
    private SearchUser task;
    private android.os.Handler mHandler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(task.getStatus() == AsyncTask.Status.RUNNING) {
                task.cancelPD();
                task.cancel(true);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(app_preferences.getBoolean("loggedin",false)){
            login(null);
            return;
        }
        et_useremail = (EditText) findViewById(R.id.et_useremail);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        check = (CheckBox) findViewById(R.id.checkboxRememberUser);
        tv_signup = (TextView)findViewById(R.id.linkSignUp);
        if(app_preferences.getBoolean("checked", false)) {
            et_useremail.setText(app_preferences.getString(AppContract.USER_EMAIL,"" ));
            et_password.setText(app_preferences.getString(AppContract.USER_PASS,"" ));
            check.setChecked(true);
        }
        btn_login.setOnClickListener(this);
        check.setOnClickListener(this);
        tv_signup.setOnClickListener(this);

        String tempString="If you aren't registered, sign up here";
        SpannableString spanString = new SpannableString(tempString);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        tv_signup.setText(spanString);
    }

    @Override
    public void onClick(View v) {
        int id  = v.getId();
        switch(id){
            case R.id.btn_login:
                String email = et_useremail.getText().toString();
                String pass = et_password.getText().toString();
                if(app_preferences.getBoolean("checked", false)) {
                    SharedPreferences.Editor editor = app_preferences.edit();
                    editor.putString(AppContract.USER_EMAIL, email);
                    editor.putString(AppContract.USER_PASS, pass);
                    editor.commit();
                }else{

                }
                if(!validate()) return;
//                if(email.equals("") || pass.equals("")) {
//                    Toast.makeText(LoginActivity.this, "Blank Field..Please Enter", Toast.LENGTH_LONG).show();
//                }
//                else {
                    task = new SearchUser();
                    task.execute(email, pass);
                    mHandler.sendEmptyMessageDelayed (1, 10*1000);



//                }
                break;
            case R.id.checkboxRememberUser:
                SharedPreferences.Editor editor = app_preferences.edit();
                editor.putBoolean("checked", check.isChecked());
                editor.commit();
                break;
            case R.id.linkSignUp:
                startActivity(new Intent(LoginActivity.this,CreateNewUserActivity.class));
                overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
                break;
        }
    }

    private boolean validate() {
        boolean valid = true;

        String email = et_useremail.getText().toString();
        String password = et_password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_useremail.setError("Enter a valid email address");
            valid = false;
        } else {
            et_useremail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            et_password.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            et_password.setError(null);
        }

        return valid;
    }

    private void login(String email){
        Intent intent = new Intent(LoginActivity.this,UserActivity.class);
        if(!app_preferences.getBoolean("loggedin",false)) {
            intent.putExtra(AppContract.USER_EMAIL, email);
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putBoolean("loggedin", true);
            editor.commit();
        }
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putBoolean("loggedin", false);
            editor.commit();
            if(app_preferences.getBoolean("checked", false)) {
                et_useremail.setText(app_preferences.getString(AppContract.USER_EMAIL,"" ));
                et_password.setText(app_preferences.getString(AppContract.USER_PASS,"" ));
                check.setChecked(true);
            }else{
                et_useremail.setText("");
                et_password.setText("");
                check.setChecked(false);
            }
        }
    }

    class SearchUser extends AsyncTask<String,String,String>{
            public ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
            private StringBuffer buffer;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            private String email;
            @Override
            protected String doInBackground(String... params) {
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://105.102.48.221/test/validate_user.php");
                    // Add your data
                    ArrayList<NameValuePair>nameValuePairs = new ArrayList<>(2);
                    nameValuePairs.add(new BasicNameValuePair(AppContract.USER_EMAIL, params[0].trim()));
                    nameValuePairs.add(new BasicNameValuePair(AppContract.USER_PASS, params[1].trim()));
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
                    email = params[0].trim();
                    return String.valueOf(buffer.charAt(0));
                }

                catch (Exception e)
                {
                    Toast.makeText(LoginActivity.this, "error: "+e.toString(), Toast.LENGTH_LONG).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                if(buffer == null){
                    Toast.makeText(LoginActivity.this, "Problem with server. Check later", Toast.LENGTH_LONG).show();
                }else {
                    if (s.equals("Y")) {
                        //Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(LoginActivity.this,UserActivity.class);
//                        intent.putExtra(AppContract.USER_EMAIL,email);
//                        startActivity(intent);
                        login(email);
                    } else {
                        Log.e("TAG", "Invalid Username or password");
                    }
                }
            }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pdLoading.dismiss();
            Toast.makeText(LoginActivity.this, "Problem with server. Try later", Toast.LENGTH_LONG).show();
        }
        private void cancelPD(){
            pdLoading.dismiss();
            Toast.makeText(LoginActivity.this, "Problem with server. Try later", Toast.LENGTH_LONG).show();
        }
    }
}

