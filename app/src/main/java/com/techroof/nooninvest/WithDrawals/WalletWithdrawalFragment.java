package com.techroof.nooninvest.WithDrawals;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.techroof.nooninvest.PayPal.PaypalIntegration;
import com.techroof.nooninvest.R;

import java.util.HashMap;
import java.util.Map;


public class WalletWithdrawalFragment extends Fragment {


    private TextView tvAccountnumber, noWalletText,availableAmountText, tvName, tvEmail, tvPhoneNumber, tvClasscategory, tvdailyAmount, tvdate,
            tvinvestmentAmount, tvitemCategory, tvtotalProfit,tvBankName;
    private Button btnRqstamount;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private String AccountNumber;
    private String UName;
    private String withdrawalId;
    private String bankName;
    private final String Status = "Requested";
    private double WithDrawalamaount;
    private DocumentReference ref;
    private String Uidd, Statuss, userWithdrawlStatus = "";
    private ConstraintLayout walletCl,investmentCl;

    //commit
    //wallet profile
    String Name, Email, PhoneNumber, ClassCategory, DailyAmount, Date, InvestmentAmount, ItemCategory, TotalProfit, uId,BankName;



    public WalletWithdrawalFragment() {
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
        View view = inflater.inflate(R.layout.fragment_wallet_withdrawal, container, false);

        btnRqstamount = view.findViewById(R.id.btn_request_amount_wallets);
        tvAccountnumber = view.findViewById(R.id.tv_Account_No_Wallets);
        walletCl = view.findViewById(R.id.wallet_cl);
        investmentCl=view.findViewById(R.id.investment_cl);
        noWalletText = view.findViewById(R.id.no_wallet_text);
        availableAmountText=view.findViewById(R.id.withdrawl_amount_text);
        tvBankName=view.findViewById(R.id.tv_Bank_Name_Wallets);
        //wallet profile
        tvName = view.findViewById(R.id.UserName);
        tvEmail = view.findViewById(R.id.user_email);
        tvPhoneNumber = view.findViewById(R.id.user_phoneno);
        tvClasscategory = view.findViewById(R.id.user_class_category);
        tvdailyAmount = view.findViewById(R.id.user_daily_amount);
        tvdate = view.findViewById(R.id.user_date);
        tvinvestmentAmount = view.findViewById(R.id.user_investment_amount);
        tvitemCategory = view.findViewById(R.id.user_item_category);
        tvtotalProfit = view.findViewById(R.id.user_net_profit);

        Name = tvName.getText().toString();
        Email = tvEmail.getText().toString();
        PhoneNumber = tvPhoneNumber.getText().toString();
        ClassCategory = tvClasscategory.getText().toString();
        DailyAmount = tvdailyAmount.getText().toString();
        Date = tvdate.getText().toString();
        InvestmentAmount = tvinvestmentAmount.getText().toString();
        ItemCategory = tvitemCategory.getText().toString();
        TotalProfit = tvtotalProfit.getText().toString();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = FirebaseAuth.getInstance().getUid();

        getdata();
        getName();
        getTotalProfit();
        getuserData();

        //method


        firestore.collection("wallets").document(uId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    DocumentSnapshot documentSnapshot= task.getResult();
                    if(documentSnapshot.exists()){

                        getUserWalletData();

                        noWalletText.setVisibility(View.INVISIBLE);
                        investmentCl.setVisibility(View.VISIBLE);
                        //  Intent intent=new Intent(getContext(), HomeActivity.class);
                        // startActivity(intent);

                    }else{

                        investmentCl.setVisibility(View.INVISIBLE);
                        noWalletText.setVisibility(View.VISIBLE);

                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });




        uid = firebaseAuth.getUid();
        AccountNumber = tvAccountnumber.getText().toString();
        userWithdrawlStatus = "False";

        firestore.collection("SentRequestsWallets")
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

                        } else {

                            //userWithdrawlStatus = "False";
                        }
                    }

                }
            }
        });

        btnRqstamount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (userWithdrawlStatus.equals("False")) {
                    //adding
                    //Toast.makeText(getContext(),"Your withdrawal limit is not reached yet",Toast.LENGTH_LONG).show();
                    if (WithDrawalamaount >= 100) {
                        //Toast.makeText(getContext(),"TESTEDy",Toast.LENGTH_LONG).show();

                        Addrequest(uid, UName, AccountNumber, Status, WithDrawalamaount,bankName);


                    }
                    if(WithDrawalamaount<100){

                        Toast.makeText(getContext(),"Your withdrawal limit is not reached yet",Toast.LENGTH_LONG).show();

                    }

                } else if (userWithdrawlStatus.equals("True")) {


                    Toast.makeText(getContext(), "you have already requested" + userWithdrawlStatus, Toast.LENGTH_SHORT).show();

                }
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);


                //checking
                /*firestore.collection("SentRequestsWallets").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                if(documentSnapshot.get(uid)!=null){

                                    Toast.makeText(getContext(), "you have already requested", Toast.LENGTH_SHORT).show();

                                }


                            } else if (WithDrawalamaount >= 100.00) {

                                Addrequest(uid, UName, AccountNumber, Status, WithDrawalamaount);

                            } else {

                                Toast.makeText(getContext(), "Your Withdrawal Limit is not reached", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });*/


            }


        });

        return view;
    }

    private void getTotalProfit() {

        uid = FirebaseAuth.getInstance().getUid();

        firestore.collection("wallets").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isComplete()) {

                    DocumentSnapshot document = task.getResult();

                    if (!document.exists()) {

                        noWalletText.setVisibility(View.VISIBLE);
                        walletCl.setVisibility(View.GONE);

                    } else {

                        if (document != null) {

                            WithDrawalamaount = document.getDouble("totalProfit").doubleValue();

                            noWalletText.setVisibility(View.GONE);
                            walletCl.setVisibility(View.VISIBLE);


                        } else {
                            Log.d("LOGGER", "No such document");
                        }

                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }


        });
    }

    private void Addrequest(String uid, String uName, String accountNumber, String status, double WithdrawalAmounts,String BankName) {
        ref = firestore.collection("SentRequestsWallets").document();
        withdrawalId = ref.getId();
        Map<String, Object> AccountMap = new HashMap<>();
        AccountMap.put("Uid", uid);
        AccountMap.put("name", uName);
        AccountMap.put("AccountNumber", accountNumber);
        AccountMap.put("Status", status);
        AccountMap.put("withdrawalAmount", WithdrawalAmounts);
        AccountMap.put("WithDrawalId", withdrawalId);
        AccountMap.put("BankName",bankName);
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

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String UAccountNo = document.getString("AccountNumber");
                        String bnkname=document.getString("BankName");
                        uid = firebaseAuth.getUid();
                        // Toast.makeText(getContext(), " id" + uid, Toast.LENGTH_SHORT).show();
                        tvAccountnumber.setText(UAccountNo);
                        tvBankName.setText(bnkname);
                        //availableAmountText.setText("$" + WithDrawalamaount);
                        AccountNumber = tvAccountnumber.getText().toString();
                        BankName=tvBankName.getText().toString();

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

    //profile details

    void getuserData() {

        uId = FirebaseAuth.getInstance().getUid();
        firestore.collection("users")
                .whereEqualTo("id", uId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String UName = document.getString("name");
                        String UEmail = document.getString("email");
                        String UPhoneNumber = document.getString("phoneNumber");
                        uId = firebaseAuth.getUid();
                        // Toast.makeText(getContext(), " id" + uId, Toast.LENGTH_SHORT).show();
                        tvName.setText(UName);
                        tvEmail.setText(UEmail);
                        tvPhoneNumber.setText(UPhoneNumber);

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


    //get user wallet data
    private void getUserWalletData() {

        uId = FirebaseAuth.getInstance().getUid();
        firestore.collection("wallets")
                .whereEqualTo("id", uId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String UInvestementAmount = document.getString("investementAmount");
                        String UClassCategory = document.getString("classCategory");
                        String UItemCategory = document.getString("itemCategory");
                        String UDate = document.getString("date");
                        double UDailyAmount = document.getDouble("dailyAmount").doubleValue();
                        String sDailyAmount = String.valueOf(UDailyAmount);
                        //int UDailyAmount= parseInt(document.get("handicap"));
                        Double UTotalProfit = document.getDouble("totalProfit");
                        uId = firebaseAuth.getUid();
                        String sTotalProfit = String.valueOf(UTotalProfit);
                        // Toast.makeText(getContext(), " id" + uId, Toast.LENGTH_SHORT).show();
                        tvinvestmentAmount.setText(UInvestementAmount);
                        tvClasscategory.setText(UClassCategory);
                        tvitemCategory.setText(UItemCategory);
                        tvdate.setText(UDate);
                        tvdailyAmount.setText(sDailyAmount);
                        //Toast.makeText(getContext(), "" + sDailyAmount, Toast.LENGTH_LONG).show();
                        tvtotalProfit.setText(sTotalProfit);
                        availableAmountText.setText("$"+sTotalProfit);


                    }
                } else {
                    Log.d("d", "Error getting documents: ", task.getException());

                }
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }





}