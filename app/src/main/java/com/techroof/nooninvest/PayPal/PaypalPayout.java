package com.techroof.nooninvest.PayPal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.nooninvest.R;

import java.util.HashMap;
import java.util.Map;

public class PaypalPayout extends AppCompatActivity {
    private TextView tvAccountnumber;
    private Button btnRqstamount;
    private FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String uid,AccountNumber,UName,Status="Requested";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_payout);
        btnRqstamount = findViewById(R.id.btn_request_amount);
        tvAccountnumber = findViewById(R.id.tv_Account_No);
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = FirebaseAuth.getInstance().getUid();
        getdata();
        getName();

        btnRqstamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uid = firebaseAuth.getUid();
                AccountNumber=tvAccountnumber.getText().toString();

                Addrequest(uid,UName,AccountNumber,Status);
            }
        });
    }



    private void getdata() {

        uid = FirebaseAuth.getInstance().getUid();
        firestore.collection("AccountNo")
                .whereEqualTo("id", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String UAccountNo = document.getString("AccountNumber");
                        uid = firebaseAuth.getUid();
                         Toast.makeText(getApplicationContext(), " id" + uid, Toast.LENGTH_SHORT).show();
                        tvAccountnumber.setText(UAccountNo);

                    }
                } else {
                    Log.d("d", "Error getting documents: ", task.getException());

                }
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });

    }


    private void Addrequest(String uId, String name,String accountNumber,String Status){


            Map<String, Object> AccountMap = new HashMap<>();
            AccountMap.put("id", uId);
            AccountMap.put("name", name);
            AccountMap.put("AccountNumber", accountNumber);
            AccountMap.put("Status",Status);


            firestore.collection("SentRequests")
                    .document(uId)
                    .set(AccountMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Account Number Added", Toast.LENGTH_LONG).show();
                                //Intent to home or previous activity
                                //Intent previousActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                                //startActivity(previousActivity);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("y", "onFailure: ");

                }
            });


        }

        private void getName(){

            uid = FirebaseAuth.getInstance().getUid();
            firestore.collection("users")
                    .whereEqualTo("id", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            UName = document.getString("name");

                        }
                    } else {
                        Log.d("d", "Error getting documents: ", task.getException());

                    }
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });


        }




    }




