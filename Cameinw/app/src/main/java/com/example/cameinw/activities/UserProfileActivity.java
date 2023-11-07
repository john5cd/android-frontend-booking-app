package com.example.cameinw.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cameinw.R;
import com.example.cameinw.Response.NoContentResponse;
import com.example.cameinw.api.ImageApi;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.dto.UserDTO;
import com.example.cameinw.model.User;
import com.example.cameinw.util.ImageUtils;
import com.example.cameinw.util.MenuUtils;
import com.example.cameinw.util.RealPathUtil;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.example.cameinw.util.UserUtils;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView textViewFirstName, textViewLastName, textViewUsername,
            textViewEmail, textViewPhone;
    private CircleImageView circleImageView, plusImage;
    private Button editButton, saveButton, cancelButton;
    private EditText editTextFirstName, editTextLastName, editTextPhone;
    private User currentUser = null;
    private String userToken;
    private Integer userId;
    private Uri picUri;
    private String realPathUri;
    private boolean isEditMode = false;
    private ActivityResultLauncher<String> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeComponents();
        getUserData();
    }

    private void initializeComponents() {
        MenuUtils.setMenus("User Profile", UserProfileActivity.this);

        progressBar = findViewById(R.id.progressBar);
        textViewFirstName = findViewById(R.id.textview_firstName);
        textViewLastName = findViewById(R.id.textview_lastName);
        textViewUsername = findViewById(R.id.textview_username);
        textViewEmail = findViewById(R.id.textview_email);
        textViewPhone = findViewById(R.id.textview_phone);
        circleImageView = findViewById(R.id.imageview_profImg);
        editButton = findViewById(R.id.button_edit);
        saveButton = findViewById(R.id.button_save);
        cancelButton = findViewById(R.id.button_cancel);
        editTextFirstName = findViewById(R.id.editview_firstname);
        editTextLastName = findViewById(R.id.editview_lastname);
        editTextPhone = findViewById(R.id.editview_phone);
        plusImage = findViewById(R.id.plusForImage);

        findViewById(R.id.edit_buttons_layout).setVisibility(View.GONE);

        editButton.setOnClickListener(v -> enterEditMode());
        saveButton.setOnClickListener(v -> saveEditedUserData());
        cancelButton.setOnClickListener(v -> exitEditMode());

        userToken = UserInfoUtils.getUserToken(this);
        userId = UserInfoUtils.getUserId(this);

        activityResultLauncher = activityLauncher ();
    }

    private void getUserData() {
        if (userId != null) {
            fetchUserData();
        } else {
            Toast.makeText(this, "Unable to fetch user details", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserData() {
        UserApi userApi = RetrofitUtils.createRequest(UserApi.class, userToken);
        Call<UserDTO> call = userApi.getUserById(userId);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    handleSuccessfulUserProfileResponse(Objects.requireNonNull(response.body()));
                } else {
                    handleUnsuccessfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                handleFailure(Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void handleSuccessfulUserProfileResponse(UserDTO userDTO) {
        User user = UserUtils.processUserDTO(userDTO);
        currentUser = user;
        textViewUsername.setText(user.getTheUserName());
        textViewFirstName.setText(user.getFirstName());
        textViewLastName.setText(user.getLastName());
        textViewEmail.setText(user.getEmail());
        textViewPhone.setText(user.getPhoneNumber());

        ImageUtils.fetchUserImageWithRetry(userId, userToken, 5, new
                ImageUtils.ImageFetchCallback() {
                    @Override
                    public void onImageFetchSuccess(int userId, Bitmap imageBitmap) {
                        circleImageView.setImageBitmap(imageBitmap);
                    }

                    @Override
                    public void onImageFetchError(int userId) {
                        handleImageFetchError();
                    }
                });
    }

    private void handleUnsuccessfulResponse(int responseCode) {
        Log.e("UserProfileActivity", "Unsuccessful response: " + responseCode);
    }

    private void handleFailure(String errorMessage) {
        Log.e("UserProfileActivity", "Error occurred: " + errorMessage);
    }

    private void handleImageFetchError() {
        Toast.makeText(this, "Error fetching user image", Toast.LENGTH_SHORT).show();
    }

    private void enterEditMode() {
        isEditMode = true;
        editButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        plusImage.setVisibility(View.VISIBLE);
        setViewVisibility(textViewFirstName, textViewLastName, textViewPhone,
                View.GONE);
        setViewVisibility(editTextFirstName, editTextLastName, editTextPhone,
                View.VISIBLE);
        populateEditTexts();
        findViewById(R.id.edit_buttons_layout).setVisibility(View.VISIBLE);

        circleImageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {

                activityResultLauncher.launch("image/*");
            } else {
                ActivityCompat.requestPermissions(UserProfileActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );
            }
        });
    }

    private void saveEditedUserData() {
        currentUser.setFirstName(editTextFirstName.getText().toString());
        currentUser.setLastName(editTextLastName.getText().toString());
        currentUser.setPhoneNumber(editTextPhone.getText().toString());

        UserApi userApi = RetrofitUtils.createRequest(UserApi.class, userToken);
        userApi.updateUser(userId, currentUser).enqueue(new Callback<NoContentResponse>() {
                    @Override
                    public void onResponse(Call<NoContentResponse> call,
                                           Response<NoContentResponse> response) {
                        if (response.isSuccessful()) {
                            updateUIWithEditedData(currentUser);
                            exitEditMode();
                            Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            handleUnsuccessfulResponse(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<NoContentResponse> call, Throwable t) {
                        handleFailure(t.getMessage());
                    }
                });


        ImageApi imageApi = RetrofitUtils.createRequest(ImageApi.class, userToken);

        if (realPathUri!=null) {
            File file = new File(realPathUri);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part profilePic = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            imageApi.updateProfilePic(userId, profilePic)
                    .enqueue(new Callback<NoContentResponse>() {
                        @Override
                        public void onResponse(Call<NoContentResponse> call, Response<NoContentResponse> response) {
                            ImageUtils.fetchUserImageWithRetry(userId, userToken, 5, new
                                    ImageUtils.ImageFetchCallback() {
                                        @Override
                                        public void onImageFetchSuccess(int userId, Bitmap imageBitmap) {
                                            circleImageView.setImageBitmap(imageBitmap);
                                        }

                                        @Override
                                        public void onImageFetchError(int userId) {
                                            handleImageFetchError();
                                        }
                                    });
                        }

                        @Override
                        public void onFailure(Call<NoContentResponse> call, Throwable t) {

                        }
                    });
        }

        exitEditMode();
    }

    private void exitEditMode() {
        isEditMode = false;
        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        plusImage.setVisibility(View.GONE);
        setViewVisibility(editTextFirstName, editTextLastName, editTextPhone,
                View.GONE);
        setViewVisibility(textViewFirstName, textViewLastName, textViewPhone,
                View.VISIBLE);
        findViewById(R.id.edit_buttons_layout).setVisibility(View.GONE);
    }

    private void setViewVisibility(View view1, View view2, View view3, int visibility) {
        view1.setVisibility(visibility);
        view2.setVisibility(visibility);
        view3.setVisibility(visibility);
    }

    private void populateEditTexts() {
        editTextFirstName.setText(textViewFirstName.getText());
        editTextLastName.setText(textViewLastName.getText());
        editTextPhone.setText(textViewPhone.getText());
    }

    private void updateUIWithEditedData(User editedUser) {
        textViewFirstName.setText(editedUser.getFirstName());
        textViewLastName.setText(editedUser.getLastName());
        textViewPhone.setText(editedUser.getPhoneNumber());
    }

    private ActivityResultLauncher<String> activityLauncher () {
        return registerForActivityResult(new
                        ActivityResultContracts.GetContent(),
                (result) -> {
                    picUri = result;
                    circleImageView.setImageURI(picUri);

                    realPathUri = RealPathUtil.realPath(picUri, UserProfileActivity.this);
        });
    }
}
