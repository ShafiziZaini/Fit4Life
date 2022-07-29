package com.example.fyplatest1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class AbsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WorkoutAdapter workoutAdapter;
    String phonenum,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abs);
        getSupportActionBar().hide();
        Intent intent= getIntent();
        phonenum = intent.getStringExtra("phone");
        pass = intent.getStringExtra("password");
        String phone = phonenum;
        String password = pass;

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //retrieve data from firebase
        FirebaseRecyclerOptions<Workout> options =
                new FirebaseRecyclerOptions.Builder<Workout>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Workout").child("Category").child("1").child("Abs"),Workout.class)
                        .build();

        workoutAdapter = new WorkoutAdapter(options);
        recyclerView.setAdapter(workoutAdapter);

        //bottom navigation
        /*BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.workout);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.record:
                        //startActivity(new Intent(getApplicationContext(),RecordActivity.class));
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.workout:
                        //startActivity(new Intent(getApplicationContext(),RecordActivity.class));
                        intent = new Intent(getApplicationContext(), WorkoutActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        //startActivity(new Intent(getApplicationContext(),RecordActivity.class));
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.me:
                        //startActivity(new Intent(getApplicationContext(),MeActivity.class));
                        intent = new Intent(getApplicationContext(), MeActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });*/
    }
    @Override
    protected void onStart() {
        super.onStart();
        workoutAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        workoutAdapter.stopListening();
    }
}