package com.example.zachmarcelo.mypurse;

/**
 * Created by Zach Marcelo on 11/8/2018.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Zach Marcelo on 9/2/2018.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText reg_password,
            reg_username,
            reg_email,
            reg_passwordconfirm,
            reg_age;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    Spinner spinner;
    ArrayAdapter<String> myAdapter;

    String user_sex;
    int _pos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTheme(R.style.NoActionBar);
        progressBar = new ProgressBar(getApplicationContext());
        reg_password = findViewById(R.id.reg_password);
        reg_passwordconfirm = findViewById(R.id.reg_passwordconfirm);
        reg_email    = findViewById(R.id.reg_email);
        reg_username = findViewById(R.id.reg_username);
        reg_age  = findViewById(R.id.reg_age);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.signin).setOnClickListener(this);
        spinner = findViewById(R.id.reg_sex);

        // Initializing a String Array
        String[] sex = new String[]{
                "Select sex...",
                "Male",
                "Female"
        };

        final List<String> sexList = new ArrayList<>(Arrays.asList(sex));
        myAdapter = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_list_item_1, sexList){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    user_sex = selectedItemText;
                    _pos = position;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void registerUser() {
        final String username = reg_username.getText().toString().trim();
        final String email = reg_email.getText().toString().trim();
        String password = reg_password.getText().toString().trim();
        final String confirm = reg_passwordconfirm.getText().toString();
        String age = reg_age.getText().toString();
        String sex = user_sex;


        if (sex.equals("Select sex...") || _pos == 0) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            spinner.requestFocus();
            return;
        }

        if (username.equals("")) {
            reg_username.setError(getString(R.string.input_error_name));
            reg_username.requestFocus();
            return;
        }
        if (email.equals("")) {
            reg_email.setError(getString(R.string.input_error_email));
            reg_email.requestFocus();
            return;
        }else{
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                reg_email.setError(getString(R.string.input_error_email_invalid));
                reg_email.requestFocus();
                return;
            }
        }

        if (age.equals("")) {
            reg_age.setError(getString(R.string.input_error_name));
            reg_age.requestFocus();
            return;
        }

        if (password.equals("")) {
            reg_password.setError(getString(R.string.input_error_password));
            reg_password.requestFocus();
            return;
        }else{
            if (password.length() < 8) {
                reg_password.setError(getString(R.string.input_error_password_length));
                reg_password.requestFocus();
                return;
            }
        }

        if(confirm.isEmpty()){
            reg_passwordconfirm.setError(getString(R.string.input_error_confirm));
            reg_passwordconfirm.requestFocus();
            return;
        }
        if(!confirm.equals(password)){
            reg_passwordconfirm.setError(getString(R.string.input_error_password_confirm));
            reg_passwordconfirm.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            User user = new User(
                                    username,
                                    email,
                                    sex,
                                    age
                            );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, getString(R.string.registration_failed)  + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register:
                registerUser();
                break;
            case R.id.signin:
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }

    }
}
