package com.techroof.nooninvest.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.techroof.nooninvest.Authentication.LoginActivity;
import com.techroof.nooninvest.HomeActivity;
import com.techroof.nooninvest.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private boolean isFirstAnimation = false;
    private TextView taglineText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        taglineText=findViewById(R.id.tagline_text);

    /*Simple hold animation to hold ImageView in the centre of the screen at a slightly larger
        scale than the ImageView's original one.*/

        /* Translates ImageView from current position to its original position, scales it from
        larger scale to its original scale.*/

        Animation hold = AnimationUtils.loadAnimation(this, R.anim.hold);

        final Animation translateScale = AnimationUtils.loadAnimation(this, R.anim.translate_scale);

        final ImageView imageView = findViewById(R.id.header_icon);

        translateScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                taglineText.setVisibility(View.VISIBLE);


                if (!isFirstAnimation) {
                    imageView.clearAnimation();
                    Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

                isFirstAnimation = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        hold.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.clearAnimation();
                imageView.startAnimation(translateScale);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(hold);

    }
}