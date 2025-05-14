package com.example.a1;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Recovery_Page extends AppCompatActivity {
    private EditText email, mobileNumber;
    private ImageButton next;
    private CountryCodePicker ccp;
    private retrofit_interface apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_page);

        initViews();
        setupRetrofit();
        setupClickListeners();
        initProgressDialog();
    }

    private void initViews() {
        email = findViewById(R.id.et_email);
        mobileNumber = findViewById(R.id.mobileNumber);
        next = findViewById(R.id.btn_recovery_submit);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(mobileNumber);
    }

    private void setupRetrofit() {
        apiService = RetrofitClient.getApiService();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCancelable(false);
    }

    private void setupClickListeners() {
        next.setOnClickListener(v -> attemptRecovery());
    }

    private void attemptRecovery() {
        String emailText = email.getText().toString().trim();
        String mobileText = mobileNumber.getText().toString().trim();
        String fullPhoneNumber = ccp.getFullNumberWithPlus();

        if (!isValidEmail(emailText)) {
            showToast("Invalid Email");
            return;
        }
        if (!isValidIndianMobile(mobileText)) {
            showToast("Invalid Mobile Number");
            return;
        }

        // Show loading dialog
        progressDialog.show();

        HashMap<String, String> recoveryMap = new HashMap<>();
        recoveryMap.put("Forget_email", emailText);
        recoveryMap.put("Forget_mobile", fullPhoneNumber);

        apiService.login_next3(recoveryMap).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Dismiss dialog regardless of response
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        navigateToOtpVerification(emailText, mobileText);
                    } else {
                        showToast(response.body().getMessage() != null ?
                                response.body().getMessage() : "Recovery failed. Please try again.");
                    }
                } else {
                    showToast("Server error. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                showToast("Network error. Please try again.");
            }
        });
    }

    private void navigateToOtpVerification(String email, String mobile) {
        Intent intent = new Intent(Recovery_Page.this, FP_OTP_Verification_Page.class);
        intent.putExtra("email", email);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
        finish(); // Optional: close this activity if not needed in back stack
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com|curaj\\.ac\\.in)$";
        return email.matches(regex);
    }

    private boolean isValidIndianMobile(String mobile) {
        return mobile.matches("^[6789]\\d{9}$");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        // Dismiss dialog to prevent memory leaks
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}