package com.tuim.components;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
        private static final String TAG = "LoginActivity";
        private static final int REQUEST_SIGNUP = 0;

        @Bind(R.id.input_username) EditText _usernameText;
        @Bind(R.id.input_servername) EditText _servernameText;
        @Bind(R.id.btn_login) Button _loginButton;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);

            _loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    login();
                }
            });

        }

        public void login() {
            Log.d(TAG, "Login");

            if (!validate()) {
                onLoginFailed();
                return;
            }

            _loginButton.setEnabled(false);

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Setting the data...");
            SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username",_usernameText.getText().toString());
            editor.putString("servername",_servernameText.getText().toString());
            editor.apply();
            progressDialog.show();


            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onLoginSuccess();
                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_SIGNUP) {
                if (resultCode == RESULT_OK) {

                    // TODO: Implement successful signup logic here
                    // By default we just finish the Activity and log them in automatically
                    this.finish();
                }
            }
        }

        @Override
        public void onBackPressed() {
            // Disable going back to the MainActivity
            moveTaskToBack(true);
        }

        public void onLoginSuccess() {
            _loginButton.setEnabled(true);
            finish();
        }

        public void onLoginFailed() {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_LONG).show();

            _loginButton.setEnabled(true);
        }

        public boolean validate() {
            boolean valid = true;

            String userName = _usernameText.getText().toString();
            String serverName = _servernameText.getText().toString();

            if ( userName.isEmpty() ) {
                _usernameText.setError("enter a username");
                valid = false;
            } else {
                _usernameText.setError(null);
            }

            if ( serverName.isEmpty() ) {
                _servernameText.setError("enter a server address");
                valid = false;
            } else {
                _servernameText.setError(null);
            }

            return valid;
        }
    }