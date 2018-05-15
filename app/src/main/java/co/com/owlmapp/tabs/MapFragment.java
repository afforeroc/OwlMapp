package co.com.owlmapp.tabs;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;
import co.com.owlmapp.R;

import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MapFragment extends Fragment implements OnMapReadyCallback, DirectionFinderListener {

    //Objects of fragment
    private SupportMapFragment fragment;
    private GoogleMap mMap;
    private AutoCompleteTextView actvOrigin;
    private AutoCompleteTextView actvDestination;
    private Button bTypeMap;

    //Lists about Places
    private String[] sNomPlaces;
    private List<LatLng> listLatLngPlaces = new ArrayList<>();
    private List<String> listNomPlaces = new ArrayList<>();

    //Properties of position A and position B
    private LatLng LatLngPosA;
    private LatLng LatLngPosB;
    private LatLng LatLngMidPoint;
    private Marker markerPosA;
    private Marker markerPosB;

    //Properties of routes
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        sNomPlaces = getResources().getStringArray(R.array.unLugares);
        ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, sNomPlaces);

        actvOrigin = view.findViewById(R.id.actvOrigin);
        actvOrigin.setAdapter(adapterPlaces);

        actvDestination = view.findViewById(R.id.actvDestination);
        actvDestination.setAdapter(adapterPlaces);

        bTypeMap = view.findViewById(R.id.MapType);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        listNomPlaces = Arrays.asList(sNomPlaces);
        addAllLatLng();

        bTypeMap.setOnClickListener(v -> changeType(v));

        actvOrigin.setOnItemClickListener((parent, view, position, id) -> {
            String sPlace = (String) parent.getItemAtPosition(position);
            LatLngPosA = setLatLng(sPlace);
            markerPosA = setMarker(markerPosA, LatLngPosA, 0, sPlace);
            sendRequest();
            hideSoftKeyBoard(getActivity());
            actvOrigin.setText("");
            actvOrigin.setHint(sPlace);
        });

        actvDestination.setOnItemClickListener((parent, view, position, id) -> {
            String sPlace = (String) parent.getItemAtPosition(position);
            LatLngPosB = setLatLng(sPlace);
            markerPosB = setMarker(markerPosB, LatLngPosB, 1, sPlace);
            sendRequest();
            hideSoftKeyBoard(getActivity());
            actvDestination.setText("");
            actvDestination.setHint(sPlace);
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng unBogota = new LatLng(4.637529, -74.083992);
        float zoom = (float) 15.5;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unBogota, zoom));

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

        mMap.setOnInfoWindowClickListener(MyOnInfoWindowClickListener);

    }

    public void changeType(View view) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public LatLng setLatLng(String sPlace) {
        int index = listNomPlaces.indexOf(sPlace);
        LatLng vLatLng = listLatLngPlaces.get(index);
        return vLatLng;
    }

    public double distance() {
        double lon1 = LatLngPosA.longitude;
        double lon2 = LatLngPosB.longitude;
        double lat1 = LatLngPosA.latitude;
        double lat2 = LatLngPosB.latitude;
        double x = 110.56 * (lat2 - lat1);
        double y = 84.8 * (lon2 - lon1);
        double d = sqrt(pow(x, 2) + pow(y, 2)) * 1000;
        d = floor(d * 100) / 100;
        return d;
    }

    public void setMidPoint() {
        double latC;
        double lngC;
        double d = distance();
        if (LatLngPosA != null && LatLngPosB != null) {
            latC = (LatLngPosA.latitude + LatLngPosB.latitude) / 2;
            lngC = (LatLngPosA.longitude + LatLngPosB.longitude) / 2;
            LatLngMidPoint = new LatLng(latC, lngC);

            if (d > 600) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLngMidPoint, 15));
            } else if (d > 400 && d <= 600) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLngMidPoint, 16));
            } else if (d >= 130 && d <= 400) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLngMidPoint, 17));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLngMidPoint, 18));
            }
        }
    }

    public Marker setMarker(Marker MarkerPosX, LatLng LatLngPosX, int isAB, String sPlace) {

        if (MarkerPosX != null) MarkerPosX.remove();

        BitmapDescriptor icon;
        if (isAB == 0) icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_a);
        else icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_b);

        Marker vMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(LatLngPosX)
                        .icon(icon)
                        .title(sPlace)

        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLngPosX, 16));
        return vMarker;
    }

    public static void hideSoftKeyBoard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void sendRequest() {
        if (LatLngPosA == null || LatLngPosB == null || LatLngPosA == LatLngPosB) {
            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }
            return;
        }

        try {
            new DirectionFinder(this, LatLngPosA, LatLngPosB).execute();
            setMidPoint();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getContext(), "Espera un momento.",
                "Buscando dirección...!", true);

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();

        for (Route route : routes) {
            Toast.makeText(getContext(), "Tiempo: " + route.duration.text,
                    Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Distancia: " + route.distance.text,
                    Toast.LENGTH_SHORT).show();
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(7);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    public void addAllLatLng() {
        listLatLngPlaces.add(new LatLng(4.635170, -74.082409));
        listLatLngPlaces.add(new LatLng(4.635326, -74.083205));
        listLatLngPlaces.add(new LatLng(4.634553, -74.082743));
        listLatLngPlaces.add(new LatLng(4.635749, -74.082342));
        listLatLngPlaces.add(new LatLng(4.635468, -74.083796));
        listLatLngPlaces.add(new LatLng(4.634458, -74.083866));
        listLatLngPlaces.add(new LatLng(4.633965, -74.083143));
        listLatLngPlaces.add(new LatLng(4.634552, -74.085085));
        listLatLngPlaces.add(new LatLng(4.634375, -74.085449));
        listLatLngPlaces.add(new LatLng(4.634067, -74.084685)); //10
        listLatLngPlaces.add(new LatLng(4.633732, -74.084450));
        listLatLngPlaces.add(new LatLng(4.633455, -74.083948));
        listLatLngPlaces.add(new LatLng(4.633223, -74.083459));
        listLatLngPlaces.add(new LatLng(4.633953, -74.085934));
        listLatLngPlaces.add(new LatLng(4.633875, -74.086408));
        listLatLngPlaces.add(new LatLng(4.633030, -74.084416));
        listLatLngPlaces.add(new LatLng(4.632917, -74.084711));
        listLatLngPlaces.add(new LatLng(4.632828, -74.084438));
        listLatLngPlaces.add(new LatLng(4.632525, -74.083913));
        listLatLngPlaces.add(new LatLng(4.632682, -74.083234)); //20
        listLatLngPlaces.add(new LatLng(4.632452, -74.083444));
        listLatLngPlaces.add(new LatLng(4.633146, -74.081579));
        listLatLngPlaces.add(new LatLng(4.636312, -74.082206));
        listLatLngPlaces.add(new LatLng(4.636735, -74.081637));
        listLatLngPlaces.add(new LatLng(4.635859, -74.081278));
        listLatLngPlaces.add(new LatLng(4.636267, -74.080571));
        listLatLngPlaces.add(new LatLng(4.637234, -74.080647));
        listLatLngPlaces.add(new LatLng(4.636868, -74.080715));
        listLatLngPlaces.add(new LatLng(4.636217, -74.080780));
        listLatLngPlaces.add(new LatLng(4.634719, -74.080743)); //30
        listLatLngPlaces.add(new LatLng(4.637274, -74.082869));
        listLatLngPlaces.add(new LatLng(4.638352, -74.082975));
        listLatLngPlaces.add(new LatLng(4.637688, -74.082615));
        listLatLngPlaces.add(new LatLng(4.637728, -74.081787));
        listLatLngPlaces.add(new LatLng(4.638298, -74.082810));
        listLatLngPlaces.add(new LatLng(4.639202, -74.082155));
        listLatLngPlaces.add(new LatLng(4.638176, -74.081254));
        listLatLngPlaces.add(new LatLng(4.638663, -74.081873));
        listLatLngPlaces.add(new LatLng(4.638890, -74.081686));
        listLatLngPlaces.add(new LatLng(4.639190, -74.082646)); //40
        listLatLngPlaces.add(new LatLng(4.638713, -74.083188));
        listLatLngPlaces.add(new LatLng(4.639673, -74.083287));
        listLatLngPlaces.add(new LatLng(4.639583, -74.083708));
        listLatLngPlaces.add(new LatLng(4.640397, -74.081868));
        listLatLngPlaces.add(new LatLng(4.641341, -74.081588));
        listLatLngPlaces.add(new LatLng(4.642891, -74.083014));
        listLatLngPlaces.add(new LatLng(4.640725, -74.083575));
        listLatLngPlaces.add(new LatLng(4.641017, -74.082631));
        listLatLngPlaces.add(new LatLng(4.641266, -74.083427));
        listLatLngPlaces.add(new LatLng(4.641591, -74.083769)); //50
        listLatLngPlaces.add(new LatLng(4.641357, -74.084061));
        listLatLngPlaces.add(new LatLng(4.642000, -74.083465));
        listLatLngPlaces.add(new LatLng(4.637387, -74.083881));
        listLatLngPlaces.add(new LatLng(4.637791, -74.083463));
        listLatLngPlaces.add(new LatLng(4.637154, -74.084503));
        listLatLngPlaces.add(new LatLng(4.638493, -74.083705));
        listLatLngPlaces.add(new LatLng(4.637990, -74.084558)); //57
        listLatLngPlaces.add(new LatLng(4.636347, -74.084251));
        listLatLngPlaces.add(new LatLng(4.636911, -74.085072));
        listLatLngPlaces.add(new LatLng(4.636594, -74.085141)); //60
        listLatLngPlaces.add(new LatLng(4.637286, -74.085513));
        listLatLngPlaces.add(new LatLng(4.636890, -74.085253));
        listLatLngPlaces.add(new LatLng(4.636432, -74.085170));
        listLatLngPlaces.add(new LatLng(4.636099, -74.085290));
        listLatLngPlaces.add(new LatLng(4.638466, -74.085890));
        listLatLngPlaces.add(new LatLng(4.635800, -74.087249));
        listLatLngPlaces.add(new LatLng(4.635953, -74.087892));
        listLatLngPlaces.add(new LatLng(4.635310, -74.088750));
        listLatLngPlaces.add(new LatLng(4.635582, -74.088481));
        listLatLngPlaces.add(new LatLng(4.636540, -74.087933)); //70
        listLatLngPlaces.add(new LatLng(4.636716, -74.091092));
        listLatLngPlaces.add(new LatLng(4.634913, -74.080180));
        listLatLngPlaces.add(new LatLng(4.640408, -74.085511));
        listLatLngPlaces.add(new LatLng(4.641355, -74.084998));
        listLatLngPlaces.add(new LatLng(4.640590, -74.086291));
        listLatLngPlaces.add(new LatLng(4.639152, -74.087209));
        listLatLngPlaces.add(new LatLng(4.639443, -74.089728));
        listLatLngPlaces.add(new LatLng(4.640693, -74.091028));
        listLatLngPlaces.add(new LatLng(4.643463, -74.083360));
        listLatLngPlaces.add(new LatLng(4.644562, -74.084087)); //80
    }

    GoogleMap.OnInfoWindowClickListener MyOnInfoWindowClickListener = marker -> Toast.makeText(getActivity(),
            marker.getPosition().latitude + ", " + marker.getPosition().longitude,
            Toast.LENGTH_LONG).show();
}
