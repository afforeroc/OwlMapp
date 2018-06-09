package co.com.millennialapps.owlmapp.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.gson.Gson;

import co.com.millennialapps.utils.activities.BaseActivity;
import co.com.millennialapps.utils.firebase.FStorageManager;
import co.com.millennialapps.utils.tools.DialogManager;
import co.com.millennialapps.owlmapp.R;
import co.com.millennialapps.owlmapp.models.Building;

public class BuildDetailActivity extends BaseActivity implements OnStreetViewPanoramaReadyCallback {

    private Building building;
    private View streetViewPanoramaFragment;
    private ImageView imgBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_detail);

        streetViewPanoramaFragment = findViewById(R.id.street_view);
        imgBuilding = findViewById(R.id.imgBuilding);

        building = new Gson().fromJson(getIntent().getStringExtra("building"), Building.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(building.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((TextView) findViewById(R.id.txtNumber)).setText(building.getNumber());
        if (building.getDescription() == null || building.getDescription().isEmpty()) {
            ((TextView) findViewById(R.id.txtDescription)).setText(R.string.no_description);
        } else {
            ((TextView) findViewById(R.id.txtDescription)).setText(building.getDescription()
                    .replace("\\n", System.getProperty("line.separator") + "" + System.getProperty("line.separator")));
        }
        if (building.getPhone() == null || building.getPhone().isEmpty()) {
            ((TextView) findViewById(R.id.txtPhone)).setText(R.string.no_phone);
        } else {
            ((TextView) findViewById(R.id.txtPhone)).setText(building.getPhone());
        }
        if (building.getEmail() == null || building.getEmail().isEmpty()) {
            ((TextView) findViewById(R.id.txtEmail)).setText(R.string.no_email);
        } else {
            ((TextView) findViewById(R.id.txtEmail)).setText(building.getEmail());
        }
        if (building.getWebPage() == null || building.getWebPage().isEmpty()) {
            ((TextView) findViewById(R.id.txtWebPage)).setText(R.string.no_web_page);
        } else {
            ((TextView) findViewById(R.id.txtWebPage)).setText(building.getWebPage());
        }
/*        ((TextView) findViewById(R.id.txtState)).setText(building.getState() + " - " + building.getCity());
        ((TextView) findViewById(R.id.txtAddress)).setText(building.getAddress());*/

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.street_view);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(building.getLatLng());

        streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaLocation -> {
            if (streetViewPanoramaLocation == null || streetViewPanoramaLocation.links == null) {
                DialogManager.showSnackbar(BuildDetailActivity.this, "La ubicación no está disponible");
                streetViewPanoramaFragment.setVisibility(View.INVISIBLE);
                imgBuilding.setVisibility(View.VISIBLE);
                FStorageManager.getInstance().downloadImage(BuildDetailActivity.this, imgBuilding,
                        Building.class.getSimpleName() + "/" + building.getNumber());
            }
        });
    }
}
