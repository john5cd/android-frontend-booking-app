package com.example.cameinw.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RealPathUtil;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadProfilePicActivity extends AppCompatActivity {

    private MaterialButton select, save;
    private ImageView pic;
    private Uri picUri;
    private String token, realPathUri, role;
    private Integer userId;
    private ActivityResultLauncher<String> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        initializeComponents();
        chooseFromLocalStorage();
        uploadOnServer();
    }

    private void initializeComponents() {
        MenuUtils.setTopBar("Select Profile Picture", UploadProfilePicActivity.this);

        select = findViewById(R.id.select_pic_button);
        save = findViewById(R.id.save_pic_button);
        pic = findViewById(R.id.myPic);

        token = UserInfoUtils.getUserToken(UploadProfilePicActivity.this);
        userId = UserInfoUtils.getUserId(UploadProfilePicActivity.this);

        role = getIntent().getStringExtra("userRole");

        activityResultLauncher = activityLauncher ();
    }

    private void chooseFromLocalStorage() {
        select.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {

                activityResultLauncher.launch("image/*");
            } else {
                ActivityCompat.requestPermissions(UploadProfilePicActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
            }
        });
    }
    private void uploadOnServer() {

        save.setOnClickListener(v -> {
            UserApi userApi = RetrofitUtils.createRequest(UserApi.class, token);

            if (realPathUri!=null) {
                File file = new File(realPathUri);

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part profilePic = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                userApi.uploadProfilePic(profilePic, userId)
                        .enqueue(new Callback<NoContentResponse>() {
                            @Override
                            public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                                if (((Integer) response.code()).equals(201)) {
                                    Toast.makeText(UploadProfilePicActivity.this, "Profile pic saved!", Toast.LENGTH_SHORT).show();
                                    if (role.equals("USER")) {
                                        handleAsUser();
                                    } else {
                                        handleAsOwner();
                                    }
                                } else {
                                    handleUnsuccessfulUpload();
                                }
                            }

                            @Override
                            public void onFailure(Call<NoContentResponse> call, Throwable t) {
                                handleFailure(t);
                            }
                        });
            } else {
                Intent searchIntent = new Intent(UploadProfilePicActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });
    }

    private void handleAsUser() {
        Intent searchIntent = new Intent(UploadProfilePicActivity.this, SearchActivity.class);
        startActivity(searchIntent);
    }

    private void handleAsOwner() {
        Intent profilePicIntent = new Intent(UploadProfilePicActivity.this, LocationActivity.class);
        startActivity(profilePicIntent);
    }

    private void handleUnsuccessfulUpload() {
        Toast.makeText(UploadProfilePicActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
    }

    private void handleFailure(Throwable t) {
        Logger.getLogger(UploadProfilePicActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
    }

    private ActivityResultLauncher<String> activityLauncher () {
        return registerForActivityResult(new
                        ActivityResultContracts.GetContent(),
                (result) -> {
                    picUri = result;
                    pic.setImageURI(picUri);

                    realPathUri = RealPathUtil.realPath(picUri, UploadProfilePicActivity.this);
                });
    }
}