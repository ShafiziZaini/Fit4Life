package com.example.fyplatest1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;

public class AddRunningAuto extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    String userID;

    EditText title, distanceText, timeText;
    NumberPicker day, month,year;
    RangeSlider slider;
    Button add;
    Activities activities = new Activities();
    String phonenum,pass,time,pace,distance;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_running);
        getSupportActionBar().hide();
        Intent intent= getIntent();
        phonenum = intent.getStringExtra("phone");
        pass = intent.getStringExtra("password");
        time = intent.getStringExtra("time");
        pace = intent.getStringExtra("pace");
        distance = intent.getStringExtra("distance");

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Activities");
        userID = user.getUid();

        title = (EditText) findViewById(R.id.challenge_title);
        distanceText = (EditText) findViewById(R.id.challenge_distance);
        distanceText.setEnabled(false);
        timeText = (EditText) findViewById(R.id.challenge_time);
        timeText.setEnabled(false);
        day =  (NumberPicker) findViewById(R.id.day);
        day.setEnabled(false);
        month =  (NumberPicker) findViewById(R.id.month);
        month.setEnabled(false);
        year =  (NumberPicker) findViewById(R.id.year);
        year.setEnabled(false);
        add =  (Button) findViewById(R.id.add_button);

        distanceText.setText(distance);
        timeText.setText(time);

        //initToolbar();
        initDeadlinePicker();

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addProfile();
            }});
    }
    /*private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog warning = new AlertDialog.Builder(AddRunning.this).create();
                warning.setTitle("Are you sure you want to go back?");
                warning.setMessage("The challenge you created has not been saved. Once you go back, the information is deleted.");
                warning.setButton(AlertDialog.BUTTON_POSITIVE, "Go back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Go back to previous activity
                        setResult(RESULT_CANCELED);
                        onBackPressed();
                        dialog.dismiss();
                    }
                });
                warning.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Stay here
                        dialog.dismiss();
                    }
                });
                warning.show();
            }
        });
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initDeadlinePicker() {
        final NumberPicker day = findViewById(R.id.day);
        final NumberPicker month = findViewById(R.id.month);
        final NumberPicker year = findViewById(R.id.year);

        LocalDate today = LocalDate.now();

        day.setMinValue(1);
        day.setMaxValue(MyDate.getDaysOfMonth(today.getMonthValue(), today.getYear()));
        day.setValue(today.getDayOfMonth());

        month.setMinValue(1);
        month.setMaxValue(12);
        month.setValue(today.getMonthValue());
        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                day.setMaxValue(MyDate.getDaysOfMonth(newVal, year.getValue()));
                if (day.getValue() > day.getMaxValue())
                    day.setValue(day.getMaxValue());
            }
        });

        year.setMinValue(today.getYear());
        year.setMaxValue(today.getYear() + 5);
        year.setValue(today.getYear());
    }

    private void addProfile(){
        //final NumberPicker day = findViewById(R.id.day);
        //final NumberPicker month = findViewById(R.id.month);
        //final NumberPicker year = findViewById(R.id.year);

        String phone = phonenum;
        String password = pass;
        //Double pace, modulus;
        //String totalpace;

        //calculate pace
        //modulus = pace % 60;
        //new DecimalFormat("#").format(pace);
        //int i = Integer.valueOf(pace.intValue());
        //int j = Integer.valueOf(modulus.intValue());
        //totalpace =  pace;

        //insert into db
        activities.setTitle(title.getText().toString());
        //activities.setDistance(distance.getText().toString());
        //activities.setTime(time.getText().toString());
        activities.setDistance(Double.parseDouble(distanceText.getText().toString().trim()));
        activities.setTime(timeText.getText().toString().trim());
        activities.setPace(pace);
        activities.setDay(day.getValue());
        activities.setMonth(month.getValue());
        activities.setYear(year.getValue());

        String unique = reference.push().getKey();

        if (!title.getText().toString().isEmpty()) {
            reference.child(userID).child(unique).setValue(activities).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AddRunningAuto.this,
                                "Running inserted", Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        Intent intent = new Intent(AddRunningAuto.this, HomeActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(AddRunningAuto.this,
                                "Running not inserted", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Toast.makeText(AddRunningAuto.this,
                    "Insert the title!", Toast.LENGTH_LONG).show();
        }

    }
}
