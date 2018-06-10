package co.com.millennialapps.owlmapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.Collections;
import java.util.LinkedList;

import co.com.millennialapps.owlmapp.R;
import co.com.millennialapps.owlmapp.activities.BuildDetailActivity;
import co.com.millennialapps.owlmapp.activities.MainActivity;
import co.com.millennialapps.owlmapp.adapters.RclBuildingsAdapter;
import co.com.millennialapps.owlmapp.models.Building;
import co.com.millennialapps.owlmapp.models.Node;
import co.com.millennialapps.owlmapp.utilitites.Shared;
import co.com.millennialapps.utils.firebase.FFirestoreManager;
import co.com.millennialapps.utils.tools.Preferences;
import co.com.millennialapps.utils.tools.SearchBarHandler;

public class BuildingsFragment extends Fragment {

    private LinkedList<Building> buildings = new LinkedList<>();
    private RecyclerView rclBuildings;
    private RclBuildingsAdapter adapter;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_places, container, false);

        rclBuildings = view.findViewById(R.id.rclBuildings);
        adapter = new RclBuildingsAdapter(getActivity(), buildings,
                (building, context) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("building", new Gson().toJson(building));
                    Intent i = new Intent(context, BuildDetailActivity.class);
                    i.putExtras(bundle);
                    getActivity().startActivity(i);
                });

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

        Collections.sort(buildings, (o1, o2) -> o1.getNumber().compareTo(o2.getNumber()));

        adapter.setBuildings(buildings);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rclBuildings.setLayoutManager(mLayoutManager);
        rclBuildings.setAdapter(adapter);

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.buildings, menu);
        this.menu = menu;

        handleBarIcons();

        ((MainActivity) getActivity()).setSbHandler(new SearchBarHandler(getActivity(), menu, R.id.action_search, R.string.search,
                null, R.color.colorPrimaryDark));
        ((MainActivity) getActivity()).getSbHandler().addChangeTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Preferences.saveSuggestion(getActivity(), query);
                query = query.toLowerCase();
                if (!((MainActivity) getActivity()).getSbHandler().getSearchView().isIconified()) {
                    ((MainActivity) getActivity()).getSbHandler().getSearchView().setIconified(true);
                }
                ((MainActivity) getActivity()).getSbHandler().close();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ((MainActivity) getActivity()).getSbHandler().populateAdapter(s);
                LinkedList<Building> filtered = new LinkedList<>();
                for (Building building : buildings) {
                    String name = removeAccents(building.getName().toLowerCase());
                    String query = removeAccents(s.toLowerCase());
                    if (name.contains(query)
                            || building.getNumber().contains(query)
                            || (building.getNumber() + " " + name).contains(query)) {
                        filtered.add(building);

                    }
                }

                adapter.setBuildings(filtered);

                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBarIcons() {
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(true);
    }

    public String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}