package com.techroof;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techroof.nooninvest.R;

public class AboutusActivity extends AppCompatActivity {

    private TextView faqText;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        faqText=findViewById(R.id.aboutus_faq_text);

        backBtn=findViewById(R.id.img_back_arrow);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        String text = "<h2>" + "What is Noon?" + "</h2> <br>"
                + "Noon is an online marketplace in the Middle East. Selling fashion, electronics, beauty, home, auto parts & baby products online in Middle East Countries."

                +"<br><h2>" + "What is Noon Invest?" + "</h2> <br>"
                +"Noon invest is a platform to participate in this online business, where you just invest with a fraction of cost and in return get paid daily."

                +"<br><h2>" + "What Noon dose with your investment?" + "</h2> <br>"
                +"Your investment empower our purchasing power so could manufacture/ import more products and sell them in our market place."

                +"<br><h2>" + "How big I can invest in Noon?" + "</h2> <br>"
                +"You can invest from as low as 25$ US Dollars and up to 4000$ Max."

                +"<br><h2>" + "How will I get my return?" + "</h2> <br>"
                +"After your participation in investment your return profit will start coming to your wallet daily, details of investment and daily income is in mentioned in the category wise list in noon invest home page."

                +"<br><h2>" + "How to withdraw my money?" + "</h2> <br>"
                +"You can apply for withdraw in withdraw amount section by submitting your bank details, it takes 3 working days to transfer the withdraw amount to your bank account."

                +"<br><h2>" + "How can I earn for free without investing?" + "</h2> <br>"
                +"As part of promotional period (limited time) you can earn by just sharing the app with your friends and family, in return you will be rewarded by 0.25$ US Dollars daily.";


                faqText.setText(Html.fromHtml(text));
    }
}