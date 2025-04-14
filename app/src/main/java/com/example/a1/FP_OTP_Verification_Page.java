package com.example.a1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FP_OTP_Verification_Page extends AppCompatActivity {
    private EditText emailOtp, mobileOtp;
    private Button resendEmailOtp, resendMobileOtp;
    private ImageButton verifyButton;
    private TextView emailTimerMin, emailTimerSec, mobileTimerMin, mobileTimerSec;
    private LinearLayout mobileOtpLayout, emailTimerLayout, mobileTimerLayout;
    private String email, mobile;
    private retrofit_interface apiService;

    private CountDownTimer emailCountDownTimer, mobileCountDownTimer;
    private final long OTP_TIMEOUT = 120000; // 2 minutes in milliseconds
    private final long COUNTDOWN_INTERVAL = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fp_otp_verification_page);

        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");

        initViews();
        setupRetrofit();
        setupClickListeners();
        startOtpTimers();
    }

    private void initViews() {
        emailOtp = findViewById(R.id.email_Otp);
        mobileOtp = findViewById(R.id.mobile_Otp);
        resendEmailOtp = findViewById(R.id.resend_1);
        resendMobileOtp = findViewById(R.id.resend_2);
        verifyButton = findViewById(R.id.btn_verify_otp);
        mobileOtpLayout = findViewById(R.id.mobile_otp_layout);
        emailTimerLayout = findViewById(R.id.email_timer_layout);
        mobileTimerLayout = findViewById(R.id.mobile_timer_layout);

        emailTimerMin = findViewById(R.id.eMin);
        emailTimerSec = findViewById(R.id.eSec);
        mobileTimerMin = findViewById(R.id.eMin2);
        mobileTimerSec = findViewById(R.id.eSec2);

        // Initially hide resend buttons and show timers
        resendEmailOtp.setVisibility(View.GONE);
        resendMobileOtp.setVisibility(View.GONE);
        emailTimerLayout.setVisibility(View.VISIBLE);

        if (mobile != null && !mobile.isEmpty()) {
            mobileTimerLayout.setVisibility(View.VISIBLE);
        } else {
            mobileOtpLayout.setVisibility(View.GONE);
            mobileTimerLayout.setVisibility(View.GONE);
        }
    }

    private void setupRetrofit() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupClickListeners() {
        verifyButton.setOnClickListener(v -> verifyOtp());
        resendEmailOtp.setOnClickListener(v -> resendEmailOtp());
        resendMobileOtp.setOnClickListener(v -> resendMobileOtp());
    }

    private void startOtpTimers() {
        startEmailOtpTimer();
        if (mobile != null && !mobile.isEmpty()) {
            startMobileOtpTimer();
        }
    }

    private void startEmailOtpTimer() {
        if (emailCountDownTimer != null) {
            emailCountDownTimer.cancel();
        }

        emailCountDownTimer = new CountDownTimer(OTP_TIMEOUT, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished, emailTimerMin, emailTimerSec);
            }

            @Override
            public void onFinish() {
                emailTimerLayout.setVisibility(View.GONE);
                resendEmailOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void startMobileOtpTimer() {
        if (mobileCountDownTimer != null) {
            mobileCountDownTimer.cancel();
        }

        mobileCountDownTimer = new CountDownTimer(OTP_TIMEOUT, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished, mobileTimerMin, mobileTimerSec);
            }

            @Override
            public void onFinish() {
                mobileTimerLayout.setVisibility(View.GONE);
                resendMobileOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void updateTimerText(long millisUntilFinished, TextView minutesView, TextView secondsView) {
        long minutes = (millisUntilFinished / 1000) / 60;
        long seconds = (millisUntilFinished / 1000) % 60;
        minutesView.setText(String.format("%02d", minutes));
        secondsView.setText(String.format("%02d", seconds));
    }

    private void verifyOtp() {
        String emailOtpText = emailOtp.getText().toString().trim();
        String mobileOtpText = mobileOtp.getText().toString().trim();

        if (!isValidOtp(emailOtpText)) {
            emailOtp.setError("Enter valid 6-digit OTP");
            emailOtp.requestFocus();
            return;
        }

        if (mobile != null && !mobile.isEmpty() && !isValidOtp(mobileOtpText)) {
            mobileOtp.setError("Enter valid 6-digit OTP");
            mobileOtp.requestFocus();
            return;
        }

        if (mobile != null && !mobile.isEmpty()) {
            verifyRecoveryOtp(emailOtpText, mobileOtpText);
        } else {
            verifyLoginOtp(emailOtpText);
        }
    }

    private void verifyRecoveryOtp(String emailOtpText, String mobileOtpText) {
        HashMap<String, String> otpMap = new HashMap<>();
        otpMap.put("Forget_Mobile_OTP", mobileOtpText);
        otpMap.put("Forget_Mobile", mobile);
        otpMap.put("Forget_Email", email);
        otpMap.put("Forget_Email_OTP", emailOtpText);

        showProgress("Verifying OTPs...");

        apiService.login_next4(otpMap).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    navigateToChangePassword();
                } else {
                    showToast("OTP verification failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dismissProgress();
                showToast("Network error. Please check your connection.");
            }
        });
    }

    private void verifyLoginOtp(String emailOtpText) {
        HashMap<String, String> otpMap = new HashMap<>();
        otpMap.put("email", email);
        otpMap.put("otp", emailOtpText);

        showProgress("Verifying OTP...");

        apiService.login_next2(otpMap).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                dismissProgress();
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        saveLoginData(loginResponse);
                        navigateToMainActivity();
                    } else {
                        showToast(loginResponse.getMessage());
                    }
                } else {
                    showToast("OTP verification failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dismissProgress();
                showToast("Network error. Please try again.");
            }
        });
    }

    // Update the resendEmailOtp method
    private void resendEmailOtp() {
        HashMap<String, String> request = new HashMap<>();
        request.put("email", email);

        showProgress("Resending OTP...");

        apiService.resend_email_otp(request).enqueue(new Callback<Fetch_confirm_registration>() {
            @Override
            public void onResponse(Call<Fetch_confirm_registration> call, Response<Fetch_confirm_registration> response) {
                dismissProgress();
                if (response.isSuccessful() && response.body() != null) {
                    Fetch_confirm_registration registrationResponse = response.body();
                    showToast(registrationResponse.getMessage());
                    resetEmailOtpTimer();
                } else {
                    String errorMessage = "Failed to resend OTP";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    showToast(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Fetch_confirm_registration> call, Throwable t) {
                dismissProgress();
                showToast("Network error. Please try again.");
            }
        });
    }

    private void resendMobileOtp() {
        if (mobile == null || mobile.isEmpty()) {
            return;
        }

        HashMap<String, String> request = new HashMap<>();
        request.put("mobile", mobile);

        showProgress("Resending OTP...");

        apiService.resend_mobile_otp(request).enqueue(new Callback<Fetch_confirm_registration>() {
            @Override
            public void onResponse(Call<Fetch_confirm_registration> call, Response<Fetch_confirm_registration> response) {
                dismissProgress();
                if (response.isSuccessful() && response.body() != null) {
                    Fetch_confirm_registration registrationResponse = response.body();
                    showToast(registrationResponse.getMessage());
                    resetMobileOtpTimer();
                } else {
                    String errorMessage = "Failed to resend OTP";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    showToast(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Fetch_confirm_registration> call, Throwable t) {
                dismissProgress();
                showToast("Network error. Please try again.");
            }
        });
    }

    private void resetEmailOtpTimer() {
        emailOtp.setText("");
        resendEmailOtp.setVisibility(View.GONE);
        emailTimerLayout.setVisibility(View.VISIBLE);
        startEmailOtpTimer();
    }

    private void resetMobileOtpTimer() {
        mobileOtp.setText("");
        resendMobileOtp.setVisibility(View.GONE);
        mobileTimerLayout.setVisibility(View.VISIBLE);
        startMobileOtpTimer();
    }

    private void saveLoginData(LoginResponse loginResponse) {
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("authToken", loginResponse.getToken());
        editor.putString("userName", loginResponse.getUser().getName());
        editor.putString("userEmail", loginResponse.getUser().getEmail());
        editor.apply();
    }

    private void navigateToChangePassword() {
        Intent intent = new Intent(this, Change_Password_Page.class);
        intent.putExtra("email", email);
        if (mobile != null) {
            intent.putExtra("mobile", mobile);
        }
        startActivity(intent);
        finish();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, Home_Screen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private boolean isValidOtp(String otp) {
        return otp != null && otp.length() == 6 && otp.matches("\\d+");
    }

    private void showProgress(String message) {
        // Implement your progress dialog
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void dismissProgress() {
        // Dismiss your progress dialog
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (emailCountDownTimer != null) {
            emailCountDownTimer.cancel();
        }
        if (mobileCountDownTimer != null) {
            mobileCountDownTimer.cancel();
        }
    }
}