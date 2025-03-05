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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registration extends AppCompatActivity {

    private EditText uniqueUserId, fullName, email, emailOtp, mobileNumber, mobileOtp, password;
    private Spinner genderSpinner;
    private CheckBox termsConditions;
    private Button registerButton, uploadProfilePictureButton, showTermsButton;
    String gender,phone_ccp ;
    private CountryCodePicker ccp;
    FirebaseDatabase db;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {  // ✅ FIXED `onCreate` METHOD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // EditText fields
        uniqueUserId = findViewById(R.id.uniqueUserId);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.et_email);
        emailOtp = findViewById(R.id.emailOtp);
        mobileNumber = findViewById(R.id.mobileNumber);
        mobileOtp = findViewById(R.id.mobileOtp);
        password = findViewById(R.id.password);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(mobileNumber);

        String helo;
        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------


        // Spinner

        genderSpinner = findViewById(R.id.genderSpinner);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Other");
        genderList.add("Prefer not to say");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        genderSpinner.setAdapter(genderAdapter);

        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------


        // CheckBox
        termsConditions = findViewById(R.id.termsConditions);

        // Buttons
        registerButton = findViewById(R.id.registerButton);
        uploadProfilePictureButton = findViewById(R.id.uploadProfilePictureButton);
        showTermsButton = findViewById(R.id.showTermsButton);

        showTermsButton.setOnClickListener(term -> terms_and_condition());
        uploadProfilePictureButton.setOnClickListener(profile -> upload_profile());








        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = uniqueUserId.getText().toString().trim();
                String name = fullName.getText().toString().trim();
                String emailText = email.getText().toString().trim();
                String emailOtpText = emailOtp.getText().toString();
                String mobileText = mobileNumber.getText().toString();
                String mobileOtpText = mobileOtp.getText().toString();
                String passwordText = password.getText().toString().trim();


                if (registerButton.getText().equals("Confirm")){

                    if (!isValidUsername(userId)) {
                        showToast("Enter a valid User ID (3-30 chars, letters, numbers, _ and . allowed, no leading/trailing _ or .)");
                        return;
                    }
                    if (TextUtils.isEmpty(name) || name.length() > 30) {
                        showToast("Enter a valid Full Name (max 30 characters)");
                        return;
                    }
                    if (!isValidEmail(emailText)) {
                        showToast("Enter a valid Gmail, Yahoo, Outlook, or Curaj Email Address");
                        return;
                    }
                    if (!isValidOtp(emailOtpText)) {
                        showToast("Enter a valid 6-digit Email OTP");
                        return;
                    }
                    if (!isValidIndianMobile(mobileText)) {
                        showToast("Enter a valid 10-digit Indian Mobile Number");
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

                    phone_ccp = ccp.getFullNumberWithPlus();
                }

                else{

                }


                // ✅ Username Validation Improved






                // ✅ Prevent Duplicate User Registrationa
                db = FirebaseDatabase.getInstance();
                reference = db.getReference("User_Details");

                reference.child(userId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        showToast("User ID already exists. Choose a different one.");
                    } else {
                        fetch_registration_data data_fetch = new fetch_registration_data(userId, name, emailText, emailOtpText, mobileText, mobileOtpText, passwordText, gender);
                        reference.child(userId).setValue(data_fetch).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task1) {
                                if (task1.isSuccessful()) {
                                    showToast("Registration successful");
                                    startActivity(new Intent(registration.this, maps_home.class));
                                    finish();
                                } else {
                                    showToast("Registration failed. Try again.");
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    // ✅ FIX: Users can now return from Terms & Conditions page
    private void terms_and_condition() {
        Intent iHome = new Intent(registration.this, terms_and_conditions.class);
        startActivity(iHome);
    }

    private void upload_profile() {
        // Handle profile upload logic
    }

    // ✅ Improved Username Validation (No leading/trailing _ or ., at least 3 chars)
    private boolean isValidUsername(String username) {
        String usernameRegex = "^(?!.*[_.]{2})(?![_\\.])[a-zA-Z0-9_.]{3,30}(?<![_.])$";
        return username.matches(usernameRegex);
    }

    // ✅ Strict Email Validation (Only allows Gmail, Yahoo, Outlook, and Curaj emails)
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com|curaj\\.ac\\.in)$";
        return email.matches(emailRegex);
    }

    // ✅ 6-Digit OTP Validation
    private boolean isValidOtp(String otp) {
        return otp.matches("^\\d{6}$");
    }

    // ✅ 10-Digit Indian Mobile Number Validation (Starts with 9, 8, 7, or 6)
    private boolean isValidIndianMobile(String mobile) {
        return mobile.matches("^[6789]\\d{9}$");
    }

    // ✅ Strong Password Validation (8+ chars, 1 uppercase, 1 lowercase, 1 number, 1 special char)
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(passwordRegex);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
