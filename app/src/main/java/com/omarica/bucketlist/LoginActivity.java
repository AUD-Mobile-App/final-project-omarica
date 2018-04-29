package com.omarica.bucketlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    // Defining the Fields
    private static final String TAG = "EMAIL_PW";
    private EditText mEmailField;
    private EditText mPasswordField;
    private FirebaseAuth mAuth;
    private Button loginButton,registerButton;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Initializing the fields
        setContentView(R.layout.activity_login);
        mEmailField = findViewById(R.id.emailEditText);
        mPasswordField = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        mProgressBar =  (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);


        // Handling register
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //Handling login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginButton.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);

                // Validating input and updating UI
                if(!mEmailField.getText().toString().equals("") && !mPasswordField.getText().toString().equals("") ) {
                    loginUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
                }
                else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                    loginButton.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    private void loginUser(String email, String password) {


        // Logging in using Firebase Authentication
        Log.d(TAG, "signIn:" + email);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                loginButton.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");

                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                    intent.putExtra("User", user.getUid());
                    startActivity(intent);

                    //updateUI(user);
                } else {

                   /* loginButton.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE); */
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Login Failed. Please check you email and password",
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


    @Override
    protected void onStart() {
        super.onStart();

        // Check if a user is currently signed in, if so, go to List Activity
        if(mAuth.getCurrentUser() != null ){
            Intent intent = new Intent(LoginActivity.this, ListActivity.class);
            startActivity(intent);
        }
    }
}
