package com.techroof.nooninvest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class CreditCardPayments extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;
    String API_GET_TOKEN = "https://techroof.net/htdocs/braintree-php-6.7.0/main.php";
    String API_CHECKOUT = "https://techroof.net/htdocs/braintree-php-6.7.0/checkout.php";
    String token, amount;
    HashMap<String, String> paramsHash;
    FirebaseFirestore firestore;
    EditText edt_amount;
    Button btn_pay;
    LinearLayout group_waiting, group_payment;
    String catVal,uid,investmentAmount,classCategory,itemCategory,date;
    Float IncDaily,totalProfitAmount;
    Float transactionAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_payments);
        btn_pay = findViewById(R.id.pay_with_creditcard);
        group_payment = findViewById(R.id.payment_group);
        group_waiting = findViewById(R.id.waiting_group);
       // edt_amount = findViewById(R.id.edt_amount);
        firestore=FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences= getSharedPreferences("requestspayments", Context.MODE_PRIVATE);
        catVal=sharedPreferences.getString("str","Note");
        uid=sharedPreferences.getString("uid","Note1");
        investmentAmount=sharedPreferences.getString("investmentAmount","Note2");
        classCategory=sharedPreferences.getString("classCategory","Note3");
        itemCategory=sharedPreferences.getString("itemCategory","Note4");
        date=sharedPreferences.getString("date","Note5");
        IncDaily=sharedPreferences.getFloat("IncDaily",12.12345f);
        totalProfitAmount=sharedPreferences.getFloat("totalProfitAmount",12.12345f);
        //transactionAmount= Float.parseFloat(investmentAmount);
        //Toast.makeText(getApplicationContext(), ""+investmentAmount, Toast.LENGTH_SHORT).show();
        new getToken().execute();

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
            }
        });


    }

    public void submitPayment(){

        DropInRequest dropInRequest=new DropInRequest().clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();
                //if (!edt_amount.getText().toString().isEmpty()) {
                    //amount = edt_amount.getText().toString();
                    paramsHash = new HashMap<>();
                    paramsHash.put("amount", investmentAmount);
                    paramsHash.put("nonce", strNonce);
                    sendPayment();

                //} else {

                    //Toast.makeText(getApplicationContext(), "Please put valid amount", Toast.LENGTH_SHORT).show();
               // }
            }
        } else if (resultCode == RESULT_CANCELED) {

            Toast.makeText(getApplicationContext(), "user cancelled", Toast.LENGTH_SHORT).show();
        } else {

            Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            Log.d("EDMT ERROR", error.toString());
        }
    }

    private void sendPayment() {
        RequestQueue requestQueue=Volley.newRequestQueue(CreditCardPayments.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, API_CHECKOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
if(response.contains("Successful")) {
    addInvest(uid, investmentAmount, classCategory, itemCategory, date, IncDaily, totalProfitAmount);
    Toast.makeText(getApplicationContext(), "Transaction Successful", Toast.LENGTH_SHORT).show();
}else{
    Toast.makeText(getApplicationContext(), "Transaction Failed", Toast.LENGTH_SHORT).show();

}
                Log.d("EDMT_LOG", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("EDMT_LOG", error.toString());
            }
        }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(paramsHash==null)
                    return null;
                Map<String,String>params=new HashMap<>();
                for(String key:paramsHash.keySet()){

                    params.put(key,paramsHash.get(key));


                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private class getToken extends AsyncTask {

        ProgressDialog mdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mdialog=new ProgressDialog(CreditCardPayments.this, android.R.style.Theme_DeviceDefault_Dialog);
            mdialog.setCancelable(false);
            mdialog.setMessage("Please Wait...");
            mdialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mdialog.dismiss();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

HttpClient client=new HttpClient();
client.get(API_GET_TOKEN, new HttpResponseCallback() {


    @Override
    public void success(String responseBody) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                group_waiting.setVisibility(View.GONE);
                group_payment.setVisibility(View.VISIBLE);


                token=responseBody;
                //Toast.makeText(getApplicationContext(), "yes"+token, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void failure(Exception exception) {


        Toast.makeText(getApplicationContext(), "error"+exception, Toast.LENGTH_SHORT).show();
        Log.d("ds", "failure: "+exception);
    }
});
            return null;
        }
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
}

