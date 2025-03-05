package com.example.a1;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registration extends AppCompatActivity {

    private EditText uniqueUserId, fullName, email, emailOtp, mobileNumber, mobileOtp, password;
    private Spinner countrySpinner, genderSpinner;
    private CheckBox termsConditions;
    private Button registerButton, uploadProfilePictureButton, showTermsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Views
        uniqueUserId = findViewById(R.id.uniqueUserId);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        emailOtp = findViewById(R.id.emailOtp);
        mobileNumber = findViewById(R.id.mobileNumber);
        mobileOtp = findViewById(R.id.mobileOtp);
        password = findViewById(R.id.password);
        countrySpinner = findViewById(R.id.country);
        genderSpinner = findViewById(R.id.genderSpinner);
        termsConditions = findViewById(R.id.termsConditions);
        registerButton = findViewById(R.id.registerButton);
        uploadProfilePictureButton = findViewById(R.id.uploadProfilePictureButton);
        showTermsButton = findViewById(R.id.showTermsButton);

        // Apply Input Filters
        applyInputFilters();

        // Setup Gender Spinner
        setupGenderSpinner();

        // Set Click Listeners
        registerButton.setOnClickListener(v -> validateAndRegister());
    }

    private void applyInputFilters() {
        // Filter for Unique User ID (only letters, numbers, underscore, dot, max 30 characters)
        InputFilter uniqueUserIdFilter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != '_' && c != '.') {
                    return "";
                }
            }
            return null;
        };
        uniqueUserId.setFilters(new InputFilter[]{uniqueUserIdFilter, new InputFilter.LengthFilter(30)});

        // Filter for Full Name (only letters and spaces, max 30 characters)
        InputFilter onlyLettersFilter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isLetter(source.charAt(i)) && source.charAt(i) != ' ') {
                    return "";
                }
            }
            return null;
        };
        fullName.setFilters(new InputFilter[]{onlyLettersFilter, new InputFilter.LengthFilter(30)});
    }

    private void setupGenderSpinner() {
        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Other");
        genderList.add("Prefer not to say");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
    }

    private void validateAndRegister() {
        String userId = uniqueUserId.getText().toString().trim();
        String name = fullName.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String emailOtpText = emailOtp.getText().toString().trim();
        String mobileText = mobileNumber.getText().toString().trim();
        String mobileOtpText = mobileOtp.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        // Validations
        if (TextUtils.isEmpty(userId) || userId.length() > 30 || !isValidUsername(userId)) {
            showToast("Enter a valid Unique User ID (only letters, numbers, _ and . allowed, max 30 chars)");
            return;
        }
        if (TextUtils.isEmpty(name) || name.length() > 30) {
            showToast("Please enter a valid Full Name (only letters, max 30 chars)");
            return;
        }
        if (!isValidEmail(emailText)) {
            showToast("Enter a valid Email Address (e.g., Gmail, Yahoo, Outlook)");
            return;
        }
        if (!isValidOtp(emailOtpText)) {
            showToast("Enter a valid 6-digit Email OTP");
            return;
        }
        if (!isValidIndianMobile(mobileText)) {
            showToast("Enter a valid 10-digit Indian Mobile Number (starting with 9, 8, 7, or 6)");
            return;
        }
        if (!isValidOtp(mobileOtpText)) {
            showToast("Enter a valid 6-digit Mobile OTP");
            return;
        }
        if (!isValidPassword(passwordText)) {
            showToast("Password must have at least 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char");
            return;
        }
        if (!termsConditions.isChecked()) {
            showToast("You must agree to the Terms & Conditions");
            return;
        }

        // If all validations pass
        showToast("Registration Successful!");
    }

    // ✅ Validates Unique User ID
    private boolean isValidUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9_.]+$";
        return username.matches(usernameRegex);
    }

    // ✅ Validates Email with Allowed Domains
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|org|net|edu|gov|mil)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches() && (email.endsWith("@gmail.com") || email.endsWith("@yahoo.com") || email.endsWith("@outlook.com"));
    }

    // ✅ Validates 6-digit OTP
    private boolean isValidOtp(String otp) {
        return otp.matches("^\\d{6}$");
    }

    // ✅ Validates Indian Mobile Numbers (10 digits, starts with 9, 8, 7, or 6)
    private boolean isValidIndianMobile(String mobile) {
        return mobile.matches("^[6789]\\d{9}$");
    }

    // ✅ Validates Strong Password
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(passwordRegex);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}