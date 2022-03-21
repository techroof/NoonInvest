package com.techroof.nooninvest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateDeepLinkActivity extends AppCompatActivity {

    Button btnSharelink;
    TextView tvCreatelink;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String invtLink;
    private String Date, currentDate;
    private String uId;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deep_link);

        btnSharelink = findViewById(R.id.btn_share);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        tvCreatelink = findViewById(R.id.create_link);
        firebaseAuth = FirebaseAuth.getInstance();
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Date = currentDate;
        backBtn=findViewById(R.id.img_back_arrow);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });


        btnSharelink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uId = FirebaseAuth.getInstance().getUid();
                CreateLink();
                //share();
            }
        });
    }

    public void CreateLink() {

        uId = FirebaseAuth.getInstance().getUid();
        String link = "https://www.nooninvest.com/?invitedby=" + uId + "&date=" + Date;

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://nooninvest.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        Toast.makeText(getApplicationContext(), "" + dynamicLinkUri, Toast.LENGTH_SHORT).show();
        invtLink = String.valueOf(dynamicLinkUri);

        tvCreatelink.setText(invtLink);

        ShareLink();

    }

    public void ShareLink() {

        String referrerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String subject = String.format("%s wants you to invest in NoonInvest app!", referrerName);
        String invitationLink = invtLink;
        String msg = "Let's Invest in NoonInvest"
                + invitationLink;
        String msgHtml = String.format("Let's invest together from NoonInvest App!  "
                + "\"%s\"", invitationLink);

        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //intent.putExtra(Intent.EXTRA_TEXT, msg);
        //intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        //if (intent.resolveActivity(getPackageManager()) != null) {
        //startActivity(intent);

        intent.putExtra(Intent.EXTRA_TEXT,msgHtml);
        intent.setType("text/plain");
        startActivity(intent);
        // }
    }
}

