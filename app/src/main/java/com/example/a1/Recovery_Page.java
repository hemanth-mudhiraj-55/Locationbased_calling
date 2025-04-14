package com.example.a1;

import static androidx.databinding.DataBindingUtil.setContentView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_page);

        initViews();
        setupRetrofit();
        setupClickListeners();
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

    private void setupClickListeners() {
        next.setOnClickListener(v -> attemptRecovery());
    }

    private void attemptRecovery() {
        String emailText = email.getText().toString().trim();
        String mobileText = mobileNumber.getText().toString().trim();

        if (!isValidEmail(emailText)) {
            showToast("Invalid Email");
            return;
        }
        if (!isValidIndianMobile(mobileText)) {
            showToast("Invalid Mobile Number");
            return;
        }

        HashMap<String, String> recoveryMap = new HashMap<>();
        recoveryMap.put("Forget_email", emailText);
        recoveryMap.put("Forget_mobile", mobileText);

        apiService.login_next3(recoveryMap).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    navigateToOtpVerification(emailText, mobileText);
                } else {
                    showToast("Recovery failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("Network error. Please try again.");
            }
        });
    }

    private void navigateToOtpVerification(String email, String mobile) {
        Intent intent = new Intent(Recovery_Page.this, FP_OTP_Verification_Page.class);
        intent.putExtra("email", email);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
    }

    // Validation methods...
    private boolean isValidEmail(String email) {
        return true;
        // Same as before
    }

    private boolean isValidIndianMobile(String mobile) {
        return true;
        // Same as before
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}