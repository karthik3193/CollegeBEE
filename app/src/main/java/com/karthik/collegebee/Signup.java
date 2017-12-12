package com.karthik.collegebee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    EditText userEmail, userPassword, userPasswordConfirm;
    Button buttonRegister;
    TextView errorString, userLogin;

    LinearLayout linearLayout;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        userEmail = (EditText) findViewById(R.id.userEmail);
        userPassword = (EditText) findViewById(R.id.userPassword);
        userPasswordConfirm = (EditText) findViewById(R.id.userPasswordConfirm);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        errorString = (TextView) findViewById(R.id.errorString);
        userLogin = (TextView) findViewById(R.id.userLogin);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        buttonRegister.setOnClickListener(this);
        userLogin.setOnClickListener(this);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            registerUserToServer();
        }
        if(v == userLogin){
            startActivity(new Intent(this, Signin.class));
            this.finish();

        }
    }

    private void registerUserToServer() {

        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString();
        String confirmPassword = userPasswordConfirm.getText().toString();

        if(!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()){
            if(password.equals(confirmPassword)){
                firebaseUserRegistration(email, password);

            }else{
                errorString.setVisibility(View.VISIBLE);
                errorString.setText("*Password didn't match");
            }
        }else{
            errorString.setVisibility(View.VISIBLE);
            errorString.setText("*Please enter all the fields.");
        }

    }

    private void firebaseUserRegistration(final String email, String password) {

        progressDialog.setMessage("Please wait....");
        progressDialog.show();

       /* firebaseAuth.fetchProvidersForEmail(email).addOnCompleteListener(this, new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                if(task.isSuccessful()){
                    emailExists = true;
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Email Already Exists", Toast.LENGTH_SHORT).show();
                }else{
                    emailExists = false;

                }

            }
        });*/


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    progressDialog.dismiss();
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){

                        Toast.makeText(getApplicationContext(), "Email already Exists.", Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(getApplicationContext(), "Technical Error : Registration Failed.", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    firebaseUser.getEmail();
                    firebaseUser.sendEmailVerification();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Registration Successfull : Please cofirm mail_id."+firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                    firebaseUser.sendEmailVerification();
                    linearLayout.setVisibility(View.INVISIBLE);

                    finish();
                    startActivity(new Intent(getApplicationContext(), Signin.class));

                }
            }

        });

    }

}
