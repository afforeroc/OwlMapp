package co.com.millennialapps.owlmapp.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import co.com.millennialapps.owlmapp.R;
import co.com.millennialapps.owlmapp.models.Building;
import co.com.millennialapps.owlmapp.models.Node;
import co.com.millennialapps.owlmapp.utilitites.Dijkstra;
import co.com.millennialapps.owlmapp.utilitites.Shared;
import co.com.millennialapps.utils.firebase.FFirestoreManager;
import co.com.millennialapps.utils.tools.DialogManager;
import co.com.millennialapps.utils.tools.MapHandler;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private MapHandler mapHandler;
    private AutoCompleteTextView edtOrigin;
    private AutoCompleteTextView edtDestination;
    private ImageButton ibtClearOrigin;
    private ImageButton ibtClearDestination;
    //private FloatingActionButton fabMapType;

    //Lists about Places
    private HashMap<String, String> sNomPlaces = new HashMap<>();

    //Properties of position A and position B
    private Marker markerPosA;
    private Marker markerPosB;
    private Node nodeFrom;
    private Node nodeTo;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        edtOrigin = view.findViewById(R.id.edtOrigin);
        edtDestination = view.findViewById(R.id.edtDestination);
        ibtClearOrigin = view.findViewById(R.id.ibtClearOrigin);
        ibtClearDestination = view.findViewById(R.id.ibtClearDestination);
        //fabMapType = view.findViewById(R.id.fabMapType);

        fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        LinkedList<Building> buildings = new LinkedList<>();
        Building building;
        for (Node node : Shared.nodes.values()) {
            if (!node.getType().equals("Camino")) {
                building = new Building();
                building.setId(node.getId());
                building.setDescription(node.getDescription());
                building.setLatitude(node.getLatitude());
                building.setLongitude(node.getLongitude());
                building.setName(node.getName());
                building.setNumber(node.getNumber());
                building.setEmail(node.getEmail());
                building.setPhone(node.getPhone());
                building.setWebPage(node.getWebPage());
                buildings.add(building);
            }
        }

        for (Building b : buildings) {
            if (b.getNumber() != null) {
                sNomPlaces.put(b.getId(), b.getNumber() + " " + b.getName());
            } else {
                sNomPlaces.put(b.getId(), b.getName());
            }
        }
        List<String> names = new ArrayList<>(sNomPlaces.values());
        Collections.sort(names);

        edtDestination.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, names.toArray(new String[sNomPlaces.size()])));
        names.add(0, "Tu ubicación");
        edtOrigin.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, names.toArray(new String[sNomPlaces.size()])));

        ibtClearOrigin.setOnClickListener(v -> {
            edtOrigin.setText("");
            nodeFrom = null;
            if (markerPosA != null) {
                markerPosA.remove();
                markerPosA = null;
            }
            mapHandler.clearPolylines();
        });
        ibtClearDestination.setOnClickListener(v -> {
            edtDestination.setText("");
            nodeTo = null;
            if (markerPosB != null) {
                markerPosB.remove();
                markerPosB = null;
            }
            mapHandler.clearPolylines();
        });

        edtOrigin.setThreshold(1);
        edtOrigin.setOnItemClickListener((parent, myView, position, id) -> {
            String sPlace = (String) parent.getItemAtPosition(position);
            if (sPlace.equals("Tu ubicación")) {
                nodeFrom = Dijkstra.getInstance(mapHandler).getCloserNodeFromMyLocation(mapHandler, Shared.nodes);
            } else {
                nodeFrom = getNode(sPlace);
            }
            markerPosA = setMarker(markerPosA, nodeFrom, true, sPlace);
            sendRequest();
            hideSoftKeyBoard();
            edtOrigin.setText(sPlace);
        });
        edtOrigin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ibtClearOrigin.setVisibility(View.VISIBLE);
                } else {
                    ibtClearOrigin.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtDestination.setThreshold(1);
        edtDestination.setOnItemClickListener((parent, myView, position, id) -> {
            String sPlace = (String) parent.getItemAtPosition(position);
            nodeTo = getNode(sPlace);
            markerPosB = setMarker(markerPosB, nodeTo, false, sPlace);
            sendRequest();
            hideSoftKeyBoard();
            edtDestination.setText(sPlace);
        });
        edtDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ibtClearDestination.setVisibility(View.VISIBLE);
                } else {
                    ibtClearDestination.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mapHandler == null) {
            mapHandler = new MapHandler(googleMap);
        }

        mapHandler.changeMapType(MapHandler.MAP_SATELLITE);
        mapHandler.enableAutoLocation(getActivity(), mGoogleApiClient, MapHandler.VIEW_LOCATION);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        //googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.setOnMyLocationChangeListener(location -> {
            //TODO
        });

        mapHandler.zoomTo(15.5f, new LatLng(4.637529, -74.083992), false);

        mapHandler.addInfoWindowListener(marker -> {
            Toast.makeText(getActivity(),
                    marker.getPosition().latitude + ", " + marker.getPosition().longitude,
                    Toast.LENGTH_LONG).show();
            return false;
        }, null, null);

        if (mapHandler.distance(mapHandler.getMyLocation(), new LatLng(4.637529, -74.083992), MapHandler.METERS) > 600) {
            DialogManager.showMessageDialog(getActivity(), R.string.warning, "Ups, parece que estás muy lejos de la universidad como para darte una ruta desde tu ubicación actual. Pero aun puedes mirar rutas entre edificios y lugares de la U. ¡Inténtalo!");
        } else {
            nodeFrom = Dijkstra.getInstance(mapHandler).getCloserNodeFromMyLocation(mapHandler, Shared.nodes);
            edtOrigin.setText("Tu ubicación");
            edtOrigin.clearFocus();
        }
    }

    public Node getNode(String sPlace) {
        for (Map.Entry<String, String> value : sNomPlaces.entrySet()) {
            if (value.getValue().equals(sPlace)) {
                return Shared.nodes.get(value.getKey());
            }
        }
        return null;
    }

    public Marker setMarker(Marker markerPosX, Node node, boolean isFrom, String sPlace) {
        if (markerPosX != null) {
            markerPosX.remove();
        }

        Marker vMarker;

        if (isFrom) {
            vMarker = mapHandler.addMarker(sPlace, node.getLatLng(), sPlace, MapHandler.HUE_GREEN, node.getName(), node.getDescription());
        } else {
            vMarker = mapHandler.addMarker(sPlace, node.getLatLng(), sPlace, MapHandler.HUE_RED, node.getName(), node.getDescription());
        }

        mapHandler.zoomTo(16f, node.getLatLng(), true);
        return vMarker;
    }

    public void hideSoftKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void sendRequest() {
        if (nodeFrom == null || nodeTo == null || nodeFrom == nodeTo) {
            return;
        }

        mapHandler.clearPolylines();
        mapHandler.zoomToMarkers();
        Dijkstra.getInstance(mapHandler).findShortestPath(Shared.nodes, nodeFrom, nodeTo);
        Dijkstra.getInstance(mapHandler).paintPath(getActivity(), nodeFrom, nodeTo);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        fragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
