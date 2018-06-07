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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import co.com.millennialapps.owlmapp.R;
import co.com.millennialapps.owlmapp.models.Building;
import co.com.millennialapps.owlmapp.models.Node;
import co.com.millennialapps.owlmapp.utilitites.Dijkstra;
import co.com.millennialapps.utils.firebase.FFirestoreManager;
import co.com.millennialapps.utils.models.route.Step;
import co.com.millennialapps.utils.tools.MapHandler;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapHandler mapHandler;
    private AutoCompleteTextView edtOrigin;
    private AutoCompleteTextView edtDestination;
    //private FloatingActionButton fabMapType;

    //Lists about Places
    private HashMap<String, String> sNomPlaces = new HashMap<>();
    private HashMap<String, Node> nodes = new HashMap<>();

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
        //fabMapType = view.findViewById(R.id.fabMapType);

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        FFirestoreManager.getInstance().get(getActivity(), "Nodes",
                task -> {
                    LinkedList<Building> buildings = new LinkedList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Node node = document.toObject(Node.class);
                        node.setId(document.getId());
                        nodes.put(document.getId(), node);
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

                    for (Building b : buildings) {
                        sNomPlaces.put(b.getId(), b.getNumber() + " " + b.getName());
                    }
                    List<String> names = new ArrayList<>(sNomPlaces.values());
                    Collections.sort(names);
                    ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, names.toArray(new String[sNomPlaces.size()]));

                    edtOrigin.setAdapter(adapterPlaces);
                    edtDestination.setAdapter(adapterPlaces);
                });

        //fabMapType.setOnClickListener(v -> changeType(v));

        edtOrigin.setThreshold(1);
        edtOrigin.setOnItemClickListener((parent, myView, position, id) -> {
            String sPlace = (String) parent.getItemAtPosition(position);
            markerPosA = setMarker(markerPosA, getNode(sPlace), sPlace);
            sendRequest();
            hideSoftKeyBoard(getActivity());
            edtOrigin.setText(sPlace);
        });
        edtDestination.setThreshold(1);
        edtDestination.setOnItemClickListener((parent, myView, position, id) -> {
            String sPlace = (String) parent.getItemAtPosition(position);
            markerPosB = setMarker(markerPosB, getNode(sPlace), sPlace);
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

    public Node getNode(String sPlace) {
        for (Map.Entry<String, String> value : sNomPlaces.entrySet()) {
            if (value.getValue().equals(sPlace)) {
                return nodes.get(value.getKey());
            }
        }
        return null;
    }

    public Marker setMarker(Marker markerPosX, Node node, String sPlace) {
        if (markerPosX != null) {
            markerPosX.remove();
        }

        Marker vMarker;
        if (nodeFrom == null) {
            vMarker = mapHandler.addMarker(sPlace, node.getLatLng(), sPlace, MapHandler.HUE_GREEN, node.getName(), node.getDescription());
            nodeFrom = node;
        } else {
            vMarker = mapHandler.addMarker(sPlace, node.getLatLng(), sPlace, MapHandler.HUE_RED, node.getName(), node.getDescription());
            nodeTo = node;
        }

        mapHandler.zoomTo(16f, node.getLatLng(), true);
        return vMarker;
    }

    public static void hideSoftKeyBoard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void sendRequest() {
        if (nodeFrom == null || nodeTo == null || nodeFrom == nodeTo) {
            return;
        }

        mapHandler.clearPolylines();
        mapHandler.zoomToMarkers();
        Dijkstra.getInstance(mapHandler).findShortestPath(nodes, nodeFrom, nodeTo);
        Dijkstra.getInstance(mapHandler).paintPath(getActivity(), nodeFrom, nodeTo);
        /*mapHandler.routes(fromPos, toPos, getString(R.string.google_maps_key),
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
                });*/
    }
}
