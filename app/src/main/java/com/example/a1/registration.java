package com.example.a1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class registration extends AppCompatActivity {

    private EditText uniqueUserId, fullName, email, emailOtp, mobileNumber, mobileOtp, password;
    private Spinner genderSpinner;
    private CheckBox termsConditions;
    private Button registerButton, uploadProfilePictureButton, showTermsButton;
    String gender,phone_ccp ;
    private CountryCodePicker ccp;
    private LinearLayout email_otp_layout , mobile_otp_layout;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private  PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;
    FirebaseDatabase db;
    DatabaseReference reference;

    private int email_mobile_otp_verified=0;
    private Retrofit retrofit;
    private retrofit_interface retrofitinterface;
    private String BASE_URL="";







    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // ✅ FIXED `onCreate` METHOD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        retrofit =new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface = retrofit.create(retrofit_interface.class);

        // EditText fields
        uniqueUserId = findViewById(R.id.uniqueUserId);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.et_email);
        emailOtp = findViewById(R.id.emailOtp);
        email_otp_layout=findViewById(R.id.email_otp_layout);

        mobileNumber = findViewById(R.id.mobileNumber);
        mobileOtp = findViewById(R.id.mobileOtp);
        mobile_otp_layout = findViewById(R.id.mobile_Otp_layout);

        password = findViewById(R.id.password);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(mobileNumber);

        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------


        // Spinner ---- ✅ Verified

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


        // CheckBox - ✅ Verified
        termsConditions = findViewById(R.id.termsConditions);

        // Buttons - ✅ Verified
        registerButton = findViewById(R.id.registerButton);
        uploadProfilePictureButton = findViewById(R.id.uploadProfilePictureButton);
        showTermsButton = findViewById(R.id.showTermsButton);

        showTermsButton.setOnClickListener(term -> terms_and_condition());
        uploadProfilePictureButton.setOnClickListener(profile -> upload_profile());


        String userId = uniqueUserId.getText().toString().trim();
        String name = fullName.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String emailOtpText = emailOtp.getText().toString();
        String mobileText = mobileNumber.getText().toString();
        String mobileOtpText = mobileOtp.getText().toString();
        String passwordText = password.getText().toString().trim();








        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



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
                        showToast("Enter a valid Mobile Number");
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


                    HashMap<String,String> hash_mail_num= new HashMap<>();
                    hash_mail_num.put("email",emailText);
                    hash_mail_num.put("uid",userId);
                    hash_mail_num.put("mobile",mobileText);

                    Call<Void> call= retrofitinterface.confirm_Registration(hash_mail_num);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.code()==401){
                                showToast(response.message());
                                return;
                            }
                            if(response.code()==400){
                                showToast(response.message());
                                return;
                            }
                            if(response.code()==200){

                                mobile_otp();

                                showToast(response.message());

                                uniqueUserId.setEnabled(false);
                                fullName.setEnabled(false);
                                email.setEnabled(false);
                                mobileNumber.setEnabled(false);
                                password.setEnabled(false);
                                ccp.setEnabled(false);
                                genderSpinner.setEnabled(false);
                                registerButton.setText("Submit");
                                email_otp_layout.setVisibility(View.VISIBLE);
                                mobile_otp_layout.setVisibility(View.VISIBLE);


                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            showToast("something went wrong, Please try again");
                        }
                    });



//                    loadingBar.setTitle("OTP Verfication");
//                    loadingBar.setMessage("Verifing");
//                    loadingBar.setCanceledOnTouchOutside(false);
//                    loadingBar.show();




                }
                else{// if the button is Submit

                    // mail verification is written here

                    HashMap<String,String> hash_submit= new HashMap<>();
                    hash_submit.put("email",emailText);
                    hash_submit.put("password",passwordText);
                    hash_submit.put("gender",gender);
                   // hash_submit.put("profile_pic",);
                    hash_submit.put("uid",userId);
                    hash_submit.put("mobile",mobileText);
                    hash_submit.put("mail_otp",emailOtpText);



                    Call<Void> call= retrofitinterface.submit_Registration(hash_submit);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.code()==401){
                                showToast(response.message());
                                return;
                            }

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            showToast("something went wrong, Please try again");
                        }
                    });





                }

            }
        });


    }

    private void mobile_otp(){
        phone_ccp = ccp.getFullNumberWithPlus();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone_ccp)       // Phone number to verify
                        .setTimeout(180L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(registration.this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // Instant verification. In some cases the phone number can be instantly
                // verified without needing to send or enter a verification code.
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                showToast("Phone Number Doesn't exsit");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mResendToken = forceResendingToken;

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            email_mobile_otp_verified++;

                        } else {
                            // Sign in failed, display a message and update the UI
                            showToast("Error in verifing mobile OTP");
                        }
                    }
                });
    }


    private void senduserto_MainActivity(){
        Intent iHome = new Intent(registration.this,maps_home.class);
        startActivity(iHome);
        finish();
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
