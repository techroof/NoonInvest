package com.techroof.nooninvest.StripePayments;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.annotations.NotNull;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
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
import com.techroof.nooninvest.R;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CreditDebitCardPayments extends AppCompatActivity {
    String YOUR_CLIENT_ID = "AXdunrYOmWgPmyiAh6uYO3nI7dVkguiTNHWky4YsdkmrLZOB1AKlGpVwV6tmKyL1WDvNuBmQkeO4QrU8",
            catVal;
    Button payPalCreditButton;
    PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId("AXdunrYOmWgPmyiAh6uYO3nI7dVkguiTNHWky4YsdkmrLZOB1AKlGpVwV6tmKyL1WDvNuBmQkeO4QrU8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_debit_card_payments);

        payPalCreditButton=findViewById(R.id.paypal_credit_card);

        SharedPreferences sharedPreferences= getSharedPreferences("requestspayments", Context.MODE_PRIVATE);

        catVal=sharedPreferences.getString("investmentAmount","string");

        int amount= Integer.parseInt(catVal);

        payPalCreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent serviceConfig = new Intent(getApplicationContext(), PayPalService.class);
                serviceConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                startService(serviceConfig);
                PayPalPayment payment = new PayPalPayment(new BigDecimal(amount),
                        "USD", "My Payment", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent paymentConfig = new Intent(getApplicationContext(), PaymentActivity.class);
                paymentConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                paymentConfig.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                startActivityForResult(paymentConfig, 0);
            }
        });


       /* payPalCreditButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value("10.00")
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
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                            }
                        });
                    }
                }
        );

        CheckoutConfig config = new CheckoutConfig(
                getApplication(),
                YOUR_CLIENT_ID,
                Environment.LIVE,
                String.format("%s://paypalpay", "com.techroof.nooninvest"),
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                new SettingsConfig(
                        true, false
                )
        );
        PayPalCheckout.setConfig(config);

*/

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK){
            PaymentConfirmation confirm = data.getParcelableExtra(
                    PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null){
                try {
                    Log.i("sampleapp", confirm.toJSONObject().toString(4));
// TODO: send 'confirm' to your server for verification
                } catch (JSONException e) {
                    Log.e("sampleapp", "no confirmation data: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("sampleapp", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("sampleapp", "Invalid payment / config set");
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}




