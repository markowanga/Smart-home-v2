package com.example.marcin.smarthomeandroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.marcin.smarthomeandroid.background.LoginCheckerAsyncTask;
import com.example.marcin.smarthomeandroid.data.MySharedPreferences;
import com.example.marcin.smarthomeandroid.R;

public class LoginActivity extends AppCompatActivity implements LoginCheckerAsyncTask.CheckLoginListener {
    private EditText editTextPassword, editTextEmail;
    private Button buttonLogin;
    private ProgressBar progressBarLogin;
    private View view;
    String email, password;
    LoginActivity l;
    boolean logged;
    boolean automaticlly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logged = false;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(getResources().getString(R.string.app_name) + " - logowanie");
        setSupportActionBar(myToolbar);

        l = this;
        view = findViewById(android.R.id.content);

        editTextEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        progressBarLogin = (ProgressBar) findViewById(R.id.progressBarLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                progressBarLogin.setVisibility(View.VISIBLE);
                buttonLogin.setEnabled(false);
                hideKeyboard(v);
                new LoginCheckerAsyncTask(getApplicationContext(), l, email, password).execute();
            }
        });

        if (MySharedPreferences.getLoginAutomatically(getApplicationContext()))
        {
            automaticlly = true;
            new LoginCheckerAsyncTask(getApplicationContext(), l,
                    MySharedPreferences.getLoginEmail(getApplicationContext()),
                    MySharedPreferences.getLoginPassword(getApplicationContext())).execute();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        if (logged && MySharedPreferences.getLoginAutomatically(getApplicationContext()))
            finish();
        super.onResume();
    }

    @Override
    public void loginDataChecked(int response) {
        progressBarLogin.setVisibility(View.INVISIBLE);
        buttonLogin.setEnabled(true);
        if (response == 0) {
            // Snackbar.make(view, "Dane poprawne :D", Snackbar.LENGTH_LONG).show();
            if (!automaticlly)
                MySharedPreferences.rememberCorrectData(getApplicationContext(), email, password);
            else automaticlly = false;
            logged = true;
            startActivity(new Intent(getApplicationContext(), NavActivity.class));
        } else if (response == -1) {
            Snackbar.make(view, "Dane niepoprawne :(", Snackbar.LENGTH_LONG).show();
            MySharedPreferences.rememberIncorrectData(getApplicationContext());
        } else if (response == -2) {
            Snackbar.make(view, "Błąd połączenia z serwerem :/", Snackbar.LENGTH_LONG).show();
        }
    }
}
