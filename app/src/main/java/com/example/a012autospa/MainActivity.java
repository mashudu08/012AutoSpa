package com.example.a012autospa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.net.ConnectException;

public class MainActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    private FirebaseAuth authProfile;
    TextView register;
    Button login;
    ProgressDialog progressDialog;
    private static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);

        //login user
        login = findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener()
        {
            /*public void onClick(View v){
                startActivity(new Intent(MainActivity.this, Bottom_nav_menu.class));
            }*/
            @Override
            public void onClick(View v)
            {
                String txtEmail = etEmail.getText().toString();
                String txtPass = etPassword.getText().toString();

                if(TextUtils.isEmpty(txtEmail))
                {
                    Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_LONG).show();
                    etEmail.setError("Email is required");
                    etEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches())
                {
                    Toast.makeText(MainActivity.this, "Please re-enter your name", Toast.LENGTH_LONG).show();
                    etEmail.setError("Valid email is required");
                    etEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(txtPass))
                {
                    Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                }
                else if(txtPass.length()<6)
                {
                    Toast.makeText(MainActivity.this, "Please re-enter your password", Toast.LENGTH_LONG).show();
                    etPassword.setError("Password is too weak");
                    etPassword.requestFocus();
                }
                else
                {
                    loginUser(txtEmail, txtPass);
                }

            }

        });

        register = findViewById(R.id.signUp);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });

    }

    private void loginUser(String email, String pass)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login in progress...");
        progressDialog.setTitle("Login");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        authProfile = FirebaseAuth.getInstance();
        authProfile.signInWithEmailAndPassword(email, pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(MainActivity.this, Bottom_nav_menu.class));
                }
                else
                {
                    progressDialog.dismiss();
                    try
                    {
                        throw task.getException();
                    }
                    catch(FirebaseAuthInvalidUserException e)
                    {
                        etEmail.setError("User does noy exist. Please register again");
                        etEmail.requestFocus();
                    }
                    catch(FirebaseAuthInvalidCredentialsException e)
                    {
                        etPassword.setError("Invalid credentials. Kindly, check and re-enter");
                        etPassword.requestFocus();
                    }
                    catch (FirebaseNetworkException e)
                    {
                        Toast.makeText(MainActivity.this, "Login requires an internet connection!", Toast.LENGTH_LONG).show();
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }
}