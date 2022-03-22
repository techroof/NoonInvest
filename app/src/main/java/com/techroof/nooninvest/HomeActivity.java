package com.techroof.nooninvest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techroof.AboutusActivity;
import com.techroof.nooninvest.Adapters.HomeFragmentViewPagerAdapter;
import com.techroof.nooninvest.Authentication.LoginActivity;
import com.techroof.nooninvest.PayPal.PaypalPayout;
import com.techroof.nooninvest.WithDrawals.WithdrawalAmounts;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tableLayout;
    private ViewPager viewPager;
    private FirebaseAuth firebaseAuth;
    FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore firebaseFirestore;
    ImageView imgToolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    String uid,accountNumber,bankName;
    //dialog
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tableLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        tableLayout.setupWithViewPager(viewPager);
        imgToolbar=findViewById(R.id.img_navigation_drawer);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser==null){

            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);


        }else{

            if (firebaseAuth.getCurrentUser().isEmailVerified()){

                HomeFragmentViewPagerAdapter viewPagerAdapter = new HomeFragmentViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                //viewPagerAdapter.addfragment(new ProfileFragment(), "PROFILE");
                viewPagerAdapter.addfragment(new HomeFragment(), "HOME");
                viewPagerAdapter.addfragment(new ReferalFragment(), "REFERAL");

                viewPager.setAdapter(viewPagerAdapter);

                mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);

                drawerLayout=findViewById(R.id.drawer);
                navigationView=findViewById(R.id.navigationView);
                navigationView.setNavigationItemSelectedListener(this);
                //dialogbox
                dialog = new Dialog(this);
                dialog.setContentView(R.layout.billing_custom_dialog);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
                }
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false); //Optional
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

                Button Okay = dialog.findViewById(R.id.btn_okay);
                Button Cancel = dialog.findViewById(R.id.btn_cancel);
                EditText AccountNo=dialog.findViewById(R.id.et_accountno);
                EditText BankName=dialog.findViewById(R.id.et_bank_name);


                Okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        uid = firebaseAuth.getUid();
                        accountNumber=AccountNo.getText().toString();
                        bankName=BankName.getText().toString();
                        addAcountNumber(uid, accountNumber,bankName);


                        //dialog.dismiss();
                    }
                });

                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });
////////////////////////////////////////////////

                //getlink();
                //link();
                imgToolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawerLayout.openDrawer(GravityCompat.START);

                    }
                });


            }else{

                firebaseAuth.signOut();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
            }
        }
//onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser==null){

            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
        }

        }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){

            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public void getlink(){

            FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                @Override
                public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                   // Log.d("MainActivity", "We Have a Dynamic Link ");
                    Toast.makeText(getApplicationContext(), "Deep Link"+pendingDynamicLinkData, Toast.LENGTH_SHORT).show();
                    Uri deepLink=null;
                    if(pendingDynamicLinkData!=null){

                        deepLink=pendingDynamicLinkData.getLink();
                        Toast.makeText(getApplicationContext(), "Deep Link is recieved"+deepLink, Toast.LENGTH_SHORT).show();
                    }

                    if(deepLink!=null){

                        //Log.d("MainActivity", "Here is the DeepLink:\n"+deepLink);
                       // Toast.makeText(getApplicationContext(), "Deep Link"+deepLink, Toast.LENGTH_SHORT).show();

                    }else{
                        //Log.d("MainActivity", "deeplink not recieved");
                        //Toast.makeText(getApplicationContext(), "Deep Link is null", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //Log.d("MainActivity", "Not Recieved");
                    Toast.makeText(getApplicationContext(), "Deep Link not recieved", Toast.LENGTH_SHORT).show();
                }
            });






        }
        void link(){

            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new
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
                                        String cn=String.valueOf(deepLink.getQueryParameters("utm_campaign"));
                                        String cm = String.valueOf(deepLink. getQueryParameters("utm_medium"));
                                        String cs = String.valueOf(deepLink.getQueryParameters("utm_source"));
                                        Toast.makeText(getApplicationContext(), "yes"+deepLink, Toast.LENGTH_SHORT).show();

                                        if (cs != null && cn != null) {
                                            Bundle params = new Bundle();
                                            params.putString(FirebaseAnalytics.Param.CAMPAIGN, cn);
                                            params.putString(FirebaseAnalytics.Param.MEDIUM, cm);
                                            params.putString(FirebaseAnalytics.Param.SOURCE, cs);

                                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.CAMPAIGN_DETAILS, params);
                                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, params);
                                        }


                                    }
                                }
                            })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "getDynamicLink:onFailure", e);
                        }
                    });











        }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.profile:
                Intent profile=new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(profile);
                break;

            case R.id.nvg_billing:
                dialog.show();
                break;

            case R.id.nvg_share:
                Intent intent=new Intent(HomeActivity.this,CreateDeepLinkActivity.class);
                startActivity(intent);
                break;

            case R.id.nvg_withdraw:
                Intent moveWithDrawal=new Intent(HomeActivity.this, WithdrawalAmounts.class);
                startActivity(moveWithDrawal);
                break;

            case R.id.aboutus:
                Intent aboutUs=new Intent(HomeActivity.this, AboutusActivity.class);
                startActivity(aboutUs);
                break;

            case R.id.nvg_log_out:
                firebaseAuth.signOut();
                Intent loginIntent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginIntent);
                break;

        }
        return true;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    //adding account number
    private void addAcountNumber(String uId, String accountNumber, String bankName) {

        Map<String, Object> AccountMap = new HashMap<>();
        AccountMap.put("id", uId);
        AccountMap.put("AccountNumber", accountNumber);
        AccountMap.put("BankName",bankName);


        firebaseFirestore.collection("AccountNo")
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

}
