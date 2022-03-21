package com.techroof.nooninvest.WithDrawals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.techroof.nooninvest.Adapters.WithDrawalFragmentViewPagerAdapter;
import com.techroof.nooninvest.R;

public class WithdrawalAmounts extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_amounts);

        imageView=findViewById(R.id.img_back_arrow);
        imageView.setClipToOutline(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tabLayout=findViewById(R.id.tablayout_show_withdrawals);
        viewPager=findViewById(R.id.viewpager_withdrawals_amounts);
        tabLayout.setupWithViewPager(viewPager);
        WithDrawalFragmentViewPagerAdapter withDrawalFragmentViewPagerAdapter=
                new WithDrawalFragmentViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        withDrawalFragmentViewPagerAdapter.addfragment(new WalletWithdrawalFragment(),"Wallets");
       withDrawalFragmentViewPagerAdapter.addfragment(new RefferalWithdrawalFragment(),"Refferals");
        viewPager.setAdapter(withDrawalFragmentViewPagerAdapter);



    }
}