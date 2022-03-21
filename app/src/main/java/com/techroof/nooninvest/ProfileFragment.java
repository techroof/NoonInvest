package com.techroof.nooninvest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvPhoneNumber, tvClasscategory, tvdailyAmount, tvdate,
            tvinvestmentAmount, tvitemCategory, tvtotalProfit,tvtotalrefferal,
            getTextView1,getTextView2,getTextView3,getTextView4,getTextView5,
            getTextView6,getTextView7,getTextView8,getTextView9,getTextView10,
    noInvestmentText,noReferralText;
    FirebaseAuth firebaseAuth;
    String Name, Email, PhoneNumber, ClassCategory, DailyAmount, Date, InvestmentAmount, ItemCategory, TotalProfit, uId;
    FirebaseFirestore firestore;
    private ConstraintLayout investmentCl,referralsCl;
    private ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Textview Declarations
        tvName = view.findViewById(R.id.UserName);
        tvEmail = view.findViewById(R.id.user_email);
        tvPhoneNumber = view.findViewById(R.id.user_phoneno);
        tvClasscategory = view.findViewById(R.id.user_class_category);
        tvdailyAmount = view.findViewById(R.id.user_daily_amount);
        tvdate = view.findViewById(R.id.user_date);
        tvinvestmentAmount = view.findViewById(R.id.user_investment_amount);
        tvitemCategory = view.findViewById(R.id.user_item_category);
        tvtotalProfit = view.findViewById(R.id.user_net_profit);
        tvtotalrefferal=view.findViewById(R.id.user_refferal_net_profit);

        noInvestmentText=view.findViewById(R.id.no_packages_text);
        noReferralText=view.findViewById(R.id.no_referals_text);

        investmentCl=view.findViewById(R.id.investment_cl);
        referralsCl=view.findViewById(R.id.referals_cl);
        //
        getTextView1=view.findViewById(R.id.textview1);
        getTextView2=view.findViewById(R.id.textview2);
        getTextView3=view.findViewById(R.id.textview3);
        getTextView4=view.findViewById(R.id.textview4);
        getTextView5=view.findViewById(R.id.textview5);
        getTextView6=view.findViewById(R.id.textview6);
        getTextView7=view.findViewById(R.id.textview7);
        getTextView8=view.findViewById(R.id.textview8);
        getTextView9=view.findViewById(R.id.textview9);
        getTextView10=view.findViewById(R.id.textview10);

        //FireBase Declarations
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        //Declaring Strings
        Name = tvName.getText().toString();
        Email = tvEmail.getText().toString();
        PhoneNumber = tvPhoneNumber.getText().toString();
        ClassCategory = tvClasscategory.getText().toString();
        DailyAmount = tvdailyAmount.getText().toString();
        Date = tvdate.getText().toString();
        InvestmentAmount = tvinvestmentAmount.getText().toString();
        ItemCategory = tvitemCategory.getText().toString();
        TotalProfit = tvtotalProfit.getText().toString();
        //calling Functions

        pd=new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();

        getuserData();

        ////////////////------------ Referrals Check-----------/////////////////

        firestore.collection("refferals").document(uId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    DocumentSnapshot documentSnapshot= task.getResult();
                    if(documentSnapshot.exists()){

                        getReferralData();
                        noReferralText.setVisibility(View.INVISIBLE);
                        //  Intent intent=new Intent(getContext(), HomeActivity.class);
                        // startActivity(intent);

                    }else{

                        referralsCl.setVisibility(View.INVISIBLE);
                        noReferralText.setVisibility(View.VISIBLE);

                        pd.dismiss();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }
        });

        ///////////-------- Wallet Check-------/////////

        firestore.collection("wallets").document(uId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    DocumentSnapshot documentSnapshot= task.getResult();
                    if(documentSnapshot.exists()){

                        getUserWalletData();

                        noInvestmentText.setVisibility(View.INVISIBLE);
                      //  Intent intent=new Intent(getContext(), HomeActivity.class);
                       // startActivity(intent);

                    }else{

                        investmentCl.setVisibility(View.INVISIBLE);
                        noInvestmentText.setVisibility(View.VISIBLE);

                        pd.dismiss();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }
        });
        return view;

    }

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

    private void getReferralData() {

        uId = FirebaseAuth.getInstance().getUid();
        firestore.collection("refferals")
                .whereEqualTo("id", uId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        float UTotalProfit = document.getLong("totalProfit");
                        uId = firebaseAuth.getUid();
                        String sTotalProfit = String.valueOf(UTotalProfit);
                        //Toast.makeText(getContext(), "" + sDailyAmount, Toast.LENGTH_LONG).show();
                        tvtotalProfit.setText(sTotalProfit);

                        pd.dismiss();

                    }
                } else {
                    Log.d("d", "Error getting documents: ", task.getException());
                    pd.dismiss();

                }
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.dismiss();
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

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

                        pd.dismiss();

                    }
                } else {
                    Log.d("d", "Error getting documents: ", task.getException());
                    pd.dismiss();

                }
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                pd.dismiss();
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }


}