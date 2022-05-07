package com.techroof.nooninvest.PayPal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;
import com.paypal.checkout.paymentbutton.PayPalCreditButton;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.techroof.nooninvest.CreditCardPayments;
import com.techroof.nooninvest.InvestmentDetailsActivity;
import com.techroof.nooninvest.R;
import com.techroof.nooninvest.StripePayments.CreditDebitCardPayments;
import com.techroof.nooninvest.StripePayments.StripeCheckout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PaypalIntegration extends AppCompatActivity {
    PayPalButton payPalButton;
    String YOUR_CLIENT_ID = "AXdunrYOmWgPmyiAh6uYO3nI7dVkguiTNHWky4YsdkmrLZOB1AKlGpVwV6tmKyL1WDvNuBmQkeO4QrU8";
    String catVal;
    TextView tvMovetocreditcard;
    private FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String uid,investmentAmount,classCategory,itemCategory,date;
    Float IncDaily,totalProfitAmount,transactionAmount;
    private ImageView backBtn;

    //paypal initialization block
    private Button btnPay;
    private final String SECRET_KEY = "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";
    private final String PUBLISH_KEY = "pk_live_51KevubKB7rgMXqBscyY4LnZwWvIGnhNzLWn9hPEzNH4hzJRXb03QxvP2DLZA9V2Kb0kW1XXrMJsaleObeQqxoUMZ0058y8C6hL";
    private PaymentSheet paymentSheet;
    private String customer_ID;
    private String Ephericalkey;
    private String ClientSecret;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_integration);

        payPalButton = findViewById(R.id.payPalButton);
        tvMovetocreditcard=findViewById(R.id.tv_move_credit_card_payments);
        backBtn=findViewById(R.id.img_back_arrow);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        pd=new ProgressDialog(PaypalIntegration.this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        pd.show();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        tvMovetocreditcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent intent=new Intent(getApplicationContext(), CreditCardPayments.class);
                startActivity(intent);*/

                PaymentFlow();
            }
        });

        SharedPreferences sharedPreferences= getSharedPreferences("requestspayments", Context.MODE_PRIVATE);
        catVal=sharedPreferences.getString("str","Note");
        uid=sharedPreferences.getString("uid","Note1");
        investmentAmount=sharedPreferences.getString("investmentAmount","Note2");
        classCategory=sharedPreferences.getString("classCategory","Note3");
        itemCategory=sharedPreferences.getString("itemCategory","Note4");
        date=sharedPreferences.getString("date","Note5");
        IncDaily=sharedPreferences.getFloat("IncDaily",12.12345f);
        totalProfitAmount=sharedPreferences.getFloat("totalProfitAmount",12.12345f);


        //credit card stripe tools


        PaymentConfiguration.init(this, PUBLISH_KEY);

        paymentSheet = new PaymentSheet(this, PaymentSheetResult -> {
            onPaymentResult(PaymentSheetResult);

        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);

                    customer_ID = object.getString("id");
                    getEphericalkey(customer_ID);
                    //Toast.makeText(getApplicationContext(), "yes"+customer_ID, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(PaypalIntegration.this);
        requestQueue.add(stringRequest);








        /////////////////////**********************************/////////////////////////////////////////////


        //transactionAmount= Float.valueOf(investmentAmount);
        //Toast.makeText(getApplicationContext(), ""+investmentAmount, Toast.LENGTH_SHORT).show();
        payPalButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(investmentAmount)
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                //Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));

                                addInvest(uid, investmentAmount, classCategory, itemCategory, date, IncDaily, totalProfitAmount);
                                Toast.makeText(getApplicationContext(), "Investment has been added", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );

        CheckoutConfig config = new CheckoutConfig(
                getApplication(),
                YOUR_CLIENT_ID,
                Environment.LIVE,
                String.format("%s://paypalpay","com.techroof.nooninvest"),
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                new SettingsConfig(
                        true, false
                )
        );
        PayPalCheckout.setConfig(config);


    }






        //paypalbutton
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
                    .update(WalletMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Investment Added", Toast.LENGTH_SHORT).show();
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

        //stripe payment methods

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {

            Toast.makeText(getApplicationContext(), "payment successfull", Toast.LENGTH_SHORT).show();
            addInvest(uid, investmentAmount, classCategory, itemCategory, date, IncDaily, totalProfitAmount);
            Intent moveToInvestmentactivity=new Intent(getApplicationContext(), InvestmentDetailsActivity.class);
            Toast.makeText(getApplicationContext(), "Investment has been added", Toast.LENGTH_LONG).show();
            startActivity(moveToInvestmentactivity);




        }
    }

    private void getEphericalkey(String customer_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST
                , "https://api.stripe.com/v1/ephemeral_keys", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    Ephericalkey = object.getString("id");
                    //Toast.makeText(getApplicationContext(), "s" + Ephericalkey, Toast.LENGTH_SHORT).show();

                    getClientSecret(customer_ID, Ephericalkey);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "epherical"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), ""+error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                header.put("Stripe-Version","2020-08-27");
                //header.put("Content-Type", "application/json");
                //params.put("customer", customer_id);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customer_ID);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(PaypalIntegration.this);
        requestQueue.add(stringRequest);

    }

    private void getClientSecret(String customer_id, String ephericalkey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);

                    ClientSecret = object.getString("client_secret");
                    if(ClientSecret==null){

                        pd.show();
                    }else if(ClientSecret!=null){

                        pd.dismiss();
                    }
                    //Toast.makeText(getApplicationContext(), "sd" + ClientSecret, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                header.put("Stripe-Version", "2020-08-27");

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customer_id);
                params.put("amount", investmentAmount + "00");
                params.put("currency", "usd");
                params.put("automatic_payment_methods[enabled]", "true");
                //return super.getParams();
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(PaypalIntegration.this);
        requestQueue.add(stringRequest);

    }

    private void PaymentFlow() {

        paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration("nooninvest", new PaymentSheet.CustomerConfiguration(customer_ID, Ephericalkey)));

    }
    }





