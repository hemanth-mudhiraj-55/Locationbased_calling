package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_pg extends AppCompatActivity {
    private EditText uid, email, password;
    private CheckBox checkBox;
    private ImageButton next;
    private Button forgetPassword, skip, signup;
    private LinearLayout uidLayout, emailLayout, passwordLayout;
    private retrofit_interface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pg);

        initViews();
        setupRetrofit();
        setupClickListeners();
    }

    private void initViews() {
        uid = findViewById(R.id.uniqueUserId);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        checkBox = findViewById(R.id.cb_remember);
        next = findViewById(R.id.btn_login);
        forgetPassword = findViewById(R.id.forgetPassword);
        skip = findViewById(R.id.skip);
        signup = findViewById(R.id.btn_signup);

        uidLayout = findViewById(R.id.uid_layout);
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
    }

    private void setupRetrofit() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupClickListeners() {
        next.setOnClickListener(v -> attemptLogin());
        signup.setOnClickListener(v -> navigateToRegistration());
        skip.setOnClickListener(v -> navigateToMainActivity());
        forgetPassword.setOnClickListener(v -> navigateToRecovery());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            togglePasswordVisibility(isChecked);
        });
    }

    private void togglePasswordVisibility(boolean isChecked) {
        int inputType = isChecked ?
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        password.setInputType(inputType);
        password.setSelection(password.getText().length());
    }

    private void attemptLogin() {
        String uidText = uid.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (!validateInputs(uidText, emailText, passwordText)) {
            return;
        }

        String encryptedPassword = EncryptionUtils.encrypt(passwordText);
        HashMap<String, String> loginMap = new HashMap<>();
        loginMap.put("uid", uidText);
        loginMap.put("email", emailText);
        loginMap.put("Encrypted_password", encryptedPassword);

        apiService.login_next(loginMap).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    navigateToOtpVerification(emailText);
                } else {
                    showToast("Login failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Network error. Please try again.");
            }
        });
    }

    private boolean validateInputs(String uid, String email, String password) {
        // Same validation methods as before
        // ...
        return true;
    }

    private void navigateToOtpVerification(String email) {
        Intent intent = new Intent(Login_pg.this,FP_OTP_Verification_Page.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(Login_pg.this, Home_Screen.class);
        startActivity(intent);
        finish();
    }

    private void navigateToRegistration() {
        Intent intent = new Intent(Login_pg.this, registration.class);
        startActivity(intent);
    }

    private void navigateToRecovery() {
        startActivity(new Intent(Login_pg.this, Recovery_Page.class));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}