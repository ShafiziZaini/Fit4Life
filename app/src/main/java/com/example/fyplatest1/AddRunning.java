package com.example.fyplatest1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class AddRunning extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    String userID;

    EditText title, distance, time;
    NumberPicker day, month,year;
    RangeSlider slider;
    Button add;
    Activities activities = new Activities();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_running);
        getSupportActionBar().hide();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Activities");
        userID = user.getUid();

        title = (EditText) findViewById(R.id.challenge_title);
        distance = (EditText) findViewById(R.id.challenge_distance);
        time = (EditText) findViewById(R.id.challenge_time);
        day =  (NumberPicker) findViewById(R.id.day);
        month =  (NumberPicker) findViewById(R.id.month);
        year =  (NumberPicker) findViewById(R.id.year);
        add =  (Button) findViewById(R.id.add_button);

        //initToolbar();
        initTargetEditText();
        initSlider();
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

    private void initTargetEditText() {
        distance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Change slider to reflect the inputed text
                if (s.toString().isEmpty()) {
                    distance.setText("0");
                    return;
                }
                int inputTarget = Integer.parseInt(s.toString());
                int minTarget = (int) slider.getValueFrom();
                int maxTarget = (int) slider.getValueTo();

                if (inputTarget > maxTarget) {
                    slider.setValues((float) maxTarget);
                } else if (inputTarget < minTarget) {
                    slider.setValues((float) minTarget);
                } else {
                    int base = (int) Math.floor(inputTarget / 5);
                    slider.setValues((float) base * 5);
                }
            }
        });
    }
    private void initSlider() {
        slider = findViewById(R.id.slider);

        // Default slider
        setSlider(Challenge.RUNNING_DISTANCE_CHALLENGE, 5);

        slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                if (fromUser)
                    distance.setText(Integer.toString((int) value));
            }
        });
    }
    private void setSlider(int type, int value) {
        slider.setValueFrom(5);
        slider.setValueTo(100);
        slider.setStepSize(0);
        // Default value set
        if (value == -1)
            value = 5;
        int sliderValue = Math.max(
                (int)slider.getValueFrom(),
                Math.min((int)slider.getValueTo(), value)
        );
        slider.setValues((float)sliderValue);
        distance.setText(Integer.toString(value));
    }


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

        Double pace;
        String totalpace;
        long modulus;

        //calculate pace
        pace = (Double.parseDouble(time.getText().toString().trim())*60) / Double.parseDouble(distance.getText().toString().trim());
        modulus = (long) (pace % 60);
        //new DecimalFormat("#").format(pace);
        int i = Integer.valueOf(pace.intValue());
        String padded = String.format("%02d" , modulus);
        totalpace =  i/60 + ":" +padded;

        //insert into db
        activities.setTitle(title.getText().toString());
        //activities.setDistance(distance.getText().toString());
        //activities.setTime(time.getText().toString());
        activities.setDistance(Double.parseDouble(distance.getText().toString().trim()));
        activities.setTime(time.getText().toString().trim() + ":00");
        activities.setPace(totalpace);
        activities.setDay(day.getValue());
        activities.setMonth(month.getValue());
        activities.setYear(year.getValue());

        String unique = reference.push().getKey();
        if (!title.getText().toString().isEmpty()) {
            reference.child(userID).child(unique).setValue(activities).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddRunning.this,
                                "Running inserted", Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        Intent intent = new Intent(AddRunning.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AddRunning.this,
                                "Running not inserted", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Toast.makeText(AddRunning.this,
                    "Insert the title!", Toast.LENGTH_LONG).show();
        }
    }
}