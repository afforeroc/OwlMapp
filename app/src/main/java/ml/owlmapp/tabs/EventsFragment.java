package ml.owlmapp.tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import Modules.TwitterConnection;
import ml.owlmapp.adapters.EventsAdapter;
import ml.owlmapp.models.Event;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;

public class EventsFragment extends Fragment {

    private RecyclerView rclEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_events, container, false);
        rclEvents = view.findViewById(R.id.rclEvents);

        TwitterConnection.getInstance().getTimelineFeedInBackground(new TwitterAdapter() {

            @Override
            public void gotUserTimeline(ResponseList<Status> statuses) {
                showTimeLine(statuses);
            }

            @Override
            public void onException(TwitterException e, TwitterMethod method) {
                Snackbar.make(view, "No pudimos conectarnos a Twitter. Revisa tu conexión de internet.", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public void showTimeLine(ResponseList<Status> statuses) {
        List<Event> events = new ArrayList<>();

        for (Status st : statuses) {
            Event event = new Event();
            event.setDescription(st.getText());
            event.setUrl(obtainUrl(st.getText()));
            events.add(event);
        }

        final EventsAdapter eventsAdapter = new EventsAdapter(EventsFragment.this.getActivity(), events, (event, context) -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(event.getUrl()));
            startActivity(i);
        });

        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EventsFragment.this.getActivity());

        EventsFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
                rclEvents.setLayoutManager(mLayoutManager);
                rclEvents.setAdapter(eventsAdapter);
            }
        });
    }

    public String obtainUrl(String text) {
        String url = "";
        int i = text.indexOf("https://t.co/");
        while (i < text.length()) {
            if (text.charAt(i) != ' ') {
                url += text.charAt(i);
                i++;
            } else {
                break;
            }
        }
        return url;
    }
}