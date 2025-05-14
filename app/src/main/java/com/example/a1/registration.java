package com.example.a1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class registration extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int PERMISSION_REQUEST_CODE = 102;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_MOBILE = "userMobile";
    private static final String KEY_PROFILE_PIC = "profilePic";
    private static final String KEY_USER_GENDER = "userGender";

    private Uri selectedImageUri;
    private ImageView profileImageView;

    // UI Elements
    private EditText uniqueUserId, fullName, email, emailOtp, mobileNumber, mobileOtp, password;
    private TextView eMin, eSec, mMin, mSec;
    private Spinner genderSpinner;
    private CheckBox termsConditions;
    private Button registerButton, uploadProfilePictureButton, showTermsButton, mOTP_resend, eOTP_resend;
    private LinearLayout emailOtpLayout, mobileOtpLayout, mOTPtimer, eOTPtimer;
    private CountryCodePicker ccp;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private timer timer = new timer();
    private SessionManager sessionManager;
    private Retrofit retrofit;
    private retrofit_interface retrofitInterface;
    private AlertDialog progressDialog;
    private String BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session manager
        sessionManager = new SessionManager(getApplicationContext());

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            finish();
            return;
        }

        setContentView(R.layout.activity_registration);

        BASE_URL = getString(R.string.local_host_server);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        initializeActivityResultLaunchers();
        initializeRetrofit();
        initializeViews();
        setupGenderSpinner();
        setupButtonListeners();
    }

    private void initializeActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && selectedImageUri != null) {
                        profileImageView.setImageURI(selectedImageUri);
                        uploadProfilePictureButton.setText("Change Profile Picture");
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        profileImageView.setImageURI(selectedImageUri);
                        uploadProfilePictureButton.setText("Change Profile Picture");
                    }
                });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    Boolean cameraGranted = permissions.get(Manifest.permission.CAMERA);
                    Boolean storageGranted = permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (cameraGranted != null && cameraGranted && storageGranted != null && storageGranted) {
                        showImagePickerOptions();
                    } else {
                        showToast("Permissions required to select image");
                    }
                });
    }

    private void initializeRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(retrofit_interface.class);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                permissionLauncher.launch(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (!allPermissionsGranted) {
                showToast("Required permissions were not granted");
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void initializeViews() {
        uniqueUserId = findViewById(R.id.uniqueUserId);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.et_email);
        emailOtp = findViewById(R.id.emailOtp);
        eOTPtimer = findViewById(R.id.eOTPtimer);
        eOTP_resend = findViewById(R.id.resend1);
        eMin = findViewById(R.id.eMin);
        eSec = findViewById(R.id.eSec);
        emailOtpLayout = findViewById(R.id.email_otp_layout);
        mobileNumber = findViewById(R.id.mobileNumber);
        mobileOtp = findViewById(R.id.mobileOtp);
        mOTPtimer = findViewById(R.id.mOTPtimer);
        mMin = findViewById(R.id.mMin);
        mSec = findViewById(R.id.mSec);
        mOTP_resend = findViewById(R.id.resend2);
        mobileOtpLayout = findViewById(R.id.mobile_Otp_layout);
        password = findViewById(R.id.password);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(mobileNumber);
        genderSpinner = findViewById(R.id.genderSpinner);
        termsConditions = findViewById(R.id.termsConditions);
        registerButton = findViewById(R.id.registerButton);
        uploadProfilePictureButton = findViewById(R.id.uploadProfilePictureButton);
        showTermsButton = findViewById(R.id.showTermsButton);
        profileImageView = findViewById(R.id.profileImageView);
    }

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

    private void setupButtonListeners() {
        showTermsButton.setOnClickListener(view -> navigateToTermsAndConditions());

        uploadProfilePictureButton.setOnClickListener(view -> {
            if (checkImagePermissions()) {
                showImagePickerOptions();
            }
        });

        registerButton.setOnClickListener(view -> {
            if (registerButton.getText().equals("Confirm")) {
                handleRegistrationConfirmation();
            } else {
                handleRegistrationSubmission();
            }
        });
    }

    private boolean checkImagePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
            return false;
        }
        return true;
    }

    private void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(new String[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    openCamera();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                selectedImageUri = FileProvider.getUriForFile(this,
                        getPackageName() + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
            return;
        }

        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoIntent.setType("image/*");
        galleryLauncher.launch(pickPhotoIntent);
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            return File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            Log.e("Registration", "Error creating image file", e);
            showToast("Could not create image file");
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    if (selectedImageUri != null) {
                        profileImageView.setImageURI(selectedImageUri);
                        uploadProfilePictureButton.setText("Change Profile Picture");
                    }
                    break;

                case GALLERY_REQUEST_CODE:
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        profileImageView.setImageURI(selectedImageUri);
                        uploadProfilePictureButton.setText("Change Profile Picture");
                    }
                    break;
            }
        }
    }

    private void handleRegistrationConfirmation() {
        if (!isNetworkAvailable()) {
            showToast("No internet connection available");
            return;
        }

        String userId = uniqueUserId.getText().toString().trim();
        String name = fullName.getText().toString();
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
        data.put("mobile", fullPhoneNumber);
        data.put("name", name);

        showProgressDialog("Verifying...");

        Call<Fetch_confirm_registration> call = retrofitInterface.confirm_Registration(data);
        call.enqueue(new Callback<Fetch_confirm_registration>() {
            @Override
            public void onResponse(Call<Fetch_confirm_registration> call, Response<Fetch_confirm_registration> response) {
                dismissProgressDialog();
                if (response.code() == 200) {
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
            public void onFailure(Call<Fetch_confirm_registration> call, Throwable t) {
                dismissProgressDialog();
                showToast("Network error. Please try again.");
                Log.e("Registration", "Error: ", t);
            }
        });
    }

    private void handleRegistrationSubmission() {
        if (!isNetworkAvailable()) {
            showToast("No internet connection available");
            return;
        }

        String emailOtpText = emailOtp.getText().toString().trim();
        String mobileOtpText = mobileOtp.getText().toString().trim();
        String encryptedPassword = EncryptionUtils.encrypt(password.getText().toString());

        if (!isValidOtp(emailOtpText) || !isValidOtp(mobileOtpText)) {
            showToast("Please enter valid 6-digit OTPs.");
            return;
        }

        String fullPhoneNumber = ccp.getFullNumberWithPlus();

        HashMap<String, String> data = new HashMap<>();
        data.put("email", email.getText().toString().trim());
        data.put("password", encryptedPassword);
        data.put("gender", genderSpinner.getSelectedItem().toString());
        data.put("uid", uniqueUserId.getText().toString().trim());
        data.put("mobile", fullPhoneNumber);
        data.put("email_otp", emailOtpText);
        data.put("mobile_otp", mobileOtpText);
        data.put("name", fullName.getText().toString());

        if (selectedImageUri != null) {
            String imageBase64 = convertImageToBase64(selectedImageUri);
            if (imageBase64 != null) {
                data.put("profile_pic", "data:image/jpeg;base64," + imageBase64);
            }
        }

        showProgressDialog("Registering...");

        Call<LoginResponse> call = retrofitInterface.submit_Registration(data);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                dismissProgressDialog();

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse registrationResponse = response.body();
                    if (registrationResponse.isSuccess()) {
                        // Save user details to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        // Store basic user info
                        editor.putString("userId", registrationResponse.getUser().getUserId());
                        editor.putString("token", registrationResponse.getToken());
                        editor.putString("email", registrationResponse.getUser().getEmail());
                        editor.putString("name", registrationResponse.getUser().getName());
                        editor.putString("mobile", registrationResponse.getUser().getMobile());
                        editor.putString("profilePic", registrationResponse.getUser().getProfilePic());
                        editor.putString("gender", registrationResponse.getUser().getGender());

                        // Store additional fields if available
                        if (registrationResponse.getBalance() != 0) {
                            editor.putFloat("balance", (float) registrationResponse.getBalance());
                        }

                        // Mark user as logged in
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        // Also store in SessionManager for consistency
                        sessionManager.createLoginSession(
                                registrationResponse.getToken(),
                                registrationResponse.getUser().getUserId(),
                                registrationResponse.getUser().getEmail(),
                                registrationResponse.getUser().getName(),
                                registrationResponse.getUser().getMobile(),
                                registrationResponse.getUser().getProfilePic(),
                                registrationResponse.getUser().getGender()
                        );

                        showToast("Registration successful!");
                        navigateToMainActivity();
                    } else {
                        showToast(registrationResponse.getMessage());
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            showToast("Error: " + errorBody);
                            Log.d("RegistrationError", errorBody);
                        } else {
                            showToast("Error: " + response.code());
                        }
                    } catch (Exception e) {
                        showToast("Error processing response");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dismissProgressDialog();
                showToast("Network error. Please try again.");
                Log.e("Registration", "Error: ", t);
            }
        });
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 500, 500);
            options.inJustDecodeBounds = false;

            inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            bitmap.recycle();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("ImageConversion", "Error converting image", e);
            return null;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void showProgressDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null);
        TextView progressText = view.findViewById(R.id.progress_text);
        progressText.setText(message);

        builder.setView(view);
        progressDialog = builder.create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void disableInputFields() {
        uniqueUserId.setEnabled(false);
        fullName.setEnabled(false);
        email.setEnabled(false);
        mobileNumber.setEnabled(false);
        password.setEnabled(false);
        ccp.setEnabled(false);
        genderSpinner.setEnabled(false);
    }

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

    private void navigateToTermsAndConditions() {
        setContentView(R.layout.activity_terms_and_conditions);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(registration.this, Home_Screen.class);
        startActivity(intent);
        finish();
    }

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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Session Manager class to handle user session
     */
    public static class SessionManager {
        private final SharedPreferences pref;
        private final SharedPreferences.Editor editor;
        private final Context context;

        public SessionManager(Context context) {
            this.context = context;
            pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
        }

        public void createLoginSession(String token, String userId, String email,
                                       String name, String mobile, String profilePic, String gender) {
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_AUTH_TOKEN, token);
            editor.putString(KEY_USER_ID, userId);
            editor.putString(KEY_USER_EMAIL, email);
            editor.putString(KEY_USER_NAME, name);
            editor.putString(KEY_USER_MOBILE, mobile);
            editor.putString(KEY_PROFILE_PIC, profilePic);
            editor.putString(KEY_USER_GENDER, gender);
            editor.commit();
        }

        public boolean isLoggedIn() {
            return pref.getBoolean(KEY_IS_LOGGED_IN, false);
        }

        public HashMap<String, String> getUserDetails() {
            HashMap<String, String> user = new HashMap<>();
            user.put(KEY_AUTH_TOKEN, pref.getString(KEY_AUTH_TOKEN, null));
            user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
            user.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, null));
            user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));
            user.put(KEY_USER_MOBILE, pref.getString(KEY_USER_MOBILE, null));
            user.put(KEY_PROFILE_PIC, pref.getString(KEY_PROFILE_PIC, null));
            user.put(KEY_USER_GENDER, pref.getString(KEY_USER_GENDER, null));
            return user;
        }

        public void logoutUser() {
            editor.clear();
            editor.commit();

            // After logout redirect user to Login Activity
            Intent intent = new Intent(context, Login_pg.class);
            // Closing all the Activities
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public String getAuthToken() {
            return pref.getString(KEY_AUTH_TOKEN, null);
        }
    }
}