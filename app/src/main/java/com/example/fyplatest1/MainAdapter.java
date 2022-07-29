package com.example.fyplatest1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MainAdapter extends FirebaseRecyclerAdapter<Activities,MainAdapter.myViewHolder> {

    private View emptyView;
    private RecyclerView recyclerView;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public MainAdapter(@NonNull FirebaseRecyclerOptions<Activities> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Activities Activities) {

        holder.title.setText(Activities.getTitle());
        holder.distance.setText(String.valueOf(Activities.getDistance()) + "km");
        holder.time.setText(String.valueOf(Activities.getTime()) + " min");
        holder.pace.setText(String.valueOf(Activities.getPace()) + "/km");
        holder.day.setText(String.valueOf(Activities.getDay()) + "/");
        holder.month.setText(String.valueOf(Activities.getMonth()) + "/");
        holder.year.setText(String.valueOf(Activities.getYear()));
        checkIfEmpty();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.running_activity_items_layout,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView title,distance, time, pace, day, month, year;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.titleID);
            distance =(TextView)itemView.findViewById(R.id.distanceID);
            time =(TextView)itemView.findViewById(R.id.timeID);
            pace =(TextView)itemView.findViewById(R.id.paceID);
            day =(TextView)itemView.findViewById(R.id.day);
            month =(TextView)itemView.findViewById(R.id.month);
            year =(TextView)itemView.findViewById(R.id.year);
        }
    }
    private void checkIfEmpty() {
        if (emptyView != null && recyclerView.getAdapter() != null) {
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }

    public void onChanged() { checkIfEmpty(); }
    public void onItemRangeInserted(int positionStart, int itemCount) { checkIfEmpty(); }
    public void onItemRangeRemoved(int positionStart, int itemCount) { checkIfEmpty(); }
}
