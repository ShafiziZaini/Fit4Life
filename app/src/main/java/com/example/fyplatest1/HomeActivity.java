package com.example.fyplatest1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    String phonenum,pass;

    TextView no_notif;
    ImageView no_image;

    private FirebaseUser user;
    private DatabaseReference reference;

    boolean isPressed =false;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        no_notif = (TextView) findViewById(R.id.no_notif);
        no_image = (ImageView) findViewById(R.id.empty);
        //no_notif.setVisibility(View.GONE);
        recyclerView = (RecyclerView)findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //retrieve data from firebase
        FirebaseRecyclerOptions<Activities> options =
                new FirebaseRecyclerOptions.Builder<Activities>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Activities").child(userID),Activities.class)
                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mainAdapter = new MainAdapter(options);
        recyclerView.setAdapter(mainAdapter);

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                no_notif.setVisibility(View.INVISIBLE);
                no_image.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                no_notif.setVisibility(View.VISIBLE);
                no_image.setVisibility(View.VISIBLE);
            }
        });


        /*if (mainAdapter.getItemCount() == 0) {
            Log.e("Item count", String.valueOf(mainAdapter.getItemCount()));
            no_notif.setVisibility(View.VISIBLE);
        } else {
            no_notif.setVisibility(View.GONE);
        }*/


        //bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.record:
                        //startActivity(new Intent(getApplicationContext(),RecordActivity.class));
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.workout:
                        //startActivity(new Intent(getApplicationContext(),RecordActivity.class));
                        intent = new Intent(getApplicationContext(), WorkoutActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.me:
                        //startActivity(new Intent(getApplicationContext(),MeActivity.class));
                        intent = new Intent(getApplicationContext(), MeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }


    //icon kat atas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.running_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //new run page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent= getIntent();
        phonenum = intent.getStringExtra("phone");
        pass = intent.getStringExtra("password");
        //getSupportActionBar().hide();
        String phone = phonenum;
        String password = pass;
        switch (item.getItemId()) {
            case R.id.add:
                //Toast.makeText(getContext(), "You can't add a new challenge now.", Toast.LENGTH_LONG).show();
                //startActivityForResult(new Intent(getApplicationContext(), AddRunning.class), 1);
                //startActivityForResult(new Intent(getContext(), NewChallengeActivity.class));
                intent = new Intent(getApplicationContext(), AddRunning.class);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }

    // disable back on login
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

