package com.aadam.befit.uiModules.LoggedOut;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.aadam.befit.Constants;
import com.aadam.befit.R;
import com.aadam.befit.database.LoginDatabaseAdapter;
import com.aadam.befit.uiModules.LoggedIn.MainActivity;
import com.aadam.befit.util.PrefUtils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    //Properties
    private static final String TWITTER_KEY = "XsnRWYGpx9kjKkY8QiaZZUyLB";
    private static final String TWITTER_SECRET = "4DXoUNi9lSEnoK9JHw7BGtpzE1PbSHcWnyadbFOo7zlv8TNYeE";

    private TwitterAuthClient authClient;
    private LoginDatabaseAdapter loginDatabaseAdapter;
    private String userEmail;

    //region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setup twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        authClient = new TwitterAuthClient();

        //initialize logindatabseadapter
        loginDatabaseAdapter = new LoginDatabaseAdapter(this);
        loginDatabaseAdapter = loginDatabaseAdapter.open();

        if (PrefUtils.getCurrentUser(LoginActivity.this) != null) {
            Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        authClient.onActivityResult(requestCode, resultCode, data);
    }
    //endregion

    //region Button Action
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_twitter_login:
                twitterLogin();

                break;

            case R.id.signUpAction:

                callSignUpActivity();

                break;

            case R.id.logInAction:

                callLoginActivity();

                break;

            default:
                break;
        }
    }
    //endregion

    //region Private Methods
    private void twitterLogin(){

        authClient.authorize(LoginActivity.this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                final TwitterSession session = result.data;

                //get user email address
                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {

                        userEmail = result.data;
                        if (loginDatabaseAdapter.checkUserExists(userEmail)) {
                            PrefUtils.setCurrentUser(loginDatabaseAdapter.getUserByEmail(userEmail), getApplicationContext());
                            callMainActivity();
                        } else {
                            loginDatabaseAdapter.insertNewTwitterUser(userEmail, String.valueOf(session.getUserId()), session.getUserName());
                            PrefUtils.setCurrentUser(loginDatabaseAdapter.getUserByEmail(userEmail), getApplicationContext());
                            callDetailsActivity();
                        }

                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void callSignUpActivity(){

        Intent signUpInt = new Intent(this, SignUpActivity.class);
        signUpInt.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(signUpInt);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void callLoginActivity(){

        Intent signInInt = new Intent(this, SignInActivity.class);
        signInInt.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(signInInt);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void callDetailsActivity() {

        Intent detailInt = new Intent(this, DetailActivity.class);
        detailInt.putExtra(Constants.KEY_EMAIL, userEmail);
        detailInt.putExtra(Constants.KEY_USERID, loginDatabaseAdapter.getUserID(userEmail));
        startActivity(detailInt);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void callMainActivity() {

        Intent homeInt = new Intent(this, MainActivity.class);
        homeInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeInt);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
    //endregion




}
