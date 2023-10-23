package com.example.trangko_new_ver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trangko_new_ver.Fragment.Safety;
import com.example.trangko_new_ver.ToSandPP.PrivacyPolicy;
import com.example.trangko_new_ver.ToSandPP.TermsOfService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {


    private EditText Email, Password;
    private TextView Forgotpass,Terms,Policy, signup;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button Login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Login = findViewById(R.id.loginBtn);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Forgotpass = findViewById(R.id.forgotpass);
        Terms = findViewById(R.id.terms_of_service);
        Policy = findViewById(R.id.privacy_policy);
        progressBar = findViewById(R.id.progressb);
        signup = findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckCredentials();
            }
        });

        Forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, ForgotPasswordActivity.class));
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this, RegisterPage.class));
                finish();
            }
        });

        Terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this, TermsOfService.class));
                finish();
            }
        });

        Policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginPage.this, PrivacyPolicy.class));
                finish();
            }
        });

    }

    private void CheckCredentials() {
        //TODO Check Credentials of the input text
        String inputemail = Email.getText().toString();
        String inputpassword = Password.getText().toString();

        if (inputemail.isEmpty()||!inputemail.contains("@")){
            Email.setError("Email is not valid");
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(inputemail).matches()){
            Toast.makeText(LoginPage.this, "PLease re-enter your email", Toast.LENGTH_SHORT).show();
            Email.setError("Email is not valid");
            return;
        }
        else if (inputpassword.isEmpty()||inputpassword.length()<8){
            Password.setError("Password is not valid");
            return;
        }else{
            progressBar.setVisibility(View.VISIBLE);
            //TODO if everified procced to LoginUser
            LoginUser(inputemail, inputpassword);
        }
    }

    private void LoginUser(String inputemail, String inputpassword) {

        auth.signInWithEmailAndPassword(inputemail,inputpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    // TODO Verifieyinh the email input from firebase
                    if (user.isEmailVerified()){
                        Toast.makeText(LoginPage.this, "Please Verify", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                    //TODO procced to main page
                    SendUserToMainPage();

                }else{
                    String exception = task.getException().getMessage();
                    Toast.makeText(LoginPage.this, "Error: " + exception, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void SendUserToMainPage() {
        startActivity(new Intent(getApplicationContext(), MainPage.class));
        finish();
    }
}