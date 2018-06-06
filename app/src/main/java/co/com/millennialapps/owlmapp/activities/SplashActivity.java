package co.com.millennialapps.owlmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import co.com.millennialapps.utils.tools.DialogManager;
import co.com.millennialapps.owlmapp.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgLogo = findViewById(R.id.imgLogo);
        Animation logo_in = AnimationUtils.loadAnimation(this, R.anim.in_fade_from_down);
        logo_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logoOut();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgLogo.startAnimation(logo_in);
    }

    private void logoOut() {
        DialogManager.dismissLoadingDialog();
        Animation logo_out = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.out_fade_to_up);
        logo_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgLogo.startAnimation(logo_out);
    }
}