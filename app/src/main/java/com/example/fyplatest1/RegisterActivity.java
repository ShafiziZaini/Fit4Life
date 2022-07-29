package com.example.fyplatest1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{


    private FirebaseAuth mAuth;

    TextView already;
    Button submitButton;
    EditText textname, textphone,textemail, textpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        submitButton = (Button)findViewById(R.id.btnRegister);
        submitButton.setOnClickListener(this);

        already = (TextView)findViewById(R.id.alreadyhaveacc);
        already.setOnClickListener(this);


        textname = (EditText)findViewById(R.id.inputName);
        textphone = (EditText)findViewById(R.id.inputPhone);
        textemail = (EditText)findViewById(R.id.inputEmail);
        textpassword = (EditText)findViewById(R.id.inputPassword);




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alreadyhaveacc:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btnRegister:
                registerUser();
                break;
        }

    }

    private void registerUser() {

        String email = textemail.getText().toString().trim();
        String name = textname.getText().toString().trim();
        String phone = textphone.getText().toString().trim();
        String password = textpassword.getText().toString().trim();
        Integer height = 0;
        Integer weight = 0;

        if (name.isEmpty()){
            textname.setError("Name is required!");
            textname.requestFocus();
            return;
        }
        if (phone.isEmpty()){
            textphone.setError("Phone is required!");
            textphone.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phone).matches()){
            textphone.setError("Please provide valid phone number!");
            textphone.requestFocus();
            return;
        }
        if (email.isEmpty()){
            textemail.setError("Email is required!");
            textemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textemail.setError("Please provide valid email!");
            textemail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            textpassword.setError("Password is required!");
            textpassword.requestFocus();
            return;
        }

        if (password.length() < 6){
            textpassword.setError("Min password length should be 6 characters!");
            textpassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
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
                                        Toast.makeText(RegisterActivity.this, "Register success!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(RegisterActivity.this, "Register failed!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterActivity.this, "Register failed!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /*EditText name, phone, password, cpassword;
    Button submitButton;
    TextView already;
    DatabaseReference reff;
    Profile profile = new Profile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);

        reff = FirebaseDatabase.getInstance().getReference().child("Profile");

        name = (EditText)findViewById(R.id.inputEmail);
        phone = (EditText)findViewById(R.id.inputPhone);
        password = (EditText)findViewById(R.id.inputPassword);
        cpassword = (EditText)findViewById(R.id.inputCPassword);
        submitButton = (Button)findViewById(R.id.btnRegister);
        already = (TextView)findViewById(R.id.alreadyhaveacc);

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                registerProfile();
            }});

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
    private void registerProfile(){
        Integer height = 0;
        Integer weight = 0;
        String pass=password.getText().toString();
        String cpass=cpassword.getText().toString();
        profile.setName (name.getText().toString ());
        profile.setPhone(phone.getText().toString());
        profile.setPassword(password.getText().toString());
        profile.setHeight(height);
        profile.setWeight(weight);

        if(pass.equals(cpass)) {
            reff.child(profile.getPhone()).setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Register success", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Register failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Toast.makeText(RegisterActivity.this,"Password Not matching",Toast.LENGTH_SHORT).show();
        }
    }*/
}

