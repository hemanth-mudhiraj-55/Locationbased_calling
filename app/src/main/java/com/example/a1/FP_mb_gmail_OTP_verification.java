package com.example.a1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FP_mb_gmail_OTP_verification extends AppCompatActivity {
    private EditText emailOtp, mobileOtp;
    private Button resendEmailOtp, resendMobileOtp;
    private ImageButton verifyButton;
    private TextView emailTimerMin, emailTimerSec, mobileTimerMin, mobileTimerSec;
    private LinearLayout emailTimerLayout, mobileTimerLayout;
    private String email, mobile;
    private retrofit_interface apiService;
    private AlertDialog progressDialog;

    private CountDownTimer emailCountDownTimer, mobileCountDownTimer;
    private final long OTP_TIMEOUT = 120000; // 2 minutes
    private final long COUNTDOWN_INTERVAL = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isUserLoggedIn(this)) {
            navigateToMainActivity();
            finish();
            return;
        }

        setContentView(R.layout.activity_fp_mb_gmail_otp_verification);
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
        resendEmailOtp = findViewById(R.id.resend_email_otp);
        resendMobileOtp = findViewById(R.id.resend_mobile_otp);
        verifyButton = findViewById(R.id.btn_verify_otp);
        emailTimerLayout = findViewById(R.id.email_timer_layout);
        mobileTimerLayout = findViewById(R.id.mobile_timer_layout);
        emailTimerMin = findViewById(R.id.eMin);
        emailTimerSec = findViewById(R.id.eSec);
        mobileTimerMin = findViewById(R.id.mMin);
        mobileTimerSec = findViewById(R.id.mSec);

        resendEmailOtp.setVisibility(View.GONE);
        resendMobileOtp.setVisibility(View.GONE);
        emailTimerLayout.setVisibility(View.VISIBLE);
        mobileTimerLayout.setVisibility(View.VISIBLE);
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
        startMobileOtpTimer();
    }

    private void startEmailOtpTimer() {
        if (emailCountDownTimer != null) {
            emailCountDownTimer.cancel();
        }

        emailCountDownTimer = new CountDownTimer(OTP_TIMEOUT, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateEmailTimerText(millisUntilFinished);
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
                updateMobileTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                mobileTimerLayout.setVisibility(View.GONE);
                resendMobileOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void updateEmailTimerText(long millisUntilFinished) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
        emailTimerMin.setText(String.format("%02d", minutes));
        emailTimerSec.setText(String.format("%02d", seconds));
    }

    private void updateMobileTimerText(long millisUntilFinished) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
        mobileTimerMin.setText(String.format("%02d", minutes));
        mobileTimerSec.setText(String.format("%02d", seconds));
    }

    private void verifyOtp() {
        String emailOtpText = emailOtp.getText().toString().trim();
        String mobileOtpText = mobileOtp.getText().toString().trim();

        if (!isValidOtp(emailOtpText)) {
            emailOtp.setError("Enter valid 6-digit OTP");
            emailOtp.requestFocus();
            return;
        }

        if (!isValidOtp(mobileOtpText)) {
            mobileOtp.setError("Enter valid 6-digit OTP");
            mobileOtp.requestFocus();
            return;
        }

        showProgressDialog("Verifying OTPs...");
        verifyLoginOtps(emailOtpText, mobileOtpText);
    }

    private void verifyLoginOtps(String emailOtpText, String mobileOtpText) {
        HashMap<String, String> otpMap = new HashMap<>();
        otpMap.put("email", email);
        otpMap.put("email_otp", emailOtpText);
        otpMap.put("mobile", mobile);
        otpMap.put("mobile_otp", mobileOtpText);

        apiService.verify_dual_otp(otpMap).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                dismissProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    handleLoginResponse(response.body());
                } else {
                    showToast("OTP verification failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dismissProgressDialog();
                showToast("Network error. Please try again.");
            }
        });
    }

    private void handleLoginResponse(LoginResponse loginResponse) {
        if (loginResponse.isSuccess()) {
            saveUserSession(loginResponse);
            saveLoginData(loginResponse);
            navigateToMainActivity();
        } else {
            showToast(loginResponse.getMessage());
        }
    }

    private void resendEmailOtp() {
        HashMap<String, String> request = new HashMap<>();
        request.put("email", email);

        showProgressDialog("Resending Email OTP...");

        apiService.resend_email_otp(request).enqueue(new Callback<Fetch_confirm_registration>() {
            @Override
            public void onResponse(Call<Fetch_confirm_registration> call, Response<Fetch_confirm_registration> response) {
                dismissProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    showToast(response.body().getMessage());
                    resetEmailOtpTimer();
                } else {
                    showToast("Failed to resend Email OTP");
                }
            }

            @Override
            public void onFailure(Call<Fetch_confirm_registration> call, Throwable t) {
                dismissProgressDialog();
                showToast("Network error. Please try again.");
            }
        });
    }

    private void resendMobileOtp() {
        HashMap<String, String> request = new HashMap<>();
        request.put("mobile", mobile);

        showProgressDialog("Resending Mobile OTP...");

        apiService.resend_mobile_otp(request).enqueue(new Callback<Fetch_confirm_registration>() {
            @Override
            public void onResponse(Call<Fetch_confirm_registration> call, Response<Fetch_confirm_registration> response) {
                dismissProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    showToast(response.body().getMessage());
                    resetMobileOtpTimer();
                } else {
                    showToast("Failed to resend Mobile OTP");
                }
            }

            @Override
            public void onFailure(Call<Fetch_confirm_registration> call, Throwable t) {
                dismissProgressDialog();
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

    private void saveUserSession(LoginResponse loginResponse) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isLoggedIn", true);
        editor.putLong("loginTime", System.currentTimeMillis());
        editor.putString("email", loginResponse.getEmail());
        editor.putString("mobile", loginResponse.getMobile());
        editor.putString("userId", loginResponse.getUserId());
        editor.putString("name", loginResponse.getName());
        editor.putString("token", loginResponse.getToken());

        editor.apply();
    }

    private void saveLoginData(LoginResponse loginResponse) {
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("authToken", loginResponse.getToken());
        editor.putString("userName", loginResponse.getUser().getName());
        editor.putString("userEmail", loginResponse.getUser().getEmail());
        editor.putString("userMobile", loginResponse.getUser().getMobile());
        editor.apply();
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
        dismissProgressDialog();
    }

    public static boolean isUserLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        long loginTime = sharedPreferences.getLong("loginTime", 0);

        long sessionDuration = TimeUnit.DAYS.toMillis(7); // 7 days session
        boolean isSessionValid = (System.currentTimeMillis() - loginTime) < sessionDuration;

        return isLoggedIn && isSessionValid;
    }

    public static void logoutUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences appPrefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        appPrefs.edit().clear().apply();
    }
}