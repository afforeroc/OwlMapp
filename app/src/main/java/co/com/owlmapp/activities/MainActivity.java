package co.com.owlmapp.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import co.com.owlmapp.R;
import co.com.owlmapp.fragments.EventsFragment;
import co.com.owlmapp.fragments.MapFragment;
import co.com.owlmapp.fragments.BuildingsFragment;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            buildTabs();
        } else {
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        //TODO FAuthManager.getInstance().authAnonymously()
        FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener(this, authResult -> buildTabs()
        ).addOnFailureListener(this, exception -> Log.e("TAG", "signInAnonymously:FAILURE", exception));
    }

    private void buildTabs() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fragmentManager = getSupportFragmentManager();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[]{"Mapa", "Edificios", "Eventos",};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new MapFragment();
                case 1:
                    return new BuildingsFragment();
                case 2:
                    return new EventsFragment();
                default:
                    return new EventsFragment();
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}