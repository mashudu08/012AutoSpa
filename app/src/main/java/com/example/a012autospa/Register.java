package com.example.a012autospa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    TextView signIn;
    ImageView back;
    EditText etName, etNumber, etEmail, etPassword;
    private static final String TAG="Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toast.makeText(Register.this,"You can register now", Toast.LENGTH_LONG).show();

        etName = findViewById(R.id.name);
        etNumber = findViewById(R.id.number);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        signIn = findViewById(R.id.Login);
        back = findViewById(R.id.back);

        Button register = findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String txtName = etName.getText().toString();
                String txtNumber = etNumber.getText().toString();
                String txtEmail = etEmail.getText().toString();
                String txtPass = etPassword.getText().toString();

                if(TextUtils.isEmpty(txtName))
                {
                    Toast.makeText(Register.this, "Please enter your name", Toast.LENGTH_LONG).show();
                    etName.setError("Name is required");
                    etName.requestFocus();
                }
                else if(TextUtils.isEmpty(txtNumber))
                {
                    Toast.makeText(Register.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                    etNumber.setError("Mobile number is required");
                    etNumber.requestFocus();
                }
                else if(txtNumber.length() != 10)
                {
                    Toast.makeText(Register.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                    etNumber.setError("Mobile number should be 10 digits");
                    etNumber.requestFocus();
                }
                else if(TextUtils.isEmpty(txtEmail))
                {
                    Toast.makeText(Register.this, "Please enter your name", Toast.LENGTH_LONG).show();
                    etEmail.setError("Email is required");
                    etEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches())
                {
                    Toast.makeText(Register.this, "Please re-enter your name", Toast.LENGTH_LONG).show();
                    etEmail.setError("Valid email is required");
                    etEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(txtPass))
                {
                    Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                }
                else if(txtPass.length()<6)
                {
                    Toast.makeText(Register.this, "Please re-enter your password", Toast.LENGTH_LONG).show();
                    etPassword.setError("Password is too weak");
                    etPassword.requestFocus();
                }
                else
                {
                    registerUser(txtName, txtNumber ,txtEmail, txtPass);
                }

            }
        });

        signIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Register.this, MainActivity.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Register.this, MainActivity.class));
            }
        });

    }

    //register user using credentials
    private void registerUser(String txtName, String txtNumber , String txtEmail, String txtPass)
    {
        //showing process dialog
        ProgressDialog progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("Saving user details...");
        progressDialog.setTitle("Registration");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String imageUrl = "";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(txtEmail, txtPass).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //User data into firebase realtime database
                    String userId = firebaseUser.getUid();
                    FirebaseDatabase db = com.google.firebase.database.FirebaseDatabase.getInstance();
                    DatabaseReference root  = db.getReference().child(userId).child("profile");
                    FirebaseDatabase.getInstance().getReference().keepSynced(true);

                    // Created a collection called booking (From "BookingClass" class) and have the unique user as the child of the collection
                    User userInfo = new User(txtName, txtNumber, txtEmail, txtPass, imageUrl);

                    //creates a unique id for booking under each user
                    //String userID = root.push().getKey();
                    root.setValue(userInfo);
                    progressDialog.dismiss();

                    firebaseUser.sendEmailVerification(); //send verification email

                    //Open main page after successful registration
                    Intent intent = new Intent(Register.this, MainActivity.class); //Prevent user from returning to registration
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();//close register activity
                }
                else
                {
                    progressDialog.setMessage("An error has occurred!");
                    progressDialog.dismiss();
                    try
                    {
                        throw task.getException();
                    }
                    catch(FirebaseAuthWeakPasswordException e)
                    {
                        etPassword.setError("Your password is too weak. Kindly use a mixture of alphabets, numbers and characters");
                        etPassword.requestFocus();
                    }
                    catch(FirebaseAuthInvalidCredentialsException e)
                    {
                        etPassword.setError("Your password is invalid, or it is already in use. Please re-enter");
                        etPassword.requestFocus();
                    }
                    catch(FirebaseAuthUserCollisionException e)
                    {
                        etPassword.setError("User is already registered with email. Use another email");
                        etPassword.requestFocus();
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }
}