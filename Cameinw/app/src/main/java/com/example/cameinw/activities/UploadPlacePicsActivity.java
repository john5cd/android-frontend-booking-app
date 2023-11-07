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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.projections.PlaceProjection;
import com.example.cameinw.api.PlaceApi;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RealPathUtil;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPlacePicsActivity extends AppCompatActivity {

    private ImageView im1, im2, im3, im4, im5, im6;
    private MaterialButton removeButton, saveButton;
    private TextView tx1, tx2, tx3, tx4, tx5, tx6;
    private Integer userId, placeId, j, i = 0, m = 0;
    private String realPathUri, token;
    private Uri picUri;
    private List<Uri> picsUris;
    private List<String> pathUris;
    private List<ImageView> picsImg;
    private List<TextView> picsTxt;
    private File file;
    private Boolean flag = false;
    private RequestBody requestFile;
    private PlaceApi placeApi;
    private UserApi userApi;
    private MultipartBody.Part placePic;
    private ActivityResultLauncher<String> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_place_pics);

        initializeComponents();
        choosePicFromGallery();
        getPlaceId();
    }

    private void initializeComponents() {
        MenuUtils.setTopBar("Select Profile Picture", UploadPlacePicsActivity.this);

        im1 = findViewById(R.id.placePic1);
        im2 = findViewById(R.id.placePic2);
        im3 = findViewById(R.id.placePic3);
        im4 = findViewById(R.id.placePic4);
        im5 = findViewById(R.id.placePic5);
        im6 = findViewById(R.id.placePic6);

        removeButton = findViewById(R.id.remove_place_pic_button);
        saveButton = findViewById(R.id.save_place_pic_button);

        token = UserInfoUtils.getUserToken(UploadPlacePicsActivity.this);
        userId = UserInfoUtils.getUserId(UploadPlacePicsActivity.this);

        placeApi = RetrofitUtils.createRequest(PlaceApi.class, token);
        userApi = RetrofitUtils.createRequest(UserApi.class, token);

        activityResultLauncher = activityLauncher();

        tx1 = findViewById(R.id.pn1);
        tx2 = findViewById(R.id.pn2);
        tx3 = findViewById(R.id.pn3);
        tx4 = findViewById(R.id.pn4);
        tx5 = findViewById(R.id.pn5);
        tx6 = findViewById(R.id.pn6);

        picsImg = new ArrayList<>();
        picsTxt = new ArrayList<>();
        picsUris = new ArrayList<>();
        picsUris = new ArrayList<>();
        pathUris = new ArrayList<>();

        picsImg.add(im1);
        picsImg.add(im2);
        picsImg.add(im3);
        picsImg.add(im4);
        picsImg.add(im5);
        picsImg.add(im6);

        picsTxt.add(tx1);
        picsTxt.add(tx2);
        picsTxt.add(tx3);
        picsTxt.add(tx4);
        picsTxt.add(tx5);
        picsTxt.add(tx6);
    }
    private void getPlaceId() {
        userApi.getPlaceByUserId(userId)
                .enqueue(new Callback<List<PlaceProjection>>() {
                    @Override
                    public void onResponse(Call<List<PlaceProjection>> call, Response<List<PlaceProjection>> response) {
                        if (((Integer) response.code()).equals(200)) {
                            handleSuccessfulPlaceCall(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PlaceProjection>> call, Throwable t) {
                        handleFailure(t);
                    }
                });
    }
    private void choosePicFromGallery() {

        im1.setOnClickListener(v -> {
            j=0;
            handlePermissions();
        });

        im2.setOnClickListener(v -> {
            j=1;
            handlePermissions();
        });

        im3.setOnClickListener(v -> {
            j=2;
            handlePermissions();
        });

        im4.setOnClickListener(v -> {
            j=3;
            handlePermissions();
        });

        im5.setOnClickListener(v -> {
            j=4;
            handlePermissions();
        });

        im6.setOnClickListener(v -> {
            j=5;
            handlePermissions();
        });

    }

    private void handlePermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {

            activityResultLauncher.launch("image/*");
        } else {
            ActivityCompat.requestPermissions(UploadPlacePicsActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
        }
    }

    private ActivityResultLauncher<String> activityLauncher () {
               return registerForActivityResult(new
                                ActivityResultContracts.GetContent(),
                        (result) -> {
                            //---FIRST SIX TIMES CHOOSING PICS---\\

                            while (i<6 && !flag) {
                                activateRemoveButton();
                                if (i<5) {
                                    picUri = result;
                                    picsImg.get(i).setImageURI(result);
                                    picsImg.get(i + 1).setVisibility(View.VISIBLE);
                                    picsImg.get(i + 1).setImageResource(R.drawable.add);
                                    realPathUri = RealPathUtil.realPath(picUri, UploadPlacePicsActivity.this);
                                    file = new File(realPathUri);
                                    picsTxt.get(i).setVisibility(View.VISIBLE);
                                    picsTxt.get(i).setText(file.getName());
                                    picsUris.add(picUri);
                                    pathUris.add(realPathUri);

                                    i += 1;
                                    break;
                                } else {
                                    picUri = result;

                                    picsImg.get(i).setImageURI(result);
                                    String realPathUri = RealPathUtil.realPath(picUri, UploadPlacePicsActivity.this);
                                    file = new File(realPathUri);
                                    picsTxt.get(i).setVisibility(View.VISIBLE);
                                    picsTxt.get(i).setText(file.getName());

                                    picsUris.add(picUri);
                                    pathUris.add(realPathUri);

                                    flag = true;
                                    break;
                                }
                            }

                            //---AFTER THE SIXTH TIME ALL IMAGE VIEWS ARE VISIBLE & AND READY TO BE CHANGED IF NEEDED---\\

                            if (i==6) {return;}

                            if (flag) {
                                if (j==0) {
                                    handlePicUri(j, result);
                                } else if (j==1) {
                                    handlePicUri(j, result);
                                } else if (j==2) {
                                    handlePicUri(j, result);
                                } else if (j==3) {
                                    handlePicUri(j, result);
                                } else if (j==4) {
                                    handlePicUri(j, result);
                                } else if (j==5) {
                                    handlePicUri(j, result);
                                }
                            }
                        }
                );
    }

    private void handlePicUri(Integer j, Uri result) {
        picUri = result;
        picsImg.get(j).setImageURI(result);
        realPathUri = RealPathUtil.realPath(picUri, UploadPlacePicsActivity.this);
        file = new File(realPathUri);
        picsTxt.get(j).setText(file.getName());
        picsUris.set(j, picUri);
        pathUris.add(realPathUri);
    }
    private void activateRemoveButton() {
        removeButton.setVisibility(View.VISIBLE);
        removeButton.setOnClickListener(v -> {
            picsUris.clear();
            pathUris.clear();
            for (int k = 0; k < 6; k++) {
                picsTxt.get(k).setVisibility(View.INVISIBLE);
                if (k==0) {
                    picsImg.get(k).setImageResource(R.drawable.add);
                } else {
                    picsImg.get(k).setVisibility(View.INVISIBLE);
                }
            }
            i=0;
        });
    }

    private void savePics(Integer placeId) {
        saveButton.setOnClickListener(v -> {
            for (String path: pathUris) {
                file = new File(path);
                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                placePic = MultipartBody.Part.createFormData("images", file.getName(), requestFile);

                Logger.getLogger(UploadPlacePicsActivity.class.getName()).log(Level.SEVERE, path);

                placeApi.uploadPlacePics(placePic, placeId)
                        .enqueue(new Callback<NoContentResponse>() {
                            @Override
                            public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                                if (((Integer) response.code()).equals(200)) {
                                    handleSuccessfulUploadPics();
                                } else {
                                    handleUnsuccessfulUploadPics();
                                }
                            }

                            @Override
                            public void onFailure(Call<NoContentResponse> call, Throwable t) {
                                handleFailure(t);
                            }
                        });
            }
        });
    }
    private void handleSuccessfulPlaceCall(List<PlaceProjection> listPlaces) {
        placeId = listPlaces.get(listPlaces.size()-1).getId();
        savePics(placeId);
    }
    private void handleSuccessfulUploadPics() {
        m+=1;
        if (m.equals(pathUris.size())) {
            Toast.makeText(UploadPlacePicsActivity.this, "Pics saved successfully!", Toast.LENGTH_SHORT).show();
            Intent facRegIntent = new Intent(UploadPlacePicsActivity.this, RegulationsAndFacilityActivity.class);
            startActivity(facRegIntent);
        }
    }
    private void handleUnsuccessfulUploadPics() {
        Logger.getLogger(UploadPlacePicsActivity.class.getName()).log(Level.SEVERE, "Try again!");
    }
    private void handleFailure(Throwable t) {
        Logger.getLogger(UploadPlacePicsActivity.class.getName()).log(Level.SEVERE, "Error occurred!", t);
    }
}