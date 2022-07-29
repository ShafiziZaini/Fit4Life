package com.example.fyplatest1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MeActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    String userID;

    Button logout;
    Profile profile = new Profile();

    private int GALLARY_PIC_REQUEST=200;
    Uri imageUri;
    ImageView imageFromGallary,imageFromDatabase=null,avatar;

    boolean isPressed =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        getSupportActionBar().hide();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        avatar=findViewById(R.id.avatar);
        imageFromGallary=findViewById(R.id.new_ava_button);
        imageFromGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        retrieveData(userID);

        logout = (Button) findViewById(R.id.signOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MeActivity.this);
                builder1.setMessage("Are you sure want to logout?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(MeActivity.this, LoginActivity.class));
                                Toast.makeText(MeActivity.this, "Logout success", Toast.LENGTH_SHORT).show();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });




        final TextView textEmail = (TextView) findViewById(R.id.email);
        final TextView textName = (TextView) findViewById(R.id.name);
        final TextView textPhone= (TextView) findViewById(R.id.phone);
        final TextView textHeight = (TextView) findViewById(R.id.height);
        final TextView textWeight = (TextView) findViewById(R.id.weight);
        final ImageView imageEmail = (ImageView) findViewById(R.id.edit_email);
        final ImageView imagePhone = (ImageView) findViewById(R.id.edit_phone);
        imageEmail.setVisibility(View.INVISIBLE);
        imagePhone.setVisibility(View.INVISIBLE);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile userProfile = snapshot.getValue(Profile.class);

                if (userProfile != null){
                    String name = userProfile.name;
                    String email = userProfile.email;
                    String phone = userProfile.phone;
                    Integer height = userProfile.height;
                    Integer weight = userProfile.weight;

                    textName.setText(name);
                    textEmail.setText(email);
                    textPhone.setText(phone);
                    textHeight.setText(" " +height+" cm");
                    textWeight.setText(" " +weight+" kg");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MeActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });
        editButtons(userID);
        renderButtons();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.me);

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
                        //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.me:
                        return true;
                }
                return false;
            }
        });
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, GALLARY_PIC_REQUEST);
    }
    private void retrieveData(String id) {
        DatabaseReference db=FirebaseDatabase.getInstance().getReference("Image")
                .child(id).child("newImage");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Picasso.with(MeActivity.this).load(Uri.parse(snapshot.getValue().toString())).into(avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setToFireStorage(Uri imageUri) {

        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("ImageFolder");
        final StorageReference ImageReference=storageReference.child("112233");
        ImageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference db= FirebaseDatabase.getInstance().getReference("Image").child(userID);
                        db.child("newImage").setValue(uri.toString());
                        Toast.makeText(MeActivity.this, "Profile image updated!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLARY_PIC_REQUEST&&resultCode==RESULT_OK){
            imageUri=data.getData();
            Picasso.with(MeActivity.this).load(imageUri).into(avatar);
            setToFireStorage(imageUri);
        }
    }

    private void renderButtons() {
        FloatingActionButton reminderButton = findViewById(R.id.reminder);
        reminderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MeActivity.this);
                dialog.setContentView(R.layout.reminder_dialog);
                dialog.setCancelable(false);

                final EditText message = dialog.findViewById(R.id.message);
                final TimePicker timePicker = dialog.findViewById(R.id.time_picker);
                Button cancelButton = dialog.findViewById(R.id.cancel_button);
                Button okButton = dialog.findViewById(R.id.ok_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {

                        int minute = timePicker.getMinute();
                        int hour = timePicker.getHour();
                        String myMessage = message.getText().toString();
                        if (myMessage.trim().isEmpty())
                            myMessage = "My Workout";

                        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                                .putExtra(AlarmClock.EXTRA_MESSAGE, myMessage)
                                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                                .putExtra(AlarmClock.EXTRA_MINUTES, minute);
                        startActivity(intent);
                        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
    private void editButtons(String id) {
        ImageView editHeight = (ImageView) findViewById(R.id.edit_height);
        ImageView editWeight = (ImageView) findViewById(R.id.edit_weight);
        TextView email = (TextView) findViewById(R.id.email);
        TextView phone = (TextView) findViewById(R.id.phone);

        editHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MeActivity.this);
                dialog.setContentView(R.layout.edit_profile);
                dialog.setCancelable(false);

                final EditText namemessage = dialog.findViewById(R.id.namemessage);
                final EditText heightmessage = dialog.findViewById(R.id.heightmessage);
                final EditText weightmessage = dialog.findViewById(R.id.weightmessage);
                final EditText phonemessage = dialog.findViewById(R.id.phonemessage);
                final EditText emailmessage = dialog.findViewById(R.id.emailmessage);
                final TextView phonetext = dialog.findViewById(R.id.phonetext);
                final TextView emailtext = dialog.findViewById(R.id.emailtext);
                emailmessage.setVisibility(View.INVISIBLE);
                phonemessage.setVisibility(View.INVISIBLE);
                phonetext.setVisibility(View.INVISIBLE);
                emailtext.setVisibility(View.INVISIBLE);
                Button cancelButton = dialog.findViewById(R.id.cancel_button);
                Button okButton = dialog.findViewById(R.id.ok_button);

                reference = FirebaseDatabase.getInstance().getReference("Users").child(id);

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String height = snapshot.child("height").getValue().toString();
                        String weight = snapshot.child("weight").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        heightmessage.setText(height);
                        weightmessage.setText(weight);
                        namemessage.setText(name);
                        phonemessage.setText(phone);
                        emailmessage.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!weightmessage.getText().toString().isEmpty() && !heightmessage.getText()
                                .toString().isEmpty() && !namemessage.getText().toString().isEmpty()) {

                            profile.setName(namemessage.getText().toString());
                            profile.setPhone(phonemessage.getText().toString());
                            profile.setEmail(emailmessage.getText().toString());
                            profile.setHeight(Integer.parseInt(heightmessage.getText().toString()));
                            profile.setWeight(Integer.parseInt(weightmessage.getText().toString()));

                            reference.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(MeActivity.this,
                                                "Profile updated", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        Intent intent = new Intent(MeActivity.this, MeActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(MeActivity.this,
                                                "Profile not inserted", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(MeActivity.this,
                                    "Insert the information!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });
        editWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MeActivity.this);
                dialog.setContentView(R.layout.edit_profile);
                dialog.setCancelable(false);

                final EditText namemessage = dialog.findViewById(R.id.namemessage);
                final EditText heightmessage = dialog.findViewById(R.id.heightmessage);
                final EditText weightmessage = dialog.findViewById(R.id.weightmessage);
                final EditText phonemessage = dialog.findViewById(R.id.phonemessage);
                final EditText emailmessage = dialog.findViewById(R.id.emailmessage);
                final TextView phonetext = dialog.findViewById(R.id.phonetext);
                final TextView emailtext = dialog.findViewById(R.id.emailtext);
                emailmessage.setVisibility(View.INVISIBLE);
                phonemessage.setVisibility(View.INVISIBLE);
                phonetext.setVisibility(View.INVISIBLE);
                emailtext.setVisibility(View.INVISIBLE);
                Button cancelButton = dialog.findViewById(R.id.cancel_button);
                Button okButton = dialog.findViewById(R.id.ok_button);

                reference = FirebaseDatabase.getInstance().getReference("Users").child(id);

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String height = snapshot.child("height").getValue().toString();
                        String weight = snapshot.child("weight").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        heightmessage.setText(height);
                        weightmessage.setText(weight);
                        namemessage.setText(name);
                        phonemessage.setText(phone);
                        emailmessage.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!weightmessage.getText().toString().isEmpty() && !heightmessage.getText().toString().isEmpty() && !namemessage.getText().toString().isEmpty()) {

                            profile.setName(namemessage.getText().toString());
                            profile.setPhone(phonemessage.getText().toString());
                            profile.setEmail(emailmessage.getText().toString());
                            profile.setHeight(Integer.parseInt(heightmessage.getText().toString()));
                            profile.setWeight(Integer.parseInt(weightmessage.getText().toString()));

                            reference.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {
                                    if (task2.isSuccessful()) {
                                        Toast.makeText(MeActivity.this,
                                                "Profile updated", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MeActivity.this, MeActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(MeActivity.this,
                                                "Profile not inserted", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(MeActivity.this,
                                    "Insert the information!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

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
