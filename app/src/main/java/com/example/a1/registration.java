package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class registration extends AppCompatActivity {

    // UI Elements
    private EditText uniqueUserId, fullName, email, emailOtp, mobileNumber, mobileOtp, password;
    private Spinner genderSpinner;
    private CheckBox termsConditions;
    private Button registerButton, uploadProfilePictureButton, showTermsButton;
    private LinearLayout emailOtpLayout, mobileOtpLayout;
    private CountryCodePicker ccp;

    // Firebase Authentication
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    // Retrofit for API calls
    private Retrofit retrofit;
    private retrofit_interface retrofitInterface;
    private final String BASE_URL = "http://10.50.15.192:3000";

    // OTP Verification Flags
    private int emailOtpCheck = 0, mobileOtpCheck = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
        emailOtpLayout = findViewById(R.id.email_otp_layout);

        mobileNumber = findViewById(R.id.mobileNumber);
        mobileOtp = findViewById(R.id.mobileOtp);
        mobileOtpLayout = findViewById(R.id.mobile_Otp_layout);

        password = findViewById(R.id.password);
        ccp = findViewById(R.id.ccp);
        //ccp.registerCarrierNumberEditText(mobileNumber);

        genderSpinner = findViewById(R.id.genderSpinner);
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
        uploadProfilePictureButton.setOnClickListener(view -> uploadProfilePicture());

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

        HashMap<String, String> data = new HashMap<>();
        data.put("email", emailText);
        data.put("uid", userId);
        data.put("mobile", mobileText);
        data.put("name",name);

        Call<Void> call = retrofitInterface.confirm_Registration(data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    // Success: Proceed to OTP verification
                    disableInputFields();
                    registerButton.setText("Submit");
                    emailOtpLayout.setVisibility(View.VISIBLE);
                    mobileOtpLayout.setVisibility(View.VISIBLE);
                    sendOtp(); // Send mobile OTP
                    showToast("OTP sent to your mobile and email.");
                } else {
                    showToast("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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

        verifyOtp(mobileOtpText); // Verify mobile OTP
        // Email OTP verification is handled on the backend

        HashMap<String, String> data = new HashMap<>();
        data.put("email", email.getText().toString().trim());
        data.put("password", password.getText().toString().trim());
        data.put("gender", genderSpinner.getSelectedItem().toString());
        data.put("uid", uniqueUserId.getText().toString().trim());
        data.put("mobile", mobileNumber.getText().toString().trim());
        data.put("email_otp", emailOtpText);

        Call<Void> call = retrofitInterface.submit_Registration(data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    mobileOtpCheck = 1;
                    showToast("Registration successful!");
                    navigateToMainActivity();
                } else {
                    showToast("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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

    // Send OTP to mobile number
    private void sendOtp() {
        String phoneNumber = ccp.getFullNumberWithPlus()+"9392525718";

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(180L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(otpCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Verify OTP
    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithOtp(credential);
    }

    // Sign in with OTP
    private void signInWithOtp(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mobileOtpCheck = 1;
                        showToast("Mobile OTP verified successfully!");
                    } else {
                        showToast("Error verifying OTP. Please try again.");
                    }
                });
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
        Intent intent = new Intent(this, terms_and_conditions.class);
        startActivity(intent);
    }

    // Navigate to Main Activity
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, maps_home.class);
        startActivity(intent);
        finish();
    }

    // Upload Profile Picture (Placeholder)
    private void uploadProfilePicture() {
        showToast("Profile picture upload functionality not implemented yet.");
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

    // OTP Callbacks
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks otpCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    signInWithOtp(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    showToast("OTP verification failed: " + e.getMessage());
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    mVerificationId = verificationId;
                    mResendToken = token;
                    showToast("OTP sent successfully.");
                }
            };
}