package com.techroof.nooninvest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.AboutusActivity;
import com.techroof.Notifications.Notifications;
import com.techroof.Services.Services;
import com.techroof.nooninvest.Adapters.HomeFragmentViewPagerAdapter;
import com.techroof.nooninvest.Authentication.LoginActivity;
import com.techroof.nooninvest.PayPal.PaypalPayout;
import com.techroof.nooninvest.WithDrawals.WithdrawalAmounts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    String uid,accountNumber,jazzCashAccountNumber,jazzCashAccountName,bankName,accountName,ibanNumber,jazzCashaccountname,jazzCashaccountnumber,accountType;
    EditText bankAccNameEt,bankAccNumEt,bankNameEt,bankIbanEt,jazzcashAccNameEt,jazzcashAccNumEt,bankAccType;
    TextView tvTellingAccountDetails;
    private ImageView imgViewNotification;
    private final int max_number = 99;
    private int notification_number_counter=0;
    private TextView tvNotificationCounter;
    private String date, currentDate;
    //dialog
    private Dialog dialog,dialogAlert;
    private String[] accountTypeList;

    ConstraintLayout bankAccountCl,jazzCashAccountCl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        accountTypeList = getResources().getStringArray(R.array.account_type);

        tableLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        tableLayout.setupWithViewPager(viewPager);
        imgToolbar=findViewById(R.id.img_navigation_drawer);
        imgViewNotification=findViewById(R.id.img_notifications);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser==null){

            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);


        }else{

            NotificationChannel();
            onTimeSet(23,59);

            currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            uid=firebaseAuth.getCurrentUser().getUid();

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
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false); //Optional
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

                Button Okay = dialog.findViewById(R.id.btn_okay);
                Button Cancel = dialog.findViewById(R.id.btn_cancel);
                bankAccountCl=dialog.findViewById(R.id.bank_account_cl);
                jazzCashAccountCl=dialog.findViewById(R.id.jazzcash_easypaisa_cl);
                bankAccNameEt=dialog.findViewById(R.id.bank_acc_name_et);
                bankAccNumEt =dialog.findViewById(R.id.bank_acc_num_et);
                bankNameEt=dialog.findViewById(R.id.bank_name_et);
                bankIbanEt=dialog.findViewById(R.id.bank_iban_et);
                jazzcashAccNameEt=dialog.findViewById(R.id.jazzcash_easypaisa_acc_name_et);
                jazzcashAccNumEt=dialog.findViewById(R.id.jazzcash_easypaisa_acc_num_et);
                bankAccType=dialog.findViewById(R.id.et_account_type);
                tvTellingAccountDetails=dialog.findViewById(R.id.textViewbillinginfo);
                tvNotificationCounter=findViewById(R.id.tv_notification_counter);

                CheckNotification();

                //dialogbox alert

                dialogAlert= new Dialog(this);
                dialogAlert.setContentView(R.layout.changing_account_custom_dialog);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialogAlert.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
                }
                dialogAlert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogAlert.setCancelable(true); //Optional
                dialogAlert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

                Button alertOkay = dialogAlert.findViewById(R.id.activation_btn_okay);
                Button alertCancel = dialogAlert.findViewById(R.id.activation_btn_cancel);



                alertOkay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogAlert.dismiss();
                        //dialog.dismiss();
                    }
                });

                alertCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                        dialogAlert.dismiss();
                    }
                });

                imgViewNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent moveNotifications=new Intent(getApplicationContext(), Notifications.class);
                        startActivity(moveNotifications);
                    }
                });


                //---------------------\\
                bankAccType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(HomeActivity.this).setTitle("Select Your Account Type")
                                .setSingleChoiceItems(accountTypeList, 0, null)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        dialog.dismiss();

                                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                        bankAccType.setText(accountTypeList[selectedPosition]);
                                        accountType = accountTypeList[selectedPosition];

                                        if(accountType.equals("Bank Account")){


                                            uid=firebaseAuth.getCurrentUser().getUid();
                                            jazzCashAccountCl.setVisibility(View.GONE);
                                            bankAccountCl.setVisibility(View.VISIBLE);

                                            getBankAccount();

                                        }else if(accountType.equals("EasyPaisa/JazzCash")){
                                            uid=firebaseAuth.getCurrentUser().getUid();
                                            bankAccountCl.setVisibility(View.GONE);
                                            jazzCashAccountCl.setVisibility(View.VISIBLE);

                                            getEasyPaisa();

                                        }
                                        //clanEt.requestFocus();
                                    }
                                })
                                .show();
                    }
                });


                Okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(accountType.equals("Bank Account")){

                            uid = firebaseAuth.getUid();
                            accountNumber=bankAccNumEt.getText().toString();
                            bankName=bankAccNameEt.getText().toString();
                            accountName=bankAccNameEt.getText().toString();
                            ibanNumber=bankIbanEt.getText().toString();
                            //Toast.makeText(getApplicationContext(), ""+accountType, Toast.LENGTH_SHORT).show();

                            addAcountNumber(uid,accountType,accountNumber,bankName,accountName,ibanNumber);




                        }else if(accountType.equals("EasyPaisa/JazzCash")){

                            uid = firebaseAuth.getUid();
                            jazzCashaccountnumber=jazzcashAccNumEt.getText().toString();
                            jazzCashaccountname=jazzcashAccNameEt.getText().toString();

                            //Toast.makeText(getApplicationContext(), ""+accountType, Toast.LENGTH_SHORT).show();

                            addEasyPaisa(uid,jazzCashaccountnumber,jazzCashaccountname,accountType);



                        }



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
                CheckingAccountDetails();
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
    private void addAcountNumber(String uId, String accountType, String accountNumber,String bankName,String accountName,String IBANNUMBER) {

        Map<String, Object> AccountMap = new HashMap<>();
        AccountMap.put("id", uId);
        AccountMap.put("AccountNumber", accountNumber);
        AccountMap.put("BankName",bankName);
        AccountMap.put("AccountName",accountName);
        AccountMap.put("accountType",accountType);
        AccountMap.put("IBANNUMBER",IBANNUMBER);


        firebaseFirestore.collection("AccountNo")
                .document(uId)
                .set(AccountMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Account details are added", Toast.LENGTH_LONG).show();
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


    private void addEasyPaisa(String uId,String accountNumber,String accountName,String accountType) {

        Map<String, Object> AccountMap = new HashMap<>();
        AccountMap.put("id", uId);
        AccountMap.put("JazzCashAccountNumber", accountNumber);
        AccountMap.put("JazzCashAccountName",accountName);
        AccountMap.put("accountType",accountType);


        firebaseFirestore.collection("AccountNo")
                .document(uId)
                .set(AccountMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            //progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Account details are added", Toast.LENGTH_LONG).show();
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

    private void getBankAccount(){


        firebaseFirestore.collection("AccountNo").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){

                    uid=task.getResult().getString("id");
                    accountNumber=task.getResult().getString("AccountNumber");
                    bankName=task.getResult().getString("BankName");
                    accountName=task.getResult().getString("AccountName");
                    //accountType=task.getResult().getString("BankAccountType");
                    //accountType=task.getResult().getString("accountType");
                    ibanNumber=task.getResult().getString("IBANNUMBER");


                    if(accountType==null){

                        accountType="Bank Account";
                    }
                    bankAccNumEt.setText(accountNumber);
                    bankNameEt.setText(bankName);
                    bankAccNameEt.setText(accountName);
                    bankIbanEt.setText(ibanNumber);

                }else{

                    Toast.makeText(getApplicationContext(), "Your bank account is not added", Toast.LENGTH_SHORT).show();

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void getEasyPaisa(){

        firebaseFirestore.collection("AccountNo").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){

                    uid=task.getResult().getString("id");
                    jazzCashaccountnumber=task.getResult().getString("JazzCashAccountNumber");
                    jazzCashaccountname=task.getResult().getString("JazzCashAccountName");
                    //accountType=task.getResult().getString("jazzCashAccountType");
                   // accountType=task.getResult().getString("accountType");


                    jazzcashAccNumEt.setText(jazzCashaccountnumber);
                    jazzcashAccNameEt.setText(jazzCashaccountname);
                    /*if(accountType==null){

                        accountType="EasyPaisa/JazzCash";
                        //Toast.makeText(getApplicationContext(), ""+accountType, Toast.LENGTH_SHORT).show();

                    }*/

                }else{


                    accountType="EasyPaisa/JazzCash";
                       //Toast.makeText(getApplicationContext(), ""+accountType, Toast.LENGTH_SHORT).show();

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void CheckingAccountDetails(){


        firebaseFirestore.collection("AccountNo").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){

                    uid=task.getResult().getString("id");
                    accountNumber=task.getResult().getString("AccountNumber");
                    bankName=task.getResult().getString("BankName");
                    accountName=task.getResult().getString("AccountName");
                    accountType=task.getResult().getString("accountType");
                    ibanNumber=task.getResult().getString("IBANNUMBER");

                    tvTellingAccountDetails.setText("You already have a " +accountType+ " in your account details");

                    dialogAlert.show();

                }else{

                    Toast.makeText(getApplicationContext(), "Your bank account is not added", Toast.LENGTH_SHORT).show();

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void CheckNotification() {


        date=currentDate;
        uid = firebaseAuth.getCurrentUser().getUid();
        //Toast.makeText(getContext(), ""+uId, Toast.LENGTH_SHORT).show();

        firebaseFirestore.collection("Notifications").whereEqualTo("Uid", uid).whereEqualTo("date",date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {


                    for (QueryDocumentSnapshot document : task.getResult()) {


                        notification_number_counter++;


                        if (max_number > notification_number_counter) {

                            tvNotificationCounter.setVisibility(View.VISIBLE);
                            tvNotificationCounter.setText(String.valueOf(notification_number_counter));
                            //documentNumber = document.getId();
                            //Toast.makeText(getContext(), ""+document.getId(), Toast.LENGTH_SHORT).show();
                        }

                    }


                }


            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //event

    public void onTimeSet(int hourofDay, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourofDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        startAlarm(c);


    }
    //calender
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Services.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),1000*60*60*24,pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,1000*60*60*24,pendingIntent);
        }
    }

    private void NotificationChannel(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O ){
            CharSequence name="Channel";
            String description="Channel Source";
            int importance= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel("notifyLemubit",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);



        }


    }
}
