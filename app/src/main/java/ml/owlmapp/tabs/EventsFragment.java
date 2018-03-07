package ml.owlmapp.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import Modules.TwitterConnection;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;

public class EventsFragment extends Fragment {

    private ListView list_view;
    private ArrayList<String> array = new ArrayList<String>();

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);



        //setContentView(R.layout.fragment_events);
        TwitterConnection.getInstance().getTimelineFeedInBackground(new TwitterAdapter() {

            @Override
            public void gotUserTimeline(ResponseList<Status> statuses) {
                showTimeLine(statuses);
            }

            @Override
            public void onException(TwitterException e, TwitterMethod method) {
                Log.e("TwitterException", Integer.toString(e.getErrorCode()) + " :: "
                        + e.getErrorMessage());
            }
        });

        return view;
    }

    public void showTimeLine(ResponseList < Status > statuses) {

        list_view = (ListView) getView().findViewById(R.id.listView);
        array = new ArrayList<String>();

        int id=0;
        for (Status  st : statuses) {
            Log.d("TIMELINE" + String.valueOf(id), st.getText());
            ++id;
            array.add( st.getText() );
        }
        final Context context = getContext();

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, array);
                list_view.setAdapter(adapter);

                View progressBar = getView().findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
            }
        });

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getItemAtPosition(position);
                String url = obtainUrl(text);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }


    public static String obtainUrl(String text){
        String url = "";

        int i = text.indexOf("https");
        char aChar;
        while(i<text.length()){
            aChar = text.charAt(i);
            if(aChar == ' ') break;
            url += aChar;
            i++;
        }
        return url;
    }

}
