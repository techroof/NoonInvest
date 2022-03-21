package com.techroof.nooninvest.Authentication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techroof.nooninvest.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegistrationActivity extends AppCompatActivity {


    private Button btnSignUp;
    private ImageView backBtn;
    private EditText edtName, edtEmail, etPhoneNumber, etPassword, etConfirmPassword;
    private ProgressDialog  pd;
    private TextView btnLogin;
    private TextInputLayout textInputLayout1, textInputLayout2;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    String Name, Email, PhoneNumber;
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnSignUp = findViewById(R.id.reg_btn);
        edtName = findViewById(R.id.et_name);
        edtEmail = findViewById(R.id.et_email);
        btnLogin = findViewById(R.id.goto_login_btn);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        firestore = FirebaseFirestore.getInstance();

        backBtn=findViewById(R.id.img_back_arrow);
        backBtn.setVisibility(View.INVISIBLE);

        pd=new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String phone = etPhoneNumber.getText().toString();
                String passwrd = etPassword.getText().toString();
                String confirmpasswrd = etConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(name)){

                    edtName.setError("Enter Name");

                }

                if (TextUtils.isEmpty(email)){

                    edtEmail.setError("Enter Email");

                }

                if (TextUtils.isEmpty(phone)){

                    etPhoneNumber.setError("Enter Phone Number");

                }

                if (TextUtils.isEmpty(passwrd)){

                    etPassword.setError("Enter Password");

                }

                if (TextUtils.isEmpty(confirmpasswrd)){

                    etConfirmPassword.setError("Confirm Password");

                }

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)
                        && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(passwrd)
                        && !TextUtils.isEmpty(confirmpasswrd) )
                {

                    if (passwrd.equals(confirmpasswrd)) {

                        pd.show();

                        SignUp(name,email,phone,passwrd);

                    } else {

                        etConfirmPassword.setError("Password doesn't match");

                    }

                }


            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void SignUp(String name, String email, String phone, String passwrd) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, passwrd)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
/////verficationemail
                            verificationEmail();
                            ///verificationsucess=> adddata
                            //edtEmail.setText("");
                            //etPassword.setText("");


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.dismiss();
                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


/*


        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, */
        /* installIfNotAvailable *//*

                                "12"    */
        /* minimumVersion *//*
)
                        .build();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "EmailSent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
*/



    }

    private void addData(String Name, String Email, String PhoneNumber) {


        String referCode = randomString(5);


        // String Uid = firestore.collection("users").document().getId();
        String Uid = mAuth.getUid();

        Map<String, Object> userRegistrationMap = new HashMap<>();
        userRegistrationMap.put("name", Name);
        userRegistrationMap.put("email", Email);
        userRegistrationMap.put("phoneNumber", PhoneNumber);
        userRegistrationMap.put("id", Uid);
        userRegistrationMap.put("referedid", referCode);
        userRegistrationMap.put("userRId", Uid);

        firestore.collection("users")
                .document(Uid)
                .set(userRegistrationMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Registered Successfully. Verification link has been sent on you email", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                            //Toast.makeText(getApplicationContext(), "User added", Toast.LENGTH_SHORT).show();
                            //Intent to home or previous activity
                            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(login);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
                // progressDialog.dismiss();


            }
        });


    }

    private void verificationEmail() {

        final FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button
                        //findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                            Name = edtName.getText().toString();
                            Email = edtEmail.getText().toString();
                            PhoneNumber = etPhoneNumber.getText().toString();
                            addData(Name, Email, PhoneNumber);

                        } else {
                            // Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }


}