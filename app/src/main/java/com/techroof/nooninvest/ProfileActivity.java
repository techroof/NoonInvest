package com.techroof.nooninvest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class ProfileActivity extends AppCompatActivity {

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
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.UserName);
        tvEmail = findViewById(R.id.user_email);
        tvPhoneNumber = findViewById(R.id.user_phoneno);
        tvClasscategory = findViewById(R.id.user_class_category);
        tvdailyAmount = findViewById(R.id.user_daily_amount);
        tvdate = findViewById(R.id.user_date);
        tvinvestmentAmount = findViewById(R.id.user_investment_amount);
        tvitemCategory = findViewById(R.id.user_item_category);
        tvtotalProfit = findViewById(R.id.user_net_profit);
        tvtotalrefferal=findViewById(R.id.user_refferal_net_profit);

        backBtn=findViewById(R.id.img_back_arrow);

        noInvestmentText=findViewById(R.id.no_packages_text);
        noReferralText=findViewById(R.id.no_referals_text);

        investmentCl=findViewById(R.id.investment_cl);
        referralsCl=findViewById(R.id.referals_cl);
        //
        getTextView1=findViewById(R.id.textview1);
        getTextView2=findViewById(R.id.textview2);
        getTextView3=findViewById(R.id.textview3);
        getTextView4=findViewById(R.id.textview4);
        getTextView5=findViewById(R.id.textview5);
        getTextView6=findViewById(R.id.textview6);
        getTextView7=findViewById(R.id.textview7);
        getTextView8=findViewById(R.id.textview8);
        getTextView9=findViewById(R.id.textview9);
        getTextView10=findViewById(R.id.textview10);

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();
            }
        });

        pd=new ProgressDialog(this);
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

                Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

                Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }
        });

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
                        tvtotalrefferal.setText(""+sTotalProfit);

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
                Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }
}