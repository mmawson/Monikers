package com.example.monikers;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.SignInButton;

import java.util.Calendar;
import java.util.Locale;

public class SignupLogin extends AppCompatActivity {

    private EditText email, password, displayname, birthday;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Button signupBtn;
    private GoogleSignInClient mGoogleSignInClient;
    SignInButton googleBtn;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_login);
        email=findViewById(R.id.emailText);
        password=findViewById(R.id.passwordText);
        displayname=findViewById(R.id.displayNameText);
        birthday=findViewById(R.id.birthdayText);
        signupBtn=findViewById(R.id.singupBtn);
        googleBtn=findViewById(R.id.googleBtn);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        updateUI();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            currentUser = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignupLogin.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            // updateUI();
                        }

                        // ...
                    }
                });
    }
    public void Signup(View view) {
        //https://firebase.google.com/docs/database/admin/save-data

        if(email.getText().toString().equals("")|| password.getText().toString().equals("")
                || displayname.getText().toString().equals("") || birthday.getText().toString().equals("")){
            Toast.makeText(this, "Please provide all information", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        currentUser=authResult.getUser();
                        saveUserDataToDB();
                        updateUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void Login(View view) {
        if(email.getText().toString().equals("")|| password.getText().toString().equals("")){
            Toast.makeText(this, "Please provide all information", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        currentUser=authResult.getUser();
                        Toast.makeText(SignupLogin.this, "Login Succesful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupLogin.this, HomeActivityWithNavDrawer.class));
                        finish();
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void saveUserDataToDB(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        usersRef.child(currentUser.getUid()).setValue(new User(displayname.getText().toString(),
                email.getText().toString(), birthday.getText().toString()));

    }
    private void updateUI(){
        if(currentUser!=null){
            findViewById(R.id.displayNameLayout).setVisibility(View.GONE);
            findViewById(R.id.birthdayLayout).setVisibility(View.GONE);
            signupBtn.setVisibility(View.GONE);
            googleBtn.setVisibility(View.VISIBLE);
        }
    }
    public void restoreUI(View view){
        findViewById(R.id.displayNameLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.birthdayLayout).setVisibility(View.VISIBLE);
        signupBtn.setVisibility(View.VISIBLE);
        googleBtn.setVisibility(View.GONE);
    }
    public void ResetPassword(View view) {
        Toast.makeText(this, "You clicked ResetPassword", Toast.LENGTH_SHORT).show();

    }
    public void sendEmailVerification(View view) {
        Toast.makeText(this, "You clicked sendEmailVerification", Toast.LENGTH_SHORT).show();
    }
    public void showDatePickerDialog(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Set the selected date to the birthday text field
                        String birthday = String.format(Locale.getDefault(), "%02d/%02d/%d", month + 1, day, year);
                        EditText birthdayText = findViewById(R.id.birthdayText);
                        birthdayText.setText(birthday);
                    }
                },
                year, month, day
        );
        // Set the maximum date to today's date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}
