package co.com.millennialapps.owlmapp.fragments;

import android.app.Activity;
import android.os.Bundle;
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

public class MapFragment extends Fragment implements OnMapReadyCallback {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        edtOrigin = view.findViewById(R.id.edtOrigin);
        edtDestination = view.findViewById(R.id.edtDestination);
        ibtClearOrigin = view.findViewById(R.id.ibtClearOrigin);
        ibtClearDestination = view.findViewById(R.id.ibtClearDestination);
        //fabMapType = view.findViewById(R.id.fabMapType);

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        LinkedList<Building> buildings = new LinkedList<>();
        for (Node node : Shared.nodes.values()) {
            if (!node.getType().equals("Camino")) {
                Building building = new Building();
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
            sNomPlaces.put(b.getId(), b.getNumber() + " " + b.getName());
        }
        List<String> names = new ArrayList<>(sNomPlaces.values());
        Collections.sort(names);
        ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, names.toArray(new String[sNomPlaces.size()]));

        edtOrigin.setAdapter(adapterPlaces);
        edtDestination.setAdapter(adapterPlaces);

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
            nodeFrom = getNode(sPlace);
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
}
