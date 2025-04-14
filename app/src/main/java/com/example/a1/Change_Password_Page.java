package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Change_Password_Page extends AppCompatActivity {
    private EditText newPassword, confirmPassword;
    private Button confirmChange;
    private String email;
    private retrofit_interface apiService;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_page);

        // Handle back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Your back button handling logic here
                // By default, just finish the activity (same as super.onBackPressed())
                finish();
            }
        });

        // Get email from intent
        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            showToast("Invalid email address");
            finish();
            return;
        }

        initViews();
        setupRetrofit();
        setupClickListeners();
    }

    private void initViews() {
        newPassword = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmChange = findViewById(R.id.confirmChange);
    }

    private void setupRetrofit() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupClickListeners() {
        confirmChange.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String passwordText = newPassword.getText().toString().trim();
        String confirmText = confirmPassword.getText().toString().trim();

        if (!validateInputs(passwordText, confirmText)) {
            return;
        }

        showProgressDialog("Changing password...");

        HashMap<String, String> passwordMap = new HashMap<>();
        passwordMap.put("email", email);
        passwordMap.put("new_password", EncryptionUtils.encrypt(passwordText));

        apiService.login_Password_change(passwordMap).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    showToast("Password changed successfully");
                    navigateToLogin();
                } else {
                    showToast("Password change failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dismissProgressDialog();
                if (!call.isCanceled()) {
                    showToast("Network error. Please try again.");
                }
            }
        });
    }

    private boolean validateInputs(String password, String confirmPassword) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please enter both password fields");
            return false;
        }

        if (!isValidPassword(password)) {
            showToast("Password must be 8+ characters with uppercase, lowercase, number, and special character");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(passwordPattern);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, Login_pg.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgressDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null);
        TextView progressText = dialogView.findViewById(R.id.progress_text);
        progressText.setText(message);

        builder.setView(dialogView);
        progressDialog = builder.create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}