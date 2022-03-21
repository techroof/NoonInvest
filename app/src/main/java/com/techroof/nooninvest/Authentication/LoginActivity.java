package com.techroof.nooninvest.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.techroof.nooninvest.HomeActivity;
import com.techroof.nooninvest.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textInputLayout1, textInputLayoutt2;
    private FirebaseAuth mAuth;
    private Button loginBtn;
    private TextView tvMoveToRegistration;
    private ProgressDialog progressDialog;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputLayout1 = findViewById(R.id.email_login);
        textInputLayoutt2 = findViewById(R.id.pwd_login);
        tvMoveToRegistration = findViewById(R.id.txt_to_register);
        loginBtn = findViewById(R.id.btn_login_here);

        backBtn=findViewById(R.id.img_back_arrow);

        mAuth = FirebaseAuth.getInstance();
        //firebaseDynamicLinks=FirebaseDynamicLinks.getInstance();

        backBtn=findViewById(R.id.img_back_arrow);
        backBtn.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCanceledOnTouchOutside(false);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = textInputLayout1.getEditText().getText().toString();
                String password = textInputLayoutt2.getEditText().getText().toString();

                if (TextUtils.isEmpty(email)) {
                    textInputLayout1.setError("Enter Email");
                }
                if (TextUtils.isEmpty(password)) {
                    textInputLayoutt2.setError("Enter Password");
                }


                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    progressDialog.show();
                    signinhere(email, password);

                }


            }
        });

        tvMoveToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);

            }
        });
    }

    public void signinhere(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //email verification
                            if (mAuth.getCurrentUser().isEmailVerified()) {

                                progressDialog.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_LONG).show();

                                intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                                intent.putExtra("uid", mAuth.getCurrentUser().getUid());
                                startActivity(intent);

                            } else {

                                progressDialog.dismiss();
                                mAuth.signOut();
                                Toast.makeText(getApplicationContext(), "Your Email is not Verified", Toast.LENGTH_SHORT).show();
                            }


                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}