package com.example.trangko_new_ver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trangko_new_ver.ToSandPP.PrivacyPolicy;
import com.example.trangko_new_ver.ToSandPP.TermsOfService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {


    private EditText Username, Password, Email, Confirmpassword;
    private TextView Login;
    private CheckBox checkbox;
    private Button Signup;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    private Context context;
    private FirebaseAuth auth;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        FirebaseApp.initializeApp(this);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.pass);
        Confirmpassword = findViewById(R.id.confpass);
        Email = findViewById(R.id.email);
        checkbox =findViewById(R.id.checkbox);
        Login =findViewById(R.id.login);
        Signup =findViewById(R.id.signup);
        context = new ContextThemeWrapper(RegisterPage.this, R.style.AppTheme2);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        Signup.setEnabled(false);
        progressBar = findViewById(R.id.pbar);
        auth = FirebaseAuth.getInstance();


        //TODO Checkbox for terms and privacy
        String terms = " By signing up you are agreeing to our\n Terms of Service and Privacy Policy ";
        TextView tp = new TextView(this);


        //TODO spannable String to click String
        SpannableString ss = new SpannableString(terms);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterPage.this, TermsOfService.class));
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterPage.this, PrivacyPolicy.class));
            }
        };
        ss.setSpan(clickableSpan1,40, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2,61, 75, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tp.setText(ss);
        tp.setMovementMethod(LinkMovementMethod.getInstance());

        checkbox.setMovementMethod(LinkMovementMethod.getInstance());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("RestrictedApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    materialAlertDialogBuilder.setTitle("Terms of Service and Privacy Policy");

                    materialAlertDialogBuilder.setView(tp,70,2,50,2);

                    materialAlertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Signup.setEnabled(true);
                            dialog.dismiss();
                        }
                    });
                    materialAlertDialogBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkbox.setChecked(false);
                        }
                    });
                    materialAlertDialogBuilder.show();

                }else{
                    Signup.setEnabled(false);
                }
            }
        });

        //TODO SIGNUP User

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
                login();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterPage.this, LoginPage.class));
                finish();
            }

        });


    }

    private void checkCredentials() {
        //TODO Checking If the Input Is Correct
        String inputusername = Username.getText().toString();
        String inputemail = Email.getText().toString();
        String inputpassword = Password.getText().toString();
        String inputconfpass = Confirmpassword.getText().toString();


//        System.out.println("UserInput"+inputemail+" " + inputusername);

        if (inputusername.isEmpty()||inputusername.length()<7){
            Username.setError("Please input a Username");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        else if (inputemail.isEmpty()||!inputemail.contains("@")){
            Email.setError("Please input n Email");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        else if (inputpassword.isEmpty()||inputpassword.length()<8){
            Password.setError("Please input a Password");
            Password.clearComposingText();
            Confirmpassword.clearComposingText();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        else if (inputconfpass.isEmpty()||inputconfpass.equals(Password)) {
            Confirmpassword.setError("Please Confirm Password");
            Password.clearComposingText();
            Confirmpassword.clearComposingText();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        else if (checkbox.equals(false)){
            checkbox.setError("Please Check Terms of service and Privacy and policy");
//            Toast.makeText(RegisterPage.this, "Please Check Terms of service and Privacy and policy", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
        else{
            //TODO if the User Is verified procced to the Register function

            progressBar.setVisibility(View.VISIBLE);

            System.out.println("UserInput  11111 "+inputemail+" " + inputusername+" "+inputpassword);

            RegisterUser(inputusername, inputemail , inputpassword);
        }


    }

    private void RegisterUser(String inputusername, String inputemail, String inputpassword) {

        System.out.println("UserInput 12121212 "+inputemail+" " + inputusername +" "+ inputpassword);

        auth.createUserWithEmailAndPassword(inputemail,inputpassword)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();

                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                            request.setDisplayName(inputusername);

                            user.updateProfile(request.build());

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                            UploadUser(user, inputusername, inputemail);


                        }
                        else {
                            String exception = task.getException().getMessage();
                            Toast.makeText(RegisterPage.this, "Error: " + exception, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                });


    }

    private void UploadUser(FirebaseUser user, String inputusername, String inputemail) {
        Map<String, Object> User_map = new HashMap<>();
        //TODO hash map documents Can Change depends of our project newsfeed
        User_map.put("UserName", inputusername);
        User_map.put("onlineStatus", "online");
        User_map.put("typingTo", "noOne");
        User_map.put("Email", inputemail);
        User_map.put("ProfileImage", " ");
        User_map.put("Desciption", " ");
        User_map.put("UID", user.getUid());
        User_map.put("search", inputusername.toLowerCase());
        //TODO Creating Collection in firestore name Trangko_Users

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(user.getUid()).setValue(User_map);

        FirebaseFirestore.getInstance().collection("Trangko_Users").document(user.getUid())
                .set(User_map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            login();
                        }
                        else {
                            Toast.makeText(RegisterPage.this, "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void login(){

        startActivity(new Intent(RegisterPage.this, MainPage.class));
        finish();

    }



}