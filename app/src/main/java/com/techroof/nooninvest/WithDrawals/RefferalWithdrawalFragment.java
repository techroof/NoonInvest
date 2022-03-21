package com.techroof.nooninvest.WithDrawals;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.nooninvest.HomeActivity;
import com.techroof.nooninvest.R;

import java.util.HashMap;
import java.util.Map;


public class RefferalWithdrawalFragment extends Fragment {

    private TextView accountNumberText, dailyAmountText, totalAvailableText, activationDateText;
    private Button btnRqstamount;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private String AccountNumber;
    private String UName;
    private String withdrawalId;
    private final String Status = "Requested";
    private String activationDate;
    private String dailyAmount;
    private double WithDrawalamaount;
    private String UAccountNo;
    private DocumentReference ref;
    private String Uidd, Statuss, userWithdrawlStatus = "";


    public RefferalWithdrawalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_refferal_withdrawal, container, false);

        btnRqstamount = view.findViewById(R.id.btn_request_amount_refferals);

        accountNumberText = view.findViewById(R.id.tv_Account_No_Refferals);
        totalAvailableText = view.findViewById(R.id.referral_total_available_amount);
        activationDateText = view.findViewById(R.id.activation_date_referrals);
        dailyAmountText = view.findViewById(R.id.daily_amount_referrals);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        uid = FirebaseAuth.getInstance().getUid();
        getdata();
        getName();
        getTotalProfit();

        uid = firebaseAuth.getUid();
        AccountNumber = UAccountNo;
        userWithdrawlStatus = "False";

        firestore.collection("SentRequestsRefferals")
                .whereEqualTo("Uid", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentSnapshot = task.getResult();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Uidd = document.getString("Uid");
                        Statuss = document.getString("Status");
                        if (Statuss.equals("Requested") || Statuss.equals("Pending")) {
                            userWithdrawlStatus = "True";
                            //Toast.makeText(getContext(), "y"+userWithdrawlStatus, Toast.LENGTH_SHORT).show();
                        } else {
                            //userWithdrawlStatus = "False";
                            //Toast.makeText(getContext(), "n"+userWithdrawlStatus, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });
        btnRqstamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*if (userWithdrawlStatus.equals("False")) {
                    //adding
                    //Toast.makeText(getContext(),"TESTED",Toast.LENGTH_LONG).show();
                    if(WithDrawalamaount>=100){
                        //Toast.makeText(getContext(),"TESTEDy",Toast.LENGTH_LONG).show();

                        Addrequest(uid,UName,AccountNumber,Status,WithDrawalamaount);
                    }

                }else if(userWithdrawlStatus.equals("True")) {


                    Toast.makeText(getContext(), "you have already requested" + userWithdrawlStatus, Toast.LENGTH_SHORT).show();

                }
                Intent intent=new Intent(getContext(), HomeActivity.class);
                startActivity(intent);*/


                firestore.collection("refferals").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {

                                check();
                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                startActivity(intent);
                            } else {

                                Toast.makeText(getContext(), "You are not Referred", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }
        });
        return view;
    }

    private void getTotalProfit() {

        uid = FirebaseAuth.getInstance().getUid();
        firestore.collection("refferals").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isComplete()) {

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {

                        WithDrawalamaount = document.getDouble("totalProfit").doubleValue();
                        dailyAmount = String.valueOf(document.get("dailyAmount"));
                        activationDate = String.valueOf(document.get("ActivationDate"));

                        totalAvailableText.setText("$" + WithDrawalamaount);
                        dailyAmountText.setText("Daily Amount: $" + dailyAmount);
                        activationDateText.setText("Activation Date: " + activationDate);


                    } else {

                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }


        });
    }


    private void Addrequest(String uid, String uName, String accountNumber, String status, double WithdrawalAmounts) {
        ref = firestore.collection("SentRequestsRefferals").document();
        withdrawalId = ref.getId();
        Map<String, Object> AccountMap = new HashMap<>();
        AccountMap.put("Uid", uid);
        AccountMap.put("name", uName);
        AccountMap.put("AccountNumber", accountNumber);
        AccountMap.put("Status", status);
        AccountMap.put("withdrawalAmount", WithdrawalAmounts);
        AccountMap.put("WithDrawalId", withdrawalId);
        ref.set(AccountMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //progressDialog.dismiss();
                            Toast.makeText(getContext(), "Request Has been sent", Toast.LENGTH_LONG).show();
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


    private void getName() {

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

    private void getdata() {

        uid = FirebaseAuth.getInstance().getUid();
        firestore.collection("AccountNo")
                .whereEqualTo("id", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().isEmpty()) {

                        accountNumberText.setText("Account Number: "+"N/A");


                    } else {


                        for (QueryDocumentSnapshot document : task.getResult()) {

                            UAccountNo = document.getString("AccountNumber");
                            uid = firebaseAuth.getUid();
                            //Toast.makeText(getContext(), " id" + uid, Toast.LENGTH_SHORT).show();
                            accountNumberText.setText("Account Number: "+UAccountNo);

                        }
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

    public void check() {

        if (userWithdrawlStatus.equals("False")) {
            //adding
            if (WithDrawalamaount >= 100) {

                Addrequest(uid, UName, AccountNumber, Status, WithDrawalamaount);
            }

        } else if (userWithdrawlStatus.equals("True")) {


            Toast.makeText(getContext(), "you have already requested" + userWithdrawlStatus, Toast.LENGTH_SHORT).show();

        }
    }

}