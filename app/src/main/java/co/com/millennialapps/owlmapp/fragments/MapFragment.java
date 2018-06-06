package co.com.millennialapps.owlmapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import co.com.millennialapps.owlmapp.R;
import co.com.millennialapps.owlmapp.models.Building;
import co.com.millennialapps.owlmapp.models.Node;
import co.com.millennialapps.utils.firebase.FFirestoreManager;
import co.com.millennialapps.utils.models.route.Step;
import co.com.millennialapps.utils.tools.MapHandler;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapHandler mapHandler;
    private AutoCompleteTextView edtOrigin;
    private AutoCompleteTextView edtDestination;
    //private FloatingActionButton fabMapType;

    //Lists about Places
    private List<String> sNomPlaces = new ArrayList<>();
    private List<LatLng> listLatLngPlaces = new ArrayList<>();

    //Properties of position A and position B
    private LatLng fromPos;
    private LatLng toPos;
    private Marker markerPosA;
    private Marker markerPosB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        edtOrigin = view.findViewById(R.id.edtOrigin);
        edtDestination = view.findViewById(R.id.edtDestination);
        //fabMapType = view.findViewById(R.id.fabMapType);

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        FFirestoreManager.getInstance().get(getActivity(), "Nodes",
                task -> {
                    LinkedList<Building> buildings = new LinkedList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Node node = document.toObject(Node.class);
                        node.setId(document.getId());
                        if (!node.getType().equals("Camino")) {
                            Building building = new Building();
                            building.setId(node.getId());
                            building.setDescription(node.getDescription());
                            building.setLatitude(node.getLatitude());
                            building.setLongitude(node.getLongitude());
                            building.setName(node.getName());
                            building.setNumber(node.getNumber());
                            buildings.add(building);
                        }
                    }
                    Collections.sort(buildings, (o1, o2) -> o1.getNumber().compareTo(o2.getNumber()));
                    for (Building b : buildings) {
                        listLatLngPlaces.add(b.getLatLng());
                        sNomPlaces.add(b.getNumber() + " " + b.getName());
                    }

                    ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_list_item_1, sNomPlaces);

                    edtOrigin.setAdapter(adapterPlaces);
                    edtDestination.setAdapter(adapterPlaces);
                });

        //fabMapType.setOnClickListener(v -> changeType(v));

        edtOrigin.setThreshold(1);
        edtOrigin.setOnItemClickListener((parent, myView, position, id) -> {
            String sPlace = (String) parent.getItemAtPosition(position);
            fromPos = setLatLng(sPlace);
            markerPosA = setMarker(markerPosA, fromPos, 0, sPlace);
            sendRequest();
            hideSoftKeyBoard(getActivity());
            edtOrigin.setText(sPlace);
        });
        edtDestination.setThreshold(1);
        edtDestination.setOnItemClickListener((parent, myView, position, id) -> {
            String sPlace = (String) parent.getItemAtPosition(position);
            toPos = setLatLng(sPlace);
            markerPosB = setMarker(markerPosB, toPos, 1, sPlace);
            sendRequest();
            hideSoftKeyBoard(getActivity());
            edtDestination.setText(sPlace);
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mapHandler == null) {
            mapHandler = new MapHandler(googleMap);
        }

        mapHandler.changeMapType(MapHandler.MAP_SATELLITE);

        mapHandler.zoomTo(15.5f, new LatLng(4.637529, -74.083992), false);

        mapHandler.addInfoWindowListener(marker -> {
            Toast.makeText(getActivity(),
                    marker.getPosition().latitude + ", " + marker.getPosition().longitude,
                    Toast.LENGTH_LONG).show();
            return false;
        }, null, null);
    }

    public LatLng setLatLng(String sPlace) {
        int index = sNomPlaces.indexOf(sPlace);
        LatLng vLatLng = listLatLngPlaces.get(index);
        return vLatLng;
    }

    public Marker setMarker(Marker markerPosX, LatLng latLngPosX, int isAB, String sPlace) {
        if (markerPosX != null) {
            markerPosX.remove();
        }

        Marker vMarker = mapHandler.addMarker(sPlace, latLngPosX, sPlace);

        mapHandler.zoomTo(16f, latLngPosX, true);
        return vMarker;
    }

    public static void hideSoftKeyBoard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void sendRequest() {
        if (fromPos == null || toPos == null || fromPos == toPos) {
            return;
        }

        mapHandler.clearPolylines();
        mapHandler.zoomToMarkers();
        mapHandler.routes(fromPos, toPos, "AIzaSyDplSLBMqp_SN9R3vdAT--pdMQ4evwrcWM",
                result -> {
                    if(result.getRoutes().size() > 0){
                        if(result.getRoutes().get(0).getLegs().size() > 0){
                            if(result.getRoutes().get(0).getLegs().get(0).getSteps().size() > 0){
                                for(Step step : result.getRoutes().get(0).getLegs().get(0).getSteps()){
                                    mapHandler.addPolyline(ContextCompat.getColor(getActivity(), R.color.black), 5, step.getPolyline().getDecodedPoints());
                                }
                            }
                        }
                    }
                });
    }
}
