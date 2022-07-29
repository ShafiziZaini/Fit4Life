package com.example.fyplatest1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends AppCompatActivity{

    private FirebaseUser user;
    private DatabaseReference reference;

    String userID;

    Profile profile = new Profile();

    private int GALLARY_PIC_REQUEST=200;
    Uri imageUri;
    ImageView imageFromGallary,imageFromDatabase=null,avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avatar=findViewById(R.id.avatar);
        imageFromGallary=findViewById(R.id.new_ava_button);
        imageFromGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        //retrieveData();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        TextView textEmail = (TextView) findViewById(R.id.Email);
        TextView textHeight = (TextView) findViewById(R.id.height);
        TextView textWeight = (TextView) findViewById(R.id.weight);
        TextView textName= (TextView) findViewById(R.id.name);
        TextView textPhone = (TextView) findViewById(R.id.phone);
        textEmail.setVisibility(View.INVISIBLE);
        textName.setVisibility(View.INVISIBLE);
        textPhone.setVisibility(View.INVISIBLE);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile userProfile = snapshot.getValue(Profile.class);
                if (userProfile != null){
                    String email = userProfile.email;
                    String name = userProfile.name;
                    String phone = userProfile.phone;
                    Integer height = userProfile.height;
                    Integer weight = userProfile.weight;

                    textEmail.setText(email);
                    textName.setText(name);
                    textPhone.setText(phone);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });
        Button btnContinue = (Button) findViewById(R.id.continue_button);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfile();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, GALLARY_PIC_REQUEST);
    }

    private void retrieveData() {
        DatabaseReference db=FirebaseDatabase.getInstance().getReference("riddle_game")
                .child("myImages").child("user1").child("newImage");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Picasso.with(MainActivity.this).load(Uri.parse(snapshot.getValue().toString())).into(imageFromDatabase);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLARY_PIC_REQUEST&&resultCode==RESULT_OK){
            imageUri=data.getData();
            Picasso.with(MainActivity.this).load(imageUri).into(avatar);
            setToFireStorage(imageUri);
        }
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
                        Toast.makeText(MainActivity.this, "Successfully added to real time", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProfile(){

        TextView textEmail = (TextView) findViewById(R.id.Email);
        TextView textHeight = (TextView) findViewById(R.id.height);
        TextView textWeight = (TextView) findViewById(R.id.weight);
        TextView textName = (TextView) findViewById(R.id.name);
        TextView textPhone = (TextView) findViewById(R.id.phone);

        profile.setEmail(textEmail.getText().toString());
        profile.setName(textName.getText().toString());
        profile.setPhone(textPhone.getText().toString());
        profile.setHeight(Integer.parseInt(textHeight.getText().toString()));
        profile.setWeight(Integer.parseInt(textWeight.getText().toString()));

        reference.child(userID).setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,
                            "Profile inserted", Toast.LENGTH_LONG).show();
                    //startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
        /*mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Profile profile = new Profile(name, phone, email, height, weight);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Information inserted!", Toast.LENGTH_SHORT).show();

                                    }else {
                                        Toast.makeText(MainActivity.this, "Information not inserted!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(MainActivity.this, "Information not inserted!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

        /*String userName = name.getText().toString().trim();
        int userHeight = Integer.parseInt(height.getText().toString().trim());
        int userWeight = Integer.parseInt(weight.getText().toString().trim());
        if (userName.isEmpty() || userHeight <= 0 || userWeight <= 0) {
            Toast.makeText(this, "The information is not suitable! Try again.", Toast.LENGTH_LONG).show();
            return;
        }

        Profile profile = new Profile(userName,id, password, userHeight, userWeight);
        reff.child(profile.getPhone()).setValue(profile);
        Toast.makeText(this, "data inserted", Toast.LENGTH_LONG).show();
        startActivity(new Intent(MainActivity.this, MainActivity.class));*/
    }


}