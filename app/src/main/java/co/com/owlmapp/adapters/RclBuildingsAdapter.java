package co.com.owlmapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.com.millennialapps.utils.firebase.FStorageManager;
import co.com.owlmapp.R;
import co.com.owlmapp.models.Building;

public class RclBuildingsAdapter extends RecyclerView.Adapter<RclBuildingsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Building category, Context context);
    }

    private final List<Building> buildings;
    private final OnItemClickListener listener;
    private final Activity activity;

    public RclBuildingsAdapter(Activity activity, List<Building> buildings, OnItemClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.buildings = buildings;
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public RclBuildingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.item_rcl_building, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(buildings.get(position), listener);
        holder.txtName.setText(buildings.get(position).getName());
        holder.txtNumber.setText(buildings.get(position).getNumber());
        FStorageManager.getInstance().downloadImage(activity, holder.img,
                Building.class.getSimpleName() + "/" + buildings.get(position).getNumber());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView img;
        private final TextView txtNumber;
        private final TextView txtName;

        ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img);
            txtNumber = view.findViewById(R.id.txtNumber);
            txtName = view.findViewById(R.id.txtName);
        }

        public void bind(final Building item, final RclBuildingsAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item, activity));
        }
    }
}
