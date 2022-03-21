package com.techroof.nooninvest;

import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.pyplcheckout.pojo.Total;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReferalFragment extends Fragment {

    private TextView tvReferalname, noReferralText,userEmail;
    private Button btnAcceptrefferal;
    private String referrerUid;
    private String refferDate;
    private String currentDate;
    private String Date;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    String currentuserId;
    int getReferencesAmount;
    int addReferencesAmount = 5;
    int TotalAmount;
    int investmentAmount = 5;
    double dailyAmount = 0.25;
    double TotalProfit = 0.00;
    private ConstraintLayout referralCl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_referal, container, false);

        tvReferalname = view.findViewById(R.id.tv_Referal_UserName);
        userEmail = view.findViewById(R.id.tv_Referal_email);
        btnAcceptrefferal = view.findViewById(R.id.btn_accept_referal);
        referralCl = view.findViewById(R.id.referral_cl);
        noReferralText = view.findViewById(R.id.no_referals_text);

        //firebase declaration
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentuserId = firebaseAuth.getUid();
        //Toast.makeText(getContext(), "idddd"+referrerUid, Toast.LENGTH_SHORT).show();
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Date = currentDate;

        btnAcceptrefferal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* String cutString=referrerUid.substring(2);
                if(idd==referrerUid){

                    Toast.makeText(getContext(), "same", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("d", ""+referrerUid);
                    Toast.makeText(getContext(), "notsame"+cutString, Toast.LENGTH_SHORT).show();

                }*/
                //ReferalAmount();
                if (referrerUid == null) {

                    Toast.makeText(getContext(), "No one has Referred You", Toast.LENGTH_SHORT).show();

                } else if (referrerUid.equals(currentuserId)) {

                    Toast.makeText(getContext(), "you cannot send link to yourself", Toast.LENGTH_SHORT).show();
                    //ReferalAmount();
                } else {
                    CreateRefferal(referrerUid, investmentAmount, dailyAmount, TotalProfit, Date, refferDate);


                }

            }
        });
        //getlink Info
        getLinkInfo();

        //get referalName

        //getReferalAmount

        return view;
    }

    private void getAmount() {

        firestore.collection("refferal")
                .whereEqualTo("id", referrerUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {


                        getReferencesAmount = document.getLong("References").intValue();
                        // Log.d("Amount", "onComplete: "+getReferencesAmount);
                        Toast.makeText(getContext(), "" + getReferencesAmount, Toast.LENGTH_SHORT).show();

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


    private void ReferalAmount() {

        String currentUid = firebaseAuth.getUid();

        getReferencesAmount += addReferencesAmount;

        DocumentReference documentReference = firestore.collection("refferals").document(referrerUid);
        HashMap updateAmount = new HashMap<>();
        updateAmount.put("References", getReferencesAmount);
        documentReference.update(updateAmount).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                Toast.makeText(getContext(), "DATA UPDATED", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "NO!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getLinkInfo() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getActivity().getIntent())
                .addOnSuccessListener(getActivity(), new
                        OnSuccessListener<PendingDynamicLinkData>() {
                            @Override
                            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                                // Get deep link from result (may be null if no link is found)
                                Uri deepLink = null;
                                // deepLink= Uri.parse(pendingDynamicLinkData.getLink().toString());
                                //Toast.makeText(getApplicationContext(), "deeplink"+deepLink, Toast.LENGTH_SHORT).show();

                                if (pendingDynamicLinkData != null) {
                                    deepLink = pendingDynamicLinkData.getLink();

                                    Log.d("deepLink", "no" + deepLink);
                                    referrerUid = deepLink.getQueryParameter("invitedby");
                                    refferDate = deepLink.getQueryParameter("date");
                                    /*String cn=String.valueOf(deepLink.getQueryParameters("utm_campaign"));
                                    String cm = String.valueOf(deepLink. getQueryParameters("utm_medium"));
                                    String cs = String.valueOf(deepLink.getQueryParameters("utm_source"));*/

                                }

                                if (referrerUid == null) {
                                    referralCl.setVisibility(View.GONE);
                                    noReferralText.setVisibility(View.VISIBLE);

                                } else {
                                    referralCl.setVisibility(View.VISIBLE);
                                    noReferralText.setVisibility(View.GONE);
                                }

                                getName();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void getName() {

        firestore.collection("users")
                .whereEqualTo("id", referrerUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String UName = document.getString("name");
                        String email = document.getString("email");
                        //uId = firebaseAuth.getUid();
                        /// Toast.makeText(getContext(), " name" + UName, Toast.LENGTH_SHORT).show();
                        tvReferalname.setText(UName);
                        userEmail.setText(email);


                    }

                    getAmount();

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

    private void CreateRefferal(String RuId, int RinvestmentAmount, double RDailyInvestment, double RTotalProfit, String CurrentDate, String SendingDate) {

        String wallet_ID = firestore.collection("Wallet").document().getId();

        Map<String, Object> ReferalMap = new HashMap<>();
        ReferalMap.put("id", RuId);
        ReferalMap.put("investementAmount", RinvestmentAmount);
        ReferalMap.put("dailyAmount", RDailyInvestment);
        ReferalMap.put("totalProfit", RTotalProfit);
        ReferalMap.put("ActivationDate", CurrentDate);
        ReferalMap.put("SendingDate", SendingDate);

        firestore.collection("refferals")
                .document(RuId)
                .set(ReferalMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //progressDialog.dismiss();
                            Toast.makeText(getContext(), "Referral is Added", Toast.LENGTH_SHORT).show();
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

}