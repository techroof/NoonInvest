package com.techroof.nooninvest.StripePayments;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.techroof.nooninvest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StripeCheckout extends AppCompatActivity {

    private Button btnPay;
    private final String SECRET_KEY = "sk_test_51KevubKB7rgMXqBsvvTIimDbsVsjXSoniS5DwwzLcjliExJVfJQsTNXkr4xmgh0fMYoegP2xS35ZTtKeKAn87XMk00kreEXTqN";
    private final String PUBLISH_KEY = "pk_test_51KevubKB7rgMXqBsRFXEzNdmxW56qsW1HcAQZAuSn4pS6dv6KdYkTgLZsWF7ZzNumUwpw11C3NMdpPLfeqzZF4jA003rkrHvXb";
    private PaymentSheet paymentSheet;
    private String customer_ID;
    private String Ephericalkey;
    private String ClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_checkout);
        btnPay = findViewById(R.id.pay_button);


        PaymentConfiguration.init(this, PUBLISH_KEY);

        paymentSheet = new PaymentSheet(this, PaymentSheetResult -> {
            onPaymentResult(PaymentSheetResult);

        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentFlow();
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    customer_ID = object.getString("id");
                    if(customer_ID==null){



                    }
                    getEphericalkey(customer_ID);

                    Toast.makeText(getApplicationContext(), "yes"+customer_ID, Toast.LENGTH_SHORT).show();

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


        RequestQueue requestQueue = Volley.newRequestQueue(StripeCheckout.this);
        requestQueue.add(stringRequest);

    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {

            Toast.makeText(getApplicationContext(), "payment successfull", Toast.LENGTH_SHORT).show();

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
                    if(Ephericalkey==null){


                    }
                    Toast.makeText(getApplicationContext(), "s" + Ephericalkey, Toast.LENGTH_SHORT).show();

                    getClientSecret(customer_ID, Ephericalkey);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "epeherical"+e.toString(), Toast.LENGTH_SHORT).show();
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


        RequestQueue requestQueue = Volley.newRequestQueue(StripeCheckout.this);
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


                    }
                    Toast.makeText(getApplicationContext(), "sd" + ClientSecret, Toast.LENGTH_SHORT).show();
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
                params.put("amount", "1000" + "00");
                params.put("currency", "usd");
                params.put("automatic_payment_methods[enabled]", "true");
                //return super.getParams();
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(StripeCheckout.this);
        requestQueue.add(stringRequest);

    }

    private void PaymentFlow() {
        paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration("nooninvest", new PaymentSheet.CustomerConfiguration(customer_ID, Ephericalkey)));
    }
}