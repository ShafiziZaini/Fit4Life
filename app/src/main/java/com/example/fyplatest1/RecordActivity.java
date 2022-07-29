package com.example.fyplatest1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RecordActivity extends AppCompatActivity {

    String phonenum,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent intent= getIntent();
        phonenum = intent.getStringExtra("phone");
        pass = intent.getStringExtra("password");
        getSupportActionBar().hide();
        String phone = phonenum;
        String password = pass;

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.record);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.record:
                        return true;
                    case R.id.workout:
                        //startActivity(new Intent(getApplicationContext(),RecordActivity.class));
                        Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);
                        intent.putExtra("phone", phone);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
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
}