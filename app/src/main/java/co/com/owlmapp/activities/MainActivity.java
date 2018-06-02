package co.com.owlmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import co.com.owlmapp.R;
import co.com.owlmapp.SearchBarHandler;
import co.com.owlmapp.adapters.TabSectionsAdapter;

public class MainActivity extends AppCompatActivity {

    private static FragmentManager fragmentManager;
    private SearchBarHandler sbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(new TabSectionsAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(sbHandler != null) {
                    sbHandler.close();
                }
            }
        });

        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.act_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public SearchBarHandler getSbHandler() {
        return sbHandler;
    }

    public void setSbHandler(SearchBarHandler sbHandler) {
        this.sbHandler = sbHandler;
    }
}