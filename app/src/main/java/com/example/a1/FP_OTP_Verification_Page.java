package com.example.a1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
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

public class FP_OTP_Verification_Page extends AppCompatActivity {
    private EditText emailOtp;
    private Button resendEmailOtp;
    private ImageButton verifyButton;
    private TextView emailTimerMin, emailTimerSec;
    private LinearLayout emailTimerLayout;
    private String email;
    private retrofit_interface apiService;
    private AlertDialog progressDialog;

    private CountDownTimer emailCountDownTimer;
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

        setContentView(R.layout.activity_fp_otp_verification_page);
        email = getIntent().getStringExtra("email");

        initViews();
        setupRetrofit();
        setupClickListeners();
        startOtpTimers();
    }

    private void initViews() {
        emailOtp = findViewById(R.id.email_Otp);
        resendEmailOtp = findViewById(R.id.resend_1);
        verifyButton = findViewById(R.id.btn_verify_otp);
        emailTimerLayout = findViewById(R.id.email_timer_layout);
        emailTimerMin = findViewById(R.id.eMin);
        emailTimerSec = findViewById(R.id.eSec);

        resendEmailOtp.setVisibility(View.GONE);
        emailTimerLayout.setVisibility(View.VISIBLE);
    }

    private void setupRetrofit() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupClickListeners() {
        verifyButton.setOnClickListener(v -> verifyOtp());
        resendEmailOtp.setOnClickListener(v -> resendEmailOtp());
    }

    private void startOtpTimers() {
        startEmailOtpTimer();
    }

    private void startEmailOtpTimer() {
        if (emailCountDownTimer != null) {
            emailCountDownTimer.cancel();
        }

        emailCountDownTimer = new CountDownTimer(OTP_TIMEOUT, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                emailTimerLayout.setVisibility(View.GONE);
                resendEmailOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void updateTimerText(long millisUntilFinished) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
        emailTimerMin.setText(String.format("%02d", minutes));
        emailTimerSec.setText(String.format("%02d", seconds));
    }

    private void verifyOtp() {
        String emailOtpText = emailOtp.getText().toString().trim();

        if (!isValidOtp(emailOtpText)) {
            emailOtp.setError("Enter valid 6-digit OTP");
            emailOtp.requestFocus();
            return;
        }

        showProgressDialog("Verifying OTP...");
        verifyLoginOtp(emailOtpText);
    }

    private void verifyLoginOtp(String emailOtpText) {
        HashMap<String, String> otpMap = new HashMap<>();
        otpMap.put("email", email);
        otpMap.put("otp", emailOtpText);

        apiService.login_next2(otpMap).enqueue(new Callback<LoginResponse>() {
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
            saveLoginData(this,loginResponse);
            navigateToMainActivity();
        } else {
            showToast(loginResponse.getMessage());
        }
    }

    private void resendEmailOtp() {
        HashMap<String, String> request = new HashMap<>();
        request.put("email", email);

        showProgressDialog("Resending OTP...");

        apiService.resend_email_otp(request).enqueue(new Callback<Fetch_confirm_registration>() {
            @Override
            public void onResponse(Call<Fetch_confirm_registration> call, Response<Fetch_confirm_registration> response) {
                dismissProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    showToast(response.body().getMessage());
                    resetEmailOtpTimer();
                } else {
                    showToast("Failed to resend OTP");
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
        Log.d("Email...............................................",loginResponse.getEmail());
        Log.d("Email...............................................",loginResponse.getUserId());
        editor.putString("email", loginResponse.getUser().getEmail());
        editor.putString("userId", loginResponse.getUser().getUserId());
        editor.putString("name", loginResponse.getUser().getName());
        editor.putString("mobile", loginResponse.getUser().getMobile());
        editor.putString("token", loginResponse.getToken());
        editor.putString("profilePic", loginResponse.getUser().getProfilePic());

        editor.apply();
    }

    private void saveLoginData(Context context, LoginResponse loginResponse) {
        // 1. Get SharedPreferences instance
        SharedPreferences preferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // 2. Log the values BEFORE storing
        Log.d("AuthDebug", "Before saving - isLoggedIn: " + preferences.getBoolean("isLoggedIn", false));
        Log.d("AuthDebug", "Before saving - authToken: " + preferences.getString("authToken", "null"));
        Log.d("AuthDebug", "Before saving - userId: " + preferences.getString("userId", "null"));

        // 3. Store the new values
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("authToken", loginResponse.getToken());
        editor.putString("userId", loginResponse.getUserId());

        // 4. Log what we're ABOUT to store
        Log.d("AuthDebug", "Attempting to store - isLoggedIn: true");
        Log.d("AuthDebug", "Attempting to store - authToken: " + loginResponse.getToken());
        Log.d("AuthDebug", "Attempting to store - userId: " + loginResponse.getUserId());

        // 5. Apply changes
        editor.apply();

        // 6. Verify IMMEDIATELY after storage (note: apply() is async)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // This runs after the apply() has completed
            Log.d("AuthDebug", "After saving - isLoggedIn: " + preferences.getBoolean("isLoggedIn", false));
            Log.d("AuthDebug", "After saving - authToken: " + preferences.getString("authToken", "null"));
            Log.d("AuthDebug", "After saving - userId: " + preferences.getString("userId", "null"));

            // Additional verification
            if (preferences.getString("authToken", null) != null) {
                Log.d("AuthDebug", "SUCCESS: authToken was properly stored");
            } else {
                Log.e("AuthDebug", "ERROR: authToken was not stored");
            }
        }, 100); // Small delay to ensure apply() completes
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