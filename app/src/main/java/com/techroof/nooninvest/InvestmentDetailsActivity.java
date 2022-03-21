package com.techroof.nooninvest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.techroof.nooninvest.Adapters.HomeFragmentViewPagerAdapter;

public class InvestmentDetailsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView imageView,investmentDetailsImg;
    private String imageUrl,catText;
    private TextView catTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment_details);

        tabLayout=findViewById(R.id.tablayoutClassInvestments);
        viewPager=findViewById(R.id.viewpager_ClassInvestment);
        imageView=findViewById(R.id.img_back_arrow);
        investmentDetailsImg=findViewById(R.id.cat_img);
        catTv=findViewById(R.id.cat_text);

        imageUrl=getIntent().getStringExtra("image_url");
        catText=getIntent().getStringExtra("category");

        catTv.setText(""+catText);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                InvestmentDetailsActivity.super.onBackPressed();
            }
        });

        Glide.with(InvestmentDetailsActivity.this).load(imageUrl).into(investmentDetailsImg);

        tabLayout.setupWithViewPager(viewPager);
        HomeFragmentViewPagerAdapter viewPagerAdapter=
                new HomeFragmentViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.addfragment(new ClassAFragment(),"Class A");
        viewPagerAdapter.addfragment(new ClassBFragment(),"Class B");
        viewPagerAdapter.addfragment(new ClassCFragment(),"Class C");
        viewPager.setAdapter(viewPagerAdapter);

    }
}