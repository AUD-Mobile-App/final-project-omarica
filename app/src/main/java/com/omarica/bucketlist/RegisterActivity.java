package com.omarica.bucketlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    // Defining the fields
    private FirebaseAuth mAuth;
    EditText emailEditText,nameEditText,passwordEditText;
    Button registerButton;
    private String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user ;

        // Initializing the fields

        registerButton = findViewById(R.id.registerButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);


        //Registering a new user
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidInfo()) {
                    mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    task.getResult().getUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(
                                            nameEditText.getText().toString()
                                    ).build());

                                    loginUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
                                }
                            });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Please enter valid user information", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean isValidInfo() {
        return !emailEditText.getText().toString().equals("")
                && !nameEditText.getText().toString().equals("")
                && !passwordEditText.getText().toString().equals("");
    }

    private void loginUser(String email, String password) {

        Log.d(TAG, "signIn:" + email);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

               // loginButton.setVisibility(View.VISIBLE);
              //  mProgressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");

                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent intent = new Intent(RegisterActivity.this, ListActivity.class);
                    intent.putExtra("User", user.getUid());
                    startActivity(intent);

                    //updateUI(user);
                } else {

                   /* loginButton.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE); */
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }

                // [START_EXCLUDE]
                if (!task.isSuccessful()) {
                    //mStatusTextView.setText(R.string.auth_failed);
                }
            }
        });
    }
}