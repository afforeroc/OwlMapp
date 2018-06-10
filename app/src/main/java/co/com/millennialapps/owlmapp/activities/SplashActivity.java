package co.com.millennialapps.owlmapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Collections;

import co.com.millennialapps.owlmapp.models.Building;
import co.com.millennialapps.owlmapp.models.Node;
import co.com.millennialapps.owlmapp.utilitites.Shared;
import co.com.millennialapps.utils.firebase.FFirestoreManager;
import co.com.millennialapps.utils.tools.ConnectionManager;
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
                if (ConnectionManager.hasInternet(SplashActivity.this)) {
                    FFirestoreManager.getInstance().get(SplashActivity.this, "Nodes", task -> {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Node node = document.toObject(Node.class);
                            node.setId(document.getId());
                            Shared.nodes.put(document.getId(), node);
                        }
                        logoOut();
                    });
                } else {
                    DialogManager.showConfirmationDialog(SplashActivity.this, R.string.not_internet,
                            "Parece que no tiene internet ahora. Si estás dentro de la Universidad puedes conectarte a su red. Inténtalo y trata de nuevo.",
                            (dialog, which) -> {
                                System.exit(0);
                            }, (dialog, which) -> {
                                System.exit(0);
                            });
                }
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