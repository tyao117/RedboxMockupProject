package ics.uci.edu.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mPasswordView, mEmailView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.txtEmail);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void connectToTomcat(View view) {
        // Post request form data
        final Map<String, String> params = new HashMap<>();
        params.put("email", mEmailView.getText().toString());
        params.put("password", mPasswordView.getText().toString());


        // no user is logged in, so we must connect to the server

        // Use the same network queue across out application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        Log.d("myTag", "Button Pressed");

        // Using 10.0.2.2 when running android emulator
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://10.0.2.2:8443/project/api/android-login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);

                        Intent goToIntent = new Intent(LoginActivity.this, SearchActivity.class);
                        startActivity(goToIntent);

                        Toast.makeText(LoginActivity.this,"Login Successful", Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());

                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data
        };

        queue.add(loginRequest);
    }

}
