package ml.owlmapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ml.owlmapp.models.Event;
import ml.owlmapp.tabs.R;

/**
 * Created by Erick Velasco on 9/5/2018.
 */

public class RclEventsAdapter extends RecyclerView.Adapter<RclEventsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Event mall, Context context);
    }

    private final List<Event> events;
    private final OnItemClickListener listener;
    private final Activity activity;

    public RclEventsAdapter(Activity activity, List<Event> events, OnItemClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.events = events;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public RclEventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.item_rcl_events, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(events.get(position), listener);
        holder.txtDescription.setText(events.get(position).getDescription());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtDescription;

        ViewHolder(View view) {
            super(view);
            txtDescription = view.findViewById(R.id.txtDescription);
        }

        public void bind(final Event item, final RclEventsAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, activity);
                }
            });
        }
    }
}