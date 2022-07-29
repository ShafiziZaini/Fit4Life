package com.example.fyplatest1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WorkoutActivity extends AppCompatActivity {

    //RecyclerView recyclerView;
    //WorkoutAdapter workoutAdapter;
    String phonenum,pass;
    TextView abs, back, triceps, leg;

    boolean isPressed =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        getSupportActionBar().hide();
        Intent intent= getIntent();
        phonenum = intent.getStringExtra("phone");
        pass = intent.getStringExtra("password");
        String phone = phonenum;
        String password = pass;

        abs = (TextView) findViewById(R.id.AbsID);
        back = (TextView) findViewById(R.id.BackID);
        triceps = (TextView) findViewById(R.id.TricepsID);
        leg = (TextView) findViewById(R.id.LegID);

        abs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AbsActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BackActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });
        triceps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TricepsActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });
        leg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LegActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

        //recyclerView = (RecyclerView)findViewById(R.id.rv);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //retrieve data from firebase
        /*FirebaseRecyclerOptions<Workout> options =
                new FirebaseRecyclerOptions.Builder<Workout>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Workout").child("Category"),Workout.class)
                        .build();

        workoutAdapter = new WorkoutAdapter(options);
        recyclerView.setAdapter(workoutAdapter);*/


        //bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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
        });
    }
    /*@Override
    protected void onStart() {
        super.onStart();
        workoutAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        workoutAdapter.stopListening();
    }*/

    @Override
    public void onBackPressed() {

        //startActivity(new Intent(this, HomeActivity.class));
        //finish();
        if (isPressed){
            finishAffinity();
            System.exit(0);
        }else{
            Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

            isPressed = true;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isPressed = false;
            }
        };

        new Handler().postDelayed(runnable, 2000);
    }
}