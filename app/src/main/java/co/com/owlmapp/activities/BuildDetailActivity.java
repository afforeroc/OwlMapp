package co.com.owlmapp.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.gson.Gson;

import co.com.millennialapps.utils.activities.BaseActivity;
import co.com.millennialapps.utils.tools.DialogManager;
import co.com.owlmapp.R;
import co.com.owlmapp.models.Building;

public class BuildDetailActivity extends BaseActivity implements OnStreetViewPanoramaReadyCallback {

    private Building building;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_detail);

        building = new Gson().fromJson(getIntent().getStringExtra("building"), Building.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(building.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((TextView) findViewById(R.id.txtDescription)).setText(building.getDescription());
/*        ((TextView) findViewById(R.id.txtState)).setText(building.getState() + " - " + building.getCity());
        ((TextView) findViewById(R.id.txtAddress)).setText(building.getAddress());*/

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.street_view);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        //streetViewPanorama.setPosition(building.getLatLng());
        streetViewPanorama.setPosition(new LatLng(4.635298, -74.082705));

        streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaLocation -> {
            if (streetViewPanoramaLocation != null && streetViewPanoramaLocation.links != null) {
                // location is present
            } else {
                DialogManager.showSnackbar(BuildDetailActivity.this, "La ubicación no está disponible");
            }
        });
    }
}
