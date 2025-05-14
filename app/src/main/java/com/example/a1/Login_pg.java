package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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
    private retrofit_interface apiService;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        if (FP_OTP_Verification_Page.isUserLoggedIn(this)) {
            navigateToMainActivity();
            finish();
            return;
        }

        setContentView(R.layout.activity_login_pg);

        initViews();
        setupRetrofit();
        setupClickListeners();
    }

    private void initViews() {
        uid = findViewById(R.id.uniqueUserId);
        email = findViewById(R.id.email_id);
        password = findViewById(R.id.et_password);
        checkBox = findViewById(R.id.cb_remember);
        next = findViewById(R.id.btn_login);
        forgetPassword = findViewById(R.id.forgetPassword);
        skip = findViewById(R.id.skip);
        signup = findViewById(R.id.btn_signup);
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
        String passwordText = password.getText().toString();

        if (!validateInputs(uidText, emailText, passwordText)) {
            return;
        }

        showProgressDialog("Logging in...");
        String encryptedPassword = EncryptionUtils.encrypt(passwordText);

        HashMap<String, String> loginMap = new HashMap<>();
        loginMap.put("uid", uidText);
        loginMap.put("email", emailText);
        loginMap.put("Encrypted_password", encryptedPassword);

        apiService.login_next(loginMap).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    navigateToOtpVerification(emailText, uidText);
                } else {
                    showErrorDialog("Login Failed", "Invalid credentials. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dismissProgressDialog();
                showErrorDialog("Network Error", "Please check your internet connection and try again.");
                Log.e("LoginError", "Login failed");
            }
        });
    }

    private boolean validateInputs(String uid, String email, String password) {
        if (!isValidUsername(uid)) {
            this.uid.setError("Invalid User ID format");
            this.uid.requestFocus();
            return false;
        }
        if (!isValidEmail(email)) {
            this.email.setError("Invalid email format");
            this.email.requestFocus();
            return false;
        }
        if (!isValidPassword(password)) {
            this.password.setError("Password must contain 8+ chars with uppercase, lowercase, number and special char");
            this.password.requestFocus();
            return false;
        }
        return true;
    }

    private void showProgressDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void navigateToOtpVerification(String email, String UID) {
        Intent intent = new Intent(this, FP_OTP_Verification_Page.class);
        intent.putExtra("email", email);
        intent.putExtra("UID", UID);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, Home_Screen.class));
        finish();
    }

    private void navigateToRegistration() {

        startActivity(new Intent(this, registration.class));
    }

    private void navigateToRecovery() {
        startActivity(new Intent(this, Recovery_Page.class));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidUsername(String username) {
        String regex = "^(?!.*[_.]{2})(?![_.])[a-zA-Z0-9_.]{3,30}(?<![_.])$";
        return username.matches(regex);
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com|curaj\\.ac\\.in)$";
        return email.matches(regex);
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(regex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }
}