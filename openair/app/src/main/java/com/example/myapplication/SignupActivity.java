package com.example.myapplication;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

public class SignupActivity extends AppCompatActivity {
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private EditText mNicknameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailView     = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView  = (EditText) findViewById(R.id.password);
        mPasswordView2 = (EditText) findViewById(R.id.password2);
        mNicknameView  = (EditText) findViewById(R.id.nickname);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    //-----------------------------------------------------------------------------
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordView2.setError(null);
        mNicknameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPasswordView2.getText().toString();
        String nickname = mNicknameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password2) && !isPasswordValid(password2)) {
            mPasswordView2.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView2;
            cancel = true;
        }

        if (password.equals(password2) == false) {
            mPasswordView.setError(getString(R.string.error_different_password));
            mPasswordView2.setError(getString(R.string.error_different_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(nickname) && !isNicknameValid(nickname)) {
            mNicknameView.setError(getString(R.string.error_invalid_nickname));
            focusView = mNicknameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // 회원 가입
            SessionManager.getInstance(getApplicationContext()).
                    Signup(email, password, password2, nickname, this);
//            setResult(RESULT_OK);
//            finish();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    private boolean isNicknameValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }
}
