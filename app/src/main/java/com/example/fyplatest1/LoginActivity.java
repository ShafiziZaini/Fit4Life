package com.example.fyplatest1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    TextView register,forgotPassword;
    Button submitButton;
    EditText textEmail, textPassword;

    private FirebaseAuth mAuth;

    String userID;

    boolean isPressed =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        submitButton = (Button) findViewById(R.id.btnLogin);
        submitButton.setOnClickListener(this);

        textEmail = (EditText)findViewById(R.id.loginEmail);
        textPassword = (EditText)findViewById(R.id.loginPassword);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btnLogin:
                userLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }

    }

    private void userLogin() {

        String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();

        if (email.isEmpty()){
            textEmail.setError("Email required!");
            textEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textEmail.setError("Please enter valid email!");
            textEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            textPassword.setError("Password required!");
            textPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            textPassword.setError("Min password length should be 6 characters!");
            textPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        /*reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Profile userProfile = snapshot.getValue(Profile.class);

                                if (userProfile != null){

                                    Intent intent;
                                    if (userProfile.weight == 0 || userProfile.height == 0) {
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                    } else {
                                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    }
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(LoginActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
                            }
                        });*/
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, "Check your email to verify your account!", Toast.LENGTH_SHORT).show();
                    }



                }else{
                    Toast.makeText(LoginActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_SHORT).show();
                }
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
