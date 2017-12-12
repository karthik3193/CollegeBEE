package com.karthik.collegebee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Signin extends AppCompatActivity implements View.OnClickListener {

    EditText userEmail, userPassword, userEmailForgotPassword;
    TextView userRegistration, errorString, userForgotPassword, errorStringForgotPassword, userLogin;
    Button buttonLogin, buttonReset;
    String mail;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    LinearLayout linearLayoutLogin, linearLayoutForgotPassword;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        progressDialog = new ProgressDialog(Signin.this);



        initializeTheViewElements();
        setOnClickListenersToElements();
    }

    private void setOnClickListenersToElements() {

        buttonLogin.setOnClickListener(this);
        userRegistration.setOnClickListener(this);
        userForgotPassword.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
        userLogin.setOnClickListener(this);

    }

    private void initializeTheViewElements() {

        userEmail = (EditText) findViewById(R.id.userEmail);
        userPassword = (EditText) findViewById(R.id.userPassword);
        userRegistration = (TextView) findViewById(R.id.userRegistration);
        errorString = (TextView) findViewById(R.id.errorString);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        userForgotPassword = (TextView) findViewById(R.id.userForgotPassword);
        linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayoutLogin);
        linearLayoutForgotPassword = (LinearLayout) findViewById(R.id.linearLayoutForgotPassword);
        userEmailForgotPassword = (EditText) findViewById(R.id.userEmailForgotPassword);
        buttonReset = (Button) findViewById(R.id.buttonReset);
        userLogin = (TextView) findViewById(R.id.userLogin);
        errorStringForgotPassword = (TextView) findViewById(R.id.errorStringForgotPassword);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onClick(View v) {

        if(v == buttonLogin){
            //Start the authentiaction process.
            authenticateUser();
        }
        if(v == userRegistration){
            launchRegisterActivity();
        }

        if(v == userForgotPassword){
            linearLayoutLogin.setVisibility(View.INVISIBLE);
            linearLayoutForgotPassword.setVisibility(View.VISIBLE);
        }

        if(v == buttonReset){
            resetPassword();
        }

        if(v == userLogin){
            linearLayoutLogin.setVisibility(View.VISIBLE);
            linearLayoutForgotPassword.setVisibility(View.INVISIBLE);
        }
    }

    private void resetPassword() {

        String email = userEmailForgotPassword.getText().toString().trim();
        if(!email.isEmpty() && email != null){
            firebaseResetPassword(email);
        }else{
            errorStringForgotPassword.setVisibility(View.VISIBLE);
            errorStringForgotPassword.setText("*Please enter email address");
        }

    }

    private void firebaseResetPassword(String email) {

        progressDialog.setMessage("Please wait....");
        progressDialog.show();


        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Mail sent: please reset your password.", Toast.LENGTH_SHORT).show();
                }else if(task.getException() instanceof FirebaseAuthInvalidUserException){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Email Doesn't exists.", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Could not reset. Please try Again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void launchRegisterActivity() {

        finish();
        startActivity(new Intent(this, Signup.class));

    }


    private void authenticateUser() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty() && email != null && password != null){

            firebaseLogin(email, password);


        }else{
            errorString.setVisibility(View.VISIBLE);
            errorString.setText("*Please enter all the fields.");
        }
    }

    private void firebaseLogin(final String email, String password) {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //task after the login

                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    Log.w("firebaseLoginFail", "signInWithEmail:failed", task.getException());
                    Toast.makeText(getApplicationContext(), "Login Failed : Email or the password is invalid", Toast.LENGTH_SHORT).show();
                }else{
                    //launch the post login activity.
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    progressDialog.dismiss();
                    if(!firebaseUser.isEmailVerified()){

                        Toast.makeText(Signin.this, "Please confirm your mail id. "+firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                        firebaseUser.sendEmailVerification();
                        return;
                    }

                    Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent=(Intent) new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("mail",firebaseUser.getEmail());
                    startActivity(intent);
                }
            }
        });

    }
}
