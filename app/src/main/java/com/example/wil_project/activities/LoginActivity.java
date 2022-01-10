package com.example.wil_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.wil_project.R;
import com.example.wil_project.app_utilities.ApplicationClass;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    BackendlessUser user;
    LinearLayout lytLogin, lytProgress;
    TextInputEditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lytLogin = findViewById(R.id.lytLogin);
        lytProgress = findViewById(R.id.lytProgress);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        lytProgress.setVisibility(View.GONE);
        lytLogin.setVisibility(View.VISIBLE);
    }

    public void RegisterProfile(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void Login(View view) {
        if(edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
        }
        else {
            lytLogin.setVisibility(View.GONE);
            lytProgress.setVisibility(View.VISIBLE);

            Backendless.UserService.login(edtEmail.getText().toString() , edtPassword.getText().toString(), new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser user) {
                    ApplicationClass.HitchhikerId = Integer.parseInt(user.getProperty("UserId").toString());
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    lytProgress.setVisibility(View.GONE);
                    LoginActivity.this.finish();
                }
                @Override
                public void handleFault( BackendlessFault fault )
                {
                    Toast.makeText(LoginActivity.this, "" +fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}