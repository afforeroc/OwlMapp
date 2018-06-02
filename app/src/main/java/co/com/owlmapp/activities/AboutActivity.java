package co.com.owlmapp.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import co.com.millennialapps.utils.activities.BaseActivity;
import co.com.owlmapp.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ((TextView) findViewById(R.id.txtDeveloper)).setText(getString(R.string.developed_by) + ":\nAndrés Forero y Erick Velasco");
        ((TextView) findViewById(R.id.txtDesigner)).setText(getString(R.string.designed_by) + ":\nAndrés Forero y Erick Velasco");
        ((TextView) findViewById(R.id.txtThanks)).setText(getString(R.string.special_thanks));
        try {
            ((TextView) findViewById(R.id.txtVersion)).setText(getString(R.string.version) + " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
