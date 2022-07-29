package com.example.fyplatest1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class WorkoutAdapter extends FirebaseRecyclerAdapter<Workout,WorkoutAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public WorkoutAdapter(@NonNull FirebaseRecyclerOptions<Workout> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Workout Workout) {

        holder.title.setText(Workout.getTitle());

                holder.title.setOnClickListener(new View.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        AppCompatActivity activity = (AppCompatActivity)view.getContext();
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.wrapper,new descfragment(Workout.getTitle(),
                                Workout.getDescription(),Workout.getGif(), Workout.getStep()))
                                .addToBackStack(null).commit();
                    }
                });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_categories,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView title, description,act1, act2, act3;
        ImageView gif;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            title=(TextView)itemView.findViewById(R.id.titleID);
            //description =(TextView)itemView.findViewById(R.id.descriptionID);
            //gif =(ImageView)itemView.findViewById(R.id.gifID);
            //act1 =(TextView)itemView.findViewById(R.id.act1ID);
            //act2 =(TextView)itemView.findViewById(R.id.act2ID);
            //act3 =(TextView)itemView.findViewById(R.id.act3ID);

        }
    }
}
