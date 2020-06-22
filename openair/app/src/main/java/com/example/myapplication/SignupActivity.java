package com.example.myapplication;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignupActivity extends AppCompatActivity {
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private EditText mNicknameView;
    //----
    private  EditText mPhoneView;



    //----
    private final static int REQUEST_CODE = 999;
    Button btnPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //printKeyHash();

        mEmailView     = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView  = (EditText) findViewById(R.id.password);
        mPasswordView2 = (EditText) findViewById(R.id.password2);
        mNicknameView  = (EditText) findViewById(R.id.nickname);
        //----
        mPhoneView = (EditText) findViewById(R.id.phoneNumber);
        mPhoneView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        //----

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                Log.i("phoneNumberTest",mPhoneView.getText().toString());
            }
        });


        btnPhone = (Button)findViewById(R.id.phoneLogin);
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startLoginPage(LoginType.PHONE);

            }
        });
    }

    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.androidfbaccountkit",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void startLoginPage(LoginType loginType) {
        if (loginType == LoginType.PHONE)
        {
            Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                            AccountKitActivity.ResponseType.TOKEN); //클라이언트 액세스 토큰 플로 활성화
            intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() != null)
            {
                Toast.makeText(this,""+result.getError().getErrorType().getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            else if (result.wasCancelled())
            {
                Toast.makeText(this,"Cancel", Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                if (result.getAccessToken() != null)
                {
                    Toast.makeText(this,"Success ! %s"+result.getAccessToken().getAccountId(), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(this,"Success ! %s"+result.getAuthorizationCode().substring(0,10), Toast.LENGTH_LONG).show();
                }


                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {

                        String phone_num = account.getPhoneNumber().toString();
                        Log.i("phonetest", phone_num);
                        phone_num = phone_num.substring(3);
                        Log.i("phonetest", phone_num);
                        phone_num = "0" + phone_num;
                        Log.i("phonetest", phone_num);

                        String phone_num1 = phone_num.substring(0,3);
                        Log.i("phonetest", phone_num1);
                        String phone_num2 = phone_num.substring(3,7);
                        Log.i("phonetest", phone_num2);
                        String phone_num3 = phone_num.substring(7);
                        Log.i("phonetest", phone_num3);
                        String phone_numf = phone_num1 + "-" + phone_num2 + "-" + phone_num3;
                        Log.i("phonetest", phone_numf);
                        mPhoneView.setText(phone_numf);
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {

                    }
                });

            }
        }
    }

    //-----------------------------------------------------------------------------
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordView2.setError(null);
        mNicknameView.setError(null);

        //----
        mPhoneView.setError(null);
        //----

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPasswordView2.getText().toString();
        String nickname = mNicknameView.getText().toString();

        //----
        String phone = mPhoneView.getText().toString();
        //----

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
        //----
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        }
        //----


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // 회원 가입
                SessionManager.getInstance(getApplicationContext()).
                    Signup(email, password, password2, nickname, phone, this);
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
