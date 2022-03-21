package com.techroof.nooninvest;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.techroof.nooninvest.BackgroundServices.BackgroundServices;
import com.techroof.nooninvest.PayPal.PaypalIntegration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class ClassAFragment extends Fragment {

    TextView textViewInvestmentAmount, textViewDailyIncome;
    private FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    Button btnaddInvest;
    String category, uid, investmentAmount, classCategory, itemCategory, date, currentDate;
    float dailyInvestment;
    float totalProfitAmount;
    double updateTotalProfitAmount = 1.25;
    int Refferals = 0;
    double getTotalProfitAmount;
    double getDailyAmount;
    double addTotalProfit;
    private String dailyinvest;

    Date date1;

    //new
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_class_a, container, false);
        textViewInvestmentAmount = view.findViewById(R.id.investement_amount);
        textViewDailyIncome = view.findViewById(R.id.txtview_dailyincome);
        firestore = FirebaseFirestore.getInstance();
        btnaddInvest = view.findViewById(R.id.btn_addInv);
        category = getActivity().getIntent().getExtras().getString("content");
        firebaseAuth = FirebaseAuth.getInstance();
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        getdata();
        //getTotalProfit();
        String getdailyincome = textViewDailyIncome.getText().toString();

        btnaddInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uid = firebaseAuth.getUid();
                //investmentAmount = textViewInvestmentAmount.getText().toString();
                classCategory = "A";
                itemCategory = category;
                date = currentDate;
                //String s = textViewDailyIncome.getText().toString();
                String s = dailyinvest;
                float IncDaily = Float.parseFloat(s);
                totalProfitAmount = 0;
                //Toast.makeText(getContext(), "" + IncDaily, Toast.LENGTH_SHORT).show();

           firestore.collection("wallets").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                   if(task.isSuccessful()){
                       DocumentSnapshot documentSnapshot=task.getResult();

                       if(documentSnapshot.exists()){
                           Toast.makeText(getContext(), "you have already invested", Toast.LENGTH_SHORT).show();


                       }else{

                           SharedPreferences shrd= getActivity().getSharedPreferences("requestspayments",Context.MODE_PRIVATE);
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

                firestore.collection("refferals").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();
                            if(documentSnapshot.exists()){
                              //  Toast.makeText(getContext(), "you have already reffered", Toast.LENGTH_SHORT).show();

                            }else{

                                addReferal(uid, investmentAmount, date, IncDaily, totalProfitAmount, Refferals);
                            }

                        }
                    }
                });

                //Toast.makeText(getContext(), "" + currentDate, Toast.LENGTH_SHORT).show();
            }
        });


        //calling function at a specific time
       /* DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
        try {
            date1= new Date();
           date1= dateFormatter.parse("13:30:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
Timer timer=new Timer();
        timer.schedule(new ClassAFragment(),
                date1);*/

        //
       /* Timer t=new Timer();
        try {
            t.schedule(new TimerTask() {
                public void run() {
 addTotalProfit();
                }
            }, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-02-09 13:50:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
        return view;
    }

    private void addInvest(String uId, String investmentAmount, String classCategory, String itemCategory, String Date, Float DailyInvestment, double TotalProfit) {

        String wallet_ID = firestore.collection("Wallet").document().getId();

        Map<String, Object> WalletMap = new HashMap<>();
        WalletMap.put("id", uId);
        WalletMap.put("investementAmount", investmentAmount);
        WalletMap.put("classCategory", classCategory);
        WalletMap.put("itemCategory", itemCategory);
        WalletMap.put("date", Date);
        WalletMap.put("dailyAmount", DailyInvestment);
        WalletMap.put("totalProfit", TotalProfit);


        firestore.collection("wallets")
                .document(uId)
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
                .whereEqualTo("class", "Class A").
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


    private void addReferal(String RuId, String RinvestmentAmount, String RDate, Float RDailyInvestment, double RTotalProfit, int Referals) {

        String wallet_ID = firestore.collection("Wallet").document().getId();

        Map<String, Object> ReferalMap = new HashMap<>();
        ReferalMap.put("id", RuId);
        ReferalMap.put("investementAmount", RinvestmentAmount);
        ReferalMap.put("date", RDate);
        ReferalMap.put("dailyAmount", RDailyInvestment);
        ReferalMap.put("totalProfit", RTotalProfit);
        ReferalMap.put("References", Referals);


        firestore.collection("refferals")
                .document(RuId)
                .set(ReferalMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //progressDialog.dismiss();
                            //Toast.makeText(getContext(), "Referals are Added", Toast.LENGTH_SHORT).show();
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

    //get Total Profit
    private void getTotalProfit() {

        uid = firebaseAuth.getUid();
        firestore.collection("wallets")
                .whereEqualTo("id", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {


                        getTotalProfitAmount = document.getDouble("totalProfit").doubleValue();
                        // Log.d("Amount", "onComplete: "+getReferencesAmount);
                        Toast.makeText(getContext(), "" + getTotalProfitAmount, Toast.LENGTH_LONG).show();

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

    private void addTotalProfit() {


        String currentUid = firebaseAuth.getUid();
        uid = firebaseAuth.getUid();


        //totalProfitAmount+=getTotalProfitAmount;
        getTotalProfitAmount += updateTotalProfitAmount;


        DocumentReference documentReference = firestore.collection("wallets").document(uid);
        HashMap updateAmountt = new HashMap<>();
        updateAmountt.put("totalProfit", getTotalProfitAmount);
        documentReference.update(updateAmountt).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                Toast.makeText(getContext(), "dataupdated", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "NO!", Toast.LENGTH_LONG).show();
            }
        });

    }

}