package com.example.trangko_new_ver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText Email;
    FirebaseAuth auth;
    Button resetbutton, back;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Email = (EditText) findViewById(R.id.email);
        resetbutton = (Button) findViewById(R.id.resetpassowrd);
        progressBar = (ProgressBar)  findViewById(R.id.progressb);
        back = (Button) findViewById(R.id.back);
        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginPage.class));
                finish();
            }
        });

        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetpassword();

            }


        });
    }

    private void resetpassword() {
        String email = Email.getText().toString().trim();

        if (email.isEmpty()){
            Email.setError("Email is required");
            Email.requestFocus();
            return;

        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Email.setError("Please provide valid Email!");
            Email.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,"Check your Email to reset your password!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(ForgotPasswordActivity.this,"Try again! Something wrong happened!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
}