package com.example.android.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by SRT on 8/29/2017.
 */

public class LateralMenu implements View.OnClickListener {
    private TextView tvUserName;
    private TextView tvUserEmail;
    private ImageView profileImageView;
    private NavigationView navigationView;

    private final int PICK_IMAGE_REQUEST = 100;
    private Context mContext;
    private Activity mActivity;
    public LateralMenu(Context context, Activity activity,NavigationView navigationView){
        this.mContext = context;
        this.mActivity =activity;
        this.navigationView=navigationView;
    }
    void setData(JSONObject data){
        View view;
        view = navigationView.getHeaderView(0);


        tvUserName = (TextView) view.findViewById(R.id.name);
        tvUserEmail = (TextView) view.findViewById(R.id.email);
        profileImageView = (ImageView)view.findViewById(R.id.profile_image);
        profileImageView.setOnClickListener(this);
        try {
            tvUserName.setText(data.getString(AppContract.USER_NAME));
            tvUserEmail.setText(data.getString(AppContract.USER_EMAIL));

            String imageStr = data.getString(AppContract.USER_IMAGE);
            Log.d("TAG", "imageStr: " + imageStr);
            if (imageStr != null && !imageStr.equals("") && !imageStr.equals("null")) {
                profileImageView.setImageBitmap(StringToBitMap(imageStr));
            }
        }catch (JSONException e) {
            Log.d("TAG", "error: "+e.toString());
        }

    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.profile_image /*|| id == R.id.profile_image_text*/){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            mActivity.startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        }
    }

}
