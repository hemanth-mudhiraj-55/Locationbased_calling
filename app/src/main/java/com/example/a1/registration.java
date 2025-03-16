package com.example.a1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class registration extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private Uri imageUri;

    // UI Elements
    private EditText uniqueUserId, fullName, email, emailOtp, mobileNumber, mobileOtp, password;
    private TextView eMin,eSec,mMin,mSec;
    private Spinner genderSpinner;
    private CheckBox termsConditions;
    private Button registerButton, uploadProfilePictureButton, showTermsButton,skip_registration,mOTP_resend,eOTP_resend;
    private LinearLayout emailOtpLayout, mobileOtpLayout,mOTPtimer,eOTPtimer;
    private CountryCodePicker ccp;

    timer timer =new timer();


    // Retrofit for API calls
    private Retrofit retrofit;
    private retrofit_interface retrofitInterface;
    private final String BASE_URL =getString(R.string.local_host_server);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(retrofit_interface.class);

        // Initialize UI Elements
        initializeViews();

        // Set up Gender Spinner
        setupGenderSpinner();

        // Set up Button Click Listeners
        setupButtonListeners();
    }

    // Initialize all UI elements
    private void initializeViews() {
        uniqueUserId = findViewById(R.id.uniqueUserId);
        fullName = findViewById(R.id.fullName);


        email = findViewById(R.id.et_email);
        emailOtp = findViewById(R.id.emailOtp);
        eOTPtimer=findViewById(R.id.eOTPtimer);
        eOTP_resend=findViewById(R.id.resend1);
        eMin =findViewById(R.id.eMin);
        eSec=findViewById(R.id.eSec);
        emailOtpLayout = findViewById(R.id.email_otp_layout);

        mobileNumber = findViewById(R.id.mobileNumber);
        mobileOtp = findViewById(R.id.mobileOtp);
        mOTPtimer=findViewById(R.id.mOTPtimer);
        mMin=findViewById(R.id.mMin);
        mSec =findViewById(R.id.mSec);
        mOTP_resend=findViewById(R.id.resend2);
        mobileOtpLayout = findViewById(R.id.mobile_Otp_layout);

        password = findViewById(R.id.password);

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(mobileNumber);

        genderSpinner = findViewById(R.id.genderSpinner);

        skip_registration= findViewById(R.id.skip);
        termsConditions = findViewById(R.id.termsConditions);
        registerButton = findViewById(R.id.registerButton);
        uploadProfilePictureButton = findViewById(R.id.uploadProfilePictureButton);
        showTermsButton = findViewById(R.id.showTermsButton);
    }

    // Set up Gender Spinner
    private void setupGenderSpinner() {
        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Prefer not to say");
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Other");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        genderSpinner.setAdapter(genderAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Gender selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    // Set up Button Click Listeners
    private void setupButtonListeners() {
        showTermsButton.setOnClickListener(view -> navigateToTermsAndConditions());
        uploadProfilePictureButton.setOnClickListener(view -> showImagePickerOptions());
        skip_registration.setOnClickListener(view -> navigateToMainActivity());

        registerButton.setOnClickListener(view -> {
            if (registerButton.getText().equals("Confirm")) {
                handleRegistrationConfirmation();
            } else {
                handleRegistrationSubmission();
            }
        });
    }

    // Handle Registration Confirmation (Step 1)
    private void handleRegistrationConfirmation() {
        String userId = uniqueUserId.getText().toString().trim();
        String name = fullName.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String mobileText = mobileNumber.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (!validateInputs(userId, name, emailText, mobileText, passwordText)) {
            return;
        }
        String fullPhoneNumber = ccp.getFullNumberWithPlus();
        HashMap<String, String> data = new HashMap<>();
        data.put("email", emailText);
        data.put("uid", userId);
        data.put("mobile" , fullPhoneNumber);
        data.put("name",name);

        Call<fetch_confirm_registration> call = retrofitInterface.confirm_Registration(data);
        call.enqueue(new Callback<fetch_confirm_registration>() {
            @Override
            public void onResponse(Call<fetch_confirm_registration> call, Response<fetch_confirm_registration> response) {
                if (response.code() == 200) {
                    // Email OTP has sent at backend
                    // Mobile OTP has sent at backend

                    // Calling timer class
                    timer.setMin(eMin);
                    timer.setSec(eSec);
                    timer.setMin(mMin);
                    timer.setSec(mSec);


                    disableInputFields();
                    registerButton.setText("Submit");
                    emailOtpLayout.setVisibility(View.VISIBLE);
                    mobileOtpLayout.setVisibility(View.VISIBLE);
                    showToast("OTP sent to your mobile and email.");
                } else {
                    showToast("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<fetch_confirm_registration> call, Throwable t) {
                showToast("Network error. Please try again.");
            }
        });
    }

    // Handle Registration Submission (Step 2)
    private void handleRegistrationSubmission() {
        String emailOtpText = emailOtp.getText().toString().trim();
        String mobileOtpText = mobileOtp.getText().toString().trim();

        if (!isValidOtp(emailOtpText) || !isValidOtp(mobileOtpText)) {
            showToast("Please enter valid 6-digit OTPs.");
            return;
        }

        //get the profile image from a user and put it in hashMap

        String fullPhoneNumber = ccp.getFullNumberWithPlus();
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email.getText().toString().trim());
        data.put("password", password.getText().toString().trim());
        data.put("gender", genderSpinner.getSelectedItem().toString());
        data.put("uid", uniqueUserId.getText().toString().trim());
        data.put("mobile", fullPhoneNumber);
        data.put("email_otp",emailOtpText);
        data.put("mobile_otp",mobileOtpText);

        showToast(email.getText().toString().trim());

        Call<fetch_submit_registration> call = retrofitInterface.submit_Registration(data);
        call.enqueue(new Callback<fetch_submit_registration>() {
            @Override
            public void onResponse(Call<fetch_submit_registration> call, Response<fetch_submit_registration> response) {
                if (response.code() == 200) {
                    showToast("Registration successful!");
                    navigateToMainActivity();
                } else {
                    showToast(Integer.toString(response.code()));
                    showToast("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<fetch_submit_registration> call, Throwable t) {
                showToast("Network error. Please try again.");
            }
        });
    }

    // Disable input fields after confirmation
    private void disableInputFields() {
        uniqueUserId.setEnabled(false);
        fullName.setEnabled(false);
        email.setEnabled(false);
        mobileNumber.setEnabled(false);
        password.setEnabled(false);
        ccp.setEnabled(false);
        genderSpinner.setEnabled(false);
    }

    // Validate all inputs
    private boolean validateInputs(String userId, String name, String emailText, String mobileText, String passwordText) {
        if (!isValidUsername(userId)) {
            showToast("Invalid User ID. Use 3-30 chars, letters, numbers, _ and . allowed.");
            return false;
        }
        if (TextUtils.isEmpty(name) || name.length() > 30) {
            showToast("Invalid Full Name. Max 30 characters allowed.");
            return false;
        }
        if (!isValidEmail(emailText)) {
            showToast("Invalid Email. Use Gmail, Yahoo, Outlook, or Curaj email.");
            return false;
        }
        if (!isValidIndianMobile(mobileText)) {
            showToast("Invalid Mobile Number. Use a valid Indian mobile number.");
            return false;
        }
        if (!isValidPassword(passwordText)) {
            showToast("Invalid Password. Use 8+ chars, 1 uppercase, 1 lowercase, 1 number, 1 special char.");
            return false;
        }
        if (!termsConditions.isChecked()) {
            showToast("You must agree to the Terms & Conditions.");
            return false;
        }
        return true;
    }

    // Navigate to Terms & Conditions
    private void navigateToTermsAndConditions() {
//        Intent intent = new Intent(this, terms_and_conditions.class);
//        startActivity(intent);
        setContentView(R.layout.activity_terms_and_conditions);
    }

    // Navigate to Main Activity
    private void navigateToMainActivity() {
        Intent intent = new Intent(registration.this, Home_Screen.class);
        startActivity(intent);
        finish();
    }


    // Validation Methods
    private boolean isValidUsername(String username) {
        String regex = "^(?!.*[_.]{2})(?![_.])[a-zA-Z0-9_.]{3,30}(?<![_.])$";
        return username.matches(regex);
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com|curaj\\.ac\\.in)$";
        return email.matches(regex);
    }

    private boolean isValidIndianMobile(String mobile) {
        return mobile.matches("^[6789]\\d{9}$");
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(regex);
    }

    private boolean isValidOtp(String otp) {
        return otp.matches("^\\d{6}$");
    }

    // Show Toast Message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    // Method to open the gallery for image selection

    private void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    openCamera();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        });
        builder.show();
    }

    // Method to open the camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a file to save the captured image
        File photoFile = createImageFile();
        if (photoFile != null) {
            imageUri = Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraLauncher.launch(intent);
        }
    }

    // Method to create a temporary file for the camera image
    private File createImageFile() {
        try {
            File storageDir = getExternalFilesDir("images");
            return File.createTempFile("IMG_", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Method to open the gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // ActivityResultLauncher for camera
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    uploadImage(imageUri); // Upload the captured image
                }
            });

    // ActivityResultLauncher for gallery
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    uploadImage(imageUri); // Upload the selected image
                }
            });

    // Method to upload the image
    private void uploadImage(Uri imageUri) {
        File imageFile = new File(imageUri.getPath());

        // Check file size (5MB = 5 * 1024 * 1024 bytes)
        long fileSizeInBytes = imageFile.length();
        long fileSizeInMB = fileSizeInBytes / (1024 * 1024);

        if (fileSizeInMB > 5) {
            Toast.makeText(this, "Image size must be less than 5MB", Toast.LENGTH_SHORT).show();
            return; // Exit if the file is too large
        }

//        // Create a MultipartBody.Part from the file
//        RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/*"));
//        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
//
//        // Get the Retrofit API service instance
//        ApiService apiService = RetrofitClient.getApiService();
//
//        // Perform the upload in a background thread
//        new Thread(() -> {
//            try {
//                // Execute the upload request
//                retrofit2.Response<ResponseBody> response = apiService.uploadImage(imagePart).execute();
//
//                // Handle the response on the UI thread
//                runOnUiThread(() -> {
//                    if (response.isSuccessful()) {
//                        Toast.makeText(MainActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload Error!", Toast.LENGTH_SHORT).show());
//            }
//        }).start();
    }




}