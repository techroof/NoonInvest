package com.techroof.nooninvest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.nooninvest.Authentication.LoginActivity;
import com.techroof.nooninvest.PayPal.PaypalIntegration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClassBFragment extends Fragment {

    TextView textViewInvestmentAmount, textViewDailyIncome;
    private FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    Button btnaddInvest;
    String category, uid,investmentAmount,classCategory,itemCategory,date,currentDate;
    float dailyInvestment;
    int totalProfitAmount;
    private String dailyinvest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view=inflater.inflate(R.layout.fragment_class_b, container, false);
        textViewInvestmentAmount = view.findViewById(R.id.investement_amount_ClassB);
        textViewDailyIncome = view.findViewById(R.id.txtview_dailyincome_ClassB);
        firestore = FirebaseFirestore.getInstance();
        btnaddInvest = view.findViewById(R.id.btn_addInv_ClassB);
        category = getActivity().getIntent().getExtras().getString("content");
        firebaseAuth = FirebaseAuth.getInstance();
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        getdata();
        String getdailyincome = textViewDailyIncome.getText().toString();
        btnaddInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                uid = firebaseAuth.getUid();
                //investmentAmount = textViewInvestmentAmount.getText().toString();
                classCategory = "B";
                itemCategory = category;
                date = currentDate;
//                String s = textViewDailyIncome.getText().toString();
                String s = dailyinvest;
                float IncDaily = Float.parseFloat(s);
                totalProfitAmount = 0;
                firestore.collection("wallets").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();
                            if(documentSnapshot.exists()){
                                Toast.makeText(getContext(), "you have already invested", Toast.LENGTH_SHORT).show();


                            }else{

                                SharedPreferences shrd= getActivity().getSharedPreferences("requestspayments", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= shrd.edit();
                                editor.putString("str",s);
                                editor.putString("uid",uid);
                                editor.putString("investmentAmount",investmentAmount);
                                editor.putString("classCategory",classCategory);
                                editor.putString("itemCategory",itemCategory);
                                editor.putString("date",date);
                                editor.putFloat("IncDaily",IncDaily);
                                editor.putFloat("totalProfitAmount",  totalProfitAmount);
                                editor.apply();
                                //addInvest(uid, investmentAmount, classCategory, itemCategory, date, IncDaily, totalProfitAmount);
                                Intent intentt = new Intent(getActivity(), PaypalIntegration.class);
                                getActivity().startActivity(intentt);
                            }

                        }
                    }
                });

                //for referal



                //Toast.makeText(getContext(), "" + currentDate, Toast.LENGTH_SHORT).show();
            }
        });




                /*Toast.makeText(getContext(), "" + IncDaily, Toast.LENGTH_SHORT).show();
                addInvest(uid, investmentAmount, classCategory, itemCategory, date, IncDaily, totalProfitAmount);
                Toast.makeText(getContext(), "" + currentDate, Toast.LENGTH_SHORT).show();
                Intent intentt = new Intent(getActivity(), AccountDetails.class);
                getActivity().startActivity(intentt);*/


        return view;

    }

    private void addInvest(String uId, String investmentAmount, String classCategory, String itemCategory, String Date, Float DailyInvestment, int TotalProfit) {

        String wallet_ID = firestore.collection("Wallet").document().getId();

        Map<String, Object> WalletMap = new HashMap<>();
        WalletMap.put("id", uId);
        WalletMap.put("investementAmount", investmentAmount);
        WalletMap.put("classCategory", classCategory);
        WalletMap.put("itemCategory", itemCategory);
        WalletMap.put("date", Date);
        WalletMap.put("dailyAmount", DailyInvestment);
        WalletMap.put("totalProfit", TotalProfit);

        firestore.collection("Wallets")
                .document(wallet_ID)
                .set(WalletMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //progressDialog.dismiss();
                            Toast.makeText(getContext(), "Investment Added", Toast.LENGTH_SHORT).show();
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

    private void getdata() {

        firestore.collection("InvestmentDetails")
                .whereEqualTo("category", category)
                .whereEqualTo("class", "Class B").
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        investmentAmount = document.getString("InvestmentRate");
                         dailyinvest = document.getString("DailyIncome");
                        uid = firebaseAuth.getUid();
                        //Toast.makeText(getContext(), " id" + uid, Toast.LENGTH_SHORT).show();
                        textViewInvestmentAmount.setText("$"+investmentAmount);
                        textViewDailyIncome.setText("$"+dailyinvest);

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